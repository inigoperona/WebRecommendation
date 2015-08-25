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
	public DistanceMatrixEdit(ArrayList<Long> names){
		m_names = names;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.distmatrix.Matrix#computeMatrix(java.util.ArrayList, float[][], boolean)
	 */
	public void computeMatrix(ArrayList<String[]> data,
			float[][] roleWeights,
			boolean isplit){
		if(!isplit){
			m_matrix = new MatrixStructure(data.size());
		} else {
			m_matrixSplit = new MatrixStructure(data.size());
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
					m_matrix.setCell(i, j, dist);
				} else {
					m_matrixSplit.setCell(i, j, dist);
				}
			}
		}
	}
	
}
