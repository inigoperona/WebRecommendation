package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;
import ehupatras.webrecommendation.distmatrix.Matrix;

public class ModelEvaluatorHMMTrainned 
				extends ModelEvaluatorHMM {

	// CREATOR
	
	public ModelEvaluatorHMMTrainned(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL,
			int modePrRe){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe);
	}
	
	// BUILD
	
	// trained HMM
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
