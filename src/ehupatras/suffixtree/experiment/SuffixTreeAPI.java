package ehupatras.suffixtree.experiment;

import ehupatras.suffixtree.GeneralizedSuffixTree;

public class SuffixTreeAPI {
    
	private GeneralizedSuffixTree m_gST = new GeneralizedSuffixTree();
	
	public void putSequence(String seq, int index){
		m_gST.put(seq, index);
	}
	
	public void printSuffixTree(){
		m_gST.print();
	}
	
    public static void main(String[] args){
        GeneralizedSuffixTree in = new GeneralizedSuffixTree();
        in.put("cacao", 0);
        in.put("banana", 1);
        in.put("milo", 2);
        in.print();
    }
}
