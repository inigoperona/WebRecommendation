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
		System.out.println("  [" + System.currentTimeMillis() + "] Start writing txt sequencesInstantiated. ");
		
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
			System.out.println("  [" + System.currentTimeMillis() + "]   Get sequencesID. ");
			ArrayList<String> keysOrd = getSequencesIDs();
			System.out.println("  [" + System.currentTimeMillis() + "]   Get all sequences. ");
			ArrayList<String[]> sequences = getSequencesInstanciated(keysOrd);
			
			for(int i=0; i<sequences.size(); i++){
				// write the sessionID
				String sessionID = keysOrd.get(i);
				writer.write(String.valueOf(sessionID));
				
				// write the URLs
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
	
	public static void writeSequencesInstanciated2(String outfilename){
		System.out.println("  [" + System.currentTimeMillis() + "] Start writing txt sequencesInstantiated. ");
		
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
			System.out.println("  [" + System.currentTimeMillis() + "]   Get sequencesID. ");
			ArrayList<String> keysOrd = getSequencesIDs();
			//System.out.println("  [" + System.currentTimeMillis() + "]   Get all sequences. ");
			//ArrayList<String[]> sequences = getSequencesInstanciated(keysOrd);
			
			// First ordered all requests we need
			ArrayList<Integer> requestIDs = new ArrayList<Integer>();
			for(int i=0; i<keysOrd.size(); i++){
				String sessionID = keysOrd.get(i);
				ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
				for(int j=0; j<sequence.size(); j++){
					// get the index of the request
					int reqind = sequence.get(j).intValue();
					
					// get near to order fastly
					int k = 0;
					int kold = 0;
					for(; k<requestIDs.size();){
						int ireqind = requestIDs.get(k);
						if(reqind<ireqind){
							break;
						}
						kold = k;
						k = k + 1000;
					}
					
					// fine search
					k = kold;
					for(; k<requestIDs.size(); k++){
						int ireqind = requestIDs.get(k);
						if(reqind<ireqind){
							break;
						}
					}
					
					// add ordered the request index
					requestIDs.add(k, reqind);
				}
			}
			
			// Access to the information we need
			Hashtable<Integer,String> requestsInfo = new Hashtable<Integer,String>();
			for(int i=0; i<requestIDs.size(); i++){
				int reqind = requestIDs.get(i);
				Request req = WebAccess.getRequest(reqind);
				String urlname = req.getFormatedUrlName();
				Page pag = Website.getPage(urlname);
				int urlid = pag.getUrlIDusage();
				//String pagrole = req.getPageRoleUHC();
				//String seqelem = String.format("%06d%s", urlid, pagrole);
				String seqelem = String.format("%06d", urlid);
				requestsInfo.put(reqind, seqelem);
			}
			
			// write the information
			for(int i=0; i<keysOrd.size(); i++){
				// write the sessionID
				String sessionID = keysOrd.get(i);
				writer.write(String.valueOf(sessionID));
				
				// write the clicks
				ArrayList<Integer> sequenceReqInd = WebAccessSequences.getSession(sessionID);
				for(int j=0; j<sequenceReqInd.size(); j++){
					int reqind = sequenceReqInd.get(j).intValue();
					String urlStr = requestsInfo.get(reqind);
					writer.write("," + urlStr);
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
	
	public static void writeSequencesInstanciated3(String outfilename){
		System.out.println("  [" + System.currentTimeMillis() + "] Start writing txt sequencesInstantiated. ");
		
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
			System.out.println("  [" + System.currentTimeMillis() + "]   Get sequencesID. ");
			ArrayList<String> keysOrd = getSequencesIDs();
			//System.out.println("  [" + System.currentTimeMillis() + "]   Get all sequences. ");
			//ArrayList<String[]> sequences = getSequencesInstanciated(keysOrd);
			
			// First ordered all requests we need
			ArrayList<Integer> requestIDs = new ArrayList<Integer>();
			for(int i=0; i<keysOrd.size(); i++){
				String sessionID = keysOrd.get(i);
				ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
				for(int j=0; j<sequence.size(); j++){
					// get the index of the request
					int reqind = sequence.get(j).intValue();
					
					// get near to order fastly
					int k = 0;
					int kold = 0;
					for(; k<requestIDs.size();){
						int ireqind = requestIDs.get(k);
						if(reqind<ireqind){
							break;
						}
						kold = k;
						k = k + 1000;
					}
					
					// fine search
					k = kold;
					for(; k<requestIDs.size(); k++){
						int ireqind = requestIDs.get(k);
						if(reqind<ireqind){
							break;
						}
					}
					
					// add ordered the request index
					requestIDs.add(k, reqind);
				}
			}
			
			// write the information
			for(int i=0; i<keysOrd.size(); i++){
				// write the sessionID
				String sessionID = keysOrd.get(i);
				writer.write(String.valueOf(sessionID));
				
				// write the clicks
				ArrayList<Integer> sequenceReqInd = WebAccessSequences.getSession(sessionID);
				for(int j=0; j<sequenceReqInd.size(); j++){
					int reqind = sequenceReqInd.get(j).intValue();
					Request req = WebAccess.getRequest(reqind);
					String urlname = req.getFormatedUrlName();
					Page pag = Website.getPage(urlname);
					int urlid = pag.getUrlIDusage();
					writer.write("," + urlid);
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
	public static ArrayList<String[]> getSequencesInstanciated(ArrayList<String> sessionIDs){
		// First ordered all requests we need
		ArrayList<Integer> requestIDs = new ArrayList<Integer>();
		for(int i=0; i<sessionIDs.size(); i++){
			String sessionID = sessionIDs.get(i);
			ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
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
			Request req = WebAccess.getRequest(reqind);
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
			String sessionID = sessionIDs.get(i);
			ArrayList<Integer> sequenceReqInd = WebAccessSequences.getSession(sessionID);
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
