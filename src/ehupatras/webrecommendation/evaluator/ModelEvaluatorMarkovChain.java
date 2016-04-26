package ehupatras.webrecommendation.evaluator;

import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorMC;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorMarkovChain.
 */
public class ModelEvaluatorMarkovChain 
				extends ModelEvaluator {

	// ATTRIBUTES
	
	/** The m_markov chain al. */
	private ArrayList<MarkovChain> m_markovChainAL = null;
	

	// CREATOR
	
	/**
	 * Instantiates a new model evaluator markov chain.
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
	public ModelEvaluatorMarkovChain(
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
	
	// GET TEST EVALUATOR
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluator#getTestSetEvaluator(int, java.util.ArrayList)
	 */
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorMC(
						testseqs, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						
						m_markovChainAL.get(iFold));
		return eval;
	}	
	
	// BUILD MODEL
	
	/**
	 * Builds the mc.
	 */
	public void buildMC(){
		// compute markov chain for each fold
		m_markovChainAL = new ArrayList<MarkovChain>();
		for(int i=0; i<m_nFolds; i++){
			m_markovChainAL.add(this.computeMarkovChain(i));
		}
	}
	
	/**
	 * Compute markov chain.
	 *
	 * @param iFold the i fold
	 * @return the markov chain
	 */
	private MarkovChain computeMarkovChain(int iFold){
		// get the train sequences from sessionIDs
		ArrayList<String> sessionIDs = m_trainAL.get(iFold); 
		int[] inds = m_distancematrix.getSessionIDsIndexes(sessionIDs, 
						m_datasetSplit!=null);
		ArrayList<String[]> trainseqs = new ArrayList<String[]>();
		for(int j=0; j<inds.length; j++){
			int index = inds[j];
			String[] seq = getDataSet(m_datasetSplit!=null).get(index);
			trainseqs.add(seq);
		}
		MarkovChain mchain = new MarkovChain(trainseqs);
		return mchain;
	}
	
	/**
	 * Gets the markov chain.
	 *
	 * @param ifold the ifold
	 * @return the markov chain
	 */
	public MarkovChain getMarkovChain(int ifold){
		return m_markovChainAL.get(ifold);
	}
	
}
