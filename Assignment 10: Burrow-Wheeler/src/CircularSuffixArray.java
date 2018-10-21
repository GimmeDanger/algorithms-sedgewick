import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

/**
 * Class describes the abstraction of a sorted array of the n circular suffixes of a string of length N.
 * As an example, consider the string "ABRACADABRA!" of length 12.
 * The table below shows its 12 circular suffixes and the result of sorting them.
 *
 *  i       Original Suffixes           Sorted Suffixes         index[i]
 * --    -----------------------     -----------------------    --------
 *  0    A B R A C A D A B R A !     ! A B R A C A D A B R A    11
 *  1    B R A C A D A B R A ! A     A ! A B R A C A D A B R    10
 *  2    R A C A D A B R A ! A B     A B R A ! A B R A C A D    7
 *  3    A C A D A B R A ! A B R     A B R A C A D A B R A !    0
 *  4    C A D A B R A ! A B R A     A C A D A B R A ! A B R    3
 *  5    A D A B R A ! A B R A C     A D A B R A ! A B R A C    5
 *  6    D A B R A ! A B R A C A     B R A ! A B R A C A D A    8
 *  7    A B R A ! A B R A C A D     B R A C A D A B R A ! A    1
 *  8    B R A ! A B R A C A D A     C A D A B R A ! A B R A    4
 *  9    R A ! A B R A C A D A B     D A B R A ! A B R A C A    6
 * 10    A ! A B R A C A D A B R     R A ! A B R A C A D A B    9
 * 11    ! A B R A C A D A B R A     R A C A D A B R A ! A B    2
 *
 * Constructor takes: O* (1.39 * W * N * lgR) or O(1.39 * N * lgN) in the worst case (based on 3-way radix qsort)
 * Extra space: 2*N = N for the input string and N for 'index'.
 */
public class CircularSuffixArray {

    // cutoff to insertion sort
    private static final int CUTOFF =  15;

    private final String s;
    private final int length;
    private final int[] index;

    // circular suffix array of s
    public CircularSuffixArray(String s) {

        if (s == null) throw new IllegalArgumentException();

        this.s = s;
        this.length = s.length();
        this.index = new int[this.length];

        for (int i = 0; i < length; i++) index[i] = i;
        StdRandom.shuffle(index);
        sort(0, length-1, 0);
    }

    private int charAt(int i, int d) {
        assert d >= 0 && d <= length;
        if (d == length) return -1;
        return s.charAt((index[i] + d) % length);
    }

    // exchange index[i] and index[j]
    private void exch(int i, int j) {
        int temp = index[i];
        index[i] = index[j];
        index[j] = temp;
    }

    // 3-way string quicksort [lo..hi] starting at dth character
    private void sort(int lo, int hi, int d) {

        // cutoff to insertion sort for small subarrays
        if (hi <= lo + CUTOFF) {
            insertion(lo, hi, d);
            return;
        }

        int lt = lo, gt = hi;
        int v = charAt(lo, d);
        int i = lo + 1;
        while (i <= gt) {
            int t = charAt(i, d);
            if      (t < v) exch(lt++, i++);
            else if (t > v) exch(i, gt--);
            else            i++;
        }

        // a[lo..lt-1] < v = a[lt..gt] < a[gt+1..hi].
        sort(lo, lt-1, d);
        if (v >= 0) sort(lt, gt, d+1);
        sort(gt+1, hi, d);
    }

    // sort from [lo] to [hi], starting at the d-th character
    private void insertion(int lo, int hi, int d) {
        for (int i = lo; i <= hi; i++)
            for (int j = i; j > lo && less(j, j-1, d); j--)
                exch(j, j-1);
    }

    // is v less than w, starting at character d
    private boolean less(int i, int j, int d) {
        for (int dd = d; dd < length; dd++) {
            if (charAt(i, dd) < charAt(j, dd)) return true;
            if (charAt(i, dd) > charAt(j, dd)) return false;
        }
        return false;
    }

    // length of s
    public int length() {
        return s.length();
    }

    // returns index of i-th sorted suffix
    public int index(int i) {
        if (!(i >= 0 && i < length)) throw new IllegalArgumentException();
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {

        String s = args[0];
        CircularSuffixArray circularSuffix = new CircularSuffixArray(s);

        final int maxVisualizationLength = 15;
        if (s.length() < maxVisualizationLength) {
            String[] suffixes = new String[s.length()];
            for (int i = 0; i < s.length(); i++) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int d = 0; d < s.length(); d++) {
                    int index = (d + i) % s.length();
                    stringBuilder.append(s.charAt(index));
                }
                suffixes[i] = stringBuilder.toString();
            }

            StdOut.println("i | Original Suffixes:");
            StdOut.println("----------------------------");
            for (int i = 0; i < s.length(); i++)
                StdOut.printf("%d %s \n", i, suffixes[i]);
            StdOut.println();

            StdOut.println("i | Sorted Suffixes | index[i]");
            StdOut.println("----------------------------");
            for (int i = 0; i < s.length(); i++)
                StdOut.printf("%d %s %d \n", i, suffixes[circularSuffix.index[i]], circularSuffix.index[i]);
        }
        else {
            StdOut.println("Input string is too long! Its length should be shorter that 15.");
        }
    }
}
