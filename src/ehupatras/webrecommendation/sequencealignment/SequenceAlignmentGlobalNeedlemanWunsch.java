package ehupatras.webrecommendation.sequencealignment;

public class SequenceAlignmentGlobalNeedlemanWunsch implements SequenceAlignment{
    private String[] mSeqA;
    private String[] mSeqB;
    private int[][] mD;
    private int mScore;
    private String mAlignmentSeqA = "";
    private String mAlignmentSeqB = "";
    private String m_gap = "-";
   
    public int getScore(String[] seqA, String[] seqB){
    	// create the gap String
    	int gaplen = seqA[0].length();
    	for(int i=1; i<gaplen; i++){ m_gap = m_gap + "-"; }
    	
    	// compute the score
        init(seqA, seqB);
        process();
        backtrack();
        return mScore;
    }
    
    private void init(String[] seqA, String[] seqB) {
            mSeqA = seqA;
            mSeqB = seqB;
            mD = new int[mSeqA.length + 1][mSeqB.length + 1];
            for (int i = 0; i <= mSeqA.length; i++) {
                    for (int j = 0; j <= mSeqB.length; j++) {
                            if (i == 0) {
                                    mD[i][j] = -j;
                            } else if (j == 0) {
                                    mD[i][j] = -i;
                            } else {
                                    mD[i][j] = 0;
                            }
                    }
            }
    }
   
    private void process() {
            for (int i = 1; i <= mSeqA.length; i++) {
                    for (int j = 1; j <= mSeqB.length; j++) {
                            int scoreDiag = mD[i-1][j-1] + weight(i, j);
                            int scoreLeft = mD[i][j-1] - 1;
                            int scoreUp = mD[i-1][j] - 1;
                            mD[i][j] = Math.max(Math.max(scoreDiag, scoreLeft), scoreUp);
                    }
            }
    }
   
    private void backtrack() {
            int i = mSeqA.length;
            int j = mSeqB.length;
            mScore = mD[i][j];
            while (i > 0 && j > 0) {                        
                    if (mD[i][j] == mD[i-1][j-1] + weight(i, j)) {                          
                            mAlignmentSeqA += mSeqA[i-1];
                            mAlignmentSeqB += mSeqB[j-1];
                            i--;
                            j--;                            
                            continue;
                    } else if (mD[i][j] == mD[i][j-1] - 1) {
                            mAlignmentSeqA += m_gap;
                            mAlignmentSeqB += mSeqB[j-1];
                            j--;
                            continue;
                    } else {
                            mAlignmentSeqA += mSeqA[i-1];
                            mAlignmentSeqB += m_gap;
                            i--;
                            continue;
                    }
            }
            mAlignmentSeqA = new StringBuffer(mAlignmentSeqA).reverse().toString();
            mAlignmentSeqB = new StringBuffer(mAlignmentSeqB).reverse().toString();
    }
   
    private int weight(int i, int j) {
            if (mSeqA[i - 1].equals(mSeqB[j - 1])) {
                    return 1;
            } else {
                    return -1;
            }
    }
   
    public void printMatrix() {
            System.out.println("D =");
            for (int i = 0; i < mSeqA.length + 1; i++) {
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
        //String[] seqA = { "A", "C", "G", "T", "C" };
        //String[] seqB = { "A", "G", "T", "C" };
    	String[] seqA = { "1H", "2C", "3U", "4H", "1C" };
    	String[] seqB = { "1H", "3U", "4H", "1C" };

        SequenceAlignmentGlobalNeedlemanWunsch nw = new SequenceAlignmentGlobalNeedlemanWunsch();
        System.out.println(nw.getScore(seqA, seqB));

        nw.printMatrix();
        nw.printScoreAndAlignments();
    }
}
