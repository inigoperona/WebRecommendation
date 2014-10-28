package ehupatras.webrecommendation.structures.page;

import java.text.Normalizer;

public class PageDiscapnet 
				extends PageAbstract {
	
	// To implement serializable needs this
	private static final long serialVersionUID = 1L;

	
	// CREATOR
	public PageDiscapnet(String urlname){
		super(urlname);
	}
	
	protected void isValid(){
		// default is true
		m_isvalid = true;
		
		// Select the URLs related with the user click.
		// Those URLs with the extension: root (/), php, pdf, asp
		String urlname2 = m_urlname.toLowerCase();
		if(	!urlname2.contains(".aspx") &&
			!urlname2.contains(".pdf") && 
			!urlname2.contains(".htm") &&
			!urlname2.contains(".html") &&
			!urlname2.contains(".doc")){
			m_isvalid = false;
		}
		
		// administration related clicks are not valid
		if(	urlname2.contains("/admin/") ||
			urlname2.contains("/administracion/")){
			m_isvalid = false;
		}
		
		// error pages
		if(urlname2.contains("/error/")){
			m_isvalid = false;
		}
	}
	
	protected void ishtml(){
		// default
		m_ishtml = false;
		
		// Detect those URLs that have a text format (no PDFs)
		String urlname2 = m_urlname.toLowerCase();
		if(	urlname2.contains(".aspx") ||
			urlname2.contains(".htm") ||
			urlname2.contains(".html")){
			m_ishtml = true;
		}
	}
	
	protected void cleanURL(){
		String urlname2 = m_urlname.toLowerCase();
		m_language = "es"; // default
		
		// label some URLs by hand //
		while(true){
			
		// actualidad
		if(urlname2.contains("actualidad")){
			m_labelByHand = "actualidad"; break;
		}
		
		// noticias
		if(urlname2.contains("noticias")){
			m_labelByHand = "noticias";
			if(urlname2.contains("accesibilidad")){
				m_labelByHand = "noticias_accesibilidad"; break;
			}
			if(urlname2.contains("ocioycultura")){
				m_labelByHand = "noticias_ocioycultura"; break;
			}
			if(urlname2.contains("tecnologia")){
				m_labelByHand = "noticias_tecnologia"; break;
			}
			if(urlname2.contains("educacion")){
				m_labelByHand = "noticias_educacion"; break;
			}
			if(urlname2.contains("empleo")){
				m_labelByHand = "noticias_empleo"; break;
			}
			if(urlname2.contains("derechos")){
				m_labelByHand = "noticias_derechos"; break;
			}
			if(urlname2.contains("medioambiente")){
				m_labelByHand = "noticias_medioambiente"; break;
			}
			break;
		}
		
		// other areas
		if(urlname2.contains("enviaramigo")){
			m_labelByHand = "enviaramigo"; break;
		}
		if(urlname2.contains("pdf")){
			m_labelByHand = "pdf"; break;
		}
		if(urlname2.contains("layouts")){
			m_labelByHand = "layouts"; break;
		}
		if(urlname2.contains("castellano/paginas/buscador.aspx")){
			m_labelByHand = "buscador"; break;
		}
		
		// buscador derecho
		if(urlname2.contains("derechos/legislacionsobrediscapacidad/buscador")){
			m_labelByHand = "derecho_buscador_hub";
			if(urlname2.contains("idlegis=")){
				m_labelByHand = "derecho_buscador_auth"; break;
			}
			break;
		}
		
		// comunidad
		if(urlname2.contains("comunidad")){
			// comunidad: movimientoasociativo
			if(urlname2.contains("comunidad/movimientoasociativo")){
				m_labelByHand = "com_movimientoasociativo_auth";
				if(urlname2.contains("comunidad/movimientoasociativo/paginas")){
					m_labelByHand = "com_movimientoasociativo_hub"; break;
				}
				if(urlname2.contains("comunidad/movimientoasociativo/organizaciones")){
					if(urlname2.contains("clasificar")){
						m_labelByHand = "com_movimientoasociativo_hub"; break;
					}
					if(urlname2.contains("resultados")){
						m_labelByHand = "com_movimientoasociativo_hub"; break;
					}
					if(urlname2.contains("buscador")){
						m_labelByHand = "com_movimientoasociativo_hub"; break;
					}
					if(urlname2.contains("alta")){
						m_labelByHand = "com_movimientoasociativo-hub"; break;
					}
				}
				break;
			}
			// comunidad_websocial
			if(urlname2.contains("comunidad/websocial")){
				m_labelByHand = "com_ws";
				// comunidad_websocial: enlaces
				if(urlname2.contains("comunidad/websocial/recursos/enlaces")){
					m_labelByHand = "com_ws_enlaces_hub"; break;
				}
				// comunidad_websocial: bibliografia
				if(urlname2.contains("bibliografia")){
					m_labelByHand = "com_ws_bibli_auth";
					if(urlname2.contains("listadoporletra")){
						m_labelByHand = "com_ws_bibli_hub"; break;
					}
					if(urlname2.contains("buscadoravanzado")){
						m_labelByHand = "com_ws_bibli_hub"; break;
					}
					if(urlname2.contains("default")){
						m_labelByHand = "com_ws_bibli_hub"; break;
					}
					break;
				}
				// comunidad_websocial: glosario
				if(urlname2.contains("glosario")){
					m_labelByHand = "com_ws_glosario_auth";
					if(urlname2.contains("glosario/paginas")){
						m_labelByHand = "com_ws_glosario_hub"; break;
					}
					break;
				}
				// comunidad_websocial: documentos
				if(urlname2.contains("documentos")){
					m_labelByHand = "com_ws_docs_auth";
					if(urlname2.contains("documentos/paginas")){
						m_labelByHand = "com_ws_docs_hub"; break;
					}
					break;
				}
				break;
			}
			// comunidad: canaljunior
			if(urlname2.contains("comunidad/canaljunior")){
				m_labelByHand = "com_canaljunior"; break;
			}
		}
		
		// images
		if(urlname2.contains("/img/") || urlname2.contains("/medioambienteenimagenes/")){
			m_labelByHand = "image"; break;
		}
		
		// INDEX
		if(urlname2.equals("/paginas/default.aspx")){
			m_labelByHand = "index"; break;
		}
		if(urlname2.equals("/castellano/paginas/default.aspx")){
			m_labelByHand = "index"; break;
		}
		if(urlname2.equals("/default.aspx")){
			m_labelByHand = "index"; break;
		}
		
		// mapaweb
		if(urlname2.contains("/castellano/paginas/mapaweb.aspx")){
			m_labelByHand = "mapaweb"; break;
		}
		
		// other areas
		if(urlname2.contains("/castellano/paginas/")){
			m_labelByHand = "esquema"; break;
		}
		if(urlname2.contains("sitecollectiondocuments")){
			m_labelByHand = "sitecollectiondocuments"; break;
		}
		if(urlname2.contains("web_accesible")){
			m_labelByHand = "web_accesible"; break;
		}
		if(urlname2.contains("allitems") || urlname2.contains("forms") || urlname2.contains("registration")){
			m_labelByHand = "forms"; break;
		}
		
		// english
		if(urlname2.contains("english")){
			m_labelByHand = "english"; 
			m_language = "en"; break;
		}
		if(urlname2.contains("/castellano/areastematicas/tecnologia/drt4all/en/")){
			m_labelByHand = "english"; 
			m_language = "en"; break;
		}
		
		// salud
		if(urlname2.contains("salud")){
			m_labelByHand = "salud"; break;
		}
		
		// others
		if(urlname2.contains("/castellano/areastematicas/")){
			m_labelByHand = "others"; break;
		}
		if(urlname2.contains("/castellano/comunidad/canalsenior/")){
			m_labelByHand = "others"; break;
		}
		
		break;
		} // end while
		
		
		// formated URL name
		m_formatedurlname = this.removeTildes(m_urlname);
		
	}
	
	private String removeTildes(String input) {
	    String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
	    //String noTildes1 = normalized.replaceAll("[^\\p{ASCII}]", "");
	    String noTildes2 = normalized.replaceAll("\\p{M}", "");
	    return noTildes2;
	}
	
	public boolean getIsSuitableToLinkPrediction(){
		// !m_labelByHand.equals("index") we want index URLs in the sequences
		// because its alignment give us information.
		m_isvalidLP =	m_isvalid &&
						m_ishtml &&
						m_isStatic &&
						m_isFrequent &&
						(
		//					!m_labelByHand.contains("actualidad") && 
		//					!m_labelByHand.contains("noticias")
							!m_labelByHand.contains("enviaramigo") && 
							!m_labelByHand.contains("pdf") && 
							!m_labelByHand.contains("layouts") && 
							!m_labelByHand.contains("buscador") && 
							!m_labelByHand.contains("com_canaljunior") && 
							!m_labelByHand.contains("image") && 
							!m_labelByHand.contains("esquema") && 
							!m_labelByHand.contains("sitecollectiondocuments") && 
							!m_labelByHand.contains("web_accesible") && 
							!m_labelByHand.contains("forms") && 
							!m_labelByHand.contains("english") && 
							!m_labelByHand.contains("salud") && 
							!m_labelByHand.contains("others")
						);
		return m_isvalidLP;
	}
	
}
