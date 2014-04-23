package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;
import java.util.Hashtable;

import ehupatras.ncd.NCD;
import ehupatras.ncd.NcdGzip;

public class DistanceMatrixNcdGzip extends Matrix {

	private Hashtable<Integer,Integer> m_dict;
	
	public DistanceMatrixNcdGzip(ArrayList<Long> names){
		m_names = names;
	}
	
	public void computeMatrix(ArrayList<String[]> data,
			float[][] roleWeights,
			boolean isplit){
		
		// Create the dictionary to convert URL-ID to 0-255 integer
		m_dict = new Hashtable<Integer,Integer>(); 
		int index = 0;
		for(int i=0; i<data.size(); i++){
			String[] seq = data.get(i);
			int[] intA = this.removeUHCtagSeq(seq);
			for(int j=0; j<intA.length; j++){
				int val = intA[j];
				if(!m_dict.containsKey(val)){
					m_dict.put(val, index);
					index++;
				}
			}
		}
		System.out.println("  " + index + " different URLs.");
		
		// initialize the matrix
		if(!isplit){
			m_matrix = new float[data.size()][data.size()];
		} else {
			m_matrixSplit = new float[data.size()][data.size()];
		}
		
		// fill the distance matrix
		for(int i=0; i<data.size(); i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing distances of row " + i);
			String[] seqA = data.get(i);
			int[] intAA = this.removeUHCtagSeq(seqA);
			int[] intAA2 = this.convertDict(intAA);
			
			for(int j=(i+1); j<data.size(); j++){
				String[] seqB = data.get(j);
				int[] intAB = this.removeUHCtagSeq(seqB);
				int[] intAB2 = this.convertDict(intAB);
				
				// compute the distance
				NCD ncDist = new NcdGzip(1);
				float dist = ncDist.getNCD(intAA2, intAB2);
				
				// fill the matrix
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
	
	private int[] convertDict(int[] intA){
		int[] intAre = new int[intA.length];
		for(int i=0; i<intA.length; i++){
			intAre[i] = m_dict.get(intA[i]);
		}
		return intAre;
	}
	
}
