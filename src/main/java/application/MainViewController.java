package application;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.json.JSONWriter;

import java.io.*;
import java.util.*;

public class MainViewController {

    @FXML
    private AnchorPane canvas;
    @FXML
    private ToggleButton drawToggle;

    private final Random random = new Random();
    private List<Point2D> pointList = new ArrayList<>();

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

    @FXML
    protected void genRandomPolygons() {
        clearCanvas();
        double maxX = canvas.getHeight();
        double maxY = canvas.getWidth();
        List<Polygon> polygonList = new ArrayList<>();

        int PolygonTotal = random.nextInt(5,10);
        for (int i = 0; i < PolygonTotal; i++) {
            boolean isIntersected;
            Polygon polygon;
            do{
                polygon = new Polygon();
                isIntersected = false;
                int verticesNumber = random.nextInt(3, 7);
                Point2D[] points = pointList.toArray(new Point2D[verticesNumber]);
                boolean PointInPolygon = false;
                Point2D centerPoint;
                do {
                    centerPoint = new Point2D(random.nextDouble(50, maxX-50), random.nextDouble(50, maxY-50));

                    if (polygon.contains(centerPoint)){
                        PointInPolygon = true;
                        break;
                    }
                } while (PointInPolygon);

                for (int j = 0; j < verticesNumber; j++) {
                    points[j] = new Point2D(random.nextDouble(centerPoint.getX() - 50,centerPoint.getX() + 50), random.nextDouble(centerPoint.getY() - 50,centerPoint.getY() + 50));
                }
                sortPointsClockwise(points, centerPoint);

                ArrayList<Double> list = null;

                for (Point2D point : points) {
                    polygon.getPoints().add(point.getX());
                    polygon.getPoints().add(point.getY());
                }

                for(Polygon p:polygonList) {
                    if (p.getBoundsInParent().intersects(polygon.getBoundsInParent())){
                        isIntersected = true;
                        break;
                    }
                }
                System.out.println(polygon);
            }while(isIntersected);

            setRandomColor(polygon);
            polygonList.add(polygon);

        }

        for (Polygon polygon : polygonList) {
            draw(polygon);
        }
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
        List<String> lines = readAllLines(file);

        clearCanvas();
        for (String line : lines) {
            String[] elements = line.split(" ");
            switch (elements[0]) {
                case "Polygon:":
                    Polygon polygon;
                    List<Double> pointList = new ArrayList<>();
                    for (int i = 1; i < elements.length; i++) {
                        pointList.add(Double.valueOf(elements[i]));
                    }
                    double[] points = pointList.stream().mapToDouble(i -> i).toArray();
                    polygon = new Polygon(points);
                    draw(polygon);
            }
        }
    }

    @FXML
    protected void exportToFile() {
        Stage filePickerWindow = new Stage();
        List<Node> elements = canvas.getChildren();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export to File");
        File file = fileChooser.showSaveDialog(filePickerWindow);
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONWriter writer;
        try (FileWriter fileWriter = new FileWriter(file)) {
            for (Node element : elements) {
                if (element.getClass() == Polygon.class) {
                    List<Double> points = ((Polygon) element).getPoints();

                    fileWriter.write("Polygon: ");
                    for (Double point : points) {
                        fileWriter.write(point + " ");
                    }
                    fileWriter.write("\n");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> readAllLines(File file) {
        List<String> strings = new ArrayList<>();

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                strings.add(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return strings;
    }
}
