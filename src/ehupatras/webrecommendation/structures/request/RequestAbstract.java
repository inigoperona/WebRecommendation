package ehupatras.webrecommendation.structures.request;

import java.io.Serializable;
import java.util.Calendar;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.structures.page.Page;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestAbstract.
 */
public abstract class RequestAbstract
						implements Request, Serializable {

	// The Serializable classes needs it 
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	
	// log file information
	// Detect IP address 
	/** The m_ip. */
	private String m_ip = "-";
	
	/** The m_user id. */
	private int m_userID = -1;
	// other log information
	/** The m_time. */
	protected Calendar m_time = null;
	
	/** The m_method. */
	protected String m_method = "-";
	
	/** The m_formated ur lname. */
	private String m_formatedURLname = "-";	
	
	/** The m_protocol. */
	private String m_protocol = "-";
	
	/** The m_status. */
	protected int m_status = -1;
	
	/** The m_reqsize. */
	private String m_reqsize = "-";
	
	/** The m_reference. */
	protected String m_reference = "-";
	
	/** The m_reference label. */
	protected String m_referenceLabel = "_";
	
	/** The m_useragent. */
	protected String m_useragent = "-";
	
	/** The m_useragent label. */
	protected String m_useragentLabel = "_";

	// if it is valid request or not
	/** The m_isvalid. */
	protected boolean m_isvalid = true;
	// if it is valid for link prediction or not
	/** The m_isvalid lp. */
	private boolean m_isvalidLP = true;

	// this information is related to a sessioning phase
	/** The m_session id. */
	private String m_sessionID = "-";

	// time in each URL and its related attributes
	/** The m_elapsedtime. */
	private float m_elapsedtime = -1;  // in milliseconds.
	// U: Unimportant (<=10sec. & >=25-30min); H: Hub (5-6min.); C: Content (25-30min.);
	/** The m_page role uhc. */
	private String m_pageRoleUHC = "-"; 

	// information extracted from user agent
	/** The m_isbot. */
	protected boolean m_isbot = false;
	
	protected int m_logFileNumber;
	
	protected boolean m_isTheEndOfTheSession = false;
	
	// constructor
	/**
	 * Instantiates a new request abstract.
	 *
	 * @param ip the ip
	 * @param ipID the ip id
	 * @param time the time
	 * @param method the method
	 * @param page the page
	 * @param protocol the protocol
	 * @param status the status
	 * @param reqsize the reqsize
	 * @param reference the reference
	 * @param useragent the useragent
	 */
	public RequestAbstract(String ip, int ipID, String time, 
			String method, Page page, String protocol,
			int status, String reqsize, String reference, String useragent,
			int logfilenumber){
		
		// IP address
		m_ip = ip;
		m_userID = ipID;
		
		// time: convert the time to Calendar object and save
		m_time = this.getTime(time);
		
		// URL requests fields
		m_method = method;
		m_formatedURLname = page.getFormatedUrlName();
		m_protocol = protocol;
		
		// status of the request
		m_status = status;
		
		// request size in bytes
		m_reqsize = reqsize;
		
		// reference: from where the request comes
		m_reference = reference;
		this.analyzeReference();
		
		// user agent field
		m_useragent = useragent;
		this.analyzeUserAgent();
		
		// compute if it is a valid request
		this.isvalid();
		// check the validity of the URL also
		if(!page.getIsValid()){	
			m_isvalid = false;
		}
		
		// detect if the request it is generated by robots
		this.isbot();
		
		// store log file number
		m_logFileNumber = logfilenumber;
	}
	
	
	
	// ABSTRACT METHODS
	
	/**
	 * Gets the time.
	 *
	 * @param time the time
	 * @return the time
	 */
	protected abstract Calendar getTime(String time);
	
	
	
	// FUNCTIONS
	
	/**
	 * Isvalid.
	 */
	protected void isvalid(){
		// default is true
		m_isvalid = true;
		
		// the request method has to be GET or POST
		String method = m_method.toLowerCase(); 
		if(!method.equals("get") && !method.equals("post")){
			m_isvalid = false;
		}
		
		// the status field has to be of the format 2xx
		if(m_status/100 != 2){
			m_isvalid = false;
		}
	}
	
	/**
	 * Isbot.
	 */
	protected void isbot(){
		// detect if the request is generated by bots, search engines, robots...
		m_isbot = false; // default
		String useragent2 = m_useragent.toLowerCase();
		if(	useragent2.contains("bot") ||
			useragent2.contains("spider") ||
			useragent2.contains("crawler")){
			m_isbot  = true;
			m_useragentLabel = m_useragentLabel + "_bot";
		}
	}
	
	/**
	 * Analyze user agent.
	 */
	protected void analyzeUserAgent(){
		String useragent2 = m_useragent.toLowerCase();		
		// bots
		if(	useragent2.contains("googlebot") ){ m_useragentLabel = m_useragentLabel + "_googlebot"; }
		if(	useragent2.contains("bingbot") ){ m_useragentLabel = m_useragentLabel + "_bingbot"; }
		if(	useragent2.contains("yahoo") ){ m_useragentLabel = m_useragentLabel + "_yahoobot"; }
		// browsers
		if(	useragent2.contains("chrome") ){ m_useragentLabel = m_useragentLabel + "_bro_chrome"; }
		if(	useragent2.contains("iexplorer") ){ m_useragentLabel = m_useragentLabel + "_bro_iexplorer"; }
		if(	useragent2.contains("firefox") ){ m_useragentLabel = m_useragentLabel + "_bro_firefox"; }
		if(	useragent2.contains("opera") ){ m_useragentLabel = m_useragentLabel + "_bro_opera"; }
		if(	useragent2.contains("safari") ){ m_useragentLabel = m_useragentLabel + "_bro_safari"; }
		// operating system
		if(	useragent2.contains("windows") ){ m_useragentLabel = m_useragentLabel + "_os_windows"; }
		if(	useragent2.contains("macintosh") ){ m_useragentLabel = m_useragentLabel + "_os_macintosh"; }
		if(	useragent2.contains("linux") ){ m_useragentLabel = m_useragentLabel + "_os_linux"; }
		if(	useragent2.contains("mobile") ){ m_useragentLabel = m_useragentLabel + "_os_mobile"; }
	}
	
	/**
	 * Analyze reference.
	 */
	protected void analyzeReference(){
		String reference2 = m_reference.toLowerCase();
		// sites related with discapnet
		if(	reference2.contains("discapnet") ){ m_referenceLabel = m_referenceLabel + "_disc"; }
		if(	reference2.contains("www.discapnet.es") ){ m_referenceLabel = m_referenceLabel + "_disc"; }
		if(	reference2.contains("discapnet.es") ){ m_referenceLabel = m_referenceLabel + "_disc_subdomain"; }
		if(	reference2.contains("technosite") ){ m_referenceLabel = m_referenceLabel + "_disc_technosite"; }
		if(	reference2.contains("once") ){ m_referenceLabel = m_referenceLabel + "_disc_once"; }
		// search engine 
		if(	reference2.contains("google") ||  reference2.contains("goo.gl") ){ m_referenceLabel = m_referenceLabel + "_se_google"; }
		if(	reference2.contains("yahoo") ){ m_referenceLabel = m_referenceLabel + "_se_yahoo"; }
		if(	reference2.contains("ask") ){ m_referenceLabel = m_referenceLabel + "_se_ask"; }
		if(	reference2.contains("iconoce") ){ m_referenceLabel = m_referenceLabel + "_se_iconoce"; }
		if(	reference2.contains("bing") ){ m_referenceLabel = m_referenceLabel + "_se_bing"; }
		// social networks
		if(	reference2.contains("facebook") ){ m_referenceLabel = m_referenceLabel + "_sn_facebook"; }
		if(	reference2.contains("t.co") || reference2.contains("twitter.com")){ m_referenceLabel = m_referenceLabel + "_sn_twitter"; }
		if(	reference2.contains("blogspot") ){ m_referenceLabel = m_referenceLabel + "_sn_blogspot"; }
		if(	reference2.contains("wordpress") ){ m_referenceLabel = m_referenceLabel + "_sn_wordpress"; }
		if(	reference2.contains("meneame") ){ m_referenceLabel = m_referenceLabel + "_sn_meneame"; }
		// other
		if(	reference2.contains("imente") ){ m_referenceLabel = m_referenceLabel + "_oth_imente"; }
		if(	reference2.contains("bit.ly") ){ m_referenceLabel = m_referenceLabel + "_oth_bit.ly"; }
		if(	reference2.contains("babylon") ){ m_referenceLabel = m_referenceLabel + "_oth_babylon"; }
		if(	reference2.contains("portalento") ){ m_referenceLabel = m_referenceLabel + "_oth_portalento"; }
		if(	reference2.contains("wikipedia") ){ m_referenceLabel = m_referenceLabel + "_oth_wikipedia"; }
		if(	reference2.contains("173.194") ){ m_referenceLabel = m_referenceLabel + "_oth_ehu"; }
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#getIsSuitableToLinkPrediction()
	 */
	public boolean getIsSuitableToLinkPrediction(){
		// !m_labelByHand.equals("index") we want index URLs in the sequences
		// because its alignment give us information.
		Page page = Website.getPage(m_formatedURLname);
		m_isvalidLP = m_isvalid &&
					  !m_isbot &&
					  m_elapsedtime>=0 &&
					  page.getIsSuitableToLinkPrediction();
		return m_isvalidLP;
	}
	
	
	
	// Getting/Setting the information of the request
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#getIsValid()
	 */
	public boolean getIsValid(){
		return m_isvalid;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#getTimeInMillis()
	 */
	public long getTimeInMillis(){
		return m_time.getTimeInMillis();
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#getUserID()
	 */
	public int getUserID(){
		return m_userID;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#setSessionID(int)
	 */
	public void setSessionID(String sessionID){
		m_sessionID = sessionID;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#setElapsedTime(float)
	 */
	public void setElapsedTime(float elapsedtime){
		m_elapsedtime = elapsedtime;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#getElapsedTime()
	 */
	public float getElapsedTime(){
		return m_elapsedtime;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#getSessionID()
	 */
	public String getSessionID(){
		return m_sessionID;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#getPageRoleUHC()
	 */
	public String getPageRoleUHC(){
		return m_pageRoleUHC;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#setPageRoleUHC(java.lang.String)
	 */
	public void setPageRoleUHC(String rolestr){
		m_pageRoleUHC = rolestr;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#getFormatedUrlName()
	 */
	public String getFormatedUrlName(){
		return m_formatedURLname;
	}
	
	public int getLogFileNumber(){
		return m_logFileNumber;
	}
	
	public void setIsTheEndOfTheSession(boolean isTheEndOfTheSession){
		m_isTheEndOfTheSession = isTheEndOfTheSession;
	}
	
	public boolean getIsTheEndOfTheSession(){
		return m_isTheEndOfTheSession;
	}
	
	// writing the requests information
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#toStringLongHeader()
	 */
	public String toStringLongHeader(){
		Page page = Website.getPage(m_formatedURLname);
		return 	"ip " +
				"userID " +
				"time " +
				"method " +
				"protocol " +
				"status " +
				"reqsize " +
				"reference " +
				"referenceLabel " +
				"useragent " +
				"useragentLabel " +
				"isvalid " +
				"sesssionID " +
				"elapsedtime " +
				"pageRoleUHC " +
				"isbot " +
				"isvalidLinkPrediction " +
				"isTheEndOfTheSession" +
				"logfilenumber " +
				page.toStringLongHeader();
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.Request#toStringLong()
	 */
	public String toStringLong(){
		Page page = Website.getPage(m_formatedURLname);
		return 	m_ip + " " +
				m_userID + " " +
				m_time.getTimeInMillis() + " " +
				m_method + " " +
				m_protocol + " " +
				m_status + " " +
				m_reqsize + " " +
				m_reference.replaceAll(" ", "") + " " +
				m_referenceLabel + " " +
				m_useragent.replaceAll(" ", "") + " " +
				m_useragentLabel + " " +
				m_isvalid + " " +
				m_sessionID + " " +
				m_elapsedtime + " " +
				m_pageRoleUHC  + " " +
				m_isbot + " " +
				m_isvalidLP + " " +
				m_isTheEndOfTheSession + " " +
				m_logFileNumber + " " +
				page.toStringLong();
	}
	
}
