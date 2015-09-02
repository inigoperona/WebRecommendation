package ehupatras.webrecommendation;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.SimilarityMatrixInverse;

// TODO: Auto-generated Javadoc
/**
 * The Class A011MainClassDistanceMatrixLGInverseSplit.
 */
public class A011MainClassDistanceMatrixLGInverseSplit {
	
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
		int[] sessionBreakers = new int[]{11};
		
		// No role: equal_UHC
		rolesW = new float[][]{	{ 1f, 1f, 1f},
	    						{ 1f, 1f, 1f},
	    						{ 1f, 1f, 1f}};
		param.createDM("SimilarityMatrixNormalize_Split", rolesW, 
				param.getDMdirectory() + "_norole", sessionBreakers);
		
		// 2 roles: similar_HC
		rolesW = new float[][]{	{ 0f,    0f,    0f},
	    						{ 0f,    1f, 0.75f},
	    						{ 0f, 0.75f,    1f}};
		param.createDM("SimilarityMatrixNormalize_Split", rolesW, 
				param.getDMdirectory() + "_wirole", sessionBreakers);
		
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
	 * @param starters the starters
	 */
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Long> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights,
			int[] starters){
		m_matrix = new SimilarityMatrixInverse(sampleSessionIDs);
		
		// split the sequences 
		m_matrix.setSplitParameters(starters, 3);
		Object[] objA = m_matrix.splitSequences(sequencesUHC);
		ArrayList<Long> sesIDsSplit = (ArrayList<Long>)objA[0];
		ArrayList<String[]> seqsSplit = (ArrayList<String[]>)objA[1];
		m_matrix.writeSeqs(databaseWD + "/sequences_split.txt", 
				sesIDsSplit, seqsSplit);
		
		m_matrix.computeMatrix(seqsSplit, roleWeights, true, databaseWD);
		m_matrix.writeMatrix(m_matrix.getMatrix(true), 
					databaseWD + "/distance_matrix_split.txt");
		
		// save all matrix
		m_matrix.save(databaseWD);
	}
	
	/**
	 * Load distance matrix.
	 *
	 * @param databaseWD the database wd
	 */
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new SimilarityMatrixInverse(null);
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
