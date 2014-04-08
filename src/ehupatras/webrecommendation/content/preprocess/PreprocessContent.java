package ehupatras.webrecommendation.content.preprocess;

import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.structures.Page;
import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.net.MalformedURLException;
import java.io.IOException;

public class PreprocessContent {

	private ArrayList<String> m_urlnames = new ArrayList<String>();
	private ArrayList<Integer> m_urlID = new ArrayList<Integer>();
	
	public void pickupURLsToDownload(){
		String[] urlnames = Website.getAllFormatedUrlNames();
		for(int i=0; i<urlnames.length; i++){
			String formurl = urlnames[i];
			Page pag = Website.getPage(formurl);
			String url = pag.getUrlName();
			int urlID = pag.getUrlIDusage();
			if(pag.getIsSuitableToLinkPrediction()){
				m_urlnames.add(url);
				m_urlID.add(urlID);
			}
		}
		
	}
	
	
	private void downloadURL(String readURL, String writeURL){
		try {
			// get URL content
			URL url = new URL(readURL);
			URLConnection conn = url.openConnection();
			
			// open the stream and put it into BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			// save to this filename
			File file = new File(writeURL);
			if (!file.exists()) { file.createNewFile();}
			
			// use FileWriter to write file
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				bw.write(inputLine);
			}
			bw.close();
			br.close();
			
			System.out.println(" Done: " + readURL);
			
		} catch(MalformedURLException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	
	public void printURLs(){
		for(int i=0; i<m_urlnames.size(); i++){
			String url = m_urlnames.get(i);
			int urlID = m_urlID.get(i);
			System.out.println(i + " " + urlID + " " + url);
			this.downloadURL("http://www." + url, 
					"/home/burdinadar/down" + i + ".html");
			//Thread.sleep(3000);
		}
	}
	
}
