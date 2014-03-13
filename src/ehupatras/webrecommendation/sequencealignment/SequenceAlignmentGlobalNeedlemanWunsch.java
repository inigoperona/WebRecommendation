package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public class SequenceAlignmentGlobalNeedlemanWunsch
				extends SequenceAlignmentBacktrack
				implements SequenceAlignment{
	
    public float getScore(String[] seqA, String[] seqB){
    	computeAlignment(seqA,seqB);
        return (float)mScore;
    }
	
    protected void init(String[] seqA, String[] seqB) {
            mSeqA = seqA;
            mSeqB = seqB;
            mD = new float[mSeqA.length + 1][mSeqB.length + 1];
            for (int i = 0; i <= mSeqA.length; i++) {
                    for (int j = 0; j <= mSeqB.length; j++) {
                            if (i == 0) {
                                    mD[i][j] = (float)-j;
                            } else if (j == 0) {
                                    mD[i][j] = (float)-i;
                            } else {
                                    mD[i][j] = 0f;
                            }
                    }
            }
    }
   
    protected void process() {
            for (int i = 1; i <= mSeqA.length; i++) {
                    for (int j = 1; j <= mSeqB.length; j++) {
                            float scoreDiag = mD[i-1][j-1] + weight(i, j);
                            float scoreLeft = mD[i][j-1] - 1f;
                            float scoreUp = mD[i-1][j] - 1f;
                            mD[i][j] = Math.max(Math.max(scoreDiag, scoreLeft), scoreUp);
                    }
            }
    }
   
    protected void backtrack() {
            int i = mSeqA.length;
            int j = mSeqB.length;
            mScore = mD[i][j];
            
            while (i > 0 || j > 0) {
                    if (i > 0 && j > 0 && mD[i][j] == mD[i-1][j-1] + weight(i, j)) {
                            mAlignmentSeqA += (new StringBuffer(mSeqA[i-1])).reverse().toString();
                            mAlignmentSeqB += (new StringBuffer(mSeqB[j-1])).reverse().toString();
                            i--;
                            j--;                            
                            continue;
                    } else if (j > 0 && mD[i][j] == mD[i][j-1] - 1f) {
                            mAlignmentSeqA += m_gap;
                            mAlignmentSeqB += (new StringBuffer(mSeqB[j-1])).reverse().toString();
                            j--;
                            continue;
                    } else {
                            mAlignmentSeqA += (new StringBuffer(mSeqA[i-1])).reverse().toString();
                            mAlignmentSeqB += m_gap;
                            i--;
                            continue;
                    }
            }
            mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();
            mAlignmentSeqB = new StringBuffer(mAlignmentSeqB).reverse().toString();
    }
    
    protected ArrayList<String[]> getTrimedAlignedSequences(String str1, String str2){
    	String[] seq1 = getStringArrayRepresentation(str1);
    	String[] seq2 = getStringArrayRepresentation(str2);
    	ArrayList<String[]> trimmedseqs = new ArrayList<String[]>();
    	trimmedseqs.add(seq1);
    	trimmedseqs.add(seq2);
    	return trimmedseqs;
    }
    
    public static void main(String [] args) {
        //String[] seqA = { "A", "C", "G", "T", "C" };
        //String[] seqB = { "A", "G", "T", "C" };
    	String[] seqA = { "01H", "02C", "03U", "04H", "01C" };
    	String[] seqB = { "01H", "03U", "04H", "01C" };
    	//String[] seqA = { "1", "2", "3", "4" };
    	//String[] seqB = { "2", "3", "4", "3" };
    	//String[] seqA = {"000011","000386","000450","000323","000450","000298","000329","000474","000152","000877","000185","000175","000231","000070","001189","000070","000074","000074","000926","000074","000106","000170","000009","000170","001023","000295","000016","000295","000436","000227","000436","000273","000432"};
    	//String[] seqB = {"000011","000152","001469","000397","001509","000397","000329","000464","000329","000016","000011","000106","000074","000489","000185","000175","000227","000273","000174","000582","000059","000011"};
    	
        SequenceAlignmentGlobalNeedlemanWunsch nw = new SequenceAlignmentGlobalNeedlemanWunsch();
        System.out.println(nw.getScore(seqA, seqB));

        nw.printMatrix();
        nw.printScoreAndAlignments();
    }
}
