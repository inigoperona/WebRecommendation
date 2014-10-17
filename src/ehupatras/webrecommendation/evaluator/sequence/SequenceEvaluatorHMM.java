package ehupatras.webrecommendation.evaluator.sequence;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderHMM;

public class SequenceEvaluatorHMM 
				extends SequenceEvaluator {

	// ATTRIBUTES
	
	private HiddenMarkovModel m_hmm = null; // model
	
	private int m_nNextSteps = 3;

	// CREATOR
	
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
	
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderHMM(m_hmm, m_nNextSteps);
		return recommender;
	}
	
}
