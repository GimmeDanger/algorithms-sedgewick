import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

public class FastCollinearPoints {
    private final ResizingArrayStack<Point> stack = new ResizingArrayStack<Point>();
    private LineSegment[] segmentsArray = null;

    /**
     * Finds all line segments containing 4 points.
     * Corner cases. Throw a java.lang.NullPointerException either the argument to the constructor is null or if any point
     * in the array is null. Throw a java.lang.IllegalArgumentException if the argument to the constructor contains a
     * repeated point.
     */
    public FastCollinearPoints(Point[] points) {
        if (points == null) throw new NullPointerException("The argument to the constructor is null.");
        int n = points.length;

        int err = -1;
        for (Point p : points)
            if (p != null)
                err++;
        if (err < 0) throw new NullPointerException("All points in the argument array are null.");

        // Sorting point by coordinate
        Arrays.sort(points);

        Point[] slopeSorted = new Point[n];

        for (int i = 0; i < n - 1; i++) {
            for (int j = i; j < n; j++)
                slopeSorted[j] = points[j];

            Arrays.sort(slopeSorted, i + 1, n, points[i].slopeOrder());
            Arrays.sort(slopeSorted, 0, i, points[i].slopeOrder());

            // repeated point in the argument array check ////////////
            if (points[i].compareTo(slopeSorted[i + 1]) == 0)
                throw new IllegalArgumentException("Argument array contains a repeated point: " + points[i].toString() + ".");
            if (i >= n - 3) continue;
            //////////////////////////////////////////////////////////

            int beg = i + 1;
            int end = i + 2;
            int prv = 0;
            while (end < n) {
                double begSlope = slopeSorted[i].slopeTo(slopeSorted[beg]);
                while (end < n && begSlope == slopeSorted[i].slopeTo(slopeSorted[end]))
                    end++;
                if (end - beg >= 3) {
                    // check for overlapping segment
                    double prvSlope = Double.NEGATIVE_INFINITY;
                    while (prv < i) {
                        prvSlope = slopeSorted[i].slopeTo(slopeSorted[prv]);
                        if (prvSlope < begSlope) prv++;
                        else                     break;
                    }
                    if (prvSlope != begSlope) {
                        stack.push(slopeSorted[i]);
                        stack.push(slopeSorted[end - 1]);
                    }
                }
                beg = end;
                end = end + 1;
            }
        }

        segmentsArray = new LineSegment[stack.size() / 2];
        for (int i = 0; i < segmentsArray.length; i++)
            segmentsArray[i] = new LineSegment(stack.pop(), stack.pop());
    }

    public int numberOfSegments() {
        return segmentsArray.length;
    }
    public LineSegment[] segments() {
        return segmentsArray;
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In("collinear-testing/equidistant.txt");
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
    }
}
