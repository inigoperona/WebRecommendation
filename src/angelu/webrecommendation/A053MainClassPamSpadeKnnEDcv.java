package angelu.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.A100MainClassAddContent;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import ehupatras.webrecommendation.modelvalidation.ModelValidationCrossValidation;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A053MainClassPamSpadeKnnEDcv {

	public static void main(String[] args) {
		
		// Parameter control
		String base = "experiments_ehupatras";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/log20000.log";
		String url2topicFile = "/URLs_to_topic.txt";
		String databaseWD = base + "/02_DATABASE_5";
		String dmWD = "/DM_04_edit";
		String validationWD = base + "/03_VALIDATION_5";
		String clustWD = "/pam_DM_04_edit";
		String profiWD = "/pam_DM_04_edit/spade1";
		preprocessingWD = args[0];
		logfile = args[1];
		url2topicFile = args[2];
		databaseWD = args[3];
		dmWD = args[4];
		validationWD = args[5];
		clustWD = args[6];
		profiWD = args[7];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		Website.load();
		
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
		//int[] ks = {1000, 750, 500, 400, 300, 250, 200, 150, 100, 50};
		int[] ks = {150, 200, 250, 300};
		//float[] seqweights = {0.05f, 0.10f, 0.15f, 0.20f};
		//float[] seqweights = {0.01f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.40f, 0.50f};
		float[] seqweights = {0.15f, 0.20f, 0.25f, 0.30f};
		float[][] rolesW = {{ 0f, 0f, 0f},
				  			{ 0f, 0f, 0f},
				  			{ 0f, 0f, 0f}};
		
		// initialize the model evaluator
		ModelEvaluatorMedoids modelev = new ModelEvaluatorMedoids(
				sequencesUHC, null, 
				matrix,
				trainAL, valAL, testAL);
		
		// evaluation parameters
		modelev.setFmeasureBeta(0.5f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);
		
		// load topic information
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objA = cont.loadUrlsTopic(preprocessingWD + url2topicFile);
		ArrayList<Integer> urlIDs = (ArrayList<Integer>)objA[0];
		int[] url2topic = (int[])objA[1];
		modelev.setTopicParameters(urlIDs, url2topic, 1f);
		
		
		// PAM + MySPADE //
		
		// Results' header
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// Start generating and evaluating the model
		for(int j=0; j<ks.length; j++){
			int k = ks[j];				
			String esperimentationStr = "pam" + k;
			
			// Load clustering
			String clustFile = validationWD + clustWD + "/" + esperimentationStr + ".javaData";
			modelev.loadClusters(clustFile);
			
			// SPADE
			for(int l=0; l<seqweights.length; l++){
				float minsup = seqweights[l];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
				
				// MEDOIDS models //
				modelev.buildMedoids(minsup, true);
				
				
				// Evaluation
				String results;
				
				
				// VALIDATION //
				
				// weighted by construction sequences (test sequences)
				int[] nrecsWSTv = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWSTv.length; ind++ ){
					int nrec = nrecsWSTv[ind];
					modelev.setEsploitationParameters(true, rolesW, 100);
					results = modelev.computeEvaluationVal("weighted", nrec, (long)0);
					System.out.print(esperimentationStr2 + "_weighted" + nrec + "_val,");
					System.out.print(results);
				}
			
				// unbounded
				modelev.setEsploitationParameters(true, rolesW, 100);
				results = modelev.computeEvaluationVal("unbounded", -1, (long)0);
				System.out.print(esperimentationStr2 + "_unbounded_val,");
				System.out.print(results);
				
				
				
				// TEST //
				
				// weighted by construction sequences (test sequences)
				int[] nrecsWST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					modelev.setEsploitationParameters(true, rolesW, 100);
					results = modelev.computeEvaluationTest("weighted", nrec, (long)0);
					System.out.print(esperimentationStr2 + "_weighted" + nrec + "_test,");
					System.out.print(results);
				}
			
				// unbounded
				modelev.setEsploitationParameters(true, rolesW, 100);
				results = modelev.computeEvaluationTest("unbounded", -1, (long)0);
				System.out.print(esperimentationStr2 + "_unbounded_test,");
				System.out.print(results);
				
			}

		}
		
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		
	}
	
}
