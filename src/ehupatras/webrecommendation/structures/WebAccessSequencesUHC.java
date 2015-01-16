package ehupatras.webrecommendation.structures;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import ehupatras.webrecommendation.structures.page.Page;
import ehupatras.webrecommendation.structures.request.Request;

// TODO: Auto-generated Javadoc
/**
 * The Class WebAccessSequencesUHC.
 */
public class WebAccessSequencesUHC 
				extends WebAccessSequences {

	/**
	 * Write sequences instanciated.
	 *
	 * @param outfilename the outfilename
	 */
	public static void writeSequencesInstanciated(String outfilename){
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences_URLwithUHC] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Write the sequences in a file line by line
		try{
			// order the keys
			ArrayList<Integer> keysOrd = getSequencesIDs();			
			ArrayList<String[]> sequences = getSequencesInstanciated(keysOrd);
			
			for(int i=0; i<sequences.size(); i++){
				int sessionID = keysOrd.get(i).intValue();
				writer.write(String.valueOf(sessionID));
				String[] sequence = sequences.get(i);
				for(int j=0; j<sequence.length; j++){
					writer.write("," + sequence[j]);
				}
				writer.write("\n");
			}
			System.out.println("  " + keysOrd.size() + " lines have been written.");
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences_URLwithUHC] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences_URLwithUHC] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	/**
	 * Gets the sequences instanciated.
	 *
	 * @param sessionIDs the session i ds
	 * @return the sequences instanciated
	 */
	public static ArrayList<String[]> getSequencesInstanciated(ArrayList<Integer> sessionIDs){
		// First ordered all requests we need
		ArrayList<Integer> requestIDs = new ArrayList<Integer>();
		for(int i=0; i<sessionIDs.size(); i++){
			int sessionID = sessionIDs.get(i).intValue();
			ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
			for(int j=0; j<sequence.size(); j++){
				int reqind = sequence.get(j).intValue();
				int k;
				for(k=0; k<requestIDs.size(); k++){
					int ireqind = requestIDs.get(k);
					if(reqind<ireqind){
						break;
					}
				}
				requestIDs.add(k, reqind);
			}
		}
		
		// Access to the information we need
		Hashtable<Integer,String> requestsInfo = new Hashtable<Integer,String>();
		for(int i=0; i<requestIDs.size(); i++){
			int reqind = requestIDs.get(i);
			Request req = WebAccessSequences.getRequest(reqind);
			String urlname = req.getFormatedUrlName();
			Page pag = Website.getPage(urlname);
			int urlid = pag.getUrlIDusage();
			String pagrole = req.getPageRoleUHC();
			String seqelem = String.format("%06d%s", urlid, pagrole);
			requestsInfo.put(reqind, seqelem);
		}
		
		// create the structure of sequences to return
		ArrayList<String[]> resultSequences = new ArrayList<String[]>();
		for(int i=0; i<sessionIDs.size(); i++){
			int sessionID = sessionIDs.get(i).intValue();
			ArrayList<Integer> sequenceReqInd = WebAccessSequences.m_sequences.get(sessionID);
			String[] sequenceUHC = new String[sequenceReqInd.size()];
			for(int j=0; j<sequenceReqInd.size(); j++){
				int reqind = sequenceReqInd.get(j).intValue();
				sequenceUHC[j] = requestsInfo.get(reqind);
			}
			resultSequences.add(sequenceUHC);
		}
		return resultSequences;
	}
	
}
