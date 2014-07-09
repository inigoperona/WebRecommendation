package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorModSTknn;

public class TestSetEvaluatorModSTknn 
				extends TestSetEvaluatorModST {

	// CREATOR
	
	public TestSetEvaluatorModSTknn(
			ArrayList<String[]> sequences,
			ArrayList<MySuffixTree> clustSuffixTree,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequences, 
				clustSuffixTree, 
				medoids, gmedoids, isDistance, rolesW, knn);
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorModSTknn(sequence,
						m_clustSuffixTree,
						m_medoids, m_gmedoids, m_isDistance, m_rolesW, m_knn);
		return seqEva;
	}
	
}