package ehupatras.markovmodel.hmm;

import java.util.List;
import java.util.ArrayList;
import java.util.Hashtable;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.ForwardBackwardScaledCalculator;
import be.ac.ulg.montefiore.run.jahmm.ViterbiCalculator;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchScaledLearner;
import be.ac.ulg.montefiore.run.jahmm.Opdf;

public class HiddenMarkovModel {

	// Web Access Sequences database
	private ArrayList<String[]> m_dataset = null; // without role tag
	
	// Clustering information
	private int[] m_trainIndexes;
	private int[] m_clusters;
	private int m_maxClusterIndex = -1;
	
	// dictionary from URLs to HMM indexes
	private Hashtable<Integer,Integer> m_dictObs; // possible observations
	// dictionary from Cluster-postion to global-position
	private ArrayList<Integer> m_dictSta;
	private int m_bufferPositions = 1000;
	
	// HMM parameters
	Hmm<ObservationInteger> m_hmm;
	//private int m_nStates;
	//private int m_nObservations;
	//private float[] m_pi; // dimension: nStates 
	//private float[][] m_e; // dimension: nStates x nObservations
	//private float[][] m_a; // dimension: nObservations x nObservations
	
	
	
	// CREATOR //
	
	public HiddenMarkovModel(ArrayList<String[]> dataset, 
			int[] trainIndexes, int[] clusterAL){
		m_dataset = dataset;
		m_trainIndexes = trainIndexes;
		m_clusters = clusterAL;
		
		this.createTheDictionary();
		this.updateClusterInfo();
	}
	
	private void createTheDictionary(){
		int index = 0;
		m_dictObs = new Hashtable<Integer,Integer>();
		for(int i=0; i<m_dataset.size(); i++){
			String[] seq = m_dataset.get(i);
			for(int j=0; j<seq.length; j++){
				int urlID = Integer.valueOf(seq[j]);
				if(!m_dictObs.containsKey(urlID)){
					m_dictObs.put(urlID, index);
					index++;
				}
			}
		}
	}
	
	private void updateClusterInfo(){
		// compute the maximum value
		int maxClIndex = -1;
		for(int i=0; i<m_clusters.length; i++){
			int cli = m_clusters[i];
			if(maxClIndex<cli){
				maxClIndex = cli;
			}
		}
		m_maxClusterIndex = maxClIndex;
	}
	
	
	
	// INITIALIZATION //
	
	public void initializeHmmParameters(){
		// State & emissions information
		int globalpos = 0;
		m_dictSta = new ArrayList<Integer>();
		ArrayList<double[]> emissions = new ArrayList<double[]>();
		ArrayList<Integer> initPiFreq = new ArrayList<Integer>();
		int nPi = 0;
		
		// for each cluster
		for(int cli=0; cli<=m_maxClusterIndex; cli++){
			
			// take cluster's sequences
			int longestSeqLength = -1;
			ArrayList<int[]> cluster = new ArrayList<int[]>();  
			for(int j=0; j<m_clusters.length; j++){
				int clj = m_clusters[j];
				if(cli==clj){
					String[] seq = m_dataset.get(m_trainIndexes[j]);
					int[] seqInt = this.convertDict(seq);
					cluster.add(seqInt);
					if(longestSeqLength<seqInt.length){
						longestSeqLength = seqInt.length;
					}
				}
			}
			
			// for each position
			for(int pos=0; pos<longestSeqLength; pos++){
				int posName = cli*m_bufferPositions + pos;
				
				// compute the frequencies of URLs in each position
				int[] ePosFreqs = new int[m_dictObs.size()];
				int nE = 0;
				for(int k=0; k<cluster.size(); k++){
					int[] seq = cluster.get(k);
					if(pos<seq.length){
						// update emissions
						int url = seq[pos];
						ePosFreqs[url]++;
						nE++;
					}
				}
				
				// convert emission frequencies to probabilities
				double[] ePosProbs = new double[ePosFreqs.length];
				for(int k=0; k<ePosFreqs.length; k++){
					ePosProbs[k] = (double)ePosFreqs[k] / (double)nE;
				}
				
				// save the state-emissions vector
				emissions.add(ePosProbs);
				// update the initial states probabilities
				if(pos==0){
					int sizeCl = cluster.size();
					initPiFreq.add(sizeCl);
					nPi = nPi + sizeCl;
				} else {
					initPiFreq.add(0);
				}
				// update the global position indexes
				m_dictSta.add(posName);
				globalpos++;
			}
		}
		
		// convert emission frequencies to probabilities
		double[] initPiProb = new double[initPiFreq.size()];
		for(int k=0; k<initPiFreq.size(); k++){
			initPiProb[k] = (double)initPiFreq.get(k) / (double)nPi;
		}

		
		
		// CREATE THE HMM //
		
		// init
		int nStates = globalpos;
		int nEmissions = m_dictObs.size();
		m_hmm = new Hmm<ObservationInteger>(nStates, new OpdfIntegerFactory(nEmissions));
		
		// initial probabilities (for each URL)
		for(int i=0; i<initPiProb.length; i++){
			m_hmm.setPi(i, initPiProb[i]);
		}
		
		// emission probabilities (for each URL and state)
		for(int i=0; i<emissions.size(); i++){
			double[] emissionVector = emissions.get(i);
			m_hmm.setOpdf(i, new OpdfInteger(emissionVector));
		}
		
		// transition probabilities (among states)
		// initialize
		for(int i=0; i<m_dictSta.size(); i++){
			for(int j=0; j<m_dictSta.size(); j++){
				m_hmm.setAij(i, j, 0);
			}
		}
		// the transitions we are interested in
		int oldCl = -1;
		int firstPos = -1;
		int nextPos = -1;
		for(int i=0; i<m_dictSta.size(); i++){
			// actual position
			int actual = m_dictSta.get(i);
			int actualCl = actual / m_bufferPositions;
			
			// compare with the previous position
			if(oldCl!=actualCl){
				// Starts a new cluster's sequence
				firstPos = i;
			}
			oldCl = actualCl;

			// the next position exists
			int nextCl = -1;
			if(i+1<m_dictSta.size()){
				int next = m_dictSta.get(i+1);
				nextCl = next / m_bufferPositions;
			}

			// and the next position is in the same cluster-sequence?
			if(actualCl==nextCl){
				// the next element is the same cluster-sequence
				nextPos = i+1;
			} else {
				// the next element is in another cluster,
				// so tie it with the first element
				nextPos = firstPos;
			}
			
			// update the transition matrix between states
			m_hmm.setAij(i, i,       0.25);
			m_hmm.setAij(i, nextPos, 0.75);
		}
		
	}
	
	private int[] convertDict(String[] strA){
		int[] intA = new int[strA.length];
		for(int i=0; i<intA.length; i++){
			int urlID = Integer.valueOf(strA[i]);
			intA[i] = m_dictObs.get(urlID);
		}
		return intA;
	}
	
	private void printHMM(){
		System.out.println(m_hmm.toString());		
	}
	
	
	// MAIN_1 //
	
	public static void main1(String[] args){
		// number of states and observations
		Hmm<ObservationInteger> hmm = new Hmm<ObservationInteger>(2, new OpdfIntegerFactory(2));
		
		// initial probabilities
		hmm.setPi(0 , 0.95);
		hmm.setPi(1 , 0.05);
		
		// emision probabilities: Opfd (Observation's probability density function)
		//hmm.setOpdf(0, new OpdfInteger( new double [] {0.95 , 0.05}) );
		//hmm.setOpdf(1, new OpdfInteger( new double [] {0.2 , 0.8}) );
		hmm.setOpdf(0, new OpdfInteger( new double [] {0.90 , 0.06, 0.04}) );
		hmm.setOpdf(1, new OpdfInteger( new double [] {0.2 , 0.6, 0.2}) );
		
		// transition probabilities
		hmm.setAij(0, 1, 0.05);
		hmm.setAij(0, 0, 0.95);
		hmm.setAij(1, 0, 0.1);
		hmm.setAij(1, 1, 0.9);
		
		// create a sequence
		ObservationInteger o1 = new ObservationInteger(0);
		ObservationInteger o2 = new ObservationInteger(2);
		List<ObservationInteger> oseq1 = new ArrayList<ObservationInteger>();
		oseq1.add(o1);
		oseq1.add(o2);
		oseq1.add(o2);
		oseq1.add(o2);
		
		// probability of a sequence of observations
		System.out.println("FB_prob");
		double prob1 = hmm.probability(oseq1);
		System.out.println(prob1);
		int[] sseq1 = hmm.mostLikelyStateSequence(oseq1);
		for(int i=0; i<sseq1.length; i++){ System.out.print(sseq1[i] + " ");}
		System.out.println();
		System.out.println();
		
		// FB log-sum-exp to avoid underflow (probability of a sequence of observations)
		System.out.println("FB_log-sum-exp_prob");
		ForwardBackwardScaledCalculator fb = new ForwardBackwardScaledCalculator(oseq1, hmm);
		double prob2 = fb.lnProbability();
		System.out.println(prob2);
		
		// Viterbi
		System.out.println("Viterbi");
		ViterbiCalculator vc = new ViterbiCalculator(oseq1, hmm);
		int[] sseq2 = vc.stateSequence();
		for(int i=0; i<sseq2.length; i++){ System.out.print(sseq2[i] + " ");}
		System.out.println();
		
		// BaumWelch
		OpdfIntegerFactory factory = new OpdfIntegerFactory(4);
		BaumWelchScaledLearner bwl = new BaumWelchScaledLearner();
		//Hmm<ObservationInteger> learntHmm = bwl.learn(initHmm, sequences);
	}
	
	
	
	// MAIN_2 //
	
	public static void main(String[] args){	
		// create data we nedd
		String[] seq1 = new String[]{"1","2","3","4","5","6"};
		String[] seq2 = new String[]{"2","3","4","5","6","1"};
		String[] seq3 = new String[]{"3","4","5","6","1","2"};
		String[] seq4 = new String[]{"4","5","6","1","2","3"};
		String[] seq5 = new String[]{"5","6","1","2","3","4"};
		ArrayList<String[]> database = new ArrayList<String[]>();
		database.add(seq1);
		database.add(seq2);
		database.add(seq3);
		database.add(seq4);
		database.add(seq5);
		int[] trainInds = new int[]{0,1,3,4};
		int[] clusters = new int[]{0,0,1,1};
		
		// Create the HMM object
		HiddenMarkovModel hmm = new HiddenMarkovModel(database, trainInds, clusters); 
		hmm.initializeHmmParameters();
		hmm.printHMM();
	}
	
}
