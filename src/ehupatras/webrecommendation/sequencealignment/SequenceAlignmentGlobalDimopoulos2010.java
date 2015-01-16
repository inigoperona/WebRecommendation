package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentGlobalDimopoulos2010.
 */
public class SequenceAlignmentGlobalDimopoulos2010 
				extends SequenceAlignmentGlobalNeedlemanWunsch {

    /* (non-Javadoc)
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentGlobalNeedlemanWunsch#getScore(java.lang.String[], java.lang.String[])
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
     * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignmentGlobalNeedlemanWunsch#getTrimedAlignedSequences(java.lang.String, java.lang.String)
     */
    protected ArrayList<String[]> getTrimedAlignedSequences(String str1, String str2){
    	String[] seq1 = getStringArrayRepresentation(str1);
    	String[] seq2 = getStringArrayRepresentation(str2);
    	ArrayList<String[]> trimmedseqs = new ArrayList<String[]>();
    	trimmedseqs.add(seq1);
    	trimmedseqs.add(seq2);
    	return trimmedseqs;
    }
	
}
