/**
 * Brute force algorithm that examines 4 points at a time and checks whether they all lie on the same line segment,
 * returning all such line segments. To check whether the 4 points p, q, r, and s are collinear, check whether
 * the three slopes between p and q, between p and r, and between p and s are all equal.
 *
 * Performance requirement. The order of growth of the running time of your program should be N4 in the worst case and
 * it should use space proportional to n plus the number of line segments returned.
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

public class BruteCollinearPoints {
    /**
     *  Stack of points. If 4 points p, q, r, s are collinear, push p and s to the stack.
     *  After all points in array Point[] points are checked for collinearity. Pop two points and add new line segment,
     *  while stack is not empty.
     */
    private static double slopeEpsilon = 10e-10;
    private final ResizingArrayStack<Point> stack = new ResizingArrayStack<Point>();
    private LineSegment[] segmentsArray = null;

    /**
     * Finds all line segments containing 4 points.
     * Corner cases. Throw a java.lang.NullPointerException either the argument to the constructor is null or if any point
     * in the array is null. Throw a java.lang.IllegalArgumentException if the argument to the constructor contains a
     * repeated point.
     */
    public BruteCollinearPoints(Point[] points) {
        if (points == null) throw new NullPointerException("The argument to the constructor is null.");
        int n = points.length;

        int err = -1;
        for (Point p : points)
            if (p != null)
                err++;
        if (err < 0) throw new NullPointerException("All points in the argument array are null.");

        double slope1, slope2, slope3;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (j == i) continue;
                if (points[i].compareTo(points[j]) == 0)
                    throw new IllegalArgumentException("Arg array contains a repeated point: " + points[i].toString() + ".");
                if (points[j].compareTo(points[i]) != 1) continue;
                for (int k = 0; k < n; k++) {
                    if (k == i || k == j || points[k].compareTo(points[j]) != 1) continue;
                    for (int l = 0; l < n; l++) {
                        if (l == i || l == j || l == k || points[l].compareTo(points[k]) != 1) continue;
                        slope1 = points[i].slopeTo(points[j]);
                        slope2 = points[j].slopeTo(points[k]);
                        slope3 = points[k].slopeTo(points[l]);
                        if ((slope1 == Double.POSITIVE_INFINITY && slope2 == Double.POSITIVE_INFINITY &&
                                slope3 == Double.POSITIVE_INFINITY) ||
                                Math.abs(slope1 - slope2) < slopeEpsilon && Math.abs(slope2 - slope3) < slopeEpsilon) {
                            // StdOut.println(i + " " + j + " " + k + " " + " " + l);
                            stack.push(points[i]);
                            stack.push(points[l]);
                        }
                    }
                }
            }
        }

        // There are no equal line segments in stack after previous loop because BruteCollinearPoints input
        // is not supposed to have 5 or more collinear points due to the assignment comment.
        segmentsArray = new LineSegment[stack.size() / 2];
        for (int i = 0; i < segmentsArray.length; i++)
            segmentsArray[i] = new LineSegment(stack.pop(), stack.pop());
    }

    /**
     * @return the number of line segments
     */
    public int numberOfSegments() {
        return segmentsArray.length;
    }

    /**
     * The method segments() should include each line segment containing 4 points exactly once. If 4 points appear on a line
     * segment in the order p→q→r→s, then you should include either the line segment p→s or s→p (but not both) and you should
     * not include subsegments such as p→r or q→r. For simplicity, we will not supply any input to BruteCollinearPoints that
     * has 5 or more collinear points.
     */
    public LineSegment[] segments() {
        return segmentsArray;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In("collinear-testing/input40.txt");
        // In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
    }
}
