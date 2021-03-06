package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderKnnToClustersTopURLsAndContentsB2_SpOrder.
 */
public class RecommenderKnnToClustersTopURLsAndContentsB2_SpOrder 
				extends RecommenderKnnToClustersTopURLsAndContentsB2{

	// CREATOR
	
	/**
	 * Instantiates a new recommender knn to clusters top ur ls and contents b2_ sp order.
	 *
	 * @param medoids the medoids
	 * @param globalMedoids the global medoids
	 * @param recosForEachMedoid the recos for each medoid
	 * @param isDistance the is distance
	 * @param rolesW the roles w
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlRelationMatrix the url relation matrix
	 * @param urlClusteringDict the url clustering dict
	 * @param conv the conv
	 * @param noProposeURLs the no propose ur ls
	 */
	public RecommenderKnnToClustersTopURLsAndContentsB2_SpOrder(
			ArrayList<String[]> medoids, int[] clustersizes, int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid,
			boolean isDistance,
			float[][] rolesW,
			int nURLs,
			float[][] urlSimilarityMatrix,
			String[][] urlRelationMatrix,
			HashMap<Integer,Integer> urlClusteringDict,
			URLconverterUsaCon conv,
			ArrayList<Integer> noProposeURLs){
		super(medoids, clustersizes, globalMedoids, recosForEachMedoid, isDistance, rolesW,
				nURLs, urlSimilarityMatrix, urlRelationMatrix, urlClusteringDict,
				conv,
				noProposeURLs);
	}
	
	/* (non-Javadoc)
	 * @see angelu.webrecommendation.evaluator.RecommenderKnnToClustersTopURLsAndContents#orderRecommendations(float[], java.util.ArrayList)
	 */
	public ArrayList<Integer> orderRecommendations(final float[] supports, ArrayList<String> recos){
		return super.orderRecommendations_SpOrder(supports, recos);
	}
	
}
