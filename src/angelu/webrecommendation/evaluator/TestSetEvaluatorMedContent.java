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
	private String[][] m_UrlRelationMatrix = null;
	private ArrayList<Integer> m_noProposeURLs = new ArrayList<Integer>();
	
	// CREATOR
	
	public TestSetEvaluatorMedContent(
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
			int knn,
			String enrichStrategy,
			String[][] m_urlRelationMatrix,
			HashMap<Integer,Integer> urlClusteringDict,
			ArrayList<Integer> noProposeURLs){
		super(sequences, modePrRe, conv, nURLs, urlSimilarityMatrix,
				medoids, gmedoids, recos, isDistance, rolesW, knn);
		m_enrichStrategy = enrichStrategy;
		m_UrlRelationMatrix = m_urlRelationMatrix;
		m_urlClusteringDict = urlClusteringDict;
		m_noProposeURLs = noProposeURLs;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorMedContent(
						sequence, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						
						m_medoids, m_gmedoids, m_recos,
						m_isDistance, m_rolesW, m_knn,
						m_enrichStrategy,
						m_UrlRelationMatrix,
						m_urlClusteringDict,
						m_noProposeURLs);
		return seqEva;
	}
	
}
