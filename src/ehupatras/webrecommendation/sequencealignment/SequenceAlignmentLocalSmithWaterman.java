package ehupatras.webrecommendation.sequencealignment;
import java.lang.reflect.Array;
import java.util.*;

public class SequenceAlignmentLocalSmithWaterman
				extends SequenceAlignment2
				implements SequenceAlignment{
    
    protected void init(String[] seqA, String[] seqB) {
            mSeqA = seqA;
            mSeqB = seqB;
            mD = new int[mSeqA.length + 1][mSeqB.length + 1];
            for (int i = 0; i <= mSeqA.length; i++) {
                    mD[i][0] = 0;                  
            }
            for (int j = 0; j <= mSeqB.length; j++) {
                    mD[0][j] = 0;
            }
    }
   
    protected void process() {
            for (int i = 1; i <= mSeqA.length; i++) {
                    for (int j = 1; j <= mSeqB.length; j++) {
                            int scoreDiag = mD[i-1][j-1] + weight(i, j);
                            int scoreLeft = mD[i][j-1] - 1;
                            int scoreUp = mD[i-1][j] - 1;
                            mD[i][j] = Math.max(Math.max(Math.max(scoreDiag, scoreLeft), scoreUp), 0);
                    }
            }
    }
   
    protected void backtrack() {
            int i = 1;
            int j = 1;
            int max = mD[i][j];

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
                    if (mD[i][j] == mD[i-1][j-1] + weight(i, j)) {                          
                            mAlignmentSeqA += (new StringBuffer(mSeqA[i-1])).reverse().toString();
                            mAlignmentSeqB += (new StringBuffer(mSeqB[j-1])).reverse().toString();
                            i--;
                            j--;                            
                            continue;
                    } else if (mD[i][j] == mD[i][j-1] - 1) {
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
   
    private int weight(int i, int j) {
            if (mSeqA[i - 1].equals(mSeqB[j - 1])) {
                    return 2;
            } else {
                    return -1;
            }
    }
    
    protected ArrayList<String[]> getTrimedAlignedSequences(String str1, String str2){
    	String[] seq1 = getStringArrayRepresentation(str1);
    	String[] seq2 = getStringArrayRepresentation(str2);
    	
    	// remove the start gaps
    	int i1;
    	for(i1=0; i1<seq1.length; i1++){
    		String elem1 = seq1[i1];
    		String elem2 = seq1[i1];
    		if(!elem1.equals(m_gap) && !elem2.equals(m_gap)){
    			break;
    		}
    	}
    	
    	// remove the final gaps
    	int i2;
    	for(i2=seq1.length-1; i2>=0; i2--){
    		String elem1 = seq1[i2];
    		String elem2 = seq1[i2];
    		if(!elem1.equals(m_gap) && !elem2.equals(m_gap)){
    			break;
    		}
    	}
    	
    	// create the new sub sequences
    	String[] seq12 = Arrays.copyOfRange(seq1, i1, i2+1);
    	String[] seq22 = Arrays.copyOfRange(seq2, i1, i2+1);
    	
    	ArrayList<String[]> trimmedseqs = new ArrayList<String[]>();
    	trimmedseqs.add(seq12);
    	trimmedseqs.add(seq22);
    	return trimmedseqs;
    }
    
    public static void main(String [] args) {              
            //String[] seqB = { "A", "C", "G", "A" };
            //String[] seqA = { "T", "C", "C", "G" };
            String[] seqB = { "01U", "02C", "03H", "01U" };
            String[] seqA = { "04U", "02C", "02C", "03H" };
           
            SequenceAlignmentLocalSmithWaterman sw = new SequenceAlignmentLocalSmithWaterman();
            System.out.println(sw.getScore(seqA, seqB));
            System.out.println(sw.getTweakedScore(seqA, seqB));
           
            sw.printMatrix();
            sw.printScoreAndAlignments();
    }
}
