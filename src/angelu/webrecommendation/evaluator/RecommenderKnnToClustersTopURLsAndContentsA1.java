package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.webrecommendation.recommender.RecommenderKnnToClustersTopURLs;


public class RecommenderKnnToClustersTopURLsAndContentsA1 
				extends RecommenderKnnToClustersTopURLsAndContents {
	
	// CREATOR
	
	public RecommenderKnnToClustersTopURLsAndContentsA1(
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
		return this.a1_Proposamena(url, urlDone);
	}
	
	private ArrayList<Integer> a1_Proposamena(int[] url, int[] urlDone){ 
		ArrayList<Integer> recomendations= new ArrayList<Integer>();
		int[] nearestURL;
		int[] n_relation=number_of_Relation(url,true);
		
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
				for(int i=0; i<=nearestURL.length-1;i++)
				{recomendations.add(nearestURL[i]);}}
			else
			{	//nearest urls URL3,URL4
				nearestURL= gertueneko_urla(urlDone[1], 2, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}	
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[0], 2, false);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}}
			
			return recomendations;}
	
		else if (urlDone.length==3)
		{	if(n_relation[0] >=n_relation[1])
			{//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[2], 2, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}
				nearestURL= gertueneko_urla(urlDone[1], 2, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}}
			else
			{ 	//nearest urls SPURL4
				nearestURL= gertueneko_urla(urlDone[2], 1, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}	
				//nearest urls SPURL3
				nearestURL= gertueneko_urla(urlDone[1], 1, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[0], 2, false);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}}
		
			return recomendations;}
		
		else
		{	if(n_relation[0] >=n_relation[1])
			{	//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[3], 2, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}
				nearestURL= gertueneko_urla(urlDone[2], 2, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}}
		 
			else
			{ 	//nearest urls SPURL4
				nearestURL= gertueneko_urla(urlDone[3], 1, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}	
				//nearest urls SPURL3
				nearestURL= gertueneko_urla(urlDone[2], 1, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}	
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[1], 1, false);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}	
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[0], 1, false);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}}
		
			return recomendations;}
		
	}
	
}
