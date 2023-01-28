package application;

import java.util.*;

import static application.MainViewController.allPoints;

public class Graph {
    protected int noOfVertices;
    public HashMap<Point2DCustom, ArrayList<VisibleVertex>> AdjList;

    public Graph(int noOfVertices) {
        this.noOfVertices = noOfVertices;
        AdjList = new HashMap<Point2DCustom, ArrayList<VisibleVertex>>();
    }

    public void calculateVisibiltyGraph() {
        for (Point2DCustom vertex : allPoints) {

            ArrayList<Point2DCustom> visibleFromVertex = MainViewController.VisibleVertices(vertex);
            //ArrayList<VisibleVertex> edges = new ArrayList<VisibleVertex>();

            for (Point2DCustom visibleVertex : visibleFromVertex) {
                addEdge(vertex,new VisibleVertex(visibleVertex,0.0));
            }
        }
    }

    public void calculateEuclidieanDistances(){
        for (Point2DCustom vertex : allPoints) {
            ArrayList<VisibleVertex> edgeList = this.AdjList.get(vertex);
            for (VisibleVertex edge : edgeList) {

                edge.distance = euclideanDistance(vertex, edge.vertex);
            }
        }
    }

    public static double euclideanDistance(Point2DCustom p1, Point2DCustom p2){
        double xDiff = p1.getX() - p2.getX();
        double yDiff = p1.getY() - p2.getY();

        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }



    public void addEdge(Point2DCustom src, VisibleVertex dest){
        if (this.AdjList.get(src)==null){
            this.AdjList.put(src,new ArrayList<VisibleVertex>());
        } else {
            try {
                this.AdjList.get(src).add(dest);
            } catch (NullPointerException e) {
                System.err.println("Error adding edge: " + e.getMessage());
            }
        }
    }

    @Override
    public String toString() {
        return "Graph{" +
                "noOfVertices=" + noOfVertices +
                ", AdjList=" + AdjList +
                '}';
    }
}
