package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics2.
 */
public class SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics2 
				extends SequenceAlignmentCombineGlobalLocalDimopoulos2010 {

	/** The m_ url i ds. */
	private ArrayList<Integer> m_UrlIDs = null;
	
	/** The m_url2topic. */
	private int[] m_url2topic = null;
	
	/** The m_topicmatch. */
	private float m_topicmatch = 0.6f;
	
	/**
	 * Instantiates a new sequence alignment combine global local dimopoulos2010 topics2.
	 *
	 * @param urlIDs the url i ds
	 * @param url2topic the url2topic
	 * @param topicmatch the topicmatch
	 */
	public SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics2(ArrayList<Integer> urlIDs, int[] url2topic, float topicmatch){
		m_UrlIDs = urlIDs;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentCombineGlobalLocalDimopoulos2010#getScore(java.lang.String[], java.lang.String[])
	 */
	public float getScore(String[] seqA, String[] seqB, int gaplen){
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
