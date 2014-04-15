package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.SimilarityMatrixInverseTopics2;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A113MainClassDistanceMatrixInverseTopics2 {

	private Matrix m_matrix;
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Integer> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights,
			String dmFile,
			float topicmatch){
		m_matrix = new SimilarityMatrixInverseTopics2(dmFile, topicmatch);
		m_matrix.computeMatrix(sampleSessionIDs, sequencesUHC, roleWeights);
		m_matrix.save(databaseWD);
		m_matrix.writeMatrix(databaseWD + "/distance_matrix.txt");
	}
	
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new SimilarityMatrixInverseTopics2(null, 0.5f);
		m_matrix.load(databaseWD);
	}
	
	public Matrix getMatrix(){
		return m_matrix;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String dmWD = "/DM_00_no_role";
		dmWD = "";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		dmWD = args[3];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		
		
		// LOAD DATABASE //
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		//database.createDatabase(databaseWD);
		database.loadDatabase(databaseWD);
		ArrayList<Integer> sampleSessionIDs = database.getSessionsIDs();
		ArrayList<String[]> sequencesUHC = database.getInstantiatedSequences();		
		
		
		// DISTANCE MATRIX //
		A113MainClassDistanceMatrixInverseTopics2 dm;
		

		// No role
		float[][] roleW1 = {{ 1f, 1f, 1f},
				            { 1f, 1f, 1f},
				            { 1f, 1f, 1f}};
		dm = new A113MainClassDistanceMatrixInverseTopics2();
		dm.createDistanceMatrix(databaseWD + "/DM_00_no_role_dist_topics2", 
				sampleSessionIDs, sequencesUHC, 
				roleW1,
				preprocessingWD + "/URLs_to_topic.txt", 0.5f);
		
		// Treat the role intelligently2
		float[][] roleW5 = {{ 0f,    0f,    0f},
	  		    			{ 0f,    1f, 0.75f},
	  		    			{ 0f, 0.75f,    1f}};
		dm = new A113MainClassDistanceMatrixInverseTopics2();
		dm.createDistanceMatrix(databaseWD + "/DM_03_intelligent2_dist_topics2", 
				sampleSessionIDs, sequencesUHC, 
				roleW5,
				preprocessingWD + "/URLs_to_topic.txt", 0.5f);
		
		
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
