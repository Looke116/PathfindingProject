package application;

import java.util.*;
public class Graph {
    protected int noOfVertices;
    public HashMap<Point2DCustom, ArrayList<VisibleVertex>> AdjList;

    public Graph(int noOfVertices) {
        this.noOfVertices = noOfVertices;
        AdjList = new HashMap<Point2DCustom, ArrayList<VisibleVertex>>();
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
