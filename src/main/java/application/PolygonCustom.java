package application;

import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.List;

public class PolygonCustom extends javafx.scene.shape.Polygon {
    protected Point2D[] PointsList;
    public PolygonCustom(Point2D[] points) {
        for (Point2D point : points) {
            this.getPoints().add(point.getX());
            this.getPoints().add(point.getY());
        }
        PointsList = points;
    }
}
