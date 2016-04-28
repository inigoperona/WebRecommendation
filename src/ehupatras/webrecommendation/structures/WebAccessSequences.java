package ehupatras.webrecommendation.structures;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

public class WebAccessSequences {

	// The sequences we are going to use to link prediction
	// sessionID1: req1, req2, req3
	/** The m_sequences. */
	private static Hashtable<String,ArrayList<Integer>> m_sequences = new Hashtable<String,ArrayList<Integer>>();
	
	private static ArrayList<String> m_sequencesID = new ArrayList<String>();
	
	/** The m_validness of sequences. */
	private static Hashtable<String,Float> m_validnessOfSequences = new Hashtable<String,Float>();
	
	/** The m_seqfilename. */
	private static String m_seqfilename = "_sequences.javaData";
	
	
	
	
	// FUNCTIONS
	
	public static boolean containsSession(String sessionIDstr){
		if(m_sequencesID.contains(sessionIDstr)){
			return true;
		}
		return false;
	}
	
	public static ArrayList<Integer> getSession(String sessionIDstr){
		return m_sequences.get(sessionIDstr);
	}
	
	public static void putSession(String sessionIDstr, ArrayList<Integer> sequence){
		m_sequences.put(sessionIDstr, sequence);
		if(!m_sequencesID.contains(sessionIDstr)){
			m_sequencesID.add(sessionIDstr);
		}
	}
	
	public static ArrayList<String> getSessionsIDs(){
		return m_sequencesID;
	}
	
	public static void removeSession(String sessionIDstr){
		m_sequences.remove(sessionIDstr);
		m_sequencesID.remove(sessionIDstr);
	}
	
	public static int getNumberOfSessions(){
		return m_sequencesID.size();
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
			m_sequences = (Hashtable<String,ArrayList<Integer>>)ois.readObject();
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
	
}
