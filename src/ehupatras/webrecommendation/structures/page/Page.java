package ehupatras.webrecommendation.structures.page;

// TODO: Auto-generated Javadoc
/**
 * The Interface Page.
 */
public interface Page {
	
	/**
	 * Gets the checks if is valid.
	 *
	 * @return the checks if is valid
	 */
	public boolean getIsValid();
	
	/**
	 * Gets the checks if is suitable to link prediction.
	 *
	 * @return the checks if is suitable to link prediction
	 */
	public boolean getIsSuitableToLinkPrediction();
	
	/**
	 * Gets the formated url name.
	 *
	 * @return the formated url name
	 */
	public String getFormatedUrlName();
	
	/**
	 * Gets the url i dusage.
	 *
	 * @return the url i dusage
	 */
	public int getUrlIDusage();
	
	/**
	 * Sets the url i dusage.
	 *
	 * @param urlIDusage the new url i dusage
	 */
	public void setUrlIDusage(int urlIDusage);
	
	/**
	 * Increment frequency.
	 */
	public void incrementFrequency();
	public void incrementFrequency(int val);
	
	/**
	 * Gets the frequency.
	 *
	 * @return the frequency
	 */
	public int getFrequency();
	
	/**
	 * Gets the checks if is frequent.
	 *
	 * @return the checks if is frequent
	 */
	public boolean getIsFrequent();
	
	/**
	 * Sets the checks if is frequent.
	 *
	 * @param minimumFrequency the new checks if is frequent
	 */
	public void setIsFrequent(int minimumFrequency);
	
	/**
	 * Increment num period.
	 */
	public void incrementNumPeriod();
	
	/**
	 * Sets the num period.
	 *
	 * @param numPeriod the new num period
	 */
	public void setNumPeriod(int numPeriod);
	
	/**
	 * Gets the num period.
	 *
	 * @return the num period
	 */
	public int getNumPeriod();
	
	/**
	 * Gets the checks if is static.
	 *
	 * @return the checks if is static
	 */
	public boolean getIsStatic();
	
	/**
	 * Sets the checks if is static.
	 *
	 * @param isStatic the new checks if is static
	 */
	public void setIsStatic(boolean isStatic);
	
	/**
	 * Sets the checks if is static.
	 *
	 * @param minimumNumPeriod the new checks if is static
	 */
	public void setIsStatic(int minimumNumPeriod);
	
	/**
	 * Gets the checks if is index.
	 *
	 * @return the checks if is index
	 */
	public boolean getIsIndex();
	
	/**
	 * Gets the url name.
	 *
	 * @return the url name
	 */
	public String getUrlName();
	
	/**
	 * Sets the page rank.
	 *
	 * @param pagerank the new page rank
	 */
	public void setPageRank(int pagerank);
	
	/**
	 * To string long header.
	 *
	 * @return the string
	 */
	public String toStringLongHeader();
	
	/**
	 * To string long.
	 *
	 * @return the string
	 */
	public String toStringLong();
	
	/**
	 * Sets the topic distribution.
	 *
	 * @param topicDistribution the new topic distribution
	 */
	public void setTopicDistribution(float[] topicDistribution);
	
	/**
	 * Gets the topic distribution.
	 *
	 * @return the topic distribution
	 */
	public float[] getTopicDistribution();
}
