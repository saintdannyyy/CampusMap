import java.util.*;

public class CampusMap {
    private Map<String, Location> locations;

    public CampusMap() {
        locations = new HashMap<>();
    }

    public void addLocation(String name, double latitude, double longitude) {
        Location location = new Location(name, latitude, longitude);
        locations.put(name, location);
    }

    public List<String> getLocationNames() {
        List<String> locationNames = new ArrayList<>();
        for (Location location : locations.values()) {
            locationNames.add(location.getName());
        }
        return locationNames;
    }

    public void addDistance(String source, String destination, double distance) {
        Location sourceLocation = locations.get(source);
        Location destinationLocation = locations.get(destination);

        if (sourceLocation != null && destinationLocation != null) {
            sourceLocation.addNeighbor(destinationLocation, distance);
            destinationLocation.addNeighbor(sourceLocation, distance);
        }
    }

    public Location getLocation(String name) {
        return locations.get(name);
    }

    public List<Location> findShortestPath(String source, String destination) {
        Location sourceLocation = locations.get(source);
        Location destinationLocation = locations.get(destination);

        if (sourceLocation == null || destinationLocation == null) {
            return null; // One or both locations do not exist
        }

        Map<Location, Double> distances = new HashMap<>();
        Map<Location, Location> previousLocations = new HashMap<>();
        Set<Location> visited = new HashSet<>();

        // Initialize distances with infinity and previousLocations with null
        for (Location location : locations.values()) {
            distances.put(location, Double.POSITIVE_INFINITY);
            previousLocations.put(location, null);
        }

        distances.put(sourceLocation, 0.0); // Distance from source to itself is 0

        PriorityQueue<Location> queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        queue.offer(sourceLocation);

        while (!queue.isEmpty()) {
            Location currentLocation = queue.poll();

            if (currentLocation == destinationLocation) {
                break; // Reached the destination
            }

            if (visited.contains(currentLocation)) {
                continue; // Already visited this location
            }

            visited.add(currentLocation);

            for (Map.Entry<Location, Double> entry : currentLocation.getNeighbors().entrySet()) {
                Location neighbor = entry.getKey();
                double distance = entry.getValue();
                double newDistance = distances.get(currentLocation) + distance;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previousLocations.put(neighbor, currentLocation);
                    queue.offer(neighbor);
                }
            }
        }

        // Reconstruct the shortest path
        List<Location> shortestPath = new ArrayList<>();
        Location current = destinationLocation;

        while (current != null) {
            shortestPath.add(0, current);
            current = previousLocations.get(current);
        }

        return shortestPath.isEmpty() ? null : shortestPath;
    }


    public List<List<Location>> filterRoutesByDistance(List<List<Location>> routes) {
        routes.sort(Comparator.comparingDouble(this::calculateTotalDistance));
        return routes;
    }

    public List<List<Location>> filterRoutesByArrivalTime(List<List<Location>> routes) {
        routes.sort(Comparator.comparingDouble(this::calculateEstimatedArrivalTime));
        return routes;
    }

    public List<List<Location>> searchRoutesByLandmark(List<List<Location>> routes, String landmark) {
        List<List<Location>> filteredRoutes = new ArrayList<>();

        if (routes != null) {
            for (List<Location> route : routes) {
                if (route != null && !route.isEmpty()) {
                    for (Location location : route) {
                        if (location != null && location.getName() != null && location.getName().equals(landmark)) {
                            filteredRoutes.add(route);
                            break;
                        }
                    }
                }
            }
        }

        return filteredRoutes;
    }

    double  calculateTotalDistance(List<Location> route) {
        double totalDistance = 0.0;

        for (int i = 0; i < route.size() - 1; i++) {
            Location sourceLocation = route.get(i);
            Location destinationLocation = route.get(i + 1);

            if (sourceLocation != null && destinationLocation != null) {
                double distance = sourceLocation.getDistanceTo(destinationLocation);
                totalDistance += distance;
            }
        }

        return totalDistance;
    }

    double calculateEstimatedArrivalTime(List<Location> route) {
        double totalTravelTime = 0.0;

        for (int i = 0; i < route.size() - 1; i++) {
            Location sourceLocation = route.get(i);
            Location destinationLocation = route.get(i + 1);

            if (sourceLocation != null && destinationLocation != null) {
                double distance = sourceLocation.getDistanceTo(destinationLocation);
                double averageTravelTime = calculateAverageTravelTime(distance);
                double adjustedTravelTime = applyTrafficPatternAdjustments(averageTravelTime);
                totalTravelTime += adjustedTravelTime;
            }
        }

        return totalTravelTime;
    }

    private double calculateAverageTravelTime(double distance) {
        // Assuming an average speed of 50 meters per minute
        double averageSpeed = 500.0;
        return distance / averageSpeed;
    }


    private double applyTrafficPatternAdjustments(double travelTime) {
        // Apply traffic pattern adjustments based on specific conditions
        if (travelTime >= 5 && travelTime < 10) {
            // Moderate traffic, increase travel time by 10%
            return travelTime * 1.1;
        } else if (travelTime >= 10) {
            // Heavy traffic, increase travel time by 20%
            return travelTime * 1.2;
        } else {
            // No traffic adjustments needed
            return travelTime;
        }
    }


    public static class Location {
        private final String name;
        private final double latitude;
        private final double longitude;
        private final Map<Location, Double> neighbors;

        public Location(String name, double latitude, double longitude) {
            this.name = name;
            this.latitude = latitude;
            this.longitude = longitude;
            this.neighbors = new HashMap<>();
        }

        public void addNeighbor(Location neighbor, double distance) {
            neighbors.put(neighbor, distance);
        }

        public double getDistanceTo(Location neighbor) {
            return neighbors.getOrDefault(neighbor, Double.POSITIVE_INFINITY);
        }

        public String getName() {
            return name;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public Map<Location, Double> getNeighbors() {
            return neighbors;
        }
    }

    public static void main(String[] args) {
        CampusMap campusMap = new CampusMap();

        // Add locations and distances
        campusMap.addLocation("Great Hall", 5.650593742091174, -0.19616334087129655);
        campusMap.addLocation("Public Affairs Directorate", 5.650717164959013, -0.19814852617387896);
        campusMap.addLocation("Viannis Bistro", 5.650957390663127, -0.19026283176032083);
        campusMap.addLocation("Sociology Department", 5.650455585725553, -0.18976394085109477);
        campusMap.addLocation("School of Languages", 5.6491637029058195, -0.1895547285513495);
        campusMap.addLocation("Cash Office", 5.650422210867779, -0.19429252267837213);
        campusMap.addLocation("Career and Counseling Centre", 5.649498852019727, -0.19030038266889965);
        campusMap.addLocation("Legon Hall Annex A", 5.647985190833957, -0.18841787439056107);
        campusMap.addLocation("Legon Hall Annex B", 5.647077666301523, -0.18842860322605126);
        campusMap.addLocation("Legon Hall Annex C", 5.647534, -0.189123);
        campusMap.addLocation("BrightHills Research", 5.644746122440687, -0.18778084978432888);
        campusMap.addLocation("WELLDONE RESEARCH CONSULT", 5.645577578484664, -0.18912061314990058);
        campusMap.addLocation("University of Ghana Guest Centre", 5.644822, -0.189128);
        campusMap.addLocation("University of Ghana Basic School", 5.643079203024593, -0.1879940854128992);
        campusMap.addLocation("Centre for Aging Studies", 5.64341041867396, -0.18622878936265513);
        campusMap.addLocation("Night Market", 5.6418962813844455, -0.189128);

        campusMap.addDistance("Great Hall", "Night Market Via University of Ghana Guest Centre", 2200); // Example distance in meters
        campusMap.addDistance("Great Hall", "Cash Office", 600); // Example distance in meters
        campusMap.addDistance("Cash Office", "Career and Counseling centre", 500); // Example distance in meters
        campusMap.addDistance("Great Hall", "Public Affairs Directorate", 350); // Example distance in meters
        campusMap.addDistance("Public Affairs Directorate", "Viannis Bistro", 650); // Example distance in meters
        campusMap.addDistance("Viannis Bistro", "Sociology Department", 90); // Example distance in meters
        campusMap.addDistance("Sociology Department", "School of Languages", 230); // Example distance in meters        campusMap.addDistance("Career and Counseling centre", "Legon Hall Annex A", 500); // Example distance in meters
        campusMap.addDistance("Legon Hall Annex A", "Legon Hall Annex C", 300); // Example distance in meters
        campusMap.addDistance("Legon Hall Annex C", "Legon Hall Annex B", 350); // Example distance in meters
        campusMap.addDistance("Legon Hall Annex C", "WELLDONE RESEARCH CONSULT", 210); // Example distance in meters
        campusMap.addDistance("Legon Hall Annex B", "BrightHills Research", 260); // Example distance in meters
        campusMap.addDistance("School of Languages", "University of Ghana Guest Centre", 500); // Example distance in meters
        campusMap.addDistance("WELLDONE RESEARCH CONSULT", "University of Ghana Guest Centre", 90); // Example distance in meters
        campusMap.addDistance("University of Ghana Guest Centre", "University of Ghana Basic School", 210); // Example distance in meters
        campusMap.addDistance("BrightHills Research", "Centre for Aging Studies", 350); // Example distance in meters
        campusMap.addDistance("University of Ghana Basic School", "Centre for Aging Studies", 220); // Example distance in meters
        campusMap.addDistance("Centre for Aging Studies", "Night Market", 170); // Example distance in meters
        campusMap.addDistance("Great Hall", "Public Affairs Directorate", 350); // Example distance in meters


        // Find shortest path
        String source = "Great Hall";
        String destination = "Night Market";
        List<Location> shortestPath = campusMap.findShortestPath(source, destination);

        if (shortestPath != null) {
            System.out.println("Shortest Path from " + source + " to " + destination + ". Starting from:");
            for (Location location : shortestPath) {
                System.out.println(location.getName());
            }

            double totalDistance = campusMap.calculateTotalDistance(shortestPath);
            double estimatedArrivalTime = campusMap.calculateEstimatedArrivalTime(shortestPath);

            System.out.println("Total Distance: " + totalDistance + " meters");
            System.out.println("Estimated Arrival Time: " + estimatedArrivalTime + " minutes");
        } else {
            System.out.println("No path found between " + source + " and " + destination);
        }

        //Sorting and filtering
        List<List<Location>> routes = new ArrayList<>();
        // Route 1: Shortest path from Great Hall to Night Market via UGGC
        List<Location> route1 = new ArrayList<>();
        route1.add(campusMap.getLocation("Great Hall"));
        route1.add(campusMap.getLocation("Cash Office"));
        route1.add(campusMap.getLocation("Career and Counseling Centre"));
        route1.add(campusMap.getLocation("Legon Hall Annex C"));
        route1.add(campusMap.getLocation("WELLDONE RESEARCH CONSULT"));
        route1.add(campusMap.getLocation("University of Ghana Guest Centre"));
        route1.add(campusMap.getLocation("University of Ghana Basic School"));
        route1.add(campusMap.getLocation("Centre for Aging Studies"));
        route1.add(campusMap.getLocation("Night Market"));
        routes.add(route1);

        // Route 2: Alternative path from Great Hall to Night Market via Legon Hall Annexes
        List<Location> route2 = new ArrayList<>();
        route2.add(campusMap.getLocation("Great Hall"));
        route2.add(campusMap.getLocation("Cash Office"));
        route2.add(campusMap.getLocation("Career and Counseling Centre"));
        route2.add(campusMap.getLocation("Legon Hall Annex A"));
        route2.add(campusMap.getLocation("Legon Hall Annex B"));
        route2.add(campusMap.getLocation("BrightHills Research"));
        route2.add(campusMap.getLocation("Centre for Aging Studies"));
        route2.add(campusMap.getLocation("Night Market"));
        routes.add(route2);

        // Route 3: Another alternative path from Great Hall to Night Market
        List<Location> route3 = new ArrayList<>();
        route3.add(campusMap.getLocation("Great Hall"));
        route3.add(campusMap.getLocation("Public Affairs Directorate"));
        route3.add(campusMap.getLocation("Viannis Bistro"));
        route3.add(campusMap.getLocation("Sociology Department"));
        route3.add(campusMap.getLocation("School of Languages"));
        route3.add(campusMap.getLocation("University of Ghana Gust Centre"));
        route3.add(campusMap.getLocation("University of Ghana Basic School"));
        route3.add(campusMap.getLocation("Centre for Aging Studies"));
        route3.add(campusMap.getLocation("Night Market"));
        routes.add(route3);

        // Filter routes by distance
        List<List<Location>> routesSortedByDistance = campusMap.filterRoutesByDistance(routes);


        // Filter routes by arrival time
        List<List<Location>> routesSortedByArrivalTime = campusMap.filterRoutesByArrivalTime(routes);

        // Search routes by landmark
        String landmark = "Cash Office";
        List<List<Location>> routesWithLandmark = campusMap.searchRoutesByLandmark(routes, landmark);
    }
}