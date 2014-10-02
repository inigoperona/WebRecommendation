package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluator;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorClust;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorClustPAM;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A041MainClassPAMSplit {

	public static void main(String[] args) {
		
		// Parameter control
		String base = "experiments_ehupatras";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/log20000.log";
		String databaseWD = base + "/02_DATABASE_5";
		String dmWD = "/DM_04_edit-split";
		String validationWD = base + "/03_VALIDATION_5";
		String clustWD = "/pam_DM_04_edit-split";
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
		Object[] objA = matrix.readSeqs(databaseWD + dmWD + "/sequences_split.txt");
		ArrayList<Long> namesSplit = (ArrayList<Long>)objA[0];
		ArrayList<String[]> seqsSplit = (ArrayList<String[]>)objA[1];
		
		
		// HOLD-OUT //
		A020MainClassHoldOut ho = new A020MainClassHoldOut();
		ho.loadParts(validationWD, sampleSessionIDs);
		//ho.createParts(validationWD, sampleSessionIDs);
		ModelValidationHoldOut mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL   = mv.getValidation();
		ArrayList<ArrayList<Long>> testAL  = mv.getTest();

		
		
		// MODEL VALIDATION //
	
		// Parameters to play with

		// k, number of clusters
		//int[] ks = {1000, 750, 500, 400, 300, 250, 200, 150, 100, 50};
		//int[] ks = {40, 30, 20, 10, 5};
		int[] ks = {150, 200, 250, 300};		
	
		// Clustering
		ModelEvaluatorClustPAM modelev = new ModelEvaluatorClustPAM(
				sequencesUHC, seqsSplit,
				matrix,
				trainAL, valAL, testAL,
				0);
		
		// PAM //
		for(int j=0; j<ks.length; j++){ // for each height
			int k = ks[j];

			String esperimentationStr = "pam" + k;
			System.out.println("[" + System.currentTimeMillis() + "] " + esperimentationStr);
			
			modelev.buildPAM(k);
			modelev.saveClusters(validationWD + clustWD + "/" + esperimentationStr + ".javaData");
			modelev.writeClusters(validationWD + clustWD + "/" + esperimentationStr + ".txt");
		}
					
			
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		
	}
	
}
