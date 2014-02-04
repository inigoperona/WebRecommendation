package ehupatras.webrecommendation.preprocess;

import ehupatras.webrecommendation.structures.Request;
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
		Enumeration<Integer> keys = oldrequests.keys();
		while(keys.hasMoreElements()){
			int userid = keys.nextElement().intValue();
			Object[] objA = oldrequests.get(userid);
			int oldsessioni = ((Integer)objA[0]).intValue();
			long oldtime = ((Long)objA[1]).longValue();
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
		Enumeration<Integer> keys = oldrequests.keys();
		while(keys.hasMoreElements()){
			int sessionID = keys.nextElement().intValue();
			Object[] objA = oldrequests.get(sessionID);
			int oldindex = ((Integer)objA[0]).intValue();
			int oldUrl = ((Integer)objA[1]).intValue();
			float sum = ((Float)objA[2]).floatValue();
			Request oldreq = WebAccessSequences.getRequest(oldindex);
			oldreq.setElapsedTime(sum);
			WebAccessSequences.replaceRequest(oldindex, oldreq);
		}
	}
	
}
