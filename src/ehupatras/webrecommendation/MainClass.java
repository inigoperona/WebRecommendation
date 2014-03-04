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
		String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA/20140228_v6";
		//String logfile = "/kk.log";
		basedirectory = args[0];
		//logfile = args[1];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(basedirectory);
		Website.setWorkDirectory(basedirectory);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		
		// LOAD PREPROCESSED LOGS //
		//System.out.println("PREPROCESSING");
		//MainClassPreprocess preprocess = new MainClassPreprocess();
		//preprocess.preprocessLogs(basedirectory, logfile);
		//preprocess.loadPreprocess();
		
		
		// CREATE THE DATABASE
		System.out.println("CREATE THE DATABASE");
		// Sampling
		//Sampling samp = new Sampling();
		//ArrayList<Integer> sampleSessionIDs = samp.getSample(10000, (long)0, false);
		SaveLoadObjects sosess = new SaveLoadObjects();
		//sosess.save(sampleSessionIDs, basedirectory + "/_sessionIDs.javaData");
		ArrayList<Integer> sampleSessionIDs = (ArrayList<Integer>)sosess.load(basedirectory + "/_sessionIDs.javaData");
		
		
		// INSTANCIATED SEQUENCES
		//ArrayList<String[]> sequencesUHC = WebAccessSequencesUHC.getSequencesInstanciated(sampleSessionIDs);
		SaveLoadObjects soseqs = new SaveLoadObjects();
		//soseqs.save(sequencesUHC, basedirectory + "/_sequencesUHC.javaData");
		ArrayList<String[]> sequencesUHC = (ArrayList<String[]>)soseqs.load(basedirectory + "/_sequencesUHC.javaData");
		
		
		// DISTANCE MATRIX //
		System.out.println("DISTANCE MATRIX");
		Matrix matrix = new SimilarityMatrix();
		//matrix.computeMatrix(sampleSessionIDs, sequencesUHC);
		//matrix.save(basedirectory);
		//matrix.writeMatrix(basedirectory + "/distance_matrix.txt");
		matrix.load(basedirectory);
		float[][] distmatrix = matrix.getMatrix();
		
		
		// HOLD-OUT //
		System.out.println("HOLD-OUT");
		ModelValidationHoldOut honestmodelval = new ModelValidationHoldOut();
		honestmodelval.prepareData(sampleSessionIDs, 70, 0, 30);
		honestmodelval.save(basedirectory);
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
		int[] cutthA = {5, 10, 15, 20, 25, 30, 40, 50};
		float[] seqweights = {0.25f, 0.50f, 0.75f};
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		
		// initialize the model evaluator
		ModelEvaluator modelev = new ModelEvaluatorUHC(sequencesUHC,matrix,trainAL,valAL,testAL);
		modelev.setFmeasureBeta(0.5f);
		modelev.setConfusionPoints(confusionPoints);
		System.out.print("options," + modelev.getEvaluationHeader());
		
		
		
		// MARKOV CHAIN
		modelev.buildMarkovChains();
		String resultsMarkov;
		
		resultsMarkov = modelev.computeEvaluationTest(-1, -1, (long)0);
		System.out.print("markovchain_unbounded,");
		System.out.print(resultsMarkov);
		
		// random
		resultsMarkov = modelev.computeEvaluationTest(0, 2, (long)0);
		System.out.print("markovchain" + "_random2,");
		System.out.print(resultsMarkov);
		
		resultsMarkov = modelev.computeEvaluationTest(0, 3, (long)0);
		System.out.print("markovchain" + "_random3,");
		System.out.print(resultsMarkov);
		
		resultsMarkov = modelev.computeEvaluationTest(0, 4, (long)0);
		System.out.print("markovchain" + "_random4,");
		System.out.print(resultsMarkov);
		
		resultsMarkov = modelev.computeEvaluationTest(0, 10, (long)0);
		System.out.print("markovchain" + "_random10,");
		System.out.print(resultsMarkov);
		
		resultsMarkov = modelev.computeEvaluationTest(0, 20, (long)0);
		System.out.print("markovchain" + "_random20,");
		System.out.print(resultsMarkov);

		// weighted
		resultsMarkov = modelev.computeEvaluationTest(1, 2, (long)0);
		System.out.print("markovchain" + "_weighted2,");
		System.out.print(resultsMarkov);
		
		resultsMarkov = modelev.computeEvaluationTest(1, 3, (long)0);
		System.out.print("markovchain" + "_weighted3,");
		System.out.print(resultsMarkov);
		
		resultsMarkov = modelev.computeEvaluationTest(1, 4, (long)0);
		System.out.print("markovchain" + "_weighted4,");
		System.out.print(resultsMarkov);
		
		resultsMarkov = modelev.computeEvaluationTest(1, 10, (long)0);
		System.out.print("markovchain" + "_weighted10,");
		System.out.print(resultsMarkov);
		
		resultsMarkov = modelev.computeEvaluationTest(1, 20, (long)0);
		System.out.print("markovchain" + "_weighted20,");
		System.out.print(resultsMarkov);
		
		
		
		/*
		// SUFFIX TREE
		modelev.resetModels();
		// Start generating and evaluating the model
		//for(int i=0; i<linkages.length; i++){
		int i = 5;
			String linkageClassName = linkages[i];
			//for(int j=0; j<cutthA.length; j++){
			for(int j=0; j<3; j++){
				int cutth = cutthA[j];
				
				String esperimentationStr = "agglo" + i + "_cl" + cutth;
			
				// Clustering
				//modelev.buildClusters(cutth, linkageClassName);
				//modelev.saveClusters(basedirectory + "/" + esperimentationStr + ".javaData");
				//modelev.writeClusters(basedirectory + "/" + esperimentationStr + ".txt");
				modelev.loadClusters(basedirectory + "/" + esperimentationStr + ".javaData");
			
				// Sequence Alignment
				modelev.clustersSequenceAlignment();
				modelev.writeAlignments(basedirectory + "/" + esperimentationStr + "_alignments.txt");
			
				// Weighted Sequences
				for(int k=0; k<seqweights.length; k++){
					float minsup = seqweights[k];
					String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
					modelev.extractWeightedSequences(minsup);
					modelev.writeWeightedSequences(basedirectory + "/" + esperimentationStr2 + ".txt");
			
					// Suffix Tree
					modelev.buildSuffixTrees();
			
					// Evaluation
					String results;
					
					// unbounded
					results = modelev.computeEvaluationTest(-1, -1, (long)0, false);
					System.out.print(esperimentationStr2 + "_unbounded,");
					System.out.print(results);
					
					// random
					results = modelev.computeEvaluationTest(0, 2, (long)0, false);
					System.out.print(esperimentationStr2 + "_random2,");
					System.out.print(results);
					
					results = modelev.computeEvaluationTest(0, 3, (long)0, false);
					System.out.print(esperimentationStr2 + "_random3,");
					System.out.print(results);
					
					results = modelev.computeEvaluationTest(0, 4, (long)0, false);
					System.out.print(esperimentationStr2 + "_random4,");
					System.out.print(results);
					
					results = modelev.computeEvaluationTest(0, 10, (long)0, false);
					System.out.print(esperimentationStr2 + "_random10,");
					System.out.print(results);
					
					results = modelev.computeEvaluationTest(0, 20, (long)0, false);
					System.out.print(esperimentationStr2 + "_random20,");
					System.out.print(results);
				
					// weighted
					results = modelev.computeEvaluationTest(1, 2, (long)0, false);
					System.out.print(esperimentationStr2 + "_weighted2,");
					System.out.print(results);
					
					results = modelev.computeEvaluationTest(1, 3, (long)0, false);
					System.out.print(esperimentationStr2 + "_weighted3,");
					System.out.print(results);
					
					results = modelev.computeEvaluationTest(1, 4, (long)0, false);
					System.out.print(esperimentationStr2 + "_weighted4,");
					System.out.print(results);
					
					results = modelev.computeEvaluationTest(1, 10, (long)0, false);
					System.out.print(esperimentationStr2 + "_weighted10,");
					System.out.print(results);
					
					results = modelev.computeEvaluationTest(1, 20, (long)0, false);
					System.out.print(esperimentationStr2 + "_weighted20,");
					System.out.print(results);
					
					
				}
			}
		//}
		
		*/
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}