package ehupatras.clustering.sapehac.experiment;

// TODO: Auto-generated Javadoc
/**
 * The Class DissimilarityMeasureEhupatras.
 */
public class DissimilarityMeasureEhupatras implements DissimilarityMeasure {
	
	/** The m_diastancematrix. */
	private float[][] m_diastancematrix;
	
	/** The m_dmindexes. */
	private int[] m_dmindexes;
	
	/**
	 * Instantiates a new dissimilarity measure ehupatras.
	 *
	 * @param distancematrix the distancematrix
	 * @param dmindexes the dmindexes
	 */
	public DissimilarityMeasureEhupatras(float[][] distancematrix, int[] dmindexes){
		m_dmindexes = dmindexes;
		m_diastancematrix = distancematrix;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.clustering.sapehac.experiment.DissimilarityMeasure#computeDissimilarity(ehupatras.clustering.sapehac.experiment.Experiment, int, int)
	 */
	public double computeDissimilarity(Experiment experiment, int observation1, int observation2){
		int rowi = m_dmindexes[observation1];
		int coli = m_dmindexes[observation2];
		return (double)m_diastancematrix[rowi][coli];
	}
}
