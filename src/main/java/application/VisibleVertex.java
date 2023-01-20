package application;

public class VisibleVertex {
    protected Point2DCustom vertex;
    protected double distance;
    public VisibleVertex(Point2DCustom vertex, double distance) {
        this.vertex = vertex;
        this.distance = distance;
    }
    @Override
    public String toString() {
        return "\nvertex=" + vertex +
                ", distance=" + distance;
    }
}
