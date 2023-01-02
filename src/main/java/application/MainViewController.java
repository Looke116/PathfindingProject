package application;

import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainViewController {

    @FXML
    private AnchorPane canvas;
    @FXML
    private ToggleButton drawToggle;

    public final int MIN_POLYGON_NUMBER = 3;
    public final int MAX_POLYGON_NUMBER = 10;
    public final int MIN_VERTICES = 3;
    public final int MAX_VERTICES = 15;
    public final int SHAPE_SIZE = 100;
    private final Random random = new Random();
    private final List<Point2D> pointList = new ArrayList<>();
    List<PolygonCustom> polygonListGlobal = new ArrayList<>();
    PointsCustom pointGlobal = new PointsCustom();
    //Group g =new Group();

    private void draw(Node node) {
        canvas.getChildren().add(node);
    }

    private void setRandomColor(Shape shape) {
        shape.setFill(new Color(
                random.nextDouble(0, 1),
                random.nextDouble(0, 1),
                random.nextDouble(0, 1),
                1));
    }

    @FXML
    private void clearCanvas() {
        canvas.getChildren().clear();
    }

    protected void genRandomPolygons() {
        double maxX = canvas.getHeight();
        double maxY = canvas.getWidth();
        List<PolygonCustom> polygonList = new ArrayList<>();

        int PolygonTotal = random.nextInt(MIN_POLYGON_NUMBER, MAX_POLYGON_NUMBER);
        for (int i = 0; i < PolygonTotal; i++) {

            Point2D[] points;

            do {
                //polygon = new PolygonCustom();
                int verticesNumber = random.nextInt(MIN_VERTICES, MAX_VERTICES);
                points = pointList.toArray(new Point2D[verticesNumber]);
                Point2D centerPoint;

                do {
                    centerPoint = new Point2D(
                            random.nextDouble(SHAPE_SIZE, maxX - SHAPE_SIZE),
                            random.nextDouble(SHAPE_SIZE, maxY - SHAPE_SIZE)
                    );
                } while (inAnyPolygon(polygonList, centerPoint));

                for (int j = 0; j < verticesNumber; j++) {
                    points[j] = new Point2D(
                            random.nextDouble(centerPoint.getX() - SHAPE_SIZE, centerPoint.getX() + SHAPE_SIZE),
                            random.nextDouble(centerPoint.getY() - SHAPE_SIZE, centerPoint.getY() + SHAPE_SIZE));
                }
                sortPointsClockwise(points, centerPoint);

            } while (overAnyPolygon(polygonList, new PolygonCustom(points)));

            PolygonCustom polygon = new PolygonCustom(points);
            setRandomColor(polygon);
            polygonList.add(polygon);
        }

        polygonListGlobal = polygonList;
        //g.getChildren().addAll(polygonListGlobal);
    }

    protected void genRandomPoints(){
        double maxX = canvas.getHeight();
        double maxY = canvas.getWidth();
        PointsCustom point = new PointsCustom();

        do{
            point.startPoint = new Point2D(random.nextDouble(10, maxX-10), random.nextDouble(10, maxY-10));
        } while (inAnyPolygon(polygonListGlobal,point.startPoint));
        pointGlobal.startPoint = point.startPoint;

        do{
            point.finishPoint = new Point2D(random.nextDouble(10, maxX-10), random.nextDouble(10, maxY-10));
        } while (inAnyPolygon(polygonListGlobal,point.finishPoint));
        pointGlobal.finishPoint = point.finishPoint;
    }

    @FXML
    protected void createRandomExample(){
        clearCanvas();
        genRandomPolygons();
        for (Polygon polygon : polygonListGlobal) {
            draw(polygon);
        }
        //draw(g);
        genRandomPoints();
        draw(new Circle(pointGlobal.startPoint.getX(),pointGlobal.startPoint.getY(),2));
        draw(new Circle(pointGlobal.finishPoint.getX(),pointGlobal.finishPoint.getY(),2));
        //draw(new Line(pointGlobal.startPoint.getX(),pointGlobal.startPoint.getY(),pointGlobal.finishPoint.getX(),pointGlobal.finishPoint.getY()));
        for(Point2D p : VisibleVertices(pointGlobal.startPoint)){
            draw(new Circle(p.getX(),p.getY(),2));
            draw(new LineCustom(pointGlobal.startPoint,p));
        }
//        System.out.println(VisibleVertices(pointGlobal.startPoint));
    }
    private static void sortPointsClockwise(Point2D[] points, Point2D center) {
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < points.length - 1; i++) {
                if (comparePoint(points[i + 1], points[i], center)) {
                    Point2D temp = points[i];
                    points[i] = points[i + 1];
                    points[i + 1] = temp;
                    changed = true;
                }
            }
        } while (changed);
    }

    private static boolean comparePoint(Point2D a, Point2D b, Point2D center) {

        if (a.getX() - center.getX() >= 0 && b.getX() - center.getX() < 0) {
            return true;
        }
        if (a.getX() - center.getX() < 0 && b.getX() - center.getX() >= 0) {
            return false;
        }
        if (a.getX() - center.getX() == 0 && b.getX() - center.getX() == 0) {
            if (a.getY() - center.getY() >= 0 || b.getY() - center.getY() >= 0) {
                return a.getY() > b.getY();
            }
            return b.getY() > a.getY();
        }

        // compute the cross product of vectors (center -> a) x (center -> b)
        double det = (a.getX() - center.getX()) * (b.getY() - center.getY()) -
                (b.getX() - center.getX()) * (a.getY() - center.getY());
        if (det < 0) {
            return true;
        }
        if (det > 0) {
            return false;
        }

        // points a and b are on the same line from the center
        // check which point is closer to the center
        double d1 = (a.getX() - center.getX()) * (a.getX() - center.getX()) +
                (a.getY() - center.getY()) * (a.getY() - center.getY());
        double d2 = (b.getX() - center.getX()) * (b.getX() - center.getX()) +
                (b.getY() - center.getY()) * (b.getY() - center.getY());
        return d1 > d2;
    }

    @FXML
    protected void mouseClicked(MouseEvent event) {
        if (!drawToggle.isSelected()) {
            return;
        }

        if (event.getButton() == MouseButton.SECONDARY) {
            removeLastPoint();
            return;
        }

        Point2D currentPoint = new Point2D(event.getX(), event.getY());
        pointList.add(currentPoint);

        if (pointList.size() > 3) {
            Point2D firstPoint = pointList.get(0);
            Point2D lastPoint = pointList.get(pointList.size() - 1);

            // Check if the last point is within 10 pixels from the first point to finish the polygon
            if (lastPoint.getX() > firstPoint.getX() - 10 && lastPoint.getX() < firstPoint.getX() + 10) {
                if (lastPoint.getY() > firstPoint.getY() - 10 && lastPoint.getY() < firstPoint.getY() + 10) {
                    clearOutline();
                    finishPolygon();
                    return;
                }
            }
        }

        Circle circle = new Circle(currentPoint.getX(), currentPoint.getY(), 5);
        circle.setFill(Color.BLUE);
        draw(circle);

        if (pointList.size() <= 1) return;
        Point2D previousPoint = pointList.get(pointList.size() - 2);
        Line line = new Line(
                currentPoint.getX(),
                currentPoint.getY(),
                previousPoint.getX(),
                previousPoint.getY()
        );
        line.setFill(Color.LIGHTBLUE);
        draw(line);
    }

    private void removeLastPoint() {
        List<Node> elements = canvas.getChildren();

        for (int i = elements.size() - 1; i >= 0; i--) {
            Node element = elements.get(i);
            if (element.getClass() == Line.class) {
                elements.remove(i);
            } else if (element.getClass() == Circle.class) {
                if (((Circle) element).getFill() == Color.BLUE) {
                    elements.remove(i);
                    break;
                }
            }
        }

        if (pointList.size() > 1) {
            pointList.remove(pointList.size() - 1);
        }
    }

    private void clearOutline() {
        List<Node> elements = canvas.getChildren();

        for (int i = 0; i < elements.size(); i++) {
            Node element = elements.get(i);
            if (element.getClass() == Line.class) {
                elements.remove(i--);
            } else if (element.getClass() == Circle.class) {
                elements.remove(i--);
            }
        }
    }

    private void finishPolygon() {
        pointList.remove(pointList.size() - 1);

        double[] points = new double[pointList.size() * 2];
        for (int i = 0; i < pointList.size(); i++) {
            points[i * 2] = pointList.get(i).getX();
            points[i * 2 + 1] = pointList.get(i).getY();
        }

        Polygon polygon = new Polygon(points);
        setRandomColor(polygon);
        draw(polygon);
        pointList.clear();
    }

    @FXML
    protected void mouseMoved(MouseEvent event) {
        Circle circle = new Circle(event.getX(), event.getY(), 5);
        circle.setFill(Color.RED);

        List<Node> elements = canvas.getChildren();
        for (int i = 0; i < elements.size(); i++) {
            Node element = elements.get(i);

            if (element.getClass() != Circle.class) continue;
            if (((Circle) element).getFill() != Color.RED) continue;
            if (((Circle) element).getRadius() != 5) continue;

            //noinspection SuspiciousListRemoveInLoop
            elements.remove(i);
        }
        canvas.getChildren().add(circle);
    }

    @FXML
    protected void showHoverText() {
    }

    @FXML
    protected void hideHoverText() {
    }

    @FXML
    protected void moveHoverText() {
    }

    @FXML
    protected void importFromFile() {
        Stage filePickerWindow = new Stage();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import From File");
        File file = fileChooser.showOpenDialog(filePickerWindow);

        if (file == null) return;

        clearCanvas();
        String content = readAllLines(file);
        content.trim();
        JSONObject object = new JSONObject(content);

        JSONArray polygonArray = object.getJSONArray("Polygons");
        JSONArray circleArray = object.getJSONArray("Circles");
        for (int i = 0; i < polygonArray.length(); i++) {
            JSONArray pointArray = polygonArray.getJSONObject(i).getJSONArray("points");
            double[] points = new double[pointArray.length()];

            for (int j = 0; j < pointArray.length(); j++) {
                points[j] = pointArray.getDouble(j);
            }

            Polygon polygon = new Polygon(points);
            polygon.setFill(Paint.valueOf(polygonArray.getJSONObject(i).getString("color")));
            draw(polygon);
        }

        for (int i = 0; i < circleArray.length(); i++) {
            double centerX = circleArray.getJSONObject(i).getDouble("centerX");
            double centerY = circleArray.getJSONObject(i).getDouble("centerY");
            double radius = circleArray.getJSONObject(i).getDouble("radius");
            Circle circle =new Circle(centerX,centerY,radius);
            circle.setFill(Paint.valueOf(polygonArray.getJSONObject(i).getString("color")));
        }
    }

    @FXML
    protected void exportToFile() {
        Stage filePickerWindow = new Stage();
        List<Node> elements = canvas.getChildren();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to File");
        File file = fileChooser.showSaveDialog(filePickerWindow);

        if (file == null) return;

        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileWriter fileWriter = new FileWriter(file)) {
            JSONArray polygonArray = new JSONArray();
            JSONArray circleArray = new JSONArray();

            for (Node element : elements) {
                if (element.getClass() == Polygon.class) {
                    JSONArray points = new JSONArray();
                    points.putAll(((Polygon) element).getPoints());

                    JSONObject object = new JSONObject();
                    object.put("points", points);
                    object.put("color", ((Polygon) element).getFill());

                    polygonArray.put(object);
                } else if (element.getClass() == Circle.class) {
                    if (((Circle) element).getFill() == Color.RED) continue;

                    JSONObject object = new JSONObject();
                    object.put("centerX", ((Circle) element).getCenterX());
                    object.put("centerY", ((Circle) element).getCenterY());
                    object.put("radius", ((Circle) element).getRadius());
                    object.put("color", ((Polygon) element).getFill());

                    polygonArray.put(object);
                }
            }

            JSONObject object = new JSONObject();
            object.put("Polygons", polygonArray);
            object.put("Circles", circleArray);

            fileWriter.write(object.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String readAllLines(File file) {
        List<String> strings = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                strings.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        String output = new String();
        for (String string : strings) {
            output += string;
        }
        return output;
    }

    public boolean inAnyPolygon(List<PolygonCustom> plgList, Point2D pt){
        for (PolygonCustom p : plgList) {
            if (p.contains(pt)){
                return true;
            }
        }
        return false;
    }

    public boolean overAnyPolygon(List<PolygonCustom> plgList, Node shape){
        for (PolygonCustom p : plgList) {
            if (p.intersects(shape.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    public boolean Visible(Point2D p1,Point2D p2, List<PolygonCustom> plgList){

        LineCustom line = new LineCustom(p1,p2);
        int count = 0;

        for (Polygon polygon : plgList) {
            if (polygon.getBoundsInParent().intersects(line.getBoundsInParent())) {

                count++;
            }
        }

        return count <= 1;
    }

    public ArrayList<Point2D> VisibleVertices(Point2D p){
        ArrayList<Point2D> result = new ArrayList<Point2D>();
        for(PolygonCustom pol : polygonListGlobal){
            for(Point2D vert : pol.PointsList){
                //System.out.println(Visible(p,vert,polygonListGlobal));
                if( (Visible(p,vert,polygonListGlobal)) && (p!=vert) ){
                    result.add(vert);
                }
            }
        }
        return result;
    }

}