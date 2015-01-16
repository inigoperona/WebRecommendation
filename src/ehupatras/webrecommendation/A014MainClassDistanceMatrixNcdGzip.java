package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.DistanceMatrixNcdGzip;
import ehupatras.webrecommendation.distmatrix.Matrix;

// TODO: Auto-generated Javadoc
/**
 * The Class A014MainClassDistanceMatrixNcdGzip.
 */
public class A014MainClassDistanceMatrixNcdGzip {

	/** The m_matrix. */
	private Matrix m_matrix;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){

		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		
		// DISTANCE MATRIX
		float[][] rolesW;
		
		// No role
		rolesW = new float[][]{	{ 0f, 0f, 0f},
	            				{ 0f, 0f, 0f},
	            				{ 0f, 0f, 0f}};
		param.createDM("NCD_gzip", rolesW, 
				param.getDMdirectory(), new int[0]);
		
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
		m_matrix = new DistanceMatrixNcdGzip(sampleSessionIDs);
		m_matrix.computeMatrix(sequencesUHC, roleWeights, false);
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
		m_matrix = new DistanceMatrixNcdGzip(null);
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
