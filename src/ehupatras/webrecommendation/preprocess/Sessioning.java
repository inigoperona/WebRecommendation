package ehupatras.webrecommendation.preprocess;

import ehupatras.webrecommendation.structures.RequestBidasoaTurismo;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import java.util.*;

public class Sessioning {
	
	public Sessioning(){
		
	}
	
	// expireSessionsInMin: We suppose after this time that a new session starts.
	// expireSessionsInMin: We split up sequences in two sessions when we see this jump of time between clicks.
	public void createSessions(int expireSessionsInMin){
		Hashtable<Integer,Object[]> oldrequests = new Hashtable<Integer,Object[]>();
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			if(i%100000==0){
				System.out.println("  " + i + "/" + WebAccessSequences.filteredlogsize() +
						" analyzed [createSessions]");
			}
			RequestBidasoaTurismo actualreq = WebAccessSequences.getRequest(i);
			int actualuser = actualreq.getUserID();
			long actualtime = actualreq.getTimeInMillis();
			if(oldrequests.containsKey(actualuser)){
				Object[] objA = oldrequests.get(actualuser);
				int oldsessioni = ((Integer)objA[0]).intValue();
				long oldtime = ((Long)objA[1]).longValue();
				int oldindex = ((Integer)objA[2]).intValue();
				float sum = ((Float)objA[3]).floatValue();
				int nreq = ((Integer)objA[4]).intValue();
				if((actualtime-oldtime)/1000 <= expireSessionsInMin*60){
					// this request is in the same session
					float oldelapssedtime = (float)(actualtime-oldtime);
					sum = sum + oldelapssedtime;
					nreq++;
					// we now know the elapsed time, so, update the old requests
					RequestBidasoaTurismo oldreq = WebAccessSequences.getRequest(oldindex);
					oldreq.setElapsedTime(oldelapssedtime);
					int oldsessionint = actualuser*10000+oldsessioni;
					oldreq.setSessionID(oldsessionint);
					WebAccessSequences.replaceRequest(oldindex, oldreq);
					// Update the user with the actual request information
					Object[] objAr = new Object[5];
					objAr[0] = new Integer(oldsessioni); // session number
					objAr[1] = new Long(actualtime); // last timestamp
					objAr[2] = new Integer(i); // last request index
					objAr[3] = new Float(sum); // accumulated sum of elapsed times
					objAr[4] = new Integer(nreq); // number of request in the session so far
					oldrequests.put(actualuser, objAr);
				} else {
					// update the last request of the previous session
					RequestBidasoaTurismo oldreq = WebAccessSequences.getRequest(oldindex);
					float oldelapssedtime = nreq==1 ? -1 : sum/(float)(nreq-1);
					oldreq.setElapsedTime(oldelapssedtime);
					int oldsessionint = actualuser*10000+oldsessioni;
					oldreq.setSessionID(oldsessionint);
					WebAccessSequences.replaceRequest(oldindex, oldreq);
					// now start a new session to the user
					oldsessioni++;
					Object[] objAr = new Object[5];
					objAr[0] = new Integer(oldsessioni); // session number
					objAr[1] = new Long(actualtime); // last timestamp
					objAr[2] = new Integer(i); // last request index
					objAr[3] = new Float(0.0); // accumulated sum of elapsed times
					objAr[4] = new Integer(1); // number of request in the session so far
					oldrequests.put(actualuser, objAr);
				}
			} else {
				Object[] objA = new Object[5];
				objA[0] = new Integer(1); // session number
				objA[1] = new Long(actualtime); // last timestamp
				objA[2] = new Integer(i); // last request index
				objA[3] = new Float((float)0.0); // accumulated sum of elapsed times
				objA[4] = new Integer(1); // number of request in the session so far
				oldrequests.put(actualuser, objA);
			}
		}
		// close the sessions that there are in the hashtable
		// order the keys to optimized the access to each module.
		ArrayList<Integer> keysOrd = WebAccessSequences.orderHashtableKeys(oldrequests.keys());
		// close the sessions
		for(int i=0; i<keysOrd.size(); i++){
			int userid = keysOrd.get(i).intValue();
			Object[] objA = oldrequests.get(userid);
			int oldsessioni = ((Integer)objA[0]).intValue();
			//long oldtime = ((Long)objA[1]).longValue();
			int oldindex = ((Integer)objA[2]).intValue();
			float sum = ((Float)objA[3]).floatValue();
			int nreq = ((Integer)objA[4]).intValue();
			RequestBidasoaTurismo oldreq = WebAccessSequences.getRequest(oldindex);
			float oldelapssedtime = nreq==1 ? -1 : sum/(float)(nreq-1);
			oldreq.setElapsedTime(oldelapssedtime);
			int oldsessionint = userid*10000+oldsessioni;
			oldreq.setSessionID(oldsessionint);
			WebAccessSequences.replaceRequest(oldindex, oldreq);
		}
	}
	
	
	public void joinConsecutiveSameUrls(){
		Hashtable<Integer,Object[]> oldrequests = new Hashtable<Integer,Object[]>();
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			if(i%100000==0){
				System.out.println("  " + i + "/" + WebAccessSequences.filteredlogsize() +
						" analyzed [joinConsecutiveSameUrls]");
			}
			RequestBidasoaTurismo actualreq = WebAccessSequences.getRequest(i);
			int actualsessionid = actualreq.getSessionID();
			int actualUrl = actualreq.getUrlIDusage();
			float actualelapsedtime = actualreq.getElapsedTime();
			if(oldrequests.containsKey(actualsessionid)){
				Object[] objA = oldrequests.get(actualsessionid);
				int oldindex = ((Integer)objA[0]).intValue();
				int oldUrl = ((Integer)objA[1]).intValue();
				float sum = ((Float)objA[2]).floatValue();
				if(actualUrl==oldUrl){
					// there are consecutive same URLs. Join them.
					// update the first URL's data in the consecutive same URL sequence
					sum = sum + actualelapsedtime; // sum the time lasted in the URL
					objA[2] = sum;
					oldrequests.put(actualsessionid, objA);
					
					// update the repeated URL
					actualreq.setElapsedTime((float)-1.0);
					WebAccessSequences.replaceRequest(i, actualreq);
				} else {
					// write the last URLs information
					RequestBidasoaTurismo oldreq = WebAccessSequences.getRequest(oldindex);
					oldreq.setElapsedTime(sum);
					WebAccessSequences.replaceRequest(oldindex, oldreq);
					
					// The URLs are different, so update the last URL
					objA[0] = i;
					objA[1] = actualUrl;
					objA[2] = actualelapsedtime;
					oldrequests.put(actualsessionid, objA);
				}
			} else {
				// enter a new object in the hashtable
				Object[] objA = new Object[3];
				objA[0] = i;
				objA[1] = actualUrl;
				objA[2] = actualelapsedtime;
				oldrequests.put(actualsessionid, objA);
			}
		}
		// close the join actions that remain in the hashtable
		// order the keys to optimized the access to each module.
		ArrayList<Integer> keysOrd = WebAccessSequences.orderHashtableKeys(oldrequests.keys());
		// close the join actions
		for(int i=0; i<keysOrd.size(); i++){
			int sessionID = keysOrd.get(i).intValue();
			Object[] objA = oldrequests.get(sessionID);
			int oldindex = ((Integer)objA[0]).intValue();
			//int oldUrl = ((Integer)objA[1]).intValue();
			float sum = ((Float)objA[2]).floatValue();
			RequestBidasoaTurismo oldreq = WebAccessSequences.getRequest(oldindex);
			oldreq.setElapsedTime(sum);
			WebAccessSequences.replaceRequest(oldindex, oldreq);
		}
	}
		
	public void createSequences(){
		int sequencecounter = 0;
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			RequestBidasoaTurismo req = WebAccessSequences.getRequest(i);
			int sessionID = req.getSessionID();
			if(	req.getIsSuitableToLinkPrediction() ){
				if( WebAccessSequences.m_sequences.containsKey(sessionID) ){
					ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
					sequence.add(i);
					WebAccessSequences.m_sequences.put(sessionID,sequence);
				} else{
					ArrayList<Integer> sequence = new ArrayList<Integer>();
					sequence.add(i);
					WebAccessSequences.m_sequences.put(sessionID,sequence);
					sequencecounter++;
				}
			}
		}
		System.out.println("  " + sequencecounter + " sequences created.");
	}
	
	public void ensureMinimumActivityInEachSequence(int nclicks){
		Enumeration<Integer> keys = WebAccessSequences.m_sequences.keys();
		int removecounter = 0;
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement().intValue();
			ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
			if(sequence.size()<nclicks){
				WebAccessSequences.m_sequences.remove(sessionID);
				removecounter++;
			}
		}
		System.out.println("  " + removecounter + " sequences removed.");
	}
	
	// we assume that this long activity were generated by web robots.
	public void removeLongSequences(float lengthpercentile){
		Enumeration<Integer> keys = WebAccessSequences.m_sequences.keys();
		
		// order the sequences' lengths
		ArrayList<Integer> lengthsInOrder = new ArrayList<Integer>();
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement().intValue();
			ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
			int seqlength = sequence.size();
			int i;
			for(i=0; i<lengthsInOrder.size(); i++){
				int iseqlength = lengthsInOrder.get(i);
				if(seqlength<iseqlength){
					break;
				}
			}
			lengthsInOrder.add(i, seqlength);
		}
		
		// compute the given percentile's position
		int nseqs = lengthsInOrder.size();
		int position = Math.round((float)nseqs*(lengthpercentile/(float)100));
		int value = lengthsInOrder.get(position);
		System.out.println("  " + value + " is the sequence length of the " +
			"percentile " + lengthpercentile + "%.");
		removeLongSequences(value);
	}
	
	// we assume that this long activity were generated by web robots.
	public void removeLongSequences(int nclicks){
		Enumeration<Integer> keys = WebAccessSequences.m_sequences.keys();
		int removecounter = 0;
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement().intValue();
			ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
			if(sequence.size()>=nclicks){
				WebAccessSequences.m_sequences.remove(sessionID);
				removecounter++;
			}
		}
		System.out.println("  " + removecounter + " sequences removed.");
	}
	
	public void computePageRoleUHC_time(int shortTimeSeconds, int hubMaxTimeMinutes, int contentMaxTimeMinutes){
		// get ordered sessionIDs
		ArrayList<Integer> keysOrd = WebAccessSequences.getSequencesIDs();
		
		// take all request indexes and order them.
		ArrayList<Integer> reqIndexes = new ArrayList<Integer>(); 
		for(int i=0; i<keysOrd.size(); i++){
			int sessionID = keysOrd.get(i).intValue();
			ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
			for(int j=0; j<sequence.size(); j++){
				int reqind = sequence.get(j).intValue();
				// add the request-index to a reqIndexes array ordered
				int k;
				for(k=0; k<reqIndexes.size(); k++){
					int kreqind = reqIndexes.get(k).intValue();
					if(reqind<kreqind){
						break;
					}
				}
				reqIndexes.add(k, reqind);
			}
		}
		
		// compute the each request's page's role
		for(int i=0; i<reqIndexes.size(); i++){
			int reqind = reqIndexes.get(i).intValue();
			RequestBidasoaTurismo req = WebAccessSequences.getRequest(reqind);
			
			// time based role
			float elapsedtime = req.getElapsedTime()/(float)1000;
			if(elapsedtime<=shortTimeSeconds){ // Unimportant
				req.setPageRoleUHC("U");
			} else{
				if(elapsedtime<=hubMaxTimeMinutes*60){ // Hub
					req.setPageRoleUHC("H");
				} else {
					if(elapsedtime<=contentMaxTimeMinutes*60){ // Content
						req.setPageRoleUHC("C");
					} else { // long time in a URL also Unimportant
						req.setPageRoleUHC("U");
					}
				}
			}
				
			// index type pages are unimportant
			if(req.getIsIndex()){
				req.setPageRoleUHC("U");
			}
				
			// Update the request with the page role
			WebAccessSequences.replaceRequest(i, req);
		}
	}
	
}
