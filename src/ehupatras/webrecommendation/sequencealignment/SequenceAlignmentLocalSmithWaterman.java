package ehupatras.webrecommendation.sequencealignment;

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
