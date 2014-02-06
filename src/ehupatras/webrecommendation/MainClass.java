package ehupatras.webrecommendation;

import ehupatras.webrecommendation.preprocess.*;
import ehupatras.webrecommendation.preprocess.log.*;
import ehupatras.webrecommendation.structures.*;
import ehupatras.webrecommendation.sampling.*;
import java.util.*;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA";
		String filename1 = "/kk1.log";
		//String basedirectory = args[0];
		//String filename1 = args[1];
		
		// initialize the data structure
		WebAccessSequences.setWorkDirectory(basedirectory);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		
		
		
		// READ THE LOG FILE(S) //
		
		LogReader logreader = new LogReaderBidasoaTurismo();
		
		// It reads the log file and store the valid requests in [ehupatras.webrecommendation.structures.WebAccessSequences]
			long starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start reading the log files and analyzing the URLs.");
			String[] logfilesA = new String[1];
			logfilesA[0] = basedirectory + filename1;
		logreader.readLogFile(logfilesA);
			long endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
		
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
		
		
			
		// SESSIONING //
			
		Sessioning ses = new Sessioning();
		
		// create sessions
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start spliting up into sessions.");
		ses.createSessions(30); // maximum period of inactivity
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");

		// join consecutive same URLs
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start joining consecutive same URLs.");
		ses.joinConsecutiveSameUrls();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// create sequences
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start creating sequences.");
		ses.createSequences();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// ensure a minimun activity in each sequence
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
		ses.computePageRoleUHC_time(10, 10, 20);
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		
		// WRITE PROCESSED DATA //
			
			// write preprocessed logs
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start writing processed logs.");
		WebAccessSequences.writeFilteredLog(basedirectory + "/filteredLog.log");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
			
		// write the sequences we have created
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start writing created sequences.");
		WebAccessSequences.writeSequences(basedirectory + "/sequences_requestIndexes.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
					+ (endtime-starttime)/1000 + " seconds.");
		
		// write the sequence instantiation1: 
		// sequence of urlID with each role: Unimportant (U), Hub (H), Content (C)
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start writing created sequences.");
		WebAccessSequences.writeSequences_URLwithUHC(basedirectory + "/sequences_urlIDurlRole.txt");
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		
		
		// SAMPLING //
		Sampling samp = new Sampling();
		ArrayList<Integer> sampleSessionIDs = samp.getSample(10000, (long)0, false);
			
		// Model Validation //
		
			
			
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}
