package application;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
import org.jgrapht.util.AVLTree;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.*;

public class MainViewController implements Initializable {

    protected static List<Point2DCustom> allPoints = new ArrayList<>();
    protected static List<PolygonCustom> polygonListGlobal = new ArrayList<>();
    protected static Point2DCustom startPoint;
    private Circle startPointCircle = new Circle();
    protected static Point2DCustom endPoint;
    private Circle endPointCircle = new Circle();
    static Graph graph;
    private static boolean canvasIsEmpty = true;
    @FXML
    private TextField MIN_VERTICES;
    @FXML
    private TextField MAX_VERTICES;
    @FXML
    private CheckBox allPaths;

    private final Stage warningStage = new Stage();
    private final Random random = new Random();
    private final List<Point2DCustom> pointList = new ArrayList<>();
    private Circle cursor = new Circle(0, 0, 2);
    @FXML
    private AnchorPane canvas;
    @FXML
    private ToggleButton drawToggle;
    @FXML
    private ToggleButton setStart;
    @FXML
    private ToggleButton setEnd;
    @FXML
    private TextField POLYGON_NUMBER;
    @FXML
    private Slider SHAPE_SIZE;
    @FXML
    private Label warningText;
    @FXML
    private AnchorPane warningPane;
    @FXML
    private Label coordinatesDisplay;

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

    public static boolean PolLine(List<PolygonCustom> polygons, LineCustom line) {
        double x1 = line.getStartX();
        double y1 = line.getStartY();
        double x2 = line.getEndX();
        double y2 = line.getEndY();
        // go through each of the vertices, plus the next
        // vertex in the list
        int next = 0;


        for (PolygonCustom pol : polygons) {
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

    public static boolean lineLine(double x1, double y1, double x2, double y2, double x3, double y3, double x4, double y4) {

        double denominator = ((x2 - x1) * (y4 - y3)) - ((y2 - y1) * (x4 - x3));
        double numerator1 = ((y1 - y3) * (x4 - x3)) - ((x1 - x3) * (y4 - y3));
        double numerator2 = ((y1 - y3) * (x2 - x1)) - ((x1 - x3) * (y2 - y1));

        if (denominator == 0) return numerator1 == 0 && numerator2 == 0;

        double r = numerator1 / denominator;
        double s = numerator2 / denominator;
        r = Math.round(r * 10000.0) / 10000.0;
        s = Math.round(s * 10000.0) / 10000.0;
        return (r > 0.0 && r < 1.0) && (s > 0.0 && s < 1.0);
    }

    public static void calculateVisibiltyGraph(){
        if ((PolLine(polygonListGlobal, new LineCustom(startPoint, endPoint)))){
            graph.addEdge(startPoint, new VisibleVertex(endPoint, 0.0));
            graph.addEdge(endPoint, new VisibleVertex(startPoint, 0.0));
        }
        for (PolygonCustom pol : polygonListGlobal){
            for (Point2DCustom currentPoint : pol.PointsList){
                graph.addEdge(currentPoint, new VisibleVertex(pol.leftNeighbour(currentPoint), 0.0));
                graph.addEdge(currentPoint, new VisibleVertex(pol.rightNeighbour(currentPoint), 0.0));
                for(Point2DCustom comparedPoint : allPoints){
                    if (inSamePolygon(currentPoint,comparedPoint)==null && currentPoint!=comparedPoint) {
                        if ((PolLine(polygonListGlobal, new LineCustom(currentPoint, comparedPoint)))) {
                            graph.addEdge(currentPoint, new VisibleVertex(comparedPoint, 0.0));
                        }
                        if((!inAnyPolygon(polygonListGlobal,comparedPoint)) && (PolLine(polygonListGlobal, new LineCustom(currentPoint, comparedPoint)))){
                            graph.addEdge(comparedPoint, new VisibleVertex(currentPoint, 0.0));
                        }
                    }
                }
            }
        }
    }

    public static void checkPolGen(int tries) throws ShapeSizeException {
        if (tries >= 200) {
            throw new ShapeSizeException("Shapes are too many and/or too big. Reduce the shape number and/or size");
        }
    }

    public static void checkEmpty(AnchorPane a) throws EmptyCanvasException {
        if (canvasIsEmpty) {
            throw new EmptyCanvasException("Canvas Already empty");
        } else {
            a.getChildren().clear();
            canvasIsEmpty = true;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        startPointCircle.setVisible(false);
        endPointCircle.setVisible(false);
        canvas.getChildren().add(cursor);
    }

    protected void draw(Node node) {
        canvas.getChildren().add(node);
        canvasIsEmpty = false;
    }

    public void setRandomColor(Shape shape) {
        shape.setFill(new Color(
                random.nextDouble(0, 1),
                random.nextDouble(0, 1),
                random.nextDouble(0, 1),
                1));
    }

    @FXML
    public void clearCanvasEvent() {

        try {
            checkEmpty(canvas);
        } catch (EmptyCanvasException e) {
            System.out.println(e.getMessage());
        }
    }

    public void clearCanvas() {
        canvas.getChildren().clear();
    }

    public void genRandomPolygons() {
        double maxX = canvas.getWidth();
        double maxY = canvas.getHeight();
        int genTriesCount = 0;
        boolean overLimit = false;

        List<PolygonCustom> polygonList = new ArrayList<>();
        int PolygonTotal = Integer.parseInt(POLYGON_NUMBER.getText());

        outer:
        for (int i = 0; i < PolygonTotal; i++) {

            Point2DCustom[] points;

            do {
                //polygon = new PolygonCustom();
                int verticesNumber = random.nextInt(Integer.parseInt(MIN_VERTICES.getText()), Integer.parseInt(MAX_VERTICES.getText()));
                points = pointList.toArray(new Point2DCustom[verticesNumber]);
                Point2DCustom centerPoint;

                do {
                    centerPoint = new Point2DCustom(
                            random.nextDouble(SHAPE_SIZE.getValue(), maxX - SHAPE_SIZE.getValue()),
                            random.nextDouble(SHAPE_SIZE.getValue(), maxY - SHAPE_SIZE.getValue())
                    );
                } while (inAnyPolygon(polygonList, centerPoint));

                for (int j = 0; j < verticesNumber; j++) {
                    points[j] = new Point2DCustom(
                            random.nextDouble(centerPoint.getX() - SHAPE_SIZE.getValue(), centerPoint.getX() + SHAPE_SIZE.getValue()),
                            random.nextDouble(centerPoint.getY() - SHAPE_SIZE.getValue(), centerPoint.getY() + SHAPE_SIZE.getValue()));
                }
                sortPointsClockwise(points, centerPoint);
                genTriesCount++;
                try {
                    checkPolGen(genTriesCount);
                } catch (ShapeSizeException e) {
                    System.out.println(e.getMessage());
                    exceptionPopup(e.getMessage());
                    break outer;
                }
            } while (intersectsAnyPolygon(polygonList, new PolygonCustom(points)));

            PolygonCustom polygon = new PolygonCustom(points);
            allPoints.addAll(List.of(polygon.PointsList));
            setRandomColor(polygon);
            polygonList.add(polygon);
        }

        polygonListGlobal = polygonList;

    }

    public void genRandomPoints() {
        double maxX = canvas.getWidth();
        double maxY = canvas.getHeight();

        do {
            startPoint = new Point2DCustom(random.nextDouble(0, maxX), random.nextDouble(0, maxY));
        } while (inAnyPolygon(polygonListGlobal, startPoint));

        do {
            endPoint = new Point2DCustom(random.nextDouble(0, maxX), random.nextDouble(0, maxY));
        } while (inAnyPolygon(polygonListGlobal, endPoint));

        allPoints.add(startPoint);
        allPoints.add(endPoint);

        startPointCircle = new Circle(startPoint.getX(),startPoint.getY(),4,Color.LIGHTBLUE);
        endPointCircle = new Circle(endPoint.getX(),endPoint.getY(),4,Color.DARKBLUE);
        startPointCircle.setVisible(true);
        endPointCircle.setVisible(true);

    }

    @FXML
    protected void createRandomExample() {
        clearCanvas();
        allPoints.clear();
        genRandomPolygons();
        for (Polygon polygon : polygonListGlobal) {
            draw(polygon);
        }

        genRandomPoints();

        draw(startPointCircle);
        draw(endPointCircle);
    }

    @FXML
    protected void mouseClicked(MouseEvent event) {
        if (drawToggle.isSelected()) {
            setEnd.setSelected(false);
            setStart.setSelected(false);

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
        if (setStart.isSelected()) {
            drawToggle.setSelected(false);
            setEnd.setSelected(false);


            Point2DCustom currentPoint = new Point2DCustom(event.getX(), event.getY());

            List<Node> elements = canvas.getChildren();
            for (int i = elements.size() - 1; i >= 0; i--) {
                Node element = elements.get(i);
                 if (element.getClass() == Circle.class) {
                    if (((Circle) element).getFill() == Color.LIGHTBLUE) {
                        elements.remove(i);
                    }
                }
            }
            allPoints.remove(startPoint);
            startPoint = currentPoint;
            allPoints.add(startPoint);
            startPointCircle = new Circle(startPoint.getX(),startPoint.getY(),4,Color.LIGHTBLUE);
            draw(startPointCircle);
        }
        if (setEnd.isSelected()) {
            drawToggle.setSelected(false);
            setStart.setSelected(false);
            Point2DCustom currentPoint = new Point2DCustom(event.getX(), event.getY());
            List<Node> elements = canvas.getChildren();
            for (int i = elements.size() - 1; i >= 0; i--) {
                Node element = elements.get(i);
                if (element.getClass() == Circle.class) {
                    if (((Circle) element).getFill() == Color.DARKBLUE) {
                        elements.remove(i);
                    }
                }
            }
            allPoints.remove(endPoint);
            endPoint = currentPoint;
            allPoints.add(endPoint);
            endPointCircle = new Circle(endPoint.getX(),endPoint.getY(),4,Color.DARKBLUE);
            draw(endPointCircle);
        }
    }

    @FXML
    protected void mouseEntered() {
        coordinatesDisplay.setVisible(true);
    }

    @FXML
    protected void mouseExited() {
        canvas.getChildren().remove(cursor);
        coordinatesDisplay.setVisible(false);
    }

    @FXML
    protected void mouseMoved(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();

        cursor.setCenterX(mouseX);
        cursor.setCenterY(mouseY);
        cursor.setFill(Color.RED);

        List<Node> elements = canvas.getChildren();
        for (int i = 0; i < elements.size(); i++) {
            Node element = elements.get(i);

            if (element.getClass() != Circle.class) continue;
            if (((Circle) element).getFill() != Color.RED) continue;
            if (((Circle) element).getRadius() != 5) continue;

            //noinspection SuspiciousListRemoveInLoop
            elements.remove(i);
        }

        coordinatesDisplay.setText("X: " + Math.round(mouseX * 100f) / 100 +
                "  " +
                "Y: " + Math.round(mouseY * 100f) / 100);
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
            } else if ((element.getClass() == Circle.class) && (((Circle) element).getFill() == Color.BLUE)) {
                elements.remove(i--);
            }
        }
    }

    private void finishPolygon() {
        pointList.remove(pointList.size() - 1);

        Point2DCustom[] points = new Point2DCustom[pointList.size()];
        for (int i = 0; i < pointList.size(); i++) {
            points[i]=new Point2DCustom(pointList.get(i).getX(),pointList.get(i).getY());
        }

        PolygonCustom polygon = new PolygonCustom(points);
        setRandomColor(polygon);
        polygonListGlobal.add(polygon);
        allPoints.addAll(Arrays.asList(polygon.PointsList));
        draw(polygon);
        pointList.clear();
        clearPath();
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
        content = content.trim();
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
            Circle circle = new Circle(centerX, centerY, radius);
            circle.setFill(Paint.valueOf(circleArray.getJSONObject(i).getString("color")));
            draw(circle);
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
                if (element.getClass() == PolygonCustom.class) {
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
                    if (((Circle) element).getFill() == Color.LIME) {
                        object.put("color", "Lime");
                    } else if (((Circle) element).getFill() == Color.BLUE) {
                        object.put("color", "Blue");
                    }

                    circleArray.put(object);
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

    public static boolean inAnyPolygon(List<PolygonCustom> plgList, Point2DCustom pt) {
        for (PolygonCustom p : plgList) {
            if (p.contains(pt)) {
                return true;
            }
        }
        return false;
    }

    public boolean intersectsAnyPolygon(List<PolygonCustom> plgList, Node shape) {
        for (PolygonCustom p : plgList) {
            if (p.intersects(shape.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }

    public static PolygonCustom inSamePolygon(Point2DCustom p1, Point2DCustom p2){
        for (PolygonCustom pol: polygonListGlobal){
            if (Arrays.asList(pol.PointsList).contains(p1) && Arrays.asList(pol.PointsList).contains(p2)){
                return pol;
            }
        }
        return null;
    }

    public void exceptionPopup(String message){
        Scene scene = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("polygon_limit.fxml"));
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Error loading FXML file: " + e.getMessage());
        }

        warningText = new Label(message);
        //warningText.setText(message);

        warningStage.setResizable(false);
        warningStage.setTitle("Warning");
        warningStage.setScene(scene);

        warningStage.show();
    }

    @FXML
    private void terminateWarningWindow(ActionEvent event) {
        Stage stage = (Stage) warningPane.getScene().getWindow();
        stage.close();
    }

    private void clearPath(){
        List<Node> elements = canvas.getChildren();

        for (int i = elements.size() - 1; i >= 0; i--) {
            Node element = elements.get(i);
            if (element.getClass() == LineCustom.class) {
                elements.remove(i);
            } else if (element.getClass() == Circle.class) {
                if (((Circle) element).getFill() == Color.LIGHTGRAY) {
                    elements.remove(i);
                    break;
                }
            }
        }
    }

    @FXML
    public void pathfind() {
        clearPath();
        graph = new Graph(allPoints.size());
        calculateVisibiltyGraph();
        graph.calculateEuclidieanDistances();

        if (allPaths.isSelected()) {
            for (Point2DCustom p : graph.AdjList.keySet()) {
                for (VisibleVertex p1 : graph.AdjList.get(p)) {
                    LineCustom l = new LineCustom(p, p1.vertex);
                    l.setStroke(Color.WHITE);
                    l.setOpacity(0.2);
                    draw(l);
                    draw(new Circle(p.getX(), p.getY(), 2, Color.LIGHTGRAY));
                }
            }
        }

        ArrayList<Point2DCustom> shortP = Dijkstra.shortestPath(graph, startPoint, endPoint);

        for (int i=0; i < shortP.size()-1;i++){
            LineCustom pathLine = new LineCustom(shortP.get(i),shortP.get(i+1));
            pathLine.setStroke(Color.RED);
            pathLine.setStrokeWidth(3);
            pathLine.setOpacity(0.65);
            draw(pathLine);
        }
    }

    static class ShapeSizeException extends Exception {
        public ShapeSizeException(String message) {
            super("\u001B[31m" + "ShapeSizeException: " + message + "\u001B[0m");
        }
    }

    static class EmptyCanvasException extends Exception {
        public EmptyCanvasException(String message) {
            super("\u001B[31m" + "EmptyCanvasException: " + message + "\u001B[0m");
        }
    }
}