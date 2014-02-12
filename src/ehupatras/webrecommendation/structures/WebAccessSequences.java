package ehupatras.webrecommendation.structures;

import ehupatras.webrecommendation.content.preprocess.pagerank.GooglePageRank;
import ehupatras.webrecommendation.structures.Request;
import java.io.*;
import java.util.*;

public class WebAccessSequences {

	// The directory where we are working
	private static String m_workdirectory = "-";
			
	// HashTable to compute urlID from formatedURLs
	private static Hashtable<String,Page> m_url2idHT = new Hashtable<String,Page>();	
	
	// The filtered/good requests from the log
	private static ArrayList<Request> m_filterlog = new ArrayList<Request>();
	private static int m_actualloadedrequest = 0;
	private static int m_lastloadedrequest = 0;
	private static int m_maxloadrequests = 100000;
	private static int m_writedmodulus = 0;
	private static int m_actualloadedmodulus = 0;
	private static String m_basenamejavadata = "requests.javaData";
	
	// The sequences we are going to use to link prediction
	// sessionID1: req1, req2, req3
	public static Hashtable<Integer,ArrayList<Integer>> m_sequences = new Hashtable<Integer,ArrayList<Integer>>();
	
	// private constructor
	private WebAccessSequences(){
	}
	
	
	// Requests related functions
	
	public static void addRequest(Request req) {
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
			m_filterlog = new ArrayList<Request>();
			System.gc();
		}
		m_filterlog.add(req);
		m_actualloadedrequest++;
		// save the last modulus last index
		m_lastloadedrequest = m_actualloadedrequest;
	}
	
	public static Request getRequest(int i) {
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
	
	public static void replaceRequest(int i, Request req){
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
			m_filterlog = (ArrayList<Request>)ois.readObject();
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
			for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
				Request req = WebAccessSequences.getRequest(i);
				if(i==0){ writer.write("requestOrder " + req.toStringLongHeader() + "\n"); }
				writer.write(i + " " + req.toStringLong() + "\n");
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
		// order the keys
		ArrayList<Integer> keysOrd = getSequencesIDs();
		
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
	
	public static void writeSequences_URLwithUHC(String outfilename){
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences_URLwithUHC] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Write the sequences in a file line by line
		try{
			// order the keys
			ArrayList<Integer> keysOrd = getSequencesIDs();			
			ArrayList<String[]> sequences = getSequences_URLwithUHC(keysOrd);
			
			for(int i=0; i<sequences.size(); i++){
				int sessionID = keysOrd.get(i).intValue();
				writer.write(String.valueOf(sessionID));
				String[] sequence = sequences.get(i);
				for(int j=0; j<sequence.length; j++){
					writer.write("," + sequence[j]);
				}
				writer.write("\n");
			}
			System.out.println("  " + keysOrd.size() + " lines have been written.");
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences_URLwithUHC] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences_URLwithUHC] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	public static ArrayList<String[]> getSequences_URLwithUHC(ArrayList<Integer> sessionIDs){
		// First ordered all requests we need
		ArrayList<Integer> requestIDs = new ArrayList<Integer>();
		for(int i=0; i<sessionIDs.size(); i++){
			int sessionID = sessionIDs.get(i).intValue();
			ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
			for(int j=0; j<sequence.size(); j++){
				int reqind = sequence.get(j).intValue();
				int k;
				for(k=0; k<requestIDs.size(); k++){
					int ireqind = requestIDs.get(k);
					if(reqind<ireqind){
						break;
					}
				}
				requestIDs.add(k, reqind);
			}
		}
		
		// Access to the information we need
		Hashtable<Integer,String> requestsInfo = new Hashtable<Integer,String>();
		for(int i=0; i<requestIDs.size(); i++){
			int reqind = requestIDs.get(i);
			Request req = WebAccessSequences.getRequest(reqind);
			int urlid = req.getUrlIDusage();
			String pagrole = req.getPageRoleUHC();
			String seqelem = String.format("%06d%s", urlid, pagrole);
			requestsInfo.put(reqind, seqelem);
		}
		
		// create the structure of sequences to return
		ArrayList<String[]> resultSequences = new ArrayList<String[]>();
		for(int i=0; i<sessionIDs.size(); i++){
			int sessionID = sessionIDs.get(i).intValue();
			ArrayList<Integer> sequenceReqInd = WebAccessSequences.m_sequences.get(sessionID);
			String[] sequenceUHC = new String[sequenceReqInd.size()];
			for(int j=0; j<sequenceReqInd.size(); j++){
				int reqind = sequenceReqInd.get(j).intValue();
				sequenceUHC[j] = requestsInfo.get(reqind);
			}
			resultSequences.add(sequenceUHC);
		}
		return resultSequences;
	}
	
	public static ArrayList<Integer> orderHashtableKeys(Enumeration<Integer> keys){
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
		return keysOrd;
	}
	
	public static ArrayList<Integer> getSequencesIDs(){
		ArrayList<Integer> keysOrd = orderHashtableKeys(m_sequences.keys());
		return keysOrd;
	}
	
	public static void setWorkDirectory(String workdirectory){
		m_workdirectory = workdirectory;
	}
	
	public static void loadStructure(){
		int i=0;
		while(true){
			String filename = m_workdirectory + "/" + 
					"_" + i + "_" + m_basenamejavadata;
			File ffile= new File(filename);
			if(!ffile.exists()){break;}
			// load the data
			System.out.println("Loading: " + ffile.getName());
			loadmodulus(i);
			// update the next javaData file
			i++;
		}
	}
	
	
	// The operations related to a set of URLs
	
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
	
}
