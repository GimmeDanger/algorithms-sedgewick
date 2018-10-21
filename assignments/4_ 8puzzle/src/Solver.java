import edu.princeton.cs.algs4.Knuth;
import edu.princeton.cs.algs4.LinkedStack;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

public class Solver {
    private MinPQ<SearchNode> nodeMinPQ = null;
    private SearchNode goalNode = null;
    private boolean isSolvable = true;
    private int minMoves = -1;

    // This class is needed to pass API test for algo Princeton course, because it doesn`t allow to make a public method
    // isSolvable () in Board class. But the most effective way to find out solvability is to check the board invariant,
    // however prinston hint suggests to launch a Solver on a twin board...
    private class SolvabilityChecker {
        private Board board;
        private int[][] blocks;
        private int N;
        private boolean isSolvable;

        private SolvabilityChecker(Board initial) {
            board = initial;
            N = initial.dimension();
            blocks = new int[N][N];
            String boardStringForm = board.toString();
            String temp = "";

            char ch;
            int index = 0;
            while (boardStringForm.charAt(index) != 32)
                index++;

            int k = 0;
            int i_k, j_k;
            while (index < boardStringForm.length()) {
                ch = boardStringForm.charAt(index);
                if (ch != 10 && ch != 32) {
                    // '\n' or space
                    temp += ch;
                }
                else if ((ch == 10 || ch == 32) && !temp.isEmpty()) {
                    i_k = k / N;
                    j_k = k - i_k * N;
                    blocks[i_k][j_k] = Integer.parseInt(temp);
                    temp = "";
                    ++k;
                }
                ++index;
            }
            isSolvable = isSolvable();
        }

        public boolean getIsSolvable() {
            return isSolvable;
        }

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
    }

    private class SearchNode implements Comparable<SearchNode> {
        private Board board;
        private SearchNode prevNode = null;
        private int moves;                   // moves from initial node
        private int priority;                // priority for minPq implementation

        private SearchNode(Board curr, SearchNode prev) {
            board = curr;
            prevNode = prev;
            if (prev == null) moves = 0;
            else              moves = prevNode.moves + 1;
            priority = board.manhattan() + moves;
            // another priority function
            // priority = board.hamming() + moves;
        }

        public int getPriority() {
            return priority;
        }

        public int getMoves() {
            return moves;
        }

        public int compareTo(SearchNode that) {
            if (this.priority < that.priority) return -1;
            if (this.priority > that.priority) return  1;
            return 0;
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        SearchNode initialNode = new SearchNode(initial, null);
        SolvabilityChecker checker = new SolvabilityChecker(initial);
        isSolvable = checker.getIsSolvable();

        nodeMinPQ = new MinPQ<SearchNode>();
        nodeMinPQ.insert(initialNode);

        if (isSolvable) {
            SearchNode currNode;
            while (true) {
                currNode = nodeMinPQ.delMin();
                if (currNode.board.isGoal()) {
                    goalNode = currNode;
                    minMoves = currNode.getMoves();
                    break;
                }
                for (Board neighbor: currNode.board.neighbors()) {
                    if (currNode.prevNode == null)
                        nodeMinPQ.insert(new SearchNode(neighbor, currNode));
                    else if (!neighbor.equals(currNode.prevNode.board))
                        nodeMinPQ.insert(new SearchNode(neighbor, currNode));
                }
            }
        }
    }
    // is the initial board solvable?
    public boolean isSolvable() {
        return isSolvable;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        return minMoves;
    }

    private class SolutionIterator implements Iterator<Board> {
        private LinkedStack<Board> solution = null;
        public SolutionIterator() {
            if (isSolvable) {
                solution = new LinkedStack<Board>();
                SearchNode currNode = goalNode;
                while (currNode != null) {
                    solution.push(currNode.board);
                    currNode = currNode.prevNode;
                }
            }
        }
        public boolean hasNext() { return (solution.isEmpty() == false); }
        public void remove()     { throw new UnsupportedOperationException(); }
        public Board next()      { return solution.pop(); }
    }

    private class SolutionIterable implements Iterable<Board> {
        public Iterator<Board> iterator() {
            return new SolutionIterator();
        }
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        return new SolutionIterable();
    }


    public static void main(String[] args) {
        // create initial board from file
        /*
        In in = new In(args[0]);
        int N = in.readInt();
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = in.readInt();
        */

        // Random nPuzzle

        int N = 4;
        Integer[] array = new Integer[N * N];
        for (int i = 0; i < N * N; i++)
            array[i] = i;
        Knuth.shuffle(array);

        int k = 0;
        int[][] blocks = new int[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                blocks[i][j] = array[k++];


        Board initial = new Board(blocks);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}