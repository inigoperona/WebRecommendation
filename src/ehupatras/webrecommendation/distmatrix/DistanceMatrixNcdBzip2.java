package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;
import ehupatras.ncd.NCD;

public class DistanceMatrixNcdBzip2 extends Matrix {
	
	public DistanceMatrixNcdBzip2(ArrayList<Long> names){
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
			int[] intAA = this.removeUHCtagSeq(seqA);
			for(int j=(i+1); j<data.size(); j++){
				String[] seqB = data.get(j);
				int[] intAB = this.removeUHCtagSeq(seqB);
				NCD ncDist = new NCD(1);
				float dist = ncDist.getNCD(intAA, intAB);
				if(!isplit){
					m_matrix[i][j] = dist;
					m_matrix[j][i] = dist;
				} else {
					m_matrixSplit[i][j] = dist;
					m_matrixSplit[j][i] = dist;
				}
			}
		}
	}
	
	private int[] removeUHCtagSeq(String[] seq){
		int[] intA = new int[seq.length];
		for(int i=0; i<seq.length; i++){
			String urlRole = seq[i];
			String url = urlRole.substring(0, urlRole.length()-1);
			intA[i] = Integer.valueOf(url);
		}
		return intA;
	}
	
}
