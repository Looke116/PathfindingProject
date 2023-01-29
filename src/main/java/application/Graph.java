package application;

import java.util.*;

import static application.MainViewController.allPoints;

public class Graph {
    protected int noOfVertices;
    public HashMap<Point2DCustom, ArrayList<VisibleVertex>> AdjList;

    //Constructor
    public Graph(int noOfVertices) {
        this.noOfVertices = noOfVertices;
        AdjList = new HashMap<Point2DCustom, ArrayList<VisibleVertex>>();
    }

    //Calculates Euclidean distances between all points in the list of vertices
    public void calculateEuclidieanDistances(){
        //Iterate through all points in the list of vertices
        for (Point2DCustom vertex : allPoints) {
            //Retrieve the list of visible vertices for the current point
            ArrayList<VisibleVertex> edgeList = this.AdjList.get(vertex);
            //Iterate through the list of visible vertices for the current point
            for (VisibleVertex edge : edgeList) {
                //Calculate the Euclidean distance between the current point and the current visible vertex
                edge.distance = euclideanDistance(vertex, edge.vertex);
            }
        }
    }

    //Calculates the Euclidean distance between two points
    public static double euclideanDistance(Point2DCustom p1, Point2DCustom p2){
        double xDiff = p1.getX() - p2.getX();
        double yDiff = p1.getY() - p2.getY();

        return Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }



    public void addEdge(Point2DCustom src, VisibleVertex dest){
        //If the source point is not already in the Adjacency List,
        //add it as a key with an empty ArrayList as its value
        this.AdjList.computeIfAbsent(src, k -> new ArrayList<VisibleVertex>());
        //Add the destination vertex to the ArrayList of visible vertices for the source point
        try {
            this.AdjList.get(src).add(dest);
        } catch (NullPointerException e) {
            System.err.println("Error adding edge: " + e.getMessage());
        }
    }

}
