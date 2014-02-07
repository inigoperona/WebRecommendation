package ehupatras.webrecommendation.preprocess.log;

import ehupatras.webrecommendation.structures.Request;
import ehupatras.webrecommendation.structures.WebAccessSequences;

public abstract class LogReader {
	
	public abstract void readLogFile(String[] logfilenamesA);
	
	// Identify the URLs that appears more than some threshold
	// for example, URLs which appear at least 10 times
	public void identifyFrequentURLs(int minimunFrequency){
		// compute the maximum URL-index value
		int maxurlid = Integer.MIN_VALUE;
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			Request req = WebAccessSequences.getRequest(i);
			int urlid = req.getUrlIDusage();
			if(maxurlid<urlid){
				maxurlid = urlid;
			}
		}
		
		// compute the frequencies of URLs
		int[] urlfrequenciesA = new int[maxurlid+1];
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			Request req = WebAccessSequences.getRequest(i);
			int urlid = req.getUrlIDusage();
			urlfrequenciesA[urlid]++;
		}
		
		// Identify frequent URLs
		int nFrequent = 0;
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			Request req = WebAccessSequences.getRequest(i);
			int urlid = req.getUrlIDusage();
			int freq = urlfrequenciesA[urlid];
			if(freq>=minimunFrequency){
				req.setIsFrequent(true);
				nFrequent++;
			} else {
				req.setIsFrequent(false);
			}
			WebAccessSequences.replaceRequest(i, req);
		}
		System.out.println("  " + nFrequent + "/" + WebAccessSequences.filteredlogsize() + 
				" requests have an URL that appears at least " + minimunFrequency + " times.");
	}
	
	
	// Identify the URLs that appears during all the time
	// for example, those URLs have to appear 10 times in every 10 days period.
	// the information provide this URLs are not volatiles
	public void identifyStaticURLs(int days, int times, float minimunPeriodFrequencyProportion){
		// compute the maximum URL-index value
		int maxurlid = Integer.MIN_VALUE;
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			Request req = WebAccessSequences.getRequest(i);
			int urlid = req.getUrlIDusage();
			if(maxurlid<urlid){
				maxurlid = urlid;
			}
		}
		
		// for each period of time see if it is accessed
		int nperiods = 0;
		long starttime = 0;
		long endtime = Long.MAX_VALUE;
		int[] urlfrequenciesi = new int[maxurlid+1];
		int[] urlInPeriods = new int[maxurlid+1];
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			Request req = WebAccessSequences.getRequest(i);
			// define the new period of time
			long actualtime = req.getTimeInMillis();
			if(starttime==0 || actualtime>=endtime){
				//end the periods analysis
				for(int j=0; j<urlInPeriods.length; j++){
					int freq = urlfrequenciesi[j];
					if(freq>=times){
						urlInPeriods[j]++;
					}
				}
				// prepare the variables for a new periods
				starttime = actualtime;
				endtime = starttime + (long)((long)days*24*3600*1000);
				nperiods++;
				urlfrequenciesi = new int[maxurlid+1];
			}
			// compute the frequencies for this period
			int urlid = req.getUrlIDusage();
			urlfrequenciesi[urlid]++;
		}
		//end the periods analysis
		for(int j=0; j<urlInPeriods.length; j++){
			int freq = urlfrequenciesi[j];
			if(freq>=times){
				urlInPeriods[j]++;
			}
		}
		
		// compute if the URLs appears enough during the time to take them into account
		// they do not have volatile information
		// so, the nature of the URL would be static
		int nstatics = 0;
		int minimunperiods = Math.round((float)nperiods*minimunPeriodFrequencyProportion);
		for(int i=0; i<WebAccessSequences.filteredlogsize(); i++){
			Request req = WebAccessSequences.getRequest(i);
			int urlid = req.getUrlIDusage();
			int freqInPeriods = urlInPeriods[urlid];
			if(freqInPeriods>=minimunperiods){
				req.setIsStatic(true);
				nstatics++;
			} else {
				req.setIsStatic(false);
			}
			WebAccessSequences.replaceRequest(i, req);
		}
		System.out.println("  " + nstatics + "/" + WebAccessSequences.filteredlogsize() + 
				" requests have an URL that appears (at least " + times + " times) in " + 
				minimunperiods + "-" + nperiods + 
				" periods of " + days + " days.");
	}
	
}
