package angelu.webrecommendation.evaluator;

import java.util.ArrayList;

import ehupatras.webrecommendation.recommender.RecommenderKnnToClustersTopURLs;

public class RecommenderKnnToClustersTopURLsAndContents001 
				extends RecommenderKnnToClustersTopURLs {

	// ATTRIBUTES
	
	private int m_leftZerosLen = 6;
	
	// CREATOR
	
	public RecommenderKnnToClustersTopURLsAndContents001(
			ArrayList<String[]> medoids,
			int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid,
			boolean isDistance,
			float[][] rolesW){
		super(medoids, globalMedoids, recosForEachMedoid, isDistance, rolesW);
	}
	
	// FUNCTIONS
	
	protected ArrayList<String> getNextpossibleSteps(int nRecos){
		
		// All information about the recommendations		
		Object[] objA = this.getNextpossibleSteps_Info(nRecos);
		ArrayList<String> recosL = (ArrayList<String>)objA[0];
		ArrayList<Integer> supportL = (ArrayList<Integer>)objA[1];
		ArrayList<Integer> clustersL = (ArrayList<Integer>)objA[2];
		ArrayList<Float> distsL = (ArrayList<Float>)objA[3];
		
		// print the path done until this moment
		/*
		for(int i=0; i<m_waydone.size(); i++){
			int step = Integer.valueOf(m_waydone.get(i));
			System.out.print(step + ",");
		}
		System.out.println();
		*/
		
		// print all the information of each recommendation
		/*
		System.out.println("---");
		for(int i=0; i<recosL.size(); i++){
			int rec = Integer.valueOf(recosL.get(i));
			int sup = supportL.get(i);
			int cl = clustersL.get(i);
			float d = distsL.get(i);
			System.out.println(recosL.get(i) + " " + sup + " " + cl + " " + d);
		}
		System.out.println("---");
		*/
		
		// enrich/change the list of recommendations
		/*
		// for example, add 200 recommendation
		int recExample = 200;
		String recExampleStr = String.format(
				"%0"+ m_leftZerosLen + "d", recExample);
		recosL.add(recExampleStr);
		*/
		
		// return the new recommendations
		return recosL;
	}
	
}
