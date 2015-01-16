package ehupatras.webrecommendation.structures.request;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ehupatras.webrecommendation.structures.page.Page;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestDiscapnet.
 */
public class RequestDiscapnet 
				extends RequestAbstract {
	
	// The Serializable classes needs it 
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	
	// CREATOR
	/**
	 * Instantiates a new request discapnet.
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
	public RequestDiscapnet(String ip, int ipID, String time, 
			String method, Page page, String protocol,
			int status, String reqsize, String reference, String useragent){
		super(ip, ipID, time, method, page, protocol, status, reqsize, reference, useragent);
	}

	// time
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.structures.request.RequestAbstract#getTime(java.lang.String)
	 */
	protected Calendar getTime(String time){
		String[] time2A = time.split(" ");
		
		String timeA_0 = time2A[0];
		String[] timeAA = timeA_0.split("-");
		int year = Integer.valueOf(timeAA[0]).intValue();
		int month = Integer.valueOf(timeAA[1]).intValue() - 1;
		int day = Integer.valueOf(timeAA[2]).intValue();
		
			// extract the other time measures: year, hour, min, sec
		String timeA_1 = time2A[1];
		String[] timeBA = timeA_1.split(":");
		int hour = Integer.valueOf(timeBA[0]).intValue();
		int min = Integer.valueOf(timeBA[1]).intValue();
		int sec = Integer.valueOf(timeBA[2]).intValue();
		GregorianCalendar gcal = new GregorianCalendar(year, month, day, hour, min, sec);
		return gcal;
	}
	
}
