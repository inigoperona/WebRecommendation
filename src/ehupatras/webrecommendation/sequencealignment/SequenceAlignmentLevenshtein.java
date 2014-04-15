package ehupatras.webrecommendation.sequencealignment;

import java.util.*;

public class SequenceAlignmentLevenshtein 
				implements SequenceAlignment{
	
    // weights of roles
    protected float[][] m_roleW = {{ 0f, 0f, 0f},  // Unimportant
    					 		   { 0f, 0f, 0f},  // Hub
    					 		   { 0f, 0f, 0f}}; // Content
	
    // to work with topics
	protected ArrayList<Integer> m_UrlIDs = null;
	// URL to URL distance
	protected float[][] m_UrlsDM = null;
	protected float m_URLsEqualnessTh = 0.6f;
	// URL to topic distance
	protected int[] m_url2topic = null;
	protected float m_topicmatch = 0.5f;
	
    // body
	
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
	        					nw + this.weight2(seqA[i-1],seqB[j-1]));
	            nw = costs[j];
	            costs[j] = cj;
	        }
	    }
	    return costs[seqB.length];
	}
	
	private float weight(String strA, String strB){
		if(strA.equals(strB)){
			return 0;
		} else {
			return 1f;
		}
	}
	
    protected float weight2(String strA, String strB) {
    	int len = strA.length();
    	String urlA = strA.substring(0,len-1);
    	String rolA = strA.substring(len-1,len);
    	int rolAi = this.role2int(rolA);
    	String urlB = strB.substring(0,len-1);
    	String rolB = strB.substring(len-1,len);
    	int rolBi = this.role2int(rolB);
    	
        if (urlA.equals(urlB)){
        	return m_roleW[rolAi][rolBi];
        } else {
        	return 1f;
        }
    }
    
    protected int role2int(String role){
    	int roli = 0;
    	if(role.equals("U")){ roli = 0; }
    	else if(role.equals("H")){ roli = 1;}
    	else if(role.equals("C")){ roli = 2;}
    	return roli;
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
    
    protected float weight4(String strA, String strB) {
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
    			wurl = m_topicmatch;
    		}
    	}
    	
    	// roles distance
    	float dist = 1f; // maximum distance
    	float wrole;
    	if(wurl<=m_topicmatch){
        	wrole = m_roleW[rolAi][rolBi];
        	dist = wurl * wrole;
        } else {
        	wrole = 1f;
        	dist = 1f;
        }
        
    	return dist;
    }
    
	public void setRoleWeights(float[][] roleweights){
		m_roleW = roleweights;
	}
	
    public void setURLsEqualnessTh(float urlsEqualnessThreshold){
    	m_URLsEqualnessTh = urlsEqualnessThreshold;
    }
	
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

