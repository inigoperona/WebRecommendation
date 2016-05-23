package ehupatras.webrecommendation;

public class A054MainClassPAMSpadeStcv {

	public static void main(String[] args) {
		
		String[] args2 = {"20160502_experiments_BT/00_preprocess", "xxx", "/empty.txt", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				"20160502_experiments_BT/01_databases", "/DM_ED",
				"20160502_experiments_BT/03_validation", "/03_pam_DM_ED", "/03_pam_DM_ED/spade", "pam_spade_st_eval.txt", "-", "1"};
		
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.loadDM();
		param.loadCrossValidation();
		param.createModelEvaluatorSeqMinSPADE();
		param.runModelEvaluatorPamSpadeST("ST_w_eS1");
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}