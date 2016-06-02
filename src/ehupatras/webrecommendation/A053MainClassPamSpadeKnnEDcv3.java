package ehupatras.webrecommendation;

public class A053MainClassPamSpadeKnnEDcv3 {

	public static void main(String[] args) {
		
		String wd = "20160530_experiments_BT_sekuentzietatik";
		String[] args2 = {wd+"/00_preprocess", "/sequences_urlIDurlRole2.txt", "/empty.txt", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "/DM_ED",
				wd+"/02_validation", "/pam_DM_ED", "/pam_DM_ED/spade1", "/pam_spade1_knnED_eval.txt", "-", "1"};
		
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase2(false);
		param.loadDM(true);
		param.loadCrossValidation();
		param.createModelEvaluatorMedoids();
		param.runModelEvaluatorM_pam();
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
