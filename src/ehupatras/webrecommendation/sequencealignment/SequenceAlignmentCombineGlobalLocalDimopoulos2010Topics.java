package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public class SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics 
				extends SequenceAlignmentCombineGlobalLocalDimopoulos2010 {

	private ArrayList<Integer> m_UrlIDs = null;
	private float[][] m_UrlsDM = null;
	private float m_URLsEqualnessTh = 0.6f;
	
	public SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics(ArrayList<Integer> urlIDs, float[][] urlsDM, float URLsEqualnessTh){
		m_UrlIDs = urlIDs;
		m_UrlsDM = urlsDM;
		m_URLsEqualnessTh = URLsEqualnessTh;
	}
	
	public float getScore(String[] seqA, String[] seqB){
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
	
    public void setURLsEqualnessTh(float urlsEqualnessThreshold){
    	m_URLsEqualnessTh = urlsEqualnessThreshold;
    }
    
}
