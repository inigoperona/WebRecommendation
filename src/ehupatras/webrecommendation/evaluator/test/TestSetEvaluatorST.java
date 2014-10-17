package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorST;

public class TestSetEvaluatorST 
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	private MySuffixTree m_suffixtree;
	
	private int m_failuremode = 0;
	private int m_maxMemory = 100;
	private int m_normMode = 0;
	
	// CREATOR
	
	public TestSetEvaluatorST(
			ArrayList<String[]> sequences,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, 
			float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			MySuffixTree suffixtree,
			int failuremode,
			int maxMemory,
			int normMode){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
		m_suffixtree = suffixtree;
		m_failuremode = failuremode;
		m_maxMemory = maxMemory;
		m_normMode = normMode;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorST(sequence, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content, 
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						m_suffixtree,
						m_failuremode, m_maxMemory, m_normMode);
		return seqEva;
	}
	
}
