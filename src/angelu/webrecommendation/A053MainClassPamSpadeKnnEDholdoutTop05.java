package angelu.webrecommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ehupatras.webrecommendation.A100MainClassAddContent;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import ehupatras.webrecommendation.modelvalidation.ModelValidationCrossValidation;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A053MainClassPamSpadeKnnEDholdoutTop05 {

	public static void main(String[] args) {
		
		// Parameter control
		String base = "/home/burdinadar/workspace_ehupatras/WebRecommendation/experiments_angelu/experiments";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/log20000.log";
		String url2topicFile = "/URLs_to_topic.txt";
		String databaseWD = base + "/02_DATABASE_5";
		String dmWD = "/DM_04_edit";
		String validationWD = base + "/03_VALIDATION_5";
		String clustWD = "/pam_DM_04_edit";
		String evalFile = "/evaluation.txt";
		preprocessingWD = args[0];
		logfile = args[1];
		url2topicFile = args[2];
		databaseWD = args[3];
		dmWD = args[4];
		validationWD = args[5];
		clustWD = args[6];
		evalFile = args[7];
		
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
		//int[] ks = {1000, 750, 500, 400, 300, 250, 200, 150, 100, 50};
		int[] ks = {150};
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
		modelev.setFmeasureBeta(1f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);
		
		// load topic information
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objA = cont.loadUrlsTopic(preprocessingWD + url2topicFile);
		ArrayList<Integer> urlIDs = (ArrayList<Integer>)objA[0];
		int[] url2topic = (int[])objA[1];
		modelev.setTopicParameters(urlIDs, url2topic, 0.5f);
		
		
		// PAM + MySPADE //
		
		// Results' header
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// open the file in which we save the evaluation process
		BufferedWriter evalWriter = null;
		try{
			evalWriter = new BufferedWriter(new FileWriter(validationWD + evalFile));
		} catch(IOException ex){
			System.err.println("[angelu.webrecommendation.A053MainClassPamSpadeKnnEDholdoutTop05] " +
					"Not possible to open the file: " + evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
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
				String resultInfo;
				
				// weighted by construction sequences (test sequences)
				int[] nrecsWSTv = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWSTv.length; ind++ ){
					int nrec = nrecsWSTv[ind];
					// Write recommendations
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_val"; 
					modelev.setLineHeader(resultInfo + ";", evalWriter);
					modelev.setEsploitationParameters(true, rolesW, 100);
					results = modelev.computeEvaluationVal(2, nrec, (long)0);
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}
			
				// unbounded
				resultInfo = esperimentationStr2 + "_unbounded_val";
				modelev.setLineHeader(resultInfo + ";", evalWriter);
				modelev.setEsploitationParameters(true, rolesW, 100);
				results = modelev.computeEvaluationVal(-1, -1, (long)0);
				System.out.print(resultInfo + ",");
				System.out.print(results);
				
				
				
				// TEST //
				
				// weighted by construction sequences (test sequences)
				int[] nrecsWST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";
					modelev.setLineHeader(resultInfo + ";", evalWriter);
					modelev.setEsploitationParameters(true, rolesW, 100);
					results = modelev.computeEvaluationTest(2, nrec, (long)0);
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}
			
				// unbounded
				resultInfo = esperimentationStr2 + "_unbounded_test";
				modelev.setLineHeader(resultInfo + ";", evalWriter);
				modelev.setEsploitationParameters(true, rolesW, 100);
				results = modelev.computeEvaluationTest(-1, -1, (long)0);
				System.out.print(resultInfo + ",");
				System.out.print(results);
				
			}

		}
		
		// close the file in which we save the evaluation process
		try{
			evalWriter.close();
		} catch (IOException ex){
			System.err.println("[[angelu.webrecommendation.A053MainClassPamSpadeKnnEDholdoutTop05]] " +
					"Problems at closing the file: " + evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		
	}
	
}
