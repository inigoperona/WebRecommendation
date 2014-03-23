package ehupatras.webrecommendation.recommender;

import java.util.*;

import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentCombineGlobalLocalDimopoulos2010;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLevenshtein;

public class RecommenderKnnToClusters
				implements Recommender {
	
	private ArrayList<String> m_waydone = new ArrayList<String>();
	private ArrayList<String[]> m_medoids;
	private int[] m_gmedoids; 
	private ArrayList<Object[]> m_recosInEachCluster;
	private int m_0recosClusters = 0;
	private boolean m_isDistance = true;
	
	public RecommenderKnnToClusters(ArrayList<String[]> medoids,
			int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid){
		m_medoids = medoids;
		m_recosInEachCluster = recosForEachMedoid;
		m_gmedoids = globalMedoids;
		m_waydone = new ArrayList<String>();
	}
	
	public void reset(){
		m_waydone = new ArrayList<String>();
	}
	
	// No recommendations clusters
	public int getNumberOfFailures(){
		return m_0recosClusters;
	}
	
	public ArrayList<String> update(ArrayList<String> waydone, String laststep, 
			boolean incrWeigh, boolean performFailureFunction){
		m_waydone = waydone;
		return m_waydone;
	}
	
	private ArrayList<String> getNextpossibleSteps(int nRecos){
		// the elements we are interested in
		ArrayList<String> recos = new ArrayList<String>(); 
		
		// medoids ordered from the nearest to farthest
		int[] orderedMedoids = this.knnSim();
		
		// for each medoid take the reccommendations
		boolean end = false;
		for(int i=0; i<orderedMedoids.length; i++){
			int nearesCl = orderedMedoids[i];
			Object[] objA = m_recosInEachCluster.get(nearesCl);
			ArrayList<String> recosCl = (ArrayList<String>)objA[0];
			ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
			
			if(recosCl.size()==0){m_0recosClusters++;}
			for(int j=0; j<recosCl.size(); j++){
				if(recos.size()<nRecos){
					String reco = recosCl.get(j);
					if(!recos.contains(reco)){
						recos.add(reco);
					}
				} else {
					end = true;
					break;
				}
			}
			if(end){ break;}
		}
		
		// return the list of recommendations
		return recos;
	}
	
	private int[] knnSim(){
		// if we do not know nothing about the navigation
		// return the most centered medoid in the database
		if(m_waydone.size()==0){
			return m_gmedoids;
		}
		
		// Else find the nearest medoid
		// waydone to String[]
		String[] waydone = new String[m_waydone.size()];
		for(int i=0; i<m_waydone.size(); i++){ waydone[i] = m_waydone.get(i); }

		// compute the similarities with the medoids
		float[] simA = new float[m_medoids.size()];
		for(int i=0; i<m_medoids.size(); i++){
			String[] medoid = m_medoids.get(i);
			SequenceAlignment seqalign;
			if(m_isDistance){
				float[][] roleW5 = {{ 0f,    0f,    0f},
  		    						{ 0f,    1f, 0.75f},
  		    						{ 0f, 0.75f,    1f}};
				seqalign = new SequenceAlignmentLevenshtein();
				seqalign.setRoleWeights(roleW5);
			} else{
				float[][] roleW1 = {{ 0f, 0f, 0f},
            						{ 0f, 0f, 0f},
            						{ 0f, 0f, 0f}};
				seqalign = new SequenceAlignmentCombineGlobalLocalDimopoulos2010();
				seqalign.setRoleWeights(roleW1);
			}
			float sim = seqalign.getScore(waydone, medoid);
			simA[i] = sim;
		}
		
		// order the similarities or the distance
		float[] simAord = this.orderSim(simA);
		
		// return the index of medoids ordered
		Object[] objA = this.orderSimilarities(simA, simAord);
		int[] orderedMedoids = (int[])objA[0];
		float[] orderedSims = (float[])objA[1];
		return orderedMedoids;
	}
	
	private float[] orderSim(float[] sims){
		// order the similarities
		float[] simAord = sims.clone();
		Arrays.sort(simAord);
		
		if(m_isDistance){
			float[] simAordCopy = simAord.clone();
			// reverse the simAord array
			int iaux = 0;
			for(int i=simAordCopy.length-1; i>=0; i--){
				simAord[iaux] = simAordCopy[i];
				iaux++;
			}
		}
		
		return simAord;
	}
	
	private Object[] orderSimilarities(float[] sims, float[] orderSims){
		// order from biggest similarity value to the minimum
		// initialize the result array
		int[] orderedMedoids = new int[sims.length];
		float[] orderedSims = new float[sims.length];
		int ind = 0;
		
		// to check if we have used before
		boolean[] isOrderA = new boolean[sims.length];
		Arrays.fill(isOrderA, false);
		// for the largest to smallest find the medoid index
		for(int i=orderSims.length-1; i>=0; i--){
			float sim = orderSims[i];
			for(int j=0; j<sims.length; j++){
				if(!isOrderA[j] && sim==sims[j]){
					orderedMedoids[ind] = j;
					orderedSims[ind] = sim;
					ind++;
					isOrderA[j] = true;
					break;
				}
			}
		}
		
		// return the index of medoids ordered
		Object[] objA = new Object[2];
		objA[0] = orderedMedoids;
		objA[1] = orderedSims;
		return objA;
	}

	public ArrayList<String> getNextpossibleStepsUnbounded(){
		return this.getNextpossibleSteps(100);
	}
	public ArrayList<String> getNextpossibleStepsRandom(int nReco, long seed){
		return this.getNextpossibleSteps(nReco);
	}
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleSteps(nRecos);
	}
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nrecos){
		return this.getNextpossibleSteps(nrecos);
	}
	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleSteps(nRecos);
	}
	public ArrayList<String> getNextpossibleStepsMarkov(int nRecos, ArrayList<String> waydone, ArrayList<String> listMarkov){
		return this.getNextpossibleSteps(nRecos);
	}
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nRecos){
		return this.getNextpossibleSteps(nRecos);
	}
	
	public static void main(String[] args){
		
		// create medoids list
		ArrayList<String[]> medoids = new ArrayList<String[]>();
		String[] medoid1 = new String[]{"1H","2H","3C"};
		String[] medoid2 = new String[]{"3C","2C","1C"};
		medoids.add(medoid1);
		medoids.add(medoid2);
		
		// create global-medoids index array
		int[] gmedoids = new int[]{1,0};
		
		// create recos
		ArrayList<Object[]> recos = new ArrayList<Object[]>();

		ArrayList<String> recs1 = new ArrayList<String>();
		recs1.add("1");
		recs1.add("2");
		ArrayList<Integer> supp1 = new ArrayList<Integer>();
		supp1.add(4);
		supp1.add(3);
		Object[] objA1 = new Object[2];
		objA1[0] = recs1;
		objA1[1] = supp1;
		
		ArrayList<String> recs2 = new ArrayList<String>();
		recs2.add("3");
		recs2.add("2");
		ArrayList<Integer> supp2 = new ArrayList<Integer>();
		supp2.add(3);
		supp2.add(2);
		Object[] objA2 = new Object[2];
		objA2[0] = recs2;
		objA2[1] = supp2;
		
		recos.add(objA1);
		recos.add(objA2);
		
		// Run the classs
		ArrayList<String> list;
		ArrayList<String> waydone = new ArrayList<String>();
		RecommenderKnnToClusters rkt5 = new RecommenderKnnToClusters(medoids, gmedoids, recos);
		
		// STEP0
		list = rkt5.getNextpossibleSteps(5);
		System.out.println("STEP0");
		for(int i=0; i<list.size(); i++){ System.out.println("reco: " + list.get(i));}
		
		// STEP1
		waydone.add("1H");
		rkt5.update(waydone, null, false, false);
		
		// STEP2
		waydone.add("2H");
		rkt5.update(waydone, null, false, false);
		list = rkt5.getNextpossibleSteps(5);
		System.out.println("STEP2");
		for(int i=0; i<list.size(); i++){ System.out.println("reco: " + list.get(i));}

	}
	
}
