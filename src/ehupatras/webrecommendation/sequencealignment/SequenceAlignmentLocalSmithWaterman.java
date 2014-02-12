package ehupatras.webrecommendation.sequencealignment;

public class SequenceAlignmentLocalSmithWaterman implements SequenceAlignment{
    private String[] mSeqA;
    private String[] mSeqB;
    private int[][] mD;
    private int mScore;
    private String mAlignmentSeqA = "";
    private String mAlignmentSeqB = "";
    private String m_gap = "-";
   
    private void computeAlignment(String[] seqA, String[] seqB){
    	// initialize all class attributes
    	mAlignmentSeqA = "";
    	mAlignmentSeqB = "";
    	m_gap = "-";
    	// create the gap String
    	int gaplen = seqA[0].length();
    	for(int i=1; i<gaplen; i++){ m_gap = m_gap + "-"; }
    	
    	// compute the score
        init(seqA, seqB);
        process();
        backtrack();
    }
    
    public float getScore(String[] seqA, String[] seqB){
    	computeAlignment(seqA,seqB);
        return (float)mScore;
    }
    
    public float getweakedScore(String[] seqA, String[] seqB){
    	// constants
    	float wm = (float)1;
    	float wms = (float)1;
    	float wg = (float)1;
    	float ws = (float)5;
    	
    	// compute the score
    	computeAlignment(seqA,seqB);
    	String[] alignSeqA = getStringArrayRepresentation(mAlignmentSeqA);
    	String[] alignSeqB = getStringArrayRepresentation(mAlignmentSeqB);
    	int alignLen = alignSeqA.length;
    	int nmatches = 0;
    	int nmismatches = 0;
    	int ngaps = 0;
    	int nspaces = 0;
    	String previousElemA = "";
    	String previousElemB = "";
    	for(int i=0; i<alignLen; i++){
    		String elemA = alignSeqA[i];
    		String elemB = alignSeqB[i];
    		if(elemA.equals(elemB)){ 
    			if(!elemA.equals(m_gap) && !elemB.equals(m_gap)){
    				nmatches++;
    			} else { // gaps
    				boolean iscounted = false;
    				if(elemA.equals(m_gap)){
    					if(previousElemA.equals(m_gap)){
    						nspaces++;
    						iscounted = true;
    					}
    				}
    				if(elemB.equals(m_gap)){
    					if(previousElemB.equals(m_gap)){
    						nspaces++;
    						iscounted = true;
    					}
    				}
    				if(!iscounted){
    					ngaps++;
    				}
    			}
    		} else {
       			if(!elemA.equals(m_gap) && !elemB.equals(m_gap)){
       				nmismatches++;
       			} else { // gaps
    				boolean iscounted = false;
    				if(elemA.equals(m_gap)){
    					if(previousElemA.equals(m_gap)){
    						nspaces++;
    						iscounted = true;
    					}
    				}
    				if(elemB.equals(m_gap)){
    					if(previousElemB.equals(m_gap)){
    						nspaces++;
    						iscounted = true;
    					}
    				}
    				if(!iscounted){
    					ngaps++;
    				}
    			}
    		}
    		previousElemA = elemA;
    		previousElemB = elemB;
    	}
    	float score = wm*(float)nmatches 
    			- wms*(float)nmismatches - wg*(float)ngaps - ws*(float)nspaces;
    	return score;
    }
    
    private String[] getStringArrayRepresentation(String str){
    	int alignLen = str.length()/m_gap.length();
    	String[] seq = new String[alignLen];
    	for(int i=0; i<alignLen; i++){
    		int startind = i*m_gap.length();
    		seq[i] = str.substring(startind, startind+m_gap.length());
    	}
    	return seq;
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
            String[] seqB = { "01U", "02C", "03H", "01U" };
            String[] seqA = { "04U", "02C", "02C", "03H" };
           
            SequenceAlignmentLocalSmithWaterman sw = new SequenceAlignmentLocalSmithWaterman();
            System.out.println(sw.getScore(seqA, seqB));
            System.out.println(sw.getweakedScore(seqA, seqB));
           
            sw.printMatrix();
            sw.printScoreAndAlignments();
    }
}
