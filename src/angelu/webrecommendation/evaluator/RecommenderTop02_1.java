package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.webrecommendation.recommender.RecommenderKnnToClustersTopURLs;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderTop02_1.
 */
public class RecommenderTop02_1 
				extends RecommenderKnnToClustersTopURLs {

	/** The m_left zeros len. */
	protected int m_leftZerosLen = 6;
	
	// CREATOR
	
	/**
	 * Instantiates a new recommender top02_1.
	 *
	 * @param medoids the medoids
	 * @param globalMedoids the global medoids
	 * @param recosForEachMedoid the recos for each medoid
	 * @param isDistance the is distance
	 * @param rolesW the roles w
	 */
	public RecommenderTop02_1(
			ArrayList<String[]> medoids, int[] clustersizes, int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid,
			boolean isDistance,
			float[][] rolesW){
		super(medoids, clustersizes, globalMedoids, recosForEachMedoid, isDistance, rolesW);
	}
	
	
	// FUNCTIONS
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.RecommenderKnnToClustersTopURLs#getNextpossibleSteps(int)
	 */
	protected ArrayList<String> getNextpossibleSteps(int nRecos){
		ArrayList<String> recosFinal = new ArrayList<String>();
		String urlStr;
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 11);
		recosFinal.add(urlStr);
		urlStr = String.format("%0"+ m_leftZerosLen + "d", 74);
		recosFinal.add(urlStr);
		return recosFinal;
	}
	
}
