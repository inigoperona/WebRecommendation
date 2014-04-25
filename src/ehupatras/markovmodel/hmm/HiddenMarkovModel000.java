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
