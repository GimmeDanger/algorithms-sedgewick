/******************************************************************************
 *
 *  An set for [A-Z] alphabet strings, implemented using a 26-way trie.
 *
 *****************************************************************************/

import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.Iterator;

/**
 *  The {@code BoggleTrieSET} class represents an ordered set of strings over
 *  the [A-Z] alphabet. It supports the usual <em>add</em>, <em>contains</em>,
 *  and <em>delete</em> methods. It also provides character-based methods
 *  for finding the string in the set that is the <em>longest prefix</em>
 *  of a given prefix, finding all strings in the set that <em>start with</em>
 *  a given prefix, and finding all strings in the set that <em>match</em> a given pattern.
 *  <p>
 *  This implementation uses a 26-way trie for string in alphabet [A-Z]
 *  The <em>add</em>, <em>contains</em>, <em>delete</em>, and
 *  <em>longest prefix</em> methods take time proportional to the length
 *  of the key (in the worst case). Construction takes constant time.
 *  <p>
 *
 *  It is based on TrieSET implementation from algs4.jar library
 *  created by Robert Sedgewick and Kevin Wayne. It is modified
 *  under the terms of the GNU General Public License.
 *
 *  @author Vladimir Nazarov
 */
public class BoggleTrieSET implements Iterable<String> {
    private static final int R = 26; // [A-Z] strings
    private static final char OFFSET = 'A';

    private Node root;      // root of trie
    private int n;          // number of keys in trie

    // R-way trie node
    private static class Node {
        private Node[] next = new Node[R];
        private boolean isString;
    }

    /**
     * Initializes an empty set of strings.
     */
    public BoggleTrieSET() {
    }

    /**
     * Does the set contain the given key?
     * @param key the key
     * @return {@code true} if the set contains {@code key} and
     *     {@code false} otherwise
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public boolean contains(String key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        Node x = get(root, key, 0);
        if (x == null) return false;
        return x.isString;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = (char) (key.charAt(d) - OFFSET);
        return get(x.next[c], key, d+1);
    }

    /**
     * Adds the key to the set if it is not already present.
     * @param key the key to add
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void add(String key) {
        if (key == null) throw new IllegalArgumentException("argument to add() is null");
        root = add(root, key, 0);
    }

    private Node add(Node x, String key, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            if (!x.isString) n++;
            x.isString = true;
        }
        else {
            char c = (char) (key.charAt(d) - OFFSET);
            x.next[c] = add(x.next[c], key, d+1);
        }
        return x;
    }

    /**
     * Returns the number of strings in the set.
     * @return the number of strings in the set
     */
    public int size() {
        return n;
    }

    /**
     * Is the set empty?
     * @return {@code true} if the set is empty, and {@code false} otherwise
     */
    public boolean isEmpty() {
        return size() == 0;
    }

    /**
     * Returns all of the keys in the set, as an iterator.
     * To iterate over all of the keys in a set named {@code set}, use the
     * foreach notation: {@code for (Key key : set)}.
     * @return an iterator to all of the keys in the set
     */
    public Iterator<String> iterator() {
        return keysWithPrefix("").iterator();
    }

    /**
     * Returns all of the keys in the set that start with {@code prefix}.
     * @param prefix the prefix
     * @return all of the keys in the set that start with {@code prefix},
     *     as an iterable
     */
    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> results = new Queue<String>();
        Node x = get(root, prefix, 0);
        collect(x, new StringBuilder(prefix), results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, Queue<String> results) {
        if (x == null) return;
        if (x.isString) results.enqueue(prefix.toString());
        for (int c = 0; c < R; c++) {
            prefix.append((char) (c + OFFSET));
            collect(x.next[c], prefix, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    /**
     * Does the set contain a word with the given prefix?
     * @param prefix the prefix
     * @return true if the set contains a word with the given prefix, false otherwise
     * @throws IllegalArgumentException if {@code prefix} is {@code null}
     */
    public boolean hasKeysWithPrefix(String prefix) {
        if (prefix == null) throw new IllegalArgumentException("argument to contains() is null");
        return (get(root, prefix, 0) != null);
    }

    /**
     * Removes the key from the set if the key is present.
     * @param key the key
     * @throws IllegalArgumentException if {@code key} is {@code null}
     */
    public void delete(String key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) {
            if (x.isString) n--;
            x.isString = false;
        }
        else {
            char c = (char) (key.charAt(d) - OFFSET);
            x.next[c] = delete(x.next[c], key, d+1);
        }

        // remove subtrie rooted at x if it is completely empty
        if (x.isString) return x;
        for (int c = 0; c < R; c++)
            if (x.next[c] != null)
                return x;
        return null;
    }


    /**
     * Unit tests the {@code BoggleTrieSET} data type.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        BoggleTrieSET set = new BoggleTrieSET();
        while (!StdIn.isEmpty()) {
            String key = StdIn.readString();
            set.add(key);
        }

        // print results
        if (set.size() < 100) {
            StdOut.println("keys(\"\"):");
            for (String key : set) {
                StdOut.println(key);
            }
            StdOut.println();
        }

        StdOut.println("keysWithPrefix(\"shor\"):");
        for (String s : set.keysWithPrefix("shor"))
            StdOut.println(s);
        StdOut.println();

        StdOut.println("keysWithPrefix(\"shortening\"):");
        for (String s : set.keysWithPrefix("shortening"))
            StdOut.println(s);
        StdOut.println();
    }
}