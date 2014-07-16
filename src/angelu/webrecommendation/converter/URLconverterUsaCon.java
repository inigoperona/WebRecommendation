package angelu.webrecommendation.converter;

import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class URLconverterUsaCon {

	// ATTRIBUTES
	
	private ArrayList<Integer> m_usageURLs = null;
	private ArrayList<Integer> m_contentURLs = null;
	
	private String m_usage2contentFile = "convert_UrlIDs_content2usage/usa2cont.csv";
	
	// CONSTRUCTOR
	
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
	
	public int getContentURL(int usageURL){
		int contentURL = -1;
		int ind = m_usageURLs.indexOf(usageURL);
		if(ind!=-1){
			contentURL = m_contentURLs.get(ind);
		}
		return contentURL;
	}
	
	public int getUsageURL(int contentURL){
		int usageURL = -1;
		int ind = m_contentURLs.indexOf(contentURL);
		if(ind!=-1){
			usageURL = m_usageURLs.get(ind);
		}
		return usageURL;
	}
	
	// MAIN
	
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
