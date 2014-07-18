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
			URLconverterUsaCon conv){
		super(sequence, medoids, gmedoids, recos, isDistance, rolesW, knn);
		m_enrichStrategy = enrichStrategy;
		m_nURLs = nURLs;
		m_UrlSimilarityMatrix = urlSimilarityMatrix;
		m_UrlRelationMatrix = urlRelationMatrix;
		m_UrlClusteringDict = urlClusteringDict;
		m_conv = conv;
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
								m_conv);
		} else if(m_enrichStrategy.equals("ContentsA2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsA2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv);
		} else if(m_enrichStrategy.equals("ContentsB1")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB1(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv);
		} else if(m_enrichStrategy.equals("ContentsB2")){
			recommender = new RecommenderKnnToClustersTopURLsAndContentsB2(
					m_medoids, m_gmedoids, m_recos,
					m_isDistance, m_rolesW,
					m_nURLs,
					m_UrlSimilarityMatrix,
					m_UrlRelationMatrix,
					m_UrlClusteringDict,
					m_conv);
		}
		
		return recommender;
	}
	
}
