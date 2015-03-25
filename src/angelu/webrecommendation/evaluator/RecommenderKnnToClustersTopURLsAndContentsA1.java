package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;
import angelu.webrecommendation.converter.URLconverterUsaCon;


// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderKnnToClustersTopURLsAndContentsA1.
 */
public class RecommenderKnnToClustersTopURLsAndContentsA1 
				extends RecommenderKnnToClustersTopURLsAndContents {
	
	// CREATOR
	
	/**
	 * Instantiates a new recommender knn to clusters top ur ls and contents a1.
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
	public RecommenderKnnToClustersTopURLsAndContentsA1(
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
		return this.a1_Proposamena(url, urlDone);
	}
	
	/**
	 * A1_ proposamena.
	 *
	 * @param url the url
	 * @param urlDone the url done
	 * @return the array list
	 */
	private ArrayList<Integer> a1_Proposamena(int[] url, int[] urlDone){ 
		ArrayList<Integer> recomendations= new ArrayList<Integer>();
		int[] nearestURL;
		int[] n_relation=number_of_Relation(urlDone,false);
		
		if(urlDone.length<=1){
			for(int i=0; i<url.length; i++){
				recomendations.add(url[i]);
			}
			return recomendations;
		}
		
		else if(urlDone.length==2)
		{
			if(n_relation[0] >=n_relation[1])
			{//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[1], 4, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[1], nearestURL, recomendations, true);}
			else
			{	//nearest urls URL3,URL4
				nearestURL= gertueneko_urla(urlDone[1], 2, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[1], nearestURL, recomendations, true);	
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[0], 2, false);
				recomendations=gehituUrlEzErrepikatua(urlDone[0], nearestURL, recomendations, false);}
		
			return recomendations;}
	
		else if (urlDone.length==3)
		{	if(n_relation[0] >=n_relation[1])
			{//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[2], 2, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[2], nearestURL, recomendations, true);
				nearestURL= gertueneko_urla(urlDone[1], 2, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[1], nearestURL, recomendations, true);
			}
			else
			{ 	//nearest urls SPURL4
				nearestURL= gertueneko_urla(urlDone[2], 1, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[2], nearestURL, recomendations, true);	
				//nearest urls SPURL3
				nearestURL= gertueneko_urla(urlDone[1], 1, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[1], nearestURL, recomendations, true);
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[0], 2, false);
				recomendations=gehituUrlEzErrepikatua(urlDone[0], nearestURL, recomendations, false);
			}
		
			return recomendations;}
		
		else
		{	if(n_relation[0] >=n_relation[1])
			{	//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[3], 2, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[3], nearestURL, recomendations, true);
				nearestURL= gertueneko_urla(urlDone[2], 2, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[2], nearestURL, recomendations, true);}
		 
			else
			{ 	//nearest urls SPURL4
				nearestURL= gertueneko_urla(urlDone[3], 1, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[3], nearestURL, recomendations, true);
				//nearest urls SPURL3
				nearestURL= gertueneko_urla(urlDone[2], 1, true);
				recomendations=gehituUrlEzErrepikatua(urlDone[2], nearestURL, recomendations, true);
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[1], 1, false);
				recomendations=gehituUrlEzErrepikatua(urlDone[1], nearestURL, recomendations, false);
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[0], 1, false);
				recomendations=gehituUrlEzErrepikatua(urlDone[0], nearestURL, recomendations, false);}
		
			return recomendations;}
		
	}
	
}
