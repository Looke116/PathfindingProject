package application;

import java.util.*;

public class Dijkstra {
    public static ArrayList<Point2DCustom> shortestPath(Graph graph, Point2DCustom start, Point2DCustom end) {
        // Initialize variables
        HashMap<Point2DCustom, Double> distances = new HashMap<Point2DCustom, Double>();
        HashMap<Point2DCustom, Point2DCustom> previous = new HashMap<Point2DCustom, Point2DCustom>();
        PriorityQueue<VisibleVertex> unvisited = new PriorityQueue<VisibleVertex>((a, b) -> (int)(a.distance - b.distance));

        // Set initial distance for all vertices to infinity, except for start vertex
        for (Point2DCustom vertex : graph.AdjList.keySet()) {
            if (vertex.equals(start)) {
                distances.put(vertex, 0.0);
                unvisited.add(new VisibleVertex(vertex, 0.0));
            } else {
                distances.put(vertex, Double.POSITIVE_INFINITY);
                unvisited.add(new VisibleVertex(vertex, Double.POSITIVE_INFINITY));
            }
        }

        // Dijkstra algorithm
        while (!unvisited.isEmpty()) {
            VisibleVertex current = unvisited.poll();
            System.out.println(unvisited);
            // End search if we have reached the end vertex
            if (current.vertex.equals(end)) {
                distances.put(end, distances.get(current.vertex));
                break;
            }

            // Update distances for neighboring vertices
            ArrayList<VisibleVertex> edges = graph.AdjList.get(current.vertex);
            for (VisibleVertex edge : edges) {
                Point2DCustom neighbor = edge.vertex;
                double distance = edge.distance;

                double newDistance = distances.get(current.vertex) + distance;
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, current.vertex);
                    unvisited.remove(new VisibleVertex(neighbor, distances.get(neighbor)));
                    unvisited.add(new VisibleVertex(neighbor, newDistance));
                }
            }


        }

        System.out.println(distances);
        // Build the shortest path by following the previous pointers
        ArrayList<Point2DCustom> path = new ArrayList<Point2DCustom>();
        Point2DCustom current = end;
        while (current != null) {
            path.add(current);
            current = previous.get(current);
        }

        // Reverse the path and return
        java.util.Collections.reverse(path);
        return path;
    }
}
