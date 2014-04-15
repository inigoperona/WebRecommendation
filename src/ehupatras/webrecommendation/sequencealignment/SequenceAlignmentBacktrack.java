package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public abstract class SequenceAlignmentBacktrack 
						implements SequenceAlignment {
    protected String[] mSeqA;
    protected String[] mSeqB;
    protected float[][] mD;
    protected float mScore;
    protected String mAlignmentSeqA = "";
    protected String mAlignmentSeqB = "";
    protected String[] m_alignSeqA;
    protected String[] m_alignSeqB;
    protected String m_gap = "-";
    
    // weights of roles
    protected float[][] m_roleW = {{ 1f, 1f, 1f},  // Unimportant
    					 		   { 1f, 1f, 1f},  // Hub
    					 		   { 1f, 1f, 1f}}; // Content

    // to work with topic distributions
	protected ArrayList<Integer> m_UrlIDs = null;
	// URL to URL distance
	protected float[][] m_UrlsDM = null;
	protected float m_URLsEqualnessTh = 0.6f;
    // URL to topic
	protected int[] m_url2topic = null;
	protected float m_topicmatch = 0.5f;
	
    // Functions to get the standard sequence alignment score
    
    protected abstract void init(String[] seqA, String[] seqB);
    protected abstract void process();
    protected abstract void backtrack();
    
    public abstract float getScore(String[] seqA, String[] seqB);
    
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
    
    // Functions to get the Dimopoulos2010 string alignment variation
    
    protected abstract ArrayList<String[]> getTrimedAlignedSequences(String str1, String str2);
    
    public Float[] getAlignmentOperations(String[] seqA, String[] seqB){
    	// compute match / mismatch / gaps / spaces
    	computeAlignment(seqA,seqB);
    	ArrayList<String[]> trimmedSeqs = getTrimedAlignedSequences(mAlignmentSeqA, mAlignmentSeqB);
    	m_alignSeqA = trimmedSeqs.get(0);
    	m_alignSeqB = trimmedSeqs.get(1);
    	int alignLen = m_alignSeqA.length;
    	float nmatches = 0;
    	float nmismatches = 0;
    	float ngaps = 0;
    	float nspaces = 0;
    	String previousElemA = "";
    	String previousElemB = "";
    	for(int i=0; i<alignLen; i++){
    		String elemA = m_alignSeqA[i];
    		String elemB = m_alignSeqB[i];
    		float w = equalURLs(elemA, elemB);
    		if(w>=0){
    			// match score-weight greater than zero (best 1)
    			// else score-weight less than zero (worst -1)
    			if(!elemA.equals(m_gap) && !elemB.equals(m_gap)){
    				nmatches = nmatches + w;
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
    	Float[] counts = new Float[4];
    	counts[0] = nmatches;
    	counts[1] = nmismatches;
    	counts[2] = ngaps;
    	counts[3] = nspaces;
    	return counts;
    }
    
    protected String[] getStringArrayRepresentation(String str){
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
                        System.out.print(String.format("%4f ", mD[i][j]));
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

    public String[] getAlignSeqA(){
    	return m_alignSeqA;
    }
    
    public String[] getAlignSeqB(){
    	return m_alignSeqB;
    }
    
    
    // similarity weight of two URLs
    
    protected int weight(int i, int j) {
        if (mSeqA[i - 1].equals(mSeqB[j - 1])) {
                return 1;
        } else {
                return -1;
        }
    }
    
    protected float weight2(int i, int j) {
    	return this.equalURLs(mSeqA[i-1], mSeqB[j-1]);
    }
    
    protected float equalURLs(String strA, String strB){
    	int len = m_gap.length();
    	String urlA = strA.substring(0,len-1);
    	String rolA = strA.substring(len-1,len);
    	int rolAi = this.role2int(rolA);
    	String urlB = strB.substring(0,len-1);
    	String rolB = strB.substring(len-1,len);
    	int rolBi = this.role2int(rolB);
    	
        if (urlA.equals(urlB)){
        	return m_roleW[rolAi][rolBi];
        } else {
        	return -1f;
        }
    }
    
    protected float weight3(String strA, String strB) {
    	int len = strA.length();
    	String urlA = strA.substring(0,len-1);
    	String rolA = strA.substring(len-1,len);
    	int urlAi = m_UrlIDs.indexOf(Integer.valueOf(urlA));
    	int rolAi = this.role2int(rolA);
    	String urlB = strB.substring(0,len-1);
    	String rolB = strB.substring(len-1,len);
    	int urlBi = m_UrlIDs.indexOf(Integer.valueOf(urlB));
    	int rolBi = this.role2int(rolB);
    	
    	// urls distance
    	float wurl;
    	if(urlAi==-1 || urlBi==-1){
    		wurl = 1f; // maximun distance
    	} else {
    		wurl = m_UrlsDM[urlAi][urlBi];
    	}
    	// roles
    	float wrole;
        if(wurl<=m_URLsEqualnessTh){
        	wrole = m_roleW[rolAi][rolBi];
        } else {
        	wrole = -1f;
        }
        
        return wrole*(1-wurl);
    }
    
    protected float weight4(String strA, String strB) {
    	// ensure that we do not have any gap
    	if(strA.equals(m_gap) || strB.equals(m_gap)){ return -1; }
    	
    	// compare the two elements
    	int len = strA.length();
    	String urlA = strA.substring(0,len-1);
    	String rolA = strA.substring(len-1,len);
    	int urlAi = m_UrlIDs.indexOf(Integer.valueOf(urlA));
    	int rolAi = this.role2int(rolA);
    	String urlB = strB.substring(0,len-1);
    	String rolB = strB.substring(len-1,len);
    	int urlBi = m_UrlIDs.indexOf(Integer.valueOf(urlB));
    	int rolBi = this.role2int(rolB);
    	
    	// urls distance
    	float wurl = 1f; // initialize with maximum distance
    	if(urlA.equals(urlB)){ // same URL
    		wurl = 0; // minimum distance
    	} else if(urlAi==-1 || urlBi==-1){ // a new URL, does not exists
    		wurl = 1f; // maximum distance
    	} else { // analyze the topics
    		int urlAtopic = m_url2topic[urlAi];
    		int urlBtopic = m_url2topic[urlBi];
    		if(urlAtopic==-1 || urlBtopic==-1){ // topic no available
    			wurl = 1f; // maximum distance
    		} else {
    			if(urlAtopic==urlBtopic){
    				wurl = m_topicmatch;
    			} else {
    				wurl = 1; // maximum distance
    			}
    		}
    	}
    	
    	// roles
    	float score = -1f; // worse score
    	float wrole;
        if(wurl<=m_topicmatch){
        	wrole = m_roleW[rolAi][rolBi];
        	score = (1f-wurl)*wrole;
        } else {
        	wrole = -1f;
        	score = -1f;
        }
        
        return score;
    }
    
    protected int role2int(String role){
    	int roli = 0;
    	if(role.equals("U")){ roli = 0; }
    	else if(role.equals("H")){ roli = 1;}
    	else if(role.equals("C")){ roli = 2;}
    	return roli;
    }
    
    public void setRoleWeights(float[][] roleweights){
    	m_roleW = roleweights;
    }
    
    public void setURLsEqualnessTh(float urlsEqualnessThreshold){
    	m_URLsEqualnessTh = urlsEqualnessThreshold;
    }
    
}
