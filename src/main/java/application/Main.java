package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Scene scene = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Error loading FXML file: " + e.getMessage());
        }

        String css = String.valueOf(Main.class.getResource("something.css"));

        scene.getStylesheets().add(css);

        // Adjust for Windows scale
        Screen screen = Screen.getPrimary();
        double dpi = screen.getDpi();
        double scaleX = screen.getOutputScaleX();
        double scaleY = screen.getOutputScaleY();


        Image icon = new Image(String.valueOf(Main.class.getResource("cg512x512.jpg")));

        try {
            stage.getIcons().add(icon);
        } catch (IllegalArgumentException e) {
            System.out.println("Error adding icon to stage: " + e.getMessage());
        }

        stage.setTitle("Pathfinding");


        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
