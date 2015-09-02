package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;
import java.util.Hashtable;

import ehupatras.ncd.NCD;
import ehupatras.ncd.NcdGzip;

// TODO: Auto-generated Javadoc
/**
 * The Class DistanceMatrixNcdGzip.
 */
public class DistanceMatrixNcdGzip extends Matrix {

	/** The m_dict. */
	private Hashtable<Integer,Integer> m_dict;
	
	/**
	 * Instantiates a new distance matrix ncd gzip.
	 *
	 * @param names the names
	 */
	public DistanceMatrixNcdGzip(ArrayList<Long> names){
		m_names = names;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.distmatrix.Matrix#computeMatrix(java.util.ArrayList, float[][], boolean)
	 */
	public void computeMatrix(ArrayList<String[]> data,
			float[][] roleWeights,
			boolean isplit,
			String workdirectory){
		
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
			m_matrix = new MatrixStructure(data.size(), workdirectory);
		} else {
			m_matrixSplit = new MatrixStructure(data.size(), workdirectory);
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
					m_matrix.addCell(i, j, dist);
					m_matrix.addCell(j, i, dist);
				} else {
					m_matrixSplit.addCell(i, j, dist);
					m_matrixSplit.addCell(j, i, dist);
				}
			}
		}
		
	}
	
	/**
	 * Removes the uh ctag seq.
	 *
	 * @param seq the seq
	 * @return the int[]
	 */
	private int[] removeUHCtagSeq(String[] seq){
		int[] intA = new int[seq.length];
		for(int i=0; i<seq.length; i++){
			String urlRole = seq[i];
			String url = urlRole.substring(0, urlRole.length()-1);
			intA[i] = Integer.valueOf(url);
		}
		return intA;
	}
	
	/**
	 * Convert dict.
	 *
	 * @param intA the int a
	 * @return the int[]
	 */
	private int[] convertDict(int[] intA){
		int[] intAre = new int[intA.length];
		for(int i=0; i<intA.length; i++){
			intAre[i] = m_dict.get(intA[i]);
		}
		return intAre;
	}
	
}
