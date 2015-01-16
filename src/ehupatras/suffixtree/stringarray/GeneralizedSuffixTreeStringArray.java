 package ehupatras.suffixtree.stringarray;

import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;

import ehupatras.suffixtree.stringarray.Edge;
import ehupatras.suffixtree.stringarray.EdgeBag;

// TODO: Auto-generated Javadoc
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

    /** The index of the last item that was added to the GST. */
    private int last = 0;
    
    /** The root of the suffix tree. */
    private final Node root = new Node();
    
    /** The last leaf that was added during the update operation. */
    private Node activeLeaf = root;

    /**
     * Searches for the given word within the GST.
     * 
     * Returns all the indexes for which the key contains the <tt>word</tt> that was
     * supplied as input.
     *
     * @param words the words
     * @return the collection of indexes associated with the input <tt>word</tt>
     */
    public Collection<Integer> search(ArrayList<String> words) {
        return search(words, -1);
    }

    /**
     * Searches for the given word within the GST and returns at most the given number of matches.
     *
     * @param words the words
     * @param results the max number of results to return
     * @return at most <tt>results</tt> values for the given word
     */
    public Collection<Integer> search(ArrayList<String> words, int results) {
        Node tmpNode = searchNode(words);
        if (tmpNode == null) {
            return null;
        }
        return tmpNode.getData(results);
    }

    /**
     * Searches for the given word within the GST and returns at most the given number of matches.
     *
     * @param words the words
     * @param to the max number of results to return
     * @return at most <tt>results</tt> values for the given word
     * @see GeneralizedSuffixTreeStringArray#ResultInfo
     */
    public ResultInfo searchWithCount(ArrayList<String> words, int to) {
        Node tmpNode = searchNode(words);
        if (tmpNode == null) {
            return new ResultInfo(Collections.EMPTY_LIST, 0);
        }

        return new ResultInfo(tmpNode.getData(to), tmpNode.getResultCount());
    }

    /**
     * Returns the tree node (if present) that corresponds to the given string.
     *
     * @param words the words
     * @return the node
     */
    private Node searchNode(ArrayList<String> words) {
        /*
         * Verifies if exists a path from the root to a node such that the concatenation
         * of all the labels on the path is a superstring of the given word.
         * If such a path is found, the last node on it is returned.
         */
        Node currentNode = root;
        Edge currentEdge;

        for (int i = 0; i < words.size(); ++i) {
            String word = words.get(i);
            // follow the edge corresponding to this char
            currentEdge = currentNode.getEdge(word);
            if (null == currentEdge) {
                // there is no edge starting with this char
                return null;
            } else {
                ArrayList<String> label = currentEdge.getLabel();
                int lenToMatch = Math.min(words.size() - i, label.size());
                
                boolean regionMatches = true;
                for(int k=0; k<lenToMatch; k++){ if(!words.get(i+k).equals(label.get(k))){ regionMatches = false; break;} }
                if (!regionMatches) {
                    // the label on the edge does not correspond to the one in the string to search
                    return null;
                }

                if (label.size() >= words.size() - i) {
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
    public void put(ArrayList<String> key, int index) throws IllegalStateException {
        if (index < last) {
            throw new IllegalStateException("The input index must not be less than any of the previously inserted ones. Got " + index + ", expected at least " + last);
        } else {
            last = index;
        }

        // reset activeLeaf
        activeLeaf = root;

        ArrayList<String> remainder = key;
        Node s = root;

        // proceed with tree construction (closely related to procedure in
        // Ukkonen's paper)
        ArrayList<String> text = new ArrayList<String>(0);
        // iterate over the string, one char at a time
        for (int i = 0; i < remainder.size(); i++) {
        	
            // line 6
        	text.add(remainder.get(i));
        	
            // line 7: update the tree with the new transitions due to this new char
        	// substring
        	ArrayList<String> remainderSubList = new ArrayList<String>(); 
        	for(int k=i; k<remainder.size(); k++){ remainderSubList.add(remainder.get(k)); }
        	
            Pair<Node, ArrayList<String>> active = update(s, text, remainderSubList, index);
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
    private Pair<Boolean, Node> testAndSplit(final Node inputs, final ArrayList<String> stringPart, final String t, final ArrayList<String> remainder, final int value) {
        // descend the tree as far as possible
        Pair<Node, ArrayList<String>> ret = canonize(inputs, stringPart);
        Node s = ret.getFirst();
        ArrayList<String> str = ret.getSecond();

        if (!str.isEmpty()) {
            Edge g = s.getEdge(str.get(0));
            
            ArrayList<String> label = g.getLabel();
            // must see whether "str" is substring of the label of an edge
            if (label.size() > str.size() && label.get(str.size()).equals(t)) {
                return new Pair<Boolean, Node>(true, s);
            } else {
                // need to split the edge
            	// substring
            	ArrayList<String> newlabel = new ArrayList<String>();
            	for(int k=str.size(); k<label.size(); k++){ newlabel.add(label.get(k)); };
            	
            	// startsWith
            	boolean startsWith = true;
            	if(str.size() <= label.size()){
            		for(int k=0; k<str.size(); k++){ if(!label.get(k).equals(str.get(k))){ startsWith = false; break; } }
            	} else {
            		startsWith = false;
            	}
                assert (startsWith);

                // build a new node
                Node r = new Node();
                // build a new edge
                Edge newedge = new Edge(str, r);

                g.setLabel(newlabel);

                // link s -> r
                r.addEdge(newlabel.get(0), g);
                s.addEdge(str.get(0), newedge);

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
                } else {
                	// startsWith
                	boolean startsWith = true;
                	if(e.getLabel().size() <= remainder.size()){
                		for(int k=0; k<e.getLabel().size(); k++){ if(!remainder.get(k).equals(e.getLabel().get(k))){ startsWith = false; break; } }
                	} else {
                		startsWith = false;
                	}
                	if (startsWith) {
                		return new Pair<Boolean, Node>(true, s);
                	} else {
                		// startsWith
                    	startsWith = true;
                    	if(remainder.size() <= e.getLabel().size()){
                    		for(int k=0; k<remainder.size(); k++){ if(!e.getLabel().get(k).equals(remainder.get(k))){ startsWith = false; break; } }
                    	} else {
                    		startsWith = false;
                    	}
                    	if (startsWith) {
                			// need to split as above
                			Node newNode = new Node();
                			newNode.addRef(value);

                			Edge newEdge = new Edge(remainder, newNode);

                			// substring
                			ArrayList<String> labelaux = new ArrayList<String>();
                			for(int k=remainder.size(); k<e.getLabel().size(); k++){ labelaux.add(e.getLabel().get(k)); }
                			e.setLabel(labelaux);

                			newNode.addEdge(e.getLabel().get(0), e);

                			s.addEdge(t, newEdge);

                			return new Pair<Boolean, Node>(false, s);
                		} else {
                			// they are different words. No prefix. but they may still share some common substr
                			return new Pair<Boolean, Node>(true, s);
                		}
                	}
                }
            }
        }

    }

    /**
     * Return a (Node, String) (n, remainder) pair such that n is a farthest descendant of
     * s (the input node) that can be reached by following a path of edges denoting
     * a prefix of inputstr and remainder will be string that must be
     * appended to the concatenation of labels from s to n to get inpustr.
     *
     * @param s the s
     * @param inputstr the inputstr
     * @return the pair
     */
    private Pair<Node, ArrayList<String>> canonize(final Node s, final ArrayList<String> inputstr) {

        if (inputstr.isEmpty()) {
            return new Pair<Node, ArrayList<String>>(s, inputstr);
        } else {
            Node currentNode = s;
            ArrayList<String> str = inputstr;
            
            Edge g = s.getEdge(str.get(0));
            // descend the tree as long as a proper label is found
            // startsWith
        	boolean startsWith = true;
        	if(g.getLabel().size()<=str.size()){
        		for(int k=0; k<g.getLabel().size(); k++){ if(!str.get(k).equals(g.getLabel().get(k))){ startsWith = false; break; } }
        	} else {
        		startsWith = false;
        	}
            while (g != null && startsWith) {
            	// substring
            	ArrayList<String> straux = new ArrayList<String>();
            	for(int k=g.getLabel().size(); k<str.size(); k++){ straux.add(str.get(k)); }
                str = straux;
                currentNode = g.getDest();
                if (str.size() > 0) {
                    g = currentNode.getEdge(str.get(0));
                }
                // startsWith
            	startsWith = true;
            	if(g.getLabel().size()<=str.size()){
            		for(int k=0; k<g.getLabel().size(); k++){ if(!str.get(k).equals(g.getLabel().get(k))){ startsWith = false; break; } }
            	} else {
            		startsWith = false;
            	}
            }

            return new Pair<Node, ArrayList<String>>(currentNode, str);
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
     * @return the pair
     */
    private Pair<Node, ArrayList<String>> update(final Node inputNode, final ArrayList<String> stringPart, final ArrayList<String> rest, final int value) {
        Node s = inputNode;
        ArrayList<String> tempstr = stringPart;
        String newChar = stringPart.get(stringPart.size() - 1);

        // line 1
        Node oldroot = root;

        // line 1b
        // substring
        ArrayList<String> tempstraux = new ArrayList<String>();
        for(int k=0; k<tempstr.size()-1; k++){ tempstraux.add(tempstr.get(k)); }
        
        Pair<Boolean, Node> ret = testAndSplit(s, tempstraux, newChar, rest, value);

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
                // substring
                ArrayList<String> tempstraux2 = new ArrayList<String>();
            	for(int k=1; k<tempstr.size(); k++){ tempstraux2.add(tempstr.get(k)); }
                tempstr = tempstraux2;
            } else {
                Pair<Node, ArrayList<String>> canret = canonize(s.getSuffix(), safeCutLastChar(tempstr));
                s = canret.getFirst();
                // use intern to ensure that tempstr is a reference from the string pool
                // concat
                tempstraux = new ArrayList<String>();
                for(int k=0; k<canret.getSecond().size(); k++){ tempstraux.add(canret.getSecond().get(k)); }
                tempstraux.add(tempstr.get(tempstr.size() - 1));
                tempstr = tempstraux; 
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

        return new Pair<Node, ArrayList<String>>(s, tempstr);
    }

    /**
     * Gets the root.
     *
     * @return the root
     */
    public Node getRoot() {
        return root;
    }

    /**
     * Safe cut last char.
     *
     * @param seq the seq
     * @return the array list
     */
    private ArrayList<String> safeCutLastChar(ArrayList<String> seq) {
        if (seq.size() == 0) {
            return (new ArrayList<String>());
        }
        // substring
        ArrayList<String> seqaux = new ArrayList<String>();
        for(int k=0; k<seq.size()-1; k++){ seqaux.add(seq.get(k)); }
        return seqaux;
    }

    /**
     * Compute count.
     *
     * @return the int
     */
    public int computeCount() {
        return root.computeAndCacheCount();
    }

    /**
     * An utility object, used to store the data returned by the GeneralizedSuffixTree GeneralizedSuffixTree.searchWithCount method.
     * It contains a collection of results and the total number of results present in the GST.
     * @see GeneralizedSuffixTreeStringArray#searchWithCount(java.lang.String, int) 
     */
    public static class ResultInfo {

        /** The total number of results present in the database. */
        public int totalResults;
        
        /** The collection of (some) results present in the GST. */
        public Collection<Integer> results;

        /**
         * Instantiates a new result info.
         *
         * @param results the results
         * @param totalResults the total results
         */
        public ResultInfo(Collection<Integer> results, int totalResults) {
            this.totalResults = totalResults;
            this.results = results;
        }
    }

    /**
     * A private class used to return a tuples of two elements.
     *
     * @param <A> the generic type
     * @param <B> the generic type
     */
    private class Pair<A, B> {

        /** The first. */
        private final A first;
        
        /** The second. */
        private final B second;

        /**
         * Instantiates a new pair.
         *
         * @param first the first
         * @param second the second
         */
        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        /**
         * Gets the first.
         *
         * @return the first
         */
        public A getFirst() {
            return first;
        }

        /**
         * Gets the second.
         *
         * @return the second
         */
        public B getSecond() {
            return second;
        }
    }
    
    /**
     * Prints the.
     */
    public void print(){
    	this.root.printNode(0);
    }
}
