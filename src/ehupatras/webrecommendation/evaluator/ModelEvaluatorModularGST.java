package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorModST;

public class ModelEvaluatorModularGST 
				extends ModelEvaluatorMedoids {

	// ATTRIBUTES
	
	protected ArrayList<ArrayList<MySuffixTree>> m_clustSuffixTreeAL = null;
	
	// CREATOR
	
	public ModelEvaluatorModularGST(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL,
			int modePrRe,
			String usage2contentFile,
			String resSimilarityFile){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe, usage2contentFile, resSimilarityFile, new ArrayList<Integer>());
	}
	
	
	// GET TEST EVALUATOR
	
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorModST(
						testseqs, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						
						m_clustSuffixTreeAL.get(iFold));
		return eval;
	}
	
	// BUILD MODEL
	
	public void buildClustersSuffixTrees(){
		// Build Cluster-SuffixTrees for each fold
		m_clustSuffixTreeAL = new ArrayList<ArrayList<MySuffixTree>>();
		for(int i=0; i<m_nFolds; i++){
			m_clustSuffixTreeAL.add(this.createClustersSuffixTrees(i));
		}
	}
	
	private ArrayList<MySuffixTree> createClustersSuffixTrees(int indexFold){
		// train sessions names
		ArrayList<Long> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<Long> trainsetnames2 = 
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
			ArrayList<Long> names = new ArrayList<Long>();
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
