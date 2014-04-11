package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public class SequenceAlignmentLevenshteinTopics 
				extends SequenceAlignmentLevenshtein {

	public SequenceAlignmentLevenshteinTopics(ArrayList<Integer> urlIDs, float[][] urlsDM, float URLsEqualnessTh){
		m_UrlIDs = urlIDs;
		m_UrlsDM = urlsDM;
		m_URLsEqualnessTh = URLsEqualnessTh;
	}
	
	protected float weight2(String strA, String strB) {
		return weight3(strA, strB);
	}
	
}
