package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.distmatrix.*;
import ehupatras.webrecommendation.modelvalidation.*;
import ehupatras.webrecommendation.evaluator.*;
import java.util.*;

public class A010MainClassDistanceMatrixEuclidean {

	private Matrix m_matrix;
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Long> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights){
		m_matrix = new SimilarityMatrixEuclidean(sampleSessionIDs);
		m_matrix.computeMatrix(sequencesUHC, roleWeights, false);
		m_matrix.save(databaseWD);
		m_matrix.writeMatrix(m_matrix.getMatrix(false),
					databaseWD + "/distance_matrix.txt");
	}
	
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new SimilarityMatrixEuclidean(null);
		m_matrix.load(databaseWD);
	}
	
	public Matrix getMatrix(){
		return m_matrix;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/log20000.log";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		
		
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
		A010MainClassDistanceMatrixEuclidean dm;
		

		// No role
		float[][] roleW1 = {{ 1f, 1f, 1f},
				            { 1f, 1f, 1f},
				            { 1f, 1f, 1f}};
		dm = new A010MainClassDistanceMatrixEuclidean();
		dm.createDistanceMatrix(databaseWD + "/DM_00_no_role_data", 
				sampleSessionIDs, sequencesUHC, 
				roleW1);
		

		// 3 roles: U & H & C
		float[][] roleW2 = {{ 1f,-1f,-1f},
      		    			{-1f, 1f,-1f},
      		    			{-1f,-1f, 1f}};
		dm = new A010MainClassDistanceMatrixEuclidean();
		dm.createDistanceMatrix(databaseWD + "/DM_01_U_H_C_data", 
				sampleSessionIDs, sequencesUHC, 
				roleW2);
		
		/*
		// 2 roles: U & HC
		float[][] roleW3 = {{ 1f,-1f,-1f},
      		    			{-1f, 1f, 1f},
      		    			{-1f, 1f, 1f}};
		dm = new A010MainClassDistanceMatrixEuclidean();
		dm.createDistanceMatrix(databaseWD + "/DM_02_U_HC_data", 
				sampleSessionIDs, sequencesUHC, 
				roleW3);
		*/
				
		/*
		// Treat the role intelligently
		float[][] roleW4 = {{-1f,   -1f,   -1f},
	  		    			{-1f,    1f, 0.75f},
	  		    			{-1f, 0.75f,    1f}};
		dm = new A010MainClassDistanceMatrixEuclidean();
		dm.createDistanceMatrix(databaseWD + "/DM_03_intelligent_data", 
				sampleSessionIDs, sequencesUHC, 
				roleW4);
		*/
		
		// Treat the role intelligently2
		float[][] roleW5 = {{ 0f,    0f,    0f},
	  		    			{ 0f,    1f, 0.75f},
	  		    			{ 0f, 0.75f,    1f}};
		dm = new A010MainClassDistanceMatrixEuclidean();
		dm.createDistanceMatrix(databaseWD + "/DM_03_intelligent2_data", 
				sampleSessionIDs, sequencesUHC, 
				roleW5);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}