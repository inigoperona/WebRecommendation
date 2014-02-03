package ehupatras.webrecommendation.structures;

import ehupatras.webrecommendation.structures.Request;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class WebAccessSequences {

	// HashTable to compute userID from the IP address
	public static Hashtable<String,Integer> m_ip2idHT = new Hashtable<String,Integer>();
		
	// HashTable to compute urlID from formatedURLs
	public static Hashtable<String,Integer> m_url2idHT = new Hashtable<String,Integer>();	
	
	// The filtered/good requests from the log
	public static List<Request> m_filterlog = new ArrayList<Request>();
	
	// private constructor
	private WebAccessSequences(){
		
	}
	
	public static void writeFilteredLog(String outfilename){
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Write in a file line by line
		try{
			writer.write(Request.toStringLongHeader() + "\n");
			for(int i=0; i<m_filterlog.size(); i++){
				Request req = WebAccessSequences.m_filterlog.get(i);
				writer.write(req.toStringLong() + "\n");
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
}
