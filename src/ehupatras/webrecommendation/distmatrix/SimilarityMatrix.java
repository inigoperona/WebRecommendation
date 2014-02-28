package ehupatras.webrecommendation.distmatrix;

import ehupatras.webrecommendation.sequencealignment.*;
import java.util.*;

public class SimilarityMatrix 
				extends Matrix {
	
	public void computeMatrix(ArrayList<Integer> names, ArrayList<String[]> data){
		m_names = names;
		m_matrix = new float[data.size()][data.size()];
		for(int i=0; i<data.size(); i++){
			// compute each row similarities
			// and check the maximum and minimum values to create the distance
			String[] seqA = data.get(i);
			float[] similaritiesrow = new float[data.size()];
			float maxsim = Float.NEGATIVE_INFINITY;
			float minsim = Float.POSITIVE_INFINITY;
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentCombineGlobalLocalDimopoulos2010();
				float sim = seqalign.getScore(seqA, seqB);
				if(sim>maxsim){ maxsim = sim; }
				if(sim<minsim){ minsim = sim; }
				similaritiesrow[j] = sim;
			}
			// covert the similarities to distance
			for(int j=0; j<data.size(); j++){
				float sim = similaritiesrow[j];
				m_matrix[i][j] = (sim-minsim) / Math.abs(maxsim-minsim);
			}
		}
	}
	
}
