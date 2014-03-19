package ehupatras.webrecommendation.sequencealignment;

import java.util.*;

public class SequenceAlignmentLocalSmithWaterman
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
                    mD[i][0] = 0f;                  
            }
            for (int j = 0; j <= mSeqB.length; j++) {
                    mD[0][j] = 0f;
            }
    }
   
    protected void process() {
            for (int i = 1; i <= mSeqA.length; i++) {
                    for (int j = 1; j <= mSeqB.length; j++) {
                            float scoreDiag = mD[i-1][j-1] + weight2(i, j);
                            float scoreLeft = mD[i][j-1] - 1f;
                            float scoreUp = mD[i-1][j] - 1f;
                            mD[i][j] = Math.max(Math.max(Math.max(scoreDiag, scoreLeft), scoreUp), 0);
                    }
            }
    }
   
    protected void backtrack() {
            int i = 1;
            int j = 1;
            float max = mD[i][j];

            for (int k = 1; k <= mSeqA.length; k++) {
                    for (int l = 1; l <= mSeqB.length; l++) {
                            if (mD[k][l] > max) {
                                    i = k;
                                    j = l;
                                    max = mD[k][l];
                            }
                    }
            }
           
            mScore = mD[i][j];
           
            int k = mSeqA.length;
            int l = mSeqB.length;
           
            while (k > i) {
                    mAlignmentSeqB += m_gap;
                    mAlignmentSeqA += (new StringBuffer(mSeqA[k - 1])).reverse().toString();
                    k--;
            }
            while (l > j) {
                    mAlignmentSeqA += m_gap;
                    mAlignmentSeqB += (new StringBuffer(mSeqB[l - 1])).reverse().toString();
                    l--;
            }
           
            while (mD[i][j] != 0) {                
                if (mD[i][j] == mD[i][j-1] - 1f) {
                	mAlignmentSeqA += m_gap;
                    mAlignmentSeqB += (new StringBuffer(mSeqB[j-1])).reverse().toString();
                    j--;
                    continue;
                } else if (mD[i][j] == mD[i-1][j] - 1f){
                    mAlignmentSeqA += (new StringBuffer(mSeqA[i-1])).reverse().toString();
                    mAlignmentSeqB += m_gap;
                    i--;
                    continue;
                } else if (mD[i][j] == mD[i-1][j-1] + weight2(i, j)) {                          
                     mAlignmentSeqA += (new StringBuffer(mSeqA[i-1])).reverse().toString();
                     mAlignmentSeqB += (new StringBuffer(mSeqB[j-1])).reverse().toString();
                     i--;
                     j--;                            
                     continue;
                }
            }
           
            while (i > 0) {
                    mAlignmentSeqB += m_gap;
                    mAlignmentSeqA += (new StringBuffer(mSeqA[i - 1])).reverse().toString();
                    i--;
            }
            while (j > 0) {
                    mAlignmentSeqA += m_gap;
                    mAlignmentSeqB += (new StringBuffer(mSeqB[j - 1])).reverse().toString();
                    j--;
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
            //String[] seqB = { "A", "C", "G", "A" };
            //String[] seqA = { "T", "C", "C", "G" };
            String[] seqB = { "01U", "02C", "03H", "01U" };
            String[] seqA = { "04U", "02C", "02C", "03H" };
           
            SequenceAlignmentLocalSmithWaterman sw = new SequenceAlignmentLocalSmithWaterman();
            System.out.println(sw.getScore(seqA, seqB));
           
            sw.printMatrix();
            sw.printScoreAndAlignments();
    }
}
