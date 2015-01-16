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
	private ArrayList<Integer> m_sessionsIDs;
	
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
	public void createDatabase(String databaseWD){
		// CREATE THE DATABASE
		System.out.println("CREATE THE DATABASE");
		
		// Sampling
		Sampling samp = new Sampling();
		int nseq = WebAccessSequences.getSequencesIDs().size();
		m_sessionsIDs = samp.getSample(nseq, (long)0, false);
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
		SaveLoadObjects sosess = new SaveLoadObjects();
		m_sessionsIDs = (ArrayList<Integer>)sosess.load(databaseWD + "/_sessionIDs.javaData");
		
		// INSTANCIATED SEQUENCES
		SaveLoadObjects soseqs = new SaveLoadObjects();
		m_sequences = (ArrayList<String[]>)soseqs.load(databaseWD + "/_sequencesUHC.javaData");
	}
	
	/**
	 * Gets the sessions i ds.
	 *
	 * @return the sessions i ds
	 */
	public ArrayList<Long> getSessionsIDs(){
		ArrayList<Long> ses = new ArrayList<Long>();
		for(int i=0; i<m_sessionsIDs.size(); i++){
			ses.add((long)m_sessionsIDs.get(i));
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