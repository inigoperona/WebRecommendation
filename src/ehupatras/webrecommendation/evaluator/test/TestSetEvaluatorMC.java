package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;

public class TestSetEvaluatorMC 
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	private MarkovChain m_markovchain = null;
	
	// CREATOR
	
	public TestSetEvaluatorMC(
			ArrayList<String[]> sequences,
			MarkovChain markovchain){
		super(sequences);
		m_markovchain = markovchain;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(){
		
	}
	
}
