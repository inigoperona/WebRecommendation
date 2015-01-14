package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.clustering.ClusteringPAM;
import ehupatras.clustering.ClusteringSilly;

public class ModelEvaluatorClustSilly 
				extends ModelEvaluatorClust {
	
	// ATTRIBUTES
	
	private int m_k;
	
	// CREATOR
	
	protected ModelEvaluatorClustSilly(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL,
			int modePrRe,
			String usage2contentFile,
			String resSimilarityFile){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe, usage2contentFile, resSimilarityFile);
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
		
		ClusteringSilly cs = new ClusteringSilly(m_k, distmatrix, trainDMindexes);
		return cs.getClustering();
	}
	
}
