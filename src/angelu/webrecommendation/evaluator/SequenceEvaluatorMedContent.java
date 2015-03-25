package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorMed;
import ehupatras.webrecommendation.recommender.Recommender;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceEvaluatorMedContent.
 */
public class SequenceEvaluatorMedContent 
				extends SequenceEvaluatorMed {

	// ATTRIBUTES
	
	/** The m_enrich strategy. */
	private String m_enrichStrategy = "ContentsA1";
	
	/** The m_n ur ls. */
	private int m_nURLs = 0;
	
	/** The m_ url relation matrix. */
	private String[][] m_UrlRelationMatrix = null;
	
	/** The m_ url clustering dict. */
	private HashMap<Integer,Integer> m_UrlClusteringDict = null;
	
	// CREATOR
	
	/**
	 * Instantiates a new sequence evaluator med content.
	 *
	 * @param sequence the sequence
	 * @param m_modePrRe the m_mode pr re
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
	 * @param urlRelationMatrix the url relation matrix
	 * @param urlClusteringDict the url clustering dict
	 * @param noProposeURLs the no propose ur ls
	 */
	public SequenceEvaluatorMedContent(
			String[] sequence,
			int m_modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			ArrayList<String[]> medoids, int[] clustersizes, int[] gmedoids,
			ArrayList<Object[]> recos,
			boolean isDistance,
			float[][] rolesW,
			int knn,
			String enrichStrategy,
			String[][] urlRelationMatrix,
			HashMap<Integer,Integer> urlClusteringDict,
			ArrayList<Integer> noProposeURLs){
		super(sequence, m_modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min,
				medoids, clustersizes, 
				gmedoids, recos, isDistance, rolesW, knn);
		m_enrichStrategy = enrichStrategy;
		m_nURLs = nURLs;
		m_UrlSimilarityMatrix_Content = urlSimilarityMatrix;
		m_UrlRelationMatrix = urlRelationMatrix;
		m_UrlClusteringDict = urlClusteringDict;
		m_conv = conv;
		m_noProposeURLs = noProposeURLs;
	}
	
	
	// GET RECOMMENDER
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorMed#getRecommender()
	 */
	public Recommender getRecommender(){
		Recommender recommender = null;
		
		
		
		if(m_enrichStrategy.equals("ContentsA1")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA1(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2v2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2v2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB1")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB1(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2v2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2v2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
			
			
			
		} else if(m_enrichStrategy.equals("ContentsA1_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA1_SpOrder(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2_SpOrder(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2v2_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2v2_SpOrder(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB1_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB1_SpOrder(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_SpOrder(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2v2_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2v2_SpOrder(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
			
			
		
		} else if(m_enrichStrategy.equals("ContentsA2B2_Sp2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2B2_Sp2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2_En2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2v2_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2v2_En2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_En2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2v2_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2v2_En2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
			
			
			
		} else if(m_enrichStrategy.equals("ContentsA1_noCl")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA1_noCl(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2_noCl")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2_noCl(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2v2_noCl")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2v2_noCl(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB1_noCl")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB1_noCl(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_noCl")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_noCl(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2v2_noCl")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2v2_noCl(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
			
		} else if(m_enrichStrategy.equals("ContentsA2_noCl_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2_noCl_En2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2v2_noCl_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2v2_noCl_En2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_noCl_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_noCl_En2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2v2_noCl_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2v2_noCl_En2(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
			
			
			
		} else if(m_enrichStrategy.equals("ContentsA2_noCl_TD")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2_noCl_TD(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_noCl_TD")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_noCl_TD(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB1_noCl_UserView")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB1_noCl_UserView(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_noCl_UserView")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_noCl_UserView(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
			
			
		} else if(m_enrichStrategy.equals("ContentsA1_noCl_TopM")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA1_noCl_TopM(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2_noCl_TopM")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2_noCl_TopM(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB1_noCl_TopM")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB1_noCl_TopM(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB1_noCl_UserView_TopM")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB1_noCl_UserView_TopM(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_noCl_TopM")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_noCl_TopM(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_noCl_UserView_TopM")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_noCl_UserView_TopM(
					m_medoids, m_clustersizes, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix_Content,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		}		
				
		
		else if(m_enrichStrategy.equals("Top02_1")){
			recommender = new RecommenderTop02_1(
			m_medoids, m_clustersizes, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top03_1")){
			recommender = new RecommenderTop03_1(
			m_medoids, m_clustersizes, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top04_1")){
			recommender = new RecommenderTop04_1(
			m_medoids, m_clustersizes, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top05_1")){
			recommender = new RecommenderTop05_1(
			m_medoids, m_clustersizes, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top06_1")){
			recommender = new RecommenderTop06_1(
			m_medoids, m_clustersizes, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top07_1")){
			recommender = new RecommenderTop07_1(
			m_medoids, m_clustersizes, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top16_1")){
			recommender = new RecommenderTop16_1(
			m_medoids, m_clustersizes, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		}
		
		return recommender;
	}
	
	
}
