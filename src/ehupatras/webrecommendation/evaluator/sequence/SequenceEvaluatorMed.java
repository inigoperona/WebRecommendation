package ehupatras.webrecommendation.evaluator.sequence;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderKnnToClustersTopURLs;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceEvaluatorMed.
 */
public class SequenceEvaluatorMed 
				extends SequenceEvaluator {

	// ATTRIBUTES
	
	/** The m_medoids. */
	protected ArrayList<String[]> m_medoids = null;
	protected int[] m_clustersizes = null;
	
	/** The m_gmedoids. */
	protected int[] m_gmedoids = null;
	
	/** The m_recos. */
	protected ArrayList<Object[]> m_recos = null;
	
	/** The m_is distance. */
	protected boolean m_isDistance = true;
	
	/** The m_roles w. */
	protected float[][] m_rolesW = {{ 0f, 0f, 0f},
									{ 0f, 0f, 0f},
									{ 0f, 0f, 0f}};
	
	/** The m_knn. */
	protected int m_knn = 100;
	
	// CREATOR
	
	/**
	 * Instantiates a new sequence evaluator med.
	 *
	 * @param sequence the sequence
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
	 */
	public SequenceEvaluatorMed(
			String[] sequence,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			ArrayList<String[]> medoids, int[] clustersizes, int[] gmedoids,
			ArrayList<Object[]> recos,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
		m_medoids = medoids;
		m_clustersizes = clustersizes;
		m_gmedoids = gmedoids;
		m_recos = recos;
		m_isDistance = isDistance;
		m_rolesW = rolesW;
		m_knn = knn;
	}
	
	/**
	 * Instantiates a new sequence evaluator med.
	 *
	 * @param sequence the sequence
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 */
	protected SequenceEvaluatorMed(
			String[] sequence,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
	}
	
	/**
	 * Instantiates a new sequence evaluator med.
	 *
	 * @param sequence the sequence
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 * @param medoids the medoids
	 * @param gmedoids the gmedoids
	 * @param isDistance the is distance
	 * @param rolesW the roles w
	 * @param knn the knn
	 */
	protected SequenceEvaluatorMed(
			String[] sequence,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage,
			float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			
			ArrayList<String[]> medoids, int[] clustersizes, int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
		m_medoids = medoids;
		m_clustersizes = clustersizes;
		m_gmedoids = gmedoids;
		m_isDistance = isDistance;
		m_rolesW = rolesW;
		m_knn = knn;
	}
	
	// GET RECOMMENDER
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator#getRecommender()
	 */
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderKnnToClustersTopURLs(
						m_medoids, m_clustersizes, m_gmedoids, 
						m_recos, m_isDistance, m_rolesW);
		return recommender;
	}
	
}
