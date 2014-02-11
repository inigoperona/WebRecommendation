package ehupatras.webrecommendation.structures;

public interface Page {
	public boolean getIsValid();
	public boolean getIsSuitableToLinkPrediction();
	public String getFormatedUrlName();
	public void setUrlIDusage(int urlIDusage);
	public int getUrlIDusage();
	public void setIsFrequent(boolean isFrequent);
	public void setIsStatic(boolean isStatic);
	public boolean getIsIndex();
	public String toStringLongHeader();
	public String toStringLong();
}
