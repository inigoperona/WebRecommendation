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
		
		String wd = "20160525_experiments_BT";
		String[] args2 = {wd+"/00_preprocess", "xxx", "-1", "xxx", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "xxx",
				"xxx", "xxx", "xxx", "xxx", "-", "1"};
		
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
		long seed = 0l;
		
		// Sampling
		Sampling samp = new Sampling();
		m_sessionsIDs = samp.getSample(sizeDB, seed, false);
		// Save
		SaveLoadObjects sosess = new SaveLoadObjects();
		sosess.save(m_sessionsIDs, databaseWD + "/_sessionIDs.javaData");
				
		// instantiated sequences
		m_sequences = WebAccessSequencesUHC.getSequencesInstanciated(m_sessionsIDs);
		// save
		SaveLoadObjects soseqs = new SaveLoadObjects();
		soseqs.save(m_sequences, databaseWD + "/_sequencesUHC.javaData");
	}

	
	public void loadDatabase(String databaseWD){
		// Sampling
		SaveLoadObjects sosess = new SaveLoadObjects();
		m_sessionsIDs = (ArrayList<String>)sosess.load(databaseWD + "/_sessionIDs.javaData"); 
		try{
			String str0 = m_sessionsIDs.get(0);
		} catch (ClassCastException ex){
			ArrayList<Integer> intAL = (ArrayList<Integer>)sosess.load(databaseWD + "/_sessionIDs.javaData"); 
			m_sessionsIDs = new ArrayList<String>(intAL.size());
			for(int i=0; i<intAL.size(); i++){ m_sessionsIDs.add(String.valueOf(intAL.get(i))); }
		}

		// INSTANCIATED SEQUENCES
		SaveLoadObjects soseqs = new SaveLoadObjects();
		m_sequences = (ArrayList<String[]>)soseqs.load(databaseWD + "/_sequencesUHC.javaData");
	}
	
	public ArrayList<String> getSessionsIDs(){
		return m_sessionsIDs;
	}
	
	public ArrayList<String[]> getInstantiatedSequences(){
		return m_sequences;
	}
}