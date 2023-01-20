package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Arrays;

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

        stage.setMinHeight(250);
        stage.setMinWidth(570);
        stage.setTitle("Pathfinding");
        stage.setScene(scene);
        stage.show();
    }



    public static void main(String[] args) {
        launch(args);
        if (Arrays.asList(args).contains("printGraph")/*Objects.equals(args[0], "printGraph")*/){
            System.out.println("Graph:\n");
            System.out.println(MainViewController.graph.toString());
            System.out.println();
        }

        if (Arrays.asList(args).contains("printPoints")/*Objects.equals(args[0], "printGraph")*/){
            System.out.println("Points:\n");
            for (Point2DCustom p : MainViewController.allPoints){
                System.out.println(p);
            }
            System.out.println();
        }

    }
}
