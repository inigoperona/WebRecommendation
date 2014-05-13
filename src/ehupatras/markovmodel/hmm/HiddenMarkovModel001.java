package ehupatras.markovmodel.hmm;

import java.util.ArrayList;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;

public class HiddenMarkovModel001 extends HiddenMarkovModel {

	public HiddenMarkovModel001(ArrayList<String[]> dataset, 
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
			// get the next states indexes
			int actualPos = i;
			int actual = m_dictSta.get(i);
			int actualCl = actual / m_bufferPositions;
			int firstPos = this.getFirstStateInd(actualCl);
			int secondPos = this.getNextStateByStateInd(firstPos, 1);
			int thridPos = this.getNextStateByStateInd(firstPos, 2);
			nextPos = this.getNextStateByStateInd(i, 1);
			
			// update the transition matrix between states
			m_hmm.setAij(actualPos, actualPos, 0.20);
			m_hmm.setAij(actualPos, nextPos,   0.50);
			double prob;
			prob = m_hmm.getAij(actualPos, firstPos);
			m_hmm.setAij(actualPos, firstPos,  prob+0.10);
			prob = m_hmm.getAij(actualPos, secondPos);
			m_hmm.setAij(actualPos, secondPos, prob+0.10);
			prob = m_hmm.getAij(actualPos, thridPos);
			m_hmm.setAij(actualPos, thridPos,  prob+0.10);
		}
	}
	
}
