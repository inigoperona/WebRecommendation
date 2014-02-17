package ehupatras.webrecommendation.sequencealignment;

import java.util.ArrayList;

public class SequenceAlignmentGlobalDimopoulos2010 
				extends SequenceAlignmentGlobalNeedlemanWunsch {

    public float getScore(String[] seqA, String[] seqB){
    	// constants
    	float wm = (float)1;
    	float wms = (float)1;
    	float wg = (float)1;
    	float ws = (float)5;
    	
    	// counts
    	Integer[] counts = getAlignmentOperations(seqA, seqB);
    	int nmatches = counts[0];
    	int nmismatches = counts[1];
    	int ngaps = counts[2];
    	int nspaces = counts[3];
    	
    	// compute the score and return
    	float score = wm*(float)nmatches 
    			- wms*(float)nmismatches - wg*(float)ngaps - ws*(float)nspaces;
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
