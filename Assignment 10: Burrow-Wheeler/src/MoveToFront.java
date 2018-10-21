import edu.princeton.cs.algs4.BinaryStdOut;
import edu.princeton.cs.algs4.BinaryStdIn;

public class MoveToFront {

    private static final int R = 256;

    public MoveToFront() { }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {
        int[] indexToChar = createSequence();
        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();
            int ind = moveToFrontEncode(indexToChar, (int) ch);
            BinaryStdOut.write(ind, 8);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {
        int[] charToIndex = createSequence();
        while (!BinaryStdIn.isEmpty()) {
            int ind = BinaryStdIn.readInt(8);
            char ch = (char) moveToFrontDecode(charToIndex, ind);
            BinaryStdOut.write(ch, 8);
        }
        BinaryStdOut.close();
    }

    // create an initial array for encoding/decoding
    private static int[] createSequence() {
        int[] sequence = new int[R];
        for (int i = 0; i < R; i++) {
            sequence[i] = i;
        }
        return sequence;
    }

    // search for the position of character 'c' (call it j)
    // and move all characters with positions i < j to the right
    private static int moveToFrontEncode(int[] encoding, int c) {
        int prev = c;
        for (int j = 0; j < R; j++) {
            int curr = encoding[j];
            encoding[j] = prev;
            if (curr == c) {
                return j;
            }
            prev = curr;
        }
        return -1;
    }

    // search for the position of index 'i' (call it j)
    // and move all indexes with positions j < i to the right
    private static int moveToFrontDecode(int[] encoding, int i) {
        int prev = encoding[i];
        for (int j = 0; j < R; j++) {
            int curr = encoding[j];
            encoding[j] = prev;
            if (j == i) {
                return curr;
            }
            prev = curr;
        }
        return -1;
    }

    // if args[0] is '-', apply move-to-front encoding
    // if args[0] is '+', apply move-to-front decoding
    public static void main(String[] args) {
        if (args.length != 0) {
            if (args[0].equals("-"))
                encode();
            else if (args[0].equals("+"))
                decode();
        }
    }
}