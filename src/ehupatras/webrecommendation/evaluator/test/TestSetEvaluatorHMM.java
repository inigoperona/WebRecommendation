package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorHMM;

public class TestSetEvaluatorHMM 
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	private HiddenMarkovModel m_hmm = null; // model
	
	private int m_nNextSteps = 3;
	
	// CREATOR
	
	public TestSetEvaluatorHMM(
			ArrayList<String[]> sequences,
			int modePrRe,
			HiddenMarkovModel hmm,
			int nNextSteps){
		super(sequences, modePrRe);
		m_hmm = hmm;
		m_nNextSteps = nNextSteps;
	}

	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] testseq){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorHMM(testseq, m_modePrRe, m_hmm, m_nNextSteps);
		return seqEva;
	}
	
}
