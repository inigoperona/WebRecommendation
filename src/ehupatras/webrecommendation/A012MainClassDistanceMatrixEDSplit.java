package ehupatras.webrecommendation;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.DistanceMatrixEdit;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.SimilarityMatrixEuclidean;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A012MainClassDistanceMatrixEDSplit {

	private Matrix m_matrix;
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Long> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights,
			int[] starters){
		m_matrix = new DistanceMatrixEdit(sampleSessionIDs);
		
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
		m_matrix = new SimilarityMatrixEuclidean(null);
		m_matrix.load(databaseWD);
	}
	
	public Matrix getMatrix(){
		return m_matrix;
	}
	
	public static void main(String[] args){
		// Parameter control
		String base = "experiments_ehupatras";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/log20000.log";
		String databaseWD = base + "/02_DATABASE_5";
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
		A012MainClassDistanceMatrixEDSplit dm;
		
		// No role
		float[][] roleW1 = {{ 0f, 0f, 0f},
	            			{ 0f, 0f, 0f},
	            			{ 0f, 0f, 0f}};
		dm = new A012MainClassDistanceMatrixEDSplit();
		dm.createDistanceMatrix(databaseWD + "/DM_04_edit-split", 
				sampleSessionIDs, sequencesUHC, 
				roleW1, new int[]{11});
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
