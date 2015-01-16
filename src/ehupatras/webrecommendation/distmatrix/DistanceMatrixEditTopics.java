package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLevenshteinTopics;
import ehupatras.webrecommendation.A100MainClassAddContent;

// TODO: Auto-generated Javadoc
/**
 * The Class DistanceMatrixEditTopics.
 */
public class DistanceMatrixEditTopics 
				extends DistanceMatrixEdit {
	
	/**
	 * Instantiates a new distance matrix edit topics.
	 *
	 * @param names the names
	 * @param urlsDMfile the urls d mfile
	 * @param urlsEqualnessThreshold the urls equalness threshold
	 */
	public DistanceMatrixEditTopics(ArrayList<Long> names, String urlsDMfile, float urlsEqualnessThreshold){
		super(names);
		m_urlsEqualnessThreshold = urlsEqualnessThreshold;
		
		// load topics
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objA = cont.loadUrlsDM(urlsDMfile);
		m_UrlIDs = (ArrayList<Integer>)objA[0];
		m_UrlsDM = (float[][])objA[1];
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.distmatrix.DistanceMatrixEdit#computeMatrix(java.util.ArrayList, float[][], boolean)
	 */
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
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentLevenshteinTopics(m_UrlIDs, m_UrlsDM, m_urlsEqualnessThreshold);
				//seqalign.setRoleWeights(roleWeights);
				float dist = seqalign.getScore(seqA, seqB);
				if(!isplit){
					m_matrix[i][j] = dist;
				} else {
					m_matrixSplit[i][j] = dist;
				}
			}
		}
	}
	
}
