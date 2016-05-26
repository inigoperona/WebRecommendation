package ehupatras.webrecommendation.usage.preprocess.log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

import ehupatras.webrecommendation.structures.WebAccess;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.structures.page.Page;
import ehupatras.webrecommendation.structures.page.PageGipuzkoa_eus;
import ehupatras.webrecommendation.structures.request.Request;
import ehupatras.webrecommendation.structures.request.RequestGipuzkoa_eus;
import ehupatras.webrecommendation.utils.SaveLoadObjects;

public class LogReaderGipuzkoa_eus extends LogReader {

	// HashTable to compute userID from the IP address
	/** The m_ip2id ht. */
	private Hashtable<String,Integer> m_ip2idHT = new Hashtable<String,Integer>();
		
	// log file to analyze
	/** The m_logfilenames a. */
	private String[] m_logfilenamesA;

	// read the log file and save the attributes we need
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.usage.preprocess.log.LogReader#readLogFile(java.lang.String[])
	 */
	public void readLogFile(String[] logfilenamesA){
		int ipID = 0;
		int indSize = 0;
		
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
		try{
			while ((line = reader.readLine()) != null) {
				String ip = "-";
				String time = "-";
				String method = "-";
				Page page = null;
				String protocol = "-";
				int status = 0;
				String reqsize = "0";
				String reference = "-";
				String useragent = "-";
				
				try{
				// split the request line
				String[] lineA = line.split(" ");
				if(lineA.length<12){ continue; }
				
				// the fields
				ip = lineA[0];
				if(!m_ip2idHT.containsKey(ip)){
					ipID++;
					m_ip2idHT.put(ip, new Integer(ipID));
				}
				
				// Two not interesting fields.
				//String field2 = lineA[1];
				//String field3 = lineA[2];
				
				// time field has special separator ([...])
				int i = 3;
				time = "";
				while(true){
					time = time + " " + lineA[i];
					if(lineA[i].endsWith("]")){ break; }
					i++;
				}
				
				// urlrequest field has special separator ("...")
				i++;
				String urlrequest = "";
				while(true){
					urlrequest = urlrequest + " " + lineA[i];
					if(!lineA[i].endsWith("\\\"") && lineA[i].endsWith("\"")){ break; }
					i++;
				}
				// URL request line
				String urlrequest2 = urlrequest.substring(2, urlrequest.length()-1);
				String[] urlrequest2A = urlrequest2.split(" ");
				method = urlrequest2A[0];
				page = new PageGipuzkoa_eus(urlrequest2A[1]); // Create page
				protocol = urlrequest2A[2];
				
				// status field
				i++;
				status = Integer.valueOf(lineA[i]).intValue();
				
				// size of the request in bytes
				i++;
				reqsize = lineA[i];
				
				// reference has special separator ("...")
				i++;
				reference = "";
				while(true){
					reference = reference + " " + lineA[i];
					if(lineA[i].endsWith("\"")){ break; }
					i++;
				}
				
				// user agent has special separator ("...")
				i++;
				useragent = "";
				while(true){
					useragent = useragent + " " + lineA[i];
					if(lineA[i].endsWith("\"")){ break; }
					i++;
				}	
				
				} catch(Exception ex){
					continue;
				}
				
				// Create a request object
				String ipIDstr = String.valueOf(m_ip2idHT.get(ip));
				String logIDstr = String.format("%04d", h); 
				String userIDstr = ipIDstr + logIDstr;  
				Request req = new RequestGipuzkoa_eus(ip, userIDstr, 
						time, method, page, protocol, status, 
						reqsize, reference, useragent,
						h);
				
				// if it is valid request, store it
				if(req.getIsValid()){
					// Store the page
					Website.storeURL(page);
					// save the valid requests
					WebAccess.addRequest(req);
					
					// print the size of the structures
					if(indSize%50000000==0){
						int mb = 1024*1024;
						// total memory
						Runtime runtime = Runtime.getRuntime();
						long tm = runtime.totalMemory();
						long fm = runtime.freeMemory();
						int um = (int)((tm-fm)/(long)mb);
						// big structures
						//int sizeWA = WebAccess.getSize();
						int sizeWS = Website.getSizeMB();
						// size of IP to ID
						SaveLoadObjects slo = new SaveLoadObjects(); 
						int sizeInBytes1 = slo.getSize(m_ip2idHT);
						int sizeInMegabytes1 = sizeInBytes1 / mb; 
						// print
						//int sum = sizeWA + sizeWS + sizeInMegabytes1;
						System.out.println(
								"    UsedMemory(MB): " + um +
								  "; Website(MB): " + sizeWS + 
								  "; IP2ID(MB): " + sizeInMegabytes1);
					}
					indSize++;
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
		WebAccess.orderRequests();
	}
	
}
