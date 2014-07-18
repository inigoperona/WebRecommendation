package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

public class RecommenderKnnToClustersTopURLsAndContentsB2 
				extends RecommenderKnnToClustersTopURLsAndContents {

	// CREATOR
	
	public RecommenderKnnToClustersTopURLsAndContentsB2(
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
	
	// FUNCTIONS
	
	public ArrayList<Integer> applyEnrichment(int[] url, int[] urlDone){
		return this.b2_Proposamena(url);
	}
	
	private ArrayList<Integer> b2_Proposamena(int[] url)
	{	ArrayList<Integer> recomendations= new ArrayList<Integer>();
		int[] nearestURL;
	
		int[] n_relation=number_of_Relation(url,true);
		recomendations.add(url[2]);
		recomendations.add(url[3]);
		
		if(n_relation[0] >=n_relation[1])
		{	//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
			nearestURL= gertueneko_urla(url[3], 1, true);
			for(int i=0; i<=nearestURL.length-1;i++){
				recomendations.add(nearestURL[i]);}
		 
			nearestURL= gertueneko_urla(url[2], 1, true);
			for(int i=0; i<=nearestURL.length-1;i++){
				recomendations.add(nearestURL[i]);}}
		 
		else
		{	//nearest urls SPURL2
			nearestURL= gertueneko_urla(url[1], 1, false);
			for(int i=0; i<=nearestURL.length-1;i++){
				recomendations.add(nearestURL[i]);}	
			//nearest urls SPURL2
			nearestURL= gertueneko_urla(url[0], 1, false);
			for(int i=0; i<=nearestURL.length-1;i++){
				recomendations.add(nearestURL[i]);}}
		
		return recomendations;
	}
	
}
