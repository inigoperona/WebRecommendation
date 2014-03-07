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
		String validationWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		validationWD = args[3];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		
		// LOAD PREPROCESSED LOGS //
		//System.out.println("PREPROCESSING");
		//MainClassPreprocess preprocess = new MainClassPreprocess();
		//preprocess.preprocessLogs(preprocessingWD, logfile);
		//preprocess.loadPreprocess();
		
		
		// CREATE THE DATABASE
		System.out.println("CREATE THE DATABASE");
		// Sampling
		ArrayList<Integer> sampleSessionIDs;
		//Sampling samp = new Sampling();
		//sampleSessionIDs = samp.getSample(8000, (long)0, false);
		SaveLoadObjects sosess = new SaveLoadObjects();
		//sosess.save(sampleSessionIDs, databaseWD + "/_sessionIDs.javaData");
		sampleSessionIDs = (ArrayList<Integer>)sosess.load(databaseWD + "/_sessionIDs.javaData");
		
		
		// INSTANCIATED SEQUENCES
		ArrayList<String[]> sequencesUHC;
		//sequencesUHC = WebAccessSequencesUHC.getSequencesInstanciated(sampleSessionIDs);
		SaveLoadObjects soseqs = new SaveLoadObjects();
		//soseqs.save(sequencesUHC, databaseWD + "/_sequencesUHC.javaData");
		sequencesUHC = (ArrayList<String[]>)soseqs.load(databaseWD + "/_sequencesUHC.javaData");
		
		
		// DISTANCE MATRIX //
		System.out.println("DISTANCE MATRIX");
		Matrix matrix = new SimilarityMatrixInverse();
		//matrix.computeMatrix(sampleSessionIDs, sequencesUHC);
		//matrix.save(databaseWD);
		//matrix.writeMatrix(databaseWD + "/distance_matrix.txt");
		matrix.load(databaseWD);
		float[][] distmatrix = matrix.getMatrix();
		
		
		

		// HOLD-OUT //
		System.out.println("HOLD-OUT");
		ModelValidationHoldOut honestmodelval = new ModelValidationHoldOut();
		//honestmodelval.prepareData(sampleSessionIDs, 70, 0, 30);
		//honestmodelval.save(validationWD);
		honestmodelval.load(validationWD);
		ArrayList<ArrayList<Integer>> trainAL = honestmodelval.getTrain();
		ArrayList<ArrayList<Integer>> valAL   = honestmodelval.getValidation();
		ArrayList<ArrayList<Integer>> testAL  = honestmodelval.getTest();

		
		// MODEL VALIDATION //
	
		// Parameters to play with
		String[] linkages = 
			{"ehupatras.clustering.sapehac.agglomeration.AverageLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.CentroidLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.CompleteLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.MedianLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.SingleLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.WardLinkage"};
		int[] cutthA = {3, 5, 10, 15};
		float[] seqweights = {0.15f, 0.20f, 0.25f, 0.50f, 0.75f};
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		
		// initialize the model evaluator
		ModelEvaluator modelev = new ModelEvaluatorUHC(sequencesUHC,matrix,trainAL,valAL,testAL);
		modelev.setFmeasureBeta(0.5f);
		modelev.setConfusionPoints(confusionPoints);
		System.out.print("options," + modelev.getEvaluationHeader());
		
		
	
		// MARKOV CHAIN //
/*
		modelev.buildMarkovChains();
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
		modelev.resetModels();
		
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
					int[] nrecsWST = new int[]{2,3,4,5,10,20};
					for(int ind=0; ind<nrecsWST.length; ind++ ){
						int nrec = nrecsWST[ind];
						results = modelev.computeEvaluationTest(1, nrec, (long)0);
						System.out.print(esperimentationStr2 + "_TrainWeighted" + nrec + ",");
						System.out.print(results);
					}
					
						// weightedTest
					int[] nrecsWST2 = new int[]{2,3,4,5,10,20};
					for(int ind=0; ind<nrecsWST.length; ind++ ){
						int nrec = nrecsWST2[ind];
						results = modelev.computeEvaluationTest(2, nrec, (long)0);
						System.out.print(esperimentationStr2 + "_TestWeighted" + nrec + ",");
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