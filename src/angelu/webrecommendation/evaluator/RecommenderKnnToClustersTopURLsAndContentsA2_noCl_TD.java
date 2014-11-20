package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

public class RecommenderKnnToClustersTopURLsAndContentsA2_noCl_TD 
				extends RecommenderKnnToClustersTopURLsAndContents {

	// CREATOR
	
	public RecommenderKnnToClustersTopURLsAndContentsA2_noCl_TD(
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
		int[] nearestURL;
		
		recomendations.add(url[2]);
		recomendations.add(url[3]);
		
		if(urlDone.length<=1){
			recomendations.add(url[1]);
			recomendations.add(url[0]);
		} else if(urlDone.length==2){	
			//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
			nearestURL= gertueneko_urla(urlDone[1], 2, true);
			recomendations=gehituUrlEzErrepikatua(urlDone[1], nearestURL, recomendations, true);
		} else if(urlDone.length==3){	
			//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
			nearestURL= gertueneko_urla(urlDone[2], 1, true);
			recomendations=gehituUrlEzErrepikatua(urlDone[2], nearestURL, recomendations, true);
		 
			nearestURL= gertueneko_urla(urlDone[1], 1, true);
			recomendations=gehituUrlEzErrepikatua(urlDone[1], nearestURL, recomendations, true);
		} else {	
			//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
			nearestURL= gertueneko_urla(urlDone[3], 1, true);
			recomendations=gehituUrlEzErrepikatua(urlDone[3], nearestURL, recomendations, true);
		 
			nearestURL= gertueneko_urla(urlDone[2], 1, true);
			recomendations=gehituUrlEzErrepikatua(urlDone[2], nearestURL, recomendations, true);
		}
		
		return recomendations;
	}
	
}
