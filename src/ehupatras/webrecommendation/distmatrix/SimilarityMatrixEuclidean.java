package ehupatras.webrecommendation.distmatrix;

import ehupatras.webrecommendation.sequencealignment.*;
import java.util.*;

public class SimilarityMatrixEuclidean 
				extends Matrix {
	
	public void computeMatrix(ArrayList<Integer> names, 
							ArrayList<String[]> data,
							float[][] roleWeights){
		m_names = names;
		m_matrix = new float[data.size()][data.size()];

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
				m_matrix[i][j] = (float)dist;
				m_matrix[j][i] = (float)dist;
			}
		}
	}
	
	private double getEuclideanDistance(float[] v1, float[] v2){
		int len = Math.min(v1.length, v2.length);
		double sum = 0d;
		for(int i=0; i<len; i++){
			double elem = (float)Math.pow((double)(v1[i]-v2[i]),2d);
			sum = sum + elem;
		}
		return Math.sqrt(sum);
	}
	
}
