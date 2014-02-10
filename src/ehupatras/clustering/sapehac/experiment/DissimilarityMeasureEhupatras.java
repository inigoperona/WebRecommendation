package ehupatras.clustering.sapehac.experiment;

public class DissimilarityMeasureEhupatras implements DissimilarityMeasure {
	
	private float[][] m_diastancematrix;
	
	public DissimilarityMeasureEhupatras(float[][] distancematrix){
		m_diastancematrix = distancematrix;
	}
	
	public double computeDissimilarity(Experiment experiment, int observation1, int observation2){
		return (double)m_diastancematrix[observation1][observation2];
	}
}
