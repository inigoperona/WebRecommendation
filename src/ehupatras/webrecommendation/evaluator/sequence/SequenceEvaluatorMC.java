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
			
			MarkovChain markovchain){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix);
		m_markovchain = markovchain;
	}
	
	// GET RECOMMENDER
	
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderMarkovChain(m_markovchain);
		return recommender;
	}
	
}
