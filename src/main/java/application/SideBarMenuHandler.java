package application;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SideBarMenuHandler {

    @FXML
    private SplitPane splitPane;
    @FXML
    private AnchorPane canvas;

    private final Random random = new Random();

    @FXML
    private void clearCanvas() {
        canvas.getChildren().clear();
    }

    @FXML
    protected void genRandomPolygons() {
        clearCanvas();
        double maxX = canvas.getHeight();
        double maxY = canvas.getWidth();
        List<Polygon> polygonList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            int verticesNumber = random.nextInt(3, 7);
            double[] points = new double[verticesNumber * 2];
            double[] centerPoint = {random.nextDouble(0, maxX), random.nextDouble(0, maxY)};

            for (int j = 0; j < verticesNumber; j++) {
                points[j * 2] = random.nextDouble(centerPoint[0] - 100, centerPoint[0] + 100);
                points[j * 2 + 1] = random.nextDouble(centerPoint[1] - 100, centerPoint[1] + 100);
            }

            Polygon polygon = new Polygon(points);
//            Polygon polygon = new Polygon(sortPointsClockwise(points));
            polygon.setFill(new Color(
                    random.nextDouble(0, 1),
                    random.nextDouble(0, 1),
                    random.nextDouble(0, 1),
                    1));
            polygonList.add(polygon);
        }

        for (Polygon polygon : polygonList) {
            canvas.getChildren().add(polygon);
        }
    }

//    private double[] sortPointsClockwise(double[] points, double[] centerPoint) {
//        List<Point2D> points
//    }

    @FXML
    protected void showHoverText() {
    }

    @FXML
    protected void hideHoverText() {
    }

    @FXML
    protected void moveHoverText() {
    }
}
