package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.evaluator.*;
import java.util.*;

public class MainClass2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA/20140228_v6";
		String logfile = "/kk.log";
		//basedirectory = args[0];
		//logfile = args[1];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(basedirectory);
		Website.setWorkDirectory(basedirectory);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		
		// LOAD PREPROCESSED LOGS //
		System.out.println("PREPROCESSING");
		MainClassPreprocess preprocess = new MainClassPreprocess();
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
		
		System.out.println("MODEL_VALIDATION 50");
		//modelev.buildClusters(50);
		//modelev.saveClusters(basedirectory + "/_cl50.javaData");
		//modelev.writeClusters(basedirectory + "/cl50.txt");
		modelev.loadClusters(basedirectory + "/_cl50.javaData");
		modelev.clustersSequenceAlignment();
		//modelev.writeAlignments(basedirectory + "/cl50_alignments.txt");
		
		System.out.println("MODEL_VALIDATION 10");
		modelev.setCutDendrogramDissimilarityThreshold(10);
		//modelev.buildClusters();
		//modelev.saveClusters(basedirectory + "/_cl10.javaData");
		//modelev.writeClusters(basedirectory + "/cl10.txt");
		//modelev.createModels();
		//modelev.writeAlignments(basedirectory + "/cl10_alignments.txt");
		
		System.out.println("MODEL_VALIDATION 25");
		modelev.setCutDendrogramDissimilarityThreshold(25);
		//modelev.buildClusters();
		//modelev.saveClusters(basedirectory + "/_cl25.javaData");
		//modelev.writeClusters(basedirectory + "/cl25.txt");
		//modelev.createModels();
		//modelev.writeAlignments(basedirectory + "/cl25_alignments.txt");
		
		System.out.println("MODEL_VALIDATION 75");
		modelev.setCutDendrogramDissimilarityThreshold(75);
		//modelev.buildClusters();
		//modelev.saveClusters(basedirectory + "/_cl75.javaData");
		//modelev.writeClusters(basedirectory + "/cl75.txt");
		//modelev.createModels();
		//modelev.writeAlignments(basedirectory + "/cl75_alignments.txt");
		
		System.out.println("MODEL_VALIDATION 90");
		modelev.setCutDendrogramDissimilarityThreshold(90);
		//modelev.buildClusters();
		//modelev.saveClusters(basedirectory + "/_cl90.javaData");
		//modelev.writeClusters(basedirectory + "/cl90.txt");
		//modelev.createModels();
		//modelev.writeAlignments(basedirectory + "/cl90_alignments.txt");
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}