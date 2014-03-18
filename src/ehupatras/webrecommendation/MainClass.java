package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.evaluator.*;
import java.util.*;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String dmWD = "/DM_00_no_role";
		dmWD = "";
		String validationWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		dmWD = args[3];
		validationWD = args[4];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		
		// LOAD PREPROCESSED LOGS //
		//A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		//preprocess.preprocessLogs(preprocessingWD, logfile);
		//preprocess.loadPreprocess();
		
		
		// LOAD DATABASE //
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		//database.createDatabase(databaseWD);
		database.loadDatabase(databaseWD);
		ArrayList<Integer> sampleSessionIDs = database.getSessionsIDs();
		ArrayList<String[]> sequencesUHC = database.getInstantiatedSequences();
		
		
		// DISTANCE MATRIX //
		A010MainClassDistanceMatrixEuclidean dm = new A010MainClassDistanceMatrixEuclidean();
		dm.loadDistanceMatrix(databaseWD + dmWD);
		Matrix matrix = dm.getMatrix();
		float[][] distmatrix = matrix.getMatrix();

		
		// HOLD-OUT //
		A020MainClassHoldOut ho = new A020MainClassHoldOut();
		ho.createParts(validationWD, sampleSessionIDs);
		ModelValidationHoldOut mv = ho.getParts();
		ArrayList<ArrayList<Integer>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Integer>> valAL   = mv.getValidation();
		ArrayList<ArrayList<Integer>> testAL  = mv.getTest();


		
		// MODEL VALIDATION //
	
		// Parameters to play with
		String[] linkages = 
			{"ehupatras.clustering.sapehac.agglomeration.AverageLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.CentroidLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.CompleteLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.MedianLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.SingleLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.WardLinkage"};
		int[] cutthA = {10, 15, 20};
		float[] seqweights = {0.10f, 0.15f, 0.20f};
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		
		// initialize the model evaluator
		ModelEvaluator modelev = new ModelEvaluatorUHC(sequencesUHC,matrix,trainAL,valAL,testAL);
		modelev.setFmeasureBeta(0.5f);
		modelev.setConfusionPoints(confusionPoints);
		System.out.print("options," + modelev.getEvaluationHeader());
		
		

		// MARKOV CHAIN //
		modelev.buildMarkovChains();

/*
		String resultsMarkov;
		
			// unbounded
		resultsMarkov = modelev.computeEvaluationTest(-1, -1, (long)0);
		System.out.print("markovchain_unbounded,");
		System.out.print(resultsMarkov);
		
			// random
		int[] nrecsR = new int[]{2,3,4,5,10,20};
		for(int i=0; i<nrecsR.length; i++ ){
			int nrec = nrecsR[i];
			resultsMarkov = modelev.computeEvaluationTest(0, nrec, (long)0);
			System.out.print("markovchain" + "_random" + nrec + ",");
			System.out.print(resultsMarkov);
		}

			// weighted
		int[] nrecsW = new int[]{2,3,4,5,10,20};
		for(int i=0; i<nrecsR.length; i++ ){
			int nrec = nrecsW[i];
			resultsMarkov = modelev.computeEvaluationTest(1, nrec, (long)0);
			System.out.print("markovchain" + "_weighted" + nrec + ",");
			System.out.print(resultsMarkov);
		}
*/
		
	
		// SUFFIX TREE //
		//modelev.resetModels();
		
		// Start generating and evaluating the model
		//for(int i=0; i<linkages.length; i++){
		int i = 5;
			String linkageClassName = linkages[i];
			for(int j=0; j<cutthA.length; j++){
				int cutth = cutthA[j];
				
				String esperimentationStr = "agglo" + i + "_cl" + cutth;
			
				// Clustering
				//modelev.buildClusters(cutth, linkageClassName);
				//modelev.saveClusters(validationWD + "/" + esperimentationStr + ".javaData");
				//modelev.writeClusters(validationWD + "/" + esperimentationStr + ".txt");
				modelev.loadClusters(validationWD + "/" + esperimentationStr + ".javaData");

				// Sequence Alignment
				modelev.clustersSequenceAlignment();
				modelev.writeAlignments(validationWD + "/" + esperimentationStr + "_alignments.txt");
			
				// Weighted Sequences
				for(int k=0; k<seqweights.length; k++){
					float minsup = seqweights[k];
					String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
					modelev.extractWeightedSequences(minsup);
					modelev.writeWeightedSequences(validationWD + "/" + esperimentationStr2 + ".txt");
			
					// Suffix Tree
					modelev.buildSuffixTrees();
			
					// Evaluation
					String results;
/*			
						// unbounded
					results = modelev.computeEvaluationTest(-1, -1, (long)0);
					System.out.print(esperimentationStr2 + "_unbounded,");
					System.out.print(results);
		
						// random
					int[] nrecsRST = new int[]{2,3,4,5,10,20};
					for(int ind=0; ind<nrecsRST.length; ind++ ){
						int nrec = nrecsRST[ind];
						results = modelev.computeEvaluationTest(0, nrec, (long)0);
						System.out.print(esperimentationStr2 + "_random" + nrec + ",");
						System.out.print(results);
					}
				
						// weightedTrain
					int[] nrecsWST1 = new int[]{2,3,4,5,10,20};
					for(int ind=0; ind<nrecsWST1.length; ind++ ){
						int nrec = nrecsWST1[ind];
						results = modelev.computeEvaluationTest(1, nrec, (long)0);
						System.out.print(esperimentationStr2 + "_TrainWeighted" + nrec + ",");
						System.out.print(results);
					}
					
						// weightedTest
					int[] nrecsWST2 = new int[]{2,3,4,5,10,20};
					for(int ind=0; ind<nrecsWST2.length; ind++ ){
						int nrec = nrecsWST2[ind];
						results = modelev.computeEvaluationTest(2, nrec, (long)0);
						System.out.print(esperimentationStr2 + "_TestWeighted" + nrec + ",");
						System.out.print(results);
					}
					
						// weighted
					int[] nrecsWST = new int[]{2,3,4,5,10,20};
					for(int ind=0; ind<nrecsWST.length; ind++ ){
						int nrec = nrecsWST[ind];
						results = modelev.computeEvaluationTest(3, nrec, (long)0);
						System.out.print(esperimentationStr2 + "_Weighted" + nrec + ",");
						System.out.print(results);
					}

						// weighted with markov
					int[] nrecsWSTM = new int[]{2,3,4,5,10,20};
					for(int ind=0; ind<nrecsWSTM.length; ind++ ){
						int nrec = nrecsWSTM[ind];
						results = modelev.computeEvaluationTest(4, nrec, (long)0);
						System.out.print(esperimentationStr2 + "_withMarkov" + nrec + ",");
						System.out.print(results);
					}
*/
					// weighted with markov
					int[] nrecsWSTOrig = new int[]{2,3,4,5,10,20};
					for(int ind=0; ind<nrecsWSTOrig.length; ind++ ){
						int nrec = nrecsWSTOrig[ind];
						results = modelev.computeEvaluationTest(5, nrec, (long)0, 0, 100);
						System.out.print(esperimentationStr2 + "_WeightedOrig" + nrec + ",");
						System.out.print(results);
					}
					
					
				}

			}
		//}
					
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}