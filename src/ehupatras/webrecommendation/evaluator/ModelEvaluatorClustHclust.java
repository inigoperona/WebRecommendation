package ehupatras.webrecommendation.evaluator;

import ehupatras.clustering.ClusteringHierarchical;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import java.util.ArrayList;

public class ModelEvaluatorClustHclust 
				extends ModelEvaluatorClust {

	// ATTRIBUTE
	
	private ArrayList<ClusteringHierarchical> m_dendroAL;
	private String m_AgglomerativeMethodClassName = "ehupatras.clustering.sapehac.agglomeration.WardLinkage";
	private float m_pCutDendrogramDiss = 50f;
	
	// CREATOR
	
	public ModelEvaluatorClustHclust(
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
	
	public void buildDendrograms(String agglemerativeMethodString){
		m_AgglomerativeMethodClassName = agglemerativeMethodString;
		m_dendroAL = new ArrayList<ClusteringHierarchical>();
		for(int i=0; i<m_nFolds; i++){
			m_dendroAL.add(this.doDendrogram(i));
		}
	}
	
	public void cutDendrograms(float pCutDendrogramDiss){
		m_pCutDendrogramDiss = pCutDendrogramDiss;
		m_clustersAL = new ArrayList<int[]>();
		for(int i=0; i<m_nFolds; i++){
			m_clustersAL.add(this.cutDendrogram(i));
		}
	}
	
	private ClusteringHierarchical doDendrogram(int indexFold){
		ArrayList<Long> trainnames = m_trainAL.get(indexFold);
		// hierarchical clustering: http://sape.inf.usi.ch/hac
		ClusteringHierarchical clustering = new ClusteringHierarchical();
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames, m_datasetSplit!=null);
		float[][] distmatrix = m_distancematrix.getMatrix(m_datasetSplit!=null);
		clustering.computeHierarchicalClustering(distmatrix,trainDMindexes,m_AgglomerativeMethodClassName);
		return clustering;
	}
	
	private int[] cutDendrogram(int indexFold){
		ClusteringHierarchical clustering = m_dendroAL.get(indexFold);
		int[] clustersA = clustering.cutDendrogramByDissimilarity(m_pCutDendrogramDiss);
		return clustersA;
	}
	
}
