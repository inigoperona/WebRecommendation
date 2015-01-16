package angelu.webrecommendation.converter;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

// TODO: Auto-generated Javadoc
/**
 * The Class URLconverterUsaCon.
 */
public class URLconverterUsaCon {

	// ATTRIBUTES
	
	/** The m_usage ur ls. */
	private ArrayList<Integer> m_usageURLs = null;
	
	/** The m_content ur ls. */
	private ArrayList<Integer> m_contentURLs = null;
	
	/** The m_usage2content file. */
	private String m_usage2contentFile = "convert_UrlIDs_content2usage/usa2cont.csv";
	
	// CONSTRUCTOR
	
	/**
	 * Instantiates a new UR lconverter usa con.
	 *
	 * @param usage2contentFile the usage2content file
	 */
	public URLconverterUsaCon(String usage2contentFile){
		m_usage2contentFile = usage2contentFile;
		m_usageURLs = new ArrayList<Integer>();
		m_contentURLs = new ArrayList<Integer>();
		
		// read the usa2cont.csv file
		BufferedReader br = null;
		
		try{
			br = new BufferedReader(new FileReader(m_usage2contentFile));
		} catch (FileNotFoundException ex){
			System.err.println(
					"[angelu.webrecommendation.converter." +
					"URLconverterUsaCon.URLconverterUsaCon] " +
					"Problems opening the file: " + usage2contentFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		String sCurrentLine;
		try{
			while ((sCurrentLine = br.readLine()) != null) {
				String[] lineA = sCurrentLine.split(",");
				int usageURL = Integer.valueOf(lineA[0]);
				int contURL = Integer.valueOf(lineA[1]);
				m_usageURLs.add(usageURL);
				m_contentURLs.add(contURL);
			}
		} catch(IOException ex){
			System.err.println(
					"[angelu.webrecommendation.converter." +
					"URLconverterUsaCon.URLconverterUsaCon] " +
					"Problems reading the file: " + usage2contentFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		try{
			if(br!=null){br.close();}
		} catch (IOException ex){
			System.err.println(
					"[angelu.webrecommendation.converter." +
					"URLconverterUsaCon.URLconverterUsaCon] " +
					"Problems closing the file: " + usage2contentFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	// FUNCTIONS
	
	/**
	 * Gets the content url.
	 *
	 * @param usageURL the usage url
	 * @return the content url
	 */
	public int getContentURL(int usageURL){
		int contentURL = -1;
		int ind = m_usageURLs.indexOf(usageURL);
		if(ind!=-1){
			contentURL = m_contentURLs.get(ind);
		}
		return contentURL;
	}
	
	/**
	 * Gets the usage url.
	 *
	 * @param contentURL the content url
	 * @return the usage url
	 */
	public int getUsageURL(int contentURL){
		int usageURL = -1;
		int ind = m_contentURLs.indexOf(contentURL);
		if(ind!=-1){
			usageURL = m_usageURLs.get(ind);
		}
		return usageURL;
	}
	
	/**
	 * Gets the usage urls.
	 *
	 * @return the usage urls
	 */
	public ArrayList<Integer> getUsageUrls(){
		return m_usageURLs;
	}
	
	// MAIN
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]){
		URLconverterUsaCon urlconv = 
				new URLconverterUsaCon("convert_UrlIDs_content2usage/usa2cont.csv");
		
		System.out.println("Usage to Content");
		for(int i=0; i<100; i++){
			System.out.println(i + "," + urlconv.getContentURL(i));
		}
		
		System.out.println("Content to Usage");
		for(int i=0; i<100; i++){
			System.out.println(i + "," + urlconv.getUsageURL(i));
		}
	}
	
}
