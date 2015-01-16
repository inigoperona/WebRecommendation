package ehupatras.webrecommendation.evaluator.sequence;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderSuffixTree2;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceEvaluatorST.
 */
public class SequenceEvaluatorST 
				extends SequenceEvaluator {

	// ATTRIBUTES
	
	/** The m_suffixtree. */
	private MySuffixTree m_suffixtree;
	
	/** The m_failuremode. */
	private int m_failuremode = 0;
	
	/** The m_max memory. */
	private int m_maxMemory = 100;
	
	/** The m_norm mode. */
	private int m_normMode = 0;
	
	// CREATOR
	
	/**
	 * Instantiates a new sequence evaluator st.
	 *
	 * @param sequence the sequence
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 * @param suffixtree the suffixtree
	 * @param failuremode the failuremode
	 * @param maxMemory the max memory
	 * @param normMode the norm mode
	 */
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
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator#getRecommender()
	 */
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderSuffixTree2(
						m_suffixtree, 
						m_failuremode, m_maxMemory, m_normMode);
		return recommender;
	}
	
}
