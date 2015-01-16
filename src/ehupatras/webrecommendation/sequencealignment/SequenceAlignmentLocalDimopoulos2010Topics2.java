package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentLocalDimopoulos2010Topics2.
 */
public class SequenceAlignmentLocalDimopoulos2010Topics2 
				extends SequenceAlignmentLocalDimopoulos2010{

	/**
	 * Instantiates a new sequence alignment local dimopoulos2010 topics2.
	 *
	 * @param urlIDs the url i ds
	 * @param url2topic the url2topic
	 * @param topicmatch the topicmatch
	 */
	public SequenceAlignmentLocalDimopoulos2010Topics2(ArrayList<Integer> urlIDs, int[] url2topic, float topicmatch){
		m_UrlIDs = urlIDs;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack#weight2(int, int)
	 */
	protected float weight2(int i, int j) {
		return weight4(mSeqA[i-1], mSeqB[j-1]);
	}
	
    /* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack#equalURLs(java.lang.String, java.lang.String)
     */
    protected float equalURLs(String strA, String strB){
    	return weight4(strA, strB);
    }
	
}
