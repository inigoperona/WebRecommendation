package angelu.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.DistanceMatrixEdit;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.SimilarityMatrixEuclidean;

// TODO: Auto-generated Javadoc
/**
 * The Class A012MainClassDistanceMatrixED.
 */
public class A012MainClassDistanceMatrixED {
	
	/** The m_matrix. */
	private Matrix m_matrix;
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		
		String wd = "20160525_experiments_BT";
		String[] args2 = {wd+"/00_preprocess", "xxx", "-1", "xxx", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "/DM_LG",
				"xxx", "xxx", "xxx", "xxx", "-", "1"};
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.createDM();
		
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
			ArrayList<String> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights){
		m_matrix = new DistanceMatrixEdit(sampleSessionIDs);
		m_matrix.computeMatrix(sequencesUHC, roleWeights, false, databaseWD);
		//m_matrix.computeMatrix2(sequencesUHC, roleWeights, false, databaseWD);
		m_matrix.writeMatrix(m_matrix.getMatrix(false),
				databaseWD + "/distance_matrix.txt");
		m_matrix.save(databaseWD);
	}
	
	/**
	 * Load distance matrix.
	 *
	 * @param databaseWD the database wd
	 */
	public void loadDistanceMatrix(String databaseWD, boolean isTXT){
		m_matrix = new SimilarityMatrixEuclidean(null);
		m_matrix.load(databaseWD, isTXT);
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
