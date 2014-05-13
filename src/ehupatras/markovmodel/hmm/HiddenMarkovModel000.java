package ehupatras.markovmodel.hmm;

import java.util.ArrayList;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;

public class HiddenMarkovModel000 extends HiddenMarkovModel {

	public HiddenMarkovModel000(ArrayList<String[]> dataset, 
			int[] trainIndexes,
			int[] clusterAL){
		super(dataset, trainIndexes, clusterAL);
	}
		
	protected void createHMM(int nStates, double[] initProbs, ArrayList<double[]> emissions){
		// init
		int nEmissions = m_dictObs.size();
		m_hmm = new Hmm<ObservationInteger>(nStates, new OpdfIntegerFactory(nEmissions));
		
		// initial probabilities (for each URL)
		for(int i=0; i<initProbs.length; i++){
			m_hmm.setPi(i, initProbs[i]);
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
		int nextPos = -1;
		for(int i=0; i<m_dictSta.size(); i++){
			// get the next position
			nextPos = this.getNextStateByStateInd(i, 1);
			
			// update the transition matrix between states
			m_hmm.setAij(i, i,       0.25);
			m_hmm.setAij(i, nextPos, 0.75);
		}
	}
	
	
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
		HiddenMarkovModel hmm = new HiddenMarkovModel000(database, trainInds, clusters);
		hmm.initializeHmmParameters();
		hmm.printHMM();
		
		// The most probable way
		String[] waydone;
		int state;
		
		/*
		waydone = new String[]{"4", "5", "6"};
		state = hmm.getTheMostProbableState(waydone);
		System.out.println("state1: " + state);
		
		waydone = new String[]{"1", "2", "3"};
		state = hmm.getTheMostProbableState(waydone);
		System.out.println("state1: " + state);
		
		waydone = new String[]{"1", "4", "3"};
		state = hmm.getTheMostProbableState(waydone);
		System.out.println("state1: " + state);
		
		waydone = new String[]{"1", "4", "2"};
		state = hmm.getTheMostProbableState(waydone);
		System.out.println("state1: " + state);
		*/
		
		// The next states
		waydone = new String[]{"4", "5", "6"};
		hmm.getNextUrls(waydone, 3);
	}
	
	
}
