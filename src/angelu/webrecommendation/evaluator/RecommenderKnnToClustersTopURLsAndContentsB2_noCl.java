package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderKnnToClustersTopURLsAndContentsB2_noCl.
 */
public class RecommenderKnnToClustersTopURLsAndContentsB2_noCl 
				extends RecommenderKnnToClustersTopURLsAndContents {

	// CREATOR
	
	/**
	 * Instantiates a new recommender knn to clusters top ur ls and contents b2_no cl.
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
	public RecommenderKnnToClustersTopURLsAndContentsB2_noCl(
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
		recomendations.add(url[2]);
		recomendations.add(url[3]);
		
		if(n_relation[0] >=n_relation[1])
		{	//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
			nearestURL= gertueneko_urla(url[3], 1, true);
			recomendations=gehituUrlEzErrepikatua(url[3], nearestURL, recomendations, true);
		 
			nearestURL= gertueneko_urla(url[2], 1, true);
			recomendations=gehituUrlEzErrepikatua(url[2], nearestURL, recomendations, true);}
		 
		else
		{	//nearest urls SPURL2
			nearestURL= gertueneko_urla(url[1], 1, true);
			recomendations=gehituUrlEzErrepikatua(url[1], nearestURL, recomendations, true);	
			//nearest urls SPURL2
			nearestURL= gertueneko_urla(url[0], 1, true);
			recomendations=gehituUrlEzErrepikatua(url[0], nearestURL, recomendations, true);}
		
		return recomendations;
	}
	
}
