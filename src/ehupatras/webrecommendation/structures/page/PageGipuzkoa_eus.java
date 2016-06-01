package ehupatras.webrecommendation.structures.page;

import java.text.Normalizer;

//TODO: Auto-generated Javadoc
/**
* The Class PageGipuzkoa_eus.
*/
public class PageGipuzkoa_eus
				extends PageAbstract {
	
	// To implement serializable needs this
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	
	// CREATOR
	/**
	 * Instantiates a new page Gipuzkoa_eus.
	 *
	 * @param urlname the urlname
	 */
	public PageGipuzkoa_eus(String urlname){
		super(urlname);
	}
	
	/* (non-Javadoc)
	 * @see Gipuzkoa_eus.webrecommendation.structures.page.PageAbstractG#isValid()
	 */
	protected void isValid(){
		// default is true
		m_isvalid = true;
		
		// Select the URLs related with the user click.
		// Those URLs with the extension: root (/), php, pdf, asp
		String urlname2 = m_urlname.toLowerCase();
		int iquest = urlname2.indexOf('?');
		String urlname3;
		if(iquest!=-1){
			urlname3 = urlname2.substring(0,iquest);
		} else {
			urlname3 = urlname2;
		}
		if(	!urlname2.equals("/") &&
		    !urlname2.equals("www.ehu.eus/es/") &&
		    !urlname2.equals("www.ehu.eus/en/en-home") &&
		    !urlname2.equals("www.ehu.eus/en/") &&
		    !urlname2.equals("www.ehu.eus/eu/") &&
		    !urlname2.equals("www.ehu.eus/es") &&
		    !urlname2.equals("www.ehu.eus/es/home") &&
		    !urlname2.equals("www.ehu.eus/eu/home") &&
		    !urlname2.equals("www.ehu.eus/eu") &&
		    !urlname2.equals("www.ehu.eus/en") &&
		    !urlname2.equals("www.ehu.es/es/") &&
		    !urlname2.equals("www.ehu.es/eu/") &&
		    !urlname2.equals("www.ehu.es/es/home") &&
		    !urlname2.equals("www.ehu.es/eu/home") &&
		    !urlname2.equals("www.ehu.es/en/") &&
		    !urlname2.equals("www.ehu.es/en/en-home") &&
			!urlname3.contains(".aspx") &&
			!urlname3.contains(".pdf") && 
			!urlname3.contains(".htm") &&
			!urlname3.contains(".html") &&
			!urlname3.contains(".doc") &&
			!urlname3.contains(".xml") &&
			!urlname3.contains(".do") ||
			
			urlname3.contains(".gif") ||
			urlname2.contains("/correow/") ||
		    urlname2.contains("/wposta/") ||
		    urlname2.contains("/piwik/")){
			m_isvalid = false;
		}
		
		int len = urlname3.length();
		String ext = urlname3.substring(len-5);
		if(!ext.contains(".")){
			if(urlname2.contains("/web/") ||
			   urlname2.contains("/ehusfera/") ||
		       urlname2.contains("/login/") ||
		       urlname2.contains("/bilatu/")){
				m_isvalid = true;
			}
		}
		
		// administration related clicks are not valid
		if(	urlname2.contains("admin") ){
			m_isvalid = false;
		}
		
		// error pages
		if(urlname2.contains("error")){
			m_isvalid = false;
		}
		
		// rss pages
		if(urlname2.contains("rss")){
			m_isvalid = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see Gipuzkoa_eus.webrecommendation.structures.page.PageAbstractG#ishtml()
	 */
	protected void ishtml(){
		m_ishtml = m_isvalid;
		/*
		// default
		m_ishtml = false;
		
		// Detect those URLs that have a text format (no PDFs)
		String urlname2 = m_urlname.toLowerCase();
		if(	urlname2.equals("/") ||
			urlname2.contains(".aspx") ||
			urlname2.contains(".htm") ||
			urlname2.contains(".html") ||
			urlname2.contains(".pdf") || 
			urlname2.contains(".doc") ||
			urlname2.contains(".xml") ||
			urlname2.contains(".do")
			){
			m_ishtml = true;
		}
		*/
	}
	
	/* (non-Javadoc)
	 * @see Gipuzkoa_eus.webrecommendation.structures.page.PageAbstractG#cleanURL()
	 */
	protected void cleanURL(){
		String urlname2 = m_urlname.toLowerCase();
		m_language = "es"; // default
		
		// label some URLs by hand //
		while(true){
			
			/// index slash
			///if(urlname2.equals("/")){
			///	m_labelByHand = "index_slash"; break;
			///}
		
		///* GIPUZKOA.EUS -EN ERABILITAKOAK *///
		///* Aktualitatea
		///*if(urlname2.contains("aktualitatea")){
		///*	m_labelByHand = "aktualitatea"; break;
		///*}	
		// *Tramiteak eta Zerbitzuak
	///*	if(urlname2.contains("tramiteak-eta-zerbitzuak")){
		///*	m_labelByHand = "tramiteak_eta_zerbitzuak"; break;
	///*	}
		///* Aldundia
	///*	if(urlname2.contains("aldundia")){
		///*	m_labelByHand = "aldundia"; break;
	///*	}
		/// *Partaidetza
	///*	if(urlname2.contains("partaidetza")){
		///*	m_labelByHand = "partaidetza"; break;
	///*	}	
		
		// /*Bilatzailea
		///*if(urlname2.contains("bilaketaren-emaitzak")){
		///*	m_labelByHand = "bilatzailea"; break;
		///*}	
		
		///* Castellano
		///*		if(urlname2.contains("/es/")){
		///*			m_labelByHand = "castellano"; break;
		///*		}	
		
		///* Euskara
			///*	if(urlname2.contains("/eu/")){
			///*		m_labelByHand = "euskara"; break;
			///*	}
		
		///* Gipuzkoa.net
		///*		if(urlname2.contains("gipuzkoa.net")){
			///*		m_labelByHand = ".net"; break;
			///*	}	
		
		///* Gipuzkoa.eus
			///*	if(urlname2.contains("gipuzkoa.eus")){
			///*		m_labelByHand = ".eus"; break;
		///*		}
		
			////** EHU.LOGETAN ERABILITAKOAK
			
			////** Ehu.eus
			    if(urlname2.contains("ehu.eus")){
			       m_labelByHand = m_labelByHand + "_.eus"; break;
				}	
			
			////** Ehu.es
				if(urlname2.contains("ehu.es")){
			    	m_labelByHand = m_labelByHand + "_.es"; break;
				}
			////** Castellano
				if(urlname2.contains("/es/")){
		        	m_labelByHand = m_labelByHand + "_castellano"; break;
				}	
				
			////** Euskara
				if(urlname2.contains("/eu/")){
			    	m_labelByHand = m_labelByHand + "_euskara"; break;
				}
			////** Euskara
							if(urlname2.contains("/en/")){
						    	m_labelByHand = m_labelByHand + "_english"; break;
							}
				
			////**Sarrera-acceso (matrikulazioa)
				if(urlname2.contains("/sarrera-acceso")){
			    	m_labelByHand = m_labelByHand + "_sarrera-acceso"; break;
				}
				
		break;
		}
		
		
		// formated URL name
		m_formatedurlname = this.removeTildes(m_urlname);
		
	}
	
	/**
	 * Removes the tildes.
	 *
	 * @param input the input
	 * @return the string
	 */
	private String removeTildes(String input) {
	    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
	    //String noTildes1 = normalized.replaceAll("[^\\p{ASCII}]", "");
	    String noTildes2 = normalized.replaceAll("\\p{M}", "");
	    return noTildes2;
	}
	
	/* (non-Javadoc)
	 * @see Gipuzkoa_eus.webrecommendation.structures.page.PageAbstractG#getIsSuitableToLinkPrediction()
	 */
	public boolean getIsSuitableToLinkPrediction(){
		// !m_labelByHand.equals("index") we want index URLs in the sequences
		// because its alignment give us information.
		m_isvalidLP =	m_isvalid &&
						m_ishtml;
		         		/*
		         	 	&&
						(	!m_labelByHand.contains("aktualitatea") && 
							!m_labelByHand.contains("tramiteak_eta_zerbitzuak") && 
							!m_labelByHand.contains("aldundia") && 
							!m_labelByHand.contains("partaidetza") && 
							!m_labelByHand.contains("bilatzailea") && 
							!m_labelByHand.contains("castelano") && 
							!m_labelByHand.contains("euskara")&&
							!m_labelByHand.contains(".eus") && 
							!m_labelByHand.contains(".net")
						);
						*/
		return m_isvalidLP;
	}
	
}

