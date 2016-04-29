package angelu.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.modelvalidation.*;
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
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		WebAccess.changeToOrderedRequests();
		param.loadLogs();
		param.createDatabase();
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
	
	
	
	
	
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
	 * Creates the database.
	 *
	 * @param databaseWD the database wd
	 */
	public void createDatabase(String databaseWD){
		int nseq = WebAccessSequences.getSequencesIDs().size();
		this.createDatabase(databaseWD, nseq);
	}
	
	/**
	 * Load database.
	 *
	 * @param databaseWD the database wd
	 */
	public void loadDatabase(String databaseWD){
		// Sampling
		SaveLoadObjects sosess = new SaveLoadObjects();
		m_sessionsIDs = (ArrayList<String>)sosess.load(databaseWD + "/_sessionIDs.javaData");
		
		// INSTANCIATED SEQUENCES
		SaveLoadObjects soseqs = new SaveLoadObjects();
		m_sequences = (ArrayList<String[]>)soseqs.load(databaseWD + "/_sequencesUHC.javaData");
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