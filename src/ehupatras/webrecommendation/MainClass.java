package ehupatras.webrecommendation;

import ehupatras.webrecommendation.preprocess.*;
import ehupatras.webrecommendation.preprocess.log.*;
import ehupatras.webrecommendation.structures.*;

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
		
		
		// READ THE LOG FILE
		LogReader logreader = new LogReaderBidasoaTurismo();
		
		// It reads the log file and store the valid requests in [ehupatras.webrecommendation.structures.WebAccessSequences]
		long starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start reading the log files and analyzing the URLs.");
		String[] logfilesA = new String[1];
		logfilesA[0] = basedirectory + filename1;
		//logfilesA[1] = basedirectory + "/kk2.log";
/*
		logfilesA[2] = basedirectory + "/201203.log";
		logfilesA[3] = basedirectory + "/201204.log";
		logfilesA[4] = basedirectory + "/201205.log";
		logfilesA[5] = basedirectory + "/201206.log";
		logfilesA[6] = basedirectory + "/201207.log";
		logfilesA[7] = basedirectory + "/201208.log";
		logfilesA[8] = basedirectory + "/201209.log";
		logfilesA[9] = basedirectory + "/201210.log";
		logfilesA[10] = basedirectory + "/201211.log";
*/
		logreader.readLogFile(logfilesA);
		long endtime = System.currentTimeMillis();
		System.out.println("[" + endtime + "] Ends. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
		
		// ensure a minimum amount of apparitions of URLs.
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start identifying frequent URLs.");
		logreader.identifyFrequentURLs(10);
		endtime = System.currentTimeMillis();
		System.out.println("[" + endtime + "] Ends. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
		
		// ensure that the URLs are static or it keeps interest during the time
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start identifying static URLs.");
		logreader.identifyStaticURLs(10, 5, (float)0.75);
		endtime = System.currentTimeMillis();
		System.out.println("[" + endtime + "] Ends. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
		
		
		// SESSIONING
		Sessioning ses = new Sessioning();
		
		// create sessions
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start spliting up into sessions.");
		ses.createSessions(10); // maximum period of inactivity
		endtime = System.currentTimeMillis();
		System.out.println("[" + endtime + "] Ends. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");

		// join consecutive same URLs
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start joining consecutive same URLs.");
		ses.joinConsecutiveSameUrls();
		System.out.println("[" + endtime + "] Ends. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		
		// WRITE PROCESSED LOGS
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Start writing processed logs.");
		WebAccessSequences.writeFilteredLog(basedirectory + "/filteredLog.log");
		endtime = System.currentTimeMillis();
		System.out.println("[" + endtime + "] Ends. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}
