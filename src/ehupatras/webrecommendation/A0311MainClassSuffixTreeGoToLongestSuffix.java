package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluator;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorUHC;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A0311MainClassSuffixTreeGoToLongestSuffix {
	
	public static void main(String[] args) {
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String dmWD = "/DM_00_no_role";
		//dmWD = "";
		String validationWD = "/home/burdinadar/eclipse_workdirectory/DATA";
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
		ModelValidationHoldOut mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL   = mv.getValidation();
		ArrayList<ArrayList<Long>> testAL  = mv.getTest();
		
		
		// MODEL VALIDATION //

		// initialize the model evaluator
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		ModelEvaluator modelev = new ModelEvaluatorUHC(sequencesUHC, null,
				matrix, trainAL, valAL, testAL);
		modelev.setFmeasureBeta(0.5f);
		modelev.setConfusionPoints(confusionPoints);
		
		// load topic information
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objAA = cont.loadUrlsTopic(preprocessingWD + "/URLs_to_topic.txt");
		ArrayList<Integer> urlIDs = (ArrayList<Integer>)objAA[0];
		int[] url2topic = (int[])objAA[1];
		modelev.setTopicParameters(urlIDs, url2topic, 0.5f);
		
		// Markov Chain uses one of failure functions
		// so just in case we computed it
		modelev.buildMarkovChains();
		
		// SUFFIX TREE //
		modelev.buildSuffixTreesFromOriginalSequences();		
		
		// Results' header
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// Experimentation string
		String esperimentationStr = "suffixtree";
			
		// Evaluation
		String results;

		//int[] failmodesA = new int[]{0, 1, 2};
		int[] failmodesA = new int[]{1}; // GoToLongestSuffix
		for(int fmodei=0; fmodei<failmodesA.length; fmodei++){
			int fmode = failmodesA[fmodei];
			String esperimentationStr2 = esperimentationStr + "_failure" + fmode;
			
			//int[] goToMemA = new int[]{1,2,3,4,5, 100};
			int[] goToMemA = new int[]{1000};
			for(int gt=0; gt<goToMemA.length; gt++){
				int gtmem = goToMemA[gt];
				String esperimentationStr3 = esperimentationStr2 + "_gt" + gtmem;
				
				// weighted by construction sequences (& test sequences)
				// enrich with step1 urls in the suffix tree
				int[] nrecsWST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					results = modelev.computeEvaluationTest(1, nrec, (long)0, fmode, gtmem, 0, false, null);
					System.out.print(esperimentationStr3 + "_weighted" + nrec + ",");
					System.out.print(results);
				}
			
				// unbounded
				results = modelev.computeEvaluationTest(1, 1000, (long)0, fmode, gtmem, 0, false, null);
				System.out.print(esperimentationStr3 + "_unbounded,");
				System.out.print(results);
			}
		}		
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
