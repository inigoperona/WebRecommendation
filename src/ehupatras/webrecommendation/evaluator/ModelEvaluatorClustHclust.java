package ehupatras.webrecommendation.evaluator;

import ehupatras.clustering.ClusteringHierarchical;
import ehupatras.clustering.sapehac.dendrogram.DendrogramNode;
import ehupatras.clustering.sapehac.dendrogram.MergeNode;
import ehupatras.clustering.sapehac.dendrogram.ObservationNode;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.distmatrix.MatrixStructure;

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
	private String m_AgglomerativeMethodClassName = "ehupatras.clustering.sapehac.agglomeration.WardLinkage";
	//private String m_AgglomerativeMethodClassName = "ehupatras.clustering.sapehac.agglomeration.AverageLinkage";
	
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
	 * Cut dendrograms.
	 *
	 * @param pCutK the cut dendrogram k
	 */
	public void cutDendrogramsK(float pCutK){
		m_pCutDendrogramDiss = pCutK;
		m_clustersAL = new ArrayList<int[]>();
		for(int i=0; i<m_nFolds; i++){
			m_clustersAL.add(this.cutDendrogramK(i));
		}
	}
	
	/**
	 * Cut dendrograms.
	 *
	 * @param cut dendrograms with SEP
	 */
	public void cutDendrogramsSEP(MatrixStructure distanceMatrix){
		m_clustersAL = new ArrayList<int[]>();
		for(int i=0; i<m_nFolds; i++){
			m_clustersAL.add(this.cutDendrogramSEP(i, distanceMatrix));
		}
	}
	
	/**
	 * Do dendrogram.
	 *
	 * @param indexFold the index fold
	 * @return the clustering hierarchical
	 */
	private ClusteringHierarchical doDendrogram(int indexFold){
		ArrayList<String> trainnames = m_trainAL.get(indexFold);
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames, m_datasetSplit!=null);
		MatrixStructure distmatrix = m_distancematrix.getMatrix(trainDMindexes, m_datasetSplit!=null);
		
		// hierarchical clustering: http://sape.inf.usi.ch/hac
		ClusteringHierarchical clustering = new ClusteringHierarchical();
		clustering.computeHierarchicalClustering(distmatrix,m_AgglomerativeMethodClassName);
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
	
	/**
	 * Cut dendrogram by k.
	 *
	 * @param indexFold the index fold
	 * @return the int[]
	 */
	private int[] cutDendrogramK(int indexFold){
		ClusteringHierarchical clustering = m_dendroAL.get(indexFold);
		int[] clustersA = clustering.cutDendrogramByK(m_pCutDendrogramDiss);
		return clustersA;
	}
	
	/**
	 * Cut dendrogram with SEP
	 *
	 * @param indexFold the index fold
	 * @return the int[]
	 */
	private int[] cutDendrogramSEP(int indexFold, MatrixStructure distanceMatrix){
		ClusteringHierarchical clustering = m_dendroAL.get(indexFold);
		int[] clustersA = clustering.cutDendrogramWithSEP(distanceMatrix);
		return clustersA;
	}
}
