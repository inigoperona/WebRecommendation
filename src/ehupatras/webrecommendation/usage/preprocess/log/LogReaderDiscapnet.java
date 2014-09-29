package ehupatras.webrecommendation.usage.preprocess.log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.structures.page.Page;
import ehupatras.webrecommendation.structures.page.PageDiscapnet;
import ehupatras.webrecommendation.structures.request.Request;
import ehupatras.webrecommendation.structures.request.RequestDiscapnet;

public class LogReaderDiscapnet extends LogReader {

	// HashTable to compute userID from the IP address
	private Hashtable<String,Integer> m_ip2idHT = new Hashtable<String,Integer>();
		
	// log file to analyze
	private String[] m_logfilenamesA;

	// read the log file and save the attributes we need
	public void readLogFile(String[] logfilenamesA){
		m_logfilenamesA = logfilenamesA;
		for(int h=0; h<m_logfilenamesA.length; h++){
			String logfilenamei = m_logfilenamesA[h];
			System.out.println("  [" + System.currentTimeMillis() + 
					"] Reading the log file: " + logfilenamei);

		// Open the given file
		BufferedReader reader = null;
		try{
			reader = new BufferedReader(new FileReader(logfilenamei));
		} catch(FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.preprocess.log.LogReaderDiscapnet] " +
					"Not file found: " + logfilenamei);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Read the given file line by line
		String line = null;
		int ipID = 0;
		try{
			while ((line = reader.readLine()) != null) {
				// split the request line
				String[] lineA = line.split(" ");
				if(lineA.length<17){ continue; }
				
				// the fields
				String day = lineA[0];
				String timeInDay = lineA[1];
				String time = day + " " + timeInDay;
				
				String server_ip = lineA[2];
				String method = lineA[3];
				
				String urlname = lineA[4]; 
				String param = lineA[5];
				String urlname2 = urlname;
				if(!param.equals("-")){
					urlname2 = urlname2 + "?" + param;
				}
				
				String server_port = lineA[6];
				//String field7 = lineA[7];
				String ip = lineA[8];
				String useragent = lineA[9];
				//String field10 = lineA[10];
				//String field11 = lineA[11];
				String reference = lineA[12];
				
				String statuscodeStr = lineA[13];
				int statuscode = Integer.valueOf(statuscodeStr).intValue();
				
				String field14 = lineA[14];
				String field15 = lineA[15];
				String reqsize = lineA[16]; // size of the request in bytes
				//String field17 = lineA[17];
				//String field18 = lineA[18];
				
				
				// the IP address field
				if(!m_ip2idHT.containsKey(ip)){
					ipID++;
					m_ip2idHT.put(ip, new Integer(ipID));
				}
				
				// Create page
				Page page = new PageDiscapnet(urlname2);			
				
			
				// Create a request object
				Request req = new RequestDiscapnet(ip, ipID, time, 
						method, page, "-", statuscode, 
						reqsize, reference, useragent);
				
				// if it is valid request, store it
				if(req.getIsValid()){
					// Store the page
					Website.storeURL(page);
					// save the valid requests
					WebAccessSequences.addRequest(req);
				}
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.preprocess.log.LogReaderDiscapnet] " +
					"Problems reading from the file: " + logfilenamei);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			reader.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.preprocess.log.LogReaderDiscapnet] " +
					"Problems at closing the file: " + logfilenamei);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		} // for each log file
		
		
		// order the WebAccessSequences
		System.out.println("  [" + System.currentTimeMillis() + 
				"] Ordering the requests. ");
		WebAccessSequences.orderRequests();
	}
	
	
}
