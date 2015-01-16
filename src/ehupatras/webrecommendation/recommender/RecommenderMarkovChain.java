package ehupatras.webrecommendation.recommender;

import ehupatras.markovmodel.MarkovChain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderMarkovChain.
 */
public class RecommenderMarkovChain 
				implements Recommender {

	/** The m_markov chain. */
	private MarkovChain m_markovChain = null;
	
	/** The m_last step. */
	private String m_lastStep = null;
	
	/**
	 * Instantiates a new recommender markov chain.
	 *
	 * @param markovchain the markovchain
	 */
	public RecommenderMarkovChain(MarkovChain markovchain){
		m_markovChain = markovchain;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#reset()
	 */
	public void reset(){
		m_lastStep = null;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNumberOfFailures()
	 */
	public int getNumberOfFailures(){
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#update(java.util.ArrayList, java.lang.String, boolean, boolean)
	 */
	public ArrayList<String> update(ArrayList<String> waydone, String laststep, 
							boolean incrWeigh, boolean performFailureFunction){
		m_lastStep = laststep;
		
		// see if there are recommendations
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> recos = (ArrayList<String>)objA[0];
		ArrayList<Float> probs = (ArrayList<Float>)objA[1];
		
		// if there are recommendations, there are transitions
		// so, the way until now is runnable
		if(recos.size()>0){
			return waydone;
		} else {
			return (new ArrayList<String>());
		}
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsUnbounded()
	 */
	public ArrayList<String> getNextpossibleStepsUnbounded(){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> recos = (ArrayList<String>)objA[0];
		ArrayList<Float> probs = (ArrayList<Float>)objA[1];
		return recos;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsRandom(int, long)
	 */
	public ArrayList<String> getNextpossibleStepsRandom(int nRecos, long seed){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> recos = (ArrayList<String>)objA[0];
		ArrayList<Float> probs = (ArrayList<Float>)objA[1];
		int realNrecos = Math.min(nRecos, recos.size());
		ArrayList<String> recos2 = new ArrayList<String>();
		Random rand = new Random(seed);
		for(int i=0; i<realNrecos; i++){
			int pos = rand.nextInt(recos.size());
			recos2.add(recos.get(pos));
		}
		return recos2;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedTrain(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> recos = (ArrayList<String>)objA[0];
		ArrayList<Float> probs = (ArrayList<Float>)objA[1];
		float[] probsA = new float[probs.size()];
		for(int i=0; i<probs.size(); i++){ probsA[i] = probs.get(i); }
		Arrays.sort(probsA);
		int realNrecos = Math.min(nRecos, recos.size());
		
		// most probable sequences
		ArrayList<String> recos2 = new ArrayList<String>();
		boolean[] isusedA = new boolean[probsA.length];
		Arrays.fill(isusedA, false);
		for(int i=probsA.length-1; i>=0; i--){
			float probmax = probsA[i];
			for(int j=0; j<probs.size(); j++){
				if(!isusedA[j]){
					if(probmax==probs.get(j)){
						recos2.add(recos.get(j));
						isusedA[j] = true;
						break;
					}
				}
			}
			if(recos2.size()==realNrecos){
				break;
			}
		}
		return recos2;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedTest(int)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nrecos){
		return this.getNextpossibleStepsWeightedTrain(nrecos, null);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeighted(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeighted(int nrecos, ArrayList<String> waydone){
		return this.getNextpossibleStepsWeightedTrain(nrecos, null);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsMarkov(int, java.util.ArrayList, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsMarkov(int nrecos, ArrayList<String> waydone, ArrayList<String> listMarkov){
		return this.getNextpossibleStepsWeightedTrain(nrecos, null);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedByOriginalSequences(int)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nrecos){
		return this.getNextpossibleStepsWeightedTrain(nrecos, null);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedEnrichWithStep1(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedEnrichWithStep1(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleStepsWeightedTrain(nRecos, null);
	}
	
	/**
	 * Gets the nextpossible steps.
	 *
	 * @return the nextpossible steps
	 */
	private Object[] getNextpossibleSteps(){
		ArrayList<String> recos = new ArrayList<String>(); 
		ArrayList<Float> probs = new ArrayList<Float>(); 
		
		if(m_lastStep==null){
			// from initial probabilities
			for(int i=0; i<m_markovChain.numberOfSymbols(); i++){
				if(m_markovChain.getInitialProb(i)>0){
					recos.add(m_markovChain.getElemName(i));
					probs.add(m_markovChain.getInitialProb(i));
				}
			}
		} else {
			// from transition matrix
			int index = m_markovChain.getElemIndex(m_lastStep);
			for(int i=0; i<m_markovChain.numberOfSymbols(); i++){
				// first verify if the URL is modeled and then recommend
				if(index>0 && m_markovChain.getTransitionMatrix(index,i)>0){
					recos.add(m_markovChain.getElemName(i));
					probs.add(m_markovChain.getTransitionMatrix(index,i));
				}
			}
		}
		
		Object[] objA = new Object[2];
		objA[0] = recos;
		objA[1] = probs;
		return objA;
	}

}
