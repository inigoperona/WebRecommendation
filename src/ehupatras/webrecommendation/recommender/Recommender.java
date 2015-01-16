package ehupatras.webrecommendation.recommender;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Interface Recommender.
 */
public interface Recommender {
	
	/**
	 * Update.
	 *
	 * @param waydone the waydone
	 * @param newstep the newstep
	 * @param incrWeigh the incr weigh
	 * @param performFailureFunction the perform failure function
	 * @return the array list
	 */
	public ArrayList<String> update(
				ArrayList<String> waydone,
				String newstep,
				boolean incrWeigh, 
				boolean performFailureFunction);
	
	/**
	 * Reset.
	 */
	public void reset();
	
	/**
	 * Gets the number of failures.
	 *
	 * @return the number of failures
	 */
	public int getNumberOfFailures();
	
	/**
	 * Gets the nextpossible steps unbounded.
	 *
	 * @return the nextpossible steps unbounded
	 */
	public ArrayList<String> getNextpossibleStepsUnbounded();
	
	/**
	 * Gets the nextpossible steps random.
	 *
	 * @param nReco the n reco
	 * @param seed the seed
	 * @return the nextpossible steps random
	 */
	public ArrayList<String> getNextpossibleStepsRandom(int nReco, long seed);
	
	/**
	 * Gets the nextpossible steps weighted train.
	 *
	 * @param nRecos the n recos
	 * @param waydone the waydone
	 * @return the nextpossible steps weighted train
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone);
	
	/**
	 * Gets the nextpossible steps weighted test.
	 *
	 * @param nrecos the nrecos
	 * @return the nextpossible steps weighted test
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nrecos);
	
	/**
	 * Gets the nextpossible steps weighted.
	 *
	 * @param nRecos the n recos
	 * @param waydone the waydone
	 * @return the nextpossible steps weighted
	 */
	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone);
	
	/**
	 * Gets the nextpossible steps markov.
	 *
	 * @param nRecos the n recos
	 * @param waydone the waydone
	 * @param listMarkov the list markov
	 * @return the nextpossible steps markov
	 */
	public ArrayList<String> getNextpossibleStepsMarkov(int nRecos, ArrayList<String> waydone, ArrayList<String> listMarkov);
	
	/**
	 * Gets the nextpossible steps weighted by original sequences.
	 *
	 * @param nRecos the n recos
	 * @return the nextpossible steps weighted by original sequences
	 */
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nRecos);
	
	/**
	 * Gets the nextpossible steps weighted enrich with step1.
	 *
	 * @param nRecos the n recos
	 * @param waydone the waydone
	 * @return the nextpossible steps weighted enrich with step1
	 */
	public ArrayList<String> getNextpossibleStepsWeightedEnrichWithStep1(int nRecos, ArrayList<String> waydone);
}
