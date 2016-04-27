package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;
import ehupatras.webrecommendation.distmatrix.Matrix;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorHMMTrainned.
 */
public class ModelEvaluatorHMMTrainned 
				extends ModelEvaluatorHMM {

	// CREATOR
	
	/**
	 * Instantiates a new model evaluator hmm trainned.
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
	public ModelEvaluatorHMMTrainned(
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
	
	// BUILD
	
	// trained HMM
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluatorHMM#getHMM(int, java.lang.String, int)
	 */
	protected HiddenMarkovModel getHMM(int indexFold, String outfilename, int hmmMode){		
		// create the HMM
		HiddenMarkovModel initHmm = super.getHMM(indexFold, outfilename, hmmMode); 
		initHmm.initializeHmmParameters();
		this.writeHMMsTXT(initHmm, outfilename + "_f" + indexFold + "_initHmm.txt");
		this.writeHMMsDOT(initHmm, outfilename + "_f" + indexFold + "_initHmm.dot");
		
		// Train it
		HiddenMarkovModel learntHmm = initHmm.baumWelch();
		this.writeHMMsTXT(learntHmm, outfilename + "_f" + indexFold + "_learntHmm.txt");
		this.writeHMMsDOT(learntHmm, outfilename + "_f" + indexFold + "_learntHmm.dot");
		return learntHmm;
	}
	
}
