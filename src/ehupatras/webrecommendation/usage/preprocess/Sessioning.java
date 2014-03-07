package ehupatras.webrecommendation.usage.preprocess;

import ehupatras.webrecommendation.structures.Request;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.structures.Page;

import java.util.*;

public class Sessioning {
	
	// expireSessionsInMin: We suppose after this time that a new session starts.
	// expireSessionsInMin: We split up sequences in two sessions when we see this jump of time between clicks.
	public void createSessions(int expireSessionsInMin){
		Hashtable<Integer,Object[]> oldrequests = new Hashtable<Integer,Object[]>();
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			if(i%100000==0){
				System.out.println("  " + i + "/" + WebAccessSequences.filteredlogsize() +
						" analyzed [createSessions]");
			}
			Request actualreq = WebAccessSequences.getRequest(i);
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
					Request oldreq = WebAccessSequences.getRequest(oldindex);
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
					Request oldreq = WebAccessSequences.getRequest(oldindex);
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
			Request oldreq = WebAccessSequences.getRequest(oldindex);
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
			Request actualreq = WebAccessSequences.getRequest(i);
			int actualsessionid = actualreq.getSessionID();
			String urlname = actualreq.getFormatedUrlName();
			Page pag = Website.getPage(urlname);
			int actualUrl = pag.getUrlIDusage();
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
					Request oldreq = WebAccessSequences.getRequest(oldindex);
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
			Request oldreq = WebAccessSequences.getRequest(oldindex);
			oldreq.setElapsedTime(sum);
			WebAccessSequences.replaceRequest(oldindex, oldreq);
		}
	}
		
	public void createSequences(){
		// to measure the proportion of valid URLs in a session
		Hashtable<Integer,Integer[]> validnessOfSequences = new Hashtable<Integer,Integer[]>();
		
		int sequencecounter = 0;
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			Request req = WebAccessSequences.getRequest(i);
			int sessionID = req.getSessionID();
			int nvalid = 0;
			int len = 0;
			if(	req.getIsSuitableToLinkPrediction() ){
				if( WebAccessSequences.m_sequences.containsKey(sessionID) ){
					ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
					sequence.add(i);
					WebAccessSequences.m_sequences.put(sessionID,sequence);
					
					// validness
					Integer[] objA = validnessOfSequences.get(sessionID);
					nvalid = objA[0] + 1;
					len = objA[1];
				} else{
					ArrayList<Integer> sequence = new ArrayList<Integer>();
					sequence.add(i);
					WebAccessSequences.m_sequences.put(sessionID,sequence);
					sequencecounter++;
				}
				len++;
				Integer[] newObjA = new Integer[2];
				newObjA[0] = nvalid;
				newObjA[1] = len;
				validnessOfSequences.put(sessionID, newObjA);
			}
		}
		System.out.println("  " + sequencecounter + " sequences created.");
		
		// update validness
		Enumeration<Integer> keys = validnessOfSequences.keys();
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement();
			Integer[] freqData = validnessOfSequences.get(sessionID);
			int nvalid = freqData[0];
			int len = freqData[1];
			float prob = (float)nvalid/(float)len;
			WebAccessSequences.m_validnessOfSequences.put(sessionID, prob);
		}
	}
	
	public void ensureMinimumValidURLs(float pValid){
		Enumeration<Integer> keys = WebAccessSequences.m_sequences.keys();
		int removecounter = 0;
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement().intValue();
			float pValidSeq = WebAccessSequences.m_validnessOfSequences.get(sessionID);
			if(pValidSeq<pValid){
				WebAccessSequences.m_sequences.remove(sessionID);
				removecounter++;
			}
		}
		System.out.println("  " + removecounter + " sequences removed.");
	}
	
	public void removeConsecutiveSameURLs(){
		// store all request_indexes
		ArrayList<Integer> reqindexes = new ArrayList<Integer>();
		Enumeration<Integer> keys = WebAccessSequences.m_sequences.keys();
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement().intValue();
			ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);			
			for(int i=0; i<sequence.size(); i++){
				reqindexes.add(sequence.get(i));
			}
		}
		int[] reqindexesA = new int[reqindexes.size()];
		for(int i=0; i<reqindexes.size(); i++){ reqindexesA[i] = reqindexes.get(i); }
		Arrays.sort(reqindexesA);
		
		// access to request data and take the URLname
		Hashtable<Integer,String> req2url = new Hashtable<Integer,String>();
		for(int i=0; i<reqindexesA.length; i++){
			int reqi = reqindexesA[i];
			Request req = WebAccessSequences.getRequest(reqi);
			String urlname = req.getFormatedUrlName();
			req2url.put(reqi, urlname);
		}
		
		// when consecutive same URL find remove the following ones
		Enumeration<Integer> keys2 = WebAccessSequences.m_sequences.keys();
		int removecounter = 0;
		while(keys2.hasMoreElements()){
			int sessionID = keys2.nextElement().intValue();
			ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
			ArrayList<Integer> sequence2 = new ArrayList<Integer>();
			String urlnameold = "";
			for(int i=0; i<sequence.size(); i++){
				int reqi = sequence.get(i);
				String urlname = req2url.get(reqi);
				if(i==0){
					sequence2.add(reqi);
					urlnameold = urlname;
				} else {
					if(!urlnameold.equals(urlname)){
						sequence2.add(reqi);
						urlnameold = urlname;
					} else {
						removecounter++;
					}
				}
			}
			WebAccessSequences.m_sequences.put(sessionID, sequence2);	
		}
		System.out.println("  " + removecounter + " requests were removed.");
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
	
	public void computePageRoleUHC_time(int shortTimeSeconds, int hubMaxTimeSeconds, int contentMaxTimeSeconds){
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
			Request req = WebAccessSequences.getRequest(reqind);
			
			// time based role
			float elapsedtime = req.getElapsedTime()/(float)1000;
			if(elapsedtime<=shortTimeSeconds){ // Unimportant
				req.setPageRoleUHC("U");
			} else{
				if(elapsedtime<=hubMaxTimeSeconds){ // Hub
					req.setPageRoleUHC("H");
				} else {
					if(elapsedtime<=contentMaxTimeSeconds){ // Content
						req.setPageRoleUHC("C");
					} else { // long time in a URL also Unimportant
						req.setPageRoleUHC("U");
					}
				}
			}
				
			// index type pages are unimportant
			String urlname = req.getFormatedUrlName();
			Page pag = Website.getPage(urlname);
			if(pag.getIsIndex()){
				req.setPageRoleUHC("U");
			}
				
			// Update the request with the page role
			WebAccessSequences.replaceRequest(reqind, req);
		}
	}
	
	
	public void computePageRoleUHC_removeOnlyUnimportant(float pMaxUnimportant){
		// store all request_indexes
		ArrayList<Integer> reqindexes = new ArrayList<Integer>();
		Enumeration<Integer> keys = WebAccessSequences.m_sequences.keys();
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement().intValue();
			ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);			
			for(int i=0; i<sequence.size(); i++){
				reqindexes.add(sequence.get(i));
			}
		}
		int[] reqindexesA = new int[reqindexes.size()];
		for(int i=0; i<reqindexes.size(); i++){ reqindexesA[i] = reqindexes.get(i); }
		Arrays.sort(reqindexesA);
		
		// access to request data and take the URLname
		Hashtable<Integer,String> req2role = new Hashtable<Integer,String>();
		for(int i=0; i<reqindexesA.length; i++){
			int reqi = reqindexesA[i];
			Request req = WebAccessSequences.getRequest(reqi);
			String role = req.getPageRoleUHC();
			req2role.put(reqi, role);
		}
		
		// remove the sequences that have a lot of Unimportant
		Enumeration<Integer> keys2 = WebAccessSequences.m_sequences.keys();
		int removecounter = 0;
		while(keys2.hasMoreElements()){
			int sessionID = keys2.nextElement().intValue();
			ArrayList<Integer> sequence = WebAccessSequences.m_sequences.get(sessionID);
			int nU = 0;
			for(int i=0; i<sequence.size(); i++){
				int reqi = sequence.get(i);
				String role = req2role.get(reqi);
				if(role.equals("U")){
					nU++;
				}
			}
			float pU = (float)nU/(float)sequence.size();
			if(pU>=pMaxUnimportant){
				WebAccessSequences.m_sequences.remove(sessionID);
				removecounter++;
			}	
		}
		System.out.println("  " + removecounter + " sequences were removed.");
	}
	
}
