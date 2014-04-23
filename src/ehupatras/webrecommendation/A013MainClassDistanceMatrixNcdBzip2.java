package ehupatras.webrecommendation;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.DistanceMatrixNcdBzip2;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A013MainClassDistanceMatrixNcdBzip2 {
	
	private Matrix m_matrix;
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Long> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights){
		m_matrix = new DistanceMatrixNcdBzip2(sampleSessionIDs);
		m_matrix.computeMatrix(sequencesUHC, roleWeights, false);
		m_matrix.writeMatrix(m_matrix.getMatrix(false),
				databaseWD + "/distance_matrix.txt");
		
		m_matrix.save(databaseWD);
	}
	
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new DistanceMatrixNcdBzip2(null);
		m_matrix.load(databaseWD);
	}
	
	public Matrix getMatrix(){
		return m_matrix;
	}
	
	public static void main(String[] args){
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		//preprocessingWD = args[0];
		//logfile = args[1];
		//databaseWD = args[2];
		
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
		A013MainClassDistanceMatrixNcdBzip2 dm;
		
		// No role
		float[][] roleW1 = {{ 0f, 0f, 0f},
	            			{ 0f, 0f, 0f},
	            			{ 0f, 0f, 0f}};
		dm = new A013MainClassDistanceMatrixNcdBzip2();
		dm.createDistanceMatrix(databaseWD + "/DM_05_ncd_bzip2", 
				sampleSessionIDs, sequencesUHC, 
				roleW1);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
