package ehupatras.markovmodel;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

// TODO: Auto-generated Javadoc
/**
 * The Class MarkovChain.
 */
public class MarkovChain {

	/** The m_symbols. */
	private Hashtable<String,Integer> m_symbols;
	
	/** The m_transition matrix. */
	private float[][] m_transitionMatrix;
	
	/** The m_initial probs. */
	private float[] m_initialProbs;
	
	/**
	 * Instantiates a new markov chain.
	 *
	 * @param sequences the sequences
	 */
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
	
	/**
	 * Number of symbols.
	 *
	 * @return the int
	 */
	public int numberOfSymbols(){
		return m_symbols.size();
	}
	
	
	/**
	 * Gets the elem name.
	 *
	 * @param index the index
	 * @return the elem name
	 */
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
	
	/**
	 * Gets the elem index.
	 *
	 * @param elemName the elem name
	 * @return the elem index
	 */
	public int getElemIndex(String elemName){
		if(m_symbols.containsKey(elemName)){
			return m_symbols.get(elemName);
		} else {
			return -1;
		}
	}
	
	/**
	 * Gets the initial prob.
	 *
	 * @param i the i
	 * @return the initial prob
	 */
	public float getInitialProb(int i){
		return m_initialProbs[i];
	}
	
	/**
	 * Gets the transition matrix.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the transition matrix
	 */
	public float getTransitionMatrix(int i, int j){
		return m_transitionMatrix[i][j];
	}
	
	/**
	 * Prints the markov chain.
	 */
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
	 * The main method.
	 *
	 * @param args the arguments
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
