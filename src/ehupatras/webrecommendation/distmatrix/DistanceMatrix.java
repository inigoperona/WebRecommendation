package ehupatras.webrecommendation.distmatrix;

import ehupatras.webrecommendation.sequencealignment.*;

import java.util.*;

public class DistanceMatrix 
				extends Matrix {
	
	public float[][] getMatrix(ArrayList<String[]> data){
		m_matrix = new float[data.size()][data.size()];
		for(int i=0; i<data.size(); i++){
			String[] seqA = data.get(i);
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentCombineGlobalLocal();
				m_matrix[i][j] = seqalign.getTweakedDistance(seqA, seqB);
			}
		}
		return m_matrix;
	}

}
