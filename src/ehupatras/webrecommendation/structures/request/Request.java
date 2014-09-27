package ehupatras.webrecommendation.structures.request;

public interface Request {
	public int getUserID();
	public int getSessionID();
	public void setSessionID(int sessionID);
	public long getTimeInMillis();
	public float getElapsedTime();
	public void setElapsedTime(float elapsedtime);
	public boolean getIsSuitableToLinkPrediction();
	public boolean getIsValid();
	public String toStringLongHeader();
	public String toStringLong();
	public String getPageRoleUHC();
	public void setPageRoleUHC(String rolestr);
	
	// page related functions
	public abstract String getFormatedUrlName();
}
