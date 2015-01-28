package ehupatras.webrecommendation.evaluator;

import ehupatras.clustering.ClusteringHierarchical;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorClustHclust.
 */
public class ModelEvaluatorClustHclust 
				extends ModelEvaluatorClust {

	// ATTRIBUTE
	
	/** The m_dendro al. */
	private ArrayList<ClusteringHierarchical> m_dendroAL;
	
	/** The m_ agglomerative method class name. */
	//private String m_AgglomerativeMethodClassName = "ehupatras.clustering.sapehac.agglomeration.WardLinkage";
	private String m_AgglomerativeMethodClassName = "ehupatras.clustering.sapehac.agglomeration.AverageLinkage";
	
	/** The m_p cut dendrogram diss. */
	private float m_pCutDendrogramDiss = 50f;
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator clust hclust.
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
	 * Builds the dendrograms.
	 *
	 * @param agglemerativeMethodString the agglemerative method string
	 */
	public void buildDendrograms(String agglemerativeMethodString){
		m_AgglomerativeMethodClassName = agglemerativeMethodString;
		m_dendroAL = new ArrayList<ClusteringHierarchical>();
		for(int i=0; i<m_nFolds; i++){
			m_dendroAL.add(this.doDendrogram(i));
		}
	}
	
	/**
	 * Cut dendrograms.
	 *
	 * @param pCutDendrogramDiss the cut dendrogram diss
	 */
	public void cutDendrograms(float pCutDendrogramDiss){
		m_pCutDendrogramDiss = pCutDendrogramDiss;
		m_clustersAL = new ArrayList<int[]>();
		for(int i=0; i<m_nFolds; i++){
			m_clustersAL.add(this.cutDendrogram(i));
		}
	}
	
	/**
	 * Do dendrogram.
	 *
	 * @param indexFold the index fold
	 * @return the clustering hierarchical
	 */
	private ClusteringHierarchical doDendrogram(int indexFold){
		ArrayList<Long> trainnames = m_trainAL.get(indexFold);
		// hierarchical clustering: http://sape.inf.usi.ch/hac
		ClusteringHierarchical clustering = new ClusteringHierarchical();
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames, m_datasetSplit!=null);
		float[][] distmatrix = m_distancematrix.getMatrix(m_datasetSplit!=null);
		clustering.computeHierarchicalClustering(distmatrix,trainDMindexes,m_AgglomerativeMethodClassName);
		return clustering;
	}
	
	/**
	 * Cut dendrogram.
	 *
	 * @param indexFold the index fold
	 * @return the int[]
	 */
	private int[] cutDendrogram(int indexFold){
		ClusteringHierarchical clustering = m_dendroAL.get(indexFold);
		int[] clustersA = clustering.cutDendrogramByDissimilarity(m_pCutDendrogramDiss);
		return clustersA;
	}
	
}
