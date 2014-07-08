package ehupatras.webrecommendation.evaluator.sequence;

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
			HiddenMarkovModel hmm,
			int nNextSteps){
		super(sequence);
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
