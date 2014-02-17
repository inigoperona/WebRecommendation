package ehupatras.webrecommendation.sequencealignment;

public class SequenceAlignmentCombineGlobalLocalDimopoulos2010 implements SequenceAlignment{
	
	public float getScore(String[] seqA, String[] seqB){
		SequenceAlignment nw = new SequenceAlignmentGlobalDimopoulos2010();
		float scoreNW = nw.getScore(seqA, seqB);
		SequenceAlignment sw = new SequenceAlignmentLocalDimopoulos2010();
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
	
    public static void main(String [] args) {
    	String[] seqA = { "1H", "2C", "3U", "4H", "1C" };
    	String[] seqB = { "1H", "3U", "4H", "1C" };

        SequenceAlignment sa = new SequenceAlignmentCombineGlobalLocalDimopoulos2010();
        System.out.println(sa.getScore(seqA, seqB));
    }
}
