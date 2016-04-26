package ehupatras.webrecommendation.evaluator;

import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorST;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorSuffixTreeGlobal.
 */
public class ModelEvaluatorSuffixTreeGlobal 
				extends ModelEvaluatorSuffixTree {
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator suffix tree global.
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
	public ModelEvaluatorSuffixTreeGlobal(
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
	
	// BUILD MODEL
	
	/**
	 * Builds the gst.
	 */
	public void buildGST(){
		// Build Suffix Trees for each fold
		m_suffixtreeAL = new ArrayList<MySuffixTree>();
		for(int i=0; i<m_nFolds; i++){
			m_suffixtreeAL.add(this.createSuffixTreeFromOriginalSequences(i));
		}
	}
	
	/**
	 * Creates the suffix tree from original sequences.
	 *
	 * @param indexFold the index fold
	 * @return the my suffix tree
	 */
	private MySuffixTree createSuffixTreeFromOriginalSequences(int indexFold){		
		ArrayList<String> trainnames = m_trainAL.get(indexFold);
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames, m_datasetSplit!=null);
		ArrayList<String[]> sequences = new ArrayList<String[]>(); 
		for(int i=0; i<trainDMindexes.length; i++){
			int index = trainDMindexes[i];
			String[] seq = (getDataSet(m_datasetSplit!=null)).get(index);
			sequences.add(seq);
		}
		MySuffixTree st = new MySuffixTree(sequences);
		return st;
	}
	
}
