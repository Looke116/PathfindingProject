package test;

import application.Point2DCustom;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class Point2DCustomTest {

    @Test
    public void testConstructor() {
        Point2DCustom point = new Point2DCustom(1.0, 2.0);
        assertEquals(1.0, point.getX(), 0.0001);
        assertEquals(2.0, point.getY(), 0.0001);
    }

    @Test
    public void testVisited() {
        Point2DCustom point = new Point2DCustom(1.0, 2.0);
        assertFalse(point.visited);
        point.visited = true;
        assertTrue(point.visited);
    }
}