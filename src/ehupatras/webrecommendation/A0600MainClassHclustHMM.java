package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorHMM;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A0600MainClassHclustHMM {

	public static void main(String[] args) {
		
		// Parameter control
		String base = "/home/burdinadar/workspace_ehupatras/WebRecommendation/experiments";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/log20000.log";
		String url2topicFile = "/URLs_to_topic.txt";
		String databaseWD = base + "/02_DATABASE_5";
		String dmWD = "/DM_04_edit";
		String validationWD = base + "/03_VALIDATION_5";
		String clustWD = "/hclust_DM_04_edit";
		String profiWD = "/hclust_DM_04_edit/hmm";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		dmWD = args[3];
		validationWD = args[4];
		clustWD = args[5];
		profiWD = args[6];
		
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
	
		// Parameters to play with
		float[] cutthA = {4f, 10f, 15f, 20f, 25f};
		
		// initialize the model evaluator
		ModelEvaluatorHMM modelev = new ModelEvaluatorHMM(
				sequencesUHC, null, 
				matrix,
				trainAL, valAL, testAL);
		
		// evaluation parameters
		modelev.setFmeasureBeta(0.5f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);
		
		// load topic information
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objAA = cont.loadUrlsTopic(preprocessingWD + "/URLs_to_topic.txt");
		ArrayList<Integer> urlIDs = (ArrayList<Integer>)objAA[0];
		int[] url2topic = (int[])objAA[1];
		modelev.setTopicParameters(urlIDs, url2topic, 0.5f);
		
		// SUFFIX TREE //
		
		// Results' header
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// Start generating and evaluating the model
		int i = 5; // Hclust - linkage method
		for(int j=0; j<cutthA.length; j++){
			float cutth = cutthA[j];				
			String esperimentationStr = "agglo" + i + "_cl" + cutth;
			
			// Load clustering
			String clustFile = validationWD + clustWD + "/" + esperimentationStr + ".javaData";
			modelev.loadClusters(clustFile);
			
			// HMM
			modelev.buildHiddenMarkovModels(validationWD + profiWD + "/" + esperimentationStr, 0);
			
			
			// Evaluation
			String results;
				
			// weighted by construction sequences (test sequences)
			int[] nrecsWST = new int[]{2,3,4,5,10,20};
			for(int ind=0; ind<nrecsWST.length; ind++ ){
				int nrec = nrecsWST[ind];
				results = modelev.computeEvaluationTest(3, nrec, (long)0, 1, 3, 0, false, null);
				System.out.print(esperimentationStr + "_weighted" + nrec + ",");
				System.out.print(results);
			}

			// unbounded
			results = modelev.computeEvaluationTest(3, 1000, (long)0, 1, 3, 0, false, null);
			System.out.print(esperimentationStr + "_unbounded,");
			System.out.print(results);
			
		}
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		
	}
	
}
