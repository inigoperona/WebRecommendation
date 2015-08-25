package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;

import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentCombineGlobalLocalDimopoulos2010;

// TODO: Auto-generated Javadoc
/**
 * The Class SimilarityMatrixInverse.
 */
public class SimilarityMatrixInverse 
				extends Matrix {
	
	/**
	 * Instantiates a new similarity matrix inverse.
	 *
	 * @param names the names
	 */
	public SimilarityMatrixInverse(ArrayList<Long> names){
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
		
		float[][] simmatrix = new float[data.size()][data.size()];
		for(int i=0; i<data.size(); i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing similarities of row " + i);
			// compute each row similarities
			// and check the maximum and minimum values to create the distance
			String[] seqA = data.get(i);
			float[] similaritiesrow = new float[data.size()];
			float maxsim = Float.NEGATIVE_INFINITY;
			float minsim = Float.POSITIVE_INFINITY;
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentCombineGlobalLocalDimopoulos2010();
				seqalign.setRoleWeights(roleWeights);
				float sim = seqalign.getScore(seqA, seqB);
				if(sim>maxsim){ maxsim = sim; }
				if(sim<minsim){ minsim = sim; }
				similaritiesrow[j] = sim;
			}
			// put the similarity between 0 and 1
			for(int j=0; j<data.size(); j++){
				float sim = similaritiesrow[j];
				simmatrix[i][j] = (sim-minsim) / Math.abs(maxsim-minsim);
			}
		}
		
		// convert to distance by inversing the value
		for(int i=0; i<data.size(); i++){
			for(int j=0; j<data.size(); j++){
				float sim = simmatrix[i][j];
				if(!isplit){
					float dist = 1f - sim;
					m_matrix.setCell(i, j, dist);
				} else {
					float dist = 1f - sim; 
					m_matrixSplit.setCell(i, j, dist);
				}
			}
		}
	}
	
}
