package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

public class RecommenderKnnToClustersTopURLsAndContentsA2v2_SpOrder 
				extends RecommenderKnnToClustersTopURLsAndContentsA2v2 {

	// CREATOR
	
	public RecommenderKnnToClustersTopURLsAndContentsA2v2_SpOrder(
			ArrayList<String[]> medoids,
			int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid,
			boolean isDistance,
			float[][] rolesW,
			int nURLs,
			float[][] urlSimilarityMatrix,
			String[][] urlRelationMatrix,
			HashMap<Integer,Integer> urlClusteringDict,
			URLconverterUsaCon conv){
		super(medoids, globalMedoids, recosForEachMedoid, isDistance, rolesW,
				nURLs, urlSimilarityMatrix, urlRelationMatrix, urlClusteringDict,
				conv);
	}
	
	public ArrayList<Integer> orderRecommendations(final float[] supports, ArrayList<String> recos){
		return super.orderRecommendations_SpOrder(supports, recos);
	}
	
}
