package ehupatras.sequentialpatternmining;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

public class MySPADE {
	private int m_nSeqs;
	private double m_minsupport = 0.5d;
	private String[] m_urls;
	private int[] m_freqs;
	
	public MySPADE(ArrayList<String[]> sequences, float minsup){
		m_minsupport = minsup;
		m_nSeqs = sequences.size();
		
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
		int minfreq = (int)Math.ceil( (float)m_nSeqs*m_minsupport );
		
    	ArrayList<Integer> sups = new ArrayList<Integer>();
    	ArrayList<String> urls = new ArrayList<String>();
		for(int i=0; i<m_freqs.length; i++){
			int freq = m_freqs[i];
			if(freq>=minfreq){
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
	
}
