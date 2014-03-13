package ehupatras.webrecommendation.sequencealignment.multiplealignment;

import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentGlobalNeedlemanWunsch;

public class SequenceAlignmentGlobalSeqAFix 
				extends SequenceAlignmentGlobalNeedlemanWunsch {
	
    protected void backtrack() {
        int i = mSeqA.length;
        int j = mSeqB.length;
        mScore = mD[i][j];
        
        while (i > 0 || j > 0) {

        	// check the values of three possible movements
        	float valueDiagonal = Float.MIN_VALUE;
        	float valueHorizontal = Float.MIN_VALUE;
        	float valueVertical = Float.MIN_VALUE;
        	if(i > 0 && j > 0){
        		valueDiagonal = mD[i-1][j-1];
        	}
        	if(j>0){
        		valueHorizontal = mD[i][j-1];
        	}
        	if(i>0){
        		valueVertical = mD[i-1][j];
        	}
        	
        	// take the minimun value of diagonal or vertical movement
        	boolean moveDiagonal = false;
        	boolean moveHorizontal = false;
        	boolean moveVertical = false;
        	if(valueDiagonal<=valueVertical && i > 0 && j > 0){
        		moveDiagonal = true;
        	} else {
        		if(i>0){
        			moveVertical = true;
        		} else {
        			break;
        		}
        	}
        	
        	// make a movement
        	if (moveDiagonal) {
        		// Diagonal movement. Match / Mismatch
        		mAlignmentSeqA += (new StringBuffer(mSeqA[i-1])).reverse().toString();
                mAlignmentSeqB += (new StringBuffer(mSeqB[j-1])).reverse().toString();
                i--;
                j--;                            
                continue;
            } else {
            	// Vertical movement. Gap insertion in seqB.
                mAlignmentSeqA += (new StringBuffer(mSeqA[i-1])).reverse().toString();
                mAlignmentSeqB += m_gap;
                i--;
                continue;
            }
        }
        mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();
        mAlignmentSeqB = new StringBuffer(mAlignmentSeqB).reverse().toString();
    }

}
