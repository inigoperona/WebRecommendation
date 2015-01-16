package ehupatras.webrecommendation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.content.preprocess.PreprocessContent;

// TODO: Auto-generated Javadoc
/**
 * The Class A100MainClassAddContent.
 */
public class A100MainClassAddContent {
	
    /**
     * Load urls dm.
     *
     * @param urlsDMfile the urls d mfile
     * @return the object[]
     */
    public Object[] loadUrlsDM(String urlsDMfile){
    	// load the distance matrix of URL's similarity
    	ArrayList<Integer> urlIDs = new ArrayList<Integer>();
    	ArrayList<float[]> urlsDM = new ArrayList<float[]>(); 
		try {
			BufferedReader br = new BufferedReader(new FileReader(urlsDMfile));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(" ");
				urlIDs.add(Integer.valueOf(line[0]));
				int nURLs = line.length-1;
				float[] urldist = new float[nURLs];
				for(int i=1; i<line.length; i++){
					urldist[i-1] = Float.valueOf(line[i]);
				}
				urlsDM.add(urldist);
			}
			br.close();
		} catch (IOException ex){
			System.err.println("Exception at reading URLs' distance matrix. " + 
					"[ehupatras.webrecommendation.A100MainClassAddContent.loadUrlsDM]");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// convert to float-matrix
		int nURLs = urlIDs.size();
		float[][] urlsDMatrix = new float[nURLs][nURLs];
		for(int i=0; i<urlsDM.size(); i++){
			urlsDMatrix[i] = urlsDM.get(i);
		}
		
		// return 
		Object[] objA = new Object[2];
		objA[0] = urlIDs;
		objA[1] = urlsDMatrix;
		return objA;
    }
	
    /**
     * Load urls topic.
     *
     * @param urlsTopicfile the urls topicfile
     * @param sep the sep
     * @return the object[]
     */
    public Object[] loadUrlsTopic(String urlsTopicfile, String sep){
    	// count different topics there are
    	ArrayList<Integer> topicIDs = new ArrayList<Integer>();
    	
    	// load the distance matrix of URL's similarity
    	ArrayList<Integer> urlIDs = new ArrayList<Integer>();
    	ArrayList<Integer> urls2topic = new ArrayList<Integer>(); 
		try {
			BufferedReader br = new BufferedReader(new FileReader(urlsTopicfile));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(sep);
				urlIDs.add(Integer.valueOf(line[0]));
				int topicID = Integer.valueOf(line[1]);
				if(topicID!=-1 && !topicIDs.contains(topicID)){
					topicIDs.add(topicID);
				}
				urls2topic.add(topicID);
			}
			br.close();
		} catch (IOException ex){
			System.err.println("Exception at reading URL-topic file. " + 
					"[ehupatras.webrecommendation.A100MainClassAddContent.loadUrlsTopic]");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// convert to integer-array
		int nURLs = urlIDs.size();		
		int[] url2topic = new int[nURLs];
		for(int i=0; i<url2topic.length; i++){
			url2topic[i] = urls2topic.get(i);
		}
		
		// return
		Object[] objA = new Object[3];
		objA[0] = urlIDs;
		objA[1] = url2topic;
		objA[2] = topicIDs.size(); // different topics there are
		return objA;
    }
	
    
    /**
     * Url to topic.
     */
    private void urlToTopic(){
		// Parameter control
		String base = "convert_UrlIDs_content2usage/angelu";
		
		// minimum affinity to be topic
		String topicThStr = "0.0";
		float topicTh = Float.parseFloat(topicThStr);
		
		// input and output
		String infileUrl2topicDist;
		String outfileUrl2topic;
		PreprocessContent pc = new PreprocessContent();
		
		// TestuHutsa
		infileUrl2topicDist = "/document-topic_distributions/document-topic-distributions1_TestuHutsa_contentID.csv";
		outfileUrl2topic = "/URL2topic_th0_contID_2/URLs_to_topic_TestuHutsa_th0_contID_cl.txt";
		pc.readURL2TopicDistribution_contID(base + infileUrl2topicDist);
		pc.computeURL2topic(topicTh);
		pc.writeURL2topic_contID(base + outfileUrl2topic);
		
		// Modua0
		infileUrl2topicDist = "/document-topic_distributions/document-topic-distributions1_Modua0_contentID.csv";
		outfileUrl2topic = "/URL2topic_th0_contID_2/URLs_to_topic_Modua0_th0_contID_cl.txt";
		pc.readURL2TopicDistribution_contID(base + infileUrl2topicDist);
		pc.computeURL2topic(topicTh);
		pc.writeURL2topic_contID(base + outfileUrl2topic);
		
		// Modua1
		infileUrl2topicDist = "/document-topic_distributions/document-topic-distributions1_Modua1_contentID.csv";
		outfileUrl2topic = "/URL2topic_th0_contID_2/URLs_to_topic_Modua1_th0_contID_cl.txt";
		pc.readURL2TopicDistribution_contID(base + infileUrl2topicDist);
		pc.computeURL2topic(topicTh);
		pc.writeURL2topic_contID(base + outfileUrl2topic);
		
		// Modua2
		infileUrl2topicDist = "/document-topic_distributions/document-topic-distributions1_Modua2_contentID.csv";
		outfileUrl2topic = "/URL2topic_th0_contID_2/URLs_to_topic_Modua2_th0_contID_cl.txt";
		pc.readURL2TopicDistribution_contID(base + infileUrl2topicDist);
		pc.computeURL2topic(topicTh);
		pc.writeURL2topic_contID(base + outfileUrl2topic);
    }
    
    /**
     * Old program.
     */
    private void oldProgram(){
		// Parameter control
		String base = "convert_UrlIDs_content2usage/angelu";
		
		// URL TO TOpic
		String topicThStr = "0.0";
		String infileUrl2topicDist;
		String outfileUrl2topic;
		//TestuHutsa
		infileUrl2topicDist = "/document-topic_distributions/document-topic-distributions1_TestuHutsa_contentID.csv";
		outfileUrl2topic = "/URL2topic_th0_contID_2/URLs_to_topic_TestuHutsa.txt";
		// Modua0
		//infileUrl2topicDist = "/document-topic_distributions/document-topic-distributions1_Modua0_contentID.csv";
		//outfileUrl2topic = "/URL2topic_th0_contID_2/URLs_to_topic_Modua0.txt";
		
		String preprocessingWD = base + "/01_preprocess";
		String contentWD = base + "/01_preprocess/Content";
		String outfileUrlDM = "/URLs_DM_TH.txt";
		
		/*
		preprocessingWD = args[0];
		contentWD = args[1];
		infileUrl2topicDist = args[2];
		topicThStr = args[3];
		outfileUrlDM = args[4];
		outfileUrl2topic = args[5];
		*/
		
		float topicTh = Float.parseFloat(topicThStr);

		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		
		// LOAD the URLs extracted from preprocesed logs //
		//Website.load();
		
		// Reading and preprocessing content data
		PreprocessContent pc = new PreprocessContent();
		pc.pickupURLsToDownload();
		//pc.writeURLs(contentWD + "/URLsToDownload.txt");
			//pc.readURLs(preprocessingWD + "/URLsToDownload.txt");
			//pc.printURLs();
		
		// Compute the distance among the URLs
		Website.setSaveFileName("/_Website2.javaData");
		pc.readURL2TopicDistribution(contentWD + infileUrl2topicDist);
		pc.computeUrlTopicSimilarities();
			//pc.printUrlDM();
		pc.writeUrlDM(contentWD + outfileUrlDM);
		
		// Compute the topic of each URL
		pc.computeURL2topic(topicTh);
		//pc.printURL2topic();
		pc.writeURL2topic(contentWD + outfileUrl2topic);
    }
    
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String args[]){
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		
		A100MainClassAddContent cont = new A100MainClassAddContent();
		cont.urlToTopic();
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
