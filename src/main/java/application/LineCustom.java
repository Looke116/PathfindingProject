package application;

import javafx.geometry.Point2D;

import java.util.List;

public class LineCustom extends javafx.scene.shape.Line {
    public LineCustom(Point2D startPoint,Point2D finishPoint) {
        this.setStartX(startPoint.getX());
        this.setStartY(startPoint.getY());
        this.setEndX(finishPoint.getX());
        this.setEndY(finishPoint.getY());
    }
}
