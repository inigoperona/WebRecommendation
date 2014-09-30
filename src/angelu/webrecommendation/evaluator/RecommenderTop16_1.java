package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.webrecommendation.recommender.RecommenderKnnToClustersTopURLs;

public class RecommenderTop16_1 
				extends RecommenderKnnToClustersTopURLs {

	protected int m_leftZerosLen = 6;
	
	// CREATOR
	
	public RecommenderTop16_1(
			ArrayList<String[]> medoids,
			int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid,
			boolean isDistance,
			float[][] rolesW){
		super(medoids, globalMedoids, recosForEachMedoid, isDistance, rolesW);
	}
	
	
	// FUNCTIONS
	
	protected ArrayList<String> getNextpossibleSteps(int nRecos){
		ArrayList<String> recosFinal = new ArrayList<String>();
		String urlStr;
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 11);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 74);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 7);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 89);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 152);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 491);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 59);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 94);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 175);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 217);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 625);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 73);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 480);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 932);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 70);
		recosFinal.add(urlStr);
		return recosFinal;
	}
	
}
