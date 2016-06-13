package ehupatras.webrecommendation.sequencealignment;

import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentLevenshtein.
 */
public class SequenceAlignmentLevenshtein 
				implements SequenceAlignment{
	
    // weights of roles
    /** The m_role w. */
    protected float[][] m_roleW = {{ 0f, 0f, 0f},  // Unimportant
    					 		   { 0f, 0f, 0f},  // Hub
    					 		   { 0f, 0f, 0f}}; // Content
	
    // to work with topics
	/** The m_ url i ds. */
    protected ArrayList<Integer> m_UrlIDs = null;
	// URL to URL distance
	/** The m_ urls dm. */
	protected float[][] m_UrlsDM = null;
	
	/** The m_ ur ls equalness th. */
	protected float m_URLsEqualnessTh = 0.6f;
	// URL to topic distance
	/** The m_url2topic. */
	protected int[] m_url2topic = null;
	
	/** The m_topicmatch. */
	protected float m_topicmatch = 0.5f;
	
    // body
	
	/* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignment#getScore(java.lang.String[], java.lang.String[])
     */
    public float getScore(String[] seqA, String[] seqB) {
	    // i == 0
	    float[] costs = new float[seqB.length + 1];
	    for(int j=0; j<costs.length; j++){
	    	costs[j] = j;
	    }
	    for(int i=1; i<=seqA.length; i++){
	    	// j == 0; nw = lev(i - 1, j)
	        costs[0] = (float)i;
	        float nw = (float)i - 1f;
	        for(int j=1; j<=seqB.length; j++){
	        	float cj = Math.min(1f + Math.min(costs[j], costs[j-1]), 
	        					nw + weight2(seqA[i-1],seqB[j-1]));
	            nw = costs[j];
	            costs[j] = cj;
	        }
	    }
	    return costs[seqB.length];
	}
	
	/**
	 * Weight.
	 *
	 * @param strA the str a
	 * @param strB the str b
	 * @return the float
	 */
	private float weight(String strA, String strB){
		if(strA.equals(strB)){
			return 0;
		} else {
			return 1f;
		}
	}
	
    /**
     * Weight2.
     *
     * @param strA the str a
     * @param strB the str b
     * @return the float
     */
    protected float weight2(String strA, String strB) {
    	int len = strA.length();
    	String urlA = strA.substring(0,len-1);
    	int urlAi = Integer.valueOf(urlA);
    	String rolA = strA.substring(len-1,len);
    	int rolAi = this.role2int(rolA);
    	
    	len = strB.length();
    	String urlB = strB.substring(0,len-1);
    	int urlBi = Integer.valueOf(urlB);
    	String rolB = strB.substring(len-1,len);
    	int rolBi = this.role2int(rolB);
    	
        if(urlAi==urlBi){
        	return m_roleW[rolAi][rolBi];
        } else {
        	return 1f;
        }
    }
    
    /**
     * Role2int.
     *
     * @param role the role
     * @return the int
     */
    protected int role2int(String role){
    	int roli = 0; // default
    	if(role.equals("U")){ roli = 0; }
    	else if(role.equals("H")){ roli = 1;}
    	else if(role.equals("C")){ roli = 2;}
    	return roli;
    }
	
    /**
     * Weight3.
     *
     * @param strA the str a
     * @param strB the str b
     * @return the float
     */
    protected float weight3(String strA, String strB) {
    	int len = strA.length();
    	String urlA = strA.substring(0,len-1);
    	String rolA = strA.substring(len-1,len);
    	int urlAi = m_UrlIDs.indexOf(Integer.valueOf(urlA));
    	int rolAi = this.role2int(rolA);
    	
    	len = strB.length();
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
    	float dist = 1f; // maximum distance
    	float wrole;
    	if(wurl<=m_URLsEqualnessTh){
        	wrole = m_roleW[rolAi][rolBi];
        	dist = wurl * wrole;
        } else {
        	wrole = 1f;
        	dist = 1f;
        }
        
    	return dist;
    }
    
    /**
     * Weight4.
     *
     * @param strA the str a
     * @param strB the str b
     * @return the float
     */
    protected float weight4(String strA, String strB) {
    	int len = strA.length();
    	String urlA = strA.substring(0,len-1);
    	String rolA = strA.substring(len-1,len);
    	int urlAi = m_UrlIDs.indexOf(Integer.valueOf(urlA));
    	int rolAi = this.role2int(rolA);
    	
    	len = strB.length();
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
    	} else {
    		// analyze the topics
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
    	
    	// roles distance
    	float dist = 1f; // maximum distance
    	float wrole;
    	if(wurl<=m_topicmatch){
        	wrole = m_roleW[rolAi][rolBi];
        	dist = wurl * (1+wrole);
        } else {
        	wrole = 1f;
        	dist = 1f;
        }
        
    	return dist;
    }
    
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignment#setRoleWeights(float[][])
	 */
	public void setRoleWeights(float[][] roleweights){
		m_roleW = roleweights;
	}
	
    /**
     * Sets the UR ls equalness th.
     *
     * @param urlsEqualnessThreshold the new UR ls equalness th
     */
    public void setURLsEqualnessTh(float urlsEqualnessThreshold){
    	m_URLsEqualnessTh = urlsEqualnessThreshold;
    }
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String [] args) {
		
		String[] seq1 = new String[]{"kU","iU","tU","tU","eU","nU"};
		String[] seq2 = new String[]{"sC","iC","tC","tC","iC","nC","gC"};
		String[] seq3 = new String[]{"sH","aH","tH","uH","rH","dH","aH","yH"};
		String[] seq4 = new String[]{"sU","uC","nH","dU","aC","yH"};
		String[] seq5 = new String[]{"rU","oU","sC","eC","tH","tH","aU","cU","oC","dC","eU"};
		String[] seq6 = new String[]{"rC","aH","iC","sH","eC","tH","hC","yH","sC","wH","oC","rH","dC"};
		
		ArrayList<String[]> data = new ArrayList<String[]>();
		data.add(seq1);
		data.add(seq2);
		data.add(seq3);
		data.add(seq4);
		data.add(seq5);
		data.add(seq6);
		
		SequenceAlignmentLevenshtein ed = new SequenceAlignmentLevenshtein();
		for(int i=0; i<data.size(); i=i+2){
	    	System.out.println(ed.getScore(data.get(i), data.get(i+1)));
		}
	}

}

