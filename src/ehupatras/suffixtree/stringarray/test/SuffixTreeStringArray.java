package ehupatras.suffixtree.stringarray.test;

import ehupatras.suffixtree.stringarray.GeneralizedSuffixTreeStringArray;
import ehupatras.suffixtree.stringarray.Node;
import java.util.ArrayList;

public class SuffixTreeStringArray {
    
	private GeneralizedSuffixTreeStringArray m_gST = new GeneralizedSuffixTreeStringArray();

	public void putSequence(String[] seq, int index){
		ArrayList<String> seqAL = new ArrayList<String>();
		for(int i=0; i<seq.length; i++){
			seqAL.add(seq[i]);
		}
		putSequence(seqAL, index);
	}
	
	public void putSequence(ArrayList<String> seq, int index){
		m_gST.put(seq, index);
	}
	
	public void printSuffixTree(){
		m_gST.print();
	}
	
	public Node getRoot(){
		return m_gST.getRoot();
	}
	
    public static void main(String[] args){
        SuffixTreeStringArray in = new SuffixTreeStringArray();
        String[] word1 = {"c", "a", "c", "a", "o"};
        String[] word2 = {"b", "a", "n", "a", "n", "a"};
        String[] word3 = {"m", "i", "l", "o"};
        //in.putSequence(word1, 0);
        in.putSequence(word2, 1);
        //in.putSequence(word3, 2);
        in.printSuffixTree();
    }
}
