package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorModST;

// TODO: Auto-generated Javadoc
/**
 * The Class TestSetEvaluatorModST.
 */
public class TestSetEvaluatorModST 
				extends TestSetEvaluatorMed {

	// ATTRIBUTES
	
	/** The m_clust suffix tree. */
	protected ArrayList<MySuffixTree> m_clustSuffixTree = null;
	
	// CREATOR
	
	/**
	 * Instantiates a new test set evaluator mod st.
	 *
	 * @param sequences the sequences
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 * @param clustSuffixTree the clust suffix tree
	 */
	public TestSetEvaluatorModST(
			ArrayList<String[]> sequences,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, 
			float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			ArrayList<MySuffixTree> clustSuffixTree){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
		m_clustSuffixTree = clustSuffixTree;
	}
	
	/**
	 * Instantiates a new test set evaluator mod st.
	 *
	 * @param sequences the sequences
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 * @param clustSuffixTree the clust suffix tree
	 * @param medoids the medoids
	 * @param gmedoids the gmedoids
	 * @param isDistance the is distance
	 * @param rolesW the roles w
	 * @param knn the knn
	 */
	protected TestSetEvaluatorModST(
			ArrayList<String[]> sequences,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, 
			float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			ArrayList<MySuffixTree> clustSuffixTree,
			ArrayList<String[]> medoids, int[] clustersizes, int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min,
				medoids, clustersizes, gmedoids, 
				isDistance, rolesW, knn);
		m_clustSuffixTree = clustSuffixTree;
	}
	
	// GET SEQUENCE EVALUATOR
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorMed#getSequenceEvaluator(java.lang.String[])
	 */
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorModST(sequence, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content, 
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						m_clustSuffixTree);
		return seqEva;
	}
	
}
