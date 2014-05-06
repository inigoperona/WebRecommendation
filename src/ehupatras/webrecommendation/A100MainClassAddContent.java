package ehupatras.webrecommendation;

import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.content.preprocess.PreprocessContent;

public class A100MainClassAddContent {
	
	public static void main(String args[]){
		
		// Parameter control
		String preprocessingWD = "/home/burdinadar/eclipse_workdirectory/DATA/20140228_v6";
		String contentWD = "/home/burdinadar/Desktop/ehupatras/CONTENT";
		String infileUrl2topicDist = "/topicTestuakBatera-document-topic-distributuions1_v2.csv";
		String outfileUrlDM = "/URLs_DM.txt";
		String outfileUrl2topic = "/URLs_to_topic.txt";
		preprocessingWD = args[0];
		contentWD = args[1];
		infileUrl2topicDist = args[2];
		outfileUrlDM = args[3];
		outfileUrl2topic = args[4];
		
		
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
		pc.writeURLs(contentWD + "/URLsToDownload.txt");
			//pc.readURLs(preprocessingWD + "/URLsToDownload.txt");
			//pc.printURLs();
		
		// Compute the distance among the URLs
		Website.setSaveFileName("/_Website2.javaData");
		pc.readURL2TopicDistribution(contentWD + infileUrl2topicDist);
		pc.computeUrlTopicSimilarities();
			//pc.printUrlDM();
		pc.writeUrlDM(contentWD + outfileUrlDM);
		
		// Compute the topic of each URL
		pc.computeURL2topic(0.4f);
		//pc.printURL2topic();
		pc.writeURL2topic(contentWD + outfileUrl2topic);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
