package ehupatras.markovmodel.hmm;

import java.util.List;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.ForwardBackwardScaledCalculator;
import be.ac.ulg.montefiore.run.jahmm.ViterbiCalculator;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchScaledLearner;
import be.ac.ulg.montefiore.run.jahmm.Opdf;
import be.ac.ulg.montefiore.run.jahmm.toolbox.MarkovGenerator;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;

// TODO: Auto-generated Javadoc
/**
 * The Class HiddenMarkovModel.
 */
public abstract class HiddenMarkovModel {

	// Web Access Sequences database
	/** The m_dataset. */
	protected ArrayList<String[]> m_dataset = null; // without role tag
	
	/** The m_url len. */
	private int m_urlLen = 1;
	
	// Clustering information
	/** The m_train indexes. */
	protected int[] m_trainIndexes;
	
	/** The m_clusters. */
	protected int[] m_clusters;
	
	/** The m_max cluster index. */
	protected int m_maxClusterIndex = -1;
	
	// dictionary from URLs to HMM indexes
	/** The m_dict obs. */
	protected ArrayList<Integer> m_dictObs; // possible observations
	// dictionary from Cluster-postion to global-position
	/** The m_dict sta. */
	protected ArrayList<Integer> m_dictSta;
	
	/** The m_buffer positions. */
	protected int m_bufferPositions = 1000;
	
	// HMM parameters
	/** The m_hmm. */
	Hmm<ObservationInteger> m_hmm;
	//private int m_nStates;
	//private int m_nObservations;
	//private float[] m_pi; // dimension: nStates 
	//private float[][] m_e; // dimension: nStates x nObservations
	//private float[][] m_a; // dimension: nObservations x nObservations
	
	
	
	// CREATOR //
	
	/**
	 * Instantiates a new hidden markov model.
	 *
	 * @param dataset the dataset
	 * @param trainIndexes the train indexes
	 * @param clusterAL the cluster al
	 */
	public HiddenMarkovModel(ArrayList<String[]> dataset, 
			int[] trainIndexes,
			int[] clusterAL){
		m_dataset = dataset;
		m_urlLen = (dataset.get(0))[0].length();
		m_trainIndexes = trainIndexes;
		m_clusters = clusterAL;
		
		this.createTheDictionary();
		this.updateClusterInfo();
	}
	
	/**
	 * Creates the the dictionary.
	 */
	private void createTheDictionary(){
		m_dictObs = new ArrayList<Integer>();
		for(int i=0; i<m_dataset.size(); i++){
			String[] seq = m_dataset.get(i);
			for(int j=0; j<seq.length; j++){
				int urlID = Integer.valueOf(seq[j]);
				if(!m_dictObs.contains(urlID)){
					m_dictObs.add(urlID);
				}
			}
		}
	}
	
	/**
	 * Update cluster info.
	 */
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
	
	/**
	 * Creates the hmm.
	 *
	 * @param nStates the n states
	 * @param initProbs the init probs
	 * @param emissions the emissions
	 */
	protected abstract void createHMM(int nStates, double[] initProbs, ArrayList<double[]> emissions);
	
	/**
	 * Initialize hmm parameters.
	 */
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
					int[] seqInt = convertUrlsDict(seq);
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
		this.createHMM(globalpos, initPiProb, emissions);
	}
	
	
	/**
	 * Gets the next state by state ind.
	 *
	 * @param stateInd the state ind
	 * @param n the n
	 * @return the next state by state ind
	 */
	protected int getNextStateByStateInd(int stateInd, int n){
		// actual cluster identification
		int actual = m_dictSta.get(stateInd);
		int actualCl = actual / m_bufferPositions;
		
		// the next position exists?
		int nextCl = this.getNextPositionCluster(stateInd, n);

		// and the next position is in the same cluster-sequence?
		int nextPos;
		if(actualCl==nextCl){
			// the next element is the same cluster-sequence
			nextPos = stateInd+n;
		} else {
			// the next element is in another cluster,
			// so tie it with the first element
			nextPos = this.getFirstStateInd(actualCl);
		}
		
		return nextPos;
	}
	
	/**
	 * Gets the next position cluster.
	 *
	 * @param stateInd the state ind
	 * @param position the position
	 * @return the next position cluster
	 */
	protected int getNextPositionCluster(int stateInd, int position){
		// the next position exists?
		int nextCl = -1;
		if((stateInd+position)<m_dictSta.size()){
			int next = m_dictSta.get(stateInd+position);
			nextCl = next / m_bufferPositions;
		}
		return nextCl;
	}
	
	/**
	 * Gets the first state ind.
	 *
	 * @param clusterId the cluster id
	 * @return the first state ind
	 */
	protected int getFirstStateInd(int clusterId){
		int firstPos = -1;
		for(int i=0; i<m_dictSta.size(); i++){
			int stId = m_dictSta.get(i);
			int clId = stId / m_bufferPositions;
			if(clusterId==clId){
				firstPos = i;
				break;
			}
		}
		return firstPos;
	}
	
	/**
	 * Convert urls dict.
	 *
	 * @param strA the str a
	 * @return the int[]
	 */
	protected int[] convertUrlsDict(String[] strA){
		int[] intA = new int[strA.length];
		for(int i=0; i<intA.length; i++){
			int urlID = Integer.valueOf(strA[i]);
			intA[i] = m_dictObs.indexOf(urlID);
		}
		return intA;
	}
	
	
	
	// TRAIN THE INIT HMM //
	
	/**
	 * Baum welch.
	 *
	 * @return the hidden markov model
	 */
	public HiddenMarkovModel baumWelch(){
		List<List<ObservationInteger>> sequences = new ArrayList<List<ObservationInteger>>();
		for(int i=0; i<m_trainIndexes.length; i++){
			String[] seq = m_dataset.get(m_trainIndexes[i]);
			int[] seqInt = convertUrlsDict(seq);
			List<ObservationInteger> oseq = new ArrayList<ObservationInteger>();
			for(int j=0; j<seqInt.length; j++){
				int obsInt = seqInt[j];
				ObservationInteger obs = new ObservationInteger(obsInt);
				oseq.add(obs);
			}
			sequences.add(oseq);
		}
		
		// execute the BaumWelch algorithm
		BaumWelchScaledLearner bwl = new BaumWelchScaledLearner();
		Hmm<ObservationInteger> learntHmm = bwl.learn(m_hmm, sequences);
		m_hmm = learntHmm;
		return this;
	}
	
	
	
	// PREDICTING THE NEXT STEP //
	
	/**
	 * Gets the next urls.
	 *
	 * @param waydone the waydone
	 * @param nSteps the n steps
	 * @return the next urls
	 */
	public Object[] getNextUrls(ArrayList<String> waydone, int nSteps){
		String[] waydoneA = new String[waydone.size()];
		for(int i=0; i<waydone.size(); i++){
			waydoneA[i] = waydone.get(i);
		}
		return this.getNextUrls(waydoneA, nSteps);
	}
	
	/**
	 * Gets the next urls.
	 *
	 * @param waydone the waydone
	 * @param nSteps the n steps
	 * @return the next urls
	 */
	public Object[] getNextUrls(String[] waydone, int nSteps){
		int actualState;
		
		// where is the pointer?
		if(waydone.length==0){
			// if the waydone is empty. For the first click.
			double maxprob = Double.NEGATIVE_INFINITY;
			int maxi = -1;
			for(int i=0; i<m_dictSta.size(); i++){
				double prob = m_hmm.getPi(i);
				if(maxprob<prob){
					maxprob = prob;
					maxi = i;
				}
			}
			actualState = maxi;
		} else {
			actualState = this.getTheMostProbableState(waydone);
		}
				
		// the next steps
		int[] nextStates = this.getNextStates(actualState, nSteps);
		
		// the observations of the next steps
		double[] obsProbsA = new double[m_dictObs.size()]; 
		int preSt = -1;
		double transProb = 1d;
		for(int i=0; i<nextStates.length; i++){
			
			// compute the transition probability
			int nextSt = nextStates[i];
			if(preSt==-1){
				transProb = 1d;
			} else {
				transProb = transProb * m_hmm.getAij(preSt, nextSt);
			}
			
			// compute the most probable observations
			Opdf<ObservationInteger> obs = m_hmm.getOpdf(nextSt);
			for(int j=0; j<m_dictObs.size(); j++){
				double prob = obs.probability(new ObservationInteger(j));
				obsProbsA[j] = obsProbsA[j] + transProb*prob;
			}

			// update states
			preSt = nextSt;
		}
		
		
		// prepare the observations to return
		ArrayList<String> urls = new ArrayList<String>(); 
		ArrayList<Float> support = new ArrayList<Float>(); 
		for(int i=0; i<obsProbsA.length; i++){
			float sup = (float)obsProbsA[i];
			if(sup>0){
				support.add(sup);
				int urlInt = m_dictObs.get(i);
				String urlStr = String.format("%0" + m_urlLen + "d", urlInt);
				urls.add(urlStr);
				//System.out.println(urlInt + " : " + urlStr + " : " + sup);
			}
		}
		Object[] objA = new Object[2];
		objA[0] = urls;
		objA[1] = support;
		return objA;
	}
	
	/**
	 * Gets the next states.
	 *
	 * @param actualState the actual state
	 * @param nStates the n states
	 * @return the next states
	 */
	private int[] getNextStates(int actualState, int nStates){
		int[] states = new int[nStates];
		int actSt = actualState;
		for(int k=0; k<nStates; k++){
			double maxprob = Double.NEGATIVE_INFINITY;
			int maxi = -1;
			for(int j=0; j<m_hmm.nbStates(); j++){
				double prob = m_hmm.getAij(actSt, j);
				if(maxprob<prob){
					maxprob = prob;
					maxi = j;
				}
			}
			states[k] = maxi;
			actSt = maxi;
		}
		return states;
	}
	
	/**
	 * Gets the the most probable state.
	 *
	 * @param waydone the waydone
	 * @return the the most probable state
	 */
	private int getTheMostProbableState(String[] waydone){
		// the way done in proper format to HMM
		List<ObservationInteger> waydoneObs = new ArrayList<ObservationInteger>();
		for(int i=0; i<waydone.length; i++){
			int urli = Integer.valueOf(waydone[i]);
			int urlInd = m_dictObs.indexOf(urli);
			ObservationInteger obs = new ObservationInteger(urlInd);
			waydoneObs.add(obs);
		}
		
		// The most probable way: Viterbi
		ViterbiCalculator vc = new ViterbiCalculator(waydoneObs, m_hmm);
		int[] waydoneSt = vc.stateSequence();
		
		/*
		// print the most probable way
		// HMM's state-symbols
		for(int i=0; i<waydoneSt.length; i++){
			System.out.print(waydoneSt[i] + " ");
		}
		System.out.println();
		// In cluster-states symbols 
		for(int i=0; i<waydoneSt.length; i++){
			System.out.print(m_dictSta.get(waydoneSt[i]) + " ");
		}
		System.out.println();
		*/
		
		// return the last state
		int lastState = waydoneSt[waydoneSt.length-1];
		return lastState;
	}
	
	
	
	
	// PRINT, WRITE the HMM
	
	/**
	 * Prints the hmm.
	 */
	public void printHMM(){
		System.out.print(this.toStringHMM());
	}
	
	/**
	 * To string hmm.
	 *
	 * @return the string
	 */
	private String toStringHMM(){
		String out = "";
		
		out+= m_hmm.toString();
		out+= "\n";
		
		out+= "States(positions): " + "\n";
		for(int i=0; i<m_dictSta.size(); i++){
			out+= i + " : " + m_dictSta.get(i) + "\n";
		}
		out+= "\n";
		
		out+= "Observations(URLs): " + "\n";
		for(int i=0; i<m_dictObs.size(); i++){
			out+= i + " : " + m_dictObs.get(i) + "\n";
		}
		out+= "\n";
		
		return out;
	}
	
	/**
	 * Write hm mtxt.
	 *
	 * @param outfile the outfile
	 */
	public void writeHMMtxt(String outfile){
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfile));
			writer.write(this.toStringHMM());
			writer.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.markovmodel.hmm.HiddenMarkovModel.writeHMM] " +
					"Problems with the file: " + outfile);
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Write hm mdot.
	 *
	 * @param outfile the outfile
	 */
	public void writeHMMdot(String outfile){
		// drawing
		GenericHmmDrawerDot hmmDrawer1 = new GenericHmmDrawerDot();
		try {
			hmmDrawer1.write(m_hmm, outfile);
		}
		catch (IOException e) {
			System.err.println("Error at drawing the HMM.");
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
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
		
		// print the hmm
		System.out.println("inithmm");
		System.out.println(hmm.toString());
		System.out.println();
		
		// create a sequence
		ObservationInteger o1 = new ObservationInteger(0);
		ObservationInteger o2 = new ObservationInteger(1);
		List<ObservationInteger> oseq1 = new ArrayList<ObservationInteger>();
		oseq1.add(o1);
		oseq1.add(o2);
		oseq1.add(o2);
		oseq1.add(o2);
		List<ObservationInteger> oseq2 = new ArrayList<ObservationInteger>();
		oseq2.add(o2);
		oseq2.add(o2);
		oseq2.add(o1);
		oseq2.add(o1);
		List<List<ObservationInteger>> sequences = new ArrayList<List<ObservationInteger>>();
		sequences.add(oseq1);
		sequences.add(oseq2);

		
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
		System.out.println();
		
		// BaumWelch
		//OpdfIntegerFactory factory = new OpdfIntegerFactory(4);
		//BaumWelchScaledLearner bwl = new BaumWelchScaledLearner();
		//Hmm<ObservationInteger> learntHmm = bwl.learn(initHmm, sequences);
		System.out.println("BaumWelch");
		BaumWelchScaledLearner bwl = new BaumWelchScaledLearner();
		Hmm<ObservationInteger> learntHmm = bwl.learn(hmm, sequences);
		System.out.println(learntHmm.toString());
		
		// sequence generator
		MarkovGenerator<ObservationInteger> mg = new MarkovGenerator<ObservationInteger>(hmm);
		List<List<ObservationInteger>> newSequences = new ArrayList<List<ObservationInteger>>();
		for(int i=0; i<10; i++){
			// we have to determine the length
			newSequences.add(mg.observationSequence(10));
		}
		System.out.println("Generate Sequences");
		for(int i=0; i<newSequences.size(); i++){
			String str = (newSequences.get(i)).toString();
			System.out.println(str);
		}
		
		// drawing
		GenericHmmDrawerDot hmmDrawer1 = new GenericHmmDrawerDot();
		try {
			hmmDrawer1.write(hmm, "hmm-generate.dot");
		}
		catch (IOException e) {
			System.err.println("Error at drawing the HMM.");
			e.printStackTrace();
			System.exit(1);
		}
	}

	
}
