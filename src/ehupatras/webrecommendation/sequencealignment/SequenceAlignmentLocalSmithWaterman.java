package ehupatras.webrecommendation.sequencealignment;

public class SequenceAlignmentLocalSmithWaterman implements SequenceAlignment{
    private String[] mSeqA;
    private String[] mSeqB;
    private int[][] mD;
    private int mScore;
    private String mAlignmentSeqA = "";
    private String mAlignmentSeqB = "";
    private String m_gap = "-";
   
    public float getScore(String[] seqA, String[] seqB){
    	// create the gap String
    	int gaplen = seqA[0].length();
    	for(int i=1; i<gaplen; i++){ m_gap = m_gap + "-"; }
    	
    	// compute the score
        init(seqA, seqB);            
        process();
        backtrack();
        return (float)mScore;
    }
    
    private void init(String[] seqA, String[] seqB) {
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
   
    private void process() {
            for (int i = 1; i <= mSeqA.length; i++) {
                    for (int j = 1; j <= mSeqB.length; j++) {
                            int scoreDiag = mD[i-1][j-1] + weight(i, j);
                            int scoreLeft = mD[i][j-1] - 1;
                            int scoreUp = mD[i-1][j] - 1;
                            mD[i][j] = Math.max(Math.max(Math.max(scoreDiag, scoreLeft), scoreUp), 0);
                    }
            }
    }
   
    private void backtrack() {
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
   
    public void printMatrix() {
            System.out.print("D =       ");
            for (int i = 0; i < mSeqB.length; i++) {
                    System.out.print(String.format("%4s ", mSeqB[i]));
            }
            System.out.println();
            for (int i = 0; i < mSeqA.length + 1; i++) {
                    if (i > 0) {
                            System.out.print(String.format("%4s ", mSeqA[i-1]));
                    } else {
                            System.out.print("     ");
                    }
                    for (int j = 0; j < mSeqB.length + 1; j++) {
                            System.out.print(String.format("%4d ", mD[i][j]));
                    }
                    System.out.println();
            }
            System.out.println();
    }
   
    public void printScoreAndAlignments() {
            System.out.println("Score: " + mScore);
            System.out.println("Sequence A: " + mAlignmentSeqA);
            System.out.println("Sequence B: " + mAlignmentSeqB);
            System.out.println();
    }
   
    public static void main(String [] args) {              
            //String[] seqB = { "A", "C", "G", "A" };
            //String[] seqA = { "T", "C", "C", "G" };
            String[] seqB = { "1U", "2C", "3H", "1U" };
            String[] seqA = { "4U", "2C", "2C", "3H" };
           
            SequenceAlignmentLocalSmithWaterman sw = new SequenceAlignmentLocalSmithWaterman();
            System.out.println(sw.getScore(seqA, seqB));
           
            sw.printMatrix();
            sw.printScoreAndAlignments();
    }
}
