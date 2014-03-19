package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public class SequenceAlignmentGlobalDimopoulos2010 
				extends SequenceAlignmentGlobalNeedlemanWunsch {

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
    
    protected ArrayList<String[]> getTrimedAlignedSequences(String str1, String str2){
    	String[] seq1 = getStringArrayRepresentation(str1);
    	String[] seq2 = getStringArrayRepresentation(str2);
    	ArrayList<String[]> trimmedseqs = new ArrayList<String[]>();
    	trimmedseqs.add(seq1);
    	trimmedseqs.add(seq2);
    	return trimmedseqs;
    }
	
}
