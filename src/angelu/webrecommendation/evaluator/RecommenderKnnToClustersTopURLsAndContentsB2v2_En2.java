package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderKnnToClustersTopURLsAndContentsB2v2_En2.
 */
public class RecommenderKnnToClustersTopURLsAndContentsB2v2_En2
				extends RecommenderKnnToClustersTopURLsAndContents {

	// CREATOR

	/**
	 * Instantiates a new recommender knn to clusters top ur ls and contents b2v2_ en2.
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
	public RecommenderKnnToClustersTopURLsAndContentsB2v2_En2(
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
		return this.b2_Proposamena(url);
	}

	/**
	 * B2_ proposamena.
	 *
	 * @param url the url
	 * @return the array list
	 */
	private ArrayList<Integer> b2_Proposamena(int[] url)
	{	ArrayList<Integer> recomendations= new ArrayList<Integer>();
		int[] nearestURL;

		int[] n_relation=number_of_Relation(url,true);

		if(n_relation[0] >=n_relation[1])
		{	//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
			nearestURL= gertueneko_urla(url[3], 1, true);
			recomendations=gehituUrlEzErrepikatua(url[3], nearestURL, recomendations, true);

			nearestURL= gertueneko_urla(url[2], 1, true);
			recomendations=gehituUrlEzErrepikatua(url[2], nearestURL, recomendations, true);}

		else
		{	//nearest urls SPURL2
			nearestURL= gertueneko_urla(url[3], 1, false);
			recomendations=gehituUrlEzErrepikatua(url[3], nearestURL, recomendations, false);	
			//nearest urls SPURL2
			nearestURL= gertueneko_urla(url[2], 1, false);
			recomendations=gehituUrlEzErrepikatua(url[2], nearestURL, recomendations, false);}

		return recomendations;
	}
	
}
