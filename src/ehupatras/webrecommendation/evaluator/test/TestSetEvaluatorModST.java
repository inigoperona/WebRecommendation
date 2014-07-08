package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;

public class TestSetEvaluatorModST 
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	protected ArrayList<MySuffixTree> m_clustSuffixTree = null;
	
	// CREATOR
	
	public TestSetEvaluatorModST(
			ArrayList<String[]> sequences,
			ArrayList<MySuffixTree> clustSuffixTree){
		super(sequences);
		m_clustSuffixTree = clustSuffixTree;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(){
		
	}	
	
}
