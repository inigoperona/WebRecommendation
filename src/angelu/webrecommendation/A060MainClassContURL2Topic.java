package angelu.webrecommendation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class A060MainClassContURL2Topic.
 */
public class A060MainClassContURL2Topic {
    
    /**
     * The main method.
     *
     * @param args the arguments
     */
    public static void main(String args[]){
    	
		// Parameter control
		String base = "convert_UrlIDs_content2usage/angelu";
		String contentWD = base + "/document-topic_distributions";
		String infileUrl2topicDist = "/document-topic-distributions1_Modua0_contentID.csv";
		String outfileUrl2topic = "/URLs_to_topic_m0.txt";
		
		contentWD = args[0];
		infileUrl2topicDist = args[1];
		outfileUrl2topic = args[2];
		
		
		A060MainClassContURL2Topic me = new A060MainClassContURL2Topic();
		Object[] objA = me.readURL2TopicDistribution(contentWD + infileUrl2topicDist);
		ArrayList<Integer> urlIDs = (ArrayList<Integer>)objA[0];
		ArrayList<float[]> url2TD = (ArrayList<float[]>)objA[1];
		int[] url2topic = me.computeURL2topic(0.0f, url2TD);
		me.writeURL2topic(contentWD + outfileUrl2topic, urlIDs, url2topic);
    }
    
	/**
	 * Read ur l2 topic distribution.
	 *
	 * @param filename the filename
	 * @return the object[]
	 */
	public Object[] readURL2TopicDistribution(String filename){
		// Read topic information
		ArrayList<Integer> urlIDs = new ArrayList<Integer>();
		ArrayList<float[]> url2topicDist = new ArrayList<float[]>();
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
				urlIDs.add(urlid);
				url2topicDist.add(topdist);
			}
			br.close();
		} catch (IOException ex){
			System.err.println("Exception at loading the URL list. " + 
					"[angelu.webrecommendation.PreprocessContent.A060MainClassContURL2Topic]");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// return
		Object[] objA = new Object[2];
		objA[0] = urlIDs;
		objA[1] = url2topicDist;
		return objA;
	}
	
	/**
	 * Compute ur l2topic.
	 *
	 * @param minsupport the minsupport
	 * @param url2topicDist the url2topic dist
	 * @return the int[]
	 */
	public int[] computeURL2topic(float minsupport, ArrayList<float[]> url2topicDist){
		int[] url2topic = new int[url2topicDist.size()];
		for(int i=0; i<url2topicDist.size(); i++){
			float[] topdist = url2topicDist.get(i);
			
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
				url2topic[i] = maxj;
			} else {
				url2topic[i] = -1;
			}
		}
		return url2topic;
	}
	
	/**
	 * Write ur l2topic.
	 *
	 * @param filename the filename
	 * @param urlIDs the url i ds
	 * @param url2topic the url2topic
	 */
	public void writeURL2topic(
			String filename,
			ArrayList<Integer> urlIDs,
			int[] url2topic){
		File file = new File(filename);
		try {
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			int len = urlIDs.size();
			for(int i=0; i<len; i++){
				int urlID = urlIDs.get(i);
				bw.write(String.valueOf(urlID+1) + ";");
				bw.write(String.valueOf(url2topic[i]+1));
				bw.write("\n");
			}
			bw.close();
		} catch (IOException ex){
			System.err.println("Exception at writing the url2topic list. " + 
					"[angelu.webrecommendation.A060MainClassContURL2Topic.writeURL2topic]");
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
}
