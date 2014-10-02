package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;
import ehupatras.markovmodel.hmm.HiddenMarkovModel000;
import ehupatras.markovmodel.hmm.HiddenMarkovModel001;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorHMM;

public class ModelEvaluatorHMM 
				extends ModelEvaluatorClust {

	// ATTRIBUTES
	
	protected ArrayList<HiddenMarkovModel> m_hmmAL = null;
	
	protected int m_nNextSteps = 3;
	
	// CREATOR
	
	public ModelEvaluatorHMM(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL,
			int modePrRe){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe);
	}
	
	// GET TEST EVALUATOR
	
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorHMM(
						testseqs, m_modePrRe,
						m_hmmAL.get(iFold),
						m_nNextSteps);
		return eval;
	}
	
	public void setEsploitationParameters(
			int nNextSteps){
		m_nNextSteps = nNextSteps;
	}
	
	// BUILD MODEL
	
	public void buildHiddenMarkovModels(String outfilename, int hmmMode){
		// compute markov chain for each fold
		m_hmmAL = new ArrayList<HiddenMarkovModel>();
		for(int i=0; i<m_nFolds; i++){
			m_hmmAL.add(this.getHMM(i, outfilename, hmmMode));
		}
	}
	
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
	
	protected void writeHMMsTXT(HiddenMarkovModel hmm, String outfile){
		for(int i=0; i<m_nFolds; i++){
			hmm.writeHMMtxt(outfile);
		}
	}
	
	protected void writeHMMsDOT(HiddenMarkovModel hmm, String outfile){
		for(int i=0; i<m_nFolds; i++){
			hmm.writeHMMdot(outfile);
		}
	}
	
}
