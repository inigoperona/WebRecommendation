package ehupatras.webrecommendation.recommender;

import java.util.ArrayList;

public interface Recommender {
	public boolean update(String newstep, boolean incrWeigh);
	public void reset();
	public ArrayList<String> getNextpossibleStepsUnbounded();
	public ArrayList<String> getNextpossibleStepsRandom(int nReco, long seed);
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone);
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nrecos);
	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone);
	public ArrayList<String> getNextpossibleStepsMarkov(int nRecos, ArrayList<String> waydone, ArrayList<String> listMarkov);
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nRecos);
}
