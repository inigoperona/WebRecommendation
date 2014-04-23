package ehupatras.markovmodel.hmm;

import java.util.List;
import java.util.ArrayList;
import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.ForwardBackwardScaledCalculator;
import be.ac.ulg.montefiore.run.jahmm.ViterbiCalculator;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchScaledLearner;

public class HiddenMarkovModel {

	// to map the urls to HMM indexes
	private int[] m_urlIDs; // possible observations
	
	// HMM parameters
	private int m_nStates;
	private int m_nObservations;
	private float[] m_pi; // dimension: nStates 
	private float[][] m_e; // dimension: nStates x nObservations
	private float[][] m_a; // dimension: nObservations x nObservations
	
	public HiddenMarkovModel(int[] urlIDs){
		m_urlIDs = urlIDs;
		
	}
	
	public void cluster2hmmLine(ArrayList<String[]> wases){
		
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
	
}
