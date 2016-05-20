package gipuzkoa_eus.webrecommendation;

import ehupatras.webrecommendation.structures.WebAccess;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.usage.preprocess.Sessioning;

public class A000MainClassPreprocess_10min2 {

	public void preprocessLogs(String basedirectory, String logFilesIndex){
		long starttime;
		long endtime;

		// start preprocessing
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] PREPROCESSING.");
		Website.load();
		WebAccess.setnMemory(10); // 60MBytes * 20moduls = 1200Mbytes
		WebAccess.changeToOrderedRequests();
		WebAccess.loadStructure();
		
		
		// SESSIONING //
		Sessioning ses = new Sessioning();
	
		// create sessions
		/*
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start spliting up into sessions.");
			// maximum period of inactivity 10 minutes
			// maximum session length
		ses.createSessions(10, 200); 
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		//WebAccess.writeFilteredLog(basedirectory + "/filteredLog3.log");
		 */

		// join consecutive same URLs
		/*
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start joining consecutive same URLs.");
		ses.joinConsecutiveSameUrls();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		//WebAccess.writeFilteredLog(basedirectory + "/filteredLog4.log");
		*/
		
		// takeFirstUrlOfTimeZero
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start creating sequences.");
		ses.createSequences();
		WebAccessSequences.writeSequencesIndex(basedirectory + "/sequences_requestIndexes_all.txt");
		WebAccessSequences.writeValidness(basedirectory + "/validness_all.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// create sequences
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Take first URL of time zero.");
		ses.takeFirstUrlOfTimeZero();
		WebAccessSequences.writeSequencesIndex(basedirectory + "/sequences_requestIndexes_all2.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
			
		// ensure a minimum activity in each sequence
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start ensuring a minimun activity in each sequence.");
		ses.ensureMinimumActivityInEachSequence(1);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// remove long sequences, the activity web robots generate
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start removing long sequences.");
		ses.removeLongSequences((float)99.9);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");

		// save the sessions structure we have created
		WebAccess.saveStructure();
		WebAccessSequences.saveSequences();
		Website.save();
			
		// write preprocessed logs
		WebAccess.writeFilteredLog(basedirectory + "/filteredLog.log");
		WebAccessSequences.writeSequencesIndex(basedirectory + "/sequences_requestIndexes.txt");
		WebAccessSequences.writeSequencesInstanciated(basedirectory + "/sequences_urlIDurlRole.txt");
	}
	
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		// Parameter control
		//String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA/all_esperimentation";
		//String basedirectory = "experiments_gieus/00_preprocess";
		//String logfilesIndex = "/loglist2.txt";

		//String basedirectory = "/home/ainhoa/workspace_WebRecommendation";
		//String basedirectory = "/home/disk/AINHOA/EHU_LOGAK_13_04_2016/experiment1/00_preprocess";
		String basedirectory = "20160400_experiments_EHU_LOG_LAGINA2/00_preprocess";
		String logfilesIndex = "/loglist.txt";
		
		//basedirectory = args[0];
		//logfilesIndex = args[1];
		
		// initialize the data structure
		WebAccess.setWorkDirectory(basedirectory);
		Website.setWorkDirectory(basedirectory);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		A000MainClassPreprocess_10min2 main = new A000MainClassPreprocess_10min2();
			
		// READ THE LOG FILE(S) //
		main.preprocessLogs(basedirectory, logfilesIndex);
			
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
