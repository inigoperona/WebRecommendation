package ehupatras.markovmodel;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class MarkovChain {

	private Hashtable<String,Integer> m_symbols;
	private float[][] m_transitionMatrix;
	private float[] m_initialProbs;
	
	public MarkovChain(ArrayList<String[]> sequences){
		
		// Define the alphabet
		int index = 0;
		m_symbols = new Hashtable<String,Integer>(); 
		for(int i=0; i<sequences.size(); i++){
			String[] seq = sequences.get(i);
			for(int j=0; j<seq.length; j++){
				String sym = seq[j];
				if(!m_symbols.containsKey(sym)){
					m_symbols.put(sym, index);
					index++;
				}
			}
		}
		int nsymbols = m_symbols.size();
		
		// initialize the transition matrix and the initial probabilities
		int[] initialFreqs = new int[nsymbols];
		int[][] transitionMatrix = new int[nsymbols][nsymbols];
		for(int i=0; i<sequences.size(); i++){
			String[] seq = sequences.get(i);
			for(int j=0; j<seq.length-1; j++){
				String sym1 = seq[j];
				int symind1 = m_symbols.get(sym1);
				String sym2 = seq[j+1];
				int symind2 = m_symbols.get(sym2);
				
				// the first element of the sequence
				// update the initial probabilities
				if(j==0){
					initialFreqs[symind1]++;
				}
				
				// update the transition matrix
				transitionMatrix[symind1][symind2]++;
			}
		}
		
		// normalize initial frequencies
		int suminit = 0;
		for(int i=0; i<initialFreqs.length; i++){
			suminit = suminit + initialFreqs[i];
		}
		m_initialProbs = new float[nsymbols];
		for(int i=0; i<initialFreqs.length; i++){
			m_initialProbs[i] = (float)initialFreqs[i] / (float)suminit;
		}
		
		// normalize transition frequencies
		m_transitionMatrix = new float[nsymbols][nsymbols];
		for(int i=0; i<transitionMatrix.length; i++){
			int sumrow = 0;
			for(int j=0; j<transitionMatrix[i].length; j++){
				sumrow = sumrow + transitionMatrix[i][j];
			}
			for(int j=0; j<transitionMatrix[i].length; j++){
				if(sumrow>0){
					m_transitionMatrix[i][j] = (float)transitionMatrix[i][j] / (float)sumrow;
				} else {
					m_transitionMatrix[i][j] = 0f;
				}
			}
		}
	}
	
	public int numberOfSymbols(){
		return m_symbols.size();
	}
	
	
	public String getElemName(int index){
		Enumeration<String> keys = m_symbols.keys();
		String[] keysA = new String[m_symbols.size()];
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			int val = m_symbols.get(key);
			keysA[val] = key;
		}
		return keysA[index];
	}
	
	public int getElemIndex(String elemName){
		if(m_symbols.containsKey(elemName)){
			return m_symbols.get(elemName);
		} else {
			return -1;
		}
	}
	
	public float getInitialProb(int i){
		return m_initialProbs[i];
	}
	
	public float getTransitionMatrix(int i, int j){
		return m_transitionMatrix[i][j];
	}
	
	private void printMarkovChain(){
		// print initial probabilities
		System.out.println("Initial Probabilities: ");
		for(int i=0; i<m_initialProbs.length; i++){
			System.out.println("_" + this.getElemName(i) + "_:" + m_initialProbs[i]);
		}
		// print transition matrix
		System.out.println("Transition Matrix: ");
		for(int i=0; i<m_transitionMatrix.length; i++){
			System.out.print("_" + this.getElemName(i) + "_");
			for(int j=0; j<m_transitionMatrix[i].length; j++){
				System.out.print("," + m_transitionMatrix[i][j]);
			}
			System.out.println();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ArrayList<String[]> seqs = new ArrayList<String[]>(); 
		seqs.add(new String[] {"0", "1", "2", "3"});
		seqs.add(new String[] {"1", "2", "3", "4"});
		seqs.add(new String[] {"1", "4", "5", "6"});
		
		MarkovChain mc = new MarkovChain(seqs);
		mc.printMarkovChain();
	}
	
}
