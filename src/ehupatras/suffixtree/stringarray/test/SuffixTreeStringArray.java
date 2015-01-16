package ehupatras.suffixtree.stringarray.test;

import ehupatras.suffixtree.stringarray.GeneralizedSuffixTreeStringArray;
import ehupatras.suffixtree.stringarray.Node;
import ehupatras.suffixtree.stringarray.EdgeBag;
import ehupatras.suffixtree.stringarray.Edge;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderSuffixTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;

// TODO: Auto-generated Javadoc
/**
 * The Class SuffixTreeStringArray.
 */
public class SuffixTreeStringArray {
    
	/** The m_g st. */
	private GeneralizedSuffixTreeStringArray m_gST = new GeneralizedSuffixTreeStringArray();
	
	/** The m_node weights. */
	private Hashtable<Node,Integer> m_nodeWeights = new Hashtable<Node,Integer>();
	
	/** The m_nseqs. */
	private int m_nseqs = 0;
	
	/**
	 * Put sequence.
	 *
	 * @param seq the seq
	 * @param index the index
	 */
	public void putSequence(String[] seq, int index){
		ArrayList<String> seqAL = StringToArrayList(seq);
		putSequence(seqAL, index);
	}
	
	/**
	 * String to array list.
	 *
	 * @param strA the str a
	 * @return the array list
	 */
	private ArrayList<String> StringToArrayList(String[] strA){
		ArrayList<String> seqL = new ArrayList<String>();
		for(int i=0; i<strA.length; i++){
			seqL.add(strA[i]);
		}
		return seqL;
	}
	
	/**
	 * Put sequence.
	 *
	 * @param seq the seq
	 * @param index the index
	 */
	public void putSequence(ArrayList<String> seq, int index){
		m_gST.put(seq, index);
		m_nseqs++;
	}
	
	/**
	 * Prints the suffix tree.
	 */
	public void printSuffixTree(){
		m_gST.print();
	}
	
	/**
	 * Gets the root.
	 *
	 * @return the root
	 */
	public Node getRoot(){
		return m_gST.getRoot();
	}
	
	/**
	 * Search.
	 *
	 * @param sequence the sequence
	 * @return the array list
	 */
	public ArrayList<Integer> search(String[] sequence){
		ArrayList<String> seqAL = StringToArrayList(sequence);
		return search(seqAL);
	}
	
	/**
	 * Search.
	 *
	 * @param sequence the sequence
	 * @return the array list
	 */
	public ArrayList<Integer> search(ArrayList<String> sequence){
		Collection<Integer> col = m_gST.search(sequence);
		ArrayList<Integer> searchSeqs = new ArrayList<Integer>();
		if(col == null){
			return searchSeqs;
		} else {
			Iterator<Integer> it = col.iterator();
			while(it.hasNext()){
				int se = it.next();
				searchSeqs.add(se);
			}
			return searchSeqs;
		}
	}
	
	/**
	 * Gets the number of nodes.
	 *
	 * @return the number of nodes
	 */
	public int getNumberOfNodes(){
		int numberOfNodes = 0;
		Node root = m_gST.getRoot();
		ArrayList<Node> nodesToAnalyze = new ArrayList<Node>();
		nodesToAnalyze.add(root);
		for(int i=0; i<nodesToAnalyze.size(); i++){
			Node nod = nodesToAnalyze.get(i);
			numberOfNodes++;
			EdgeBag ebag = nod.getEdges();
			ArrayList<Edge> edges = ebag.values();
			for(int j=0; j<edges.size(); j++){
				Edge edge = edges.get(j);
				Node dest = edge.getDest();
				nodesToAnalyze.add(dest);
			}
		}
		return numberOfNodes;
	}
	
	/**
	 * Gets the number of edges.
	 *
	 * @return the number of edges
	 */
	public float getNumberOfEdges(){
		ArrayList<Integer> nEdgesA = new ArrayList<Integer>();
		
		// save the number of edges in each node
		Node root = m_gST.getRoot();
		ArrayList<Node> nodesToAnalyze = new ArrayList<Node>();
		nodesToAnalyze.add(root);
		for(int i=0; i<nodesToAnalyze.size(); i++){
			Node nod = nodesToAnalyze.get(i);
			EdgeBag ebag = nod.getEdges();
			ArrayList<Edge> edges = ebag.values();
			nEdgesA.add(edges.size());
			for(int j=0; j<edges.size(); j++){
				Edge edge = edges.get(j);
				Node dest = edge.getDest();
				nodesToAnalyze.add(dest);
			}
		}
		
		// compute the average of edges values
		int nElem = 0;
		int sum = 0;
		for(int i=0; i<nEdgesA.size(); i++){
			sum = sum + nEdgesA.get(i);
			if(nEdgesA.get(i)>0){ nElem++; }
		}
		return ((float)sum / (float)nElem);
	}
	
	/**
	 * Increment node weight.
	 *
	 * @param nod the nod
	 */
	public void incrementNodeWeight(Node nod){
		int freq = 0;
		if(m_nodeWeights.containsKey(nod)){
			freq = m_nodeWeights.get(nod);
		}
		freq++;
		m_nodeWeights.put(nod, freq);
	}

	/**
	 * Gets the node weight.
	 *
	 * @param nod the nod
	 * @return the node weight
	 */
	public int getNodeWeight(Node nod){
		int freq = 0;
		if(m_nodeWeights.contains(nod)){
			freq = m_nodeWeights.get(nod);
		}
		return freq;
	}
	
	/**
	 * Weight the suffix tree.
	 *
	 * @param sequences the sequences
	 */
	public void weightTheSuffixTree(ArrayList<String[]> sequences){
		RecommenderSuffixTree recST = new RecommenderSuffixTree(this, 0);
		for(int i=0; i<sequences.size(); i++){
			String[] seq = sequences.get(i);
			recST.reset();
			for(int i1=0; i1<seq.length; i1++){
				recST.update(null, seq[i1], true, false);
			}
		}
	}
	
	/**
	 * Gets the number of construction sequences.
	 *
	 * @return the number of construction sequences
	 */
	public int getNumberOfConstructionSequences(){
		return m_nseqs;
	}
	
	
    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String[] args){
        SuffixTreeStringArray in = new SuffixTreeStringArray();
        String[] word1 = {"c", "a", "c", "a", "o"};
        String[] word2 = {"b", "a", "n", "a", "n", "a"};
        String[] word3 = {"m", "i", "l", "o"};
        String[] word4 = {"c", "a", "r"};
        in.putSequence(word1, 0);
        in.putSequence(word2, 1);
        in.putSequence(word3, 2);
        in.putSequence(word4, 3);
        in.printSuffixTree();
        
        String[] seq1 = {"c", "a"};
        String[] seq2 = {"c", "a", "c", "a"};
        String[] seq3 = {"b", "a"};
        String[] seq4 = {"l", "o"};
        String[] seq5 = {"s", "u", "a"};
        
        ArrayList<Integer> re;
        re = in.search(seq1);
        for(int i=0; i<re.size(); i++) {System.out.print(re.get(i));}
        System.out.println();
        re = in.search(seq2);
        for(int i=0; i<re.size(); i++) {System.out.print(re.get(i));}
        System.out.println();
        re = in.search(seq3);
        for(int i=0; i<re.size(); i++) {System.out.print(re.get(i));}
        System.out.println();
        re = in.search(seq4);
        for(int i=0; i<re.size(); i++) {System.out.print(re.get(i));}
        System.out.println();
        re = in.search(seq5);
        for(int i=0; i<re.size(); i++) {System.out.print(re.get(i));}
        System.out.println();
    }
}
