package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorModST;

public class TestSetEvaluatorModST 
				extends TestSetEvaluatorMed {

	// ATTRIBUTES
	
	protected ArrayList<MySuffixTree> m_clustSuffixTree = null;
	
	// CREATOR
	
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
	
	protected TestSetEvaluatorModST(
			ArrayList<String[]> sequences,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, 
			float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			ArrayList<MySuffixTree> clustSuffixTree,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min,
				medoids, gmedoids, isDistance, rolesW, knn);
		m_clustSuffixTree = clustSuffixTree;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorModST(sequence, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content, 
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						m_clustSuffixTree);
		return seqEva;
	}
	
}
