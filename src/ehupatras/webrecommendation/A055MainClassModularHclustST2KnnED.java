package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorModularGST;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A055MainClassModularHclustST2KnnED {

	public static void main(String[] args) {
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/log20000.log";
		String url2topicFile = "/URLs_to_topic.txt";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String dmWD = "/DM_03_intelligent2_dist";
		//dmWD = "";
		String validationWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String clustWD = "/CL_00_no_role";
		clustWD = "";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		dmWD = args[3];
		validationWD = args[4];
		clustWD = args[5];
		
		
		
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
		//ho.createParts(validationWD, sampleSessionIDs);
		ho.loadParts(validationWD, sampleSessionIDs);
		ModelValidationHoldOut mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL   = mv.getValidation();
		ArrayList<ArrayList<Long>> testAL  = mv.getTest();

		
		
		// MODEL VALIDATION //
	
		// Parameters to play with
		//float[] cutthA = {10, 15, 20, 25};
		//int[] cutthA = {1,2,4,6,8};
		//float[] cutthA = {0.1f,0.2f,0.4f,0.6f,0.8f, 1f,2f,4f,6f,8f, 10f,15f,20f,25f};
		//float[] cutthA = {5f, 10f, 20f, 30f, 40f, 50f, 100f, 150f, 200f, 250f, 300f, 400f, 500f, 750f, 1000f}; 
		float[] cutthA = {4f, 10f, 15f};
		int[] knnA = {1,2,5,10,100};
		float[][] rolesW = {{ 0f, 0f, 0f},
	  						{ 0f, 0f, 0f},
	  						{ 0f, 0f, 0f}};
		
		// initialize the model evaluator
		ModelEvaluatorModularGST modelev = new ModelEvaluatorModularGST(
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
		
		// SUFFIX TREE for each cluster //
		
		// Results' header
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// Start generating and evaluating the model
		int i = 5; // Hclust - linkage method
		for(int j=0; j<cutthA.length; j++){
			float cutth = cutthA[j];
			String esperimentationStr = "agglo" + i + "_cl" + cutth;
			
			// load clustering
			String clustFile = validationWD + clustWD + "/" + esperimentationStr + ".javaData";
			modelev.loadClusters(clustFile);
			
			// Create clusters-STs
			modelev.buildClustersSuffixTrees();
			modelev.buildMedoids(0.5f, false);
			
			
			for(int k=0; k<knnA.length; k++){
				int knn = knnA[k];
				modelev.setKnn(knnA[k]);
				String esperimentationStr2 = esperimentationStr + "_knn" + knn;
			
				
				// Evaluation
				String results;
			
				// weighted by construction sequences (test sequences)
				int[] nrecsWST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					results = modelev.computeEvaluationTest(3, nrec, (long)0, 1, 1000, 0, true, rolesW);
					System.out.print(esperimentationStr2 + "_weighted" + nrec + ",");
					System.out.print(results);
				}

				// unbounded
				results = modelev.computeEvaluationTest(-1, 1000, (long)0, 1, 1000, 0, true, rolesW);
				System.out.print(esperimentationStr2 + "_unbounded,");
				System.out.print(results);
			}
		}
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
