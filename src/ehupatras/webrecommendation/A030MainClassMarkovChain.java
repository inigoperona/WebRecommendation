package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.evaluator.*;
import java.util.*;

public class A030MainClassMarkovChain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
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
		//ho.createParts(validationWD, sampleSessionIDs);
		ModelValidationHoldOut mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL   = mv.getValidation();
		ArrayList<ArrayList<Long>> testAL  = mv.getTest();


		// build model: MC
		ModelEvaluatorMarkovChain modelev = new ModelEvaluatorMarkovChain(
				sequencesUHC, null, 
				matrix, 
				trainAL, valAL, testAL,
				0);
		modelev.buildMC();
		
		// Evaluation parameters
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
		
		
		// write result headers
		System.out.print("options," + modelev.getEvaluationHeader());

		// compute results
		String resultsMarkov;
		
			// random
		/*
		int[] nrecsR = new int[]{2,3,4,5,10,20};
		for(int i=0; i<nrecsR.length; i++ ){
			int nrec = nrecsR[i];
			resultsMarkov = modelev.computeEvaluationTest(0, nrec, (long)0, 0, 100, false, null);
			System.out.print("markovchain" + "_random" + nrec + ",");
			System.out.print(resultsMarkov);
		}
		*/

			// weighted
		int[] nrecsW = new int[]{2,3,4,5,10,20};
		for(int i=0; i<nrecsW.length; i++ ){
			int nrec = nrecsW[i];
			resultsMarkov = modelev.computeEvaluationTest("weighted", nrec, (long)0);
			System.out.print("markovchain" + "_weighted" + nrec + ",");
			System.out.print(resultsMarkov);
		}
		
			// unbounded
		resultsMarkov = modelev.computeEvaluationTest("unbounded", -1, (long)0);
		System.out.print("markovchain_unbounded,");
		System.out.print(resultsMarkov);
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}