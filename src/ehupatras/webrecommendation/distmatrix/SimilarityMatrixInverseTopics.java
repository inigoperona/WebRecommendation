package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;

import ehupatras.webrecommendation.A100MainClassAddContent;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics;

// TODO: Auto-generated Javadoc
/**
 * The Class SimilarityMatrixInverseTopics.
 */
public class SimilarityMatrixInverseTopics 
				extends SimilarityMatrixInverse {
	
	/**
	 * Instantiates a new similarity matrix inverse topics.
	 *
	 * @param names the names
	 * @param urlsDMfile the urls d mfile
	 * @param urlsEqualnessThreshold the urls equalness threshold
	 */
	public SimilarityMatrixInverseTopics(ArrayList<String> names, String urlsDMfile, float urlsEqualnessThreshold){
		super(names);
		m_urlsEqualnessThreshold = urlsEqualnessThreshold;
		
		// load topics
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objA = cont.loadUrlsDM(urlsDMfile);
		m_UrlIDs = (ArrayList<Integer>)objA[0];
		m_UrlsDM = (float[][])objA[1];
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.distmatrix.SimilarityMatrixInverse#computeMatrix(java.util.ArrayList, float[][], boolean)
	 */
	public void computeMatrix(ArrayList<String[]> data,
			float[][] roleWeights,
			boolean isplit,
			String workdirectory){
		if(!isplit){
			m_matrix = new MatrixStructure(data.size(), workdirectory);
		} else {
			m_matrixSplit = new MatrixStructure(data.size(), workdirectory);
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
				SequenceAlignment seqalign = new SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics(m_UrlIDs, m_UrlsDM, m_urlsEqualnessThreshold);
				seqalign.setRoleWeights(roleWeights);
				float sim = seqalign.getScore(seqA, seqB);
				if(sim>maxsim){ maxsim = sim; }
				if(sim<minsim){ minsim = sim; }
				similaritiesrow[j] = sim;
			}
			// put the similarity between 0 and 1 (normalize it)
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
					m_matrix.addCell(i, j, dist);
				} else {
					float dist = 1f - sim;
					m_matrixSplit.addCell(i, j, dist);
				}
			}
		}
	}
	
	/**
	 * Sets the urls equalness threshold.
	 *
	 * @param urlsEqualnessThreshold the new urls equalness threshold
	 */
	public void setUrlsEqualnessThreshold(float urlsEqualnessThreshold){
		m_urlsEqualnessThreshold = urlsEqualnessThreshold;
	}
	
}
