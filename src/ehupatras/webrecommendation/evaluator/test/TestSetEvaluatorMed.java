package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorMed;

public class TestSetEvaluatorMed
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	protected ArrayList<String[]> m_medoids = null;
	protected int[] m_gmedoids = null;
	protected ArrayList<Object[]> m_recos = null;
	
	protected boolean m_isDistance = true;
	protected float[][] m_rolesW = {{ 0f, 0f, 0f},
									{ 0f, 0f, 0f},
									{ 0f, 0f, 0f}};
	protected int m_knn = 100;
	
	// CREATOR
	
	public TestSetEvaluatorMed(
			ArrayList<String[]> sequences,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, 
			float[][] urlSimilarityMatrix,
			
			ArrayList<String[]> medoids,
			int[] gmedoids,
			ArrayList<Object[]> recos,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix);
		m_medoids = medoids;
		m_gmedoids = gmedoids;
		m_recos = recos;
		m_isDistance = isDistance;
		m_rolesW = rolesW;
		m_knn = knn;
	}
	
	protected TestSetEvaluatorMed(
			ArrayList<String[]> sequences,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, 
			float[][] urlSimilarityMatrix){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix);
	}
	
	protected TestSetEvaluatorMed(
			ArrayList<String[]> sequences,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, 
			float[][] urlSimilarityMatrix,
			
			ArrayList<String[]> medoids,
			int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix);
		m_medoids = medoids;
		m_gmedoids = gmedoids;
		m_isDistance = isDistance;
		m_rolesW = rolesW;
		m_knn = knn;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorMed(sequence, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_medoids, m_gmedoids, m_recos,
						m_isDistance, m_rolesW, m_knn);
		return seqEva;
	}
	
}
