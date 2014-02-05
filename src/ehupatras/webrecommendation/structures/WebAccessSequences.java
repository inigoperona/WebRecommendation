package ehupatras.webrecommendation.structures;

import ehupatras.webrecommendation.structures.RequestBidasoaTurismo;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

public class WebAccessSequences {

	// The directory where we are working
	private static String m_workdirectory = "-";
	
	// HashTable to compute userID from the IP address
	public static Hashtable<String,Integer> m_ip2idHT = new Hashtable<String,Integer>();
		
	// HashTable to compute urlID from formatedURLs
	public static Hashtable<String,Integer> m_url2idHT = new Hashtable<String,Integer>();	
	
	// The filtered/good requests from the log
	private static ArrayList<RequestBidasoaTurismo> m_filterlog = new ArrayList<RequestBidasoaTurismo>();
	private static int m_actualloadedrequest = 0;
	private static int m_lastloadedrequest = 0;
	private static int m_maxloadrequests = 100000;
	private static int m_writedmodulus = 0;
	private static int m_actualloadedmodulus = 0;
	private static String m_basenamejavadata = "requests.javaData";
	
	// The sequences we are going to use to link prediction
	public static Hashtable<Integer,ArrayList<Integer>> m_sequences = new Hashtable<Integer,ArrayList<Integer>>();
	
	// private constructor
	private WebAccessSequences(){
		
	}
	
	public static void addRequest(RequestBidasoaTurismo req) {
		// load the last modulus to add if we do not have already loaded
		if(m_writedmodulus>m_actualloadedmodulus){
			long starttime = System.currentTimeMillis();
			savemodulus(m_actualloadedmodulus);
			loadmodulus(m_writedmodulus);
			long endtime = System.currentTimeMillis();
			System.out.println("  [" + endtime + "] End swaping modulus. " + 
					(endtime-starttime)/1000 + " seconds. [addRequest]");
		}
		if(m_actualloadedrequest>=m_maxloadrequests){
			// dump: save the requests we have loaded until now
			long starttime = System.currentTimeMillis();
			savemodulus(m_writedmodulus);
			// verbose
			long endtime = System.currentTimeMillis();
			System.out.println("  [" + endtime + "] End dump the modulus " + m_writedmodulus +
					" to the hard drive. " + (endtime-starttime)/1000 + " seconds.");
			// update counters
			m_writedmodulus++;
			m_actualloadedmodulus++;
			// initialize parameters
			m_actualloadedrequest = 0;
			m_filterlog = new ArrayList<RequestBidasoaTurismo>();
			System.gc();
		}
		m_filterlog.add(req);
		m_actualloadedrequest++;
		// save the last modulus last index
		m_lastloadedrequest = m_actualloadedrequest;
	}
	
	public static RequestBidasoaTurismo getRequest(int i) {
		int imodulus = i / m_maxloadrequests;
		int iindex = i % m_maxloadrequests;
		if(m_actualloadedmodulus!=imodulus){
			long starttime = System.currentTimeMillis();
			int oldmod = m_actualloadedmodulus;
			savemodulus(m_actualloadedmodulus);
			loadmodulus(imodulus);
			long endtime = System.currentTimeMillis();
			System.out.println("  [" + endtime + "] End swaping modulus. " +
					oldmod + " <-> " + m_actualloadedmodulus + ". " +
					(endtime-starttime)/1000 + " seconds. [getRequest]");
		}
		return m_filterlog.get(iindex);
	}
	
	public static void replaceRequest(int i, RequestBidasoaTurismo req){
		int imodulus = i / m_maxloadrequests;
		int iindex = i % m_maxloadrequests;
		if(m_actualloadedmodulus!=imodulus){
			long starttime = System.currentTimeMillis();
			int oldmod = m_actualloadedmodulus;
			savemodulus(m_actualloadedmodulus);
			loadmodulus(imodulus);
			long endtime = System.currentTimeMillis();
			System.out.println("  [" + endtime + "] End swaping modulus. " + 
					oldmod + " <-> " + m_actualloadedmodulus + ". " +
					(endtime-starttime)/1000 + " seconds. [replaceRequest]");
		}
		m_filterlog.remove(iindex);
		m_filterlog.add(iindex, req);
	}
	
	private static void loadmodulus(int mod){
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
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(fis);
			m_filterlog = (ArrayList<RequestBidasoaTurismo>)ois.readObject();
		} catch(IOException ex){
			
		} catch(ClassNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.loadmodulus] " +
					"Problems at reading the file: " + outputfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
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
	
	private static void savemodulus(int mod){
		String outputfilename = "_" + mod + "_" + m_basenamejavadata;
		// open the file
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(m_workdirectory + "/" + outputfilename);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.savemodulus] " +
				"Problems at opening the file: " + outputfilename + " to write.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		// write in the file
		ObjectOutputStream oos = null;
		try{
			oos = new ObjectOutputStream(fos);
			oos.writeObject(m_filterlog);
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.savemodulus] " +
				"Problems at writing in the file: " + outputfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		// close the file
		try{
			oos.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.savemodulus] " +
				"Problems at closing the file: " + outputfilename + " after writing.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	public static int filteredlogsize() {
		return m_writedmodulus*m_maxloadrequests + m_lastloadedrequest;
	}
	
	public static void writeFilteredLog(String outfilename){
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeFilteredLog] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Write in a file line by line
		try{
			writer.write(RequestBidasoaTurismo.toStringLongHeader() + "\n");
			for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
				RequestBidasoaTurismo req = WebAccessSequences.getRequest(i);
				writer.write(req.toStringLong() + "\n");
			}
			System.out.println("  " + filteredlogsize() + " lines have been written.");
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeFilteredLog] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeFilteredLog] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	public static void writeSequences(String outfilename){
		Enumeration<Integer> keys = m_sequences.keys();
		
		// order the keys
		ArrayList<Integer> keysOrd = new ArrayList<Integer>();
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement().intValue();
			int i;
			for(i=0; i<keysOrd.size(); i++){
				int sessionID2 = keysOrd.get(i);
				if(sessionID<=sessionID2){
					break;
				}
			}
			keysOrd.add(i, sessionID);
		}
		
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Write the sequences in a file line by line
		try{
			for(int i=0; i<keysOrd.size(); i++){
				int sessionID = keysOrd.get(i).intValue();
				ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
				writer.write(String.valueOf(sessionID)); // write the session identification
				for(int j=0; j<sequence.size(); j++){
					int urlindex = sequence.get(j);
					writer.write("," + urlindex);
				}
				writer.write("\n");
			}
			System.out.println("  " + keysOrd.size() + " lines have been written.");
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeFilteredLog] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
	}
	
	public static void setWorkDirectory(String workdirectory){
		m_workdirectory = workdirectory;
	}
	
	public static void loadStructure(){
		
	}
	
}
