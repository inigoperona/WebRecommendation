package angelu.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorClustPAM;
import ehupatras.webrecommendation.modelvalidation.ModelValidationCrossValidation;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A041MainClassPAMcv {

	public static void main(String[] args) {
		
		// Parameter control
		String base = "experiments_ehupatras";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/log20000.log";
		String databaseWD = base + "/02_DATABASE_5";
		String dmWD = "/DM_04_edit";
		String validationWD = base + "/03_VALIDATION_5";
		String clustWD = "/pam_DM_04_edit";
		
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

		
		// CROSS VALIDATION //
		A021MainClassCrossValidation ho = new A021MainClassCrossValidation();
		ho.loadParts(validationWD, sampleSessionIDs);
		ModelValidationCrossValidation mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL  = mv.getValidation();
		ArrayList<ArrayList<Long>> testAL  = mv.getTest();

		
		// MODEL VALIDATION //
	
		// Parameters to play with
		// k, number of clusters
		//int[] ks = {1000, 750, 500, 400, 300, 250, 200, 150, 100, 50};
		//int[] ks = {40, 30, 20, 10, 5};
		int[] ks = {150, 200, 250, 300};
		
		// initialize the model evaluator
		ModelEvaluatorClustPAM modelev = new ModelEvaluatorClustPAM(
				sequencesUHC, null,
				matrix,
				trainAL, valAL, testAL,
				1);
		
		// evaluation parameters
		modelev.setFmeasureBeta(0.5f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);	
	
		// PAM CLUSTERING //
		for(int j=0; j<ks.length; j++){ // for each height
			int k = ks[j];

			String esperimentationStr = "pam" + k;
			System.out.println("[" + System.currentTimeMillis() + "] " + esperimentationStr);
			
			// Clustering
			modelev.buildPAM(k);
			modelev.saveClusters(validationWD + clustWD + "/" + esperimentationStr + ".javaData");
			modelev.writeClusters(validationWD + clustWD + "/" + esperimentationStr + ".txt");
		}
					
			
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		
	}
	
}
