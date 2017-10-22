import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdDraw;

public class KdTree {
    private static class KdTreeNode {
        private KdTreeNode left;
        private KdTreeNode right;
        private final boolean isVertical;
        private final double x;
        private final double y;

        public KdTreeNode(final KdTreeNode left, final KdTreeNode right, final boolean isVertical,
                          final double x, final double y) {
            this.left = left;
            this.right = right;
            this.isVertical = isVertical;
            this.x = x;
            this.y = y;
        }
    }

    private static final RectHV initialRect = new RectHV(0, 0, 1, 1);
    private KdTreeNode root;
    private int size;

    // construct an empty set of points
    public KdTree() {
        root = null;
        size = 0;
    }

    public boolean isEmpty() {
        return (size == 0);
    }

    public int size() {
        return size;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(final Point2D p) {
        if (p == null)
            throw new java.lang.NullPointerException();
        root = insert(root, p, true);
    }

    private KdTreeNode insert(final KdTreeNode node, final Point2D p, final boolean isVertical) {
        // create a new node if argument node is null
        if (node == null) {
            ++size;
            return new KdTreeNode(null, null, isVertical, p.x(), p.y());
        }

        // return argument node if this point has already existed
        if (node.x == p.x() && node.y == p.y())
            return node;

        // insert new node due to its vertical/horizontal orientation
        if (node.isVertical && p.x() < node.x || !node.isVertical && p.y() < node.y) {
            node.left = insert(node.left, p, !node.isVertical);
        } else {
            node.right = insert(node.right, p, !node.isVertical);
        }
        return node;
    }

    // does the set contain point p?
    public boolean contains(final Point2D p) {
        if (p == null)
            throw new java.lang.NullPointerException();
        return contains(root, p);
    }

    private boolean contains(final KdTreeNode node, final Point2D p) {
        if (node == null)
            return false;
        if (node.x == p.x() && node.y == p.y()) {
            return true;
        }
        if (node.isVertical && p.x() < node.x || !node.isVertical && p.y() < node.y) {
            return contains(node.left, p);
        } else {
            return contains(node.right, p);
        }
    }

    // draw all points to standard draw
    public void draw() {
        StdDraw.setScale(0, 1);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius();
        initialRect.draw();
        draw(root, initialRect);
    }

    private void draw(final KdTreeNode node, final RectHV rect) {
        if (node == null)
            return;

        StdDraw.setPenRadius(0.01);

        // draw a point
        StdDraw.setPenColor(StdDraw.BLACK);
        new Point2D(node.x, node.y).draw();

        Point2D min, max;
        if (node.isVertical) {
            StdDraw.setPenColor(StdDraw.RED);
            min = new Point2D(node.x, rect.ymin());
            max = new Point2D(node.x, rect.ymax());
        } else {
            StdDraw.setPenColor(StdDraw.BLUE);
            min = new Point2D(rect.xmin(), node.y);
            max = new Point2D(rect.xmax(), node.y);
        }

        // draw that division line
        StdDraw.setPenRadius();
        min.drawTo(max);

        // recursively draw children
        draw(node.left, leftRect(rect, node));
        draw(node.right, rightRect(rect, node));
    }

    private RectHV leftRect(final RectHV rect, final KdTreeNode node) {
        if (node.isVertical) {
            return new RectHV(rect.xmin(), rect.ymin(), node.x, rect.ymax());
        } else {
            return new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.y);
        }
    }

    private RectHV rightRect(final RectHV rect, final KdTreeNode node) {
        if (node.isVertical) {
            return new RectHV(node.x, rect.ymin(), rect.xmax(), rect.ymax());
        } else {
            return new RectHV(rect.xmin(), node.y, rect.xmax(), rect.ymax());
        }
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(final RectHV rect) {
        final Stack<Point2D> stack = new Stack<Point2D>();
        range(root, initialRect, rect, stack);
        return stack;
    }

    private void range(final KdTreeNode node, final RectHV nrect, final RectHV rect, final Stack<Point2D> stack) {
        if (node == null)
            return;

        if (rect.intersects(nrect)) {
            final Point2D p = new Point2D(node.x, node.y);
            if (rect.contains(p))
                stack.push(p);
            range(node.left, leftRect(nrect, node), rect, stack);
            range(node.right, rightRect(nrect, node), rect, stack);
        }
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(final Point2D p) {
        return nearest(root, initialRect, p, null);
    }

    private Point2D nearest(final KdTreeNode node, final RectHV rect, final Point2D p, final Point2D candidate) {
        if (node == null){
            return candidate;
        }

        double dqn = 0.0;
        double drq = 0.0;
        RectHV left = null;
        RectHV right = null;
        final Point2D query = new Point2D(p.x(), p.y());
        Point2D nearest = candidate;

        if (nearest != null) {
            dqn = query.distanceSquaredTo(nearest);
            drq = rect.distanceSquaredTo(query);
        }

        if (nearest == null || dqn > drq) {
            final Point2D point = new Point2D(node.x, node.y);
            if (nearest == null || dqn > query.distanceSquaredTo(point))
                nearest = point;

            if (node.isVertical) {
                left = new RectHV(rect.xmin(), rect.ymin(), node.x, rect.ymax());
                right = new RectHV(node.x, rect.ymin(), rect.xmax(), rect.ymax());

                if (p.x() < node.x) {
                    nearest = nearest(node.left, left, p, nearest);
                    nearest = nearest(node.right, right, p, nearest);
                } else {
                    nearest = nearest(node.right, right, p, nearest);
                    nearest = nearest(node.left, left, p, nearest);
                }
            } else {
                left = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), node.y);
                right = new RectHV(rect.xmin(), node.y, rect.xmax(), rect.ymax());

                if (p.y() < node.y) {
                    nearest = nearest(node.left, left, p, nearest);
                    nearest = nearest(node.right, right, p, nearest);
                } else {
                    nearest = nearest(node.right, right, p, nearest);
                    nearest = nearest(node.left, left, p, nearest);
                }
            }
        }

        return nearest;
    }
}
