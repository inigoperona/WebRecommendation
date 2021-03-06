package ehupatras.webrecommendation.sequencealignment;

import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentLocalSmithWaterman.
 */
public class SequenceAlignmentLocalSmithWaterman
				extends SequenceAlignmentBacktrack
				implements SequenceAlignment{
    
    /* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack#getScore(java.lang.String[], java.lang.String[])
     */
    public float getScore(String[] seqA, String[] seqB){
    	computeAlignment(seqA,seqB);
        return (float)mScore;
    }
	
    /* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack#init(java.lang.String[], java.lang.String[])
     */
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
   
    /* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack#process()
     */
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
   
    /* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack#backtrack()
     */
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
                    mAlignmentSeqA += this.reversing(mSeqA[k - 1]);
                    k--;
            }
            while (l > j) {
                    mAlignmentSeqA += m_gap;
                    mAlignmentSeqB += this.reversing(mSeqB[l - 1]);
                    l--;
            }
           
            while (mD[i][j] != 0) {                
                if (mD[i][j] == mD[i][j-1] - 1f) {
                	mAlignmentSeqA += m_gap;
                    mAlignmentSeqB += this.reversing(mSeqB[j-1]);
                    j--;
                    continue;
                } else if (mD[i][j] == mD[i-1][j] - 1f){
                    mAlignmentSeqA += this.reversing(mSeqA[i-1]);
                    mAlignmentSeqB += m_gap;
                    i--;
                    continue;
                } else if (mD[i][j] == mD[i-1][j-1] + weight2(i, j)) {                          
                     mAlignmentSeqA += this.reversing(mSeqA[i-1]);
                     mAlignmentSeqB += this.reversing(mSeqB[j-1]);
                     i--;
                     j--;                            
                     continue;
                }
            }
           
            while (i > 0) {
                    mAlignmentSeqB += m_gap;
                    mAlignmentSeqA += this.reversing(mSeqA[i - 1]);
                    i--;
            }
            while (j > 0) {
                    mAlignmentSeqA += m_gap;
                    mAlignmentSeqB += this.reversing(mSeqB[j - 1]);
                    j--;
            }
           
            mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();
            mAlignmentSeqB = new StringBuffer(mAlignmentSeqB).reverse().toString();
    }
    private String reversing(String str){
    	int gaplen = m_gap.length();
    	int diff = gaplen - str.length();
    	String str2 = str;
    	for(int i=0; i<diff; i++){
    		str2 = "0" + str2; 
    	}
    	StringBuffer str3 = new StringBuffer(str2);
    	return str3.reverse().toString();
    }
    
    /* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack#getTrimedAlignedSequences(java.lang.String, java.lang.String)
     */
    protected ArrayList<String[]> getTrimedAlignedSequences(String str1, String str2){
    	String[] seq1 = getStringArrayRepresentation(str1);
    	String[] seq2 = getStringArrayRepresentation(str2);
    	ArrayList<String[]> trimmedseqs = new ArrayList<String[]>();
    	trimmedseqs.add(seq1);
    	trimmedseqs.add(seq2);
    	return trimmedseqs;
    }
    
    /**
     * The main method.
     *
     * @param args the arguments
     */
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
