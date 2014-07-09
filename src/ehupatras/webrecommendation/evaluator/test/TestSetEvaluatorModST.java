package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorModST;

public class TestSetEvaluatorModST 
				extends TestSetEvaluatorMed {

	// ATTRIBUTES
	
	protected ArrayList<MySuffixTree> m_clustSuffixTree = null;
	
	// CREATOR
	
	public TestSetEvaluatorModST(
			ArrayList<String[]> sequences,
			ArrayList<MySuffixTree> clustSuffixTree){
		super(sequences);
		m_clustSuffixTree = clustSuffixTree;
	}
	
	protected TestSetEvaluatorModST(
			ArrayList<String[]> sequences,
			ArrayList<MySuffixTree> clustSuffixTree,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequences, 
				medoids, gmedoids, isDistance, rolesW, knn);
		m_clustSuffixTree = clustSuffixTree;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorModST(sequence, m_clustSuffixTree);
		return seqEva;
	}
	
}
