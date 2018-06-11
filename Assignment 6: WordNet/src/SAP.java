import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;

/**
 * Shortest ancestral path (SAP) class represents a data type for finding shortest ancestral path and a common ancestor
 * associated with this path in a given digraph G = {V, E}.
 *
 * Class uses extra space proportional to (V + E), all its methods take time proportional to (V + E).
 *
 * Some definitions:
 * An ancestral path between two vertices v and w in a digraph is a directed path from v to a common ancestor x,
 * together with a directed path from w to the same ancestor x. A shortest ancestral path is an ancestral path of
 * minimum total length. We refer to the common ancestor in a shortest ancestral path as a shortest common ancestor.
 * Note also that an ancestral path is a path, but not a directed path.
 */
public class SAP {
    private static final int INFINITY = Integer.MAX_VALUE;
    private static final int FIND_SAP_RESULT_SIZE = 2;
    private final Digraph G;

    /**
     * SAP constructor
     * @param G a digraph (not necessarily a DAG)
     */
    public SAP(Digraph G) {
        this.G = new Digraph(G);
    }

    /**
     * Finds shortest ancestral path and a common ancestor associated with this path for two vertexes
     * @param v a vertex
     * @param w a vertex
     * @throws IllegalArgumentException unless {0 <= v < G.V() && 0 <= w < G.V()}
     * @return array of size 2, where the first element is length and the second is common ancestor
     */
    private int[] findSAP(int v, int w) {
        BreadthFirstDirectedPaths dpv = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths dpw = new BreadthFirstDirectedPaths(G, w);
        return handleBfsDirectPaths(dpv, dpw);
    }

    /**
     * Finds shortest ancestral path and a common ancestor associated with this path for two sets of vertexes
     * @param v a set of vertexes
     * @param w a set of vertexes
     * @throws IllegalArgumentException unless {0 <= v[i] < G.V() && 0 <= w[i] < G.V()} or v, w are not null
     * @return array of size 2, where the first element is length and the second is common ancestor
     */
    private int[] findSAP(Iterable<Integer> v, Iterable<Integer> w) {
        BreadthFirstDirectedPaths dpv = new BreadthFirstDirectedPaths(G, v);
        BreadthFirstDirectedPaths dpw = new BreadthFirstDirectedPaths(G, w);
        return handleBfsDirectPaths(dpv, dpw);
    }

    /**
     * Handles bfs direct paths from v and w to find SAP
     * @param dpv direct paths from v
     * @param dpw direct paths from w
     * @return array of size 2, where the first element is length and the second is common ancestor
     */
    private int[] handleBfsDirectPaths(BreadthFirstDirectedPaths dpv, BreadthFirstDirectedPaths dpw) {
        int commonAncestor = -1;
        int shortestLength = INFINITY;
        for (int s = 0; s < G.V(); s++) {
            if (dpv.hasPathTo(s) && dpw.hasPathTo(s)) {
                int currentLength = dpv.distTo(s) + dpw.distTo(s);
                if (currentLength < shortestLength) {
                    shortestLength = currentLength;
                    commonAncestor = s;
                }
            }
        }
        // if there is no required path, return {-1. -1}
        if (commonAncestor == -1)
            shortestLength = -1;
        int[] res = new int[FIND_SAP_RESULT_SIZE];
        res[0] = shortestLength;
        res[1] = commonAncestor;
        return res;
    }

    /**
     * Finds a length of shortest ancestral path between v and w
     * @param v a vertex
     * @param w a vertex
     * @return length of shortest ancestral path between v and w; -1 if no such path
     */
    public int length(int v, int w) {
        int[] s = findSAP(v, w);
        return s[0];
    }

    /**
     * Finds a common ancestor of v and w that participates in a shortest ancestral path
     * @param v a vertex
     * @param w a vertex
     * @return a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
     */
    public int ancestor(int v, int w) {
        int[] s = findSAP(v, w);
        return s[1];
    }

    /**
     * Finds a length of shortest ancestral path between any vertex in v and any vertex in w
     * @param v a set of vertexes
     * @param w a set of vertexes
     * @return length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        int[] s = findSAP(v, w);
        return s[0];
    }

    /**
     * Finds a common ancestor of set v and set w that participates in a shortest ancestral path
     * @param v a set of vertexes
     * @param w a set of vertexes
     * @return a common ancestor of set v and set w that participates in a shortest ancestral path; -1 if no such path
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        int[] s = findSAP(v, w);
        return s[1];
    }

    /**
     * Unit testing of this class
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
