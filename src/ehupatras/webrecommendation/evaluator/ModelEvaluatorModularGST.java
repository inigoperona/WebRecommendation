package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorModST;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorModularGST.
 */
public class ModelEvaluatorModularGST 
				extends ModelEvaluatorMedoids {

	// ATTRIBUTES
	
	/** The m_clust suffix tree al. */
	protected ArrayList<ArrayList<MySuffixTree>> m_clustSuffixTreeAL = null;
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator modular gst.
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
	public ModelEvaluatorModularGST(
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
				new TestSetEvaluatorModST(
						testseqs, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						
						m_clustSuffixTreeAL.get(iFold));
		return eval;
	}
	
	// BUILD MODEL
	
	/**
	 * Builds the clusters suffix trees.
	 */
	public void buildClustersSuffixTrees(){
		// Build Cluster-SuffixTrees for each fold
		m_clustSuffixTreeAL = new ArrayList<ArrayList<MySuffixTree>>();
		for(int i=0; i<m_nFolds; i++){
			m_clustSuffixTreeAL.add(this.createClustersSuffixTrees(i));
		}
	}
	
	/**
	 * Creates the clusters suffix trees.
	 *
	 * @param indexFold the index fold
	 * @return the array list
	 */
	private ArrayList<MySuffixTree> createClustersSuffixTrees(int indexFold){
		// train sessions names
		ArrayList<String> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<String> trainsetnames2 = 
				m_distancematrix.getSessionIDs(trainsetnames, m_datasetSplit!=null);
		
		// assignment to each case
		int[] clindexes = m_clustersAL.get(indexFold);
		// maximum cluster index
		int climax = Integer.MIN_VALUE;
		for(int i=0; i<clindexes.length; i++){
			int cli = clindexes[i];
			if(cli>climax){
				climax = cli;
			}
		}
		
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
			
			// take the sequences
			int[] clusterDMind = m_distancematrix.getSessionIDsIndexes2(names, m_datasetSplit!=null);
			ArrayList<String[]> sequences = new ArrayList<String[]>(); 
			for(int i=0; i<clusterDMind.length; i++){
				int index = clusterDMind[i];
				String[] seq = this.getDataSet(m_datasetSplit!=null).get(index);
				sequences.add(seq);
			}
			
			// create the Suffix Tree
			MySuffixTree st = new MySuffixTree(sequences);
			stAL.add(st);
		}
		
		return stAL;
	}
	
	
}
