package ehupatras.webrecommendation;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.DistanceMatrixEditTopics;

// TODO: Auto-generated Javadoc
/**
 * The Class A112MainClassDistanceMatrixEDTopics.
 */
public class A112MainClassDistanceMatrixEDTopics {
	
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
		rolesW = new float[][]{	{ 0f, 0f, 0f},
    							{ 0f, 0f, 0f},
    							{ 0f, 0f, 0f}};
		param.createDM("EditDistance_TopicCont", rolesW, 
				"/DM_04_edit_TopicCont", new int[0]);
		
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
	 * @param dmFile the dm file
	 * @param urlsEqualnessThreshold the urls equalness threshold
	 */
	public void createDistanceMatrix(String databaseWD,
			ArrayList<String> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights,
			String dmFile,
			float urlsEqualnessThreshold){
		m_matrix = new DistanceMatrixEditTopics(sampleSessionIDs, dmFile, urlsEqualnessThreshold);
		m_matrix.computeMatrix(sequencesUHC, roleWeights, false, databaseWD);
		m_matrix.writeMatrix(m_matrix.getMatrix(false),
					databaseWD + "/distance_matrix.txt");
		
		m_matrix.save(databaseWD);
	}
	
	/**
	 * Load distance matrix.
	 *
	 * @param databaseWD the database wd
	 */
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new DistanceMatrixEditTopics(null, null, 0.6f);
		m_matrix.load(databaseWD,false);
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
