package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.modelvalidation.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class A001MainClassCreateDatabase.
 */
public class A001MainClassCreateDatabase {

	/** The m_sessions i ds. */
	private ArrayList<String> m_sessionsIDs;
	
	/** The m_sequences. */
	private ArrayList<String[]> m_sequences;

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {

		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadLogs();
		param.createDatabase();
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
	
	
	
	
	
	/**
	 * Creates the database.
	 *
	 * @param databaseWD the database wd
	 */
	public void createDatabase(String databaseWD, int sizeDB){
		// Sampling
		Sampling samp = new Sampling();
		m_sessionsIDs = samp.getSample(sizeDB, (long)0, false);
		// Save
		SaveLoadObjects sosess = new SaveLoadObjects();
		sosess.save(m_sessionsIDs, databaseWD + "/_sessionIDs.javaData");
		
		// instantiated sequences
		m_sequences = WebAccessSequencesUHC.getSequencesInstanciated(m_sessionsIDs);
		// save
		SaveLoadObjects soseqs = new SaveLoadObjects();
		soseqs.save(m_sequences, databaseWD + "/_sequencesUHC.javaData");
	}
	
	/**
	 * Load database.
	 *
	 * @param databaseWD the database wd
	 */
	public void loadDatabase(String databaseWD){
		// Sampling
		ArrayList<Integer> sampleSessionIDs;
		SaveLoadObjects sosess = new SaveLoadObjects();
		m_sessionsIDs = (ArrayList<String>)sosess.load(databaseWD + "/_sessionIDs.javaData");
		
		// INSTANCIATED SEQUENCES
		SaveLoadObjects soseqs = new SaveLoadObjects();
		m_sequences = (ArrayList<String[]>)soseqs.load(databaseWD + "/_sequencesUHC.javaData");
	}
	
	public void loadDatabase2(String seqfile){
		ArrayList<String[]> seqsDB = new ArrayList<String[]>();
		ArrayList<String> sesIdDB = new ArrayList<String>();
		
		BufferedReader br = null;
		try{
			long ind = 0;
			String sCurrentLine;
			br = new BufferedReader(new FileReader(seqfile));
			while ((sCurrentLine = br.readLine()) != null) {
				String[] sequenceA = sCurrentLine.split(",");
				int seqlen = sequenceA.length - 1;
				
				// sessions IDs
				int sesID = 0;
				String sesIDstr = sequenceA[0];
				
				// sequence
				String[] seqA = new String[seqlen]; 
				for(int i=1; i<sequenceA.length; i++){
					int urlInt = Integer.valueOf(sequenceA[i]).intValue();
					String urlstr = urlInt + "C";
					seqA[i-1] = urlstr;
				}
				
				// add the sequence
				seqsDB.add(seqA);
				
				// session ID
				String indStr = String.valueOf(ind);
				sesIdDB.add(indStr);
				ind++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null){
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	
		// save the structures
		m_sessionsIDs = sesIdDB;
		m_sequences = seqsDB;
	}
	
	/**
	 * Gets the sessions i ds.
	 *
	 * @return the sessions i ds
	 */
	public ArrayList<String> getSessionsIDs(){
		ArrayList<String> ses = new ArrayList<String>();
		for(int i=0; i<m_sessionsIDs.size(); i++){
			ses.add(m_sessionsIDs.get(i));
		}
		return ses;
	}
	
	/**
	 * Gets the instantiated sequences.
	 *
	 * @return the instantiated sequences
	 */
	public ArrayList<String[]> getInstantiatedSequences(){
		return m_sequences;
	}

}