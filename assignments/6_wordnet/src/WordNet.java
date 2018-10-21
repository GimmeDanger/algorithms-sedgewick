import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;

import java.util.ArrayList;

/**
 *  WordNet class datatype which represented a semantic lexicon for the English language
 *  This datatype uses space linear in the input size (size of synsets and hypernyms files)
 *  The constructor takes time linear in the input size.
 *  The method isNoun() runs in time logarithmic in the number of nouns.
 *  The methods distance() and sap() run in time linear in the size of the WordNet digraph.
 */
public class WordNet {

    private final Digraph G; // digraph of WordNet synsets id
    private final SAP sap; // sap for distance and common ancestor methods
    private final SET<Noun> nounSet; // set of WordNet nouns for fast searching
    private final ArrayList<String> idList; // idList[synset_id] == synset

    /**
     *  Auxiliary Noun data type which stores string representation of noun and list of synset ids which contain noun
     *  Noun is comparable to be used in SET for fast searching
     */
    private class Noun implements Comparable<Noun> {
        private final String noun;
        private final ArrayList<Integer> id = new ArrayList<>();

        public Noun(String word) {
            noun = word;
        }

        public void addId(int x) {
            id.add(x);
        }

        public String toString() {
            return noun;
        }

        public ArrayList<Integer> getId() {
            return id;
        }

        @Override
        public int compareTo(Noun that) {
            return this.noun.compareTo(that.noun);
        }
    }

    /**
     * Constructor
     * @param synsets name of input file of synsets
     * @param hypernyms name of inpute file of hypernyms
     */
    public WordNet(String synsets, String hypernyms) {
        // synsets and hypernums are checked for being null inside In constructors
        In inSynsets = new In(synsets);
        In inHypernyms = new In(hypernyms);

        // Initialize idList and nounSet
        idList = new ArrayList<String>();
        nounSet = new SET<Noun>();

        // Parse inSynsets to construct nounSet, idList and find total number of vertexes in WordNet graph
        int idTotalNumber = 0;
        String line = inSynsets.readLine();
        while (line != null) {
            String[] synsetLine = line.split(","); //< split current line to get id and synset
            int synsetId = Integer.parseInt(synsetLine[0]); //< parse out id from synsetLine[0]
            String[] synsetNouns = synsetLine[1].split(" "); //< split synsetLine[1] to get array of synset nouns
            for (String nounName : synsetNouns) { //< iterate through synset nouns and add it to nounSet if needed
                Noun noun = new Noun(nounName);
                if (nounSet.contains(noun)) {
                    noun = nounSet.ceiling(noun);
                } else {
                    nounSet.add(noun);
                }
                noun.addId(synsetId);
            }
            idTotalNumber++;
            idList.add(synsetLine[1]);
            line = inSynsets.readLine();
        }

        // Initialize graph of Wordnet
        G = new Digraph(idTotalNumber);

        // The vertex is root iff it out degree is 0
        boolean[] notRoot = new boolean[idTotalNumber];

        // Parse inHypernyms to construct edges of G
        line = inHypernyms.readLine();
        while (line != null) {
            String[] hypernymsLine = line.split(","); //< split current line to get directed edges from synset_id
            int synsetId = Integer.parseInt(hypernymsLine[0]);
            for (int i = 1; i < hypernymsLine.length; i++) { //< add all adjacent edges to G
                int ancestorId = Integer.parseInt(hypernymsLine[i]);
                G.addEdge(synsetId, ancestorId);
            }
            if (hypernymsLine.length > 1)
                notRoot[synsetId] = true; //< synset is not a root, some synset may be seen more than once
            line = inHypernyms.readLine();
        }

        // find total number of roots
        int rootTotal = 0;
        for (int i = 0; i < idTotalNumber; i++)
            if (!notRoot[i])
                rootTotal++;

        // test G for being a rooted DAG
        validateConstructedGraph(rootTotal);

        // Initialize sap
        sap = new SAP(G);
    }

    /**
     * Validate the constructed graph is a rooted DAG
     * @param rootsTotal number of roots in G
     * Throw an IllegalArgumentException unless {rootsTotal == 1 && G has no cycle}
     */
    private void validateConstructedGraph(int rootsTotal) {
        DirectedCycle directedCycle = new DirectedCycle(G);
        if (directedCycle.hasCycle() || rootsTotal != 1) {
            throw new java.lang.IllegalArgumentException();
        }
    }

    /**
     * Validate the word is a WordNet noun
     * @param word an input string
     * @return Noun representation of the word
     * Throw an IllegalArgumentException unless {word is a WordNet noun}
     */
    private Noun validateNoun(String word) {
        if (!isNoun(word))
            throw new java.lang.IllegalArgumentException();
        return new Noun(word);
    }

    /**
     * Check if the given word is a WordNet noun
     * @param word a string that should be checked
     * @return true if the given word is a WordNet noun
     */
    public boolean isNoun(String word) {
        if (word == null)
            throw new java.lang.IllegalArgumentException();
        Noun noun = new Noun(word);
        return nounSet.contains(noun);
    }

    /**
     * All WordNet nouns (without duplicates)
     * @return all WordNet nouns as Iterable
     */
    public Iterable<String> nouns() {
        Queue<String> q = new Queue<>();
        for (Noun noun : nounSet)
            q.enqueue(noun.toString());
        return q;
    }

    /**
     * Finds distance in a shortest ancestral path between nounA and nounB in WordNet
     * @param nounA a string
     * @param nounB a string
     * @return distance in a shortest ancestral path between nounA and nounB in WordNet
     */
    public int distance(String nounA, String nounB) {
        Noun a = nounSet.ceiling(validateNoun(nounA));
        Noun b = nounSet.ceiling(validateNoun(nounB));
        return sap.length(a.getId(), b.getId());
    }

    /**
     * Finds a synset that is the common ancestor of nounA and nounB in a shortest ancestral path
     * @param nounA a string
     * @param nounB a string
     * @return a synset that is the common ancestor of nounA and nounB in a shortest ancestral path
     */
    public String sap(String nounA, String nounB) {
        Noun a = nounSet.ceiling(validateNoun(nounA));
        Noun b = nounSet.ceiling(validateNoun(nounB));
        return idList.get(sap.ancestor(a.getId(), b.getId()));
    }

    /**
     * Unit testing of this class
     */
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
        while (!StdIn.isEmpty()) {
            String nounA = StdIn.readString();
            String nounB = StdIn.readString();
            int length   = wn.distance(nounA, nounB);
            String ancestor = wn.sap(nounA, nounB);
            StdOut.printf("length = %d, ancestor = %s\n", length, ancestor);
        }
    }
}
