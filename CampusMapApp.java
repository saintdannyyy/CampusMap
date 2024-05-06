import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class CampusMapApp extends Application {

    private CampusMap campusMap;

    private ComboBox<String> sourceComboBox;
    private ComboBox<String> destinationComboBox;
    private Button calculateButton;
    private Label resultLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        // Initialize CampusMap and add locations and distances
        initializeCampusMap();

        // Create UI controls
        sourceComboBox = new ComboBox<>();
        destinationComboBox = new ComboBox<>();
        calculateButton = new Button("Calculate");
        resultLabel = new Label();

        // Set event handler for the Calculate button
        calculateButton.setOnAction(event -> calculateButtonClicked());

        // Set up the layout
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));

        gridPane.add(new Label("Source:"), 0, 0);
        gridPane.add(sourceComboBox, 1, 0);
        gridPane.add(new Label("Destination:"), 0, 1);
        gridPane.add(destinationComboBox, 1, 1);
        gridPane.add(calculateButton, 0, 2, 2, 1);
        gridPane.add(resultLabel, 0, 3, 2, 1);

        // Set up the main layout
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().add(gridPane);

        // Set up the scene
        Scene scene = new Scene(mainLayout, 400, 200);

        // Set up the stage
        primaryStage.setTitle("Campus Map App");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void initializeCampusMap() {
        // Create CampusMap instance
        campusMap = new CampusMap();

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


        // Populate source and destination ComboBoxes with location names
        List<String> locationNames = campusMap.getLocationNames();
        ObservableList<String> observableLocationNames = FXCollections.observableArrayList(locationNames);
        sourceComboBox.setItems(observableLocationNames);
        destinationComboBox.setItems(observableLocationNames);
    }
    

    private void calculateButtonClicked() {
        String source = sourceComboBox.getValue();
        String destination = destinationComboBox.getValue();

        if (source != null && destination != null) {
            // Find shortest path
            List<CampusMap.Location> shortestPath = campusMap.findShortestPath(source, destination);

            if (shortestPath != null) {
                StringBuilder sb = new StringBuilder();
                sb.append("Shortest Path from ").append(source).append(" to ").append(destination).append(". Starting from:\n");

                for (CampusMap.Location location : shortestPath) {
                    sb.append(location.getName()).append("\n");
                }

                double totalDistance = campusMap.calculateTotalDistance(shortestPath);
                double estimatedArrivalTime = campusMap.calculateEstimatedArrivalTime(shortestPath);

                sb.append("Total Distance: ").append(totalDistance).append(" meters\n");
                sb.append("Estimated Arrival Time: ").append(estimatedArrivalTime).append(" minutes");

                resultLabel.setText(sb.toString());
            } else {
                resultLabel.setText("No path found between " + source + " and " + destination);
            }
        } else {
            resultLabel.setText("Please select source and destination");
        }
    }

}
