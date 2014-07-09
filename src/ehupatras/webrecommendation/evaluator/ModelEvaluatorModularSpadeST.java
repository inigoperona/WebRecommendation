package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorModSTknn;

public class ModelEvaluatorModularSpadeST 
				extends ModelEvaluatorMedoids {

	// ATTRIBUTES
	
	private ArrayList<ArrayList<MySuffixTree>> m_clustSuffixTreeAL = null;
	
	// CREATOR
	
	public ModelEvaluatorModularSpadeST(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL);
	}
	
	
	// GET TEST EVALUATOR
	
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorModSTknn(
						testseqs, 
						m_clustSuffixTreeAL.get(iFold),
						m_medoidsAL.get(iFold),
						m_gmedoidsAL.get(iFold),
						m_isDistance, m_rolesW, m_knn);
		return eval;
	}
	
	// BUILD MODEL
	
	public void buildClustersSpadeSuffixTrees(float minsup, String workdir){
		m_minsupport = minsup;
		
		// Build Cluster-SuffixTrees for each fold
		m_clustSuffixTreeAL = new ArrayList<ArrayList<MySuffixTree>>();
		for(int i=0; i<m_nFolds; i++){
			m_clustSuffixTreeAL.add(this.createClustersSPADESuffixTrees(i, workdir));
		}
	}
	
	private ArrayList<MySuffixTree> createClustersSPADESuffixTrees(int indexFold, String workdir){
		// train sessions names
		ArrayList<Long> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<Long> trainsetnames2 = 
				m_distancematrix.getSessionIDs(trainsetnames, m_datasetSplit!=null);
		
		// assignment to each case
		int[] clindexes = m_clustersAL.get(indexFold);
		
		// maximum cluster index
		int climax = super.getMaxIndex(clindexes);
		
		// for each cluster
		ArrayList<MySuffixTree> stAL = new ArrayList<MySuffixTree>(); 
		for(int cli=0; cli<=climax; cli++){
			// take the sessions we are interested in
			ArrayList<Long> names = new ArrayList<Long>();
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
