package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public class SequenceAlignmentLocalDimopoulos2010Topics2 
				extends SequenceAlignmentLocalDimopoulos2010{

	public SequenceAlignmentLocalDimopoulos2010Topics2(ArrayList<Integer> urlIDs, int[] url2topic, float topicmatch){
		m_UrlIDs = urlIDs;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
	}
	
	protected float weight2(int i, int j) {
		return weight4(mSeqA[i-1], mSeqB[j-1]);
	}
	
    protected float equalURLs(String strA, String strB){
    	return weight4(strA, strB);
    }
	
}
