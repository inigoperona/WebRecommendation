package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

public class RecommenderKnnToClustersTopURLsAndContentsB1_noCl_UserView_TopM 
				extends RecommenderKnnToClustersTopURLsAndContents {

	// CREATOR
	
	public RecommenderKnnToClustersTopURLsAndContentsB1_noCl_UserView_TopM(
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
		return this.b1_Proposamena(url,urlDone);
	}
	
	private ArrayList<Integer> b1_Proposamena(int[] url,int[] urlDone)
	{	ArrayList<Integer> recomendations= new ArrayList<Integer>();
		int[] nearestURL;
	
		int[] n_relation=number_of_Topics(urlDone,false);
		
		if(n_relation[0] >=n_relation[1])
		{	//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
			nearestURL= gertueneko_urla(url[3], 2, true);
			recomendations=gehituUrlEzErrepikatua(url[3], nearestURL, recomendations, true);		 
			nearestURL= gertueneko_urla(url[2], 2, true);
			recomendations=gehituUrlEzErrepikatua(url[2], nearestURL, recomendations, true);}
		
		else
		{ 	//nearest urls SPURL4
			nearestURL= gertueneko_urla(url[3], 1, true);
			recomendations=gehituUrlEzErrepikatua(url[3], nearestURL, recomendations, true);
			//nearest urls SPURL3
			nearestURL= gertueneko_urla(url[2], 1, true);
			recomendations=gehituUrlEzErrepikatua(url[2], nearestURL, recomendations, true);
			//nearest urls SPURL2
			nearestURL= gertueneko_urla(url[1], 1, true);
			recomendations=gehituUrlEzErrepikatua(url[1], nearestURL, recomendations, true);	
			//nearest urls SPURL2
			nearestURL= gertueneko_urla(url[0], 1, true);
			recomendations=gehituUrlEzErrepikatua(url[0], nearestURL, recomendations, true);}

		return recomendations;
	}
	
}
