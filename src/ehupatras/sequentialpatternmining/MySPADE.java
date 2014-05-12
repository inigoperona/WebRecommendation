package ehupatras.sequentialpatternmining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

public class MySPADE {
	// sequences
	private ArrayList<String[]> m_sequences;
	private int m_nSeqs;
	
	// frequency of URLs
	private double m_minsupport = 0.5d;
	private String[] m_urls;
	private int[] m_freqs;
	private int m_minfreq;
	
	public MySPADE(ArrayList<String[]> sequences, float minsup){
		m_sequences = sequences;
		m_nSeqs = sequences.size();
		m_minsupport = minsup;
		m_minfreq = (int)Math.ceil( (float)m_nSeqs*m_minsupport );
		
		Hashtable<String,Integer> frequencies = new Hashtable<String,Integer>();
		for(int i=0; i<sequences.size(); i++){
			String[] seq = sequences.get(i);
			
			// uniq the seq
			ArrayList<String> seq2 = new ArrayList<String>();
			for(int j=0; j<seq.length; j++){
				String url = seq[j];
				if(!seq2.contains(url)){
					seq2.add(url);
				}
			}
			
			// count the frequencies for each URL 
			for(int j=0; j<seq2.size(); j++){
				String url = seq2.get(j);
				if(frequencies.containsKey(url)){
					int freq = frequencies.get(url);
					freq++;
					frequencies.put(url, freq);
				} else {
					frequencies.put(url, 1);
				}
			}
		}
		
		// Filter the URLs which has small frequencies
		ArrayList<String> urls = new ArrayList<String>();
		ArrayList<Integer> freqs = new ArrayList<Integer>();
		Enumeration<String> keys = frequencies.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			int freq = frequencies.get(key);
			if(freq>1){
				urls.add(key);
				freqs.add(freq);
			}
		}
		
		// order the URLs by frequency
		m_urls = new String[freqs.size()];
		m_freqs = new int[freqs.size()];
		boolean[] isOrdered = new boolean[freqs.size()];
		Arrays.fill(isOrdered, false);
		for(int i=0; i<freqs.size(); i++){
			int maxfreq = Integer.MIN_VALUE;
			int maxind = -1;
			for(int j=0; j<freqs.size(); j++){
				int freq = freqs.get(j);
				if(!isOrdered[j] && maxfreq<freq){
					maxfreq = freq;
					maxind = j;
				}
			}
			m_freqs[i] = freqs.get(maxind);
			m_urls[i] = urls.get(maxind);
			isOrdered[maxind] = true;
		}
	}
	
	
	public Object[] getFrequentSequencesLength1(){
    	ArrayList<String> urls = new ArrayList<String>();
    	ArrayList<Integer> sups = new ArrayList<Integer>();
		for(int i=0; i<m_freqs.length; i++){
			int freq = m_freqs[i];
			if(freq>=m_minfreq){
				String url = m_urls[i];
				sups.add(freq);
				urls.add(url);
			} else {
				break;
			}
		}
    	
    	// return the value
    	Object[] objA = new Object[2];
    	objA[0] = urls;
    	objA[1] = sups;
		return objA;
	}
	
	
	public Object[] getFrequentSequencess(){
		// get frequent URLs
		Object[] objA = this.getFrequentSequencesLength1();
		ArrayList<String> urls = (ArrayList<String>)objA[0];
    	ArrayList<Integer> urlSups = (ArrayList<Integer>)objA[1];
    	
    	
    	// remove those no frequent URLs
    	ArrayList<ArrayList<String>> seqstrim = new ArrayList<ArrayList<String>>(); 
    	for(int i=0; i<m_nSeqs; i++){
    		
    		// for each sequence
    		String[] seq1 = m_sequences.get(i);
    		ArrayList<String> seq2 = new ArrayList<String>(); 
    		for(int j=0; j<seq1.length; j++){
    			
    			// for each URL, is frequent?
    			String str1 = seq1[j];
    			int ind = urls.indexOf(str1);
    			if(ind!=-1){
    				seq2.add(str1);
    			}
    		}
    		
    		// add the trimmed sequence
    		if(seq2.size()>0){
    			seqstrim.add(seq2);
    		}
    	}
    	System.out.println("  myspade: initial iteration: " + 
    				seqstrim.size() + "/" + m_nSeqs);
    	System.gc();
 
    	
    	// add URLs as a length-1 sequences
    	ArrayList<String[]> seqs = new ArrayList<String[]>();
    	ArrayList<Integer> seqSups = new ArrayList<Integer>();
    	ArrayList<int[]> indsAL = new ArrayList<int[]>();
    	for(int i=0; i<urls.size(); i++){
    		String url = urls.get(i);
    		String[] seq = new String[1];
    		seq[0] = url;
    		seqs.add(seq);
    		seqSups.add(urlSups.get(i));
    		int[] inds = new int[seqstrim.size()];
    		for(int j=0; j<seqstrim.size(); j++){
    			int ind = seqstrim.get(j).indexOf(url);
    			inds[j] = ind;
    		}
    		indsAL.add(inds);
    	}
    	
    	// create new sequences using frequent URLs
    	for(int i=0; i<seqs.size(); i++){
    		String[] seq = seqs.get(i);
    		int[] inds = indsAL.get(i);
    		
    		// add frequent URL and count
    		for(int j=0; j<urls.size(); j++){
    			String url = urls.get(j);
    			
    			// see in how many sequences the next URL exists
    			int freq = 0;
    			int[] inds2 = new int[seqstrim.size()];
        		for(int k=0; k<seqstrim.size(); k++){
        			ArrayList<String> seqtr = seqstrim.get(k);
        			int from = inds[k];
        			int find = -1;
        			if(from!=-1){
        				find = this.indexOf(url, from+1, seqtr);
        			}
        			inds2[k] = find;
        			if(find!=-1){
        				freq++;
        			}
        		}
        		
        		// check the support
    			if(freq>=m_minfreq){
    				String[] newseq = new String[seq.length+1];
    				int k;
    				for(k=0; k<seq.length; k++){
    					newseq[k] = seq[k];
    				}
    				newseq[k] = url;
    				seqs.add(newseq);
    				seqSups.add(freq);
    				indsAL.add(inds2);
    				System.out.println("  myspade: " + i + "x" + j + " iteration: " +
    							freq + "/" + m_nSeqs);
    			}
    		}
    	}
    	
    	// prepare the sequences to return
    	Object[] objRe = new Object[2];
    	objRe[0] = seqs;
    	objRe[1] = seqSups;
    	return objRe;
	}
	
	private int indexOf(String url, int from, ArrayList<String> seq){
		// find the requested url
		int ind = -1;
		for(int i=from; i<seq.size(); i++){
			String url2 = seq.get(i);
			if(url.equals(url2)){
				ind = i;
				break;
			}
		}
		return ind;
	}
	
	private String sequenceToString(String[] sequence){
		String str;
		if(sequence.length>0){
			str = sequence[0];
		} else {
			str = "";
		}
		for(int j=1; j<sequence.length; j++){
			str = str + "," + sequence[j];
		}
		return str;
	}
	
	public static void main(String[] args){
		String[] seq1 = new String[]{"1","2","3","4","5"};
		String[] seq2 = new String[]{"1","2","3","9","8"};
		String[] seq3 = new String[]{"1","2","7","6","9"};
		String[] seq4 = new String[]{"1","6","8","7","5"};
		ArrayList<String[]> seqs = new ArrayList<String[]>();
		seqs.add(seq1);
		seqs.add(seq2);
		seqs.add(seq3);
		seqs.add(seq4);
		MySPADE msp = new MySPADE(seqs, 0.5f);
		
		// Lenth 1 sequences
		System.out.println("--- Lenth 1 frequent sequences ---");
		Object[] objA1 = msp.getFrequentSequencesLength1();
		ArrayList<String> urls = (ArrayList<String>)objA1[0];
		ArrayList<Integer> sups = (ArrayList<Integer>)objA1[1];
		for(int i=0; i<urls.size(); i++){
			System.out.println(urls.get(i) + " - " + sups.get(i));
		}
		
		// frequent sequences
		System.out.println("--- Frequent Sequences ---");
		Object[] objA2 = msp.getFrequentSequencess();
		ArrayList<String[]> freqSeqs = (ArrayList<String[]>)objA2[0];
		ArrayList<Integer> freqSups = (ArrayList<Integer>)objA2[1];
		for(int i=0; i<freqSeqs.size(); i++){
			String[] freqseq = freqSeqs.get(i);
			System.out.println("i" + i  + ": " + 
						msp.sequenceToString(freqseq) + " - " + 
						freqSups.get(i));
		}
	}
	
}
