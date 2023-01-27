package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.BackgroundImage;
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

        // Adjust for Windows scale
        Screen screen = Screen.getPrimary();
        double dpi = screen.getDpi();
        double scaleX = screen.getOutputScaleX();
        double scaleY = screen.getOutputScaleY();

        Image icon = new Image(String.valueOf(Main.class.getResource("/images/icon.jpg")));

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
        try {
            checkArgs(args);
        } catch(noArgs e) {
            System.out.println(e.getMessage());
            return;
        }
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

    static class noArgs extends Exception {
        public noArgs(String message) {
            super("\u001B[31m" + "ArgsException: " + message + "\u001B[0m");
        }
    }

    public static void checkArgs(String[] args) throws Main.noArgs {
        if (args.length==0) {
            throw new Main.noArgs("No arguments assigned");
        }
    }

}
