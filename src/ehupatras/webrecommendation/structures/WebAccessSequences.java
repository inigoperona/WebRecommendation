package ehupatras.webrecommendation.structures;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import ehupatras.webrecommendation.utils.SaveLoadObjects;

public class WebAccessSequences {

	// The sequences we are going to use to link prediction
	// sessionID1: req1, req2, req3
	/** The m_sequences. */
	private static HashMap<String,ArrayList<Integer>> m_sequencesDATA = new HashMap<String,ArrayList<Integer>>();
	protected static ArrayList<String> m_sequencesID = new ArrayList<String>();
	
	// valideness
	private static HashMap<String,Float> m_validnessOfSequences = new HashMap<String,Float>();
	
	/** The m_seqfilename. */
	private static String m_seqfilename = "_sequences.javaData";
	private static String m_seqIDfilename = "_sequencesIDs.javaData";
	
	
	
	
	// FUNCTIONS
	
	public static boolean containsSession(String sessionIDstr){
		if(m_sequencesDATA.containsKey(sessionIDstr)){
			return true;
		} else {
			return false;
		}
	}
	
	public static ArrayList<Integer> getSession(String sessionIDstr){
		return m_sequencesDATA.get(sessionIDstr);
	}
	
	public static void addSession(String sessionIDstr, ArrayList<Integer> sequence){
		m_sequencesDATA.put(sessionIDstr,sequence);
		m_sequencesID.add(sessionIDstr);
	}
	
	public static void setSession(String sessionIDstr, ArrayList<Integer> sequence){
		m_sequencesDATA.put(sessionIDstr, sequence);
	}
	
	public static void putSession(String sessionIDstr, ArrayList<Integer> sequence){
		if(m_sequencesDATA.containsKey(sessionIDstr)){
			m_sequencesDATA.put(sessionIDstr, sequence);
		} else {
			WebAccessSequences.addSession(sessionIDstr,sequence);
		}
	}
	
	public static ArrayList<String> getSessionsIDs(){
		return m_sequencesID;
	}
	
	public static void removeSession(String sessionIDstr){
		m_sequencesDATA.remove(sessionIDstr);
		m_sequencesID.remove(sessionIDstr);
	}
	
	public static int getNumberOfSessions(){
		return m_sequencesDATA.size();
	}
	
	public static void putValidness(String sessionIDstr, float p){
		m_validnessOfSequences.put(sessionIDstr, p);
	}
	
	public static float getValidness(String sessionIDstr){
		return m_validnessOfSequences.get(sessionIDstr);
	}
	
	/**
	 * Write sequences index.
	 *
	 * @param outfilename the outfilename
	 */
	public static void writeSequencesIndex(String outfilename){
		System.out.println("  [" + System.currentTimeMillis() + "] Start writing txt sequencesIndexes. ");
		
		// order the keys
		ArrayList<String> keysOrd = getSequencesIDs();
		
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
				String sessionID = keysOrd.get(i);
				ArrayList<Integer> sequence = WebAccessSequences.m_sequencesDATA.get(sessionID);
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
	/*
	public static ArrayList<String> orderHashtableKeys(Enumeration<String> keys){
		// order the keys
		ArrayList<String> keysOrd = new ArrayList<String>();
		while(keys.hasMoreElements()){
			String sessionID = keys.nextElement();
			BigInteger sessionIDBI = new BigInteger(sessionID);  
			int i;
			for(i=0; i<keysOrd.size(); i++){
				String sessionID2 = keysOrd.get(i);
				BigInteger sessionID2BI = new BigInteger(sessionID2);
				if(sessionIDBI.compareTo(sessionID2BI) <= 0){
					break;
				}
			}
			keysOrd.add(i, sessionID);
		}
		return keysOrd;
	}
	*/
	
	/**
	 * Gets the sequences i ds.
	 *
	 * @return the sequences i ds
	 */
	/*
	public static ArrayList<String> getSequencesIDs(){
		ArrayList<String> keysOrd = orderHashtableKeys(m_sequences.keys());
		return keysOrd;
	}
	*/
	
	public static ArrayList<String> getSequencesIDs(){
		return m_sequencesID;
	}
	
	public static void saveSequences(String seqfilename){
		m_seqfilename = seqfilename;
		WebAccessSequences.saveSequences();
	}
	
	/**
	 * Save sequences.
	 */
	public static void saveSequences(){
		String wd = WebAccess.getWorkDirectory();
		String outfile = wd + "/" + m_seqfilename;
		
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
			obj_out.writeObject( m_sequencesDATA );
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
		
		// save sequencesIDs
		WebAccessSequences.saveSequencesIDs();
	}
	
	private static void saveSequencesIDs(){
		String wd = WebAccess.getWorkDirectory();
		String outfile = wd + "/" + m_seqIDfilename;
		
		// Write to disk with FileOutputStream
		FileOutputStream f_out = null;
		try{
			f_out = new FileOutputStream(outfile);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.saveSequencesIDs] " +
					"Problems at opening the file: " + outfile + " to write.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
			
		// Write object with ObjectOutputStream
		// Write object out to disk
		ObjectOutputStream obj_out = null;
		try{
			obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject( m_sequencesID );
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.saveSequencesIDs] " +
					"Problems at writing the file: " + outfile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		// close
		try{
			obj_out.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.saveSequencesIDs] " +
					"Problems closing the file: " + outfile + " after writing.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	
	/**
	 * Load sequences.
	 */
	public static void loadSequences(){
		String wd = WebAccess.getWorkDirectory();
		String outputfilename = wd + "/" + m_seqfilename;
		
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
			m_sequencesDATA = (HashMap<String,ArrayList<Integer>>)ois.readObject();
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
		
		// load sequencesIDs
		WebAccessSequences.loadSequencesIDs();
	}
	
	private static void loadSequencesIDs(){
		String wd = WebAccess.getWorkDirectory();
		String outputfilename = wd + "/" + m_seqIDfilename;
		
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
			m_sequencesID = (ArrayList<String>)ois.readObject();
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
	 * Write validness.
	 *
	 * @param outfilename the outfilename
	 */
	public static void writeValidness(String outfilename){
		// order the keys
		ArrayList<String> keysOrd = getSequencesIDs();
		
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
				String sessionID = keysOrd.get(i);
				float prob = WebAccessSequences.m_validnessOfSequences.get(sessionID);
				writer.write(sessionID); // write the session identification
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
	
	
	public static int[] sesIDs2reqIndexList(ArrayList<String> sesIDs){
		System.out.println("  [" + System.currentTimeMillis() + "] Take all valid request indexes and sort.");
		ArrayList<Integer> reqindexes = new ArrayList<Integer>();

		for(int i=0; i<sesIDs.size(); i++){
			String sessionID = sesIDs.get(i);

			ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
			for(int j=0; j<sequence.size(); j++){
				int reqID = sequence.get(j);
			
				int pos = Collections.binarySearch(reqindexes, reqID);
				if(pos<0){
					pos = Math.abs(pos+1);
				} else {
					pos++;
				}
		
				reqindexes.add(pos, reqID);
			}
		}
		
		int[] reqindexesA = new int[reqindexes.size()];
		for(int i=0; i<reqindexes.size(); i++){
			reqindexesA[i] = reqindexes.get(i);
		}
		return reqindexesA;
	}

	
	public static void writeSequencesInstanciated(String outfilename){
		WebAccessSequencesUHC.writeSequencesInstanciated(outfilename, false);
	}
	
	public static ArrayList<String[]> getSequencesInstanciated(ArrayList<String> sessionIDs){
		return WebAccessSequencesUHC.getSequencesInstanciated(sessionIDs, false);
	}
	
	public static int getSize(){
		int mb = 1024*1024;
		SaveLoadObjects slo = new SaveLoadObjects();
		
		int sizeInBytes1 = slo.getSize(m_sequencesDATA);
		int sizeInMegabytes1 = sizeInBytes1 / mb;
		
		int sizeInBytes2 = slo.getSize(m_sequencesID);
		int sizeInMegabytes2 = sizeInBytes2 / mb;
		
		int sizeInBytes3 = slo.getSize(m_validnessOfSequences);
		int sizeInMegabytes3 = sizeInBytes3 / mb;
		
		int sum = sizeInMegabytes1 + sizeInMegabytes2 + sizeInMegabytes3;
		return sum;
	}
}
