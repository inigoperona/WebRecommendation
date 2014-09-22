package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A053MainClassPamSpadeKnnEDSplit {
	
	public static void main(String[] args) {
		
		// Parameter control
		String base = "experiments_ehupatras";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/log20000.log";
		String url2topicFile = "/URLs_to_topic.txt";
		String urlSimilarityMatrix = "contentEnrichment/relations/ResSimilarity.txt";
		String urlRelationMatrix = "contentEnrichment/relations/ResRelations.txt";
		String clusterPartitionFile = "contentEnrichment/clusterPartitions/ClusterPartitionModua0.txt";
		String usage2contentFile = "convert_UrlIDs_content2usage/usa2cont.csv";
		String databaseWD = base + "/02_DATABASE_5";
		String dmWD = "/DM_04_edit";
		String validationWD = base + "/03_VALIDATION_5";
		String clustWD = "/pam_DM_04_edit";
		String profiWD = "/pam_DM_04_edit/spade1";
		String evalFile = "/evaluation.txt";
		
		preprocessingWD = args[0];
		logfile = args[1];
		url2topicFile = args[2];
		urlSimilarityMatrix = args[3];
		urlRelationMatrix = args[4];
		clusterPartitionFile = args[5];
		usage2contentFile = args[6];
		databaseWD = args[7];
		dmWD = args[8];
		validationWD = args[9];
		clustWD = args[10];
		profiWD = args[11];
		evalFile = args[12];
		
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
		Object[] objA = matrix.readSeqs(databaseWD + dmWD + "/sequences_split.txt");
		ArrayList<Long> namesSplit = (ArrayList<Long>)objA[0];
		ArrayList<String[]> seqsSplit = (ArrayList<String[]>)objA[1];

		
		// HOLD-OUT //
		A020MainClassHoldOut ho = new A020MainClassHoldOut();
		ho.createParts(validationWD, sampleSessionIDs);
		ModelValidationHoldOut mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL   = mv.getValidation();
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
				sequencesUHC, seqsSplit, 
				matrix, 
				trainAL, valAL, testAL,
				new ArrayList<Integer>());
		
		// evaluation parameters
		modelev.setFmeasureBeta(0.5f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);
		
		// load topic information
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objAA = cont.loadUrlsTopic(preprocessingWD + url2topicFile);
		ArrayList<Integer> urlIDs = (ArrayList<Integer>)objAA[0];
		int[] url2topic = (int[])objAA[1];
		int difftopics = (int)objAA[2];
		modelev.setTopicParameters(urlIDs, url2topic, difftopics, 0.5f, clusterPartitionFile);
		
		
		
		// PAM + MySPADE //
		
		// Results' header
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// Start generating and evaluating the model
		for(int j=0; j<ks.length; j++){
			int k = ks[j];
				
			String esperimentationStr = "pam" + k;
			
			// load clustering
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
				
				// weighted by construction sequences (test sequences)
				int[] nrecsWST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					modelev.setEsploitationParameters(true, rolesW, 100);
					results = modelev.computeEvaluationTest("weighted", nrec, (long)0);
					System.out.print(esperimentationStr2 + "_weighted" + nrec + ",");
					System.out.print(results);
				}
			
				// unbounded
				modelev.setEsploitationParameters(true, rolesW, 100);
				results = modelev.computeEvaluationTest("unbounded", -1, (long)0);
				System.out.print(esperimentationStr2 + "_unbounded,");
				System.out.print(results);
			}

		}
		
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		
	}
	
}
