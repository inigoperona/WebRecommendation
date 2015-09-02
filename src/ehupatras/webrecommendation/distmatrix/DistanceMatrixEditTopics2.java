package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;

import ehupatras.webrecommendation.A100MainClassAddContent;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLevenshteinTopics2;

// TODO: Auto-generated Javadoc
/**
 * The Class DistanceMatrixEditTopics2.
 */
public class DistanceMatrixEditTopics2 
				extends DistanceMatrixEdit {

	/**
	 * Instantiates a new distance matrix edit topics2.
	 *
	 * @param names the names
	 * @param urls2topicfile the urls2topicfile
	 * @param topicmatch the topicmatch
	 */
	public DistanceMatrixEditTopics2(ArrayList<Long> names, String urls2topicfile, float topicmatch){
		super(names);
		m_topicmatch = topicmatch;
		
		// load topics
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objA = cont.loadUrlsTopic(urls2topicfile, " ");
		m_UrlIDs = (ArrayList<Integer>)objA[0];
		m_url2topic = (int[])objA[1];
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.distmatrix.DistanceMatrixEdit#computeMatrix(java.util.ArrayList, float[][], boolean)
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
		// create the similarity matrix
		for(int i=0; i<data.size(); i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing distances of row " + i);
			String[] seqA = data.get(i);
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentLevenshteinTopics2(m_UrlIDs, m_url2topic, m_topicmatch);
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

}
