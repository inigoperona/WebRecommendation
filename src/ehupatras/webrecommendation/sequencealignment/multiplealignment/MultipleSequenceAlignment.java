package ehupatras.webrecommendation.sequencealignment.multiplealignment;

import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentGlobalNeedlemanWunsch;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentBacktrack;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class MultipleSequenceAlignment.
 */
public class MultipleSequenceAlignment {

	/** The m_gap. */
	private String m_gap = "-";
	
	/** The m_msa. */
	private ArrayList<String[]> m_msa; 
	
	/**
	 * Msa.
	 *
	 * @param seqclust the seqclust
	 * @param d the d
	 * @param was the was
	 */
	public void msa(int[] seqclust, float[][] d, ArrayList<String[]> was){
		ArrayList<Integer> seqclustList = new ArrayList<Integer>();
		for(int i=0; i<seqclust.length; i++){
			seqclustList.add(seqclust[i]);
		}
		this.msa(seqclustList, d, was);
	}
	
	/**
	 * Msa.
	 *
	 * @param seqclust the seqclust
	 * @param d the d
	 * @param was the was
	 */
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
		    	Float[] counts = seqalign.getAlignmentOperations(center, sequencej);
		    	float nmatches = counts[0];
		    	float nmismatches = counts[1];
		    	float ngaps = counts[2];
		    	float nspaces = counts[3];
				String[] align1 = seqalign.getAlignSeqA();
				String[] align2 = seqalign.getAlignSeqB();
				
				// save the aligned sequences
				alingsCenter.add(align1); // all the versions of the aligned center
				alingsNode.add(align2);
				
				// update spaces (consecutive gaps) counter of the center
				// because "once gap, always gap"
				int pos = 0;
				int cnt = 0;
				while(pos<align1.length){
					Object[] objA = this.nextSymbol(align1,pos);
					int gaps = (int)objA[0];
					int nextpos = (int)objA[1];
					if(cnt<spaces.size() && gaps>spaces.get(cnt)){
						spaces.set(cnt, gaps);
					}
					cnt++;
					pos = nextpos;
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
		m_msa = new ArrayList<String[]>();
		m_msa.add(newCenterA); // align1, in index 0
		for(int j=0; j<seqclust.size(); j++){
			if (min!=seqclust.get(j)){
				// Perform the alignment
				SequenceAlignmentBacktrack seqalign = new SequenceAlignmentGlobalSeqAFix();
				String[] sequencej = was.get(seqclust.get(j));
				Float[] counts = seqalign.getAlignmentOperations(newCenterA, sequencej);
		    	float nmatches = counts[0];
		    	float nmismatches = counts[1];
		    	float ngaps = counts[2];
		    	float nspaces = counts[3];
		    	String[] align1 = seqalign.getAlignSeqA();
				String[] align2 = seqalign.getAlignSeqB();
				
				// save the new alignment version
				m_msa.add(align2);
			}
		}
	}
	
	/**
	 * Next symbol.
	 *
	 * @param symbols the symbols
	 * @param actualpos the actualpos
	 * @return the object[]
	 */
	private Object[] nextSymbol(String[] symbols, int actualpos){
		int gaps = 0;
		String symb = symbols[actualpos];
		while(symb.equals(m_gap)){
			gaps++;
			// next element
			actualpos++;
			if(actualpos<symbols.length){
				symb = symbols[actualpos];
			} else {
				break;
			}
		}
		if(gaps==0){ actualpos++; }
		Object[] objA = new Object[2];
		objA[0] = gaps; // number of consecutive gaps
		objA[1] = actualpos; // position of the next no-gap symbol
		return objA;
	}
	
	/**
	 * Gets the multiple sequence alignment.
	 *
	 * @return the multiple sequence alignment
	 */
	public String[][] getMultipleSequenceAlignment(){
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
	
	/**
	 * Write aligned sequences.
	 */
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
	 * The main method.
	 *
	 * @param args the arguments
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
		ArrayList<Long> namesL = new ArrayList<Long>();
		wasL.add(seq1); namesL.add(0l);
		wasL.add(seq2); namesL.add(1l);
		wasL.add(seq3); namesL.add(2l);
		wasL.add(seq4); namesL.add(3l);
		wasL.add(seq5); namesL.add(4l);
		wasL.add(seq6); namesL.add(5l);
		
		// Which are in the same cluster?
		ArrayList<Integer> clusters = new ArrayList<Integer>();
		clusters.add(1);
		clusters.add(2);
		clusters.add(4);
		clusters.add(5);
		
		// role weights
		float[][] roleW1 = {{ 1f,-1f,-1f},
		          		  {-1f, 1f,-1f},
		          		  {-1f,-1f, 1f}};
		
		// Create the distance matrix of the sequences
		ehupatras.webrecommendation.distmatrix.Matrix dm = 
				new ehupatras.webrecommendation.distmatrix.SimilarityMatrixEuclidean(namesL);
		dm.computeMatrix(wasL, roleW1, false);
		float[][] dmatrix = dm.getMatrix(false);

		// Perform the multiple sequence alignment
		MultipleSequenceAlignment malign = new MultipleSequenceAlignment();
		malign.msa(clusters, dmatrix, wasL);
		malign.writeAlignedSequences();
		String[][] msaM = malign.getMultipleSequenceAlignment();
	}

}
