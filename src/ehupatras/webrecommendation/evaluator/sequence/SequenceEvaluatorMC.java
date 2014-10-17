package ehupatras.webrecommendation.evaluator.sequence;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderMarkovChain;

public class SequenceEvaluatorMC 
				extends SequenceEvaluator {

	// ATTRIBUTES
	
	private MarkovChain m_markovchain = null;
	
	// CREATOR
	
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
	
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderMarkovChain(m_markovchain);
		return recommender;
	}
	
}
