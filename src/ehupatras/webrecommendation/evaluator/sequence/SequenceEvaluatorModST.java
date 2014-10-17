package ehupatras.webrecommendation.evaluator.sequence;

import java.util.ArrayList;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderClustersSuffixTree2;

public class SequenceEvaluatorModST 
				extends SequenceEvaluatorMed {

	// ATTRIBUTES
	
	protected ArrayList<MySuffixTree> m_clustSuffixTree = null;
	
	// CREATOR
	
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
	
	protected SequenceEvaluatorModST(
			String[] sequence,
			int modePrRe,
			URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min,
			
			ArrayList<MySuffixTree> clustSuffixTree,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequence, modePrRe, conv, nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min,
				medoids, gmedoids, isDistance, rolesW, knn);
		m_clustSuffixTree = clustSuffixTree;
	}
	
	// GET RECOMMENDER
	
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderClustersSuffixTree2(m_clustSuffixTree);
		return recommender;
	}
	
}
