package ehupatras.webrecommendation.evaluator.sequence;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderMarkovChain;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceEvaluatorMC.
 */
public class SequenceEvaluatorMC 
				extends SequenceEvaluator {

	// ATTRIBUTES
	
	/** The m_markovchain. */
	private MarkovChain m_markovchain = null;
	
	// CREATOR
	
	/**
	 * Instantiates a new sequence evaluator mc.
	 *
	 * @param sequence the sequence
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 * @param markovchain the markovchain
	 */
	public SequenceEvaluatorMC(
			String[] sequence,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			MarkovChain markovchain){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
		m_markovchain = markovchain;
	}
	
	// GET RECOMMENDER
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator#getRecommender()
	 */
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderMarkovChain(m_markovchain);
		return recommender;
	}
	
}
