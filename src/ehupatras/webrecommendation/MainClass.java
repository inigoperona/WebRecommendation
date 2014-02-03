package ehupatras.webrecommendation;

import ehupatras.webrecommendation.preprocess.log.*;
import ehupatras.webrecommendation.structures.*;

public class MainClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA";
		WebAccessSequences.setWorkDirectory(basedirectory);
		long starttimeprogram = System.currentTimeMillis();
		
		// READ THE LOG FILE
		LogReader logreader = new LogReaderBidasoaTurismo();
		
		// It reads the log file and store the valid requests in [ehupatras.webrecommendation.structures.WebAccessSequences]
		long starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Starts reading the log files and analyzing the URLs.");
		String[] logfilesA = new String[2];
		logfilesA[0] = basedirectory + "/201201.log";
		logfilesA[1] = basedirectory + "/201202.log";
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
		System.out.println("[" + starttime + "] Starts identifying frequent URLs.");
		logreader.identifyFrequentURLs(10);
		endtime = System.currentTimeMillis();
		System.out.println("[" + endtime + "] Ends.");
		System.out.println("  Elapsed time: " + (endtime-starttime)/1000 + " seconds.");
		
		// ensure that the URLs are static or it keeps interest during the time
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Starts identifying static URLs.");
		logreader.identifyStaticURLs(10, 5, (float)0.75);
		endtime = System.currentTimeMillis();
		System.out.println("[" + endtime + "] Ends. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
		
		
		
		// WRITE PROCESSED LOGS
		starttime = System.currentTimeMillis();
		System.out.println("[" + starttime + "] Starts writing processed logs.");
		WebAccessSequences.writeFilteredLog(basedirectory + "/filteredLog.log");
		endtime = System.currentTimeMillis();
		System.out.println("[" + endtime + "] Ends. Elapsed time: "
				+ (endtime-starttime)/1000 + " seconds.");
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}

}
