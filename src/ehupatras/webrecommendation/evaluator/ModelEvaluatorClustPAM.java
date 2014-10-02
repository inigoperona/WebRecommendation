package ehupatras.webrecommendation.evaluator;

import ehupatras.clustering.ClusteringPAM;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;

import java.util.ArrayList;

public class ModelEvaluatorClustPAM 
				extends ModelEvaluatorClust {

	// ATTRIBUTES
	
	private int m_k;	
	
	// CREATOR
	
	public ModelEvaluatorClustPAM(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL,
			int modePrRe){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe);
	}

	// FUNCTIONS
	
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		return null;
	}
	
	// BUILD MODEL
	
	public void buildPAM(int k){
		m_k = k;
		
		// Clustering for each fold
		m_clustersAL = new ArrayList<int[]>();
		for(int i=0; i<m_nFolds; i++){
			m_clustersAL.add(this.clusteringPAM(i));
		}
	}
	
	private int[] clusteringPAM(int indexFold){
		ArrayList<Long> trainnames = m_trainAL.get(indexFold);
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames, m_datasetSplit!=null);
		float[][] distmatrix = m_distancematrix.getMatrix(m_datasetSplit!=null);
		
		ClusteringPAM pam = new ClusteringPAM(m_k, distmatrix, trainDMindexes);
		pam.runPAM();
		
		return pam.getMedoidAssignment();
	}
	
}
