package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.content.preprocess.PreprocessContent;

public class A060MainClassAddContent {
	
	public static void main(String args[]){
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA/20140228_v6";
		String logfile = "/kk.log";
		//preprocessingWD = args[0];
		//logfile = args[1];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		
		// LOAD PREPROCESSED LOGS //
		A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		preprocess.loadPreprocess();
		
		// Reading and preprocessing content data
		PreprocessContent pc = new PreprocessContent();
		pc.pickupURLsToDownload();
		pc.printURLs();
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
