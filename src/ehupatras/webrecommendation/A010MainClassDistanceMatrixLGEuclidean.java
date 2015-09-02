package ehupatras.webrecommendation;

import ehupatras.webrecommendation.distmatrix.*;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class A010MainClassDistanceMatrixLGEuclidean.
 */
public class A010MainClassDistanceMatrixLGEuclidean {

	/** The m_matrix. */
	private Matrix m_matrix;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
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
	
	
	
	
	
	/**
	 * Creates the distance matrix.
	 *
	 * @param databaseWD the database wd
	 * @param sampleSessionIDs the sample session i ds
	 * @param sequencesUHC the sequences uhc
	 * @param roleWeights the role weights
	 */
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Long> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights){
		m_matrix = new SimilarityMatrixEuclidean(sampleSessionIDs);
		m_matrix.computeMatrix(sequencesUHC, roleWeights, false, databaseWD);
		m_matrix.save(databaseWD);
		m_matrix.writeMatrix(m_matrix.getMatrix(false),
					databaseWD + "/distance_matrix.txt");
	}
	
	/**
	 * Load distance matrix.
	 *
	 * @param databaseWD the database wd
	 */
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new SimilarityMatrixEuclidean(null);
		m_matrix.load(databaseWD);
	}
	
	/**
	 * Gets the matrix.
	 *
	 * @return the matrix
	 */
	public Matrix getMatrix(){
		return m_matrix;
	}
}