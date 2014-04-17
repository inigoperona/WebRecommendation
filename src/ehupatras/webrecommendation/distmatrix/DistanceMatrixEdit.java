package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLevenshtein;

public class DistanceMatrixEdit extends Matrix {
	
	public DistanceMatrixEdit(ArrayList<Integer> names){
		m_names = names;
	}
	
	public void computeMatrix(ArrayList<String[]> data,
			float[][] roleWeights,
			boolean isplit){
		if(!isplit){
			m_matrix = new float[data.size()][data.size()];
		} else {
			m_matrixSplit = new float[data.size()][data.size()];
		}
		// create the similarity matrix
		for(int i=0; i<data.size(); i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing distances of row " + i);
			String[] seqA = data.get(i);
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentLevenshtein();
				seqalign.setRoleWeights(roleWeights);
				float dist = seqalign.getScore(seqA, seqB);
				if(!isplit){
					m_matrix[i][j] = dist;
				} else {
					m_matrixSplit[i][j] = dist;
				}
			}
		}
	}
	
}
