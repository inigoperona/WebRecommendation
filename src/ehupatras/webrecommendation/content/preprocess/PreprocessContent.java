package ehupatras.webrecommendation.content.preprocess;

import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.structures.page.Page;

import java.util.ArrayList;
import java.net.URL;
import java.net.URLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.io.IOException;
import java.math.BigDecimal;

public class PreprocessContent {

	// url identification
	private ArrayList<String> m_formurls = new ArrayList<String>();
	private ArrayList<String> m_urlnames = new ArrayList<String>();
	private ArrayList<Integer> m_urlID = new ArrayList<Integer>();
	
	// URL identification
	private ArrayList<float[]> m_url2topicDist;
	private float[][] m_URLsDM;
	private int[] m_url2topic;
	
	
	
	// SELECTING THE URLS TO DOWNLOAD //
	
	public void pickupURLsToDownload(){
		String[] urlnames = Website.getAllFormatedUrlNames();
		for(int i=0; i<urlnames.length; i++){
			String formurl = urlnames[i];
			Page pag = Website.getPage(formurl);
			String url = pag.getUrlName();
			int urlID = pag.getUrlIDusage();
			if(pag.getIsSuitableToLinkPrediction()){
				m_formurls.add(formurl);
				m_urlnames.add(url);
				m_urlID.add(urlID);
			}
		}
		
	}
	
	public void printURLs(){
		for(int i=0; i<m_urlnames.size(); i++){
			String url = m_urlnames.get(i);
			int urlID = m_urlID.get(i);
			System.out.println(i + " " + urlID + " " + url);
		}
	}
	
	public void writeURLs(String filename){
		File file = new File(filename);
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i=0; i<m_urlnames.size(); i++){
				bw.write(m_urlID.get(i) + " ");
				bw.write(m_formurls.get(i) + " ");
				bw.write(m_urlnames.get(i) + "\n");
			}
			bw.close();
		} catch (IOException ex){
			System.err.println("Exception at writing the URL list. " + 
					"[ehupatras.webrecommendation.content.preprocess.PreprocessContent.writeURLs]");
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public void readURLs(String filename){
		m_urlID = new ArrayList<Integer>();
		m_formurls = new ArrayList<String>();
		m_urlnames = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(" ");
				m_urlID.add(Integer.valueOf(line[0]));
				m_formurls.add(line[1]);
				m_urlnames.add(line[2]);
			}
			br.close();
		} catch (IOException ex){
			System.err.println("Exception at loading the URL list. " + 
					"[ehupatras.webrecommendation.content.preprocess.PreprocessContent.loadURLs]");
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	
	
	// READING URL - TOPIC_DISTRIBUTION FILE //
	
	public void readURL2TopicDistribution(String filename){
		// Read topic information
		m_url2topicDist = new ArrayList<float[]>(m_formurls.size());
		for(int i=0; i<m_formurls.size(); i++){m_url2topicDist.add(null);}
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(",");
				int urlid = Integer.valueOf(line[0]);
				int index = m_urlID.indexOf(urlid);
				if(index==-1){continue;}
				float[] topdist = new float[line.length-1];
				for(int i=1; i<line.length; i++){
					String numStr1 = line[i];
					int startNum = 0;
					for(int l=startNum; l<numStr1.length(); l++){
						char c = numStr1.charAt(l);
						if(Character.isDigit(c)){startNum=l; break;}
					}
					int endNum = numStr1.length() - 1;
					for(int l=endNum; l>=0; l--){
						char c = numStr1.charAt(l);
						if(Character.isDigit(c)){endNum=l+1; break;}
					}
					String numStr2 = numStr1.substring(startNum, endNum);
					String res = new BigDecimal(numStr2).toPlainString();
					float ptopic = Float.parseFloat(res);
					topdist[i-1] = ptopic;
				}
				m_url2topicDist.set(index, topdist);
			}
			br.close();
		} catch (IOException ex){
			System.err.println("Exception at loading the URL list. " + 
					"[ehupatras.webrecommendation.content.preprocess.PreprocessContent.readURL2Topic]");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// Update Website structure
		for(int i=0; i<m_formurls.size(); i++){
			String formurl = m_formurls.get(i);
			float[] topdist = m_url2topicDist.get(i);
			Page pag = Website.getPage(formurl);
			pag.setTopicDistribution(topdist);
			Website.setPage(formurl, pag);
		}
		Website.save();
	}
	
	public void readURL2TopicDistribution_contID(String filename){
		// Read topic information
		m_url2topicDist = new ArrayList<float[]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(",");
				int urlid = Integer.valueOf(line[0]);
				float[] topdist = new float[line.length-1];
				for(int i=1; i<line.length; i++){
					String numStr1 = line[i];
					int startNum = 0;
					for(int l=startNum; l<numStr1.length(); l++){
						char c = numStr1.charAt(l);
						if(Character.isDigit(c)){startNum=l; break;}
					}
					int endNum = numStr1.length() - 1;
					for(int l=endNum; l>=0; l--){
						char c = numStr1.charAt(l);
						if(Character.isDigit(c)){endNum=l+1; break;}
					}
					String numStr2 = numStr1.substring(startNum, endNum);
					String res = new BigDecimal(numStr2).toPlainString();
					float ptopic = Float.parseFloat(res);
					topdist[i-1] = ptopic;
				}
				m_url2topicDist.add(urlid, topdist);
			}
			br.close();
		} catch (IOException ex){
			System.err.println("Exception at loading the URL list. " + 
					"[ehupatras.webrecommendation.content.preprocess.PreprocessContent.readURL2Topic]");
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public void computeUrlTopicSimilarities(){
		int nUrl = m_formurls.size();
		
		// compute the distances among URLs
		float[][] urlsDM = new float[nUrl][nUrl];
		for(int i=0; i<nUrl; i++){
			float[] topicDistribution1 = m_url2topicDist.get(i);
			for(int j=i; j<nUrl; j++){
				float[] topicDistribution2 = m_url2topicDist.get(j);
				float distance = this.euclideanDistance(topicDistribution1, topicDistribution2);
				urlsDM[i][j] = distance;
				urlsDM[j][i] = distance;
			}
		}
		
		// normalize distances
		m_URLsDM = new float[nUrl][nUrl];
		for(int i=0; i<nUrl; i++){
			float min = Float.POSITIVE_INFINITY;
			float max = Float.NEGATIVE_INFINITY;
			for(int j=0; j<nUrl; j++){
				float val = urlsDM[i][j];
				if(val>max){max=val;}
				if(val<min){min=val;}
			}
			for(int j=0; j<nUrl; j++){
				float val = urlsDM[i][j];
				float val1 = val-min;
				float val2 = max-min;
				if(val2>0){
					m_URLsDM[i][j] = val1/val2;
				} else {
					m_URLsDM[i][j] = 1;
				}
			}
		}
		
	}
	
	private float euclideanDistance(float[] v1, float[] v2){
		if(v1==null || v2==null){
			// the maximum distance is 1,
			// because we are measuring the distance between distributions
			return 1f;
		}
		
		int l = Math.min(v1.length, v2.length);
		double sum = 0d;
		for(int i=0; i<l; i++){
			double posval = Math.pow( (double)v1[i]-(double)v2[i], 2d);
			sum = sum + posval;
		}
		double result = Math.sqrt(sum);
		return (float)result;
	}
	
	private float getDistance(String formalizedURL1, String formalizedURL2){
		int i = m_formurls.indexOf(formalizedURL1);
		int j = m_formurls.indexOf(formalizedURL2);
		if(i<j){
			return m_URLsDM[i][j];
		} else {
			return m_URLsDM[j][i];
		}
	}
	
	public void printUrlDM(){
		int len = m_formurls.size();
		for(int i=0; i<len; i++){
			int urlID = m_urlID.get(i);
			System.out.print(urlID);
			for(int j=0; j<len; j++){
				System.out.print("," + m_URLsDM[i][j]);
			}
			System.out.println();
		}
	}
	
	public void writeUrlDM(String filename){
		File file = new File(filename);
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			int len = m_formurls.size();
			for(int i=0; i<len; i++){
				int urlID = m_urlID.get(i);
				bw.write(String.valueOf(urlID));
				for(int j=0; j<len; j++){
					bw.write(" " + m_URLsDM[i][j]);
				}
				bw.write("\n");
			}
			bw.close();
		} catch (IOException ex){
			System.err.println("Exception at writing the URL list. " + 
					"[ehupatras.webrecommendation.content.preprocess.PreprocessContent.writeURLs]");
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public void computeURL2topic(float minsupport){
		m_url2topic = new int[m_url2topicDist.size()];
		for(int i=0; i<m_url2topicDist.size(); i++){
			float[] topdist = m_url2topicDist.get(i);
			
			float maxprob = -1f;
			int maxj = -1;
			for(int j=0; topdist!=null && j<topdist.length; j++){
				float prob = topdist[j];
				if(maxprob<prob){
					maxprob = prob;
					maxj = j;
				}
			}
			
			if(maxprob>=minsupport){
				m_url2topic[i] = maxj;
			} else {
				m_url2topic[i] = -1;
			}
		}
	}
	
	public void printURL2topic(){
		for(int i=0; i<m_url2topic.length; i++){
			System.out.println(	m_urlID.get(i) + " " +
								m_url2topic[i]);
		}
	}
	
	public void writeURL2topic(String filename){
		File file = new File(filename);
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			int len = m_formurls.size();
			for(int i=0; i<len; i++){
				int urlID = m_urlID.get(i);
				bw.write(String.valueOf(urlID) + " ");
				bw.write(String.valueOf(m_url2topic[i]));
				bw.write("\n");
			}
			bw.close();
		} catch (IOException ex){
			System.err.println("Exception at writing the URL list. " + 
					"[ehupatras.webrecommendation.content.preprocess.PreprocessContent.writeURL2topic]");
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	public void writeURL2topic_contID(String filename){
		File file = new File(filename);
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			for(int i=0; i<m_url2topic.length; i++){
				bw.write(String.valueOf(i+1) + ";");
				bw.write(String.valueOf(m_url2topic[i]+1));
				bw.write("\n");
			}
			bw.close();
		} catch (IOException ex){
			System.err.println("Exception at writing the URL list. " + 
					"[ehupatras.webrecommendation.content.preprocess.PreprocessContent.writeURL2topic]");
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	
	
	private void downloadURL(String readURL, String writeURL){
		try {
			// get URL content
			URL url = new URL(readURL);
			URLConnection conn = url.openConnection();
			
			// open the stream and put it into BufferedReader
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			// save to this filename
			File file = new File(writeURL);
			if (!file.exists()) { file.createNewFile();}
			
			// use FileWriter to write file
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			String inputLine;
			while ((inputLine = br.readLine()) != null) {
				bw.write(inputLine);
			}
			bw.close();
			br.close();
			
			System.out.println(" Done: " + readURL);
			
		} catch(MalformedURLException e){
			e.printStackTrace();
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
}
