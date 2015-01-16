package ehupatras.webrecommendation.evaluator.sequence;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderHMM;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceEvaluatorHMM.
 */
public class SequenceEvaluatorHMM 
				extends SequenceEvaluator {

	// ATTRIBUTES
	
	/** The m_hmm. */
	private HiddenMarkovModel m_hmm = null; // model
	
	/** The m_n next steps. */
	private int m_nNextSteps = 3;

	// CREATOR
	
	/**
	 * Instantiates a new sequence evaluator hmm.
	 *
	 * @param sequence the sequence
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 * @param hmm the hmm
	 * @param nNextSteps the n next steps
	 */
	public SequenceEvaluatorHMM(
			String[] sequence,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			HiddenMarkovModel hmm,
			int nNextSteps){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
		m_hmm = hmm;
		m_nNextSteps = nNextSteps;
	}
	
	// GET RECOMMENDER
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator#getRecommender()
	 */
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderHMM(m_hmm, m_nNextSteps);
		return recommender;
	}
	
}
