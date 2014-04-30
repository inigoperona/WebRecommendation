package ehupatras.suffixtree.stringarray.myst;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import ehupatras.suffixtree.stringarray.Node;
import ehupatras.suffixtree.stringarray.EdgeBag;
import ehupatras.suffixtree.stringarray.Edge;


public class MySuffixTree {

	// the suffix tree creator
	private SuffixTreeStringArray m_suffixtreeSA;
	
	// my suffix tree
	private ArrayList<ArrayList<String>> m_labelsAL;
	private ArrayList<ArrayList<Integer>> m_childrenAL;
	private int[] m_depths;
	private ArrayList<ArrayList<String>> m_paths;
	private float[] m_frequencies;
	private float[] m_norm1;
	private float[] m_norm2;
	private ArrayList<ArrayList<Integer>> m_childrenAL2;
	
	// database info
	private int m_nSequences = 0;
	private int m_nRequests = 0;
	
	public MySuffixTree(ArrayList<String[]> sequences){
		// create the suffix tree
		m_suffixtreeSA = new SuffixTreeStringArray();
		for(int i=0; i<sequences.size(); i++){
			String[] sequence = sequences.get(i);
			m_suffixtreeSA.putSequence(sequence, i);
			m_nRequests = m_nRequests + sequence.length;
		}
		m_nSequences = sequences.size();
		
		// initialize
		this.init();
		m_suffixtreeSA = null;
	}
	
	private void init(){
        this.generateMyST();
        this.updateDepth();
        this.computeFrequencies();
        this.computePaths();
        this.normalize1();
	}
	
	private void generateMyST(){
		// convert the ST to the vector format
		m_labelsAL = new ArrayList<ArrayList<String>>();
		m_childrenAL = new ArrayList<ArrayList<Integer>>();
		int nNodes = 0;
		
		// the first node to analyze is the root
		Node root = m_suffixtreeSA.getRoot();
		ArrayList<Node> nodesToAnalyze = new ArrayList<Node>();
		nodesToAnalyze.add(root);
		m_labelsAL.add(new ArrayList<String>());
		nNodes++;
		
		// add the other nodes
		for(int i=0; i<nodesToAnalyze.size(); i++){
			Node nod = nodesToAnalyze.get(i);
			EdgeBag ebag = nod.getEdges();
			ArrayList<Edge> edges = ebag.values();
			ArrayList<Integer> children = new ArrayList<Integer>();
			for(int j=0; j<edges.size(); j++){
				Edge edge = edges.get(j);
				Node dest = edge.getDest();
				nodesToAnalyze.add(dest);
				ArrayList<String> label = edge.getLabel();
				m_labelsAL.add(label);
				children.add(nNodes);
				nNodes++;
			}
			// update MySuffixTree
			m_childrenAL.add(children);
		}
	}
	
	private void updateDepth(){
		// initialize depth array
		m_depths = new int[m_childrenAL.size()];
		
		// root node 
		m_depths[0] = 0;
		for(int i=0; i<m_childrenAL.size(); i++){
			int actualdepth = m_depths[i];
			ArrayList<Integer> children = m_childrenAL.get(i);
			for(int j=0; j<children.size(); j++){
				int indChild = children.get(j);
				m_depths[indChild] = actualdepth + 1;
			}
		}
	}
	
	private void computeFrequencies(){
		// initialize frequencies array
		m_frequencies = new float[m_labelsAL.size()];
		
		// add the root node to analyze
		ArrayList<Integer> analyzeInds = new ArrayList<Integer>();
		analyzeInds.add(0);
		m_frequencies[0] = (float)m_nSequences;
		
		// compute all nodes path frequencies
		ArrayList<Integer> nodePath = new ArrayList<Integer>();
		for(int i=0; i<m_labelsAL.size(); i++){
			int ind = analyzeInds.get(i);
			
			// get the node data
			ArrayList<Integer> children = m_childrenAL.get(ind);
			int depth = m_depths[ind];
			
			// add next children to analyze
			for(int j=0; j<children.size(); j++){
				int chInd = children.get(j);
				analyzeInds.add(i+1+j, chInd);
			}
			
			// update the node path
			if(nodePath.size()<=depth){
				nodePath.add(ind);
			} else {
				int len = nodePath.size();
				for(int j=len-1; j>=depth; j--){
					nodePath.remove(j);
				}
				nodePath.add(ind);
			}
			
			// compute the weight of the actual node
			ArrayList<String> path = this.getPath(nodePath);
			
			// compute the frequency
			if(ind!=0){
				m_frequencies[ind] = (float)m_suffixtreeSA.search(path).size();
			}
		}
	}
	
	private void computePaths(){
		// initialize the path array
		m_paths = new ArrayList<ArrayList<String>>();
		for(int i=0; i<m_labelsAL.size(); i++){
			m_paths.add(new ArrayList<String>());
		}
		
		// add the root node to analyze
		ArrayList<Integer> analyzeInds = new ArrayList<Integer>();
		analyzeInds.add(0);
		
		// compute all nodes path frequencies
		ArrayList<Integer> nodePath = new ArrayList<Integer>();
		for(int i=0; i<m_labelsAL.size(); i++){
			int ind = analyzeInds.get(i);
			
			// get the node data
			ArrayList<Integer> children = m_childrenAL.get(ind);
			int depth = m_depths[ind];
			
			// add next children to analyze
			for(int j=0; j<children.size(); j++){
				int chInd = children.get(j);
				analyzeInds.add(i+1+j, chInd);
			}
			
			// update the node path
			if(nodePath.size()<=depth){
				nodePath.add(ind);
			} else {
				int len = nodePath.size();
				for(int j=len-1; j>=depth; j--){
					nodePath.remove(j);
				}
				nodePath.add(ind);
			}
			
			// compute the weight of the actual node
			ArrayList<String> path = this.getPath(nodePath);
			m_paths.set(ind, path);
		}
	}
	
	private ArrayList<String> getPath(ArrayList<Integer> nodePath){
		ArrayList<String> path = new ArrayList<String>();
		for(int i=0; i<nodePath.size(); i++){
			int ind = nodePath.get(i);
			ArrayList<String> label = m_labelsAL.get(ind);
			for(int j=0; j<label.size(); j++){
				path.add(label.get(j));
			}
		}
		return path;
	}
	
	private void normalize1(){
		// initialize normalized array
		m_norm1 = new float[m_labelsAL.size()];
		
		// add the root node to analyze
		ArrayList<Integer> analyzeInds = new ArrayList<Integer>();
		analyzeInds.add(0);
		
		// for each node path
		for(int i=0; i<m_paths.size(); i++){
			ArrayList<String> path = m_paths.get(i);
			ArrayList<Integer> findInds = this.findAllSuffixes(path);
			float sum = 0;
			int nInd = findInds.size();
			for(int j=0; j<nInd; j++){
				int indj = findInds.get(j);
				sum = sum + m_frequencies[indj];
			}
			float avg;
			if(nInd>0){
				avg = (float)sum / (float)nInd;
			} else {
				avg = Float.POSITIVE_INFINITY;
			}
			m_norm1[i] = avg;
		}
	}
	
	
	private ArrayList<Integer> findAllSuffixes(ArrayList<String> path){
		ArrayList<Integer> findInds = new ArrayList<Integer>();
		
		for(int i1=0; i1<path.size(); i1++){
			// for each suffix
			ArrayList<String> suffix = new ArrayList<String>();
			for(int i2=i1; i2<path.size(); i2++){
				String elem = path.get(i2);
				suffix.add(elem);
			}
			
			// find the suffix
			for(int j=0; j<m_paths.size(); j++){
				ArrayList<String> pathi = m_paths.get(j);
				if(suffix.equals(pathi)){
					findInds.add(j);
					break;
				}
			}
		}
		
		return findInds;
	}
	
	public void printSuffixTree(){
		ArrayList<Integer> printInds = new ArrayList<Integer>();
		printInds.add(0);
		for(int i=0; i<m_labelsAL.size(); i++){
			int ind = printInds.get(i); 
			ArrayList<String> label = m_labelsAL.get(ind);
			ArrayList<Integer> children = m_childrenAL.get(ind);
			int depth = m_depths[ind];
			float freq = m_frequencies[ind];
			float norm1 = m_norm1[ind];
			
			// print line //
			
			// initial gaps
			for(int j=0; j<depth; j++){
				System.out.print(" ");
			}
			
			// label
			System.out.print("Label");
			for(int j=0; j<label.size(); j++){
				System.out.print("," + label.get(j));
			}
			System.out.print("; ");
			
			// frequencies
			System.out.print("Frequency(" + freq + "); ");
			
			// norm1
			System.out.print("Normalize1(" + norm1 + "); ");
			
			// childrens
			System.out.print("Children");
			for(int j=0; j<children.size(); j++){
				int chInd = children.get(j);
				System.out.print("," + chInd);
				printInds.add(i+1+j, chInd);
			}
			
			System.out.println();
		}
	}
	
	public boolean isRunnable(ArrayList<String> waydone){
		int[] pointers = this.performWay(waydone);
		int pointerNode = pointers[0];
		int pointerLabel = pointers[1];
		if(pointerNode==-1){
			return false;
		} else {
			return true;
		}
	}
	
	public int[] performWay(ArrayList<String> waydone){
		int[] pointers = new int[2];
		pointers[0] = -1;
		pointers[1] = -1;
		
		// perform the way
		int pointerNode = 0;
		int pointerLabel = 0;
		for(int i=0; i<waydone.size(); i++){
			String step = waydone.get(i);
			pointers = this.doStep(pointerNode, pointerLabel, step);
			pointerNode = pointers[0];
			pointerLabel = pointers[1];
			if(pointerNode==-1){
				return pointers;
			}
		}
		return pointers;
	}
	
	public int[] doStep(int actualNodeInd, int actualLabelInd, String nextStep){
		int[] pointers = new int[2];
		pointers[0] = -1; // node pointer
		pointers[1] = -1; // label position pointer
		
		// see if we have passed all by all elements of the node label
		ArrayList<String> actualLabel = m_labelsAL.get(actualNodeInd);
		if(actualLabelInd<actualLabel.size()){
			// check the next element in the label
			String elem = actualLabel.get(actualLabelInd);
			if(elem.equals(nextStep)){
				pointers[0] = actualNodeInd;
				pointers[1] = actualLabelInd + 1;
			}
		} else {
			// find the next node
			ArrayList<Integer> children = m_childrenAL.get(actualNodeInd);		
			for(int i=0; i<children.size(); i++){
				int ind = children.get(i);
				ArrayList<String> label = m_labelsAL.get(ind);
				String elem = label.get(0);
				if(elem.equals(nextStep)){
					pointers[0] = ind;
					pointers[1] = 1;
				}
			}
		}
		return pointers;
	}
	
	public Object[] getNextpossibleStepsFrequencies(int actualNodeInd, int actualLabelInd){
		return this.getNextpossibleSteps(actualNodeInd, actualLabelInd, m_frequencies);
	}
	
	public Object[] getNextpossibleStepsNorm1(int actualNodeInd, int actualLabelInd){
		return this.getNextpossibleSteps(actualNodeInd, actualLabelInd, m_norm1);
	}
	
	public Object[] getNextpossibleStepsNorm2(int actualNodeInd, int actualLabelInd){
		return this.getNextpossibleSteps(actualNodeInd, actualLabelInd, m_norm1);
	}
	
	private Object[] getNextpossibleSteps(int actualNodeInd, int actualLabelInd, float[] weights){
		// the data to return
		ArrayList<String> urlsAL = new ArrayList<String>();
		ArrayList<Float> weightsAL = new ArrayList<Float>();
		// see if we have passed all by all elements of the node label
		ArrayList<String> actualLabel = m_labelsAL.get(actualNodeInd);
		if(actualLabelInd<actualLabel.size()){
			// check the next element in the label
			float freq = m_frequencies[actualNodeInd];
			String elem = actualLabel.get(actualLabelInd);
			urlsAL.add(elem);
			weightsAL.add(freq);
		} else {
			// find the next node
			ArrayList<Integer> children = m_childrenAL.get(actualNodeInd);		
			for(int i=0; i<children.size(); i++){
				int ind = children.get(i);
				ArrayList<String> label = m_labelsAL.get(ind);
				String elem = label.get(0);
				float freq = m_frequencies[ind];
				urlsAL.add(elem);
				weightsAL.add((float)freq);
			}
		}
		// return the data
		Object[] objA = new Object[2];
		objA[0] = urlsAL;
		objA[1] = weightsAL;
		return objA;
	}
	
	public int getNumberOfNodes(){
		return m_labelsAL.size();
	}
	
	public float getNumberOfEdges(){
		float sum = 0f;
		for(int i=0; i<m_childrenAL.size(); i++){
			ArrayList<Integer> children = m_childrenAL.get(i);
			sum = sum + (float)children.size();
		}
		return (sum / (float)m_childrenAL.size());
	}
	
	public static void main(String[] args){
		// create the database
        String[] word1 = {"c", "a", "c", "a", "o"};
        String[] word2 = {"b", "a", "n", "a", "n", "a"};
        String[] word3 = {"m", "i", "l", "o"};
        String[] word4 = {"c", "a", "r"};
        ArrayList<String[]> sequences = new ArrayList<String[]>(); 
        sequences.add(word1);
        sequences.add(word2);
        sequences.add(word3);
        sequences.add(word4);
        
        // create our suffix tree
        MySuffixTree st = new MySuffixTree(sequences);
        st.printSuffixTree();
        
        // runnable ways?
        ArrayList<String> waydone1 = new ArrayList<String>();
        waydone1.add("a");
        waydone1.add("c");
        waydone1.add("a");
        waydone1.add("o");
        int[] points1 = st.performWay(waydone1);
        System.out.println("runnable?: " + points1[0] + ":" + points1[1]);
        ArrayList<String> waydone2 = new ArrayList<String>();
        waydone2.add("");
        int[] points2 = st.performWay(waydone2);
        System.out.println("runnable?: " + points2[0] + ":" + points2[1]);
        ArrayList<String> waydone3 = new ArrayList<String>();
        waydone3.add("b");
        int[] points3 = st.performWay(waydone3);
        System.out.println("runnable?: " + points3[0] + ":" + points3[1]);
	}
	
}
