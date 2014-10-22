package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.DistanceMatrixNcdGzip;
import ehupatras.webrecommendation.distmatrix.Matrix;

public class A014MainClassDistanceMatrixNcdGzip {

	private Matrix m_matrix;
	
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
				"/DM_05_ncd_gzip", new int[0]);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
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
	
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new DistanceMatrixNcdGzip(null);
		m_matrix.load(databaseWD);
	}
	
	public Matrix getMatrix(){
		return m_matrix;
	}
	
}
