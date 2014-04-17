package ehupatras.webrecommendation;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.DistanceMatrixEdit;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.SimilarityMatrixEuclidean;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A014MainClassDistanceMatrixEDSplit {

	private Matrix m_matrix;
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Integer> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights){
		m_matrix = new DistanceMatrixEdit(sampleSessionIDs);
		m_matrix.setSplitParameters(new int[]{11}, 3);
		Object[] objA = m_matrix.splitSequences(sequencesUHC);
		ArrayList<Integer> sesIDsSplit = (ArrayList<Integer>)objA[0];
		ArrayList<String[]> seqsSplit = (ArrayList<String[]>)objA[1];
		m_matrix.setNamesSplit(sesIDsSplit);
		m_matrix.computeMatrix(seqsSplit, roleWeights, true);
		m_matrix.save(databaseWD);
		m_matrix.writeMatrix(m_matrix.getMatrixSplit(),
					databaseWD + "/distance_matrix_split.txt");
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
		A014MainClassDistanceMatrixEDSplit dm;
		
		// No role
		float[][] roleW1 = {{ 0f, 0f, 0f},
	            			{ 0f, 0f, 0f},
	            			{ 0f, 0f, 0f}};
		dm = new A014MainClassDistanceMatrixEDSplit();
		dm.createDistanceMatrix(databaseWD + "/DM04-edit-split", 
				sampleSessionIDs, sequencesUHC, 
				roleW1);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
