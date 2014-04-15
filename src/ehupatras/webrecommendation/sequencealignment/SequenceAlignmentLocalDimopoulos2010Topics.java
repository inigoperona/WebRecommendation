package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public class SequenceAlignmentLocalDimopoulos2010Topics 
				extends SequenceAlignmentLocalDimopoulos2010 {
	
	public SequenceAlignmentLocalDimopoulos2010Topics(ArrayList<Integer> urlIDs, float[][] urlsDM, float URLsEqualnessTh){
		m_UrlIDs = urlIDs;
		m_UrlsDM = urlsDM;
		m_URLsEqualnessTh = URLsEqualnessTh;
	}
	
	protected float weight2(int i, int j) {
		return weight3(mSeqA[i-1], mSeqB[j-1]);
	}
	
    protected float equalURLs(String strA, String strB){
    	return weight3(strA, strB);
    }
	
}
