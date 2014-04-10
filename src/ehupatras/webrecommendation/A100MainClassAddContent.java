package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.content.preprocess.PreprocessContent;

public class A100MainClassAddContent {
	
	public static void main(String args[]){
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA/20140228_v6";
		//preprocessingWD = args[0];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		
		// LOAD the URLs extracted from preprocesed logs //
		Website.load();
		
		// Reading and preprocessing content data
		PreprocessContent pc = new PreprocessContent();
		pc.pickupURLsToDownload();
		pc.writeURLs(preprocessingWD + "/URLsToDownload.txt");
		//pc.readURLs(preprocessingWD + "/URLsToDownload.txt");
		//pc.printURLs();
		Website.setSaveFileName("/_Website2.javaData");
		pc.readURL2Topic("/home/burdinadar/Desktop/ehupatras/CONTENT/document-topic-distributions1_v2.csv");
		pc.computeUrlTopicSimilarities();
		//pc.printUrlDM();
		pc.writeUrlDM("/home/burdinadar/Desktop/ehupatras/CONTENT/URLs_DM.txt");
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
