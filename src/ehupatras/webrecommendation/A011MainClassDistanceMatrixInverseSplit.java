package ehupatras.webrecommendation;

import java.util.ArrayList;
import java.io.File;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.SimilarityMatrixInverse;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A011MainClassDistanceMatrixInverseSplit {
	
	private Matrix m_matrix;
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Long> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights,
			int[] starters){
		m_matrix = new SimilarityMatrixInverse(sampleSessionIDs);
		
		// create the distance matrix without splitting sequences
		//m_matrix.computeMatrix(sequencesUHC, roleWeights, false);
		//m_matrix.writeMatrix(m_matrix.getMatrix(false),
		//			databaseWD + "/distance_matrix.txt");
		
		// split the sequences 
		m_matrix.setSplitParameters(starters, 3);
		Object[] objA = m_matrix.splitSequences(sequencesUHC);
		ArrayList<Long> sesIDsSplit = (ArrayList<Long>)objA[0];
		ArrayList<String[]> seqsSplit = (ArrayList<String[]>)objA[1];
		m_matrix.writeSeqs(databaseWD + "/sequences_split.txt", 
				sesIDsSplit, seqsSplit);
		
		m_matrix.computeMatrix(seqsSplit, roleWeights, true);
		m_matrix.writeMatrix(m_matrix.getMatrix(true), 
					databaseWD + "/distance_matrix_split.txt");
		
		// save all matrix
		m_matrix.save(databaseWD);
	}
	
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new SimilarityMatrixInverse(null);
		m_matrix.load(databaseWD);
	}
	
	public Matrix getMatrix(){
		return m_matrix;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String preprocessingWD = "experiments/DATA";
		String logfile = "/log20000.log";
		String databaseWD = "experiments/DATA";
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
		A011MainClassDistanceMatrixInverseSplit dm;
		

		// No role
		float[][] roleW1 = {{ 1f, 1f, 1f},
				            { 1f, 1f, 1f},
				            { 1f, 1f, 1f}};
		dm = new A011MainClassDistanceMatrixInverseSplit();
		dm.createDistanceMatrix(databaseWD + "/DM00-no_role-split", 
				sampleSessionIDs, sequencesUHC, 
				roleW1, new int[]{11});
		
		// Treat the role intelligently2
		float[][] roleW5 = {{ 0f,    0f,    0f},
	  		    			{ 0f,    1f, 0.75f},
	  		    			{ 0f, 0.75f,    1f}};
		dm = new A011MainClassDistanceMatrixInverseSplit();
		dm.createDistanceMatrix(databaseWD + "/DM03-U_HC2-split", 
				sampleSessionIDs, sequencesUHC, 
				roleW5, new int[]{11});
		
		
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
