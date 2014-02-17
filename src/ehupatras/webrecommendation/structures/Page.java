package ehupatras.webrecommendation.structures;

public interface Page {
	public boolean getIsValid();
	public boolean getIsSuitableToLinkPrediction();
	public String getFormatedUrlName();
	public int getUrlIDusage();
	public void setUrlIDusage(int urlIDusage);
	public void incrementFrequency();
	public int getFrequency();
	public boolean getIsFrequent();
	public void setIsFrequent(int minimumFrequency);
	public void incrementNumPeriod();
	public void setNumPeriod(int numPeriod);
	public int getNumPeriod();
	public boolean getIsStatic();
	public void setIsStatic(boolean isStatic);
	public void setIsStatic(int minimumNumPeriod);
	public boolean getIsIndex();
	public String getUrlName();
	public void setPageRank(int pagerank);
	public String toStringLongHeader();
	public String toStringLong();
}
