package angelu.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluator;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorUHC;
import ehupatras.webrecommendation.modelvalidation.ModelValidationCrossValidation;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A041MainClassPAMholdout {

	public static void main(String[] args) {
		
		// Parameter control
		String base = "/home/burdinadar/workspace_ehupatras/WebRecommendation/experiments";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/kk.log";
		String databaseWD = base + "/02_DATABASE_5";
		String dmWD = "/DM_04_edit";
		dmWD = "";
		String validationWD = base + "/03_VALIDATION_5";
		String clustWD = "/pam_DM_04_edit";
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
		A012MainClassDistanceMatrixED dm = new A012MainClassDistanceMatrixED();
		dm.loadDistanceMatrix(databaseWD + dmWD);
		Matrix matrix = dm.getMatrix();

		
		// HOLD-OUT //
		A021MainClassCrossValidation ho = new A021MainClassCrossValidation();
		ho.loadParts(validationWD, sampleSessionIDs);
		//ho.createParts(validationWD, sampleSessionIDs);
		ModelValidationCrossValidation mv = ho.getParts();
		
		ArrayList<ArrayList<Long>> trainALaux = mv.getTrain();
		ArrayList<ArrayList<Long>> trainAL = new ArrayList<ArrayList<Long>>();
		trainAL.add(trainALaux.get(0));
		
		ArrayList<ArrayList<Long>> valALaux  = mv.getValidation();
		ArrayList<ArrayList<Long>> valAL  = new ArrayList<ArrayList<Long>>();
		valAL.add(valALaux.get(0));
		
		ArrayList<ArrayList<Long>> testALaux  = mv.getTest();
		ArrayList<ArrayList<Long>> testAL = new ArrayList<ArrayList<Long>>();
		testAL.add(testALaux.get(0));


		
		// MODEL VALIDATION //
	
		// Parameters to play with

		// k, number of clusters
		//int[] ks = {1000, 750, 500, 400, 300, 250, 200, 150, 100, 50};
		//int[] ks = {40, 30, 20, 10, 5};
		int[] ks = {10, 20, 40, 60, 90, 100, 150, 200, 250, 350};
		
		// initialize the model evaluator
		ModelEvaluator modelev = new ModelEvaluatorUHC(sequencesUHC, null,
				matrix, trainAL, valAL, testAL);
		modelev.setFmeasureBeta(0.5f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);		
	
		// HIERARCHICAL CLUSTERING //
		for(int j=0; j<ks.length; j++){ // for each height
			int k = ks[j];

			String esperimentationStr = "pam" + k;
			System.out.println("[" + System.currentTimeMillis() + "] " + esperimentationStr);
			
			// Clustering
			modelev.buildClustersPAM(k);
			modelev.saveClusters(validationWD + clustWD + "/" + esperimentationStr + ".javaData");
			modelev.writeClusters(validationWD + clustWD + "/" + esperimentationStr + ".txt");
			//modelev.loadClusters(validationWD + "/" + esperimentationStr + ".javaData");
		}
					
			
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		
	}
	
}

