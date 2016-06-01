package ehupatras.webrecommendation.structures.request;

import java.util.Calendar;
import java.util.GregorianCalendar;
import ehupatras.webrecommendation.structures.page.Page;

// TODO: Auto-generated Javadoc
/**
 * The Class RequestGipuzkoa_eus.
 */
public class RequestGipuzkoa_eus
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
	public RequestGipuzkoa_eus(String ip, String ipIDstr, String time, 
			String method, Page page, String protocol,
			int status, String reqsize, String reference, String useragent,
			int logfilenumber){
		super(ip, ipIDstr, time, method, page, protocol, status, reqsize, reference, useragent, logfilenumber);
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

	
	protected void isvalid(){
		// default is true
		m_isvalid = true;
		
		// the request method has to be GET or POST
		String method = m_method.toLowerCase(); 
		if(!method.equals("get") && !method.equals("post")){
			m_isvalid = false;
		}
		
		// the status field has to be of the format 2xx or 3xx
		if(m_status/100 != 2 && m_status/100!=3){
			m_isvalid = false;
		}
	}
	
	
	protected void isbot(){
		// detect if the request is generated by bots, search engines, robots...
		m_isbot = false; // default
		String useragent2 = m_useragent.toLowerCase();
		if(	useragent2.contains("bot") ||
			useragent2.contains("spider") ||
			useragent2.contains("crawler")){
			m_isbot  = true;
			m_useragentLabel = m_useragentLabel + "_bot";
		}
	}
	
	/**
	 * Analyze user agent.
	 */
	protected void analyzeUserAgent(){
		String useragent2 = m_useragent.toLowerCase();		
		// bots
		if(	useragent2.contains("googlebot") ){ m_useragentLabel = m_useragentLabel + "_googlebot"; }
		if(	useragent2.contains("bingbot") ){ m_useragentLabel = m_useragentLabel + "_bingbot"; }
		if(	useragent2.contains("yahoo") ){ m_useragentLabel = m_useragentLabel + "_yahoobot"; }
		// browsers
		if(	useragent2.contains("chrome") ){ m_useragentLabel = m_useragentLabel + "_bro_chrome"; }
		if(	useragent2.contains("iexplorer") ){ m_useragentLabel = m_useragentLabel + "_bro_iexplorer"; }
		if(	useragent2.contains("firefox") ){ m_useragentLabel = m_useragentLabel + "_bro_firefox"; }
		if(	useragent2.contains("opera") ){ m_useragentLabel = m_useragentLabel + "_bro_opera"; }
		if(	useragent2.contains("safari") ){ m_useragentLabel = m_useragentLabel + "_bro_safari"; }
		// operating system
		if(	useragent2.contains("windows") ){ m_useragentLabel = m_useragentLabel + "_os_windows"; }
		if(	useragent2.contains("macintosh") ){ m_useragentLabel = m_useragentLabel + "_os_macintosh"; }
		if(	useragent2.contains("linux") ){ m_useragentLabel = m_useragentLabel + "_os_linux"; }
		if(	useragent2.contains("mobile") ){ m_useragentLabel = m_useragentLabel + "_os_mobile"; }
		if(	useragent2.contains("android") ){ m_useragentLabel = m_useragentLabel + "_os_android"; }
		if(	useragent2.contains("iphone") ){ m_useragentLabel = m_useragentLabel + "_os_iphone"; }
	}
	
	/**
	 * Analyze reference.
	 */
	protected void analyzeReference(){
		String reference2 = m_reference.toLowerCase();
		// SITES RELATED with gipuzkoa.eus/net ///*
		//	if(	reference2.contains("discapnet") ){ m_referenceLabel = m_referenceLabel + "_disc"; }
	///*	if(	reference2.contains("wwww.gipuzkoa.net") ){ m_referenceLabel = m_referenceLabel + "_wwwgipnet"; }
	///*	if(	reference2.contains("wwww.gipuzkoa.eus") ){ m_referenceLabel = m_referenceLabel + "_wwwgipeus"; }
		//if(	reference2.contains("discapnet.es") ){ m_referenceLabel = m_referenceLabel + "_disc_subdomain"; }
		
		// SITES RELATED WITH ehu.eus
		if(	reference2.contains("www.ehu.eus") ){ m_referenceLabel = m_referenceLabel + "_wwwehu_eus"; }
		if(	reference2.contains("www.ehu.es") ){ m_referenceLabel = m_referenceLabel + "_wwwehu_es"; }
		if(	reference2.contains("ehu.eus") ){ m_referenceLabel = m_referenceLabel + "_ehu_eus"; }
		if(	reference2.contains("ehu.es") ){ m_referenceLabel = m_referenceLabel + "_ehu_es"; }
		
		///*if(	reference2.contains("gipuzkoa.net") ){ m_referenceLabel = m_referenceLabel + "_gipnet"; }
		///*if(	reference2.contains("gipuzkoa.eus") ){ m_referenceLabel = m_referenceLabel + "_gipeus"; }
		//if(	reference2.contains("technosite") ){ m_referenceLabel = m_referenceLabel + "_disc_technosite"; }
		//if(	reference2.contains("once") ){ m_referenceLabel = m_referenceLabel + "_disc_once"; }
		//if(	reference2.contains("wwww2.gipuzkoa.net") ){ m_referenceLabel = m_referenceLabel + "_Zergabide_inst"; }
		//if(	reference2.contains("b5m.gipuzkoa.net") ){ m_referenceLabel = m_referenceLabel + "_Gipuzkoamapak"; }
		
		// search engine 
		if(	reference2.contains("google") ||  reference2.contains("goo.gl") ){ m_referenceLabel = m_referenceLabel + "_se_google"; }
		if(	reference2.contains("yahoo") ){ m_referenceLabel = m_referenceLabel + "_se_yahoo"; }
		if(	reference2.contains("ask") ){ m_referenceLabel = m_referenceLabel + "_se_ask"; }
		//if(	reference2.contains("iconoce") ){ m_referenceLabel = m_referenceLabel + "_se_iconoce"; }
		if(	reference2.contains("bing") ){ m_referenceLabel = m_referenceLabel + "_se_bing"; }
		// social networks
		if(	reference2.contains("facebook") ){ m_referenceLabel = m_referenceLabel + "_sn_facebook"; }
		if(	reference2.contains("twitter.com")){ m_referenceLabel = m_referenceLabel + "_sn_twitter"; }
		if(	reference2.contains("blogspot") ){ m_referenceLabel = m_referenceLabel + "_sn_blogspot"; }
		if(	reference2.contains("wordpress") ){ m_referenceLabel = m_referenceLabel + "_sn_wordpress"; }
		if(	reference2.contains("meneame") ){ m_referenceLabel = m_referenceLabel + "_sn_meneame"; }
		// other
	
		if(	reference2.contains("bbs.soul-plus.net")||
			reference2.contains("freesex.mobi")||
			reference2.contains("fuli8.erog.fr")||
			reference2.contains("www.flyflv.com")||
			reference2.contains("www.forum78.com")||
			reference2.contains("www.freemomson.com")||
			reference2.contains("www.fuqer.com")||
			reference2.contains("www.haoxiee.net")||
			reference2.contains("www.ijizz.mobi")||
			reference2.contains("www.javlibrary.com")||
			reference2.contains("www.jinxiujiaqi.com")||
			reference2.contains("www.jz199.com")||
			reference2.contains("www.keezmovies.com")||
			reference2.contains("www.pixiv.net")||
			reference2.contains("www.pornhd.com")||
			reference2.contains("www.pornhub.com")||
			reference2.contains("xxoogif.com")
			){ m_referenceLabel = m_referenceLabel + "_porn"; }
		
		//if(	reference2.contains("imente") ){ m_referenceLabel = m_referenceLabel + "_oth_imente"; }
		//if(	reference2.contains("bit.ly") ){ m_referenceLabel = m_referenceLabel + "_oth_bit.ly"; }
		if(	reference2.contains("babylon") ){ m_referenceLabel = m_referenceLabel + "_oth_babylon"; }
		//if(	reference2.contains("portalento") ){ m_referenceLabel = m_referenceLabel + "_oth_portalento"; }
		if(	reference2.contains("wikipedia") ){ m_referenceLabel = m_referenceLabel + "_oth_wikipedia"; }
		if(	reference2.contains("173.194") ){ m_referenceLabel = m_referenceLabel + "_oth_ehu"; }
	}
	
	
}

