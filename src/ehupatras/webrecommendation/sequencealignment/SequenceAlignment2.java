package ehupatras.webrecommendation.sequencealignment;

public abstract class SequenceAlignment2 
						implements SequenceAlignment {
    protected String[] mSeqA;
    protected String[] mSeqB;
    protected int[][] mD;
    protected int mScore;
    protected String mAlignmentSeqA = "";
    protected String mAlignmentSeqB = "";
    protected String m_gap = "-";

    protected abstract void init(String[] seqA, String[] seqB);
    protected abstract void process();
    protected abstract void backtrack();
    
    protected void computeAlignment(String[] seqA, String[] seqB){
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
    
    private Integer[] getAlignmentOperations(String[] seqA, String[] seqB){
    	// compute match / mismatch / gaps / spaces
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
    	
    	// return the count values
    	Integer[] counts = new Integer[4];
    	counts[0] = nmatches;
    	counts[1] = nmismatches;
    	counts[2] = ngaps;
    	counts[3] = nspaces;
    	return counts;
    }
    
    public float getTweakedScore(String[] seqA, String[] seqB){
    	// constants
    	float wm = (float)1;
    	float wms = (float)1;
    	float wg = (float)1;
    	float ws = (float)5;
    	
    	// counts
    	Integer[] counts = getAlignmentOperations(seqA, seqB);
    	int nmatches = counts[0];
    	int nmismatches = counts[1];
    	int ngaps = counts[2];
    	int nspaces = counts[3];

    	// compute the score and return
    	float score = wm*(float)nmatches 
    			- wms*(float)nmismatches - wg*(float)ngaps - ws*(float)nspaces;
    	return score;
    }
    
    public float getTweakedDistance(String[] seqA, String[] seqB){
    	// constants
    	float wm = (float)1;
    	float wms = (float)1;
    	float wg = (float)1;
    	float ws = (float)5;
    	
    	// counts
    	Integer[] counts = getAlignmentOperations(seqA, seqB);
    	int nmatches = counts[0];
    	int nmismatches = counts[1];
    	int ngaps = counts[2];
    	int nspaces = counts[3];

    	// compute the score and return
    	float score = (wms*(float)nmismatches + wg*(float)ngaps + ws*(float)nspaces)
    			 * (float)nmatches/(float)seqA.length;
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

}
