package application;
public class Point2DCustom extends javafx.geometry.Point2D {
    //Helper class for creating points
    public double pointB;
    public double pointA;
    //Constructor
    public Point2DCustom(double v, double v1) {
        super(v, v1);
        this.pointA=v;
        this.pointB=v1;
    }

}