package ehupatras.webrecommendation.structures;

import ehupatras.webrecommendation.structures.page.Page;
import ehupatras.webrecommendation.structures.request.Request;
import ehupatras.webrecommendation.utils.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Website.
 */
public class Website {
	
	// ATTRIBUTES
	
	// work directory
	/** The m_workdirectory. */
	private static String m_workdirectory = ".";
	
	// HashTable to compute urlID from formatedURLs
	private static Hashtable<String,Page> m_url2idHT = new Hashtable<String,Page>();
	private static Hashtable<Integer,String> m_ID2urlname = new Hashtable<Integer,String>();
	//private static ArrayList<String> m_urls = new ArrayList<String>();
	//private static ArrayList<Page> m_pages = new ArrayList<Page>();
	//private static ArrayList<Integer> m_ids = new ArrayList<Integer>();	
	
	/** The m_url id. */
	private static int m_urlID = 0;
	
	/** The m_max url id. */
	private static int m_maxUrlID = 0;
	
	// File to save the Website structure
	private static String m_saveFileName = "/_Website.javaData";
	
	// dump to disk attributes
	private static int m_maxloadpages = 10000;
	private static int m_actualloadedpage = 0;
	private static int m_lastloadedpage = 0;
	private static int m_writedmodulus = 0;
	private static String m_basenamejavadata = "website.javaData";
	
	
	// FUNCTIONS TO MANAGE THE WEBSITE'S PAGE
	
	/**
	 * Contains url.
	 *
	 * @param urlname the urlname
	 * @return true, if successful
	 */
	public static boolean containsURL(String urlname){
		return m_url2idHT.containsKey(urlname);
	}
	
	/**
	 * Put url.
	 *
	 * @param urlname the urlname
	 * @param page the page
	 */
	public static void putURL(String urlname, Page page){
		// old function
		//m_url2idHT.put(urlname, page);
		
		// the new function
		/*
		int ind = 
		
		int imodulus = i / m_maxloadpages;
		int iindex = i % m_maxloadpages;
		
		// if we have in memory aplly there
		int iMem = m_actualloadedmodulusS.indexOf(new Integer(imodulus));
		if(iMem!=-1){
			m_filterlogS.get(iMem).remove(iindex);
			m_filterlogS.get(iMem).add(iindex, req);
		} else {
			// the main modulus
			if(m_actualloadedmodulus!=imodulus){
				long starttime = System.currentTimeMillis();
				int oldmod = m_actualloadedmodulus;
				savemodulus(m_actualloadedmodulus, m_filterlog, basenamejavadata);
				loadmodulus(imodulus, basenamejavadata, false, false);
				long endtime = System.currentTimeMillis();
				System.out.println("  [" + endtime + "] End swaping modulus. " + 
					oldmod + " <-> " + m_actualloadedmodulus + ". " +
					(endtime-starttime)/1000 + " seconds. [replaceRequest]");
			}
			m_filterlog.remove(iindex);
			m_filterlog.add(iindex, req);
		}
		*/
		
	}
	
	/**
	 * Gets the urlid.
	 *
	 * @param urlname the urlname
	 * @return the urlid
	 */
	public static int getURLID(String urlname){
		return m_url2idHT.get(urlname).getUrlIDusage();
	}
	
	/**
	 * Gets the page.
	 *
	 * @param urlname the urlname
	 * @return the page
	 */
	public static Page getPage(String urlname){
		return m_url2idHT.get(urlname);
	}
	
	/**
	 * Gets the page.
	 *
	 * @param urlID the url id
	 * @return the page
	 */
	public static Page getPage(int urlID){
		String urlname = m_ID2urlname.get(urlID);
		Page pag = m_url2idHT.get(urlname);
		return pag;
	}
	
	/**
	 * Sets the page.
	 *
	 * @param urlname the urlname
	 * @param pag the pag
	 */
	public static void setPage(String urlname, Page pag){
		m_url2idHT.put(urlname, pag);
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public static int size(){
		return m_url2idHT.size();
	}
	
	/**
	 * Compute page rank.
	 */
	public static void computePageRank(){
		Enumeration<String> formatedURLsKeys = m_url2idHT.keys();
		while(formatedURLsKeys.hasMoreElements()){
			String key = formatedURLsKeys.nextElement();
			Page page = m_url2idHT.get(key);
			if(page.getIsSuitableToLinkPrediction()){
				String urlname = page.getUrlName();
				System.out.println(urlname);
			}
			
			//GooglePageRank obj = new GooglePageRank();
			//int pagerankvalue = obj.getPR(urlname);
			//page.setPageRank(pagerankvalue);
			
			//System.out.println(pagerankvalue);
		}
	}
	
	/**
	 * Store url.
	 *
	 * @param page the page
	 */
	public static void storeURL(Page page){
		String formatedurlname = page.getFormatedUrlName();
		if(!Website.containsURL(formatedurlname)){
			Website.m_urlID++;
			page.setUrlIDusage(m_urlID);
			page.incrementFrequency();
			// keep URL_ID to formatedString Hashtable
			m_ID2urlname.put(m_urlID, formatedurlname);
			// as a maximum ID number the last one
			m_maxUrlID = m_urlID;
			Website.putURL(formatedurlname, page);
		} else {
			Page oldpag = Website.getPage(formatedurlname);
			oldpag.incrementFrequency();
			Website.putURL(formatedurlname, oldpag);
		}
	}
	
	/**
	 * Gets the maximum url id.
	 *
	 * @return the maximum url id
	 */
	public static int getMaximumUrlID(){
		return m_maxUrlID;
	}
	
	/**
	 * Gets the all formated url names.
	 *
	 * @return the all formated url names
	 */
	public static String[] getAllFormatedUrlNames(){
		Enumeration<String> keys = m_url2idHT.keys();
		String[] keysA = new String[m_url2idHT.size()];
		int i = 0;
		while(keys.hasMoreElements()){
			keysA[i] = keys.nextElement();
			i++;
		}
		return keysA;
	}
	
	
	
	
	
	// FUNCTIONS TO STORE THE WEBSITE
	
	/**
	 * Write website.
	 */
	public static void writeWebsite(){
		Enumeration<String> keys = m_url2idHT.keys();
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			Page pag = m_url2idHT.get(key);
			int urlid = pag.getUrlIDusage();
			String urlname = pag.getUrlName();
			System.out.println(urlid + " " + urlname);
		}
	}
	
	/**
	 * Save.
	 */
	public static void save(){
		SaveLoadObjects slo = new SaveLoadObjects();
		Object[] objA = new Object[2];
		objA[0] = m_url2idHT;
		objA[1] = m_ID2urlname;
		slo.save(objA, m_workdirectory + m_saveFileName);
	}
	
	/**
	 * Load.
	 */
	public static void load(){
		SaveLoadObjects slo = new SaveLoadObjects();
		Object[] objA = (Object[])slo.load(m_workdirectory + m_saveFileName);
		m_url2idHT = (Hashtable<String,Page>)objA[0];
		m_ID2urlname = (Hashtable<Integer,String>)objA[1];
		
		// update the maximum index
		int maxindex = Integer.MIN_VALUE;
		Enumeration<Integer> keys = m_ID2urlname.keys();
		while(keys.hasMoreElements()){
			Integer key = keys.nextElement();
			if(maxindex<key){
				maxindex = key;
			}
		}
		m_maxUrlID = maxindex;
		m_urlID = maxindex;
	}
	
	/**
	 * Sets the save file name.
	 *
	 * @param saveFileName the new save file name
	 */
	public static void setSaveFileName(String saveFileName){
		m_saveFileName = saveFileName;
	}
	
	/**
	 * Sets the work directory.
	 *
	 * @param workdirectory the new work directory
	 */
	public static void setWorkDirectory(String workdirectory){
		m_workdirectory = workdirectory;
	}
	
	
	
	
	// FUNCTIONS TO EXTRACT HOMEPAGE PAGES
	
	/**
	 * Gets the home pages.
	 *
	 * @return the home pages
	 */
	public static int[] getHomePages(){
		// structure to store pages
		ArrayList<Integer> indexesAL = new ArrayList<Integer>();
		
		// for each page in the website
		String[] keys = Website.getAllFormatedUrlNames();
		for(int i=0; i<keys.length; i++){
			Page pag = Website.getPage(keys[i]);
			if(pag.getIsIndex()){
				int usageID = pag.getUrlIDusage();
				indexesAL.add(usageID);
			}
		}
		
		// convert to int[]
		int[] indexes = new int[indexesAL.size()];
		for(int i=0; i<indexesAL.size(); i++){
			indexes[i] = indexesAL.get(i);
		}
		return indexes;
	}

	public static int getSize(){
		SaveLoadObjects slo = new SaveLoadObjects(); 
		int obj1s = slo.getSize(m_url2idHT);
		int obj2s = slo.getSize(m_ID2urlname);
		int sizeInBytes = obj1s + obj2s;
		int mb = 1024*1024;
		int sizeInMegabytes = sizeInBytes / mb; 
		return sizeInMegabytes; 
	}
	
}
