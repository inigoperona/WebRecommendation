package ehupatras.webrecommendation.structures.request;

import java.util.Calendar;
import java.util.GregorianCalendar;
import ehupatras.webrecommendation.structures.page.Page;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestBidasoaTurismo.
 */
public class RequestBidasoaTurismo 
				extends RequestAbstract {

	// The Serializable classes needs it 
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	
	// CREATOR
	/**
	 * Instantiates a new request bidasoa turismo.
	 *
	 * @param ip the ip
	 * @param ipID the ip id
	 * @param time the time
	 * @param method the method
	 * @param page the page
	 * @param protocol the protocol
	 * @param status the status
	 * @param reqsize the reqsize
	 * @param reference the reference
	 * @param useragent the useragent
	 */
	public RequestBidasoaTurismo(String ip, int ipID, String time, 
			String method, Page page, String protocol,
			int status, String reqsize, String reference, String useragent,
			int logfilenumber){
		super(ip, ipID, time, method, page, protocol, status, reqsize, reference, useragent, logfilenumber);
	}
	
	

	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.RequestAbstract#getTime(java.lang.String)
	 */
	protected Calendar getTime(String time){
		String time2 = time.substring(2, time.length()-1);
		String[] time2A = time2.split(" ");
		String timeA_0 = time2A[0];
		String[] timeAA = timeA_0.split("/");
			// time zone (GTM+01)
		//String timezone = time2A[1];
			// day
		int day = Integer.valueOf(timeAA[0]).intValue();
			// month
		String monthstr = timeAA[1];
		int month = 0;
		switch (monthstr){
			case "Jan" : month = 0; break;
			case "Feb" : month = 1; break;
			case "Mar" : month = 2; break;
			case "Apr" : month = 3; break;
			case "May" : month = 4; break;
			case "Jun" : month = 5; break;
			case "Jul" : month = 6; break;
			case "Aug" : month = 7; break;
			case "Sep" : month = 8; break;
			case "Oct" : month = 9; break;
			case "Nov" : month = 10; break;
			case "Dec" : month = 11; break;
		}
			// extract the other time measures: year, hour, min, sec
		String othertime = timeAA[2];
		String[] othertimeA = othertime.split(":");
		int year = Integer.valueOf(othertimeA[0]).intValue();
		int hour = Integer.valueOf(othertimeA[1]).intValue();
		int min = Integer.valueOf(othertimeA[2]).intValue();
		int sec = Integer.valueOf(othertimeA[3]).intValue();
		GregorianCalendar gcal = new GregorianCalendar(year, month, day, hour, min, sec);
		return gcal;
	}

	
}
