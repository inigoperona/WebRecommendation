package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderKnnToClustersTopURLsAndContentsA2v2.
 */
public class RecommenderKnnToClustersTopURLsAndContentsA2v2 
				extends RecommenderKnnToClustersTopURLsAndContents {

	// CREATOR
	
	/**
	 * Instantiates a new recommender knn to clusters top ur ls and contents a2v2.
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
	public RecommenderKnnToClustersTopURLsAndContentsA2v2(
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
		int[] nearestURL;
		
		int[] n_relation=number_of_Relation(urlDone,false);
		recomendations.add(url[2]);
		recomendations.add(url[3]);
		
		if(urlDone.length<=1)
		{	recomendations.add(url[1]);
			recomendations.add(url[0]);}
		
		else if(urlDone.length==2)
		{	if(n_relation[0] >=n_relation[1])
			{	//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[1], 2, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[1], nearestURL, recomendations, true);}
		 
			else
			{	//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[0], 2, false);
				recomendations=gehituUrlEzErrepikatua(urlDone[0], nearestURL, recomendations, false);}}
	
		else if(urlDone.length==3)
		{	if(n_relation[0] >=n_relation[1])
			{	//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[2], 1, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[2], nearestURL, recomendations, true);
		 
				nearestURL= gertueneko_urla(urlDone[1], 1, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[1], nearestURL, recomendations, true);}

			else
			{	//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[2], 2, false);
				recomendations=gehituUrlEzErrepikatua(urlDone[2], nearestURL, recomendations, false);}}
		
		else
		{	if(n_relation[0] >=n_relation[1])
			{	//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[3], 1, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[3], nearestURL, recomendations, true);
		 
				nearestURL= gertueneko_urla(urlDone[2], 1, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[2], nearestURL, recomendations, true);}
		 
			else
			{	//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[3], 1, false);
				recomendations=gehituUrlEzErrepikatua(urlDone[3], nearestURL, recomendations, false);
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[2], 1, false);
				recomendations=gehituUrlEzErrepikatua(urlDone[2], nearestURL, recomendations, false);}}
		
		
		return recomendations;
	}
	
}
