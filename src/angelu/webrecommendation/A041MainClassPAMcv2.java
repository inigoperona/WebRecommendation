package angelu.webrecommendation;

public class A041MainClassPAMcv2 {

	public static void main(String[] args) {
		
		String wd = "20161121_EHU_sessions";
		String[] args2 = {wd+"/00_preprocess", "/small.txt", "5", ".javaData",
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "/DM_ED",
				wd+"/02_validation", "/pam_DM_ED", "xxx", "/eval.txt", "-", "1"};
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase2(false);
		param.loadDM(false);
		param.loadCrossValidation();
		param.createModelEvaluatorClustPAM();
		param.runModelEvaluatorP();
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
