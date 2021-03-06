package ehupatras.webrecommendation.usage.preprocess;

import ehupatras.webrecommendation.structures.WebAccess;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.structures.page.Page;
import ehupatras.webrecommendation.structures.request.Request;
import ehupatras.webrecommendation.utils.SaveLoadObjects;

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
		//Map<Integer, Object[]> oldrequests = Collections.synchronizedMap(new LinkedHashMap<Integer, Object[]>());
		Hashtable<String,Object[]> oldrequests = new Hashtable<String,Object[]>();
		ArrayList<String> usersIDsAL = new ArrayList<String>();
		
		
		// for each request
		for(int i=0; i<WebAccess.filteredlogsize(); i++){
			// print the situation every X number of processed requests
			if(i%1000000==0){
				System.out.println("  " + i + "/" + WebAccess.filteredlogsize() +
						" analyzed [createSessions]");
				// memory
				int mb = 1024*1024;
				// total memory
				Runtime runtime = Runtime.getRuntime();
				long tm = runtime.totalMemory();
				long fm = runtime.freeMemory();
				int um = (int)((tm-fm)/(long)mb);				
				// oldrequests and usersIDsAL
				SaveLoadObjects slo = new SaveLoadObjects(); 
				int sizeInBytes1 = slo.getSize(oldrequests);
				int sizeInBytes2 = slo.getSize(usersIDsAL);
				int sizeInMegabytes1 = sizeInBytes1 / mb;
				int sizeInMegabytes2 = sizeInBytes2 / mb;
				int sumThis = sizeInMegabytes1 + sizeInMegabytes2;
				// print
				System.out.println(
						"    UsedMemory(MB): " + um +
						  "; createSessions(MB): " + sumThis);
			}
			
			// get the request features
			Request actualreq = WebAccess.getRequest(i);
			String actualuserStr = actualreq.getUserID(); // IP identifier
			long actualtime = actualreq.getTimeInMillis();
			int actualLogFileNumb = actualreq.getLogFileNumber(); // log number
			
			// whether the user already has appeared or not 
			if(oldrequests.containsKey(actualuserStr)){
				// get the previous request's features
				Object[] objA = oldrequests.get(actualuserStr);
				int oldsessioni = ((Integer)objA[0]).intValue();
				long oldtime = ((Long)objA[1]).longValue();
				int oldindex = ((Integer)objA[2]).intValue();
				float sum = ((Float)objA[3]).floatValue();
				int nreq = ((Integer)objA[4]).intValue();
				int oldLogFileNumb = ((Integer)objA[5]).intValue();
				boolean oldIsOpened = ((Boolean)objA[6]).booleanValue();
				long elapsedTimeInSeconds = (actualtime-oldtime)/1000;
				
				// whether the new requests is in the same session or not
				if( 	oldIsOpened &&
						oldLogFileNumb==actualLogFileNumb &&
						nreq<=m_maxLenghtOfTheSequence && 
						elapsedTimeInSeconds<=expiredTimeInSeconds ){
					// this request is in the same session
					float oldelapssedtime = (float)(actualtime-oldtime);
					sum = sum + oldelapssedtime;
					nreq++;
					
					// we now know the elapsed time, so, update the old request
					this.updateTheOldRequest(oldindex, oldelapssedtime, actualuserStr, oldsessioni, false);
					
					// update the user with the actual request information
					Object[] objAr = this.createOldRequestsObj(oldsessioni, actualtime, i, sum, nreq, oldLogFileNumb, true);
					oldrequests.put(actualuserStr, objAr);
				} else {
					// this requests is first of the next session
					// update the last request of the previous session
					float oldelapssedtime = nreq==1 ? -1f : sum/(float)(nreq-1);
					this.updateTheOldRequest(oldindex, oldelapssedtime, actualuserStr, oldsessioni, true);
					
					// now start a new session to the user
					oldsessioni++;
					Object[] objAr = this.createOldRequestsObj(oldsessioni, actualtime, i, 0.0f, 1, actualLogFileNumb, true);
					oldrequests.put(actualuserStr, objAr);
				}
			} else { // the first occurrence of the user
				// start the first session
				Object[] objAr = this.createOldRequestsObj(1, actualtime, i, 0.0f, 1, actualLogFileNumb, true);
				oldrequests.put(actualuserStr, objAr);
			}
			
			// keep isOpened users
			if(!usersIDsAL.contains(actualuserStr)){
				usersIDsAL.add(actualuserStr);
			}
			
			// check all oldrequests if the sessions are closed
			if(i%10000==0){
				// order the keys to optimized the access to each module.
				// ArrayList<Integer> keysOrd = this.orderHashtableKeysByDataUser(oldrequests, 2);
				//Set<Integer> col = oldrequests.keySet();
				//Iterator<Integer> it = col.iterator();
				
				// close the sessions
				for(int j=0; j<usersIDsAL.size(); j++){
					// get the user and its last request's features
					String useridstr = usersIDsAL.get(j);
					Object[] objA = oldrequests.get(useridstr);
					int oldsessioni = ((Integer)objA[0]).intValue();
					long oldtime = ((Long)objA[1]).longValue();
					int oldindex = ((Integer)objA[2]).intValue();
					float sum = ((Float)objA[3]).floatValue();
					int nreq = ((Integer)objA[4]).intValue();
					int oldLogFileNumb = ((Integer)objA[5]).intValue();
					boolean oldIsOpened = ((Boolean)objA[6]).booleanValue();
					
					if(oldIsOpened){
						// analyze the session to close it
						long diffInSeconds = (actualtime-oldtime)/1000;
						if( diffInSeconds>expiredTimeInSeconds ){
							// close the session in the DB
							float oldelapssedtime = nreq==1 ? -1 : sum/(float)(nreq-1);
							this.updateTheOldRequest(oldindex, oldelapssedtime, useridstr, oldsessioni, true);
							
							// close the session in oldrequests
							Object[] objAr = this.createOldRequestsObj(oldsessioni, oldtime, oldindex, sum, nreq, oldLogFileNumb, false);
							oldrequests.put(useridstr, objAr);
							int rmind = usersIDsAL.indexOf(useridstr);
							usersIDsAL.remove(rmind);
						}
					}
				}
			}
		}
		
		// close the sessions that there are in the hashTable
		// order the keys to optimized the access to each module.
		//ArrayList<Integer> keysOrd = this.orderHashtableKeysByDataUser(oldrequests, 2);
		//Set<Integer> col = oldrequests.keySet();
		//Iterator<Integer> it = col.iterator();
		
		// close the sessions
		System.out.println("  Number of connections to close: " + usersIDsAL.size());
		for(int i=0; i<usersIDsAL.size(); i++){
			// write the situation every X number of requests
			if(i%10000==0){
				System.out.println("  " + i + "/" + oldrequests.size() +
						" closing connections [createSessions]");
			}
			
			// get the user and its last request's features
			String useridstr = usersIDsAL.get(i);
			Object[] objA = oldrequests.get(useridstr);
			int oldsessioni = ((Integer)objA[0]).intValue();
			long oldtime = ((Long)objA[1]).longValue();
			int oldindex = ((Integer)objA[2]).intValue();
			float sum = ((Float)objA[3]).floatValue();
			int nreq = ((Integer)objA[4]).intValue();
			int oldLogFileNumb = ((Integer)objA[5]).intValue();
			boolean oldIsOpened = ((Boolean)objA[6]).booleanValue();
			
			if(oldIsOpened){
				// update the request registry
				float oldelapssedtime = nreq==1 ? -1 : sum/(float)(nreq-1);
				this.updateTheOldRequest(oldindex, oldelapssedtime, useridstr, oldsessioni, true);
			}
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
	private void updateTheOldRequest(
			int oldindex, float oldelapssedtime, 
			String actualuserStr, int oldsessioni,
			boolean isTheEndOfTheSession){
		Request oldreq = WebAccess.getRequest(oldindex);
		oldreq.setElapsedTime(oldelapssedtime);
		// compute the sessionID and update
		String oldsessioniStr = String.format("%04d", oldsessioni);
		String oldsessionIDStr = actualuserStr + oldsessioniStr;
		//long oldsessionint = (long)actualuser*10000l+(long)oldsessioni; // create the sessions ID
		oldreq.setSessionID(oldsessionIDStr);
		oldreq.setIsTheEndOfTheSession(isTheEndOfTheSession);
		WebAccess.replaceRequest(oldindex, oldreq);
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
	private Object[] createOldRequestsObj(
			int sessionNumber, long starttime, int startIndex, 
			float sessionDuration, int sessionNumReqs,
			int logFileNUmb, 
			boolean isOpened){
		Object[] objAr = new Object[7];
		objAr[0] = new Integer(sessionNumber); // session number
		objAr[1] = new Long(starttime); // last timestamp
		objAr[2] = new Integer(startIndex); // last request index
		objAr[3] = new Float(sessionDuration); // accumulated sum of elapsed times
		objAr[4] = new Integer(sessionNumReqs); // number of request in the session so far
		objAr[5] = new Integer(logFileNUmb); // the log file number form where the request was stored
		objAr[6] = new Boolean(isOpened); // this requests id the last of the session. The session is closed
		return objAr;
	}
	
	
	
	
	/**
	 * Join consecutive same urls.
	 */
	public void joinConsecutiveSameUrls(){
		Hashtable<String,Object[]> oldrequests = new Hashtable<String,Object[]>();
		
		// for each request
		for(int i=0; i<WebAccess.filteredlogsize(); i++){
			// print the situation every X number of processed requests
			if(i%1000000==0){
				System.out.println("  " + i + "/" + WebAccess.filteredlogsize() +
						" analyzed [joinConsecutiveSameUrls]");
				// memory
				int mb = 1024*1024;
				// total memory
				Runtime runtime = Runtime.getRuntime();
				long tm = runtime.totalMemory();
				long fm = runtime.freeMemory();
				int um = (int)((tm-fm)/(long)mb);
				// oldrequests and usersIDsAL
				SaveLoadObjects slo = new SaveLoadObjects(); 
				int sizeInBytes1 = slo.getSize(oldrequests);
				int sizeInMegabytes1 = sizeInBytes1 / mb;
				// print
				System.out.println(
						"    UsedMemory(MB): " + um +
						  "; joinConsecutiveSameUrls(MB): " + sizeInMegabytes1);
			}
			
			// get the request features
			Request actualreq = WebAccess.getRequest(i);
			String actualsessionid = actualreq.getSessionID();
			String urlname = actualreq.getFormatedUrlName();
			boolean isTheEndOfTheSession = actualreq.getIsTheEndOfTheSession();
			int actualUrlID = Website.getURLID(urlname);
			float actualelapsedtime = actualreq.getElapsedTime();
			
			// whether the session already has appeared or not
			if(oldrequests.containsKey(actualsessionid)){
				// get the previous request's features
				Object[] objA = oldrequests.get(actualsessionid);
				int oldindex = ((Integer)objA[0]).intValue();
				int oldUrlID = ((Integer)objA[1]).intValue();
				float sum = ((Float)objA[2]).floatValue();
				
				// whether the consecutive URLs are equal or not
				if(actualUrlID==oldUrlID){
					// there are consecutive same URLs. Join them.
					// update the first URL's data in the consecutive same URL sequence
					sum = sum + actualelapsedtime; // sum the time lasted in the URL
					objA[2] = sum;
					oldrequests.put(actualsessionid, objA);
					
					// update the repeated URL
					actualreq.setElapsedTime((float)-2.0);
					WebAccess.replaceRequest(i, actualreq);
				} else {
					// the next one is a different URL
					// write the last URLs information
					Request oldreq = WebAccess.getRequest(oldindex);
					
					// Adapt the ET of consecutive URLs to the ET median of EHU's logs= 14s
					if(sum>14000){
						sum=14000;
					}
					
					oldreq.setElapsedTime(sum);
					WebAccess.replaceRequest(oldindex, oldreq);
					
					// The URLs are different, so update the last URL
					objA[0] = i;
					objA[1] = actualUrlID;
					objA[2] = actualelapsedtime;
					oldrequests.put(actualsessionid, objA);
				}
			} else {
				// the first occurrence of the session
				// enter a new object in the hashTable
				Object[] objA = new Object[3];
				objA[0] = i;
				objA[1] = actualUrlID;
				objA[2] = actualelapsedtime;
				oldrequests.put(actualsessionid, objA);
			}
			
			// if it is the last request of the session
			if(isTheEndOfTheSession){
				oldrequests.remove(actualsessionid);
			}
		}
	}
	

	/*
	private ArrayList<Integer> orderHashtableKeysByDataUser(Hashtable<Integer,Object[]> objHt, int iData){
		Enumeration<Integer> keys = objHt.keys();
		ArrayList<Integer> keysOrd = new ArrayList<Integer>();
		while(keys.hasMoreElements()){
			// get the userID or sessionID
			int userID = keys.nextElement().intValue();
			
			// get the info stored about the user/session
			Object[] objA1 = objHt.get(userID);
			int oldindex1 = ((Integer)objA1[iData]).intValue();
			int mod1 = WebAccessSequences.getModulusAfterGetRequest(oldindex1);
			
			// fast approach to the exact point
			int i = 0;
//			int a = 10;
//			int b = 0;
//			for(; i<keysOrd.size();){
//				int userID2 = keysOrd.get(i);
//				Object[] objA2 = objHt.get(userID2);
//				int oldindex2 = ((Integer)objA2[iData]).intValue();
//				int mod2 = WebAccessSequences.getModulusAfterGetRequest(oldindex2);
//				if(mod1<mod2){
//					break;
//				}
//				i = (int)Math.pow((double)a, (double)b);
//				b++;
//			}
//			i = (int)Math.pow((double)a, (double)(b-1));
//			i = i>=1 ? i : 0;
			
			for(; i<keysOrd.size(); i=i+10000){
				int userID2 = keysOrd.get(i);
				Object[] objA2 = objHt.get(userID2);
				int oldindex2 = ((Integer)objA2[iData]).intValue();
				int mod2 = WebAccessSequences.getModulusAfterGetRequest(oldindex2);
				if(mod1<mod2){
					break;
				}
			}
			i = i>0 ? i-10000 : 0;
			
			// the very exact point 
			for(; i<keysOrd.size(); i++){
				int userID2 = keysOrd.get(i);
				Object[] objA2 = objHt.get(userID2);
				int oldindex2 = ((Integer)objA2[iData]).intValue();
				int mod2 = WebAccessSequences.getModulusAfterGetRequest(oldindex2);
				if(mod1<=mod2){
					break;
				}
			}
			keysOrd.add(i, userID);
			
			// print the situation
			if(keysOrd.size() % 100000==0){
				System.out.println("  [" + System.currentTimeMillis() + "] Ordering elements: " + keysOrd.size());
			}
		}
		return keysOrd;		
	}
	*/
	
	/*
	private ArrayList<Long> orderHashtableKeysByDataSession(Hashtable<Long,Object[]> objHt, int iData){
		Enumeration<Long> keys = objHt.keys();
		ArrayList<Long> keysOrd = new ArrayList<Long>();
		while(keys.hasMoreElements()){
			long sessionID = keys.nextElement().longValue();
			Object[] objA1 = objHt.get(sessionID);
			int oldindex1 = ((Integer)objA1[iData]).intValue();
			int mod1 = WebAccessSequences.getModulusAfterGetRequest(oldindex1);
			int i = 0;
			// fast approach to the exact point
			for(; i<keysOrd.size(); i=i+10000){
				long sessionID2 = keysOrd.get(i);
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
				long sessionID2 = keysOrd.get(i);
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
	*/
	
	/**
	 * Creates the sequences.
	 */
	public void createSequences(){
		System.out.println("  [" + System.currentTimeMillis() + "] Start creating sequences.");
		
		// load get is suitable for link prediction
		System.out.println("    [" + System.currentTimeMillis() + "] Load all Website url-valid information.");
		Website.loadIsSuitableToLinkPrediction();
		
		// temporal for storing sequences and valideness
		// to measure the proportion of valid URLs in a session
		// [0]: No. valid clicks; [1]: Original length of the sequence;
		HashMap<String,Object[]> sequencesDATA = new HashMap<String,Object[]>();
		
		int sequencecounter = 0;
		// for each request
		for(int i=0; i<WebAccess.filteredlogsize(); i++){
			// print the situation every X number of processed requests
			if(i%1000000==0){
				System.out.println("  " + i + "/" + WebAccess.filteredlogsize() +
						" analyzed [createSequences]");
				// memory
				int mb = 1024*1024;
				// total memory
				Runtime runtime = Runtime.getRuntime();
				long tm = runtime.totalMemory();
				long fm = runtime.freeMemory();
				int um = (int)((tm-fm)/(long)mb);
				// oldrequests and usersIDsAL
				int was = WebAccessSequences.getSize();
				// print
				System.out.println(
						"    UsedMemory(MB): " + um +
						  "; createSequences(MB): " + was);
			}
			
			// get the request's information
			Request req = WebAccess.getRequest(i);
			String sessionID = req.getSessionID();
			boolean isSuitableForLinkPrediction = req.getIsSuitableToLinkPrediction();
			boolean isTheEndOfTheSession = req.getIsTheEndOfTheSession();
			int nvalid = 0;
			int len = 0;
			ArrayList<Integer> sequence = null;
			
			// if the request is valid then take for the sequence
			if(	isSuitableForLinkPrediction ){
				// whether the session is already recorded
				if( sequencesDATA.containsKey(sessionID) ){
					Object[] sequenceObjA = sequencesDATA.get(sessionID);
					sequence = (ArrayList<Integer>)sequenceObjA[0];
					nvalid = (int)sequenceObjA[1];
					len = (int)sequenceObjA[2];
					sequence.add(i);
					nvalid++;
				} else {
					sequence = new ArrayList<Integer>();
					sequence.add(i);
					sequencecounter++;
					nvalid++;
				} 
				len++;
				Object[] newObjA = new Object[4];
				newObjA[0] = sequence;
				newObjA[1] = nvalid;
				newObjA[2] = len;
				newObjA[3] = isTheEndOfTheSession;
				sequencesDATA.put(sessionID, newObjA);
			
				if(isTheEndOfTheSession){
					WebAccessSequences.addSession(sessionID, sequence);
					float prob = (float)nvalid/(float)len;
					WebAccessSequences.putValidness(sessionID, prob);
					sequencesDATA.remove(sessionID);
				}
			}
		}
		System.out.println("  [" + System.currentTimeMillis() + "] " + sequencecounter + " sequences created.");
		
		// unload the is suitable for link prediction
		Website.unloadIsSuitableToLinkPrediction();
	}
	

	public void createSessions2(int expireSessionsInMin){
		float expiredTimeInSeconds = expireSessionsInMin*60;
		
		// store all request_indexes
		ArrayList<String> keys = WebAccessSequences.getSessionsIDs();
		int[] reqindexesA = WebAccessSequences.sesIDs2reqIndexList(keys);
		
		// access to request data and take the URLname
		Hashtable<Integer,Float> req2et = new Hashtable<Integer,Float>();
		for(int i=0; i<reqindexesA.length; i++){
			int reqi = reqindexesA[i];
			Request req = WebAccess.getRequest(reqi);
			float et = req.getElapsedTime();
			req2et.put(reqi, et);
		}
				
		// when consecutive same URL find remove the following ones
		Hashtable<Integer,String> req2sesID = new Hashtable<Integer,String>();
		ArrayList<String> keys2 = WebAccessSequences.getSessionsIDs();
		int nsplitAll = 0;
		for(int l=0; l<keys2.size(); l++){
			String sessionID = keys2.get(l);
			ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
			ArrayList<Integer> sequence2 = new ArrayList<Integer>();
			int nsplit = 0;
			for(int i=0; i<sequence.size(); i++){
				int reqi = sequence.get(i);
				float et = req2et.get(reqi);
				float elapsedTimeInSeconds = et/1000f;				
				if(elapsedTimeInSeconds<=expiredTimeInSeconds){
					sequence2.add(reqi);
					req2sesID.put(reqi, sessionID);
				} else {
					WebAccessSequences.putSession(sessionID, sequence2);
					String nsplitStr = String.format("%02d", nsplit);
					sessionID = sessionID + nsplitStr;
					//sessionID = sessionID * 100 + nsplit;
					nsplit++;
					nsplitAll++;
					sequence2 = new ArrayList<Integer>();
					sequence2.add(reqi);
				}
				req2sesID.put(reqi, sessionID);
			}
			WebAccessSequences.putSession(sessionID, sequence2);
		}
		System.out.println("  [createSessions2] Number of split sequences: " + nsplitAll);
				
		// update all request indexes
		for(int i=0; i<reqindexesA.length; i++){
			int reqi = reqindexesA[i];
			String sesID = req2sesID.get(reqi);
			Request req = WebAccess.getRequest(reqi);
			req.setSessionID(sesID);
			WebAccess.replaceRequest(reqi, req);
		}
	}
	
	/**
	 * Ensure minimum valid ur ls.
	 *
	 * @param pValid the valid
	 */
	public void ensureMinimumValidURLs(float pValid){
		ArrayList<String> keys = WebAccessSequences.getSessionsIDs();
		int removecounter = 0;
		for(int i=0; i<keys.size();){
			String sessionID = keys.get(i);
			float pValidSeq = WebAccessSequences.getValidness(sessionID);
			if(pValidSeq<pValid){
				WebAccessSequences.removeSession(sessionID);
				removecounter++;
			} else {
				i++;
			}
		}
		System.out.println("  " + removecounter + " sequences removed.");
	}
	
	/**
	 * Removes the consecutive same urls.
	 */
	public void removeConsecutiveSameURLs(){
		System.out.println("  [" + System.currentTimeMillis() + "] Remove consecutive same URLs.");
		
		// store all request_indexes
		ArrayList<String> keys = WebAccessSequences.getSessionsIDs();
		int[] reqindexesA = WebAccessSequences.sesIDs2reqIndexList(keys);
		
		// access to request data and take the URLname
		Hashtable<Integer,String> req2url = new Hashtable<Integer,String>();
		for(int i=0; i<reqindexesA.length; i++){
			int reqi = reqindexesA[i];
			Request req = WebAccess.getRequest(reqi);
			String urlname = req.getFormatedUrlName();
			req2url.put(reqi, urlname);
		}
		
		// when consecutive same URL find remove the following ones
		ArrayList<String> keys2 = WebAccessSequences.getSessionsIDs();
		int removecounter = 0;
		for(int l=0; l<keys2.size(); l++){
			String sessionID = keys2.get(l);
			ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
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
			WebAccessSequences.setSession(sessionID, sequence2);	
		}
		System.out.println("  " + removecounter + " requests were removed.");
	}
	
	public void takeFirstUrlOfTimeZero(){
		System.out.println("  [" + System.currentTimeMillis() + "] Take first URL of time 0.");		
		
		// store all request_indexes
		ArrayList<String> keys = WebAccessSequences.getSessionsIDs();
		int[] reqindexesA = WebAccessSequences.sesIDs2reqIndexList(keys);
		
		// access to request data and take the elapsedtime
		Hashtable<Integer,Float> req2et = new Hashtable<Integer,Float>();
		for(int i=0; i<reqindexesA.length; i++){
			int reqi = reqindexesA[i];
			Request req = WebAccess.getRequest(reqi);
			float elaptime = req.getElapsedTime();
			req2et.put(reqi, elaptime);
		}
		
		// when time zero is found take the first one
		//ArrayList<Integer> reqIndDB = new ArrayList<Integer>();
		ArrayList<Float> elapTimeDB = new ArrayList<Float>();
		Hashtable<Integer,Integer> reqIndDBht = new Hashtable<Integer,Integer>(); // <reqIndex, vectorIndex> 
		int removecounter = 0;
		int addcounter = 0;
		for(int l=0; l<keys.size(); l++){
			String sessionID = keys.get(l);
			ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
			if(sequence==null){ 
				System.out.println("kk"); 
			}
			ArrayList<Integer> sequence2 = new ArrayList<Integer>();
			int firstreq = -3;
			float lastET = -3;
			
			//System.out.print("seq1");
			for(int i=0; i<sequence.size(); i++){
				int reqi = sequence.get(i);
				float elaptime = req2et.get(reqi);
				//System.out.print("," + reqi + "(" + elaptime + ")");
				
				if(elaptime>=-1){
					if(elaptime==0f){
						lastET = -3;
						if(firstreq==-3){
							firstreq = reqi;
						}
					} else {
						lastET = elaptime;
						if(firstreq==-3){
							firstreq = reqi;
						}
					}
				}
				
				// add to the sequence
				if(		lastET>=-1 || 
						i==sequence.size()-1 || 
						elaptime<=-2){
					if(firstreq==-3){ // nothing to store
						firstreq = -3;
						lastET = -3;
						continue;
					}
					sequence2.add(firstreq);
					//reqIndDB.add(firstreq);
					if(lastET==-3){
						lastET=0;
					}
					elapTimeDB.add(lastET);
					reqIndDBht.put(firstreq, addcounter);
					addcounter++;
					firstreq = -3;
					lastET = -3;
				} else {
					removecounter++;
				}
			}
			/*
			System.out.println();
			System.out.print("seq2");
			for(int kk=0; kk<sequence2.size(); kk++){
				int reqi = sequence2.get(kk);
				int ind = reqIndDBht.get(reqi);
				float elaptime = elapTimeDB.get(ind);
				System.out.print("," + reqi + "(" + elaptime + ")");
			}
			System.out.println();
			*/
			WebAccessSequences.setSession(sessionID, sequence2);
		}
		System.out.println("  " + removecounter + " requests were removed.");
		
		// update the database
		long time1 = System.currentTimeMillis();
		System.out.println("  [" + time1 + "] start updating elapsed times.");
		for(int i=0; i<reqindexesA.length; i++){
			int reqInd = reqindexesA[i];
			//int ind = reqIndDB.indexOf(reqInd);
			Integer indObj = reqIndDBht.get(reqInd);
			int ind = indObj==null ? -1 : indObj;
			float elapt = -3;
			if(ind!=-1){
				elapt = elapTimeDB.get(ind);
			}
			
			// update elapsed time
			Request req = WebAccess.getRequest(reqInd);
			float elapt0 = req.getElapsedTime();
			if(elapt0!=-2){
				req.setElapsedTime(elapt);
				WebAccess.replaceRequest(reqInd, req);
			}
		}
		long time2 = System.currentTimeMillis();
		System.out.println("  [" + time2 + "] end updating elapsed times.");
	}
	
	/**
	 * Ensure minimum activity in each sequence.
	 *
	 * @param nclicks the nclicks
	 */
	public void ensureMinimumActivityInEachSequence(int nclicks){
		System.out.println("  [" + System.currentTimeMillis() + "] Start ensuring a minimum activity in each sequence.");
		
		ArrayList<String> keys = WebAccessSequences.getSessionsIDs();
		int removecounter = 0;
		for(int l=0; l<keys.size();){
			String sessionID = keys.get(l);
			ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
			if(sequence.size()<nclicks){
				WebAccessSequences.removeSession(sessionID);
				removecounter++;
			} else {
				l++;
			}
		}
		System.out.println("  [" + System.currentTimeMillis() + "]   " + removecounter + " sequences removed.");
	}
	
	// we assume that this long activity were generated by web robots.
	/**
	 * Removes the long sequences.
	 *
	 * @param lengthpercentile the lengthpercentile
	 */
	public void removeLongSequences(float lengthpercentile){
		System.out.println("  [" + System.currentTimeMillis() + "] Start computing the length percentile: " + lengthpercentile);
		
		// get all sessionIDs
		ArrayList<String> keys = WebAccessSequences.getSessionsIDs();
		
		// order the sequences' lengths
		ArrayList<Integer> lengthsInOrder = new ArrayList<Integer>();
		for(int l=0; l<keys.size(); l++){
			String sessionID = keys.get(l);
			ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
			int seqlength = sequence.size();
			int pos = Collections.binarySearch(lengthsInOrder, seqlength);
			if(pos<0){
				pos = Math.abs(pos+1);
			} else {
				pos++;
			}
			/*
			int i;
			for(i=0; i<lengthsInOrder.size(); i++){
				int iseqlength = lengthsInOrder.get(i);
				if(seqlength<iseqlength){
					break;
				}
			}
			*/
			lengthsInOrder.add(pos, seqlength);
		}
		
		// compute the given percentile's position
		int nseqs = lengthsInOrder.size();
		int position = Math.round((float)nseqs*(lengthpercentile/(float)100));
		int position2 = position>=lengthsInOrder.size() ?  lengthsInOrder.size()-1 : position;
		int value = lengthsInOrder.size()>0 ? lengthsInOrder.get(position2) : -1;
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
		System.out.println("  [" + System.currentTimeMillis() + "] Start removing long sequences up to: " + nclicks);
		
		ArrayList<String> keys = WebAccessSequences.getSessionsIDs();
		int removecounter = 0;
		for(int l=0; l<keys.size();){
			String sessionID = keys.get(l);
			ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
			if(sequence.size()>=nclicks){
				WebAccessSequences.removeSession(sessionID);
				removecounter++;
			} else {
				l++;
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
		ArrayList<String> keysOrd = WebAccessSequences.getSequencesIDs();
		
		// take all request indexes and order them.
		ArrayList<Integer> reqIndexes = new ArrayList<Integer>(); 
		for(int i=0; i<keysOrd.size(); i++){
			String sessionID = keysOrd.get(i);
			ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
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
			Request req = WebAccess.getRequest(reqind);
			
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
			WebAccess.replaceRequest(reqind, req);
		}
	}
	
	
	/**
	 * Compute page role uh c_remove only unimportant.
	 *
	 * @param pMaxUnimportant the max unimportant
	 */
	public void computePageRoleUHC_removeOnlyUnimportant(float pMaxUnimportant){
		// store all request_indexes
		ArrayList<String> keys = WebAccessSequences.getSessionsIDs();
		int[] reqindexesA = WebAccessSequences.sesIDs2reqIndexList(keys);
		
		// access to request data and take the URLname
		Hashtable<Integer,String> req2role = new Hashtable<Integer,String>();
		for(int i=0; i<reqindexesA.length; i++){
			int reqi = reqindexesA[i];
			Request req = WebAccess.getRequest(reqi);
			String role = req.getPageRoleUHC();
			req2role.put(reqi, role);
		}
		
		// remove the sequences that have a lot of Unimportant
		ArrayList<String> keys2 = WebAccessSequences.getSessionsIDs();
		int removecounter = 0;
		for(int l=0; l<keys2.size(); l++){
			String sessionID = keys2.get(l);
			ArrayList<Integer> sequence = WebAccessSequences.getSession(sessionID);
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
				WebAccessSequences.removeSession(sessionID);
				removecounter++;
			}	
		}
		System.out.println("  " + removecounter + " sequences were removed.");
	}
	
	
	
	public void selectFixedNumberOfSequences(int nses){
		ArrayList<String> keys = WebAccessSequences.getSessionsIDs();
	
		// remove the last sessions
		int len = keys.size();
		for(int l=nses; l<len; l++){
			String sessionID = keys.get(nses);
			WebAccessSequences.removeSession(sessionID);
		}
	}
	
}
