package ehupatras.webrecommendation.evaluator;

import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorST;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorSuffixTree.
 */
public abstract class ModelEvaluatorSuffixTree 
				extends ModelEvaluatorClust {

	// ATTRIBUTES
	
	/** The m_suffixtree al. */
	protected ArrayList<MySuffixTree> m_suffixtreeAL;
	
	/** The m_failuremode. */
	protected int m_failuremode = 0;
	
	/** The m_max memory. */
	protected int m_maxMemory = 100;
	
	/** The m_norm mode. */
	protected int m_normMode = 0;
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator suffix tree.
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
	public ModelEvaluatorSuffixTree(
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
	
	// GET EVALUATOR
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluator#getTestSetEvaluator(int, java.util.ArrayList)
	 */
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorST(
						testseqs, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						
						m_suffixtreeAL.get(iFold),
						m_failuremode, m_maxMemory, m_normMode);
		return eval;
	}
	
	/**
	 * Sets the esploitation parameters.
	 *
	 * @param failuremode the failuremode
	 * @param maxMemory the max memory
	 * @param normMode the norm mode
	 */
	public void setEsploitationParameters(
			int failuremode,
			int maxMemory,
			int normMode){
		m_failuremode = failuremode;
		m_maxMemory = maxMemory;
		m_normMode = normMode;
	}
	
	// BUILD MODEL
	
	/**
	 * Buid suffix trees.
	 *
	 * @param sequencesAL the sequences al
	 */
	protected void buidSuffixTrees(ArrayList<ArrayList<String[]>> sequencesAL){
		m_suffixtreeAL = new ArrayList<MySuffixTree>();
		for(int i=0; i<m_nFolds; i++){
			ArrayList<String[]> sequences = sequencesAL.get(i);
			MySuffixTree st = new MySuffixTree(sequences);
			m_suffixtreeAL.add(st);
		}
	}
	
	
	// utilities
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluator#getNumberOfNodes(int)
	 */
	public int getNumberOfNodes(int iFold){
		return m_suffixtreeAL.get(iFold).getNumberOfNodes();
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluator#getNumberOfEdges(int)
	 */
	public float getNumberOfEdges(int iFold){
		return m_suffixtreeAL.get(iFold).getNumberOfEdges();
	}
	
}
