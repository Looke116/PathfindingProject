package application;

public class LineCustom extends javafx.scene.shape.Line {
    //Helper class for creating lines
    //Constructor
    public LineCustom(Point2DCustom vertexA, Point2DCustom vertexB) {
        this.setStartX(vertexA.getX());
        this.setStartY(vertexA.getY());
        this.setEndX(vertexB.getX());
        this.setEndY(vertexB.getY());
    }

    public Point2DCustom getStartPoint(){
        return new Point2DCustom(this.getStartX(),this.getStartY());
    }
    public Point2DCustom getEndPoint(){
        return new Point2DCustom(this.getEndX(),this.getEndY());
    }
}