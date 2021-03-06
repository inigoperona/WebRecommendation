package discapnet.webrecommendation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ehupatras.webrecommendation.structures.WebAccess;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.usage.preprocess.Sessioning;
import ehupatras.webrecommendation.usage.preprocess.log.LogReader;
import ehupatras.webrecommendation.usage.preprocess.log.LogReaderDiscapnet;

// TODO: Auto-generated Javadoc
/**
 * The Class A000MainClassPreprocess.
 */
public class A000MainClassPreprocess {

	/**
	 * Preprocess logs.
	 *
	 * @param basedirectory the basedirectory
	 * @param logFilesIndex the log files index
	 */
	public void preprocessLogs(String basedirectory, String logFilesIndex){
		long starttime;
		long endtime;
		
		// start preprocessing
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] PREPROCESSING.");
		

		// FILTER LOGS //
		LogReader logreader = new LogReaderDiscapnet();
		
		// It reads the log file and store the valid requests in [ehupatras.webrecommendation.structures.WebAccessSequences]
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start reading the log files and analyzing the URLs.");
			String[] logfilesA = this.getLogFiles(basedirectory, logFilesIndex);
		logreader.readLogFile(logfilesA);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
		Website.save();
		WebAccess.writeFilteredLog(basedirectory + "/filteredLog1.log");

		// ensure a minimum amount of apparitions of URLs.
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start identifying frequent URLs.");
		logreader.identifyFrequentURLs(10);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
		
		// ensure that the URLs are static or it keeps interest during the time
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start identifying static URLs.");
		logreader.identifyStaticURLs(10, 5, (float)0.75);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
		Website.save();
		WebAccess.writeFilteredLog(basedirectory + "/filteredLog2.log");
		WebAccess.changeToOrderedRequests();		
		
		// SESSIONING //
		Sessioning ses = new Sessioning();
	
		// create sessions
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start spliting up into sessions.");
		ses.createSessions(10, 200); // maximum period of inactivity
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		WebAccess.writeFilteredLog(basedirectory + "/filteredLog3.log");

		// join consecutive same URLs
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start joining consecutive same URLs.");
		ses.joinConsecutiveSameUrls();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		WebAccess.writeFilteredLog(basedirectory + "/filteredLog4.log");

		
		// create sequences
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start creating sequences.");
		ses.createSequences();
		WebAccessSequences.writeSequencesIndex(basedirectory + "/sequences_requestIndexes_all.txt");
		WebAccessSequences.writeValidness(basedirectory + "/validness_all.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// ensure that each sequence is made up mainly by valid URLs
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start ensuring minimum amount of valid URLs.");
		ses.ensureMinimumValidURLs(0.75f);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		
		// ensure that there are not consecutive same URLs
		// in this case only select the first one
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start removing consecutive same URLs.");
		ses.removeConsecutiveSameURLs();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
			
		// ensure a minimum activity in each sequence
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start ensuring a minimun activity in each sequence.");
		ses.ensureMinimumActivityInEachSequence(3);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// remove long sequences, the activity web robots generate
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start removing long sequences.");
		ses.removeLongSequences((float)98);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		
		// compute the each web page role
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start computing pages' role.");
		ses.computePageRoleUHC_time(5, 20, 10*60);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
	
		// remove those sequences that have many Unimportant
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start removing Unimportant sequences.");
		ses.computePageRoleUHC_removeOnlyUnimportant(0.75f);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");

		
		// write preprocessed logs
		WebAccess.writeFilteredLog(basedirectory + "/filteredLog.log");
		WebAccessSequences.writeSequencesIndex(basedirectory + "/sequences_requestIndexes.txt");
		WebAccessSequences.writeSequencesInstanciated(basedirectory + "/sequences_urlIDurlRole.txt");
						
		// save the sessions structure we have created
		WebAccess.saveStructure();
		WebAccessSequences.saveSequences();
		Website.save();
	}
	
	/**
	 * Load preprocess.
	 */
	public void loadPreprocess(){
		long starttime;
		long endtime;
		
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start loading preprocessed data.");
		Website.load();
		WebAccess.loadStructure();
		WebAccessSequences.loadSequences();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
	}
	
	
	/**
	 * Gets the log files.
	 *
	 * @param basedirectory the basedirectory
	 * @param logFilesIndex the log files index
	 * @return the log files
	 */
	public String[] getLogFiles(String basedirectory, String logFilesIndex){
		ArrayList<String> logFilesAL = new ArrayList<String>();
		
		// read the logFilesIndex file
		BufferedReader br = null;
		
		// open
		try{
			br = new BufferedReader(new FileReader(basedirectory + logFilesIndex));
		} catch (FileNotFoundException ex){
			System.err.println(
					"[discapnet.webrecommendation.A000MainClassPreprocess" +
					"getLogFiles] " +
					"Problems opening the file: " + logFilesIndex);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// read		
		String sCurrentLine;
		try{
			while ((sCurrentLine = br.readLine()) != null) {
				logFilesAL.add(basedirectory + "/" + sCurrentLine);
			}
		} catch(IOException ex){
			System.err.println(
					"[discapnet.webrecommendation.A000MainClassPreprocess." +
					"getLogFiles] " +
					"Problems reading the file: " + logFilesIndex);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close
		try{
			if(br!=null){br.close();}
		} catch (IOException ex){
			System.err.println(
					"[discapnet.webrecommendation.A000MainClassPreprocess." +
					"getLogFiles] " +
					"Problems closing the file: " + logFilesIndex);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// convert the array list to string-array
		String[] logFilesA = new String[logFilesAL.size()];
		for(int i=0; i<logFilesAL.size(); i++){
			logFilesA[i] = logFilesAL.get(i);
		}
		return logFilesA;
	}
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		// Parameter control
		//String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA/all_esperimentation";
		String basedirectory = "experiments_discapnet/01_preprocess";
		String logfilesIndex = "/logFilesIndex.txt";
		
		basedirectory = args[0];
		logfilesIndex = args[1];
		
		// initialize the data structure
		WebAccess.setWorkDirectory(basedirectory);
		Website.setWorkDirectory(basedirectory);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		A000MainClassPreprocess main = new A000MainClassPreprocess();
			
		// READ THE LOG FILE(S) //
		main.preprocessLogs(basedirectory, logfilesIndex);
			
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
