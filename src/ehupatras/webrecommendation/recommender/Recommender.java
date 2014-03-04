package ehupatras.webrecommendation.recommender;

import java.util.ArrayList;

public interface Recommender {
	public boolean update(String newstep);
	public void reset();
	public ArrayList<String> getNextpossibleStepsUnbounded();
	public ArrayList<String> getNextpossibleStepsRandom(int nReco, long seed);
	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone);
}
