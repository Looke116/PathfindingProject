package application;

import java.util.*;

public class Dijkstra {
    public static ArrayList<Point2DCustom> shortestPath(Graph graph, Point2DCustom start, Point2DCustom end) {
// Initialize data structures
        HashMap<Point2DCustom, Double> distanceMap = new HashMap<>();
        HashMap<Point2DCustom, Point2DCustom> predecessorMap = new HashMap<>();
        PriorityQueue<VisibleVertex> priorityQueue = new PriorityQueue<>(new DistanceComparator());
        // Initialize distance map and predecessor map
        for (Point2DCustom vertex : graph.AdjList.keySet()) {
            distanceMap.put(vertex, Double.MAX_VALUE);
            predecessorMap.put(vertex, null);
        }
        distanceMap.put(start, 0.0);

        // Add start vertex to priority queue
        priorityQueue.add(new VisibleVertex(start, 0.0));

        // Run Dijkstra's algorithm
        while (!priorityQueue.isEmpty()) {
            VisibleVertex current = priorityQueue.poll();
            Point2DCustom currentVertex = current.vertex;

            // Iterate through all neighboring vertices
            for (VisibleVertex neighbor : graph.AdjList.get(currentVertex)) {
                Point2DCustom neighborVertex = neighbor.vertex;
                double distance = neighbor.distance;

                // Update distance and predecessor if a shorter path is found
                if (distanceMap.get(currentVertex) + distance < distanceMap.get(neighborVertex)) {
                    distanceMap.put(neighborVertex, distanceMap.get(currentVertex) + distance);
                    predecessorMap.put(neighborVertex, currentVertex);
                    priorityQueue.add(new VisibleVertex(neighborVertex, distanceMap.get(neighborVertex)));
                }
            }
        }

        // Build the shortest path from the predecessor map
        ArrayList<Point2DCustom> path = new ArrayList<>();
        Point2DCustom current = end;
        while (current != null) {
            path.add(0, current);
            current = predecessorMap.get(current);
        }

        return path;
    }

/*
    public static ArrayList<Point2DCustom> dijkstra(ArrayList<Point2DCustom> allPoints, Point2DCustom startPoint, Graph graph) {
        ArrayList<Double> distances = new ArrayList<Double>();
        ArrayList<Point2DCustom> visitedVertices = new ArrayList<Point2DCustom>();
        ArrayList<Point2DCustom> previousVertices = new ArrayList<Point2DCustom>();
        PriorityQueue<queueElement> queue = new PriorityQueue<queueElement>();

        // Init all distances with infinity assuming that currently we can't reach
        // any of the vertices except start one.
        for (Point2DCustom point : allPoints){
            distances.set(allPoints.indexOf(point),Double.POSITIVE_INFINITY);
            previousVertices.set(allPoints.indexOf(point),null);
        }
        distances.set(allPoints.indexOf(startPoint),0.0);

        // Init vertices queue.
        queue.add(new queueElement(allPoints.indexOf(startPoint), distances.get(allPoints.indexOf(startPoint))));

        while (!queue.isEmpty()) {
            Point2DCustom currentVertex = allPoints.get(queue.poll().index);

            ArrayList<VisibleVertex> neighbors = graph.AdjList.get(currentVertex);
            for (VisibleVertex neighbor : neighbors){
                // Don't visit already visited vertices.
                if (!visitedVertices.contains(neighbor.vertex)) {
                    // Update distances to every neighbor from current vertex.

                    Double existingDistanceToNeighbor = distances.get(allPoints.indexOf(neighbor.vertex));
                    const distanceToNeighborFromCurrent = distances[currentVertex.number] + neighbor.distance;

                    if (distanceToNeighborFromCurrent < existingDistanceToNeighbor) {
                        distances[neighbor.otherVertex] = distanceToNeighborFromCurrent;

                        // Change priority.
                        if (queue.hasValue(neighbor.otherVertex)) {
                            queue.changePriority(neighbor.otherVertex, distances[neighbor.otherVertex]);
                        }

                        // Remember previous vertex.
                        previousVertices[neighbor.otherVertex] = currentVertex;
                    }

                    // Add neighbor to the queue for further visiting.
                    if (!queue.hasValue(neighbor.otherVertex)) {
                        queue.add(neighbor.otherVertex, distances[neighbor.otherVertex]);
                    }
                }
            }

            // Add current vertex to visited ones.
            visitedVertices[currentVertex.number] = currentVertex.number;
        }

        return {
                distances,
                previousVertices,
        };
    }*/
}

class DistanceComparator implements Comparator<VisibleVertex> {
    public int compare(VisibleVertex v1, VisibleVertex v2) {
        return Double.compare(v1.distance, v2.distance);
    }

}

class queueElement {
    public int index;
    public double distance;
    public queueElement(int index, double distance) {
        this.index = index;
        this.distance = distance;
    }

}
