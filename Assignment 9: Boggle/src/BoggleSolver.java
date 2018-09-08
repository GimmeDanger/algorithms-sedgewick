import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.PatriciaSET;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

/**
 * TODO: complete final class description
 */
public class BoggleSolver
{
    private final PatriciaSET dictionarySet;

    /**
     * Initializes the data structure using the given array of strings as the dictionary.
     * Each word is assumed to contain only the uppercase letters A through Z.)
     * @param dictionary
     */
    public BoggleSolver(String[] dictionary) {
        dictionarySet = new PatriciaSET();
        for (String str : dictionary)
            dictionarySet.add(str);
    }

    /**
     * Construct string representation of BoogleBoard
     * @param board
     * @return String which represents a given BoogleBoard
     */
    private String boardToString(BoggleBoard board) {
        int m = board.rows();
        int n = board.cols();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(board.getLetter(i, j));
            }
        }
        return sb.toString();
    }

    /**
     * Validate a vertex in coordinate representation. Used in constructAdj.
     * @return true if vertex (i, j) is valid, false otherwise
     */
    private boolean validateCoords(int m, int n, int i, int j) {
        return (i >= 0 && i < m && j >= 0 && j < n);
    }

    /**
     * Returns vertex index in dense enumeration from 0 to rows * cols - 1. Used in constructAdj.
     * @return vertex index in dense enumeration from 0 to rows * cols - 1
     */
    private int coordsToVertex(int n, int i, int j) {
        return i * n + j;
    }

    /**
     * Constracts iterable bag of adjacent vertices for a given BoogleBoard.
     * @param board
     * @return iterable bag of adjacent vertices
     */
    private ArrayList<Bag<Integer>> constructAdj(BoggleBoard board) {
        int m = board.rows();
        int n = board.cols();
        int verticesNum = m * n;
        ArrayList<Bag<Integer>> adj = new ArrayList<Bag<Integer>>();
        for (int v = 0; v < verticesNum; v++) {
            adj.add(new Bag<Integer>());
        }
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                int v = i * n + j;
                if (validateCoords(m, n, i - 1, j - 1))
                    adj.get(v).add (coordsToVertex(n, i - 1, j - 1));
                if (validateCoords(m, n, i - 1, j))
                    adj.get(v).add (coordsToVertex(n,i - 1, j));
                if (validateCoords(m, n, i - 1, j + 1))
                    adj.get(v).add (coordsToVertex(n,i - 1, j + 1));
                if (validateCoords(m, n, i, j + 1))
                    adj.get(v).add (coordsToVertex(n, i, j + 1));
                if (validateCoords(m, n, i, j - 1))
                    adj.get(v).add (coordsToVertex(n, i, j - 1));
                if (validateCoords(m, n, i + 1, j - 1))
                    adj.get(v).add (coordsToVertex(n, i + 1, j - 1));
                if (validateCoords(m, n, i + 1, j))
                    adj.get(v).add (coordsToVertex(n, i + 1, j));
                if (validateCoords(m, n, i + 1, j + 1))
                    adj.get(v).add (coordsToVertex(n, i + 1, j + 1));
            }
        }
        return adj;
    }

    /**
     * Check if any word in dictionarySet has a given prefix, this is a critical backtracking optimization
     * @param prefix
     * @return true if there is a word in dictionarySet with a given prefix
     */
    private boolean prefixExists(String prefix) {
        return true; //< TODO
    }

    /**
     * Dfs-style recursive algorithm which finds all valid words in BoogleBoard.
     * Word is considered to be valid if it is contained in dictionarySet and represented as simple valid path in Boogle graph
     * @param boardWordSet set of all valid words in given BoogleBoard which have been already founded
     * @param board string representation of BoogleBoard for better performance
     * @param adj iterable bag of adjacent vertices
     * @param path current path
     * @param marked boolean array for dfs
     * @param s source vertex
     */
    private void dfs(PatriciaSET boardWordSet, String board, ArrayList<Bag<Integer>> adj,
                     StringBuilder path, boolean[] marked, int s) {
        marked[s] = true;
        char ch = board.charAt(s);
        path.append(ch);
        if (ch == 'Q')
            path.append('U');

        String prefix = path.toString();
        if (prefixExists(prefix)) {
            for (int w : adj.get(s)) {
                if (!marked[w])
                    dfs(boardWordSet, board, adj, path, marked, w);
            }
            // StdOut.println("Prefix = " + prefix);
            if (scoreOf(prefix) > 0)
                boardWordSet.add(prefix);
        }

        marked[s] = false;
        if (path.length() > 1 && path.charAt(path.length()-1) == 'U' && path.charAt(path.length()-2) == 'Q')
            path.deleteCharAt(path.length()-1);
        path.deleteCharAt(path.length()-1);
    }

    /**
     * Returns the set of all valid words in the given Boggle board, as an Iterable.
     * It uses dfs-style algorithm to find all simple paths in BoogleBoard.
     * @param board of type BoogleBoard
     * @return the set of all valid words in the given Boggle board, as an Iterable.
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        // Resulting iterable object
        PatriciaSET boardWordSet = new PatriciaSET();

        // Explicit Boggle graph data
        int verticesNum = board.rows() * board.cols();
        boolean[] marked = new boolean[verticesNum];
        StringBuilder path = new StringBuilder(); //< used for O(1)* concatenation
        ArrayList<Bag<Integer>> adj = constructAdj(board); //< precomputed for better performance

        // String representation of BoggleBoard for performance
        String boardString = boardToString(board);

        // find and add to result all valide simple paths starting from board[i][j], where (i * n + j) == s
        for (int s = 0; s < verticesNum; s++) {
            dfs(boardWordSet, boardString, adj, path, marked, s);
            assert (path.length() == 0);
        }

        return boardWordSet;
    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise.
     * Word is assumed to contain only the uppercase letters A through Z.)
     * @param word
     * @return the score of the given word if it is in the dictionary, zero otherwise.
     */
    public int scoreOf(String word) {
        if (!dictionarySet.contains(word))
            return 0;
        switch (word.length()) {
            case 0: case 1: case 2:
                return 0;
            case 3: case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default:
                return 11;
        }
    }

    /**
     * Unit testing
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}