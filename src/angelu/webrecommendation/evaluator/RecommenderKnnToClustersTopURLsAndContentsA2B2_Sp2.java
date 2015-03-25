package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;



// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderKnnToClustersTopURLsAndContentsA2B2_Sp2.
 */
public class RecommenderKnnToClustersTopURLsAndContentsA2B2_Sp2
				extends RecommenderKnnToClustersTopURLsAndContents {

	// CREATOR

	/**
	 * Instantiates a new recommender knn to clusters top ur ls and contents a2 b2_ sp2.
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
	public RecommenderKnnToClustersTopURLsAndContentsA2B2_Sp2(
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

	// FUNCTIONS

	/* (non-Javadoc)
	 * @see angelu.webrecommendation.evaluator.RecommenderKnnToClustersTopURLsAndContents#applyEnrichment(int[], int[])
	 */
	public ArrayList<Integer> applyEnrichment(int[] url, int[] urlDone){
		return this.a2_Proposamena(url, urlDone);
	}

	/**
	 * A2_ proposamena.
	 *
	 * @param url the url
	 * @param urlDone the url done
	 * @return the array list
	 */
	private ArrayList<Integer> a2_Proposamena (int[] url, int[] urlDone)
	{	ArrayList<Integer> recomendations= new ArrayList<Integer>();

		recomendations.add(url[2]);
		recomendations.add(url[3]);


		return recomendations;
	}

}