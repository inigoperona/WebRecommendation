package ehupatras.webrecommendation.evaluator;

import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorMC;
import java.util.ArrayList;

public class ModelEvaluatorMarkovChain 
				extends ModelEvaluator {

	// ATTRIBUTES
	
	private ArrayList<MarkovChain> m_markovChainAL = null;
	

	// CREATOR
	
	public ModelEvaluatorMarkovChain(
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
	
	// GET TEST EVALUATOR
	
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
	
	public void buildMC(){
		// compute markov chain for each fold
		m_markovChainAL = new ArrayList<MarkovChain>();
		for(int i=0; i<m_nFolds; i++){
			m_markovChainAL.add(this.computeMarkovChain(i));
		}
	}
	
	private MarkovChain computeMarkovChain(int iFold){
		// get the train sequences from sessionIDs
		ArrayList<Long> sessionIDs = m_trainAL.get(iFold); 
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
	
	public MarkovChain getMarkovChain(int ifold){
		return m_markovChainAL.get(ifold);
	}
	
}
