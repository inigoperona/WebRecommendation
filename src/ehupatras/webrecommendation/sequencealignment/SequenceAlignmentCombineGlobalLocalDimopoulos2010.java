package ehupatras.webrecommendation.sequencealignment;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceAlignmentCombineGlobalLocalDimopoulos2010.
 */
public class SequenceAlignmentCombineGlobalLocalDimopoulos2010 
					implements SequenceAlignment{
	
    // weights of roles
    /** The m_role w. */
    protected float[][] m_roleW = {{ 1f, 1f, 1f},  // Unimportant
    					 		   { 1f, 1f, 1f},  // Hub
    					 		   { 1f, 1f, 1f}}; // Content
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignment#setRoleWeights(float[][])
	 */
	public void setRoleWeights(float[][] roleweights){
		m_roleW = roleweights;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.sequencealignment.SequenceAlignment#getScore(java.lang.String[], java.lang.String[])
	 */
	public float getScore(String[] seqA, String[] seqB){
		SequenceAlignmentBacktrack nw = new SequenceAlignmentGlobalDimopoulos2010();
		nw.setRoleWeights(m_roleW);
		float scoreNW = nw.getScore(seqA, seqB);
		SequenceAlignmentBacktrack sw = new SequenceAlignmentLocalDimopoulos2010();
		sw.setRoleWeights(m_roleW);
		float scoreSW = sw.getScore(seqA, seqB);
		float p;
		if(seqA.length > seqB.length){
			p = (float)seqB.length/(float)seqA.length;
		} else {
			p = (float)seqA.length/(float)seqB.length;
		}
		float score = ((float)1-p)*scoreSW + p*scoreNW;
		return score;
	}
	
    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String [] args) {
    	String[] seqA = { "1H", "2C", "3U", "4H", "1C" };
    	String[] seqB = { "1H", "3U", "4H", "1C" };

        SequenceAlignment sa = new SequenceAlignmentCombineGlobalLocalDimopoulos2010();
        System.out.println(sa.getScore(seqA, seqB));
    }
}
