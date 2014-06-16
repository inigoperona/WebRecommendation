package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.evaluator.*;
import java.util.*;

public class A030MainClassMarkovChain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String base = "/home/burdinadar/workspace_ehupatras/WebRecommendation/experiments";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/kk.log";
		String databaseWD = base + "/02_DATABASE_5";
		String dmWD = "/DM_04_edit";
		String validationWD = base + "/03_VALIDATION_5";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		dmWD = args[3];
		validationWD = args[4];
		
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		
		// LOAD PREPROCESSED LOGS //
		//A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		//preprocess.preprocessLogs(preprocessingWD, logfile);
		//preprocess.loadPreprocess();
		
		
		// LOAD DATABASE //
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		//database.createDatabase(databaseWD);
		database.loadDatabase(databaseWD);
		ArrayList<Long> sampleSessionIDs = database.getSessionsIDs();
		ArrayList<String[]> sequencesUHC = database.getInstantiatedSequences();
		

		// DISTANCE MATRIX //
		A010MainClassDistanceMatrixEuclidean dm = new A010MainClassDistanceMatrixEuclidean();
		dm.loadDistanceMatrix(databaseWD + dmWD);
		Matrix matrix = dm.getMatrix();
		
		
		// HOLD-OUT //
		A020MainClassHoldOut ho = new A020MainClassHoldOut();
		ho.loadParts(validationWD, sampleSessionIDs);
		//ho.createParts(validationWD, sampleSessionIDs);
		ModelValidationHoldOut mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL   = mv.getValidation();
		ArrayList<ArrayList<Long>> testAL  = mv.getTest();


		// MARKOV CHAIN VALIDATION //

		// initialize the model evaluator
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		ModelEvaluator modelev = 
				new ModelEvaluatorUHC(sequencesUHC, null, 
						matrix, trainAL, valAL, testAL);
		modelev.setFmeasureBeta(0.5f);
		modelev.setConfusionPoints(confusionPoints);
		
		// load topic information
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objAA = cont.loadUrlsTopic(preprocessingWD + "/URLs_to_topic.txt");
		ArrayList<Integer> urlIDs = (ArrayList<Integer>)objAA[0];
		int[] url2topic = (int[])objAA[1];
		modelev.setTopicParameters(urlIDs, url2topic, 0.5f);
		
		// write result headers
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// compute markov chain
		modelev.buildMarkovChains();

		// compute results
		String resultsMarkov;
		
			// random
		/*
		int[] nrecsR = new int[]{2,3,4,5,10,20};
		for(int i=0; i<nrecsR.length; i++ ){
			int nrec = nrecsR[i];
			resultsMarkov = modelev.computeEvaluationTest(0, nrec, (long)0, 0, 100, false, null);
			System.out.print("markovchain" + "_random" + nrec + ",");
			System.out.print(resultsMarkov);
		}
		*/

			// weighted
		int[] nrecsW = new int[]{2,3,4,5,10,20};
		for(int i=0; i<nrecsW.length; i++ ){
			int nrec = nrecsW[i];
			resultsMarkov = modelev.computeEvaluationTest(1, nrec, (long)0, 0, 100, 0, false, null);
			System.out.print("markovchain" + "_weighted" + nrec + ",");
			System.out.print(resultsMarkov);
		}
		
			// unbounded
		resultsMarkov = modelev.computeEvaluationTest(-1, -1, (long)0, 0, 100, 0, false, null);
		System.out.print("markovchain_unbounded,");
		System.out.print(resultsMarkov);
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}