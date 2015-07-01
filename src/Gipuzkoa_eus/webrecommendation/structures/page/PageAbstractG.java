package Gipuzkoa_eus.webrecommendation.structures.page;

import java.io.Serializable;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class PageAbstract.
 */
public abstract class PageAbstractG
				implements Page, Serializable {

	// To implement serializable needs this
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	// URL's information
	/** The m_urlname. */
	protected String m_urlname = "-";
	
	/** The m_formatedurlname. */
	protected String m_formatedurlname = "-";
		// the code we assign to it
	/** The m_url i dusage. */
		protected int m_urlIDusage = -1;
	
	/** The m_url i dcontent. */
	protected int m_urlIDcontent = -1;
		// in which language is the requested page: es (spanish), en (english), eu (basque, euskera), fr (french)
	/** The m_language. */
		protected String m_language = "-"; 
		// for example, the URL appears every 10 days at least 10 times
	/** The m_num period. */
		private int m_numPeriod = 0;
	
	/** The m_is static. */
	protected boolean m_isStatic = false;
		// for example, the URL appears in the log files at least 10 times
	/** The m_frequency. */
		private int m_frequency = 0;
	
	/** The m_is frequent. */
	protected boolean m_isFrequent = false;
		// some URL's nature labeled by hand
	/** The m_label by hand. */
		protected String m_labelByHand = "-";
		// if it is a pure html or text based webpage (no pdf for example)
	/** The m_ishtml. */
		protected boolean m_ishtml = false;
		// if it is a avalid URL
	/** The m_isvalid. */
		protected boolean m_isvalid = true;
		// if it is valid for link prediction
	/** The m_isvalid lp. */
		protected boolean m_isvalidLP = true;
	
	// Semantic information of the data //
		// pagerank
	/** The m_pagerankvalue. */
	private int m_pagerankvalue = 0;
		// topic distribution
	/** The m_topic dist. */
		private float[] m_topicDist = null;
	
		// CONTENT DATA
	/** The m_word list. */
		private ArrayList<String> m_wordList;
	
	
	
	// CREATOR
	/**
	 * Instantiates a new page abstract.
	 *
	 * @param urlname the urlname
	 */
	public PageAbstractG(String urlname){
		m_urlname = urlname;
		isValid();
		ishtml();
		cleanURL();
	}
	
	
	
	// abstract methods
	
	/**
	 * Checks if is valid.
	 */
	protected abstract void isValid();	
	
	/**
	 * Ishtml.
	 */
	protected abstract void ishtml();
	
	/**
	 * Clean url.
	 */
	protected abstract void cleanURL();
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getIsSuitableToLinkPrediction()
	 */
	public abstract boolean getIsSuitableToLinkPrediction();
	
	// Getting/Setting the attributes
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getUrlName()
	 */
	public String getUrlName(){
		return m_urlname;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getIsValid()
	 */
	public boolean getIsValid(){
		return m_isvalid;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getFormatedUrlName()
	 */
	public String getFormatedUrlName(){
		return m_formatedurlname;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#setUrlIDusage(int)
	 */
	public void setUrlIDusage(int urlIDusage){
		m_urlIDusage = urlIDusage;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getUrlIDusage()
	 */
	public int getUrlIDusage(){
		return m_urlIDusage;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getIsIndex()
	 */
	public boolean getIsIndex(){
		return m_labelByHand.equals("index");
	}
	
	
	// frequency of the page in the Website
	
	/**
	 * Sets the checks if is frequent.
	 *
	 * @param isFrequent the new checks if is frequent
	 */
	public void setIsFrequent(boolean isFrequent){
		m_isFrequent = isFrequent;
	}
		
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#incrementFrequency()
	 */
	public void incrementFrequency(){
		m_frequency++;
	}
		
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getFrequency()
	 */
	public int getFrequency(){
		return m_frequency;
	}
		
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#setIsFrequent(int)
	 */
	public void setIsFrequent(int minimumFrequency){
		if(m_frequency>=minimumFrequency){
			m_isFrequent = true;
		}
	}
		
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getIsFrequent()
	 */
	public boolean getIsFrequent(){
		return m_isFrequent;
	}
	
	
	
	// Number of periods / staticness of the page in the website
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#setIsStatic(boolean)
	 */
	public void setIsStatic(boolean isStatic){
		m_isStatic = isStatic;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#incrementNumPeriod()
	 */
	public void incrementNumPeriod(){
		m_numPeriod++;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getNumPeriod()
	 */
	public int getNumPeriod(){
		return m_numPeriod;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#setIsStatic(int)
	 */
	public void setIsStatic(int minimumNumPeriod){
		if(m_numPeriod>=minimumNumPeriod){
			m_isStatic = true;
		}
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getIsStatic()
	 */
	public boolean getIsStatic(){
		return m_isStatic;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#setNumPeriod(int)
	 */
	public void setNumPeriod(int numPeriod){
		m_numPeriod = numPeriod;
	}
	
	
	
	// PageRank of the page
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#setPageRank(int)
	 */
	public void setPageRank(int pagerank){
		m_pagerankvalue = pagerank;
	}
	
	
	
	// Topic distribution
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#setTopicDistribution(float[])
	 */
	public void setTopicDistribution(float[] topicDistribution){
		m_topicDist = topicDistribution;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#getTopicDistribution()
	 */
	public float[] getTopicDistribution(){
		return m_topicDist;
	}
	
	
	
	// Writing the Page information
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#toStringLongHeader()
	 */
	public String toStringLongHeader(){
		return 	"urlname " +
				"formatedurlname " +
				"language " +
				"nStaticPeriods " +
				"isStatic " +
				"nFrequency " +
				"isFrequent " +
				"labelByHand " +
				"ishtml " +
				"urlIDusage " +
				"urlIDcontent";
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.page.Page#toStringLong()
	 */
	public String toStringLong(){
		return 	m_urlname + " " +
				m_formatedurlname + " " +
				m_language  + " " +
				m_numPeriod + " " +
				m_isStatic + " " +
				m_frequency + " " +
				m_isFrequent + " " +
				m_labelByHand + " " +
				m_ishtml + " " +
				m_urlIDusage + " " +
				m_urlIDcontent;
	}
	
}
