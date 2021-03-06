package ehupatras.webrecommendation.usage.preprocess.log;

import ehupatras.webrecommendation.structures.WebAccess;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.structures.page.Page;
import ehupatras.webrecommendation.structures.page.PageBidasoaTurismo;
import ehupatras.webrecommendation.structures.request.Request;
import ehupatras.webrecommendation.structures.request.RequestBidasoaTurismo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;

// TODO: Auto-generated Javadoc
/**
 * The Class LogReaderBidasoaTurismo.
 */
public class LogReaderBidasoaTurismo extends LogReader {

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
			System.err.println("[ehupatras.webrecommendation.preprocess.log.LogReaderBidasoaTurismo] " +
					"Not file found: " + logfilenamei);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Read the given file line by line
		String line = null;
		try{
			while ((line = reader.readLine()) != null) {
				// split the request line
				String[] lineA = line.split(" ");
				if(lineA.length<9){ continue; }
				
				// the IP address field
				String ip = lineA[0];
				if(!m_ip2idHT.containsKey(ip)){
					ipID++;
					m_ip2idHT.put(ip, new Integer(ipID));
				}
				
				// Two not interesting fields.
				//String field2 = lineA[1];
				//String field3 = lineA[2];
				
				// time field has special separator ([...])
				int i = 3;
				String time = "";
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
				String method = urlrequest2A[0];
				Page page = new PageBidasoaTurismo(urlrequest2A[1]);
				String protocol = urlrequest2A[2];
				
				// status field
				i++;
				int status = Integer.valueOf(lineA[i]).intValue();
				
				// size of the request in bytes
				i++;
				String reqsize = lineA[i];
				
				// reference has special separator ("...")
				i++;
				String reference = "";
				while(true){
					reference = reference + " " + lineA[i];
					if(lineA[i].endsWith("\"")){ break; }
					i++;
				}
				
				// user agent has special separator ("...")
				i++;
				String useragent = "";
				while(true){
					useragent = useragent + " " + lineA[i];
					if(lineA[i].endsWith("\"")){ break; }
					i++;
				}
				
				// Create a request object
				String ipIDstr = String.valueOf(m_ip2idHT.get(ip));
				String logIDstr = String.format("%04d", h); 
				String userIDstr = ipIDstr + logIDstr;  
				Request req = new RequestBidasoaTurismo(ip, userIDstr, time, 
						method, page, protocol, status, 
						reqsize, reference, useragent,
						h);
				
				// if it is valid request, store it
				if(req.getIsValid() && page.getIsValid()){
					// Store the page
					Website.storeURL(page);
					// save the valid requests
					WebAccess.addRequest(req);
				}
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.preprocess.log.LogReaderBidasoaTurismo] " +
					"Problems reading from the file: " + logfilenamei);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			reader.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.preprocess.log.LogReaderBidasoaTurismo] " +
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
