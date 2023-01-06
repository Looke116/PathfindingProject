package application;
public class PolygonCustom extends javafx.scene.shape.Polygon {
    protected Point2DCustom[] PointsList;
    public PolygonCustom(Point2DCustom[] points) {
        for (Point2DCustom point : points) {
            this.getPoints().add(point.getX());
            this.getPoints().add(point.getY());
        }
        PointsList = points;
    }
    public Point2DCustom[] getPointsList() {
        return this.PointsList;
    }

    public Point2DCustom leftNeighbour (Point2DCustom p){
        Point2DCustom result = null;
        for(int i = 0; i<this.PointsList.length; i++) {
            if (this.PointsList[i]==p && i==0){
                result = this.PointsList[this.PointsList.length-1];
            } else if (this.PointsList[i]==p){
                result = this.PointsList[i-1];
            }
        }
        return result;
    }
    public Point2DCustom rightNeighbour (Point2DCustom p){
        Point2DCustom result = null;
        for(int i=0; i<this.PointsList.length; i++) {
            if (this.PointsList[i]==p && i==this.PointsList.length-1){
                result = this.PointsList[0];
            } else if (this.PointsList[i]==p){
                result = this.PointsList[i+1];
            }
        }
        return result;
    }
}
