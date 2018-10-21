import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.ResizingArrayQueue;

import java.util.ArrayList;

// Burrows–Wheeler data compression algorithm.
public final class BurrowsWheeler {

    private static final int R = 256;

    public BurrowsWheeler() { }

    // apply Burrows-Wheeler transform, reading from standard input and writing to standard output
    public static void transform() {
        if (BinaryStdIn.isEmpty())
            return;

        String s = BinaryStdIn.readString();
        CircularSuffixArray suffix = new CircularSuffixArray(s);

        int len = suffix.length();
        for (int i = 0; i < len; i++) {
            if (suffix.index(i) == 0) {
                BinaryStdOut.write(i, 32);
                break;
            }
        }

        for (int i = 0; i < len; i++) {
            char ch = s.charAt(((len - 1) + suffix.index(i)) % len);
            BinaryStdOut.write(ch, 8);
        }

        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
    public static void inverseTransform() {
        if (BinaryStdIn.isEmpty())
            return;

        // Stage 1: read 'first'
        int fst = BinaryStdIn.readInt(32);

        // Stage 2: read all input chars t[] and sort them into tSorted[] by counting sort.
        // Save original t-index of char in tSearch for fast searching in Stage 3.
        // Notice that size is unknown in advance & both arrays are required to store for inverse BTW.
        ArrayList<Character> t = new ArrayList<>();
        ArrayList<ResizingArrayQueue<Integer>> cnt = new ArrayList<>(R);
        for (int i = 0; i < R; i++) cnt.add(i, new ResizingArrayQueue<>());
        while (!BinaryStdIn.isEmpty()) {
            int charInd = BinaryStdIn.readInt(8);
            cnt.get(charInd).enqueue(t.size());
            t.add((char) charInd);
        }
        int pos = 0;
        int[] tSearch = new int[t.size()];
        char[] tSorted = new char[t.size()];
        for (int i = 0; i < R; i++) {
            int cntSize = cnt.get(i).size();
            if (cntSize == 0) continue;
            for (int j = 0; j < cntSize; j++) {

                tSorted[pos + j] = (char) i;
                tSearch[pos + j] = cnt.get(i).dequeue();
            }
            pos += cntSize;
        }

        // Stage 3: decoding
        int currInd = fst;
        for (int i = 0; i < t.size(); i++) {
            BinaryStdOut.write(tSorted[currInd], 8);
            currInd = tSearch[currInd];
        }

        BinaryStdOut.close();
    }

    // if args[0] is '-', apply Burrows-Wheeler transform
    // if args[0] is '+', apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args.length != 0) {
            if (args[0].equals("-"))
                transform();
            else if (args[0].equals("+"))
                inverseTransform();
        }
    }
}
