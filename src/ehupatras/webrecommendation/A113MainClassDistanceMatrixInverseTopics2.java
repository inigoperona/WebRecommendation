package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.SimilarityMatrixInverseTopics2;

public class A113MainClassDistanceMatrixInverseTopics2 {

	private Matrix m_matrix;
	
	public static void main(String[] args) {
		
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		
		// DISTANCE MATRIX
		float[][] rolesW;
		
		// No role: equal_UHC
		rolesW = new float[][]{	{ 1f, 1f, 1f},
	    						{ 1f, 1f, 1f},
	    						{ 1f, 1f, 1f}};
		param.createDM("SimilarityMatrixNormalize_TopicDisc", rolesW, 
				"/DM_00_no_role_dist_topicsDisc", new int[0]);
		
		// 2 roles: similar_HC
		rolesW = new float[][]{	{ 0f,    0f,    0f},
	    						{ 0f,    1f, 0.75f},
	    						{ 0f, 0.75f,    1f}};
		param.createDM("SimilarityMatrixNormalize_TopicDisc", rolesW, 
				"/DM_03_intelligent2_dist_topicsDisc", new int[0]);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
	
	
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Long> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights,
			String dmFile,
			float topicmatch){
		m_matrix = new SimilarityMatrixInverseTopics2(sampleSessionIDs, dmFile, topicmatch);
		m_matrix.computeMatrix(sequencesUHC, roleWeights, false);
		m_matrix.writeMatrix(m_matrix.getMatrix(false),
					databaseWD + "/distance_matrix.txt");
		
		m_matrix.save(databaseWD);
	}
	
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new SimilarityMatrixInverseTopics2(null, null, 0.5f);
		m_matrix.load(databaseWD);
	}
	
	public Matrix getMatrix(){
		return m_matrix;
	}
	
}
