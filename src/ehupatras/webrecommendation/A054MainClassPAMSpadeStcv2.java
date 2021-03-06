package ehupatras.webrecommendation;

public class A054MainClassPAMSpadeStcv2 {

	public static void main(String[] args) {
		
		String wd = "20161121_EHU_sessions";
		String[] args2 = {wd+"/00_preprocess", "/small.txt", "5", ".javaData",
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "/DM_ED",
				wd+"/02_validation", "/pam_DM_ED", "/pam_DM_ED/spade", "/pam_spade_st_eval.txt", "-", "1"};
		
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args2);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase2(false);
		param.loadDM(false);
		param.loadCrossValidation();
		param.createModelEvaluatorSeqMinSPADE();
		param.runModelEvaluatorPamSpadeST("ST_w_eS1");
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
