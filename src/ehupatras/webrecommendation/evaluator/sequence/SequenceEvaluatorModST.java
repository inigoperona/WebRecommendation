package ehupatras.webrecommendation.evaluator.sequence;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderClustersSuffixTree2;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceEvaluatorModST.
 */
public class SequenceEvaluatorModST 
				extends SequenceEvaluatorMed {

	// ATTRIBUTES
	
	/** The m_clust suffix tree. */
	protected ArrayList<MySuffixTree> m_clustSuffixTree = null;
	
	// CREATOR
	
	/**
	 * Instantiates a new sequence evaluator mod st.
	 *
	 * @param sequence the sequence
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 * @param clustSuffixTree the clust suffix tree
	 */
	public SequenceEvaluatorModST(
			String[] sequence,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			ArrayList<MySuffixTree> clustSuffixTree){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix,
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
		m_clustSuffixTree = clustSuffixTree;
	}
	
	/**
	 * Instantiates a new sequence evaluator mod st.
	 *
	 * @param sequence the sequence
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
	protected SequenceEvaluatorModST(
			String[] sequence,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			ArrayList<MySuffixTree> clustSuffixTree,
			ArrayList<String[]> medoids, int[] clustersizes, int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min,
				medoids, clustersizes, gmedoids, isDistance, rolesW, knn);
		m_clustSuffixTree = clustSuffixTree;
	}
	
	// GET RECOMMENDER
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorMed#getRecommender()
	 */
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderClustersSuffixTree2(m_clustSuffixTree);
		return recommender;
	}
	
}
