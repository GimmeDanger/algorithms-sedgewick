import edu.princeton.cs.algs4.*;

// Brute-force implementation with O(N) time for range and nearest
public class PointSET {
    private SET<Point2D> set = null;

    // construct an empty set of points
    public PointSET() { set = new SET<Point2D>(); }

    public boolean isEmpty() { return set.isEmpty(); }

    public int size() { return set.size(); }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null)
            throw new java.lang.NullPointerException();
        set.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new java.lang.NullPointerException();
        return set.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : set)
            p.draw();
    }

    // all points that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new java.lang.NullPointerException();

        Stack<Point2D> stack = new Stack<Point2D>();

        for (Point2D p : set) {
            if (rect.contains(p))
                stack.push(p);
        }

        return stack;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (isEmpty()) return null;

        double dist;
        double minimalDist = Double.MAX_VALUE;
        Point2D nearestPoint = null;

        for (Point2D q : set) {
            dist = q.distanceSquaredTo(p);
            if (dist < minimalDist) {
                nearestPoint = q;
                minimalDist = dist;
            }
        }

        return nearestPoint;
    }

    public static void main(String[] args) {
        PointSET set = new PointSET();

        set.insert(new Point2D (0.1, 0.3));
        set.insert(new Point2D (0.5, 0.4));
        set.insert(new Point2D (0.6, 0.25));
        set.insert(new Point2D (0.4, 0.7));
        set.insert(new Point2D (0.9, 0.65));

        // Test 1
        StdOut.println(set.contains(new Point2D(0.1, 0.3)) ? "contains" : "does not contain");
        StdOut.println(set.contains(new Point2D(0.99, 0.65)) ? "contains" : "does not contain");

        // Test 2
        StdOut.println(set.nearest(new Point2D(0.5, 0.9)));
        StdOut.println(set.nearest(new Point2D(0.0, 0.0)));

        // Test 3
        StdOut.println(set.range(new RectHV(0.0, 0.49, 1.0, 1.0)));
        StdOut.println(set.range(new RectHV(0.0, 0.0, 1.0, 1.0)));
    }
}
