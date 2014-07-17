package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorSeqMinSPADE;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A0501MainClassHclustSpadeSt {

	public static void main(String[] args) {
		
		// Parameter control
		String base = "experiments_ehupatras";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/log20000.log";
		String url2topicFile = "/URLs_to_topic.txt";
		String databaseWD = base + "/02_DATABASE_5";
		String dmWD = "/DM_04_edit";
		String validationWD = base + "/03_VALIDATION_5";
		String clustWD = "/hclust_DM_04_edit";
		String profiWD = "/hclust_DM_04_edit/spade";
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


		
		// MODEL VALIDATION //
	
		// Parameters to play with
		//int[] cutthA = {10, 15, 20, 25};
		float[] cutthA = {4f, 10f, 15f, 20f, 25f};
		//float[] seqweights = {0.25f, 0.50f};
		float[] seqweights = {0.10f, 0.15f, 0.20f, 0.25f, 0.50f};
		
		// initialize the model evaluator
		ModelEvaluatorSeqMinSPADE modelev = new ModelEvaluatorSeqMinSPADE(
				sequencesUHC, null, 
				matrix,
				trainAL, valAL, testAL);
		
		// Evaluation parameters
		modelev.setFmeasureBeta(0.5f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);
		
		// load topic information
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objAA = cont.loadUrlsTopic(preprocessingWD + url2topicFile);
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
			
			// load clustering
			String clustFile = validationWD + clustWD + "/" + esperimentationStr + ".javaData";
			modelev.loadClusters(clustFile);

			// SPADE
			for(int k=0; k<seqweights.length; k++){
				float minsup = seqweights[k];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
			
				// SPADE
				String spadeFileTxt = validationWD + profiWD + "/" + esperimentationStr2 + ".txt";
				String spadeFileJav = validationWD + profiWD + "/" + esperimentationStr2 + ".javaData";
				modelev.spade(minsup, validationWD + profiWD, spadeFileTxt, spadeFileJav);
				
				// build suffix tree
				modelev.buildST();
				
				
				// Evaluation
				String results;
				
				// weighted by construction sequences (test sequences)
				int[] nrecsWST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					modelev.setEsploitationParameters(1, 1000, 0);
					results = modelev.computeEvaluationTest("ST_w_eS1", nrec, (long)0);
					System.out.print(esperimentationStr2 + "_weighted" + nrec + ",");
					System.out.print(results);
				}

				// unbounded
				modelev.setEsploitationParameters(1, 1000, 0);
				results = modelev.computeEvaluationTest("ST_w_eS1", 1000, (long)0);
				System.out.print(esperimentationStr2 + "_unbounded,");
				System.out.print(results);
			}

		}
		
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
