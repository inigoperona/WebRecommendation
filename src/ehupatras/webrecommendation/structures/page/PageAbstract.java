package ehupatras.webrecommendation.structures.page;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class PageAbstract
				implements Page, Serializable {

	// To implement serializable needs this
	private static final long serialVersionUID = 1L;
	
	// URL's information
	protected String m_urlname = "-";
	protected String m_formatedurlname = "-";
		// the code we assign to it
	protected int m_urlIDusage = -1;
	protected int m_urlIDcontent = -1;
		// in which language is the requested page: es (spanish), en (english), eu (basque, euskera), fr (french)
	protected String m_language = "-"; 
		// for example, the URL appears every 10 days at least 10 times
	private int m_numPeriod = 0;
	protected boolean m_isStatic = false;
		// for example, the URL appears in the log files at least 10 times
	private int m_frequency = 0;
	protected boolean m_isFrequent = false;
		// some URL's nature labeled by hand
	protected String m_labelByHand = "-";
		// if it is a pure html or text based webpage (no pdf for example)
	protected boolean m_ishtml = false;
		// if it is a avalid URL
	protected boolean m_isvalid = true;
		// if it is valid for link prediction
	protected boolean m_isvalidLP = true;
	
	// Semantic information of the data //
		// pagerank
	private int m_pagerankvalue = 0;
		// topic distribution
	private float[] m_topicDist = null;
	
		// CONTENT DATA
	private ArrayList<String> m_wordList;
	
	
	
	// CREATOR
	public PageAbstract(String urlname){
		m_urlname = urlname;
		isValid();
		ishtml();
		cleanURL();
	}
	
	
	
	// abstract methods
	
	protected abstract void isValid();	
	protected abstract void ishtml();
	protected abstract void cleanURL();
	public abstract boolean getIsSuitableToLinkPrediction();
	
	// Getting/Setting the attributes
	
	public String getUrlName(){
		return m_urlname;
	}
	
	public boolean getIsValid(){
		return m_isvalid;
	}
	
	public String getFormatedUrlName(){
		return m_formatedurlname;
	}
	
	public void setUrlIDusage(int urlIDusage){
		m_urlIDusage = urlIDusage;
	}
	
	public int getUrlIDusage(){
		return m_urlIDusage;
	}
	
	public boolean getIsIndex(){
		return m_labelByHand.equals("index");
	}
	
	
	// frequency of the page in the Website
	
	public void setIsFrequent(boolean isFrequent){
		m_isFrequent = isFrequent;
	}
		
	public void incrementFrequency(){
		m_frequency++;
	}
		
	public int getFrequency(){
		return m_frequency;
	}
		
	public void setIsFrequent(int minimumFrequency){
		if(m_frequency>=minimumFrequency){
			m_isFrequent = true;
		}
	}
		
	public boolean getIsFrequent(){
		return m_isFrequent;
	}
	
	
	
	// Number of periods / staticness of the page in the website
	
	public void setIsStatic(boolean isStatic){
		m_isStatic = isStatic;
	}
	
	public void incrementNumPeriod(){
		m_numPeriod++;
	}
	
	public int getNumPeriod(){
		return m_numPeriod;
	}
	
	public void setIsStatic(int minimumNumPeriod){
		if(m_numPeriod>=minimumNumPeriod){
			m_isStatic = true;
		}
	}
	
	public boolean getIsStatic(){
		return m_isStatic;
	}
	
	public void setNumPeriod(int numPeriod){
		m_numPeriod = numPeriod;
	}
	
	
	
	// PageRank of the page
	
	public void setPageRank(int pagerank){
		m_pagerankvalue = pagerank;
	}
	
	
	
	// Topic distribution
	
	public void setTopicDistribution(float[] topicDistribution){
		m_topicDist = topicDistribution;
	}
	
	public float[] getTopicDistribution(){
		return m_topicDist;
	}
	
	
	
	// Writing the Page information
	
	public String toStringLongHeader(){
		return 	"urlname " +
				"formatedurlname " +
				"language " +
				"isStatic " +
				"isFrequent " +
				"labelByHand " +
				"ishtml " +
				"urlIDusage " +
				"urlIDcontent";
	}
	
	public String toStringLong(){
		return 	m_urlname + " " +
				m_formatedurlname + " " +
				m_language  + " " +
				m_isStatic + " " +
				m_isFrequent + " " +
				m_labelByHand + " " +
				m_ishtml + " " +
				m_urlIDusage + " " +
				m_urlIDcontent;
	}
	
}
