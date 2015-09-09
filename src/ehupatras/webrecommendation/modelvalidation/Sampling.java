package ehupatras.webrecommendation.modelvalidation;

import ehupatras.webrecommendation.structures.WebAccessSequences;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class Sampling.
 */
public class Sampling {
	
	/**
	 * Gets the sample.
	 *
	 * @param ncases the ncases
	 * @param randomseed the randomseed
	 * @param allowrepetitions the allowrepetitions
	 * @return the sample
	 */
	public ArrayList<Long> getSample(int ncases, long randomseed, boolean allowrepetitions){
		// ordered sessionsIDs
		ArrayList<Long> keysOrd = WebAccessSequences.getSequencesIDs();
		
		// if more than database size is required
		// then allow repetition of cases
		if(keysOrd.size()<ncases){
			allowrepetitions = true;
		}
		
		// take ncases sessionsIDs randomly
		ArrayList<Long> selectedKeys = new ArrayList<Long>(); 
		Random rand = new Random(randomseed);
		for(int i=0; i<ncases;){
			int pos = rand.nextInt(keysOrd.size());
			long sessionID = keysOrd.get(pos);
			if(allowrepetitions){
				selectedKeys.add(sessionID);
				i++;
			} else {
				if(!selectedKeys.contains(sessionID)){
					selectedKeys.add(sessionID);
					i++;
				}
			}
		}
		return selectedKeys;
	}
	
}
