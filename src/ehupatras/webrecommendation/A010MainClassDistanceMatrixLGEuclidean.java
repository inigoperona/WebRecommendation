package ehupatras.webrecommendation;

import ehupatras.webrecommendation.distmatrix.*;
import java.util.*;

public class A010MainClassDistanceMatrixLGEuclidean {

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
		param.createDM("SimilarityMatrixAsDataMatrix", rolesW, 
				param.getDMdirectory() + "_norole", new int[0]);
		
		/*
		// 3 roles: U & H & C
		rolesW = new float[][]{	{ 1f,-1f,-1f},
	    						{-1f, 1f,-1f},
	    						{-1f,-1f, 1f}};
		param.createDM("SimilarityMatrixAsDataMatrix", rolesW, 
				"/DM_01_U_H_C_data", new int[0]);
		*/
		
		/*
		// 2 roles: U & equal_HC
		rolesW = new float[][]{	{ 1f,-1f,-1f},
      		    				{-1f, 1f, 1f},
		      		    		{-1f, 1f, 1f}};
		param.createDM("SimilarityMatrixAsDataMatrix", rolesW, 
				"/DM_02_U_HC_data", new int[0]);
		 */
		
		/*
		// 2 roles: diff_U & similar_HC
		rolesW = new float[][]{	{-1f,   -1f,   -1f},
	  		    				{-1f,    1f, 0.75f},
	  		    				{-1f, 0.75f,    1f}};
		param.createDM("SimilarityMatrixAsDataMatrix", rolesW, 
				"/DM_03_similarHC_data", new int[0]);
		 */
		
		// 2 roles: similar_HC
		rolesW = new float[][]{	{ 0f,    0f,    0f},
	    						{ 0f,    1f, 0.75f},
	    						{ 0f, 0.75f,    1f}};
		param.createDM("SimilarityMatrixAsDataMatrix", rolesW, 
				param.getDMdirectory() + "_wirole", new int[0]);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");		
	}
	
	
	
	
	
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
}