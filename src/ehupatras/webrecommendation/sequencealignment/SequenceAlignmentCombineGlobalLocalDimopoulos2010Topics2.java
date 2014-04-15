package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public class SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics2 
				extends SequenceAlignmentCombineGlobalLocalDimopoulos2010 {

	private ArrayList<Integer> m_UrlIDs = null;
	private int[] m_url2topic = null;
	private float m_topicmatch = 0.6f;
	
	public SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics2(ArrayList<Integer> urlIDs, int[] url2topic, float topicmatch){
		m_UrlIDs = urlIDs;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
	}
	
	public float getScore(String[] seqA, String[] seqB){
		SequenceAlignmentBacktrack nw = new SequenceAlignmentGlobalDimopoulos2010Topics2(m_UrlIDs, m_url2topic, m_topicmatch);
		nw.setRoleWeights(m_roleW);
		float scoreNW = nw.getScore(seqA, seqB);
		SequenceAlignmentBacktrack sw = new SequenceAlignmentLocalDimopoulos2010Topics2(m_UrlIDs, m_url2topic, m_topicmatch);
		sw.setRoleWeights(m_roleW);
		float scoreSW = sw.getScore(seqA, seqB);
		
		float p;
		if(seqA.length > seqB.length){
			p = (float)seqB.length/(float)seqA.length;
		} else {
			p = (float)seqA.length/(float)seqB.length;
		}
		float score = ((float)1-p)*scoreSW + p*scoreNW;
		return score;
	}
}
