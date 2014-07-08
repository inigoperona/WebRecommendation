package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;

public class TestSetEvaluatorModSTknn 
				extends TestSetEvaluatorModST {

	// ATTRIBUTES
	
	private ArrayList<String[]> m_medoids = null;
	private int[] m_gmedoids = null;
	
	// CREATOR
	
	public TestSetEvaluatorModSTknn(
			ArrayList<String[]> sequences,
			ArrayList<MySuffixTree> clustSuffixTree,
			ArrayList<String[]> medoids,
			int[] gmedoids){
		super(sequences, clustSuffixTree);
		m_clustSuffixTree = clustSuffixTree;
		m_medoids = medoids;
		m_gmedoids = gmedoids;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(){
		
	}	
	
}
