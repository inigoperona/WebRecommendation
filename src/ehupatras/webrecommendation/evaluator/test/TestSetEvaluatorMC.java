package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorMC;

public class TestSetEvaluatorMC 
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	private MarkovChain m_markovchain = null;
	
	// CREATOR
	
	public TestSetEvaluatorMC(
			ArrayList<String[]> sequences,
			int modePrRe,
			MarkovChain markovchain){
		super(sequences, modePrRe);
		m_markovchain = markovchain;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorMC(sequence, m_modePrRe, m_markovchain);
		return seqEva;
	}
	
}
