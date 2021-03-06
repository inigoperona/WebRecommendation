package ehupatras.webrecommendation.content.preprocess.pagerank;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;;
 
// TODO: Auto-generated Javadoc
/**
 * The Class GooglePageRank.
 */
public class GooglePageRank {
 
  /**
   * The main method.
   *
   * @param args the arguments
   */
  public static void main(String[] args) {
 
	GooglePageRank obj = new GooglePageRank();
	System.out.println(obj.getPR(
			"bidasoaturismo.com/index.php?option=com_content&view=article&id=308&Itemid=9&lang=en&ch=1", 
			"bidasoaturismo.com/index.php?option=com_content&view=article&id=308&Itemid=9&lang=en&ch=1"
			));
 
  }
 
  /**
   * Gets the pr.
   *
   * @param domain the domain
   * @param urlname the urlname
   * @return the pr
   */
  public int getPR(String domain, String urlname) {
 
	  System.out.println(domain);
	  System.out.println(urlname);
	  
	String result = "";
 
	JenkinsHash jenkinsHash = new JenkinsHash();
	long hash = jenkinsHash.hash(("info:" + domain).getBytes());
 
	//Append a 6 in front of the hashing value.
	String url = "http://toolbarqueries.google.com/tbr?client=navclient-auto&hl=en&"
	   + "ch=6" + hash + "&ie=UTF-8&oe=UTF-8&features=Rank&q=info:" + domain;
 
	System.out.println("Sending request to : " + url);
 
	try {
		URLConnection conn = new URL(url).openConnection();
 
		BufferedReader br = new BufferedReader(new InputStreamReader(
			conn.getInputStream()));
 
		String input;
		while ((input = br.readLine()) != null) {
 
			// What Google returned? Example : Rank_1:1:9, PR = 9
			System.out.println(input);
 
			result = input.substring(input.lastIndexOf(":") + 1);
		}
 
	} catch (Exception e) {
		System.out.println(e.getMessage());
	}
 
	if ("".equals(result)) {
		return 0;
	} else {
		return Integer.valueOf(result);
	}
 
  }
 
}
