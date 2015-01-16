package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;
import java.util.Arrays;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentLocalDimopoulos2010.
 */
public class SequenceAlignmentLocalDimopoulos2010 
				extends SequenceAlignmentLocalSmithWaterman {

    /* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLocalSmithWaterman#getScore(java.lang.String[], java.lang.String[])
     */
    public float getScore(String[] seqA, String[] seqB){
    	// constants
    	float wm  = 1f;
    	float wms = 1f;
    	float wg  = 1f;
    	float ws  = 5f;
    	
    	// counts
    	Float[] counts = getAlignmentOperations(seqA, seqB);
    	float nmatches = counts[0];
    	float nmismatches = counts[1];
    	float ngaps = counts[2];
    	float nspaces = counts[3];
    	
    	// compute the score and return
    	float score = wm*nmatches 
    			- wms*nmismatches - wg*ngaps - ws*nspaces;
    	return score;
    }
	
    /* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLocalSmithWaterman#getTrimedAlignedSequences(java.lang.String, java.lang.String)
     */
    protected ArrayList<String[]> getTrimedAlignedSequences(String str1, String str2){
    	String[] seq1 = getStringArrayRepresentation(str1);
    	String[] seq2 = getStringArrayRepresentation(str2);
    	
    	// remove the start gaps
    	int i1;
    	for(i1=0; i1<seq1.length; i1++){
    		String elem1 = seq1[i1];
    		String elem2 = seq1[i1];
    		if(!elem1.equals(m_gap) && !elem2.equals(m_gap)){
    			break;
    		}
    	}
    	
    	// remove the final gaps
    	int i2;
    	for(i2=seq1.length-1; i2>=0; i2--){
    		String elem1 = seq1[i2];
    		String elem2 = seq1[i2];
    		if(!elem1.equals(m_gap) && !elem2.equals(m_gap)){
    			break;
    		}
    	}
    	
    	// create the new sub sequences
    	String[] seq12 = Arrays.copyOfRange(seq1, i1, i2+1);
    	String[] seq22 = Arrays.copyOfRange(seq2, i1, i2+1);
    	
    	ArrayList<String[]> trimmedseqs = new ArrayList<String[]>();
    	trimmedseqs.add(seq12);
    	trimmedseqs.add(seq22);
    	return trimmedseqs;
    }
    
}
