package application;
public class Edge extends LineCustom{

    public Point2DCustom vertexB;
    public Point2DCustom vertexA;

    public Edge(Point2DCustom vertexA, Point2DCustom vertexB) {
        super(vertexA, vertexB);
        this.vertexA=vertexA;
        this.vertexB=vertexB;
    }

}