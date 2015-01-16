package ehupatras.webrecommendation.structures;

import ehupatras.webrecommendation.structures.request.Request;

import java.io.*;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class WebAccessSequences.
 */
public class WebAccessSequences {

	// The directory where we are working
	/** The m_workdirectory. */
	private static String m_workdirectory = ".";
	
	// The requests container
	// 1
	/** The m_filterlog. */
	private static ArrayList<Request> m_filterlog = new ArrayList<Request>();
	
	/** The m_actualloadedmodulus. */
	private static int m_actualloadedmodulus = 0;
	// 2
	/** The m_filterlog2. */
	private static ArrayList<Request> m_filterlog2 = new ArrayList<Request>();
	
	/** The m_actualloadedmodulus2. */
	private static int m_actualloadedmodulus2 = 0;
	
	// Auxiliar ArrayList<Request> to read faster
	/** The m_n memory. */
	private static int m_nMemory = 6;
	
	/** The m_filterlog s. */
	private static ArrayList<ArrayList<Request>> m_filterlogS;
	
	/** The m_actualloadedmodulus s. */
	private static ArrayList<Integer> m_actualloadedmodulusS;
	
	// Necessary attributes when we are adding requests
	//private static int m_maxloadrequests = 10000;
	/** The m_maxloadrequests. */
	private static int m_maxloadrequests = 100000;
	// 1
	/** The m_actualloadedrequest. */
	private static int m_actualloadedrequest = 0;
	
	/** The m_lastloadedrequest. */
	private static int m_lastloadedrequest = 0;
	
	/** The m_writedmodulus. */
	private static int m_writedmodulus = 0;
	
	/** The m_basenamejavadata. */
	private static String m_basenamejavadata = "requests.javaData";
	// 2
	/** The m_actualloadedrequest2. */
	private static int m_actualloadedrequest2 = 0;
	
	/** The m_lastloadedrequest2. */
	private static int m_lastloadedrequest2 = 0;
	
	/** The m_writedmodulus2. */
	private static int m_writedmodulus2 = 0;
	
	/** The m_basenamejavadata2. */
	private static String m_basenamejavadata2 = "orderedrequests.javaData";
	
	// order of requests; long[0]: requests_index; long[1]: requests timestamp
	/** The m_ordered requests. */
	private static ArrayList<long[]> m_orderedRequests = new ArrayList<long[]>(); 
	
	// The sequences we are going to use to link prediction
	// sessionID1: req1, req2, req3
	/** The m_sequences. */
	public static Hashtable<Integer,ArrayList<Integer>> m_sequences = new Hashtable<Integer,ArrayList<Integer>>();
	
	/** The m_validness of sequences. */
	public static Hashtable<Integer,Float> m_validnessOfSequences = new Hashtable<Integer,Float>();
	
	/** The m_seqfilename. */
	private static String m_seqfilename = "_sequences.javaData";
	
	
	// protected constructor
	static {
		m_filterlogS = new ArrayList<ArrayList<Request>>();
		m_actualloadedmodulusS = new ArrayList<Integer>();
		for(int i=0; i<m_nMemory; i++){
			m_actualloadedmodulusS.add(-1);
			m_filterlogS.add(null);
		}
	}
	
	
	// to have more control in modulus change
	/**
	 * Gets the actual modulus.
	 *
	 * @return the actual modulus
	 */
	public static int getActualModulus(){
		return m_actualloadedmodulus;
	}
	
	/**
	 * Gets the modulus after get request.
	 *
	 * @param i the i
	 * @return the modulus after get request
	 */
	public static int getModulusAfterGetRequest(int i) {
		int imodulus = i / m_maxloadrequests;
		return imodulus;
	}
	
	
	
	// Requests related functions
	
	/**
	 * Adds the request.
	 *
	 * @param req the req
	 */
	public static void addRequest(Request req) {
		WebAccessSequences.addRequest(req, m_basenamejavadata);
	}
	
	/**
	 * Adds the request.
	 *
	 * @param req the req
	 * @param basenamejavadata the basenamejavadata
	 */
	public static void addRequest(Request req, String basenamejavadata) {
		// load the last modulus to add if we do not have already loaded
		if(m_writedmodulus>m_actualloadedmodulus){
			if(m_actualloadedmodulus==-1){m_actualloadedmodulus=0;}
			long starttime = System.currentTimeMillis();
			savemodulus(m_actualloadedmodulus, m_filterlog, basenamejavadata);
			loadmodulus(m_writedmodulus, basenamejavadata, true, false);
			long endtime = System.currentTimeMillis();
			System.out.println("  [" + endtime + "] End swaping modulus. " + 
					(endtime-starttime)/1000 + " seconds. [addRequest]");
		}
		if(m_actualloadedrequest>=m_maxloadrequests){
			// dump: save the requests we have loaded until now
			long starttime = System.currentTimeMillis();
			savemodulus(m_writedmodulus, m_filterlog, basenamejavadata);
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
	
	/**
	 * Adds the request2.
	 *
	 * @param req the req
	 * @param basenamejavadata the basenamejavadata
	 */
	public static void addRequest2(Request req, String basenamejavadata) {
		// load the last modulus to add if we do not have already loaded
		if(m_writedmodulus2>m_actualloadedmodulus2){
			if(m_actualloadedmodulus2==-1){m_actualloadedmodulus2=0;}
			long starttime = System.currentTimeMillis();
			savemodulus(m_actualloadedmodulus2, m_filterlog2, basenamejavadata);
			loadmodulus(m_writedmodulus2, basenamejavadata, true, true);
			long endtime = System.currentTimeMillis();
			System.out.println("  [" + endtime + "] End swaping modulus. " + 
					(endtime-starttime)/1000 + " seconds. [addRequest2]");
		}
		if(m_actualloadedrequest2>=m_maxloadrequests){
			// dump: save the requests we have loaded until now
			long starttime = System.currentTimeMillis();
			savemodulus(m_writedmodulus2, m_filterlog2, basenamejavadata);
			// verbose
			long endtime = System.currentTimeMillis();
			System.out.println("  [" + endtime + "] End dump the modulus " + m_writedmodulus2 +
					" to the hard drive. " + (endtime-starttime)/1000 + " seconds. [addRequest2]");
			// update counters
			m_writedmodulus2++;
			m_actualloadedmodulus2++;
			// initialize parameters
			m_actualloadedrequest2 = 0;
			m_filterlog2 = new ArrayList<Request>();
			System.gc();
		}
		m_filterlog2.add(req);
		m_actualloadedrequest2++;
		// save the last modulus last index
		m_lastloadedrequest2 = m_actualloadedrequest2;
	}
	
	/**
	 * Gets the request.
	 *
	 * @param i the i
	 * @return the request
	 */
	public static Request getRequest(int i) {
		return WebAccessSequences.getRequest(i, m_basenamejavadata);
	}
	
	/**
	 * Gets the request.
	 *
	 * @param i the i
	 * @param basenamejavadata the basenamejavadata
	 * @return the request
	 */
	public static Request getRequest(int i, String basenamejavadata) {
		int imodulus = i / m_maxloadrequests;
		int iindex = i % m_maxloadrequests;
		
		// if we have in memory return it
		int iMem = m_actualloadedmodulusS.indexOf(new Integer(imodulus));
		if(iMem!=-1){
			return m_filterlogS.get(iMem).get(iindex);
		}
		
		// the main modulus
		if(m_actualloadedmodulus!=imodulus){
			long starttime = System.currentTimeMillis();
			int oldmod = m_actualloadedmodulus;
			savemodulus(m_actualloadedmodulus, m_filterlog, basenamejavadata);
			loadmodulus(imodulus, basenamejavadata, false, false);
			long endtime = System.currentTimeMillis();
			String inmem = "(" + m_actualloadedmodulus + ")";
			for(int j=0; j<m_actualloadedmodulusS.size(); j++){inmem=inmem+","+m_actualloadedmodulusS.get(j);}
			System.out.println("  [" + endtime + "] End swaping modulus. " +
					oldmod + " <-> " + m_actualloadedmodulus + ". " +
					inmem + ". " +
					(endtime-starttime)/1000 + " seconds. [getRequest]");
		}
		return m_filterlog.get(iindex);
	}
	
	/**
	 * Replace request.
	 *
	 * @param i the i
	 * @param req the req
	 */
	public static void replaceRequest(int i, Request req){
		WebAccessSequences.replaceRequest(i, req, m_basenamejavadata);
	}
	
	/**
	 * Replace request.
	 *
	 * @param i the i
	 * @param req the req
	 * @param basenamejavadata the basenamejavadata
	 */
	public static void replaceRequest(int i, Request req, String basenamejavadata){
		int imodulus = i / m_maxloadrequests;
		int iindex = i % m_maxloadrequests;
		
		// if we have in memory aplly there
		int iMem = m_actualloadedmodulusS.indexOf(new Integer(imodulus));
		if(iMem!=-1){
			m_filterlogS.get(iMem).remove(iindex);
			m_filterlogS.get(iMem).add(iindex, req);
		} else {
			// the main modulus
			if(m_actualloadedmodulus!=imodulus){
				long starttime = System.currentTimeMillis();
				int oldmod = m_actualloadedmodulus;
				savemodulus(m_actualloadedmodulus, m_filterlog, basenamejavadata);
				loadmodulus(imodulus, basenamejavadata, false, false);
				long endtime = System.currentTimeMillis();
				System.out.println("  [" + endtime + "] End swaping modulus. " + 
					oldmod + " <-> " + m_actualloadedmodulus + ". " +
					(endtime-starttime)/1000 + " seconds. [replaceRequest]");
			}
			m_filterlog.remove(iindex);
			m_filterlog.add(iindex, req);
		}
	}
	
	/**
	 * Loadmodulus.
	 *
	 * @param mod the mod
	 * @param basenamejavadata the basenamejavadata
	 * @param isAddReq the is add req
	 * @param modeOrd the mode ord
	 */
	private static void loadmodulus(int mod, String basenamejavadata, boolean isAddReq, boolean modeOrd){
		if(!isAddReq){
		// save the loaded modulus in the memory
		int i1Mod = m_actualloadedmodulusS.get(0);
		if(i1Mod!=-1){
			// save the last element
			int lastIndexMem = m_nMemory-1;
			int modtosave = m_actualloadedmodulusS.get(lastIndexMem);
			if(modtosave!=-1){ // remove the first module in going out
				ArrayList<Request> filterlogAux = m_filterlogS.get(lastIndexMem);				
				savemodulus(modtosave, filterlogAux, basenamejavadata);
				m_actualloadedmodulusS.set(lastIndexMem, -1);
				m_filterlogS.set(lastIndexMem, null);
			}
			// move all other modulus in the memory
			for(int i2Mem=m_nMemory-2; i2Mem>=0; i2Mem--){
				int i2Mod = m_actualloadedmodulusS.get(i2Mem);
				if(i2Mod==-1){
					continue;
				} else {
					ArrayList<Request> filterlogAux2 = m_filterlogS.get(i2Mem);
					m_filterlogS.set(i2Mem+1, filterlogAux2);
					m_actualloadedmodulusS.set(i2Mem+1, i2Mod);
					m_filterlogS.set(i2Mem, null);
					m_actualloadedmodulusS.set(i2Mem, -1);
				}
			}
		}
		if(m_filterlog.size()>0){
			m_filterlogS.set(0, m_filterlog);
			m_actualloadedmodulusS.set(0, m_actualloadedmodulus);
		}
		} // if(!isAddReq)
		
		// load the modulus
		String outputfilename = "_" + mod + "_" + basenamejavadata;
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
			if(!modeOrd){
				m_filterlog = (ArrayList<Request>)ois.readObject();
			} else {
				m_filterlog2 = (ArrayList<Request>)ois.readObject();
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.loadmodulus] " +
					"Problems at reading the file: " + outputfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		} catch(ClassNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.loadmodulus] " +
					"Problems at casting to a specific object: " + outputfilename);
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
		if(!modeOrd){
			m_actualloadedmodulus = mod;
		} else {
			m_actualloadedmodulus2 = mod;
		}
	}
	
	/**
	 * Savemodulus.
	 *
	 * @param mod the mod
	 * @param filterlog the filterlog
	 * @param basenamejavadata the basenamejavadata
	 */
	private static void savemodulus(int mod, ArrayList<Request> filterlog, String basenamejavadata){
		if(mod!=-1){
		
		String outputfilename = "_" + mod + "_" + basenamejavadata;
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
			oos.writeObject(filterlog);
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
		
		} // end if
	}
	
	/**
	 * Filteredlogsize.
	 *
	 * @return the int
	 */
	public static int filteredlogsize() {
		return m_writedmodulus*m_maxloadrequests + m_lastloadedrequest;
	}
	
	/**
	 * Write filtered log.
	 *
	 * @param outfilename the outfilename
	 */
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
				Request req = WebAccessSequences.getRequest(i, m_basenamejavadata);
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
	
	/**
	 * Write sequences index.
	 *
	 * @param outfilename the outfilename
	 */
	public static void writeSequencesIndex(String outfilename){
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
	
	/**
	 * Order hashtable keys.
	 *
	 * @param keys the keys
	 * @return the array list
	 */
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
	
	/**
	 * Gets the sequences i ds.
	 *
	 * @return the sequences i ds
	 */
	public static ArrayList<Integer> getSequencesIDs(){
		ArrayList<Integer> keysOrd = orderHashtableKeys(m_sequences.keys());
		return keysOrd;
	}
	
	/**
	 * Sets the work directory.
	 *
	 * @param workdirectory the new work directory
	 */
	public static void setWorkDirectory(String workdirectory){
		m_workdirectory = workdirectory;
	}
	
	/**
	 * Save sequences.
	 */
	public static void saveSequences(){
		String outfile = m_workdirectory + "/" + m_seqfilename;
		
		// Write to disk with FileOutputStream
		FileOutputStream f_out = null;
		try{
			f_out = new FileOutputStream(outfile);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.saveSequences] " +
					"Problems at opening the file: " + outfile + " to write.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
			
		// Write object with ObjectOutputStream
		// Write object out to disk
		ObjectOutputStream obj_out = null;
		try{
			obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject( m_sequences );
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.saveSequences] " +
					"Problems at writing the file: " + outfile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		// close
		try{
			obj_out.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.saveSequences] " +
					"Problems closing the file: " + outfile + " after writing.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	/**
	 * Load sequences.
	 */
	public static void loadSequences(){
		String outputfilename = m_workdirectory + "/" + m_seqfilename;
		
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(outputfilename);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.Website.loadObject] " +
					"Problems at opening the file: " + outputfilename + " to read.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(fis);
			m_sequences = (Hashtable<Integer,ArrayList<Integer>>)ois.readObject();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.Website.loadObject] " +
					"Problems at reading the file: " + outputfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		} catch(ClassNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.Website.loadObject] " +
					"Problems at casting to a specific object: " + outputfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		try{
			ois.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.Website.loadObject] " +
					"Problems closing the file: " + outputfilename + " after reading.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	/**
	 * Load structure.
	 *
	 * @param basenamejavadata the basenamejavadata
	 */
	public static void loadStructure(String basenamejavadata){
		m_basenamejavadata = basenamejavadata;
		WebAccessSequences.loadStructure();
	}
	
	/**
	 * Load structure.
	 */
	public static void loadStructure(){
		int i=0;
		m_writedmodulus = 0;
		while(true){
			String filename = m_workdirectory + "/" + 
					"_" + i + "_" + m_basenamejavadata;
			File ffile= new File(filename);
			if(!ffile.exists()){break;}
			// load the data
			System.out.println("Loading: " + ffile.getName());
			loadmodulus(i, m_basenamejavadata, false, false);
			// update the pointers
			//m_actualloadedrequest = 0;
			m_lastloadedrequest = m_filterlog.size()-1;
			m_writedmodulus = i;
			// update the next javaData file
			i++;
		}
	}
	
	/**
	 * Save structure.
	 */
	public static void saveStructure(){
		// save all modulus in memory
		for(int i=0; i<m_nMemory; i++){
			int iMod = m_actualloadedmodulusS.get(i);
			if(iMod!=-1){
				ArrayList<Request> filterlog2 = m_filterlogS.get(i);
				savemodulus(iMod, filterlog2, m_basenamejavadata);
				m_actualloadedmodulusS.set(i, -1);
				m_filterlogS.set(i, null);
			}
		}
		
		// save all modulus
		for(int i=0; i<=m_writedmodulus; i++){
			if(m_actualloadedmodulus!=i){ // save the main modulus
				savemodulus(m_actualloadedmodulus, m_filterlog, m_basenamejavadata);
				loadmodulus(i, m_basenamejavadata, false, false);
				m_actualloadedmodulus = i;
			}
			savemodulus(i, m_filterlog, m_basenamejavadata);
			
			// update pointers
			m_lastloadedrequest = m_filterlog.size()-1;
		}
	}
	
	/**
	 * Save structure2.
	 */
	public static void saveStructure2(){
		// save all modulus
		for(int i=0; i<=m_writedmodulus2; i++){
			if(m_actualloadedmodulus2!=i){ // save the main modulus
				savemodulus(m_actualloadedmodulus2, m_filterlog2, m_basenamejavadata2);
				loadmodulus(i, m_basenamejavadata2, true, true);
				m_actualloadedmodulus2 = i;
			}
			savemodulus(i, m_filterlog2, m_basenamejavadata2);
			
			// update pointers
			m_lastloadedrequest2 = m_filterlog2.size()-1;
		}
	}
	
	/**
	 * Reset modulus.
	 */
	private static void resetModulus(){
		// reset all modulus
		for(int i=0; i<m_nMemory; i++){
			m_actualloadedmodulusS.set(i, -1);
			m_filterlogS.set(i, null);
		}
		// 1
		m_actualloadedmodulus = -1;
		m_filterlog = new ArrayList<Request>();
		m_actualloadedrequest = 0;
		// 2
		m_actualloadedmodulus2 = -1;
		m_filterlog2 = new ArrayList<Request>();
		m_actualloadedrequest2 = 0;
	}
	
	
	/**
	 * Write validness.
	 *
	 * @param outfilename the outfilename
	 */
	public static void writeValidness(String outfilename){
		// order the keys
		ArrayList<Integer> keysOrd = getSequencesIDs();
		
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeValidness] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Write the sequences in a file line by line
		try{
			for(int i=0; i<keysOrd.size(); i++){
				int sessionID = keysOrd.get(i).intValue();
				float prob = WebAccessSequences.m_validnessOfSequences.get(sessionID);
				writer.write(String.valueOf(sessionID)); // write the session identification
				writer.write(" " + String.valueOf(prob));
				writer.write("\n");
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeValidness] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeValidness] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
	}
	
	
	/**
	 * Order requests.
	 */
	public static void orderRequests(){
		WebAccessSequences.saveStructure();
		WebAccessSequences.orderRequestsInd();
		for(int i=0; i<m_orderedRequests.size(); i++){
			long[] obj = m_orderedRequests.get(i);
			int ind = (int)obj[0];
			Request req = WebAccessSequences.getRequest(ind, m_basenamejavadata);
			WebAccessSequences.addRequest2(req, m_basenamejavadata2);
		}
		WebAccessSequences.saveStructure2();
		m_basenamejavadata = m_basenamejavadata2;
		m_lastloadedrequest = m_lastloadedrequest2;
		WebAccessSequences.resetModulus();
	}
	
	/**
	 * Order requests ind.
	 */
	private static void orderRequestsInd(){
		m_orderedRequests = new ArrayList<long[]>();
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			// actual request
			Request req = WebAccessSequences.getRequest(i, m_basenamejavadata);
			long ind = (long)i;
			long time = req.getTimeInMillis();
			long[] obj = new long[2];
			obj[0] = ind;
			obj[1] = time;
			
			// fast approach to the position we need
			int k=1000;
			for(; k<m_orderedRequests.size(); k=k+1000){
				long[] obj2 = m_orderedRequests.get(k);
				long time2 = obj2[1];
				if(time<time2){
					break;
				}
			}
			
			// it has to be in the position
			int j=k-1000;
			for(; j<m_orderedRequests.size(); j++){
				long[] obj2 = m_orderedRequests.get(j);
				long time2 = obj2[1];
				if(time<time2){
					break;
				}
			}
			m_orderedRequests.add(j, obj);
		}
	}
	
}
