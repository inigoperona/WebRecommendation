package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluator;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorSuffixTreeGlobal;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A0313MainClassSuffixTreeGoToLength1Suffix {

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
		ho.loadParts(validationWD, sampleSessionIDs);
		ModelValidationHoldOut mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL   = mv.getValidation();
		ArrayList<ArrayList<Long>> testAL  = mv.getTest();
		
		
		// MODEL VALIDATION //

		// build model: GST
		ModelEvaluatorSuffixTreeGlobal modelev = new ModelEvaluatorSuffixTreeGlobal(
				sequencesUHC, null,
				matrix, 
				trainAL, valAL, testAL);
		modelev.buildGST();
		
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
		
		// Results' header
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// Experimentation string
		String esperimentationStr = "suffixtree";
			
		// Evaluation
		String results;

		//int[] failmodesA = new int[]{0, 1, 2};
		int[] failmodesA = new int[]{1}; // GoToLongestSuffix
		for(int fmodei=0; fmodei<failmodesA.length; fmodei++){
			int fmode = failmodesA[fmodei];
			String esperimentationStr2 = esperimentationStr + "_failure" + fmode;
			
			//int[] goToMemA = new int[]{1,2,3,4,5, 100};
			int[] goToMemA = new int[]{1}; // Length1
			for(int gt=0; gt<goToMemA.length; gt++){
				int gtmem = goToMemA[gt];
				String esperimentationStr3 = esperimentationStr2 + "_gt" + gtmem;
				
				// weighted by construction sequences (& test sequences)
				// enrich with step1 urls in the suffix tree
				int[] nrecsWST = new int[]{2,3,4,5,10,20};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					modelev.setEsploitationParameters(fmode, gtmem, 0);
					results = modelev.computeEvaluationTest("ST_wTrain", nrec, (long)0);
					System.out.print(esperimentationStr3 + "_weighted" + nrec + ",");
					System.out.print(results);
				}
			
				// unbounded
				modelev.setEsploitationParameters(fmode, gtmem, 0);
				results = modelev.computeEvaluationTest("unbounded", 1000, (long)0);
				System.out.print(esperimentationStr3 + "_unbounded,");
				System.out.print(results);
			}
		}		
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
