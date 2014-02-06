package ehupatras.webrecommendation.sampling;

import ehupatras.webrecommendation.structures.WebAccessSequences;
import java.util.*;

public class Sampling {
	
	public ArrayList<Integer> getSample(int ncases, long randomseed){
		// ordered sessionsIDs
		ArrayList<Integer> keysOrd = WebAccessSequences.getSequencesIDs();
		
		// take ncases sessionsIDs randomly
		ArrayList<Integer> selectedKeys = new ArrayList<Integer>(); 
		Random rand = new Random(randomseed);
		for(int i=0; i<ncases; i++){
			int pos = rand.nextInt(keysOrd.size());
			int sessionID = keysOrd.get(pos);
			selectedKeys.add(sessionID);
		}
		
		return selectedKeys;
	}
	
}
