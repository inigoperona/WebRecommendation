package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorHMM;

// TODO: Auto-generated Javadoc
/**
 * The Class TestSetEvaluatorHMM.
 */
public class TestSetEvaluatorHMM 
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	/** The m_hmm. */
	private HiddenMarkovModel m_hmm = null; // model
	
	/** The m_n next steps. */
	private int m_nNextSteps = 3;
	
	// CREATOR
	
	/**
	 * Instantiates a new test set evaluator hmm.
	 *
	 * @param sequences the sequences
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 * @param hmm the hmm
	 * @param nNextSteps the n next steps
	 */
	public TestSetEvaluatorHMM(
			ArrayList<String[]> sequences,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs,
			float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			HiddenMarkovModel hmm,
			int nNextSteps){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
		m_hmm = hmm;
		m_nNextSteps = nNextSteps;
	}

	// GET SEQUENCE EVALUATOR
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.test.TestSetEvaluator#getSequenceEvaluator(java.lang.String[])
	 */
	public SequenceEvaluator getSequenceEvaluator(String[] testseq){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorHMM(testseq, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content, 
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						m_hmm, m_nNextSteps);
		return seqEva;
	}
	
}
