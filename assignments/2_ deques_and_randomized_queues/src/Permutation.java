import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Permutation takes a command-line integer k; reads in a sequence of strings from standard input
 * using StdIn.readString(); and prints exactly k of them, uniformly at random. Print each item
 * from the sequence at most once. You may assume that 0 ≤ k ≤ n, where n is the number of string
 * on standard input.
 *
 * The running time of Permutation must be linear in the size of the input. You may use only a constant
 * amount of memory plus either one Deque or RandomizedQueue object of maximum size at most n. (For an
 * extra challenge, use only one Deque or RandomizedQueue object of maximum size at most k.)
 */

public class Permutation {
    public static void main(String[] args)  {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> randQueue = new RandomizedQueue<String>();
        while (!edu.princeton.cs.algs4.StdIn.isEmpty()) {
            randQueue.enqueue(StdIn.readString());
        }
        for (int i = 0; i < k; i++) {
            StdOut.println(randQueue.dequeue());
        }

    }
}
