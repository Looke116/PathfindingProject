package application;

import java.util.*;

public class Dijkstra {
    public static ArrayList<Point2DCustom> shortestPath(Graph graph, Point2DCustom start, Point2DCustom end) {
        // Create a priority queue to store vertices that are waiting to be processed
        PriorityQueue<VisibleVertex> queue = new PriorityQueue<>(new Comparator<VisibleVertex>() {
            @Override
            public int compare(VisibleVertex v1, VisibleVertex v2) {
                return Double.compare(v1.distance, v2.distance);
            }
        });

        // Create a HashMap to store the shortest distance from the start vertex to each vertex
        HashMap<Point2DCustom, Double> distance = new HashMap<>();
        for (Point2DCustom vertex : graph.AdjList.keySet()) {
            distance.put(vertex, Double.MAX_VALUE);
        }
        distance.replace(start, 0.0);

        // Create a HashMap to store the previous vertex in the shortest path from the start vertex to each vertex
        HashMap<Point2DCustom, Point2DCustom> previous = new HashMap<>();

        // Add the start vertex to the queue
        queue.offer(new VisibleVertex(start, 0));

        int n = 0;

        while (!queue.isEmpty()) {
            // Get the vertex with the shortest distance from the queue
            Point2DCustom current = queue.poll().vertex;

            // If the extracted vertex is the end vertex, the shortest path has been found
            if (current.equals(end)) {
                break;
            }
            // Get the neighbors of the current vertex
            ArrayList<VisibleVertex> neighbors = graph.AdjList.get(current);

            // Update the distance of each neighbor
            for (VisibleVertex neighbor : neighbors) {
                double newDistance = distance.get(current) + neighbor.distance;
                if (newDistance < distance.get(neighbor.vertex)) {
                    distance.replace(neighbor.vertex, newDistance);
                    previous.put(neighbor.vertex, current);
                    queue.offer(new VisibleVertex(neighbor.vertex, newDistance));
                    n++;
                }
            }
        }
        System.out.println(n);
        // Create an ArrayList to store the shortest path from the start vertex to the end vertex
        ArrayList<Point2DCustom> path = new ArrayList<>();
        path.add(end);

        // Get the previous vertex in the path
        Point2DCustom previousVertex = previous.get(end);

        // Iterate through the previous vertices to build the shortest path
        while (previousVertex != null) {
            path.add(previousVertex);
            previousVertex = previous.get(previousVertex);
        }

        // Reverse the order of the path to get the correct order
        Collections.reverse(path);

        return path;
    }
}