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
	//private static Hashtable<String,Page> m_url2idHT = new Hashtable<String,Page>();
	//private static Hashtable<Integer,String> m_ID2urlname = new Hashtable<Integer,String>();
	private static ArrayList<String> m_urls = new ArrayList<String>();
	private static ArrayList<Page> m_pages = new ArrayList<Page>();
	private static ArrayList<Integer> m_ids = new ArrayList<Integer>();	
	
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
		return m_urls.contains(urlname);
	}
	
	
	public static void storeURL(Page page){
		String url = page.getFormatedUrlName();
		if(!Website.containsURL(url)){ // new page
			page.setUrlIDusage(m_urlID);
			Website.m_urlID++;
			m_maxUrlID = m_urlID; // as a maximum ID number the last one
			page.incrementFrequency();
		} else { // old page
			page = Website.getPage(url);
			page.incrementFrequency();
		}
		Website.putURL(url, page);
	}
	
	public static void putURL(String urlname, Page page){
		// old function
		int ind = m_urls.indexOf(urlname);
		if(ind==-1){ // old page
			m_urls.add(urlname);
			m_pages.add(page);
			int urlid = page.getUrlIDusage(); 
			m_ids.add(urlid);
		} else { // new page
			m_pages.set(ind, page);
		}
		
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
	
	public static int getURLID(String urlname){
		int ind = m_urls.indexOf(urlname);
		return m_ids.get(ind);
	}
	
	public static String getURL(int urlID){
		int ind = m_ids.indexOf(urlID);
		return m_urls.get(ind);
	}
	
	public static Page getPage(String urlname){
		int ind = m_urls.indexOf(urlname);
		return m_pages.get(ind);
	}
	
	public static Page getPage(int urlID){
		int ind = m_ids.indexOf(urlID);
		return m_pages.get(ind);
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public static int size(){
		return m_urls.size();
	}
	
	/**
	 * Compute page rank.
	 */
	public static void computePageRank(){
		for(int i=0; i<m_urls.size(); i++){
			String url = m_urls.get(i);
			Page page = m_pages.get(i);
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
	public static ArrayList<String> getAllFormatedUrlNames(){
		return m_urls;
	}
	
	
	
	
	
	// FUNCTIONS TO STORE THE WEBSITE
	
	/**
	 * Write website.
	 */
	public static void writeWebsite(){
		for(int i=0; i<m_urls.size(); i++){
			Page pag = m_pages.get(i);
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
		Object[] objA = new Object[3];
		objA[0] = m_urls;
		objA[1] = m_ids;
		objA[2] = m_pages;
		slo.save(objA, m_workdirectory + m_saveFileName);
	}
	
	/**
	 * Load.
	 */
	public static void load(){
		SaveLoadObjects slo = new SaveLoadObjects();
		Object[] objA = (Object[])slo.load(m_workdirectory + m_saveFileName);
		m_urls = (ArrayList<String>)objA[0];
		m_ids = (ArrayList<Integer>)objA[1];
		m_pages = (ArrayList<Page>)objA[2];
		
		// update the maximum index
		int maxindex = Integer.MIN_VALUE;
		for(int i=0; i<m_ids.size(); i++){
			Integer urlid = m_ids.get(i);
			if(maxindex<urlid){
				maxindex = urlid;
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
		for(int i=0; i<m_urls.size(); i++){
			String url = m_urls.get(i);
			Page pag = Website.getPage(url);
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
		int obj1s = slo.getSize(m_urls);
		int obj2s = slo.getSize(m_ids);
		int obj3s = slo.getSize(m_pages);
		int sizeInBytes = obj1s + obj2s + obj3s;
		int mb = 1024*1024;
		int sizeInMegabytes = sizeInBytes / mb; 
		return sizeInMegabytes;
	}
	
}
