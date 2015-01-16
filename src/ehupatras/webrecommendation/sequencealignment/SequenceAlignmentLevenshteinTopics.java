package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentLevenshteinTopics.
 */
public class SequenceAlignmentLevenshteinTopics 
				extends SequenceAlignmentLevenshtein {

	/**
	 * Instantiates a new sequence alignment levenshtein topics.
	 *
	 * @param urlIDs the url i ds
	 * @param urlsDM the urls dm
	 * @param URLsEqualnessTh the UR ls equalness th
	 */
	public SequenceAlignmentLevenshteinTopics(ArrayList<Integer> urlIDs, float[][] urlsDM, float URLsEqualnessTh){
		m_UrlIDs = urlIDs;
		m_UrlsDM = urlsDM;
		m_URLsEqualnessTh = URLsEqualnessTh;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLevenshtein#weight2(java.lang.String, java.lang.String)
	 */
	protected float weight2(String strA, String strB) {
		return weight3(strA, strB);
	}
	
}
