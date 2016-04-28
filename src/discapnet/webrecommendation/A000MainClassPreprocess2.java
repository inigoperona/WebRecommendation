package discapnet.webrecommendation;

import ehupatras.webrecommendation.structures.WebAccess;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.usage.preprocess.Sessioning;

// TODO: Auto-generated Javadoc
/**
 * The Class A000MainClassPreprocess2.
 */
public class A000MainClassPreprocess2 {

	/**
	 * Preprocess logs.
	 *
	 * @param basedirectory the basedirectory
	 * @param logFilesIndex the log files index
	 */
	public void preprocessLogs(String basedirectory, String logFilesIndex){
		// start preprocessing
		long starttime = System.currentTimeMillis();		

		Website.load();
		System.out.println("Website. loaded items: " + Website.size());
		WebAccess.loadStructure("orderedrequests.javaData");
		System.out.println("WebAccessSequences. loaded items: " + WebAccess.filteredlogsize());


		// SESSIONING //
		Sessioning ses = new Sessioning();

		// select the first sessions
		//ses.selectFixedNumberOfSequences(100);
		
		// write preprocessed logs
		WebAccessSequences.writeSequencesIndex(basedirectory + "/sequences_requestIndexesSample.txt");
		WebAccessSequencesUHC.writeSequencesInstanciated(basedirectory + "/sequences_urlIDurlRoleSample.txt");
		WebAccessSequences.saveSequences("_sequencesSample.javaData");
	}
	
	public static void main(String[] args) {
		// Parameter control
		//String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA/all_esperimentation";
		String basedirectory = "experiments_discapnet";
		String preDir = basedirectory + "/01_preprocess";
		String logfilesIndex = "/logFilesIndex.txt";
		String distDir = basedirectory + "/02_database";
		
		//preDir = args[0];
		//logfilesIndex = args[1];
		//distDir = args[2];
		
		// initialize the data structure
		WebAccess.setWorkDirectory(preDir);
		Website.setWorkDirectory(preDir);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		A000MainClassPreprocess2 main = new A000MainClassPreprocess2();
			
		// READ THE LOG FILE(S) //
		main.preprocessLogs(preDir, logfilesIndex);
		
		// CREATE DATABASE
		//A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		//database.createDatabase(distDir, 100);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
