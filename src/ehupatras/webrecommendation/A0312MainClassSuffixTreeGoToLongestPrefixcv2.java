package ehupatras.webrecommendation;

public class A0312MainClassSuffixTreeGoToLongestPrefixcv2 {

	public static void main(String[] args) {

		String wd = "20160530_experiments_BT_sekuentzietatik";
		String[] args2 = {wd+"/00_preprocess", "/sequences_urlIDurlRole2.txt", "/empty.txt", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "/DM_ED",
				wd+"/02_validation", "/01_gst", "xxx", "/eval.txt", "-", "1"};
		
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase2(false);
		param.loadDM(false);
		param.loadCrossValidation();
		param.createModelEvaluatorSuffixTreeGlobal();
		param.runModelEvaluatorSTG(2,1000,"ST_wTrain");
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
