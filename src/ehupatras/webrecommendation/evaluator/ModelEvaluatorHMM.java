package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;
import ehupatras.markovmodel.hmm.HiddenMarkovModel000;
import ehupatras.markovmodel.hmm.HiddenMarkovModel001;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorHMM;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorHMM.
 */
public class ModelEvaluatorHMM 
				extends ModelEvaluatorClust {

	// ATTRIBUTES
	
	/** The m_hmm al. */
	protected ArrayList<HiddenMarkovModel> m_hmmAL = null;
	
	/** The m_n next steps. */
	protected int m_nNextSteps = 3;
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator hmm.
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
	public ModelEvaluatorHMM(
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
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluator#getTestSetEvaluator(int, java.util.ArrayList)
	 */
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorHMM(
						testseqs, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						
						m_hmmAL.get(iFold),
						m_nNextSteps);
		return eval;
	}
	
	/**
	 * Sets the esploitation parameters.
	 *
	 * @param nNextSteps the new esploitation parameters
	 */
	public void setEsploitationParameters(
			int nNextSteps){
		m_nNextSteps = nNextSteps;
	}
	
	// BUILD MODEL
	
	/**
	 * Builds the hidden markov models.
	 *
	 * @param outfilename the outfilename
	 * @param hmmMode the hmm mode
	 */
	public void buildHiddenMarkovModels(String outfilename, int hmmMode){
		// compute markov chain for each fold
		m_hmmAL = new ArrayList<HiddenMarkovModel>();
		for(int i=0; i<m_nFolds; i++){
			m_hmmAL.add(this.getHMM(i, outfilename, hmmMode));
		}
	}
	
	/**
	 * Gets the hmm.
	 *
	 * @param indexFold the index fold
	 * @param outfilename the outfilename
	 * @param hmmMode the hmm mode
	 * @return the hmm
	 */
	protected HiddenMarkovModel getHMM(int indexFold, String outfilename, int hmmMode){
		// train sequences indexes
		ArrayList<Long> trSesIDs = m_trainAL.get(indexFold);
		int[] trInds = m_distancematrix.getSessionIDsIndexes(trSesIDs, m_datasetSplit!=null);
		
		// clusters. Assign cluster to each train case
		int[] clInds = m_clustersAL.get(indexFold);
		
		// create the HMM
		HiddenMarkovModel initHmm;
		if(hmmMode==0){
			initHmm = new HiddenMarkovModel000(m_dataset, trInds, clInds);
		} else { // hmmMode==1
			initHmm = new HiddenMarkovModel001(m_dataset, trInds, clInds);
		}
		initHmm.initializeHmmParameters();
		this.writeHMMsTXT(initHmm, outfilename + "_f" + indexFold + "_initHmm.txt");
		this.writeHMMsDOT(initHmm, outfilename + "_f" + indexFold + "_initHmm.dot");
		
		return initHmm;
	}
	
	/**
	 * Write hm ms txt.
	 *
	 * @param hmm the hmm
	 * @param outfile the outfile
	 */
	protected void writeHMMsTXT(HiddenMarkovModel hmm, String outfile){
		for(int i=0; i<m_nFolds; i++){
			hmm.writeHMMtxt(outfile);
		}
	}
	
	/**
	 * Write hm ms dot.
	 *
	 * @param hmm the hmm
	 * @param outfile the outfile
	 */
	protected void writeHMMsDOT(HiddenMarkovModel hmm, String outfile){
		for(int i=0; i<m_nFolds; i++){
			hmm.writeHMMdot(outfile);
		}
	}
	
}
