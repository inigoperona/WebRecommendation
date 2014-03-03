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
		String logfile = "/kk.log";
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
		ModelEvaluator modelev = new ModelEvaluatorUHC(sequencesUHC,matrix,trainAL,valAL,testAL);
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// Parameters to play with
		String[] linkages = 
			{"ehupatras.clustering.sapehac.agglomeration.AverageLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.CentroidLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.CompleteLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.MedianLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.SingleLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.WardLinkage"};
		int[] cutthA = {5, 10, 15, 20, 25, 30, 40, 50};
		float[] seqweights = {0.25f, 0.40f, 0.50f, 0.6f, 0.7f, 0.8f};
		float[] confusionPoints = {0.00f,0.10f,0.25f,0.50f,0.75f,0.90f,1.00f};
		
		// Start generating andd evaluating the model
		for(int i=0; i<linkages.length; i++){
			String linkageClassName = linkages[i];
			for(int j=0; j<cutthA.length; j++){
				int cutth = cutthA[j];
				
				String esperimentationStr = "agglo" + i + "_cl" + cutth;
			
				// Clustering
				modelev.buildClusters(cutth, linkageClassName);
				modelev.saveClusters(basedirectory + "/" + esperimentationStr + ".javaData");
				modelev.writeClusters(basedirectory + "/" + esperimentationStr + ".txt");
				//modelev.loadClusters(basedirectory + "/" + esperimentationStr + ".javaData");
			
				/*
				// Sequence Alignment
				modelev.clustersSequenceAlignment();
				modelev.writeAlignments(basedirectory + "/cl" + cutth + "_alignments.txt");
			
				// Weighted Sequences
				modelev.extractWeightedSequences(0.25f);
				modelev.writeWeightedSequences(basedirectory + "/cl" + cutth + "_ws0.25.txt");
			
				// Suffix Tree
				modelev.buildSuffixTrees();
			
				// Evaluation
				modelev.setConfusionPoints(confusionPoints);
				modelev.setFmeasureBeta(0.5f);
				String results = modelev.computeEvaluationTest();
				System.out.print(esperimentationStr + ",");
				System.out.print(results);
				*/
			}
		}
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}