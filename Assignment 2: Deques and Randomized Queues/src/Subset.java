import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

/**
 * Subset.java that takes a command-line integer k; reads in a sequence of N strings from standard input using
 * StdIn.readString(); and prints out exactly k of them, uniformly at random. Each item from the sequence can
 * be printed out at most once. You may assume that 0 ≤ k ≤ N, where N is the number of string on standard input.
 *
 * The running time of Subset must be linear in the size of the input. You may use only a constant amount of memory
 * plus either one Deque or RandomizedQueue object of maximum size at most N, where N is the number of strings on
 * standard input. (For an extra challenge, use only one Deque or RandomizedQueue object of maximum size at most k.)
 */

public class Subset {
    public static void main(String[] args)
    {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> randQueue = new RandomizedQueue<String>();
        while (!StdIn.isEmpty()) {
            randQueue.enqueue(StdIn.readString());
        }
        for (int i = 0; i < k; i++) {
            StdOut.println(randQueue.dequeue());
        }
    }
}
