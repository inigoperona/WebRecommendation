package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
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
			URLconverterUsaCon conv,
			int nURLs, 
			float[][] urlSimilarityMatrix,			
			
			MarkovChain markovchain){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix);
		m_markovchain = markovchain;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorMC(sequence, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_markovchain);
		return seqEva;
	}
	
}
