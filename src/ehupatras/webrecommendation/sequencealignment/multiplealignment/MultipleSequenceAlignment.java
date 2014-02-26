package ehupatras.webrecommendation.sequencealignment.multiplealignment;

import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentGlobalNeedlemanWunsch;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack;
import java.util.ArrayList;

public class MultipleSequenceAlignment {

	private String m_gap = "-";
	private ArrayList<String[]> m_msa; 
	
	public void msa(ArrayList<Integer> seqclust, float[][] d, ArrayList<String[]> was){
		// define the gap length
		int gaplen = was.get(0)[0].length();
		m_gap = "";
		for(int i=0; i<gaplen; i++){ m_gap = m_gap + "-"; }
		
		// For each sequence accumulate distance to other sequences
		ArrayList<Float> dist = new ArrayList<Float>();
		for(int i=0; i<seqclust.size(); i++){
			dist.add((float)0);
			for(int j=0; j<seqclust.size(); j++){
				if(i!=j){
					int rowindex = seqclust.get(j);
					int colindex = seqclust.get(i);
					float newdist = dist.get(i) + d[rowindex][colindex];
					dist.set(i, newdist);
				}
			}
		}
		
		// See which sequence is most centered.
		float mind = dist.get(0);
		int min = seqclust.get(0);
		for(int i=0; i<seqclust.size(); i++){
			if(mind>dist.get(i)){
				mind = dist.get(i);
				min = seqclust.get(i);
			}
		}
		String[] center = was.get(min);
		
		// initialize each center position amount of spaces to zero
		ArrayList<Integer> spaces = new ArrayList<Integer>();
		for(int i=0; i<=center.length; i++){
			spaces.add(0);
		}
		
		// Progressively align the center with other j-sequences
		ArrayList<String[]> alingsCenter = new ArrayList<String[]>();
		ArrayList<String[]> alingsNode = new ArrayList<String[]>();
		for(int j=0; j<seqclust.size(); j++){
			if(min!=seqclust.get(j)){
				
				// Perform global alignment betweeen center and sequencej
				SequenceAlignmentBacktrack seqalign = new SequenceAlignmentGlobalNeedlemanWunsch();
		    	String[] sequencej = was.get(seqclust.get(j));
		    	Integer[] counts = seqalign.getAlignmentOperations(center, sequencej);
		    	int nmatches = counts[0];
		    	int nmismatches = counts[1];
		    	int ngaps = counts[2];
		    	int nspaces = counts[3];
				String[] align1 = seqalign.getAlignSeqA();
				String[] align2 = seqalign.getAlignSeqB();
				
				// save the aligned sequences
				alingsCenter.add(align1); // all the versions of the aligned center
				alingsNode.add(align2);
				
				// update spaces (consecutive gaps) counter of the center
				// because "once gap, always gap"
				int pos = 0;
				int cnt = 0;
				while(pos<align1.length-1){
					Object[] objA = this.nextSymbol(align1,pos);
					int gaps = (int)objA[0];
					pos = (int)objA[1];
					if(gaps>spaces.get(cnt)){
						spaces.set(cnt, gaps);
					}
					cnt++;
				}
			}
		}
		
		// create the new center (reference) with new gaps-spaces
		ArrayList<String> newCenter = new ArrayList<String>();
		int ind;
		for(ind=0; ind<center.length; ind++){
			for(int j=0; j<spaces.get(ind); j++){ 
				newCenter.add(m_gap);
			}
			newCenter.add(center[ind]);
		}
		for(int j=0; j<spaces.get(ind); j++){newCenter.add(m_gap);} // gaps at the end
		String[] newCenterA = new String[newCenter.size()];
		for(int i=0; i<newCenter.size(); i++){ newCenterA[i] = newCenter.get(i); }
		
		// Align the new-center with other j-sequences
		// Hashtable<String,Integer>[] ws = new Hashtable[seqclust.size()];
		// for(int i=0; i<seqclust.size(); i++){ ws[i] = new Hashtable<String,Integer>(); }
		m_msa = new ArrayList<String[]>();
		m_msa.add(newCenterA); // align1, in index 0
		for(int j=0; j<seqclust.size(); j++){
			if (min!=seqclust.get(j)){
				
				// Perform the alignment
				SequenceAlignmentBacktrack seqalign = new SequenceAlignmentGlobalNeedlemanWunsch();
				String[] sequencej = was.get(seqclust.get(j));
				Integer[] counts = seqalign.getAlignmentOperations(newCenterA, sequencej);
		    	int nmatches = counts[0];
		    	int nmismatches = counts[1];
		    	int ngaps = counts[2];
		    	int nspaces = counts[3];
		    	String[] align1 = seqalign.getAlignSeqA();
				String[] align2 = seqalign.getAlignSeqB();
				
				// save the new alignment version
				m_msa.add(align2);				
			}
		}
	}
	
	private Object[] nextSymbol(String[] symbols, int actualpos){
		int gaps = 0;
		String symb = symbols[actualpos];
		while(symb.equals(m_gap)){
			gaps++;
			actualpos++;
			if(actualpos<symbols.length){
				symb = symbols[actualpos];
			} else {
				symb = "";
			}
		}
		if(gaps==0){ actualpos++; }
		Object[] objA = new Object[2];
		objA[0] = gaps; // number of consecutive gaps
		objA[1] = actualpos; // position of the next no-gap symbol
		return objA;
	}
	
	private String[][] getMultipleSequenceAlignment(){
		int seqlen = m_msa.get(0).length;
		String[][] msaMatrix = new String[m_msa.size()][seqlen];
		for(int i=0; i<m_msa.size(); i++){
			String[] alseq = m_msa.get(i); 
			for(int j=0; j<alseq.length; j++){
				msaMatrix[i][j] = alseq[j];
			}
		}	
		return msaMatrix;
	}
	
	private void writeAlignedSequences(){
		for(int i=0; i<m_msa.size(); i++){
			String[] alseq = m_msa.get(i);
			System.out.print(i + ": ");
			for(int j=0; j<alseq.length; j++){
				System.out.print(alseq[j] + ".");
			}
			System.out.println();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Database
		String[] seq1 = {"1U", "2H", "3C", "4U"};
		String[] seq2 = {"1U", "2U", "3C", "4H"};
		String[] seq3 = {"2C", "3C", "4C", "3H"};
		String[] seq4 = {"3C", "4C", "2C", "1U"};
		String[] seq5 = {"3H", "4C", "2C", "1H"};
		String[] seq6 = {"3U", "4C", "2H", "1H"};
		
		// Create WAS-array
		ArrayList<String[]> wasL = new ArrayList<String[]>();
		wasL.add(seq1);
		wasL.add(seq2);
		wasL.add(seq3);
		wasL.add(seq4);
		wasL.add(seq5);
		wasL.add(seq6);
		
		// Which are in the same cluster?
		ArrayList<Integer> clusters = new ArrayList<Integer>();
		clusters.add(1);
		clusters.add(2);
		clusters.add(4);
		clusters.add(5);
		
		// Create the distance matrix of the sequences
		ehupatras.webrecommendation.distmatrix.Matrix dm = 
				new ehupatras.webrecommendation.distmatrix.SimilarityMatrix();
		dm.computeMatrix(wasL);
		float[][] dmatrix = dm.getMatrix();

		// Perform the multiple sequence alignment
		MultipleSequenceAlignment malign = new MultipleSequenceAlignment();
		malign.msa(clusters, dmatrix, wasL);
		malign.writeAlignedSequences();
		String[][] msaM = malign.getMultipleSequenceAlignment();
	}

}
