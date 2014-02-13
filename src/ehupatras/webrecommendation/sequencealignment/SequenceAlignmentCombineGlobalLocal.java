package ehupatras.webrecommendation.sequencealignment;

public class SequenceAlignmentCombineGlobalLocal implements SequenceAlignment{
	
	public float getScore(String[] seqA, String[] seqB){
		SequenceAlignment nw = new SequenceAlignmentGlobalNeedlemanWunsch();
		float scoreNW = nw.getScore(seqA, seqB);
		SequenceAlignment sw = new SequenceAlignmentLocalSmithWaterman();
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
	
	public float getTweakedScore(String[] seqA, String[] seqB){
		SequenceAlignment nw = new SequenceAlignmentGlobalNeedlemanWunsch();
		float scoreNW = nw.getTweakedScore(seqA, seqB);
		SequenceAlignment sw = new SequenceAlignmentLocalSmithWaterman();
		float scoreSW = sw.getTweakedScore(seqA, seqB);
		float p;
		if(seqA.length > seqB.length){
			p = (float)seqB.length/(float)seqA.length;
		} else {
			p = (float)seqA.length/(float)seqB.length;
		}
		float score = ((float)1-p)*scoreSW + p*scoreNW;
		return score;
	}
	
	public float getTweakedDistance(String[] seqA, String[] seqB){
		SequenceAlignment nw = new SequenceAlignmentGlobalNeedlemanWunsch();
		float scoreNW = nw.getTweakedDistance(seqA, seqB);
		SequenceAlignment sw = new SequenceAlignmentLocalSmithWaterman();
		float scoreSW = sw.getTweakedDistance(seqA, seqB);
		float p;
		if(seqA.length > seqB.length){
			p = (float)seqB.length/(float)seqA.length;
		} else {
			p = (float)seqA.length/(float)seqB.length;
		}
		float score = ((float)1-p)*scoreSW + p*scoreNW;
		return score;
	}
	
    public static void main(String [] args) {
    	String[] seqA = { "1H", "2C", "3U", "4H", "1C" };
    	String[] seqB = { "1H", "3U", "4H", "1C" };

        SequenceAlignment sa = new SequenceAlignmentCombineGlobalLocal();
        System.out.println(sa.getScore(seqA, seqB));
        System.out.println(sa.getTweakedScore(seqA, seqB));
    }
}
