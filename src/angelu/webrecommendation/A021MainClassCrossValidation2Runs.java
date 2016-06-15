package angelu.webrecommendation;

public class A021MainClassCrossValidation2Runs {

	public static void main(String[] args) {
		
		String wd = "20160613_experiments_BT";
		String[] args2 = {wd+"/00_preprocess", "/sequences_urlIDurlRole3_9325_txikia.txt", "5", "_s0.javaData", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "/DM_ED",
				wd+"/02_validation", "xxx", "xxx", "xxx", "-", "0"};
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase2(false);
		param.loadDM(false);
		param.createCrossValidationRuns();

		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
