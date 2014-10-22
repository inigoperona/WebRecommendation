package ehupatras.webrecommendation.recommender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import ehupatras.suffixtree.stringarray.myst.MySuffixTree;

public class RecommenderKnnToSuffixTrees2 implements Recommender {

	private ArrayList<String> m_waydone = new ArrayList<String>();
	
	private RecommenderClustersSuffixTree2 m_recClustersSuffixTree;
	private RecommenderKnnToClustersTopURLs m_recKnnToClustersTopURLs;
	
	private int m_knn = 100;
	private int m_0recosClusters = 0;
	private boolean m_isDistance = true;
	private float[][] m_rolesW = {{ 0f, 0f, 0f},
								  { 0f, 0f, 0f},
								  { 0f, 0f, 0f}};
	
	public RecommenderKnnToSuffixTrees2(ArrayList<String[]> medoids,
				int[] globalMedoids,
				ArrayList<MySuffixTree> stAL,
				boolean isDistance,
				float[][] rolesW,
				int knn){
		m_waydone = new ArrayList<String>();
		m_recKnnToClustersTopURLs = new RecommenderKnnToClustersTopURLs(medoids, globalMedoids, null, isDistance, rolesW);
		m_recClustersSuffixTree = new RecommenderClustersSuffixTree2(stAL);
		m_knn = Math.min(medoids.size(), knn);
	}
	
	public void reset(){
		m_waydone = new ArrayList<String>();
		m_recKnnToClustersTopURLs.reset();
		m_recClustersSuffixTree.reset();
	}
	
	public int getNumberOfFailures(){
		return m_0recosClusters;
	}
	
	// find the nearest clusters
	public ArrayList<String> update(ArrayList<String> waydone, String laststep, 
			boolean incrWeigh, boolean performFailureFunction){
		m_recKnnToClustersTopURLs.update(waydone, laststep, incrWeigh, performFailureFunction);
		m_recClustersSuffixTree.update(waydone, laststep, incrWeigh, performFailureFunction);
		m_waydone.add(laststep);
		return m_waydone;
	}
	
	
	// mix evenly
	private Object[] getNextpossibleSteps(){
		// medoids ordered from the nearest to farthest
		Object[] objAa = m_recKnnToClustersTopURLs.knnSim();
		int[] orderedMedoids = (int[])objAa[0];
		float[] orderedSims = (float[])objAa[1]; // it can be null
		
		// for each medoid take the recommendations
		boolean[] validClusters = new boolean[orderedMedoids.length];
		Arrays.fill(validClusters, false);
		for(int i=0; i<orderedMedoids.length && i<m_knn; i++){
			int nearesCl = orderedMedoids[i];
			validClusters[nearesCl] = true;
		}
		
		// get the recommendations
		m_recClustersSuffixTree.setValidSTs(validClusters);
		Object[] objA = m_recClustersSuffixTree.getNextpossibleSteps();
		//ArrayList<String> listOfURLs = (ArrayList<String>)objA[0];
		//ArrayList<Float> listOfWeights = (ArrayList<Float>)objA[1];
		
		
		// return the list of recommendations
		return objA;
	}
	
	public ArrayList<String> getNextpossibleStepsUnbounded(){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> listOfUrls = (ArrayList<String>)objA[0];
		return listOfUrls;
	}
	
	public ArrayList<String> getNextpossibleStepsRandom(int nReco, long seed){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> list = (ArrayList<String>)objA[0];
		int realNreco = Math.min(nReco, list.size());
		ArrayList<String> list2 = new ArrayList<String>(); 
		Random rand = new Random(seed);
		for(int i=0; i<realNreco; i++){
			int pos = rand.nextInt(list.size());
			list2.add(list.get(pos));
		}
		return list2;
	}
	
	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone){
		// get the possible URLs in the actual position
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> list = (ArrayList<String>)objA[0];
		ArrayList<Float> weig = (ArrayList<Float>)objA[1];
		
		// get the most weighted URLs
		ArrayList<String> listOfURLs = RecommenderSuffixTree2.getTheMostWeightedFloatURLs(nRecos, list, weig);
		if(listOfURLs.size()>=nRecos){
			return listOfURLs;
		}
		
		// else add URLs from step1
		if(waydone==null || waydone.size()==0){ return listOfURLs; }
		// add step1 recommendations
		Object[] objA2 = m_recClustersSuffixTree.getStep1Recommendations(listOfURLs);
		ArrayList<String> listOfURLsStep1 = (ArrayList<String>)objA2[0];
		ArrayList<Float> listOfWeightsStep1 = (ArrayList<Float>)objA2[1];
		ArrayList<String> addlist = RecommenderSuffixTree2.getTheMostWeightedFloatURLs(nRecos-listOfURLs.size(), listOfURLsStep1, listOfWeightsStep1);
		for(int i=0; i<addlist.size(); i++){
			listOfURLs.add(addlist.get(i));
		}
		
		// return
		return listOfURLs;
	}
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nrecos){
		return this.getNextpossibleStepsWeighted(nrecos, null);
	}
	public ArrayList<String> getNextpossibleStepsMarkov(int nRecos, ArrayList<String> waydone, ArrayList<String> listMarkov){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nRecos){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	public ArrayList<String> getNextpossibleStepsWeightedEnrichWithStep1(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	
}
