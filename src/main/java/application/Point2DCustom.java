package application;
public class Point2DCustom extends javafx.geometry.Point2D {
public boolean visited = false;
    public double pointB;
    public double pointA;
    public Point2DCustom(double v, double v1) {
        super(v, v1);
        this.pointA=v;
        this.pointB=v1;
    }
}