package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorModularGST;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A054MainClassModularHclustST {
	
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
		
		// initialize the model evaluator
		ModelEvaluatorModularGST modelev = new ModelEvaluatorModularGST(
				sequencesUHC, null,
				matrix,
				trainAL, valAL, testAL,
				0, "", "");
		
		// evaluation parameters
		modelev.setFmeasureBeta(0.5f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);

		// load topic information
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objAA = cont.loadUrlsTopic(preprocessingWD + url2topicFile, " ");
		ArrayList<Integer> urlIDs = (ArrayList<Integer>)objAA[0];
		int[] url2topic = (int[])objAA[1];
		int difftopics = (int)objAA[2];
		modelev.setTopicParameters(urlIDs, url2topic, difftopics, 0.5f, clusterPartitionFile);
		
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
			
			// Modular approach: clusters-ST
			modelev.buildClustersSuffixTrees();
			
			
			// Evaluation
			String results;
			
			// weighted by construction sequences (test sequences)
			int[] nrecsWST = new int[]{2,3,4,5,10,20};
			for(int ind=0; ind<nrecsWST.length; ind++ ){
				int nrec = nrecsWST[ind];
				modelev.setEsploitationParameters(1, 1000, 0);
				results = modelev.computeEvaluationTest("weighted", nrec, (long)0);
				System.out.print(esperimentationStr + "_weighted" + nrec + ",");
				System.out.print(results);
			}

			// unbounded
			modelev.setEsploitationParameters(1, 1000, 0);
			results = modelev.computeEvaluationTest("unbounded", 1000, (long)0);
			System.out.print(esperimentationStr + "_unbounded,");
			System.out.print(results);
		}
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
	
}
