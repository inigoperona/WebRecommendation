package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.sampling.*;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.evaluator.*;
import java.util.*;

public class MainClass2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		//basedirectory = args[0];
		//logfile = args[1];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(basedirectory);
		Website.setWorkDirectory(basedirectory);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		
		// LOAD PREPROCESSED LOGS //
		MainClassPreprocess preprocess = new MainClassPreprocess();
		//preprocess.preprocessLogs(basedirectory, logfile);
		preprocess.loadPreprocess();
		
		
		// SAMPLING //
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start sampling.");
		Sampling samp = new Sampling();
		ArrayList<Integer> sampleSessionIDs = samp.getSample(100, (long)0, false);
		// get instanciated sequences
		ArrayList<String[]> sequencesUHC = WebAccessSequencesUHC.getSequencesInstanciated(sampleSessionIDs);
		
		// DISTANCE MATRIX //
		Matrix matrix = new SimilarityMatrix();
		if(false){
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start computing the similarity matrix.");
		matrix.computeMatrix(sampleSessionIDs, sequencesUHC);
		matrix.save(basedirectory);
		matrix.writeMatrix(basedirectory + "/distance_matrix.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		} else {
			matrix.load(basedirectory);
		}
		float[][] distmatrix = matrix.getMatrix();
		
		// HOLD-OUT //
		ModelValidationHoldOut honestmodelval = new ModelValidationHoldOut();
		honestmodelval.prepareData(sampleSessionIDs, 70, 0, 30);
		honestmodelval.save(basedirectory);
		ArrayList<Integer> train = honestmodelval.getTrain();
		ArrayList<Integer> val   = honestmodelval.getValidation();
		ArrayList<Integer> test  = honestmodelval.getTest();
		ArrayList<ArrayList<Integer>> trainAL = new ArrayList<ArrayList<Integer>>();
		trainAL.add(train);
		ArrayList<ArrayList<Integer>> valAL = new ArrayList<ArrayList<Integer>>();
		valAL.add(val);
		ArrayList<ArrayList<Integer>> testAL = new ArrayList<ArrayList<Integer>>();
		testAL.add(test);
		
		// MODEL VALIDATION //
		ModelEvaluator modelev = 
				new ModelEvaluatorUHC(sequencesUHC,matrix,trainAL,valAL,testAL);
		modelev.createModels();
		//modelev.writeClusters(basedirectory + "/clusters.txt");
		modelev.writeAlignments(basedirectory + "/alignments.txt");
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}