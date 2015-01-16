package ehupatras.webrecommendation.recommender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import ehupatras.markovmodel.hmm.HiddenMarkovModel;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderHMM.
 */
public class RecommenderHMM implements Recommender {

	/** The m_hmm. */
	private HiddenMarkovModel m_hmm = null;
	
	/** The m_waydone. */
	private ArrayList<String> m_waydone = new ArrayList<String>();
	
	/** The m_0recos. */
	private int m_0recos = 0;
	
	/** The m_n next steps. */
	private int m_nNextSteps = 3;

	/**
	 * Instantiates a new recommender hmm.
	 *
	 * @param hmm the hmm
	 * @param nNextSteps the n next steps
	 */
	public RecommenderHMM(HiddenMarkovModel hmm, int nNextSteps){
		m_hmm = hmm;
		m_nNextSteps = nNextSteps;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#reset()
	 */
	public void reset(){
		m_waydone = new ArrayList<String>();
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNumberOfFailures()
	 */
	public int getNumberOfFailures(){
		return m_0recos;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#update(java.util.ArrayList, java.lang.String, boolean, boolean)
	 */
	public ArrayList<String> update(ArrayList<String> waydone, String laststep, 
			boolean incrWeigh, boolean performFailureFunction){
		m_waydone.add(laststep);
		return m_waydone;
	}
	
	/**
	 * Gets the nextpossible steps.
	 *
	 * @param nRecos the n recos
	 * @return the nextpossible steps
	 */
	private ArrayList<String> getNextpossibleSteps(int nRecos){
		Object[] objA = m_hmm.getNextUrls(m_waydone, m_nNextSteps);
		ArrayList<String> urls = (ArrayList<String>)objA[0];
		ArrayList<Float> supports = (ArrayList<Float>)objA[1];
		ArrayList<String> recos = this.getTheMostWeightedURLs(nRecos, urls, supports);
		
		return recos;
	}
	
	
	/**
	 * Gets the the most weighted ur ls.
	 *
	 * @param nrec the nrec
	 * @param list the list
	 * @param supports the supports
	 * @return the the most weighted ur ls
	 */
	private ArrayList<String> getTheMostWeightedURLs(int nrec, ArrayList<String> list, ArrayList<Float> supports){
		ArrayList<String> recos = new ArrayList<String>();
		
		// order the frequencies of searched sequences of the way
		float[] supportsA = new float[supports.size()];
		for(int i=0; i<supports.size(); i++){
			supportsA[i] = supports.get(i);
		}
		float[] supportsA2 = supportsA.clone();
		Arrays.sort(supportsA2);
		
		boolean[] isusedA = new boolean[supportsA.length];
		Arrays.fill(isusedA, false);
		
		for(int i=supportsA2.length-1; i>=0; i--){
			float supmax = supportsA2[i];
			for(int j=0; j<supportsA.length; j++){
				if(!isusedA[j]){
					if(supmax==supportsA[j]){
						recos.add(list.get(j));
						isusedA[j] = true;
						break;
					}
				}
			}
			if(recos.size()==nrec){
				break;
			}
		}
		
		return recos;
	}
	
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsUnbounded()
	 */
	public ArrayList<String> getNextpossibleStepsUnbounded(){
		return this.getNextpossibleSteps(100);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsRandom(int, long)
	 */
	public ArrayList<String> getNextpossibleStepsRandom(int nReco, long seed){
		ArrayList<String> list = this.getNextpossibleSteps(100);
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
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedTrain(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleSteps(nRecos);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedTest(int)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nRecos){
		return this.getNextpossibleSteps(nRecos);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeighted(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleSteps(nRecos);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsMarkov(int, java.util.ArrayList, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsMarkov(int nRecos, ArrayList<String> waydone, ArrayList<String> listMarkov){
		return this.getNextpossibleSteps(nRecos);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedByOriginalSequences(int)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nRecos){
		return this.getNextpossibleSteps(nRecos);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedEnrichWithStep1(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedEnrichWithStep1(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleSteps(nRecos);
	}
	
}
