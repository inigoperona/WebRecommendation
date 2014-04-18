package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.evaluator.*;
import java.util.*;

public class A040MainClassHclust {

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
		//dmWD = "";
		String validationWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String clustWD = "/CL_00_no_role";
		clustWD = "";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		dmWD = args[3];
		validationWD = args[4];
		clustWD = args[5];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
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
	
		// Parameters to play with
		// Agglomeration method of Hierarchical Clustering
		String[] linkages = 
			{"ehupatras.clustering.sapehac.agglomeration.AverageLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.CentroidLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.CompleteLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.MedianLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.SingleLinkage",
			 "ehupatras.clustering.sapehac.agglomeration.WardLinkage"};
		int i = 5;
		String linkageClassName = linkages[i];
		// Cutting the dendrogram
		//int[] cutthA = {10, 15, 20, 25};
		float[] cutthA = {4f, 10f, 15f, 20f, 25f};
		//int[] cutthA = {1, 2, 4, 6, 8};
		//float[] cutthA = {0.1f, 0.2f, 0.4f, 0.6f, 0.8f};
		
		// initialize the model evaluator
		ModelEvaluator modelev = new ModelEvaluatorUHC(sequencesUHC, null,
				matrix, trainAL, valAL, testAL);
		modelev.setFmeasureBeta(0.5f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);		
	
		// HIERARCHICAL CLUSTERING //
		for(int j=0; j<cutthA.length; j++){ // for each height
			float cutth = cutthA[j];
				
			String esperimentationStr = "agglo" + i + "_cl" + cutth;
			System.out.println("[" + System.currentTimeMillis() + "] " + esperimentationStr);
			
			// Clustering
			modelev.buildClustersH(cutth, linkageClassName);
			modelev.saveClusters(validationWD + clustWD + "/" + esperimentationStr + ".javaData");
			modelev.writeClusters(validationWD + clustWD + "/" + esperimentationStr + ".txt");
			//modelev.loadClusters(validationWD + "/" + esperimentationStr + ".javaData");
		}
					
			
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}
