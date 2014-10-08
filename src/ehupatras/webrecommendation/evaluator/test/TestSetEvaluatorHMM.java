package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
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
			URLconverterUsaCon conv,
			int nURLs,
			float[][] urlSimilarityMatrix,
			
			HiddenMarkovModel hmm,
			int nNextSteps){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix);
		m_hmm = hmm;
		m_nNextSteps = nNextSteps;
	}

	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] testseq){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorHMM(testseq, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_hmm, m_nNextSteps);
		return seqEva;
	}
	
}
