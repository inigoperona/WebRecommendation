package ehupatras.webrecommendation.usage.preprocess;

import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.structures.page.Page;
import ehupatras.webrecommendation.structures.request.Request;
import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class Sessioning.
 */
public class Sessioning {

	/** The m_max lenght of the sequence. */
	private int m_maxLenghtOfTheSequence = 200;
	
	// expireSessionsInMin: We suppose after this time that a new session starts.
	// expireSessionsInMin: We split up sequences in two sessions when we see this jump of time between clicks.
	/**
	 * Creates the sessions.
	 *
	 * @param expireSessionsInMin the expire sessions in min
	 * @param maxLenghtOfTheSequence the max lenght of the sequence
	 */
	public void createSessions(int expireSessionsInMin, int maxLenghtOfTheSequence){
		m_maxLenghtOfTheSequence = maxLenghtOfTheSequence;
		
		long expiredTimeInSeconds = expireSessionsInMin*60;
		Hashtable<Integer,Object[]> oldrequests = new Hashtable<Integer,Object[]>();
		
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			if(i%100000==0){
				System.out.println("  " + i + "/" + WebAccessSequences.filteredlogsize() +
						" analyzed [createSessions]");
			}
			Request actualreq = WebAccessSequences.getRequest(i);
			int actualuser = actualreq.getUserID();
			long actualtime = actualreq.getTimeInMillis();
			int actualLogFileNumb = actualreq.getLogFileNumber();
			if(oldrequests.containsKey(actualuser)){
				Object[] objA = oldrequests.get(actualuser);
				int oldsessioni = ((Integer)objA[0]).intValue();
				long oldtime = ((Long)objA[1]).longValue();
				int oldindex = ((Integer)objA[2]).intValue();
				float sum = ((Float)objA[3]).floatValue();
				int nreq = ((Integer)objA[4]).intValue();
				int oldLogFileNumb = ((Integer)objA[5]).intValue();
				long elapsedTimeInSeconds = (actualtime-oldtime)/1000;
				if( 	oldLogFileNumb==actualLogFileNumb &&
						nreq<=m_maxLenghtOfTheSequence && 
						elapsedTimeInSeconds<=expiredTimeInSeconds ){
					// this request is in the same session
					float oldelapssedtime = (float)(actualtime-oldtime);
					sum = sum + oldelapssedtime;
					nreq++;
					// we now know the elapsed time, so, update the old requests
					this.updateTheOldRequest(oldindex, oldelapssedtime, actualuser, oldsessioni);
					// Update the user with the actual request information
					Object[] objAr = this.createOldRequestsObj(oldsessioni, actualtime, i, sum, nreq, oldLogFileNumb);
					oldrequests.put(actualuser, objAr);
				} else {
					// update the last request of the previous session
					float oldelapssedtime = nreq==1 ? -1f : sum/(float)(nreq-1);
					this.updateTheOldRequest(oldindex, oldelapssedtime, actualuser, oldsessioni);
					// now start a new session to the user
					oldsessioni++;
					Object[] objAr = this.createOldRequestsObj(oldsessioni, actualtime, i, 0.0f, 1, actualLogFileNumb);
					oldrequests.put(actualuser, objAr);
				}
			} else {
				Object[] objAr = this.createOldRequestsObj(1, actualtime, i, 0.0f, 1, actualLogFileNumb);
				oldrequests.put(actualuser, objAr);
			}
		}
		// close the sessions that there are in the hashtable
		// order the keys to optimized the access to each module.
		ArrayList<Integer> keysOrd = this.orderHashtableKeysByData(oldrequests, 2);
		// close the sessions
		System.out.println("  Number of connections to close: " + keysOrd.size());
		for(int i=0; i<keysOrd.size(); i++){
			if(i%100000==0){
				System.out.println("  " + i + "/" + keysOrd.size() +
						" closing connections [createSessions]");
			}
			int userid = keysOrd.get(i).intValue();
			Object[] objA = oldrequests.get(userid);
			int oldsessioni = ((Integer)objA[0]).intValue();
			//long oldtime = ((Long)objA[1]).longValue();
			int oldindex = ((Integer)objA[2]).intValue();
			float sum = ((Float)objA[3]).floatValue();
			int nreq = ((Integer)objA[4]).intValue();
			// update the request registry
			float oldelapssedtime = nreq==1 ? -1 : sum/(float)(nreq-1);
			this.updateTheOldRequest(oldindex, oldelapssedtime, userid, oldsessioni);
		}
	}
	
	/**
	 * Update the old request.
	 *
	 * @param oldindex the oldindex
	 * @param oldelapssedtime the oldelapssedtime
	 * @param actualuser the actualuser
	 * @param oldsessioni the oldsessioni
	 */
	private void updateTheOldRequest(int oldindex, float oldelapssedtime, int actualuser, int oldsessioni){
		Request oldreq = WebAccessSequences.getRequest(oldindex);
		oldreq.setElapsedTime(oldelapssedtime);
		int oldsessionint = actualuser*10000+oldsessioni;
		oldreq.setSessionID(oldsessionint);
		WebAccessSequences.replaceRequest(oldindex, oldreq);
	}
	
	/**
	 * Creates the old requests obj.
	 *
	 * @param sessionNumber the session number
	 * @param starttime the starttime
	 * @param startIndex the start index
	 * @param sessionDuration the session duration
	 * @param sessionNumReqs the session num reqs
	 * @return the object[]
	 */
	private Object[] createOldRequestsObj(int sessionNumber, long starttime, int startIndex, 
			float sessionDuration, int sessionNumReqs,
			int logFileNUmb){
		Object[] objAr = new Object[6];
		objAr[0] = new Integer(sessionNumber); // session number
		objAr[1] = new Long(starttime); // last timestamp
		objAr[2] = new Integer(startIndex); // last request index
		objAr[3] = new Float(sessionDuration); // accumulated sum of elapsed times
		objAr[4] = new Integer(sessionNumReqs); // number of request in the session so far
		objAr[5] = new Integer(logFileNUmb); // the log file number form where the request was stored
		return objAr;
	}
	
	
	
	
	/**
	 * Join consecutive same urls.
	 */
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
		System.out.println("  Start ordering elements: " + oldrequests.size());
		ArrayList<Integer> keysOrd = this.orderHashtableKeysByData(oldrequests, 0);
		// close the join actions
		System.out.println("  Number of connections to close: " + keysOrd.size());
		for(int i=0; i<keysOrd.size(); i++){
			if(i%100000==0){
				System.out.println("  " + i + "/" + keysOrd.size() +
						" closing connections [joinConsecutiveSameUrls]");
			}
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
	
	/**
	 * Order hashtable keys by data.
	 *
	 * @param objHt the obj ht
	 * @param iData the i data
	 * @return the array list
	 */
	private ArrayList<Integer> orderHashtableKeysByData(Hashtable<Integer,Object[]> objHt, int iData){
		Enumeration<Integer> keys = objHt.keys();
		ArrayList<Integer> keysOrd = new ArrayList<Integer>();
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement().intValue();
			Object[] objA1 = objHt.get(sessionID);
			int oldindex1 = ((Integer)objA1[iData]).intValue();
			int mod1 = WebAccessSequences.getModulusAfterGetRequest(oldindex1);
			int i = 0;
			// fast approach to the exact point
			for(; i<keysOrd.size(); i=i+10000){
				int sessionID2 = keysOrd.get(i);
				Object[] objA2 = objHt.get(sessionID2);
				int oldindex2 = ((Integer)objA2[iData]).intValue();
				int mod2 = WebAccessSequences.getModulusAfterGetRequest(oldindex2);
				if(mod1<mod2){
					break;
				}
			}
			i = i>0 ? i-10000 : 0;
			// the very exact point 
			for(; i<keysOrd.size(); i++){
				int sessionID2 = keysOrd.get(i);
				Object[] objA2 = objHt.get(sessionID2);
				int oldindex2 = ((Integer)objA2[iData]).intValue();
				int mod2 = WebAccessSequences.getModulusAfterGetRequest(oldindex2);
				if(mod1<=mod2){
					break;
				}
			}
			keysOrd.add(i, sessionID);
			if(keysOrd.size() % 100000==0){
				System.out.println("  [" + System.currentTimeMillis() + "] Ordering elements: " + keysOrd.size());
			}
		}
		return keysOrd;		
	}
	
	/**
	 * Creates the sequences.
	 */
	public void createSequences(){
		// to measure the proportion of valid URLs in a session
		Hashtable<Integer,Integer[]> validnessOfSequences = new Hashtable<Integer,Integer[]>();
		
		int sequencecounter = 0;
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			Request req = WebAccessSequences.getRequest(i);
			int sessionID = req.getSessionID();
			int nvalid = 0;
			int len = 0;
			boolean isSuitableForLinkPrediction = req.getIsSuitableToLinkPrediction(); 
			if(	isSuitableForLinkPrediction ){
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
	
	/**
	 * Ensure minimum valid ur ls.
	 *
	 * @param pValid the valid
	 */
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
	
	/**
	 * Removes the consecutive same ur ls.
	 */
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
	
	/**
	 * Ensure minimum activity in each sequence.
	 *
	 * @param nclicks the nclicks
	 */
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
	/**
	 * Removes the long sequences.
	 *
	 * @param lengthpercentile the lengthpercentile
	 */
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
		int value = lengthsInOrder.size()>0 ? lengthsInOrder.get(position) : -1;
		System.out.println("  " + value + " is the sequence length of the " +
			"percentile " + lengthpercentile + "%.");
		removeLongSequences(value);
	}
	
	// we assume that this long activity were generated by web robots.
	/**
	 * Removes the long sequences.
	 *
	 * @param nclicks the nclicks
	 */
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
	
	/**
	 * Compute page role uh c_time.
	 *
	 * @param shortTimeSeconds the short time seconds
	 * @param hubMaxTimeSeconds the hub max time seconds
	 * @param contentMaxTimeSeconds the content max time seconds
	 */
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
	
	
	/**
	 * Compute page role uh c_remove only unimportant.
	 *
	 * @param pMaxUnimportant the max unimportant
	 */
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
	
	
	
	public void selectFixedNumberOfSequences(int nses){
		Enumeration<Integer> keys = WebAccessSequences.m_sequences.keys();
		int counter = 0;
		
		// admit the first sessions
		while(keys.hasMoreElements()){
			keys.nextElement();
			counter++;
			if(counter>=nses){
				break;
			}
		}
		
		// remove the last sessions
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement().intValue();
			WebAccessSequences.m_sequences.remove(sessionID);
		}
	}
	
}
