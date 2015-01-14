package ehupatras.webrecommendation;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.DistanceMatrixEdit;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.distmatrix.SimilarityMatrixEuclidean;

public class A012MainClassDistanceMatrixEDSplit {

	private Matrix m_matrix;

	public static void main(String[] args){
		
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
		param.createDM("EditDistance_Split", rolesW, 
				param.getDMdirectory(), sessionBreakers);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

	
	
	
	
	
	public void createDistanceMatrix(String databaseWD,
			ArrayList<Long> sampleSessionIDs,
			ArrayList<String[]> sequencesUHC,
			float[][] roleWeights,
			int[] starters){
		m_matrix = new DistanceMatrixEdit(sampleSessionIDs);
		
		// split the sequences 
		m_matrix.setSplitParameters(starters, 3);
		Object[] objA = m_matrix.splitSequences(sequencesUHC);
		ArrayList<Long> sesIDsSplit = (ArrayList<Long>)objA[0];
		ArrayList<String[]> seqsSplit = (ArrayList<String[]>)objA[1];
		m_matrix.writeSeqs(databaseWD + "/sequences_split.txt", 
				sesIDsSplit, seqsSplit);
		
		m_matrix.computeMatrix(seqsSplit, roleWeights, true);
		m_matrix.writeMatrix(m_matrix.getMatrix(true), 
					databaseWD + "/distance_matrix_split.txt");
		
		// save all matrix
		m_matrix.save(databaseWD);
	}
	
	public void loadDistanceMatrix(String databaseWD){
		m_matrix = new SimilarityMatrixEuclidean(null);
		m_matrix.load(databaseWD);
	}
	
	public Matrix getMatrix(){
		return m_matrix;
	}	
}
