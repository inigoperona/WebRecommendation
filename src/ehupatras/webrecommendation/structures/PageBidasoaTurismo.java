package ehupatras.webrecommendation.structures;

public class PageBidasoaTurismo implements Page {
	
	// URL's information
	private String m_urlname = "-";
	private String m_formatedurlname = "-";
		// the code we assign to it
	private int m_urlIDusage = -1;
	private int m_urlIDcontent = -1;
		// in which language is the requested page: es (spanish), en (english), eu (basque, euskera), fr (french)
	private String m_language = "-"; 
		// for example, the URL appears every 10 days at least 10 times
	private boolean m_isStatic = false;
		// for example, the URL appears in the log files at least 10 times
	private boolean m_isFrequent = false;
		// some URL's nature labeled by hand
	private String m_labelByHand = "-";
		// if it is a pure html or text based webpage (no pdf for example)
	private boolean m_ishtml = false;
		// if it is a avalid URL
	private boolean m_isvalid = true;
	
		// Semantic information of the data
	private int m_pagerankvalue = 0;
	
	public PageBidasoaTurismo(String urlname){
		m_urlname = urlname;
		isValid();
		ishtml();
		cleanURL();
	}
	
	private void isValid(){
		// default is true
		m_isvalid = true;
		
		// Select the URLs related with the user click.
		// Those URLs with the extension: root (/), php, pdf, asp
		String urlname2 = m_urlname.toLowerCase();
		if(	!urlname2.equals("/") &&
			!urlname2.contains(".php") && 
			!urlname2.contains(".pdf")){
			m_isvalid = false;
		}
		
		// administration related clicks are not valid
		if(	urlname2.contains("/administrator/") ||
			urlname2.contains("/wp-admin/") ||
			urlname2.contains("/wp-content/") ||
			urlname2.contains("/xmlrpc.php") ||
			urlname2.contains("/wp-login.php") ||
			urlname2.contains("/wp-includes/") ||
			urlname2.contains("/wp-cron.php") ||
			urlname2.contains("/phpmyadmin/") ||
			urlname2.contains("option=com_ccboard")){
			m_isvalid = false;
		}
	}
	
	public boolean getIsValid(){
		return m_isvalid;
	}
	
	private void ishtml(){
		// default
		m_ishtml = false;
		
		// Detect those URLs that have a text format (no PDFs)
		String urlname2 = m_urlname.toLowerCase();
		if(	urlname2.equals("/") ||
			urlname2.contains(".php")){
			m_ishtml = true;
		}
	}
	
	private void cleanURL(){
		String urlname2 = m_urlname.toLowerCase();
		
		// language analysis
		m_language = "-"; // default
		if(urlname2.contains("lang=es")){
			m_language = "es";
		}
		if(urlname2.contains("lang=en")){
			m_language = "en";
		}
		if(urlname2.contains("lang=fr")){
			m_language = "fr";
		}
		if(urlname2.contains("lang=eu")){
			m_language = "eu";
		}
		
		// label some URLs by hand
		if(urlname2.contains("option=com_agenda")){
			m_labelByHand = "agenda";
		}
		if(urlname2.contains("noticias")){
			m_labelByHand = "noticias";
		}
		if(urlname2.contains("searchword=")){
			m_labelByHand = "search";
		}
		if(urlname2.contains("option=com_alojamientos")){
			m_labelByHand = "accomodation";
		}
		
		// Remove the parameters we do not need from the URL
		String[] params = {"option=", "view=", "layout=", "id=", "itemid=", 
				"categoria=", "cat=", "alojamiento=", "forum=", "limitstart="};
		if(urlname2.startsWith("/index.php?")){
			String[] urlname2A = urlname2.split("php");
			String newparams = "";
			if(urlname2A.length>=2){
				//String part0 = urlname2A[0];
				String part1 = urlname2A[1];
				String part11 = part1.substring(1,part1.length());
				String[] paramA = part11.split("&");
				// rewrite the URL following a fix order of parameters
				for(int i=0; i<params.length; i++){
					for(int j=0; j<paramA.length; j++){
						if(paramA[j].startsWith(params[i])){
							newparams = newparams + paramA[j] + "&";
							break;
						}
					}
				}
			}
			if(!newparams.equals("")){
				newparams = newparams.substring(0,newparams.length()-1);
				m_formatedurlname = "/index.php?" + newparams;
			} else {
				m_formatedurlname = "/index.php";
			}
		} else {
			m_formatedurlname = urlname2;
		}
		
		// label the index URL
		if(	m_formatedurlname.equals("/") || 
			m_formatedurlname.equals("/index.php")){
			m_labelByHand = "index";
			m_formatedurlname = "/index.php";
		}
	}
	
	public boolean getIsSuitableToLinkPrediction(){
		// !m_labelByHand.equals("index") we want index URLs in the sequences
		// because its alignment give us information.
		return 	m_isvalid &&
				m_ishtml &&
				m_isStatic &&
				m_isFrequent &&
				(	!m_labelByHand.equals("agenda") && 
					!m_labelByHand.equals("noticias") && 
					!m_labelByHand.equals("search")	);
	}
	
	
	// Getting/Setting the attributes
	
	public String getFormatedUrlName(){
		return m_formatedurlname;
	}
	
	public void setUrlIDusage(int urlIDusage){
		m_urlIDusage = urlIDusage;
	}
	
	public int getUrlIDusage(){
		return m_urlIDusage;
	}
	
	public void setIsFrequent(boolean isFrequent){
		m_isFrequent = isFrequent;
	}
	
	public void setIsStatic(boolean isStatic){
		m_isStatic = isStatic;
	}
	
	public boolean getIsIndex(){
		return m_labelByHand.equals("index");
	}
	
	public String getUrlName(){
		String urlname2 = m_urlname.replace("lang=es", "lang=en");
		String urlname3 = urlname2.replace("lang=eu", "lang=en");
		String urlname4 = urlname3.replace("lang=fr", "lang=en");
		String urlname5 = "bidasoaturismo.com" + urlname4; 
		return urlname5;
	}
	
	public void setPageRank(int pagerank){
		m_pagerankvalue = pagerank;
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
