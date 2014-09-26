package ehupatras.webrecommendation.structures.page;

public class PageBidasoaTurismo
				extends PageAbstract {
	
	// To implement serializable needs this
	private static final long serialVersionUID = 1L;
	
	
	public PageBidasoaTurismo(String urlname){
		super(urlname);
	}
	
	protected void isValid(){
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
	
	protected void ishtml(){
		// default
		m_ishtml = false;
		
		// Detect those URLs that have a text format (no PDFs)
		String urlname2 = m_urlname.toLowerCase();
		if(	urlname2.equals("/") ||
			urlname2.contains(".php")){
			m_ishtml = true;
		}
	}
	
	protected void cleanURL(){
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

	
	public String getUrlName(){
		String urlname2 = m_urlname.replace("lang=es", "lang=en");
		String urlname3 = urlname2.replace("lang=eu", "lang=en");
		String urlname4 = urlname3.replace("lang=fr", "lang=en");
		String urlname5 = "bidasoaturismo.com" + urlname4; 
		return urlname5;
	}
	
}
