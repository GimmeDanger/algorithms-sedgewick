import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
// import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.ResizingArrayStack;
import java.util.Arrays;


public class FastCollinearPoints {

    private static final int MIN_POINTS_PER_SEGMENT = 4;
    private static final int MIN_EXTRA_POINTS_PER_SEGMENT = 3;
    private final LineSegment[] segmentsArray;

    /**
     * Finds all line segments containing 4 points.
     * Corner cases. Throw a java.lang.IllegalArgumentException either the argument to the constructor is null or if any point
     * in the array is null. Throw a java.lang.IllegalArgumentException if the argument to the constructor contains a
     * repeated point.
     */
    public FastCollinearPoints(Point[] points) {

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
        Point[] slopeSorted = new Point[n];
        ResizingArrayStack<LineSegment> stack = new ResizingArrayStack<>();
        for (int i = 0; i <= n - MIN_POINTS_PER_SEGMENT; i++) {

            // initialize current slope array with unused points
            int slopeSortedSize = 0;
            for (int j = i + 1; j < n; j++) {
                Point q = pointsSorted[j];
                //if (!used.contains(q))
                slopeSorted[slopeSortedSize++] = q;
            }
            if (slopeSortedSize < MIN_EXTRA_POINTS_PER_SEGMENT)
                continue;

            // sort by slope with respect to p
            Point p = pointsSorted[i];
            Arrays.sort(slopeSorted, 0, slopeSortedSize, p.slopeOrder());

            // find continuous segment of size MIN_EXTRA_POINTS_PER_SEGMENT
            int lo = 0;
            int hi = lo + 1;
            boolean segFound = false;
            double loSlope = p.slopeTo(slopeSorted[lo]);
            while (hi < slopeSortedSize) {
                double hiSlope = p.slopeTo(slopeSorted[hi]);
                if (Double.compare(loSlope, hiSlope) != 0) {
                    if (segFound) {
                        stack.push(new LineSegment(p, slopeSorted[hi-1]));
                    }
                    lo = hi;
                    segFound = false;
                    loSlope = hiSlope;
                }
                if (hi + 1 - lo >= MIN_EXTRA_POINTS_PER_SEGMENT)
                    segFound = true;
                hi++;
            }
            if (segFound) {
                stack.push(new LineSegment(p, slopeSorted[hi-1]));
            }
        }

        // There are no equal line segments in stack after previous loop because BruteCollinearPoints input
        // is not supposed to have 5 or more collinear points due to the assignment comment.
        segmentsArray = new LineSegment[stack.size()];
        for (int i = 0; i < segmentsArray.length; i++)
            segmentsArray[i] = stack.pop();
    }

    public int numberOfSegments() {
        return segmentsArray.length;
    }

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

        // draw the points
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            // segment.draw();
        }
        // StdDraw.show();
    }
}
