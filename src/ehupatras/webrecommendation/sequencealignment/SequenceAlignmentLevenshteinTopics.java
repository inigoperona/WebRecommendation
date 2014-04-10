package ehupatras.webrecommendation.sequencealignment;

public class SequenceAlignmentLevenshteinTopics 
				extends SequenceAlignmentLevenshtein {

	protected float weight2(String strA, String strB) {
		return weight3(strA, strB);
	}
	
}
