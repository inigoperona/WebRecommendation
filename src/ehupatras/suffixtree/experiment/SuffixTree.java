package ehupatras.suffixtree.experiment;

import ehupatras.suffixtree.GeneralizedSuffixTree;

public class SuffixTree {
	
    public static void main(String[] args){
        GeneralizedSuffixTree in = new GeneralizedSuffixTree();
        in.put("cacao", 0);
        in.put("banana", 1);
        //in.put("milo", 2);
        in.print();
    }
    
}
