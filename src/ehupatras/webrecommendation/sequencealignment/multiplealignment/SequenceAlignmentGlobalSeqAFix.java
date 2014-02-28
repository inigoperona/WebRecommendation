package ehupatras.webrecommendation.sequencealignment.multiplealignment;

import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentGlobalNeedlemanWunsch;

public class SequenceAlignmentGlobalSeqAFix 
				extends SequenceAlignmentGlobalNeedlemanWunsch {
	
	/*
    protected void init(String[] seqA, String[] seqB) {
        mSeqA = seqA;
        mSeqB = seqB;
        mD = new int[mSeqA.length + 1][mSeqB.length + 1];
        for (int i = 0; i <= mSeqA.length; i++) {
                for (int j = 0; j <= mSeqB.length; j++) {
                        if (i == 0) {
                        		// Big gap penalty to the sequenceB to not insert any gap there
                                mD[i][j] = -j * 10000;
                        } else if (j == 0) {
                                mD[i][j] = -i;
                        } else {
                                mD[i][j] = 0;
                        }
                }
        }
    }
    */
    
    protected void process() {
        for (int i = 1; i <= mSeqA.length; i++) {
                for (int j = 1; j <= mSeqB.length; j++) {
                        int scoreDiag = mD[i-1][j-1] + weight(i, j);
                        int scoreLeft = mD[i][j-1] - 1 * 10000;
                        int scoreUp = mD[i-1][j] - 1;
                        mD[i][j] = Math.max(Math.max(scoreDiag, scoreLeft), scoreUp);
                }
        }
    }
	
    /*
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
                } else if (j > 0 && mD[i][j] == mD[i][j-1] - 1) {
                	// No insert gaps in the SequenceA.
                    //mAlignmentSeqA += m_gap;
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
    */

}
