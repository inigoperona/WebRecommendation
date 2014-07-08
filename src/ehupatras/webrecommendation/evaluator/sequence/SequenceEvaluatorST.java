package ehupatras.webrecommendation.evaluator.sequence;

import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderSuffixTree2;

public class SequenceEvaluatorST 
				extends SequenceEvaluator {

	// ATTRIBUTES
	
	private MySuffixTree m_suffixtree;
	
	private int m_failuremode = 0;
	private int m_maxMemory = 100;
	private int m_normMode = 0;
	
	// CREATOR
	
	public SequenceEvaluatorST(
			String[] sequence,
			MySuffixTree suffixtree,
			int failuremode,
			int maxMemory,
			int normMode){
		super(sequence);
		m_suffixtree = suffixtree;
		m_failuremode = failuremode;
		m_maxMemory = maxMemory;
		m_normMode = normMode;
	}
	
	// GET RECOMMENDER
	
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderSuffixTree2(
						m_suffixtree, 
						m_failuremode, m_maxMemory, m_normMode);
		return recommender;
	}
	
}
