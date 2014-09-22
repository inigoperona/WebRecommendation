package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;



public class RecommenderKnnToClustersTopURLsAndContentsA2B2_Sp2
				extends RecommenderKnnToClustersTopURLsAndContents {

	// CREATOR

	public RecommenderKnnToClustersTopURLsAndContentsA2B2_Sp2(
			ArrayList<String[]> medoids,
			int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid,
			boolean isDistance,
			float[][] rolesW,
			int nURLs,
			float[][] urlSimilarityMatrix,
			String[][] urlRelationMatrix,
			HashMap<Integer,Integer> urlClusteringDict,
			URLconverterUsaCon conv,
			ArrayList<Integer> noProposeURLs){
		super(medoids, globalMedoids, recosForEachMedoid, isDistance, rolesW,
			nURLs, urlSimilarityMatrix, urlRelationMatrix, urlClusteringDict,
			conv,
			noProposeURLs);
	}

	// FUNCTIONS

	public ArrayList<Integer> applyEnrichment(int[] url, int[] urlDone){
		return this.a2_Proposamena(url, urlDone);
	}

	private ArrayList<Integer> a2_Proposamena (int[] url, int[] urlDone)
	{	ArrayList<Integer> recomendations= new ArrayList<Integer>();

		recomendations.add(url[2]);
		recomendations.add(url[3]);


		return recomendations;
	}

}