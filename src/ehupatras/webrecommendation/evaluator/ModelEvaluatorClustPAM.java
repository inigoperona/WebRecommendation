package ehupatras.webrecommendation.evaluator;

import ehupatras.clustering.ClusteringPAM;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.MatrixStructure;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorClustPAM.
 */
public class ModelEvaluatorClustPAM 
				extends ModelEvaluatorClust {

	// ATTRIBUTES
	
	/** The m_k. */
	private int m_k;	
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator clust pam.
	 *
	 * @param dataset the dataset
	 * @param datasetSplit the dataset split
	 * @param dm the dm
	 * @param trainAL the train al
	 * @param valAL the val al
	 * @param testAL the test al
	 * @param modePrRe the mode pr re
	 * @param usage2contentFile the usage2content file
	 * @param resSimilarityFile the res similarity file
	 */
	public ModelEvaluatorClustPAM(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<String>> trainAL,
			ArrayList<ArrayList<String>> valAL,
			ArrayList<ArrayList<String>> testAL,
			int modePrRe,
			String usage2contentFile,
			String resSimilarityFile){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe, usage2contentFile, resSimilarityFile);
	}

	// FUNCTIONS
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluator#getTestSetEvaluator(int, java.util.ArrayList)
	 */
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		return null;
	}
	
	// BUILD MODEL
	
	/**
	 * Builds the pam.
	 *
	 * @param k the k
	 */
	public void buildPAM(int k){
		m_k = k;
		
		// Clustering for each fold
		m_clustersAL = new ArrayList<int[]>();
		for(int i=0; i<m_nFolds; i++){
			m_clustersAL.add(this.clusteringPAM(i));
		}
	}
	
	/**
	 * Clustering pam.
	 *
	 * @param indexFold the index fold
	 * @return the int[]
	 */
	private int[] clusteringPAM(int indexFold){
		ArrayList<String> trainnames = m_trainAL.get(indexFold);
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames, m_datasetSplit!=null);
		/*
		int[] trainDMindexes2 = new int[]{
				trainDMindexes[0],
				trainDMindexes[2],
				trainDMindexes[4],
				trainDMindexes[6],
				trainDMindexes[8],
				trainDMindexes[10],
				trainDMindexes[12],
				trainDMindexes[14],
				trainDMindexes[16],
				trainDMindexes[18],
				trainDMindexes[20],
				trainDMindexes[22],
				trainDMindexes[24],
				trainDMindexes[26],
				trainDMindexes[28],
				trainDMindexes[30],
				trainDMindexes[32]};
		*/
		MatrixStructure distmatrix = m_distancematrix.getMatrix(trainDMindexes, m_datasetSplit!=null);
		
		////////////
		/*
		System.out.println("f" + indexFold);
		for(int i=0; i<trainDMindexes.length; i++){
			System.out.println(trainDMindexes[i]);
		}
		System.out.println("---");
		return trainDMindexes;
		*/
		////////////
		
		//m_k=3;
		ClusteringPAM pam = new ClusteringPAM(m_k, distmatrix);
		return pam.getClusterAssignment();
	}
	
}
