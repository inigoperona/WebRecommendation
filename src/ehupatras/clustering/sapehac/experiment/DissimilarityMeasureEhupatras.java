package ehupatras.clustering.sapehac.experiment;

import ehupatras.webrecommendation.distmatrix.MatrixStructure;

// TODO: Auto-generated Javadoc
/**
 * The Class DissimilarityMeasureEhupatras.
 */
public class DissimilarityMeasureEhupatras implements DissimilarityMeasure {
	
	/** The m_diastancematrix. */
	private MatrixStructure m_diastancematrix;
	
	/**
	 * Instantiates a new dissimilarity measure ehupatras.
	 *
	 * @param distancematrix the distancematrix
	 * @param dmindexes the dmindexes
	 */
	public DissimilarityMeasureEhupatras(MatrixStructure distancematrix){
		m_diastancematrix = distancematrix;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.clustering.sapehac.experiment.DissimilarityMeasure#computeDissimilarity(ehupatras.clustering.sapehac.experiment.Experiment, int, int)
	 */
	public double computeDissimilarity(Experiment experiment, int observation1, int observation2){
		if(observation1<=observation2){
			return (double)m_diastancematrix.getCell(observation1, observation2);
		} else {
			return (double)m_diastancematrix.getCell(observation2, observation1);
		}
	}
}
