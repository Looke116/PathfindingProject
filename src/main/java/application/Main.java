package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"));
        String css = String.valueOf(Main.class.getResource("something.css"));
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(css);

        // Adjust for Windows scale
        Screen screen = Screen.getPrimary();
        double dpi = screen.getDpi();
        double scaleX = screen.getOutputScaleX();
        double scaleY = screen.getOutputScaleY();


        Image icon = new Image(String.valueOf(Main.class.getResource("icon.png")));
        stage.getIcons().add(icon);

        stage.setTitle("Pathfinding");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
