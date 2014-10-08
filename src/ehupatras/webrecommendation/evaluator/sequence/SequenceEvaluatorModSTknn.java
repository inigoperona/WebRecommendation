package ehupatras.webrecommendation.evaluator.sequence;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderKnnToSuffixTrees2;

public class SequenceEvaluatorModSTknn 
				extends SequenceEvaluatorModST {

	// CREATOR
	
	public SequenceEvaluatorModSTknn(
			String[] sequence,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			
			ArrayList<MySuffixTree> clustSuffixTree,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix,
				clustSuffixTree, 
				medoids, gmedoids, isDistance, rolesW, knn);
	}
	
	// GET RECOMMENDER
	
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderKnnToSuffixTrees2(
						m_medoids, m_gmedoids, 
						m_clustSuffixTree, 
						m_isDistance, m_rolesW, m_knn);
		return recommender;
	}
	
}
