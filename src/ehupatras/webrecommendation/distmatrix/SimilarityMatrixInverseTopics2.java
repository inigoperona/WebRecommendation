package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;

import ehupatras.webrecommendation.A100MainClassAddContent;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics2;

// TODO: Auto-generated Javadoc
/**
 * The Class SimilarityMatrixInverseTopics2.
 */
public class SimilarityMatrixInverseTopics2 
				extends SimilarityMatrixInverse {

	/**
	 * Instantiates a new similarity matrix inverse topics2.
	 *
	 * @param names the names
	 * @param urls2topicfile the urls2topicfile
	 * @param topicmatch the topicmatch
	 */
	public SimilarityMatrixInverseTopics2(ArrayList<String> names, String urls2topicfile, float topicmatch){
		super(names);
		m_topicmatch = topicmatch;
		
		// load topics
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objA = cont.loadUrlsTopic(urls2topicfile, " ");
		m_UrlIDs = (ArrayList<Integer>)objA[0];
		m_url2topic = (int[])objA[1];
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.distmatrix.SimilarityMatrixInverse#computeMatrix(java.util.ArrayList, float[][], boolean)
	 */
	public void computeMatrix( ArrayList<String[]> data,
			float[][] roleWeights,
			boolean isplit,
			String workdirectory){
		if(!isplit){
			m_matrix = new MatrixStructure(false, data.size(), workdirectory);
		} else {
			m_matrixSplit = new MatrixStructure(false, data.size(), workdirectory);
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
				SequenceAlignment seqalign = new SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics2(m_UrlIDs, m_url2topic, m_topicmatch);
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
	
}
