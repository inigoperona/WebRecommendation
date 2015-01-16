package ehupatras.webrecommendation.sequencealignment;

// TODO: Auto-generated Javadoc
/**
 * The Interface SequenceAlignment.
 */
public interface SequenceAlignment {
	
	/**
	 * Gets the score.
	 *
	 * @param seqA the seq a
	 * @param seqB the seq b
	 * @return the score
	 */
	public float getScore(String[] seqA, String[] seqB);
	
	/**
	 * Sets the role weights.
	 *
	 * @param roleweights the new role weights
	 */
	public void setRoleWeights(float[][] roleweights);
}
