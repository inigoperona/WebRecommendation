package ehupatras.webrecommendation.structures;

import ehupatras.webrecommendation.structures.page.Page;
import ehupatras.webrecommendation.structures.request.Request;
import ehupatras.webrecommendation.utils.*;

import java.io.File;
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
	
	// indexing elements
	private static ArrayList<String> m_urls = new ArrayList<String>();
	private static int m_n0 = 0;
	//private static ArrayList<Integer> m_ids = new ArrayList<Integer>();	
	
	// store in the memory limited recommendations
	private static int m_maxloadpages = 200000;
	private static ArrayList<Integer> m_indexesAL = new ArrayList<Integer>(m_maxloadpages);
	private static ArrayList<Page> m_pages = new ArrayList<Page>();
	private static ArrayList<Page> m_pagesAL2 = new ArrayList<Page>(m_maxloadpages);
	private static ArrayList<Integer> m_frequenciesAL = new ArrayList<Integer>(m_maxloadpages);
	
	// File to save the Website structure
	private static String m_saveFileName = "/_Website.javaData";
	
	// dump to disk attributes
	private static int m_actualloadedmodulus = 0;
	private static int m_nomodulus = 0;
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
		Website.putURL(url, page);
	}
	
	public static void putURL(String urlname, Page page){	
		// get the URL-ID
		int urlid = m_urls.indexOf(urlname);
		int ind2 = -1;
		if(urlid==-1){
			m_urls.add(urlname);
			urlid = m_urls.size()-1;
		} else {
			ind2 = m_indexesAL.indexOf(urlid);
		}
		
		// update the structure in the memory
		if(ind2==-1){
			m_indexesAL.add(urlid);
			m_pagesAL2.add(page);
			m_frequenciesAL.add(1);
		} else {
			int freq = m_frequenciesAL.get(ind2) + 1;
			m_frequenciesAL.set(ind2, freq);
		}
		
		// save pages
		Website.dumpToDisk(false);
	}
	
	private static void dumpToDisk(boolean forceToDump){
		// when the maximum number of Pages has been store, dump it.
		if(forceToDump || m_indexesAL.size()>=m_maxloadpages){
			// order the indexes
			ArrayList<Integer> orderedIndexesAL = new ArrayList<Integer>();
			for(int i=0; i<m_indexesAL.size(); i++){
				int urlidi = m_indexesAL.get(i);
				
				// fast approach
				int jump = 1000;
				int j = jump;
				for(; j<orderedIndexesAL.size(); j=j+jump){
					int indj = orderedIndexesAL.get(j);
					int urlidj = m_indexesAL.get(indj);
					if(urlidi<urlidj){
						break;
					}
				}
				j = j - jump;
				
				// one by one
				for(; j<orderedIndexesAL.size(); j++){
					int indj = orderedIndexesAL.get(j);
					int urlidj = m_indexesAL.get(indj);
					if(urlidi<urlidj){
						break;
					}
				}
				orderedIndexesAL.add(j,i);
			}
			
			// save all the pages
			for(int i=0; i<orderedIndexesAL.size(); i++){
				// get the data to update
				int indi = orderedIndexesAL.get(i);
				Page pag = m_pagesAL2.get(indi);
				int urlidi = m_indexesAL.get(indi);
				int freq = m_frequenciesAL.get(indi);
				
				// get the stored page
				if(urlidi<m_n0){
					pag = Website.getPage(urlidi);
				}
				
				// update the page info
				pag.setUrlIDusage(urlidi);
				pag.incrementFrequency(freq);
				
				// set the page
				Website.setPage(urlidi, pag);
				//System.out.println("id: " + urlidi + "; url: " + m_urls.get(urlidi));
				
				// when the modulus is full
				if(		m_nomodulus==m_actualloadedmodulus && 
						(m_pages.size()>=m_maxloadpages ||
						forceToDump)){
					long starttime = System.currentTimeMillis();
					Website.savemodulus(m_actualloadedmodulus);
					long endtime = System.currentTimeMillis();
					System.out.println("  [" + endtime + "] Save modulus: " +
							m_actualloadedmodulus + ". " +
							(endtime-starttime)/1000 + " seconds. [Website.putURL]");
					m_pages = new ArrayList<Page>();
					m_actualloadedmodulus++;
					m_nomodulus = m_actualloadedmodulus; 
				}
			}
			
			// initialize the temporary website
			m_n0 = m_urls.size();
			m_indexesAL = new ArrayList<Integer>(m_maxloadpages);
			m_pagesAL2 = new ArrayList<Page>(m_maxloadpages);
			m_frequenciesAL = new ArrayList<Integer>(m_maxloadpages);
		}
	}

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
		return Website.getPage(ind);
	}
	
	public static Page getPage(int urlID){
		int imodulus = urlID / m_maxloadpages;
		int iindex = urlID % m_maxloadpages;
		
		if(m_actualloadedmodulus!=imodulus){
			long starttime = System.currentTimeMillis();
			int oldmod = m_actualloadedmodulus;
			savemodulus(m_actualloadedmodulus);
			loadmodulus(imodulus);
			long endtime = System.currentTimeMillis();
			String inmem = "(" + m_actualloadedmodulus + ")";
			System.out.println("  [" + endtime + "] End swaping modulus. " +
					oldmod + " <-> " + m_actualloadedmodulus + ". " +
					inmem + ". " +
					(endtime-starttime)/1000 + " seconds. [Website.getPage]");
		}
		
		return m_pages.get(iindex);
	}
	
	public static void setPage(int urlID, Page pag){
		int imodulus = urlID / m_maxloadpages;
		int iindex = urlID % m_maxloadpages;
		
		if(m_actualloadedmodulus!=imodulus){
			long starttime = System.currentTimeMillis();
			int oldmod = m_actualloadedmodulus;
			savemodulus(m_actualloadedmodulus);
			loadmodulus(imodulus);
			long endtime = System.currentTimeMillis();
			System.out.println("  [" + endtime + "] End swaping modulus. " + 
				oldmod + " <-> " + m_actualloadedmodulus + ". " +
				(endtime-starttime)/1000 + " seconds. [Website.setPage]");
		}
		
		if(m_pages.size()<=iindex){
			for(int i=m_pages.size(); i<=iindex; i++){
				m_pages.add(null);
			}
		}
		
		m_pages.set(iindex, pag);
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
			Page page = Website.getPage(i);
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
		return m_urls.size()-1;
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
			Page pag = Website.getPage(i);
			int urlid = pag.getUrlIDusage();
			String urlname = pag.getUrlName();
			System.out.println(urlid + " " + urlname);
		}
	}
	
	/**
	 * Save.
	 */
	public static void save(){
		// save last pages
		Website.dumpToDisk(true);
		
		// save all modules
		for(int i=0; i<=m_nomodulus; i++){
			if(m_actualloadedmodulus!=i){
				savemodulus(m_actualloadedmodulus);
				loadmodulus(i);
				m_actualloadedmodulus = i;
			}
			savemodulus(i);
		}
	}
	
	/**
	 * Load.
	 */
	public static void load(){
		int i=0;
		m_nomodulus = 0;
		while(true){
			// check the file
			String filename = m_workdirectory + "/" + 
					"_" + i + "_" + m_basenamejavadata;
			File ffile= new File(filename);
			if(!ffile.exists()){break;}
			
			// load the data
			System.out.println("Loading: " + ffile.getName());
			loadmodulus(i);
			m_actualloadedmodulus = i;
			m_nomodulus = i;
			
			// load the m_urls array
			for(int j=0; j<m_pages.size(); j++){
				Page pa = m_pages.get(j);
				String url = pa.getFormatedUrlName();
				m_urls.add(url);
			}
			
			// update the next javaData file
			i++;
		}
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
		int mb = 1024*1024;
		int sizeInMegabytes = obj1s / mb; 
		return sizeInMegabytes;
	}
	
}
