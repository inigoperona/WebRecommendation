package ehupatras.webrecommendation.recommender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderKnnToSuffixTrees.
 */
public class RecommenderKnnToSuffixTrees 
				implements Recommender {
	
	/** The m_waydone. */
	private ArrayList<String> m_waydone = new ArrayList<String>();
	
	/** The m_rec clusters suffix tree. */
	private RecommenderClustersSuffixTree m_recClustersSuffixTree;
	
	/** The m_rec knn to clusters top ur ls. */
	private RecommenderKnnToClustersTopURLs m_recKnnToClustersTopURLs;
	
	/** The m_knn. */
	private int m_knn = 100;
	
	/** The m_0recos clusters. */
	private int m_0recosClusters = 0;
	
	/** The m_is distance. */
	private boolean m_isDistance = true;
	
	/** The m_roles w. */
	private float[][] m_rolesW = {{ 0f, 0f, 0f},
								  { 0f, 0f, 0f},
								  { 0f, 0f, 0f}};
	
	/**
	 * Instantiates a new recommender knn to suffix trees.
	 *
	 * @param medoids the medoids
	 * @param globalMedoids the global medoids
	 * @param stAL the st al
	 * @param isDistance the is distance
	 * @param rolesW the roles w
	 * @param knn the knn
	 */
	public RecommenderKnnToSuffixTrees(ArrayList<String[]> medoids,
			int[] globalMedoids,
			ArrayList<SuffixTreeStringArray> stAL,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		m_waydone = new ArrayList<String>();
		m_recKnnToClustersTopURLs = new RecommenderKnnToClustersTopURLs(medoids, globalMedoids, null, isDistance, rolesW);
		m_recClustersSuffixTree = new RecommenderClustersSuffixTree(stAL);
		m_knn = Math.min(medoids.size(), knn);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#reset()
	 */
	public void reset(){
		m_waydone = new ArrayList<String>();
		m_recKnnToClustersTopURLs.reset();
		m_recClustersSuffixTree.reset();
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNumberOfFailures()
	 */
	public int getNumberOfFailures(){
		return m_0recosClusters;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#update(java.util.ArrayList, java.lang.String, boolean, boolean)
	 */
	public ArrayList<String> update(ArrayList<String> waydone, String laststep, 
			boolean incrWeigh, boolean performFailureFunction){
		m_recKnnToClustersTopURLs.update(waydone, laststep, incrWeigh, performFailureFunction);
		m_recClustersSuffixTree.update(waydone, laststep, incrWeigh, performFailureFunction);
		m_waydone.add(laststep);
		return m_waydone;
	}
	
	/**
	 * Gets the nextpossible steps.
	 *
	 * @return the nextpossible steps
	 */
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
		//ArrayList<Integer> listOfWeights = (ArrayList<Integer>)objA[1];
		
		
		// return the list of recommendations
		return objA;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsUnbounded()
	 */
	public ArrayList<String> getNextpossibleStepsUnbounded(){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> listOfUrls = (ArrayList<String>)objA[0];
		return listOfUrls;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsRandom(int, long)
	 */
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
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeighted(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone){
		// get the possible URLs in the actual position
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> list = (ArrayList<String>)objA[0];
		ArrayList<Integer> weig = (ArrayList<Integer>)objA[1];
		
		// get the most weighted URLs
		ArrayList<String> listOfURLs = m_recClustersSuffixTree.getTheMostWeightedURLs(nRecos, list, weig);
		if(listOfURLs.size()>=nRecos){
			return listOfURLs;
		}
		
		// else add URLs from step1
		if(waydone.size()==0){ return listOfURLs; }
		// add step1 recommendations
		Object[] objA2 = m_recClustersSuffixTree.getStep1Recommendations(listOfURLs);
		ArrayList<String> listOfURLsStep1 = (ArrayList<String>)objA2[0];
		ArrayList<Integer> listOfWeightsStep1 = (ArrayList<Integer>)objA2[1];
		ArrayList<String> addlist = m_recClustersSuffixTree.getTheMostWeightedURLs(nRecos-listOfURLs.size(), listOfURLsStep1, listOfWeightsStep1);
		for(int i=0; i<addlist.size(); i++){
			listOfURLs.add(addlist.get(i));
		}
		
		// return
		return listOfURLs;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedTrain(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedTest(int)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nrecos){
		return this.getNextpossibleStepsWeighted(nrecos, null);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsMarkov(int, java.util.ArrayList, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsMarkov(int nRecos, ArrayList<String> waydone, ArrayList<String> listMarkov){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedByOriginalSequences(int)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nRecos){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedEnrichWithStep1(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedEnrichWithStep1(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	
}
