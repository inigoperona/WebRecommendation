package ehupatras.webrecommendation.structures;

import ehupatras.webrecommendation.structures.page.Page;
import ehupatras.webrecommendation.utils.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.ArrayList;

public class Website {
	
	// ATTRIBUTES
	
	// work directory
	private static String m_workdirectory = ".";
	
	// HashTable to compute urlID from formatedURLs
	private static Hashtable<String,Page> m_url2idHT = new Hashtable<String,Page>();
	private static Hashtable<Integer,String> m_ID2urlname = new Hashtable<Integer,String>();
	private static int m_urlID = 0;
	private static int m_maxUrlID = 0;
	
	// File to save the Website structure
	private static String m_saveFileName = "/_Website.javaData";
	
	
	
	
	
	// FUNCTIONS TO MANAGE THE WEBSITE'S PAGE
	
	public static boolean containsURL(String urlname){
		return m_url2idHT.containsKey(urlname);
	}
	
	public static void putURL(String urlname, Page page){
		m_url2idHT.put(urlname, page);
	}
	
	public static int getURLID(String urlname){
		return m_url2idHT.get(urlname).getUrlIDusage();
	}
	
	public static Page getPage(String urlname){
		return m_url2idHT.get(urlname);
	}
	
	public static Page getPage(int urlID){
		String urlname = m_ID2urlname.get(urlID);
		Page pag = m_url2idHT.get(urlname);
		return pag;
	}
	
	public static void setPage(String urlname, Page pag){
		m_url2idHT.put(urlname, pag);
	}
	
	public static int size(){
		return m_url2idHT.size();
	}
	
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
	
	public static int getMaximumUrlID(){
		return m_maxUrlID;
	}
	
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
	
	public static void save(){
		SaveLoadObjects slo = new SaveLoadObjects();
		Object[] objA = new Object[2];
		objA[0] = m_url2idHT;
		objA[1] = m_ID2urlname;
		slo.save(objA, m_workdirectory + m_saveFileName);
	}
	
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
	
	public static void setSaveFileName(String saveFileName){
		m_saveFileName = saveFileName;
	}
	
	public static void setWorkDirectory(String workdirectory){
		m_workdirectory = workdirectory;
	}
	
	
	
	
	// FUNCTIONS TO EXTRACT HOMEPAGE PAGES
	
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
	
}
