package application;

import javafx.fxml.FXML;
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
import org.jgrapht.util.AVLTree;
import java.io.*;
import java.util.*;

public class MainViewController {

    @FXML
    private AnchorPane canvas;
    @FXML
    private ToggleButton drawToggle;

    public final int MIN_POLYGON_NUMBER = 2;
    public final int MAX_POLYGON_NUMBER = 4;
    public final int MIN_VERTICES = 3;
    public final int MAX_VERTICES = 6;
    public final int SHAPE_SIZE = 100;
    private final Random random = new Random();
    private final List<Point2DCustom> pointList = new ArrayList<>();
    private final List<Point2DCustom> allPoints = new ArrayList<>();
    private List<PolygonCustom> polygonListGlobal = new ArrayList<>();
    private Point2DCustom startPoint;
    private Point2DCustom endPoint;
    private AVLTree<Double> avl;

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

            Point2DCustom[] points;

            do {
                //polygon = new PolygonCustom();
                int verticesNumber = random.nextInt(MIN_VERTICES, MAX_VERTICES);
                points = pointList.toArray(new Point2DCustom[verticesNumber]);
                Point2DCustom centerPoint;

                do {
                    centerPoint = new Point2DCustom(
                            random.nextDouble(SHAPE_SIZE, maxX - SHAPE_SIZE),
                            random.nextDouble(SHAPE_SIZE, maxY - SHAPE_SIZE)
                    );
                } while (inAnyPolygon(polygonList, centerPoint));

                for (int j = 0; j < verticesNumber; j++) {
                    points[j] = new Point2DCustom(
                            random.nextDouble(centerPoint.getX() - SHAPE_SIZE, centerPoint.getX() + SHAPE_SIZE),
                            random.nextDouble(centerPoint.getY() - SHAPE_SIZE, centerPoint.getY() + SHAPE_SIZE));
                }
                sortPointsClockwise(points, centerPoint);

            } while (intersectsAnyPolygon(polygonList, new PolygonCustom(points)));

            PolygonCustom polygon = new PolygonCustom(points);
            allPoints.addAll(List.of(polygon.PointsList));
            setRandomColor(polygon);
            polygonList.add(polygon);
        }

        polygonListGlobal = polygonList;

    }

    protected void genRandomPoints(){
        double maxX = canvas.getHeight();
        double maxY = canvas.getWidth();

        do{
            startPoint = new Point2DCustom(random.nextDouble(10, maxX-10), random.nextDouble(10, maxY-10));
        } while (inAnyPolygon(polygonListGlobal,startPoint));

        do{
            endPoint = new Point2DCustom(random.nextDouble(10, maxX-10), random.nextDouble(10, maxY-10));
        } while (inAnyPolygon(polygonListGlobal,endPoint));

        allPoints.add(startPoint);
        allPoints.add(endPoint);
    }

    @FXML
    protected void createRandomExample(){
        allPoints.clear();
        clearCanvas();
        genRandomPolygons();
        for (Polygon polygon : polygonListGlobal) {
            draw(polygon);
        }

        genRandomPoints();

        for(Point2DCustom p : allPoints){
            for(Point2DCustom p1 : VisibleVertices(p)){
                draw(new Circle(p.getX(),p.getY(),1));
                draw(new LineCustom(p,p1));
            }
        }
        draw(new Circle(startPoint.getX(),startPoint.getY(),3,Color.RED));
        draw(new Circle(endPoint.getX(),endPoint.getY(),3,Color.BLUE));
        //calculateVisibilityGraph();

    }
    private static void sortPointsClockwise(Point2DCustom[] points, Point2DCustom center) {
        boolean changed;
        do {
            changed = false;
            for (int i = 0; i < points.length - 1; i++) {
                if (comparePoint(points[i + 1], points[i], center)) {
                    Point2DCustom temp = points[i];
                    points[i] = points[i + 1];
                    points[i + 1] = temp;
                    changed = true;
                }
            }
        } while (changed);
    }

    private static boolean comparePoint(Point2DCustom a, Point2DCustom b, Point2DCustom center) {

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

        Point2DCustom currentPoint = new Point2DCustom(event.getX(), event.getY());
        pointList.add(currentPoint);

        if (pointList.size() > 3) {
            Point2DCustom firstPoint = pointList.get(0);
            Point2DCustom lastPoint = pointList.get(pointList.size() - 1);

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
        Point2DCustom previousPoint = pointList.get(pointList.size() - 2);
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

    public boolean inAnyPolygon(List<PolygonCustom> plgList, Point2DCustom pt){
        for (PolygonCustom p : plgList) {
            if (p.contains(pt)){
                return true;
            }
        }
        return false;
    }

    public boolean intersectsAnyPolygon(List<PolygonCustom> plgList, Node shape){
        for (PolygonCustom p : plgList) {
            if (p.intersects(shape.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    boolean Visible(List<PolygonCustom> polygons, LineCustom line) {
        double x1 = line.getStartX();
        double y1 = line.getStartY();
        double x2 = line.getEndX();
        double y2 = line.getEndY();
        // go through each of the vertices, plus the next
        // vertex in the list
        int next = 0;


        for(PolygonCustom pol : polygons) {
            Point2DCustom[] vertices = pol.getPointsList();
            for (int current = 0; current < vertices.length; current++) {

                // get next vertex in list
                // if we've hit the end, wrap around to 0
                next = current + 1;
                if (next == vertices.length) next = 0;

                // get the PVectors at our current position
                // extract X/Y coordinates from each
                double x3 = vertices[current].getX();
                double y3 = vertices[current].getY();
                double x4 = vertices[next].getX();
                double y4 = vertices[next].getY();

                // do a Line/Line comparison
                // if true, return 'true' immediately and
                // stop testing (faster)
                boolean hit = lineLine(x1, y1, x2, y2, x3, y3, x4, y4);
                if (hit) {
                    return false;
                }
            }
        }
        //System.out.println(euclideanDistance(line.getStartPoint(),line.getEndPoint()));
        return true;
    }

    boolean lineLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {

        double denominator = ((x2 - x1) * (y4 - y3)) - ((y2 - y1) * (x4 - x3));
        double numerator1 = ((y1 - y3) * (x4 - x3)) - ((x1 - x3) * (y4 - y3));
        double numerator2 = ((y1 - y3) * (x2 - x1)) - ((x1 - x3) * (y2 - y1));

        if (denominator == 0) return numerator1 == 0 && numerator2 == 0;

        double r = numerator1 / denominator;
        double s = numerator2 / denominator;
        r = Math.round(r*10000.0)/10000.0;
        s = Math.round(s*10000.0)/10000.0;
        return (r > 0.0 && r < 1.0) && (s > 0.0 && s < 1.0);
    }

    public ArrayList<Point2DCustom> VisibleVertices(Point2DCustom p){
        ArrayList<Point2DCustom> result = new ArrayList<Point2DCustom>();
        for(PolygonCustom pol : polygonListGlobal){
            for(Point2DCustom vert : pol.PointsList){

                if(
                        (p!=vert) &&
                        (!((new ArrayList<>(Arrays.asList(pol.PointsList)).contains(p)) &&
                        (new ArrayList<>(Arrays.asList(pol.PointsList)).contains(vert)))) &&
                        (Visible(polygonListGlobal,new LineCustom(p,vert)))
                ){
                    result.add(vert);
                } else if (new ArrayList<>(Arrays.asList(pol.PointsList)).contains(p)) {
                    result.add(pol.leftNeighbour(p));
                    result.add(pol.rightNeighbour(p));
                }
            }
        }
        if ((p==startPoint) && (Visible(polygonListGlobal,new LineCustom(p,endPoint)))){
            result.add(endPoint);
        } else if ((p==endPoint) && (Visible(polygonListGlobal,new LineCustom(p,startPoint)))) {
            result.add(startPoint);
        }
        return result;
    }

    public double euclideanDistance(Point2DCustom p1, Point2DCustom p2){
        double xDiff = p1.getX() - p2.getX();
        double yDiff = p1.getY() - p2.getY();

        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }

    public Graph calculateVisibilityGraph() {
        Graph graph = new Graph(allPoints.size());
        for (Point2DCustom vertex : allPoints) {
            ArrayList<Point2DCustom> visibleFromVertex = VisibleVertices(vertex);
            for (Point2DCustom visibleVertex : visibleFromVertex) {
                if (vertex != visibleVertex) {
                    Hashtable<Point2DCustom, Double> edgeToAdd = new Hashtable<Point2DCustom, Double>();
                    edgeToAdd.put(visibleVertex,0.0);
                    graph.addEdge(vertex, edgeToAdd);
                    }
                }
            }
        return graph;
        }

}