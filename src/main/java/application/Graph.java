package application;

import java.util.*;
public class Graph {
    protected int noOfVertices;
    protected Map<Point2DCustom, ArrayList<Hashtable<Point2DCustom, Double>>> AdjList;

    public Graph(int noOfVertices) {
        this.noOfVertices = noOfVertices;
        AdjList = new Map<Point2DCustom, ArrayList<Hashtable<Point2DCustom, Double>>>() {
            @Override
            public int size() {
                return 0;
            }

            @Override
            public boolean isEmpty() {
                return false;
            }

            @Override
            public boolean containsKey(Object key) {
                return false;
            }

            @Override
            public boolean containsValue(Object value) {
                return false;
            }

            @Override
            public ArrayList<Hashtable<Point2DCustom, Double>> get(Object key) {
                return null;
            }

            @Override
            public ArrayList<Hashtable<Point2DCustom, Double>> put(Point2DCustom key, ArrayList<Hashtable<Point2DCustom, Double>> value) {
                return null;
            }

            @Override
            public ArrayList<Hashtable<Point2DCustom, Double>> remove(Object key) {
                return null;
            }

            @Override
            public void putAll(Map<? extends Point2DCustom, ? extends ArrayList<Hashtable<Point2DCustom, Double>>> m) {

            }

            @Override
            public void clear() {

            }

            @Override
            public Set<Point2DCustom> keySet() {
                return null;
            }

            @Override
            public Collection<ArrayList<Hashtable<Point2DCustom, Double>>> values() {
                return null;
            }

            @Override
            public Set<Entry<Point2DCustom, ArrayList<Hashtable<Point2DCustom, Double>>>> entrySet() {
                return null;
            }
        };
    }
    public void addVertex(Point2DCustom v){
        this.AdjList.put(v,new ArrayList<Hashtable<Point2DCustom, Double>>());
    }
    public void addEdge(Point2DCustom src,Hashtable<Point2DCustom, Double> dest){
        this.AdjList.get(src).add(dest);
    }

}
