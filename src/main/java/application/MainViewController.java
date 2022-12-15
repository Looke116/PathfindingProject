package application;

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

        for (int i = 0; i < 5; i++) {
            int verticesNumber = random.nextInt(3, 7);
            double[] points = new double[verticesNumber * 2];
            double[] centerPoint = {random.nextDouble(0, maxX), random.nextDouble(0, maxY)};

            for (int j = 0; j < verticesNumber; j++) {
                points[j * 2] = random.nextDouble(centerPoint[0] - 100, centerPoint[0] + 100);
                points[j * 2 + 1] = random.nextDouble(centerPoint[1] - 100, centerPoint[1] + 100);
            }

            Polygon polygon = new Polygon(sortPointsClockwise(points));
            setRandomColor(polygon);
            polygonList.add(polygon);
        }

        for (Polygon polygon : polygonList) {
            draw(polygon);
        }
    }

    private double[] sortPointsClockwise(double[] points) {
        List<Point2D> pointList = new ArrayList<>();
        Map<Double, List<Integer>> sortList = new HashMap<>();

        // Convert to ArrayList
        for (int i = 0; i < points.length / 2; i++) {
            pointList.add(new Point2D(points[i * 2], points[i * 2 + 1]));
        }

        // Compute the center point relative to other all points
        double centerX = 0, centerY = 0;
        for (Point2D point : pointList) {
            centerX += point.getX();
            centerY += point.getY();
        }
        Point2D centerPoint = new Point2D(centerX, centerY);

        // Compute the angle for each point relative to the center point
        // and store it along it's index
        for (int i = 0; i < pointList.size(); i++) {
            Point2D point = pointList.get(i);
            double angle = Math.atan2(
                    point.getY() - centerPoint.getY(),
                    point.getX() - centerPoint.getX()
            );

            List<Integer> list;
            if (sortList.containsKey(angle)) {
                list = sortList.get(angle);
            } else {
                list = new ArrayList<>();
            }
            list.add(i);
            sortList.put(angle, list);
        }

        // Sort the list by the angle
        Set<Double> angles = sortList.keySet();
        List<Double> sortedAngles = angles.stream().sorted().toList();
        List<Double> outputPoints = new ArrayList<>();
        for (Double angle : sortedAngles) {
            for (Integer index : sortList.get(angle)) {
                outputPoints.add(pointList.get(index).getX());
                outputPoints.add(pointList.get(index).getY());
            }
        }

        return outputPoints.stream().mapToDouble(i -> i).toArray();
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
