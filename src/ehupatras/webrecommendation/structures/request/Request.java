package ehupatras.webrecommendation.structures.request;

// TODO: Auto-generated Javadoc
/**
 * The Interface Request.
 */
public interface Request {
	
	/**
	 * Gets the user id.
	 *
	 * @return the user id
	 */
	public int getUserID();
	
	/**
	 * Gets the session id.
	 *
	 * @return the session id
	 */
	public long getSessionID();
	
	/**
	 * Sets the session id.
	 *
	 * @param sessionID the new session id
	 */
	public void setSessionID(long sessionID);
	
	/**
	 * Gets the time in millis.
	 *
	 * @return the time in millis
	 */
	public long getTimeInMillis();
	
	/**
	 * Gets the elapsed time.
	 *
	 * @return the elapsed time
	 */
	public float getElapsedTime();
	
	/**
	 * Sets the elapsed time.
	 *
	 * @param elapsedtime the new elapsed time
	 */
	public void setElapsedTime(float elapsedtime);
	
	/**
	 * Gets the checks if is suitable to link prediction.
	 *
	 * @return the checks if is suitable to link prediction
	 */
	public boolean getIsSuitableToLinkPrediction();
	
	/**
	 * Gets the checks if is valid.
	 *
	 * @return the checks if is valid
	 */
	public boolean getIsValid();
	
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
	 * Gets the page role uhc.
	 *
	 * @return the page role uhc
	 */
	public String getPageRoleUHC();
	
	/**
	 * Sets the page role uhc.
	 *
	 * @param rolestr the new page role uhc
	 */
	public void setPageRoleUHC(String rolestr);
	
	// page related functions
	/**
	 * Gets the formated url name.
	 *
	 * @return the formated url name
	 */
	public abstract String getFormatedUrlName();
	
	public int getLogFileNumber();
	
	public void setIsTheEndOfTheSession(boolean isTheEndOfTheSession);
	
	public boolean getIsTheEndOfTheSession();
}
