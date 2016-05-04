package ehupatras.webrecommendation.structures;

import ehupatras.webrecommendation.structures.page.Page;
import ehupatras.webrecommendation.structures.request.Request;
import ehupatras.webrecommendation.utils.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	//private static ArrayList<Integer> m_ids = new ArrayList<Integer>();	
	
	/** The m_url id. */
	private static int m_urlID = 0;
	
	/** The m_max url id. */
	private static int m_maxUrlID = 0;
	
	// File to save the Website structure
	private static String m_saveFileName = "/_Website.javaData";
	
	// dump to disk attributes
	private static int m_actualloadedmodulus = -1;
	private static int m_maxloadpages = 10000;
	private static int m_actualloadedpage = -1;
	private static int m_lastloadedpage = -1;
	private static int m_writedmodulus = -1;
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
		int ind = m_urls.indexOf(urlname);
		if(ind==-1){ // old page
			m_urls.add(urlname);
			m_pages.add(page);
		} else { // new page
			m_pages.set(ind, page);
		}
	}
	
	/*
	public static void storeURL(Page page){
		String url = page.getFormatedUrlName();
		if(!Website.containsURL(url)){ // new page
			page.incrementFrequency();
		} else { // old page
			page = Website.getPage(url);
			page.incrementFrequency();
		}
		Website.putURL2(url, page);
	}
	
	private static void putURL(String urlname, Page page, int indmod){
		if(indmod==-1){ // new page
			m_urls.add(urlname);
			// URL ID
			int urlid = Website.m_urlID;
			page.setUrlIDusage(urlid);
			m_pages.add(page);			
			Website.m_maxUrlID = urlid;
			Website.m_urlID++;
		} else { // old page
			m_pages.set(indmod, page);
		}
	}
		
	public static void putURL2(String urlname, Page page){
		// compute the modulus and index
		int ind = m_urls.indexOf(urlname);
		
		if(Website.m_urlID == 10128){
			System.out.println("ind: " + ind);
		}
		
		if(ind == 10129){
			System.out.println("ind: " + ind);
		}
		
		if(ind!=-1){ // it exits
			int imodulus = ind / m_maxloadpages;
			int iindex = ind % m_maxloadpages;
			
			// load the new modulus
			if(imodulus!=m_actualloadedmodulus){
				long starttime = System.currentTimeMillis();
				int oldmod = m_actualloadedmodulus;
				
				// save
				if(m_actualloadedmodulus==-1){
					m_actualloadedmodulus = 0;
				} else {
					savemodulus(m_actualloadedmodulus);
				}
				
				// load
				if(m_writedmodulus<imodulus){
					m_pages = new ArrayList<Page>();
					m_actualloadedpage = 0;
					m_writedmodulus++;
				} else {
					loadmodulus(imodulus);
				}
				
				System.gc();
				
				long endtime = System.currentTimeMillis();
				System.out.println("  [" + endtime + "] End swaping modulus. " + 
						oldmod + " <-> " + m_actualloadedmodulus + ". " +
						(endtime-starttime)/1000 + " seconds. [Website.putURL]");
			}
			
			// write
			m_pages.set(iindex, page);
			
		} else { // add
			int imodulus;
			int iindex;
			if(m_writedmodulus==-1){
				m_pages = new ArrayList<Page>();
				m_actualloadedpage = 0;
				m_writedmodulus = 1;
				m_actualloadedmodulus = 0;
				imodulus = 0;
				iindex = -1;
			} else {
				if((m_writedmodulus-1)!=m_actualloadedmodulus){
					savemodulus(m_actualloadedmodulus);
					loadmodulus(m_writedmodulus);
				} else {
					if(m_pages.size()>=m_maxloadpages){
						savemodulus(m_actualloadedmodulus);
						m_pages = new ArrayList<Page>();
						m_actualloadedpage = 0;
						m_writedmodulus++;
						m_actualloadedmodulus = m_writedmodulus - 1; 
					}
				}

				iindex = -1;
			}
			
			// write
			Website.putURL(urlname, page, iindex);
			m_actualloadedpage++;
			m_lastloadedpage = m_actualloadedpage - 1;
		}
		
		
	}
	 */

	private static void savemodulus(int mod){
		String outputfilename = "_" + mod + "_" + m_basenamejavadata;
		
		// open
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(m_workdirectory + "/" + outputfilename);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccess.savemodulus] " +
				"Problems at opening the file: " + outputfilename + " to write.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// write
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(fos);
			oos.writeObject(m_pages);
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccess.savemodulus] " +
				"Problems at writing in the file: " + outputfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close
		try{
			oos.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccess.savemodulus] " +
				"Problems at closing the file: " + outputfilename + " after writing.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	private static void loadmodulus(int mod){		
		// open
		String outputfilename = "_" + mod + "_" + m_basenamejavadata;
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(m_workdirectory + "/" + outputfilename);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.loadmodulus] " +
					"Problems at opening the file: " + outputfilename + " to read.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// read
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(fis);
			m_pages = (ArrayList<Page>)ois.readObject();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.loadmodulus] " +
					"Problems at reading the file: " + outputfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		} catch(ClassNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.loadmodulus] " +
					"Problems at casting to a specific object: " + outputfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close
		try{
			ois.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.loadmodulus] " +
					"Problems closing the file: " + outputfilename + " after reading.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// update the indexes
		m_actualloadedmodulus = mod;
	}


	public static int getURLID(String urlname){
		int ind = m_urls.indexOf(urlname);
		return ind;
	}
	
	public static String getURL(int urlID){
		return m_urls.get(urlID);
	}
	
	public static Page getPage(String urlname){
		int ind = m_urls.indexOf(urlname);
		return m_pages.get(ind);
	}
	
	public static Page getPage(int urlID){
		return m_pages.get(urlID);
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
		Object[] objA = new Object[2];
		objA[0] = m_urls;
		objA[1] = m_pages;
		slo.save(objA, m_workdirectory + m_saveFileName);
	}
	
	/**
	 * Load.
	 */
	public static void load(){
		SaveLoadObjects slo = new SaveLoadObjects();
		Object[] objA = (Object[])slo.load(m_workdirectory + m_saveFileName);
		m_urls = (ArrayList<String>)objA[0];
		m_pages = (ArrayList<Page>)objA[1];
		
		// update the maximum index
		int maxindex = m_urls.size()-1;
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
		int obj2s = slo.getSize(m_pages);
		int sizeInBytes = obj1s + obj2s;
		int mb = 1024*1024;
		int sizeInMegabytes = sizeInBytes / mb; 
		return sizeInMegabytes;
	}
	
}
