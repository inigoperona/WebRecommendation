package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorMed;

public class TestSetEvaluatorMedContent 
				extends TestSetEvaluatorMed {

	// ATTRIBUTES
	
	private String m_enrichStrategy = "ContentsA1";
	private int m_nURLs = 0;
	private float[][] m_UrlSimilarityMatrix = null;
	private String[][] m_UrlRelationMatrix = null;
	private HashMap<Integer,Integer> m_UrlClusteringDict = null;
	private URLconverterUsaCon m_conv = null;
	private ArrayList<Integer> m_noProposeURLs = new ArrayList<Integer>();
	
	// CREATOR
	
	public TestSetEvaluatorMedContent(
			ArrayList<String[]> sequences,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			ArrayList<Object[]> recos,
			boolean isDistance,
			float[][] rolesW,
			int knn,
			String enrichStrategy,
			int nURLs,
			float[][] urlSimilarityMatrix,
			String[][] urlRelationMatrix,
			HashMap<Integer,Integer> urlClusteringDict,
			URLconverterUsaCon conv,
			ArrayList<Integer> noProposeURLs){
		super(sequences, medoids, gmedoids, recos, isDistance, rolesW, knn);
		m_enrichStrategy = enrichStrategy;
		m_nURLs = nURLs;
		m_UrlSimilarityMatrix = urlSimilarityMatrix;
		m_UrlRelationMatrix = urlRelationMatrix;
		m_UrlClusteringDict = urlClusteringDict;
		m_conv = conv;
		m_noProposeURLs = noProposeURLs;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorMedContent(
						sequence, 
						m_medoids, m_gmedoids, m_recos,
						m_isDistance, m_rolesW, m_knn,
						m_enrichStrategy,
						m_nURLs,
						m_UrlSimilarityMatrix,
						m_UrlRelationMatrix,
						m_UrlClusteringDict,
						m_conv,
						m_noProposeURLs);
		return seqEva;
	}
	
}
