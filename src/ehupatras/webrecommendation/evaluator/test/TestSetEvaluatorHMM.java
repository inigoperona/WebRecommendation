package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;

public class TestSetEvaluatorHMM 
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	private HiddenMarkovModel m_hmm = null;
	
	// CREATOR
	
	public TestSetEvaluatorHMM(
			ArrayList<String[]> sequences,
			HiddenMarkovModel hmm){
		super(sequences);
		m_hmm = hmm;
	}

	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(){
		
	}
	
}
