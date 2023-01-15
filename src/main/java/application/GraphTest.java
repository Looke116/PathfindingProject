package application;

import application.Graph;
import application.Point2DCustom;
import application.VisibleVertex;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;


public class GraphTest {

    @Test
    public void testAddEdge() {
        Graph graph = new Graph(1);
        Point2DCustom src = new Point2DCustom(0, 0);
        VisibleVertex dest = new VisibleVertex(new Point2DCustom(1.0,2.0),3.0);
        graph.addEdge(src, dest);

        ArrayList<VisibleVertex> expected = new ArrayList<VisibleVertex>();
        expected.add(dest);

        ArrayList<VisibleVertex> actual = graph.AdjList.get(src);

        assertEquals(expected, actual);
    }
}