package ehupatras.webrecommendation.evaluator.sequence;

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
			MarkovChain markovchain){
		super(sequence);
		m_markovchain = markovchain;
	}
	
	// GET RECOMMENDER
	
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderMarkovChain(m_markovchain);
		return recommender;
	}
	
}
