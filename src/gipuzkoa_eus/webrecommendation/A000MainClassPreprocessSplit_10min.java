package gipuzkoa_eus.webrecommendation;

import ehupatras.webrecommendation.structures.WebAccess;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.usage.preprocess.Sessioning;

public class A000MainClassPreprocessSplit_10min {

	public void preprocessLogs(String basedirectory, String logFilesIndex){
		long starttime;
		long endtime;
		
		// start preprocessing
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] PREPROCESSING.");
		WebAccess.setnMemory(20); // 60MBytes * 20moduls = 1200Mbytes
		WebAccess.changeToOrderedRequests();
		
		// load the preprocess
		Website.load();
		WebAccess.loadStructure();
		WebAccessSequences.loadSequences();
		
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
		WebAccessSequences.writeFilteredLog(basedirectory + "/filteredLog3.log");
		*/

		// join consecutive same URLs
		/*
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start joining consecutive same URLs.");
		ses.joinConsecutiveSameUrls();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		WebAccessSequences.writeFilteredLog(basedirectory + "/filteredLog4.log");
		*/

		
		// create sequences
		/*
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start creating sequences.");
		ses.createSequences();
		WebAccessSequences.writeSequencesIndex(basedirectory + "/sequences_requestIndexes_all.txt");
		WebAccessSequences.writeValidness(basedirectory + "/validness_all.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		*/
		
		// split the sequences that have clicks longer than expire time
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start spliting sequences.");
		ses.createSessions2(10);
		WebAccessSequences.writeSequencesIndex(basedirectory + "/sequences_requestIndexes_all_split.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		
		// ensure that each sequence is made up mainly by valid URLs
		/*
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start ensuring minimum amount of valid URLs.");
		ses.ensureMinimumValidURLs(0.75f);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		*/
		
		// ensure that there are not consecutive same URLs
		// in this case only select the first one
		/*
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start removing consecutive same URLs.");
		ses.removeConsecutiveSameURLs();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		*/
		
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

		
		// write preprocessed logs
		WebAccess.writeFilteredLog(basedirectory + "/filteredLog_split.log");
		WebAccessSequences.writeSequencesIndex(basedirectory + "/sequences_requestIndexes_split.txt");
		WebAccessSequences.writeSequencesInstanciated(basedirectory + "/sequences_urlIDurlRole_split.txt");
						
		// save the sessions structure we have created
		WebAccess.saveStructure();
		WebAccessSequences.saveSequences();
		Website.save();
	}
	
}
