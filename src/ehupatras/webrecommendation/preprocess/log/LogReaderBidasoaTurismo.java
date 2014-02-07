package ehupatras.webrecommendation.preprocess.log;

import ehupatras.webrecommendation.structures.Request;
import ehupatras.webrecommendation.structures.RequestBidasoaTurismo;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class LogReaderBidasoaTurismo extends LogReader {

	// log file to analyze
	private String[] m_logfilenamesA;

	// constructor
	public LogReaderBidasoaTurismo(){		
	}
	
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
			System.err.println("[ehupatras.webrecommendation.preprocess.log.LogReaderBidasoaTurismo] " +
					"Not file found: " + logfilenamei);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Read the given file line by line
		String line = null;
		int ipID = 0;
		int urlID = 0;
		try{
			while ((line = reader.readLine()) != null) {
				// split the request line
				String[] lineA = line.split(" ");
				if(lineA.length<9){ continue; }
				// the IP address field
				String ip = lineA[0];
				if(!WebAccessSequences.m_ip2idHT.containsKey(ip)){
					ipID++;
					WebAccessSequences.m_ip2idHT.put(ip, new Integer(ipID));
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
				// reference has special separator ("...")
				i++;
				String useragent = "";
				while(true){
					useragent = useragent + " " + lineA[i];
					if(lineA[i].endsWith("\"")){ break; }
					i++;
				}	
				
				// Create a request object
				Request req = new RequestBidasoaTurismo(ip, ipID, time, urlrequest, status, 
						reqsize, reference, useragent);
				
				// if it is valid request, store it
				if(req.getIsValid()){
					// Give ID number to each URL
					String formatedurlname = req.getFormatedUrlName();
					if(!WebAccessSequences.m_url2idHT.containsKey(formatedurlname)){
						urlID++;
						WebAccessSequences.m_url2idHT.put(formatedurlname, new Integer(urlID));
					}
					req.setUrlIDusage(WebAccessSequences.m_url2idHT.get(formatedurlname).intValue());
					
					// save the valid requests
					WebAccessSequences.addRequest(req);
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
	}
	
}
