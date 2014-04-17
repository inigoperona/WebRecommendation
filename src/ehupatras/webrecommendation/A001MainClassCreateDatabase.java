package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.modelvalidation.*;
import java.util.*;

public class A001MainClassCreateDatabase {

	private ArrayList<Integer> m_sessionsIDs;
	private ArrayList<String[]> m_sequences;
	
	public void createDatabase(String databaseWD){
		// CREATE THE DATABASE
		System.out.println("CREATE THE DATABASE");
		
		// Sampling
		Sampling samp = new Sampling();
		m_sessionsIDs = samp.getSample(8000, (long)0, false);
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
		ArrayList<Integer> sampleSessionIDs;
		SaveLoadObjects sosess = new SaveLoadObjects();
		m_sessionsIDs = (ArrayList<Integer>)sosess.load(databaseWD + "/_sessionIDs.javaData");
		
		// INSTANCIATED SEQUENCES
		SaveLoadObjects soseqs = new SaveLoadObjects();
		m_sequences = (ArrayList<String[]>)soseqs.load(databaseWD + "/_sequencesUHC.javaData");
	}
	
	public ArrayList<Integer> getSessionsIDs(){
		return m_sessionsIDs;
	}
	
	public ArrayList<String[]> getInstantiatedSequences(){
		return m_sequences;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		String logfile = "/kk.log";
		String databaseWD = "/home/burdinadar/eclipse_workdirectory/DATA";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		
		// LOAD PREPROCESSED LOGS //
		A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		preprocess.loadPreprocess();
		
		// CREATE THE DATABSE //
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		database.createDatabase(databaseWD);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}