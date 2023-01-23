package application;

import java.util.*;

import static application.MainViewController.allPoints;

public class Graph extends Thread {
    protected int noOfVertices;
    public HashMap<Point2DCustom, ArrayList<VisibleVertex>> AdjList;

    public Graph(int noOfVertices) {
        this.noOfVertices = noOfVertices;
        AdjList = new HashMap<Point2DCustom, ArrayList<VisibleVertex>>();
    }

    @Override
    public void run() {
        for (Point2DCustom vertex : allPoints) {

            ArrayList<Point2DCustom> visibleFromVertex = MainViewController.VisibleVertices(vertex);
            ArrayList<VisibleVertex> edges = new ArrayList<VisibleVertex>();

            for (Point2DCustom visibleVertex : visibleFromVertex) {
                addEdge(vertex,new VisibleVertex(visibleVertex,0.0));
            }
        }
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
