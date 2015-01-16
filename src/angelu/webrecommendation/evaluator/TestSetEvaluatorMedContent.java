package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorMed;

// TODO: Auto-generated Javadoc
/**
 * The Class TestSetEvaluatorMedContent.
 */
public class TestSetEvaluatorMedContent 
				extends TestSetEvaluatorMed {

	// ATTRIBUTES
	
	/** The m_enrich strategy. */
	private String m_enrichStrategy = "ContentsA1";
	
	/** The m_ url relation matrix. */
	private String[][] m_UrlRelationMatrix = null;
	
	/** The m_no propose ur ls. */
	private ArrayList<Integer> m_noProposeURLs = new ArrayList<Integer>();
	
	// CREATOR
	
	/**
	 * Instantiates a new test set evaluator med content.
	 *
	 * @param sequences the sequences
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 * @param medoids the medoids
	 * @param gmedoids the gmedoids
	 * @param recos the recos
	 * @param isDistance the is distance
	 * @param rolesW the roles w
	 * @param knn the knn
	 * @param enrichStrategy the enrich strategy
	 * @param m_urlRelationMatrix the m_url relation matrix
	 * @param urlClusteringDict the url clustering dict
	 * @param noProposeURLs the no propose ur ls
	 */
	public TestSetEvaluatorMedContent(
			ArrayList<String[]> sequences,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, 
			float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
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
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min,
				medoids, gmedoids, recos, isDistance, rolesW, knn);
		m_enrichStrategy = enrichStrategy;
		m_UrlRelationMatrix = m_urlRelationMatrix;
		m_urlClusteringDict = urlClusteringDict;
		m_noProposeURLs = noProposeURLs;
	}
	
	// GET SEQUENCE EVALUATOR
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorMed#getSequenceEvaluator(java.lang.String[])
	 */
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorMedContent(
						sequence, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						
						m_medoids, m_gmedoids, m_recos,
						m_isDistance, m_rolesW, m_knn,
						m_enrichStrategy,
						m_UrlRelationMatrix,
						m_urlClusteringDict,
						m_noProposeURLs);
		return seqEva;
	}
	
}
