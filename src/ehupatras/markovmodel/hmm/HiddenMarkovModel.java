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

public abstract class HiddenMarkovModel {

	// Web Access Sequences database
	protected ArrayList<String[]> m_dataset = null; // without role tag
	private int m_urlLen = 1;
	
	// Clustering information
	protected int[] m_trainIndexes;
	protected int[] m_clusters;
	protected int m_maxClusterIndex = -1;
	
	// dictionary from URLs to HMM indexes
	protected ArrayList<Integer> m_dictObs; // possible observations
	// dictionary from Cluster-postion to global-position
	protected ArrayList<Integer> m_dictSta;
	protected int m_bufferPositions = 1000;
	
	// HMM parameters
	Hmm<ObservationInteger> m_hmm;
	//private int m_nStates;
	//private int m_nObservations;
	//private float[] m_pi; // dimension: nStates 
	//private float[][] m_e; // dimension: nStates x nObservations
	//private float[][] m_a; // dimension: nObservations x nObservations
	
	
	
	// CREATOR //
	
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
	
	public abstract void initializeHmmParameters();
	
	protected int[] convertUrlsDict(String[] strA){
		int[] intA = new int[strA.length];
		for(int i=0; i<intA.length; i++){
			int urlID = Integer.valueOf(strA[i]);
			intA[i] = m_dictObs.indexOf(urlID);
		}
		return intA;
	}
	
	
	
	// TRAIN THE INIT HMM //
	
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
	
	public Object[] getNextUrls(ArrayList<String> waydone, int nSteps){
		String[] waydoneA = new String[waydone.size()];
		for(int i=0; i<waydone.size(); i++){
			waydoneA[i] = waydone.get(i);
		}
		return this.getNextUrls(waydoneA, nSteps);
	}
	
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
	
	public void printHMM(){
		System.out.print(this.toStringHMM());
	}
	
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
	
	public void writeHMM(String outfile){
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
