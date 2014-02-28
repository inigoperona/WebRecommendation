package ehupatras.clustering.sapehac.experiment;

public class DissimilarityMeasureEhupatras implements DissimilarityMeasure {
	
	private float[][] m_diastancematrix;
	private int[] m_dmindexes;
	
	public DissimilarityMeasureEhupatras(float[][] distancematrix, int[] dmindexes){
		m_dmindexes = dmindexes;
		m_diastancematrix = distancematrix;
	}
	
	public double computeDissimilarity(Experiment experiment, int observation1, int observation2){
		int rowi = m_dmindexes[observation1];
		int coli = m_dmindexes[observation2];
		return (double)m_diastancematrix[rowi][coli];
	}
}
