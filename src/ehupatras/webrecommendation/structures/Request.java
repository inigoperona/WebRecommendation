package ehupatras.webrecommendation.structures;

public abstract class Request {
	public abstract int getUserID();
	public abstract int getSessionID();
	public abstract void setSessionID(int sessionID);
	public abstract long getTimeInMillis();
	public abstract float getElapsedTime();
	public abstract void setElapsedTime(float elapsedtime);
	public abstract boolean getIsSuitableToLinkPrediction();
	public abstract boolean getIsValid();
	public abstract String toStringLongHeader();
	public abstract String toStringLong();
	public abstract String getPageRoleUHC();
	public abstract void setPageRoleUHC(String rolestr);
	
	// page related functions
	public abstract String getFormatedUrlName();
}
