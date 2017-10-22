import edu.princeton.cs.algs4.Knuth;
import edu.princeton.cs.algs4.LinkedStack;
import edu.princeton.cs.algs4.StdOut;
import java.util.Iterator;

public class Board {
    private int[][] blocks = null;
    private int N = 0;              // dimension of puzzle
    private int zeroI = 0;          // i-th index of space block
    private int zeroJ = 0;          // j-th index of space block

    // construct a board from an N-by-N array of block (where blocks[i][j] = block in row i, column j)
    public Board(int[][] blocks) {
        if (blocks == null)
            throw new java.lang.NullPointerException("Initial array pointer that constructor received is null");
        if (blocks.length != blocks[0].length)
            throw new java.lang.IllegalArgumentException("Initial array that constructor received has diff dimensions");
        // For simplicity it might be assumed that the constructor receives an N-by-N array containing the N^2 integers
        // between 0 and N^2 − 1, where 0 represents the blank square. So input integers won`t be checked for equality.
        N = blocks.length;
        if (N < 1)
            throw new java.lang.IllegalArgumentException("Initial array dimension N is smaller that 1");
        this.blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                if (blocks[i][j] == 0) {
                    zeroI = i;
                    zeroJ = j;
                }
                this.blocks[i][j] = blocks[i][j];
            }
    }

    // board dimension N
    public int dimension() {
        return N;
    }

    // number of blocks out of place
    public int hamming() {
        int ans = 0;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (blocks[i][j] != 0 && blocks[i][j] != (i * N + j + 1) % (N * N))
                    ans++;
        return ans;
    }

    // sum of Manhattan distances between blocks and goal
    public int manhattan() {
        int blockValue, igoal, jgoal, ans = 0;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                blockValue = blocks[i][j];
                if (blockValue == 0) continue;
                if (blockValue != (i * N + j + 1) % (N * N)) {
                    igoal = (blockValue - 1) / N;
                    jgoal = (blockValue - 1) - igoal * N;
                    ans += Math.abs(igoal - i) + Math.abs(jgoal - j);
                }
            }
        return ans;
    }

    // is this board the goal board?
    public boolean isGoal() {
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (blocks[i][j] != (i * N + j + 1) % (N * N))
                    return false;
        return true;
    }

    // a board that is obtained by exchanging any pair of blocks
    public Board twin() {
        if (N < 2) return new Board(blocks);
        int[][] twn = new int[N][N];
        for (int i = 0; i < N; i++)
             for (int j = 0; j < N; j++)
                 twn[i][j] = blocks[i][j];
        int temp;
        if (twn[0][0] != 0) {
            if (twn[0][1] != 0) {
                temp = twn[0][0];
                twn[0][0] = twn[0][1];
                twn[0][1] = temp;
            }
            else {
                temp = twn[0][0];
                twn[0][0] = twn[1][0];
                twn[1][0] = temp;
            }
        }
        else {
            if (twn[N-1][N-2] != 0) {
                temp = twn[N-1][N-1];
                twn[N-1][N-1] = twn[N-1][N-2];
                twn[N-1][N-2] = temp;
            }
            else {
                temp = twn[N-1][N-1];
                twn[N-1][N-1] = twn[N-2][N-1];
                twn[N-2][N-1] = temp;
            }
        }
        return new Board(twn);
    }

    // does this board equal y?
    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return  false;

        Board that = (Board) y;
        if (this.N != that.N) return false;
        for (int i = 0; i < this.N; i++)
            for (int j = 0; j < this.N; j++)
                if (this.blocks[i][j] != that.blocks[i][j])
                    return false;
        return true;
    }

    private class NeighborIterator implements Iterator<Board> {
        private LinkedStack<Board> neighbors;

        public NeighborIterator() {
            neighbors = new LinkedStack<Board>();
            if (zeroI > 0) {
                Board leftNeighbor = new Board(blocks);
                leftNeighbor.blocks[zeroI][zeroJ] = leftNeighbor.blocks[zeroI - 1][zeroJ];
                leftNeighbor.blocks[zeroI - 1][zeroJ] = 0;
                --leftNeighbor.zeroI;
                neighbors.push(leftNeighbor);
            }
            if (zeroI < N - 1) {
                Board rightNeighbor = new Board(blocks);
                rightNeighbor.blocks[zeroI][zeroJ] = rightNeighbor.blocks[zeroI + 1][zeroJ];
                rightNeighbor.blocks[zeroI + 1][zeroJ] = 0;
                ++rightNeighbor.zeroI;
                neighbors.push(rightNeighbor);
            }
            if (zeroJ > 0) {
                Board upperNeighbor = new Board(blocks);
                upperNeighbor.blocks[zeroI][zeroJ] = upperNeighbor.blocks[zeroI][zeroJ - 1];
                upperNeighbor.blocks[zeroI][zeroJ - 1] = 0;
                --upperNeighbor.zeroJ;
                neighbors.push(upperNeighbor);
            }
            if (zeroJ < N - 1) {
                Board downNeighbor = new Board(blocks);
                downNeighbor.blocks[zeroI][zeroJ] = downNeighbor.blocks[zeroI][zeroJ + 1];
                downNeighbor.blocks[zeroI][zeroJ + 1] = 0;
                ++downNeighbor.zeroJ;
                neighbors.push(downNeighbor);
            }
        }

        public boolean hasNext() { return (neighbors.isEmpty() == false); }
        public void remove()     { throw new UnsupportedOperationException(); }
        public Board next()      { return neighbors.pop(); }
    }

    private class NeighborIterable implements Iterable<Board> {
        public Iterator<Board> iterator() {
            return new NeighborIterator();
        }
    }

    public Iterable<Board> neighbors() {
        return new NeighborIterable();
    }

    /* cannot make it public to pass Princeton API test :( */
    private boolean isSolvable() {
        int i_k, j_k;
        int i_l, j_l;
        int inv = 0;
        for (int k = 0; k < N * N; ++k) {
            i_k = k / N;
            j_k = k - i_k * N;
            if (blocks[i_k][j_k] == 0)
                continue;
            for (int l = 0; l < k; ++l) {
                i_l = l / N;
                j_l = l - i_l * N;
                if (blocks[i_l][j_l] > blocks[i_k][j_k])
                    ++inv;
            }
        }

        if (N % 2 == 0) {
            for (int k = 0; k < N * N; ++k) {
                i_k = k / N;
                j_k = k - i_k * N;
                if (blocks[i_k][j_k] == 0)
                    inv += 1 + i_k;
            }
        }

        return (inv % 2 == 0);
    }

    // string representation of this board
    public String toString() {
        String ret = new String();
        ret += N +  "\n";
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                ret += (" " + blocks[i][j]);
                if (j == N - 1) {
                    if (i != N - 1)
                        ret += "\n";
                }
            }
        ret += "\n";
        return ret;
    }

    public static void main(String[] args) {
        int N = 3;
        Integer[] array = new Integer[N * N];
        for (int i = 0; i < N * N; i++)
            array[i] = i;
        Knuth.shuffle(array);

        int k = 0;
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = array[k++];

        Board b = new Board(blocks);
        StdOut.println("Random board:\n" + b + "\nhamming : " + b.hamming() + "\nmanhattan: " + b.manhattan() + "\n");
        Board twin = b.twin();
        StdOut.println("Twin board:\n" + twin + "\nhamming : " + twin.hamming() + "\nmanhattan: " + twin.manhattan());
    }
}