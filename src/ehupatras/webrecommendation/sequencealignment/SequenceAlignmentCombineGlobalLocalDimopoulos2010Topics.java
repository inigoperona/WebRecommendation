package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics.
 */
public class SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics 
				extends SequenceAlignmentCombineGlobalLocalDimopoulos2010 {

	/** The m_ url i ds. */
	private ArrayList<Integer> m_UrlIDs = null;
	
	/** The m_ urls dm. */
	private float[][] m_UrlsDM = null;
	
	/** The m_ ur ls equalness th. */
	private float m_URLsEqualnessTh = 0.6f;
	
	/**
	 * Instantiates a new sequence alignment combine global local dimopoulos2010 topics.
	 *
	 * @param urlIDs the url i ds
	 * @param urlsDM the urls dm
	 * @param URLsEqualnessTh the UR ls equalness th
	 */
	public SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics(ArrayList<Integer> urlIDs, float[][] urlsDM, float URLsEqualnessTh){
		m_UrlIDs = urlIDs;
		m_UrlsDM = urlsDM;
		m_URLsEqualnessTh = URLsEqualnessTh;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentCombineGlobalLocalDimopoulos2010#getScore(java.lang.String[], java.lang.String[])
	 */
	public float getScore(String[] seqA, String[] seqB, int gaplen){
		SequenceAlignmentBacktrack nw = new SequenceAlignmentGlobalDimopoulos2010Topics(m_UrlIDs,m_UrlsDM,m_URLsEqualnessTh);
		nw.setRoleWeights(m_roleW);
		float scoreNW = nw.getScore(seqA, seqB);
		
		SequenceAlignmentBacktrack sw = new SequenceAlignmentLocalDimopoulos2010Topics(m_UrlIDs,m_UrlsDM,m_URLsEqualnessTh);
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
	
    /**
     * Sets the UR ls equalness th.
     *
     * @param urlsEqualnessThreshold the new UR ls equalness th
     */
    public void setURLsEqualnessTh(float urlsEqualnessThreshold){
    	m_URLsEqualnessTh = urlsEqualnessThreshold;
    }
    
}
