package ehupatras.webrecommendation.structures.request;

import java.util.Calendar;
import java.util.GregorianCalendar;

import ehupatras.webrecommendation.structures.page.Page;

public class RequestDiscapnet 
				extends RequestAbstract {
	
	// The Serializable classes needs it 
	private static final long serialVersionUID = 1L;
	
	
	// CREATOR
	public RequestDiscapnet(String ip, int ipID, String time, 
			String method, Page page, String protocol,
			int status, String reqsize, String reference, String useragent){
		super(ip, ipID, time, method, page, protocol, status, reqsize, reference, useragent);
	}

	// time
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
