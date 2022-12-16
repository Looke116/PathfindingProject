package application;

import javafx.geometry.Point2D;

import java.util.List;

public class Polygon extends javafx.scene.shape.Polygon {
    public Polygon(List<Point2D> points) {
        for (Point2D point : points) {
            this.getPoints().add(point.getX());
            this.getPoints().add(point.getY());
        }
    }
}
