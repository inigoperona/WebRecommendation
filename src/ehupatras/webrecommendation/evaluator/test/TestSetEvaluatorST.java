package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;

public class TestSetEvaluatorST 
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	private MySuffixTree m_suffixtree;
	
	// CREATOR
	
	public TestSetEvaluatorST(
			ArrayList<String[]> sequences,
			MySuffixTree suffixtree){
		super(sequences);
		m_suffixtree = suffixtree;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(){
		
	}
	
}
