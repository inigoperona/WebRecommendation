package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public class SequenceAlignmentLevenshteinTopics2 
				extends SequenceAlignmentLevenshtein {

	public SequenceAlignmentLevenshteinTopics2(ArrayList<Integer> urlIDs, int[] url2topic, float topicmatch){
		m_UrlIDs = urlIDs;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
	}
	
	protected float weight2(String strA, String strB) {
		return weight4(strA, strB);
	}
	
}
