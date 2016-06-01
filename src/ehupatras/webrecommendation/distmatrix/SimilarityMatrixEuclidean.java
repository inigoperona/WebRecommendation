package ehupatras.webrecommendation.distmatrix;

import ehupatras.webrecommendation.sequencealignment.*;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class SimilarityMatrixEuclidean.
 */
public class SimilarityMatrixEuclidean 
				extends Matrix {
	
	/**
	 * Instantiates a new similarity matrix euclidean.
	 *
	 * @param names the names
	 */
	public SimilarityMatrixEuclidean(ArrayList<String> names){
		m_names = names;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.distmatrix.Matrix#computeMatrix(java.util.ArrayList, float[][], boolean)
	 */
	public void computeMatrix(ArrayList<String[]> data,
							float[][] roleWeights,
							boolean isplit,
							String workdirectory){
		if(!isplit){
			m_matrix = new MatrixStructure(false, data.size(), workdirectory);
		} else {
			m_matrixSplit = new MatrixStructure(false, data.size(), workdirectory);
		}
		
		// create the similarity matrix
		float[][] similaritiesM = new float[data.size()][data.size()];
		for(int i=0; i<data.size(); i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing similarities of row " + i);
			String[] seqA = data.get(i);
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentCombineGlobalLocalDimopoulos2010();
				seqalign.setRoleWeights(roleWeights);
				float sim = seqalign.getScore(seqA, seqB);
				similaritiesM[i][j] = sim;
			}
		}

		// create the distance matrix
		// to do that take the similarity-matrix as a data-matrix
		// we represent each sequence in a space
		for(int i=0; i<data.size(); i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing euclidean of row " + i);
			float[] vectori = similaritiesM[i];
			for(int j=(i+1); j<data.size(); j++){
				float[] vectorj = similaritiesM[j];
				double dist = this.getEuclideanDistance(vectori, vectorj);
				if(!isplit){
					m_matrix.addCell(i, j, (float)dist);
					m_matrix.addCell(j, i, (float)dist);
				} else {
					m_matrixSplit.addCell(i, j, (float)dist);
					m_matrixSplit.addCell(j, i, (float)dist);
				}
			}
		}
	}
	
	/**
	 * Gets the euclidean distance.
	 *
	 * @param v1 the v1
	 * @param v2 the v2
	 * @return the euclidean distance
	 */
	private double getEuclideanDistance(float[] v1, float[] v2){
		int len = Math.min(v1.length, v2.length);
		double sum = 0d;
		for(int i=0; i<len; i++){
			double elem = (float)Math.pow((double)(v1[i]-v2[i]),2d);
			sum = sum + elem;
		}
		return Math.sqrt(sum);
	}
	
	public void computeMatrix2(ArrayList<String[]> data,
			float[][] roleWeights,
			boolean isplit,
			String workdirectory){
		
	}
	
}
