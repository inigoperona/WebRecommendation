package ehupatras.suffixtree.experiment;

import ehupatras.suffixtree.stringarray.GeneralizedSuffixTreeStringArray;

public class SuffixTreeStringArray {
    
	private GeneralizedSuffixTreeStringArray m_gST = new GeneralizedSuffixTreeStringArray();
	
	public void putSequence(String[] seq, int index){
		m_gST.put(seq, index);
	}
	
	public void printSuffixTree(){
		m_gST.print();
	}
	
    public static void main(String[] args){
        GeneralizedSuffixTreeStringArray in = new GeneralizedSuffixTreeStringArray();
        String[] word1 = {"c", "a", "c", "a", "o"};
        String[] word2 = {"b", "a", "n", "a", "n", "a"};
        String[] word3 = {"m", "i", "l", "o"};
        in.put(word1, 0);
        in.put(word2, 1);
        //in.put(word3, 2);
        in.print();
    }
}
