package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentLevenshteinTopics2.
 */
public class SequenceAlignmentLevenshteinTopics2 
				extends SequenceAlignmentLevenshtein {

	/**
	 * Instantiates a new sequence alignment levenshtein topics2.
	 *
	 * @param urlIDs the url i ds
	 * @param url2topic the url2topic
	 * @param topicmatch the topicmatch
	 */
	public SequenceAlignmentLevenshteinTopics2(ArrayList<Integer> urlIDs, int[] url2topic, float topicmatch){
		m_UrlIDs = urlIDs;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLevenshtein#weight2(java.lang.String, java.lang.String)
	 */
	protected float weight2(String strA, String strB) {
		return weight4(strA, strB);
	}
	
}
