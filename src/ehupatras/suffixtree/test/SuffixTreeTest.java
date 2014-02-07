package ehupatras.suffixtree.test;

import ehupatras.suffixtree.GeneralizedSuffixTree;
   
 

public class SuffixTreeTest {

    public static void test1() {
        GeneralizedSuffixTree in = new GeneralizedSuffixTree();

        String word = "cacao";
        in.put(word, 0);
        in.print();
 
    }

    
    public static void main(String[] args){
    	test1();
    }
}
