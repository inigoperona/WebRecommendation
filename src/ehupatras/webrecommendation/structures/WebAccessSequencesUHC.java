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


	public static void writeSequencesInstanciated(String outfilename){
		WebAccessSequencesUHC.writeSequencesInstanciated(outfilename, true);
	}
	
	protected static void writeSequencesInstanciated(String outfilename, boolean writeRoles){
		System.out.println("  [" + System.currentTimeMillis() + "] Start writing txt sequencesInstantiated. ");
		
		// get sessionsID
		System.out.println("  [" + System.currentTimeMillis() + "]   Get sequencesID. ");
		ArrayList<String> keysOrd = getSequencesIDs();
		
		// order all requests
		int[] reqindexesA = WebAccessSequences.sesIDs2reqIndexList(keysOrd);
		
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
			// write the information
			Hashtable<Integer,String> requestsInfo = new Hashtable<Integer,String>();
			int k = 0;
			for(int i=0; i<keysOrd.size(); i++){
				// write the sessionID
				String sessionID = keysOrd.get(i);
				writer.write(String.valueOf(sessionID));
				
				// store the information we need
				ArrayList<Integer> sequenceReqInd = WebAccessSequences.getSession(sessionID);
				int lenses = sequenceReqInd.size();
				int lastReqInd = sequenceReqInd.get(lenses-1);
				for(; k<reqindexesA.length; k++){
					int reqind = reqindexesA[k];
					if(reqind>lastReqInd){
						break;
					}
					Request req = WebAccess.getRequest(reqind);
					String urlname = req.getFormatedUrlName();
					int urlid = Website.getURLID(urlname);
					String seqelem = String.format("%06d", urlid);
					if(writeRoles){
						String pagrole = req.getPageRoleUHC();
						seqelem = seqelem + pagrole;
					}
					requestsInfo.put(reqind, seqelem);
				}
				
				// write the clicks
				for(int j=0; j<sequenceReqInd.size(); j++){
					int reqind = sequenceReqInd.get(j);
					String urlidstr = requestsInfo.get(reqind);
					writer.write("," + urlidstr);
					requestsInfo.remove(reqind);
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
	
	public static ArrayList<String[]> getSequencesInstanciated(ArrayList<String> sessionIDs){
		return WebAccessSequencesUHC.getSequencesInstanciated(sessionIDs, true);
	}
	
	protected static ArrayList<String[]> getSequencesInstanciated(ArrayList<String> sessionIDs, boolean writeRoles){
		// get sessionsID
		System.out.println("  [" + System.currentTimeMillis() + "]   Get sequencesID. ");
		ArrayList<String> keysOrd = getSequencesIDs();
				
		// order all requests
		int[] reqindexesA = WebAccessSequences.sesIDs2reqIndexList(keysOrd);
				
		// save the information
		ArrayList<String[]> resultSequences = new ArrayList<String[]>();
		Hashtable<Integer,String> requestsInfo = new Hashtable<Integer,String>();
		int k = 0;
		for(int i=0; i<keysOrd.size(); i++){
			String sessionID = keysOrd.get(i);
			
			// store the information we need
			ArrayList<Integer> sequenceReqInd = WebAccessSequences.getSession(sessionID);
			int lenses = sequenceReqInd.size();
			int lastReqInd = sequenceReqInd.get(lenses-1);
			for(; k<reqindexesA.length; k++){
				int reqind = reqindexesA[k];
				if(reqind>lastReqInd){
					break;
				}
				Request req = WebAccess.getRequest(reqind);
				String urlname = req.getFormatedUrlName();
				Page pag = Website.getPage(urlname);
				int urlid = pag.getUrlIDusage();
				String seqelem = String.format("%06d", urlid);
				if(writeRoles){
					String pagrole = req.getPageRoleUHC();
					seqelem = seqelem + pagrole;
				}
				requestsInfo.put(reqind, seqelem);
			}
			
			// write the clicks
			String[] sequenceUHC = new String[sequenceReqInd.size()];
			for(int j=0; j<sequenceReqInd.size(); j++){				
				int reqind = sequenceReqInd.get(j);
				sequenceUHC[j] = requestsInfo.get(reqind);
				requestsInfo.remove(reqind);
			}
			resultSequences.add(sequenceUHC);
		}
		
		return resultSequences;
	}
	
}
