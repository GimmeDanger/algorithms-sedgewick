import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

/**
 * Class that performs outcast detection.
 * Given a list of WordNet nouns x1, x2, ..., xn, which noun is the least related to the others?
 * Outcast noun xt is noun that maximizes dt = distance(xt, x1) + ... distance(xt, xn)
 *
 * Here is a sample execution:
 * % more outcast5.txt
 * horse zebra cat bear table
 *
 * % more outcast8.txt
 * water soda bed orange_juice milk apple_juice tea coffee
 *
 * % more outcast11.txt
 * apple pear peach banana lime lemon blueberry strawberry mango watermelon potato
 *
 * % java-algs4 Outcast synsets.txt hypernyms.txt outcast5.txt outcast8.txt outcast11.txt
 * outcast5.txt: table
 * outcast8.txt: bed
 * outcast11.txt: potato
 */
public class Outcast {
    private final WordNet wordnet;

    /**
     * Constructor
     * @param wordnet datatype which represented a semantic lexicon for the English language
     */
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }

    /**
     * Returns outcast noun
     * @param nouns array of WordNet nouns
     * @return outcast noun
     */
    public String outcast(String[] nouns) {
        int[] distance = new int[nouns.length];
        for (int i = 0; i < nouns.length; i++) {
            for (int j = i + 1; j < nouns.length; j++) {
                int dist = wordnet.distance(nouns[i], nouns[j]);
                distance[i] += dist;
                distance[j] += dist;
            }
        }

        int maxInd = 0;
        int maxDist = 0;
        for (int i = 0; i < nouns.length; i++) {
            if (distance[i] > maxDist) {
                maxDist = distance[i];
                maxInd = i;
            }
        }
        return nouns[maxInd];
    }

    /**
     * Unit testing of this class
     */
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
