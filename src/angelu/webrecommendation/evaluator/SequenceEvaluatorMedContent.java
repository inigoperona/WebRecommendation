package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorMed;
import ehupatras.webrecommendation.recommender.Recommender;

public class SequenceEvaluatorMedContent 
				extends SequenceEvaluatorMed {

	// ATTRIBUTES
	
	private String m_enrichStrategy = "ContentsA1";
	private int m_nURLs = 0;
	private float[][] m_UrlSimilarityMatrix = null;
	private String[][] m_UrlRelationMatrix = null;
	private HashMap<Integer,Integer> m_UrlClusteringDict = null;
	private URLconverterUsaCon m_conv = null;
	private ArrayList<Integer> m_noProposeURLs = new ArrayList<Integer>();
	
	// CREATOR
	
	public SequenceEvaluatorMedContent(
			String[] sequence,
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
		super(sequence, medoids, gmedoids, recos, isDistance, rolesW, knn);
		m_enrichStrategy = enrichStrategy;
		m_nURLs = nURLs;
		m_UrlSimilarityMatrix = urlSimilarityMatrix;
		m_UrlRelationMatrix = urlRelationMatrix;
		m_UrlClusteringDict = urlClusteringDict;
		m_conv = conv;
		m_noProposeURLs = noProposeURLs;
	}
	
	// GET RECOMMENDER
	
	public Recommender getRecommender(){
		Recommender recommender = null;
		
		if(m_enrichStrategy.equals("ContentsA1")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA1(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2v2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2v2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB1")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB1(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2v2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2v2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
			
		} else if(m_enrichStrategy.equals("ContentsA1_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA1_SpOrder(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2_SpOrder(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2v2_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2v2_SpOrder(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB1_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB1_SpOrder(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_SpOrder(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2v2_SpOrder")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2v2_SpOrder(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
			
		} else if(m_enrichStrategy.equals("ContentsB2_TD")){
					recommender = new RecommenderKnnToClustersTopURLsAndContentsB2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		
		} else if(m_enrichStrategy.equals("ContentsA2B2_Sp2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2B2_Sp2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2_En2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2v2_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2v2_En2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_En2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2v2_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2v2_En2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
			
		} else if(m_enrichStrategy.equals("ContentsA2_noCl")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2_noCl(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2v2_noCl")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2v2_noCl(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_noCl")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_noCl(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2v2_noCl")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2v2_noCl(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2_noCl_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2_noCl_En2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsA2v2_noCl_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2v2_noCl_En2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2_noCl_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2_noCl_En2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		} else if(m_enrichStrategy.equals("ContentsB2v2_noCl_En2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2v2_noCl_En2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv,
					m_noProposeURLs);
		}
		
		else if(m_enrichStrategy.equals("Top02_1")){
			recommender = new RecommenderTop02_1(
			m_medoids, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top03_1")){
			recommender = new RecommenderTop03_1(
			m_medoids, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top04_1")){
			recommender = new RecommenderTop04_1(
			m_medoids, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top05_1")){
			recommender = new RecommenderTop05_1(
			m_medoids, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top06_1")){
			recommender = new RecommenderTop06_1(
			m_medoids, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top07_1")){
			recommender = new RecommenderTop07_1(
			m_medoids, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		} else if(m_enrichStrategy.equals("Top16_1")){
			recommender = new RecommenderTop16_1(
			m_medoids, m_gmedoids, m_recos,
			m_isDistance, m_rolesW);
		}
		
		return recommender;
	}
	
}
