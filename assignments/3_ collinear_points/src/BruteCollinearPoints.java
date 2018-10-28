/**
 * Brute force algorithm that examines 4 points at a time and checks whether they all lie on the same line segment,
 * returning all such line segments. To check whether the 4 points p, q, r, and s are collinear, check whether
 * the three slopes between p and q, between p and r, and between p and s are all equal.
 *
 * Performance requirement. The order of growth of the running time of your program should be N^4 in the worst case and
 * it should use space proportional to n plus the number of line segments returned.
 */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

// import edu.princeton.cs.algs4.StdDraw;

public class BruteCollinearPoints {

    private final LineSegment[] segmentsArray;

    /**
     * Finds all line segments containing 4 points.
     * Corner cases. Throw a java.lang.NullPointerException either the argument to the constructor is null or if any point
     * in the array is null. Throw a java.lang.IllegalArgumentException if the argument to the constructor contains a
     * repeated point.
     */
    public BruteCollinearPoints(Point[] points) {

        if (points == null)
            throw new IllegalArgumentException("The argument to the constructor is null.");
        for (Point p : points)
            if (p == null)
                throw new IllegalArgumentException("Arg array contains null a null point.");
        for (int i = 0; i < points.length; i++)
            for (int j = i + 1; j < points.length; j++)
                if (points[i].compareTo(points[j]) == 0)
                    throw new IllegalArgumentException("Arg array contains a repeated point: " + points[i].toString() + ".");

        // Sorting point by coordinate
        Point[] pointsSorted = points.clone();
        Arrays.sort(pointsSorted);

        int n = pointsSorted.length;
        ResizingArrayStack<LineSegment> stack = new ResizingArrayStack<>();
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    for (int s = k + 1; s < n; s++) {
                        double slope1 = pointsSorted[i].slopeTo(pointsSorted[j]);
                        double slope2 = pointsSorted[j].slopeTo(pointsSorted[k]);
                        double slope3 = pointsSorted[k].slopeTo(pointsSorted[s]);
                        if (Double.compare(slope1, slope2) == 0 && Double.compare(slope2, slope3) == 0)
                            stack.push(new LineSegment(pointsSorted[i], pointsSorted[s]));
                    }
                }
            }
        }

        // There are no equal line segments in stack after previous loop because BruteCollinearPoints input
        // is not supposed to have 5 or more collinear points due to the assignment comment.
        segmentsArray = new LineSegment[stack.size()];
        for (int i = 0; i < segmentsArray.length; i++)
            segmentsArray[i] = stack.pop();
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
        int n = numberOfSegments();
        LineSegment[] temp = new LineSegment[n];
        for (int i = 0; i < n; i++) {
            temp[i] = segmentsArray[i];
        }
        return temp;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points: for dbg purposes
        /*
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();
        */

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            // segment.draw();
        }
        // StdDraw.show();
    }
}
