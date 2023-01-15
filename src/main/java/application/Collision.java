package application;

import javafx.scene.Node;

import java.util.ArrayList;
import java.util.List;

public interface Collision {
    boolean inAnyPolygon(List<PolygonCustom> plgList, Point2DCustom pt);
    boolean intersectsAnyPolygon(List<PolygonCustom> plgList, Node shape);
    boolean Visible(List<PolygonCustom> polygons, LineCustom line);
    ArrayList<Point2DCustom> VisibleVertices(Point2DCustom p);
    void calculateVisibilityGraph();
}
