//module com.example.pathfindingproject {
//    requires javafx.controls;
//    requires javafx.fxml;
//
//
//    opens com.example.pathfindingproject to javafx.fxml;
//    exports com.example.pathfindingproject;
//}

module application {
    requires javafx.controls;
    requires javafx.fxml;


    opens application to javafx.fxml;
    exports application;
}