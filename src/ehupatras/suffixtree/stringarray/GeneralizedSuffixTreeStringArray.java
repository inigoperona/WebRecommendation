package ehupatras.suffixtree.stringarray;

import java.util.Collection;
import java.util.Collections;

/**
 * A Generalized Suffix Tree, based on the Ukkonen's paper "On-line construction of suffix trees"
 * http://www.cs.helsinki.fi/u/ukkonen/SuffixT1withFigs.pdf
 *
 * Allows for fast storage and fast(er) retrieval by creating a tree-based index out of a set of strings.
 * Unlike common suffix trees, which are generally used to build an index out of one (very) long string,
 * a Generalized Suffix Tree can be used to build an index over many strings.
 *
 * Its main operations are put and search:
 * Put adds the given key to the index, allowing for later retrieval of the given value.
 * Search can be used to retrieve the set of all the values that were put in the index with keys that contain a given input.
 *
 * In particular, after put(K, V), search(H) will return a set containing V for any string H that is substring of K.
 *
 * The overall complexity of the retrieval operation (search) is O(m) where m is the length of the string to search within the index.
 *
 * Although the implementation is based on the original design by Ukkonen, there are a few aspects where it differs significantly.
 * 
 * The tree is composed of a set of nodes and labeled edges. The labels on the edges can have any length as long as it's greater than 0.
 * The only constraint is that no two edges going out from the same node will start with the same character.
 * 
 * Because of this, a given (startNode, stringSuffix) pair can denote a unique path within the tree, and it is the path (if any) that can be
 * composed by sequentially traversing all the edges (e1, e2, ...) starting from startNode such that (e1.label + e2.label + ...) is equal
 * to the stringSuffix.
 * See the search method for details.
 * 
 * The union of all the edge labels from the root to a given leaf node denotes the set of the strings explicitly contained within the GST.
 * In addition to those Strings, there are a set of different strings that are implicitly contained within the GST, and it is composed of
 * the strings built by concatenating e1.label + e2.label + ... + $end, where e1, e2, ... is a proper path and $end is prefix of any of
 * the labels of the edges starting from the last node of the path.
 *
 * This kind of "implicit path" is important in the testAndSplit method.
 *  
 */
public class GeneralizedSuffixTreeStringArray {

    /**
     * The index of the last item that was added to the GST
     */
    protected int last = 0;
    /**
     * The root of the suffix tree
     */
    protected final Node root = new Node();
    /**
     * The last leaf that was added during the update operation
     */
    protected Node activeLeaf = root;

    /**
     * Searches for the given word within the GST.
     *
     * Returns all the indexes for which the key contains the <tt>word</tt> that was
     * supplied as input.
     *
     * @param word the key to search for
     * @return the collection of indexes associated with the input <tt>word</tt>
     */
    public Collection<Integer> search(StringArray word) {
        return search(word, -1);
    }

    /**
     * Searches for the given word within the GST and returns at most the given number of matches.
     *
     * @param word the key to search for
     * @param results the max number of results to return
     * @return at most <tt>results</tt> values for the given word
     */
    public Collection<Integer> search(StringArray word, int results) {
        Node tmpNode = searchNode(word);
        if (tmpNode == null) {
            return null;
        }
        return tmpNode.getData(results);
    }

    /**
     * Searches for the given word within the GST and returns at most the given number of matches.
     *
     * @param word the key to search for
     * @param to the max number of results to return
     * @return at most <tt>results</tt> values for the given word
     * @see GeneralizedSuffixTreeStringArray#ResultInfo
     */
    public ResultInfo searchWithCount(StringArray word, int to) {
        Node tmpNode = searchNode(word);
        if (tmpNode == null) {
            return new ResultInfo(Collections.EMPTY_LIST, 0);
        }

        return new ResultInfo(tmpNode.getData(to), tmpNode.getResultCount());
    }

    /**
     * Returns the tree node (if present) that corresponds to the given string.
     */
    private Node searchNode(StringArray word) {
        /*
         * Verifies if exists a path from the root to a node such that the concatenation
         * of all the labels on the path is a superstring of the given word.
         * If such a path is found, the last node on it is returned.
         */
        Node currentNode = root;
        Edge currentEdge;

        for (int i = 0; i < word.length(); ++i) {
            String ch = word.charAt(i);
            // follow the edge corresponding to this char
            currentEdge = currentNode.getEdge(ch);
            if (null == currentEdge) {
                // there is no edge starting with this char
                return null;
            } else {
                StringArray label = currentEdge.getLabel();
                int lenToMatch = Math.min(word.length() - i, label.length());
                if (!word.regionMatches(i, label, 0, lenToMatch)) {
                    // the label on the edge does not correspond to the one in the string to search
                    return null;
                }

                if (label.length() >= word.length() - i) {
                    return currentEdge.getDest();
                } else {
                    // advance to next node
                    currentNode = currentEdge.getDest();
                    i += lenToMatch - 1;
                }
            }
        }

        return null;
    }

    /**
     * Adds the specified <tt>index</tt> to the GST under the given <tt>key</tt>.
     *
     * Entries must be inserted so that their indexes are in non-decreasing order,
     * otherwise an IllegalStateException will be raised.
     *
     * @param key the string key that will be added to the index
     * @param index the value that will be added to the index
     * @throws IllegalStateException if an invalid index is passed as input
     */
    public void put(StringArray key, int index) throws IllegalStateException {
        if (index < last) {
            throw new IllegalStateException("The input index must not be less than any of the previously inserted ones. Got " + index + ", expected at least " + last);
        } else {
            last = index;
        }

        // reset activeLeaf
        activeLeaf = root;

        StringArray remainder = key;
        Node s = root;

        // proceed with tree construction (closely related to procedure in
        // Ukkonen's paper)
        StringArray text = new StringArray();
        // iterate over the string, one char at a time
        for (int i = 0; i < remainder.length(); i++) {	
            // line 6
            text = text.concat(remainder.charAt(i));

            // line 7: update the tree with the new transitions due to this new char
            Pair<Node, StringArray> active = update(s, text, remainder.substring(i), index);
            // line 8: make sure the active pair is canonical
            active = canonize(active.getFirst(), active.getSecond());
            
            s = active.getFirst();
            text = active.getSecond();
        }

        // add leaf suffix link, is necessary
        if (null == activeLeaf.getSuffix() && activeLeaf != root && activeLeaf != s) {
            activeLeaf.setSuffix(s);
        }

    }
    public void put(String[] key, int index) throws IllegalStateException {
    	StringArray keyarray = new StringArray(key);
    	this.put(keyarray, index);
    }

    /**
     * Tests whether the string stringPart + t is contained in the subtree that has inputs as root.
     * If that's not the case, and there exists a path of edges e1, e2, ... such that
     *     e1.label + e2.label + ... + $end = stringPart
     * and there is an edge g such that
     *     g.label = stringPart + rest
     * 
     * Then g will be split in two different edges, one having $end as label, and the other one
     * having rest as label.
     *
     * @param inputs the starting node
     * @param stringPart the string to search
     * @param t the following character
     * @param remainder the remainder of the string to add to the index
     * @param value the value to add to the index
     * @return a pair containing
     *                  true/false depending on whether (stringPart + t) is contained in the subtree starting in inputs
     *                  the last node that can be reached by following the path denoted by stringPart starting from inputs
     *         
     */
    private Pair<Boolean, Node> testAndSplit(final Node inputs, final StringArray stringPart, final String t, 
    															final StringArray remainder, final int value) {
        // descend the tree as far as possible
        Pair<Node, StringArray> ret = canonize(inputs, stringPart);
        Node s = ret.getFirst();
        StringArray str = ret.getSecond();

        if (str.length()>0) {
            Edge g = s.getEdge(str.charAt(0));

            StringArray label = g!=null ? g.getLabel() : (new StringArray());
            // must see whether "str" is substring of the label of an edge
            if (label.length() > str.length() && label.charAt(str.length()).equals(t)) {
                return new Pair<Boolean, Node>(true, s);
            } else {
                // need to split the edge
                StringArray newlabel = label.substring(str.length());
                assert (label.startsWith(str));

                // build a new node
                Node r = new Node();
                // build a new edge
                Edge newedge = new Edge(str, r);

                g.setLabel(newlabel);

                // link s -> r
                r.addEdge(newlabel.charAt(0), g);
                s.addEdge(str.charAt(0), newedge);

                return new Pair<Boolean, Node>(false, r);
            }

        } else {
            Edge e = s.getEdge(t);
            if (null == e) {
                // if there is no t-transtion from s
                return new Pair<Boolean, Node>(false, s);
            } else {
                if (remainder.equals(e.getLabel())) {
                    // update payload of destination node
                    e.getDest().addRef(value);
                    return new Pair<Boolean, Node>(true, s);
                } else if (remainder.startsWith(e.getLabel())) {
                    return new Pair<Boolean, Node>(true, s);
                } else if (e.getLabel().startsWith(remainder)) {
                    // need to split as above
                    Node newNode = new Node();
                    newNode.addRef(value);

                    Edge newEdge = new Edge(remainder, newNode);

                    e.setLabel(e.getLabel().substring(remainder.length()));

                    newNode.addEdge(e.getLabel().charAt(0), e);

                    s.addEdge(t, newEdge);

                    return new Pair<Boolean, Node>(false, s);
                } else {
                    // they are different words. No prefix. but they may still share some common substr
                    return new Pair<Boolean, Node>(true, s);
                }
            }
        }

    }

    /**
     * Return a (Node, String) (n, remainder) pair such that n is a farthest descendant of
     * s (the input node) that can be reached by following a path of edges denoting
     * a prefix of inputstr and remainder will be string that must be
     * appended to the concatenation of labels from s to n to get inpustr.
     */
    protected Pair<Node, StringArray> canonize(final Node s, final StringArray inputstr) {

        if (inputstr.length()==0) {
            return new Pair<Node, StringArray>(s, inputstr);
        } else {
            Node currentNode = s;
            StringArray str = inputstr;
            Edge g = s.getEdge(str.charAt(0));
            // descend the tree as long as a proper label is found
            while (g != null && str.startsWith(g.getLabel())) {
                str = str.substring(g.getLabel().length());
                currentNode = g.getDest();
                if (str.length() > 0) {
                    g = currentNode.getEdge(str.charAt(0));
                }
            }

            return new Pair<Node, StringArray>(currentNode, str);
        }
    }

    /**
     * Updates the tree starting from inputNode and by adding stringPart.
     * 
     * Returns a reference (Node, String) pair for the string that has been added so far.
     * This means:
     * - the Node will be the Node that can be reached by the longest path string (S1)
     *   that can be obtained by concatenating consecutive edges in the tree and
     *   that is a substring of the string added so far to the tree.
     * - the String will be the remainder that must be added to S1 to get the string
     *   added so far.
     * 
     * @param inputNode the node to start from
     * @param stringPart the string to add to the tree
     * @param rest the rest of the string
     * @param value the value to add to the index
     */
    protected Pair<Node, StringArray> update(final Node inputNode, final StringArray stringPart, final StringArray rest, final int value) {
        Node s = inputNode;
        StringArray tempstr = stringPart;
        String newChar = stringPart.charAt(stringPart.length() - 1);

        // line 1
        Node oldroot = root;

        // line 1b
        Pair<Boolean, Node> ret = testAndSplit(s, tempstr.substring(0, tempstr.length() - 1), newChar, rest, value);

        Node r = ret.getSecond();
        boolean endpoint = ret.getFirst();

        Node leaf;
        // line 2
        while (!endpoint) {
            // line 3
            Edge tempEdge = r.getEdge(newChar);
            if (null != tempEdge) {
                // such a node is already present. This is one of the main differences from Ukkonen's case:
                // the tree can contain deeper nodes at this stage because different strings were added by previous iterations.
                leaf = tempEdge.getDest();
            } else {
                // must build a new leaf
                leaf = new Node();
                leaf.addRef(value);
                Edge newedge = new Edge(rest, leaf);
                r.addEdge(newChar, newedge);
            }

            // update suffix link for newly created leaf
            if (activeLeaf != root) {
                activeLeaf.setSuffix(leaf);
            }
            activeLeaf = leaf;

            // line 4
            if (oldroot != root) {
                oldroot.setSuffix(r);
            }

            // line 5
            oldroot = r;

            // line 6
            if (null == s.getSuffix()) { // root node
                assert (root == s);
                // this is a special case to handle what is referred to as node _|_ on the paper
                tempstr = tempstr.substring(1);
            } else {
                Pair<Node, StringArray> canret = canonize(s.getSuffix(), safeCutLastChar(tempstr));
                s = canret.getFirst();
                // use intern to ensure that tempstr is a reference from the string pool
                StringArray temstr2 = (canret.getSecond()).concat( tempstr.charAt(tempstr.length()-1) );
                tempstr = tempstr.concat(temstr2);
            }

            // line 7
            ret = testAndSplit(s, safeCutLastChar(tempstr), newChar, rest, value);
            r = ret.getSecond();
            endpoint = ret.getFirst();

        }

        // line 8
        if (oldroot != root) {
            oldroot.setSuffix(r);
        }
        oldroot = root;

        return new Pair<Node, StringArray>(s, tempstr);
    }

    Node getRoot() {
        return root;
    }

    private StringArray safeCutLastChar(StringArray seq) {
        if (seq.length() == 0) {
            return new StringArray();
        }
        return seq.substring(0, seq.length() - 1);
    }

    public int computeCount() {
        return root.computeAndCacheCount();
    }

    /**
     * An utility object, used to store the data returned by the GeneralizedSuffixTree GeneralizedSuffixTree.searchWithCount method.
     * It contains a collection of results and the total number of results present in the GST.
     * @see GeneralizedSuffixTreeStringArray#searchWithCount(java.lang.String, int) 
     */
    public static class ResultInfo {

        /**
         * The total number of results present in the database
         */
        public int totalResults;
        /**
         * The collection of (some) results present in the GST
         */
        public Collection<Integer> results;

        public ResultInfo(Collection<Integer> results, int totalResults) {
            this.totalResults = totalResults;
            this.results = results;
        }
    }

    /**
     * A private class used to return a tuples of two elements
     */
    protected class Pair<A, B> {

        private final A first;
        private final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        public A getFirst() {
            return first;
        }

        public B getSecond() {
            return second;
        }
    }
    
    public void print(){
    	this.root.printNode(0);
    }
}
