package ehupatras.webrecommendation.structures;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.io.*;

public class Website {
		
	// work directory
	private static String m_workdirectory = ".";
	
	// HashTable to compute urlID from formatedURLs
	private static Hashtable<String,Page> m_url2idHT = new Hashtable<String,Page>();
	private static Hashtable<Integer,String> m_ID2urlname = new Hashtable<Integer,String>();
	private static int m_urlID = 0;
	private static int m_maxUrlID = 0;
	
	
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
		Website.saveObject(m_url2idHT, "_1_Website.javaData");
		Website.saveObject(m_ID2urlname, "_2_Website.javaData");
	}
	
	private static void saveObject(Object obj, String outfile){
		// Write to disk with FileOutputStream
		FileOutputStream f_out = null;
		try{
			f_out = new FileOutputStream(m_workdirectory + "/" + outfile);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.Website.save] " +
					"Problems at opening the file: " + outfile + " to write.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
			
		// Write object with ObjectOutputStream
		// Write object out to disk
		ObjectOutputStream obj_out = null;
		try{
			obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject( obj );
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.Website.save] " +
					"Problems at writing the file: " + outfile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		// close
		try{
			obj_out.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.Website.save] " +
					"Problems closing the file: " + outfile + " after writing.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	public static void load(){
		m_url2idHT = (Hashtable<String,Page>)loadObject(m_workdirectory + "/_1_Website.javaData");
		m_ID2urlname = (Hashtable<Integer,String>)loadObject(m_workdirectory + "/_2_Website.javaData");
		
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
	
	private static Object loadObject(String outputfilename){
		Object obj = null;
		
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(outputfilename);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.Website.loadObject] " +
					"Problems at opening the file: " + outputfilename + " to read.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(fis);
			obj = ois.readObject();
		} catch(IOException ex){
			
		} catch(ClassNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.Website.loadObject] " +
					"Problems at reading the file: " + outputfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		try{
			ois.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.Website.loadObject] " +
					"Problems closing the file: " + outputfilename + " after reading.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		return obj;
	}
	
	public static void setWorkDirectory(String workdirectory){
		m_workdirectory = workdirectory;
	}

}
