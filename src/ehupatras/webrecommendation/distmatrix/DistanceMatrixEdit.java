package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLevenshtein;

// TODO: Auto-generated Javadoc
/**
 * The Class DistanceMatrixEdit.
 */
public class DistanceMatrixEdit extends Matrix {
	
	/**
	 * Instantiates a new distance matrix edit.
	 *
	 * @param names the names
	 */
	public DistanceMatrixEdit(ArrayList<String> names){
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
		for(int i=0; i<data.size(); i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing distances of row " + i);
			String[] seqA = data.get(i);
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentLevenshtein();
				seqalign.setRoleWeights(roleWeights);
				float dist = seqalign.getScore(seqA, seqB);
				if(!isplit){
					m_matrix.addCell(i, j, dist);
				} else {
					m_matrixSplit.addCell(i, j, dist);
				}
			}
		}
	}
	
	public void computeMatrix2(ArrayList<String[]> data,
			float[][] roleWeights,
			boolean isplit,
			String workdirectory){
		
		// create the similarity matrix
		int n = data.size();
		float distMatrixLen = (float)(n+1)*((float)n/2f);
		int distMatrixLenInt = Math.round(distMatrixLen);
		float[] distMatrixA = new float[distMatrixLenInt];
		int kont=0;
		for(int i=0; i<n; i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing distances of row " + i);
			String[] seqA = data.get(i);
			for(int j=i; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentLevenshtein();
				seqalign.setRoleWeights(roleWeights);
				float dist = seqalign.getScore(seqA, seqB);
				distMatrixA[kont]=dist;
				//distMatrixAL.add(dist);
				kont++;
			}
		}
		
		System.out.println("post length:" + distMatrixA.length);
		for(int i=0; i<distMatrixA.length; i++){
			System.out.println(i + ":" + distMatrixA[i]);
		}
		
		
	}
	
}
