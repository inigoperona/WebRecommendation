package ehupatras.webrecommendation.structures;

public interface Request {
	public int getUserID();
	public int getSessionID();
	public void setSessionID(int sessionID);
	public String getFormatedUrlName();
	public int getUrlIDusage();
	public void setUrlIDusage(int urlIDusage);
	public long getTimeInMillis();
	public float getElapsedTime();
	public void setElapsedTime(float elapsedtime);
	public void setIsFrequent(boolean isFrequent);
	public void setIsStatic(boolean isStatic);
	public boolean getIsSuitableToLinkPrediction();
	public String toStringLongHeader();
	public String toStringLong();
}
