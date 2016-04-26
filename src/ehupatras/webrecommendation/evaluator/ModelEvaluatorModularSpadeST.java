package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorModSTknn;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorModularSpadeST.
 */
public class ModelEvaluatorModularSpadeST 
				extends ModelEvaluatorMedoids {

	// ATTRIBUTES
	
	/** The m_clust suffix tree al. */
	private ArrayList<ArrayList<MySuffixTree>> m_clustSuffixTreeAL = null;
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator modular spade st.
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
	public ModelEvaluatorModularSpadeST(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<String>> trainAL,
			ArrayList<ArrayList<String>> valAL,
			ArrayList<ArrayList<String>> testAL,
			int modePrRe,
			String usage2contentFile,
			String resSimilarityFile){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe, usage2contentFile, resSimilarityFile, new ArrayList<Integer>());
	}
	
	
	// GET TEST EVALUATOR
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids#getTestSetEvaluator(int, java.util.ArrayList)
	 */
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorModSTknn(
						testseqs, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						
						m_clustSuffixTreeAL.get(iFold),
						m_medoidsAL.get(iFold), m_clustersizesAL.get(iFold), m_gmedoidsAL.get(iFold),
						m_isDistance, m_rolesW, m_knn);
		return eval;
	}
	
	// BUILD MODEL
	
	/**
	 * Builds the clusters spade suffix trees.
	 *
	 * @param minsup the minsup
	 * @param workdir the workdir
	 */
	public void buildClustersSpadeSuffixTrees(float minsup, String workdir){
		m_minsupport = minsup;
		
		// Build Cluster-SuffixTrees for each fold
		m_clustSuffixTreeAL = new ArrayList<ArrayList<MySuffixTree>>();
		for(int i=0; i<m_nFolds; i++){
			m_clustSuffixTreeAL.add(this.createClustersSPADESuffixTrees(i, workdir));
		}
	}
	
	/**
	 * Creates the clusters spade suffix trees.
	 *
	 * @param indexFold the index fold
	 * @param workdir the workdir
	 * @return the array list
	 */
	private ArrayList<MySuffixTree> createClustersSPADESuffixTrees(int indexFold, String workdir){
		// train sessions names
		ArrayList<String> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<String> trainsetnames2 = 
				m_distancematrix.getSessionIDs(trainsetnames, m_datasetSplit!=null);
		
		// assignment to each case
		int[] clindexes = m_clustersAL.get(indexFold);
		
		// maximum cluster index
		int climax = super.getMaxIndex(clindexes);
		
		// for each cluster
		ArrayList<MySuffixTree> stAL = new ArrayList<MySuffixTree>(); 
		for(int cli=0; cli<=climax; cli++){
			// take the sessions we are interested in
			ArrayList<String> names = new ArrayList<String>();
			for(int i=0; i<clindexes.length; i++){
				if(cli==clindexes[i]){
					names.add(trainsetnames2.get(i));
				}
			}
			
			ArrayList<String[]> freqSeqs = super.getSpadeSequences(names, workdir);
			
			// create the Suffix Tree
			MySuffixTree st = new MySuffixTree(freqSeqs);
			stAL.add(st);
		}
		
		return stAL;
	}
	
}
