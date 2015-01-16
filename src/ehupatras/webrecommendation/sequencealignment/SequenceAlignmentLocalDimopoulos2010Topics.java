package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentLocalDimopoulos2010Topics.
 */
public class SequenceAlignmentLocalDimopoulos2010Topics 
				extends SequenceAlignmentLocalDimopoulos2010 {
	
	/**
	 * Instantiates a new sequence alignment local dimopoulos2010 topics.
	 *
	 * @param urlIDs the url i ds
	 * @param urlsDM the urls dm
	 * @param URLsEqualnessTh the UR ls equalness th
	 */
	public SequenceAlignmentLocalDimopoulos2010Topics(ArrayList<Integer> urlIDs, float[][] urlsDM, float URLsEqualnessTh){
		m_UrlIDs = urlIDs;
		m_UrlsDM = urlsDM;
		m_URLsEqualnessTh = URLsEqualnessTh;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack#weight2(int, int)
	 */
	protected float weight2(int i, int j) {
		return weight3(mSeqA[i-1], mSeqB[j-1]);
	}
	
    /* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack#equalURLs(java.lang.String, java.lang.String)
     */
    protected float equalURLs(String strA, String strB){
    	return weight3(strA, strB);
    }
	
}
