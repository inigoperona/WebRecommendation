package ehupatras.webrecommendation.evaluator.sequence;

import angelu.webrecommendation.converter.URLconverterUsaCon;
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
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			MySuffixTree suffixtree,
			int failuremode,
			int maxMemory,
			int normMode){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
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
