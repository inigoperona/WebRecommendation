package angelu.webrecommendation;

public class A021MainClassCrossValidation2 {
	
	public static void main(String[] args) {
		
		String wd = "20161121_EHU_sessions";
		String[] args2 = {wd+"/00_preprocess", "/small.txt", "5", "_s0.javaData", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "/DM_ED",
				wd+"/02_validation", "xxx", "xxx", "xxx", "-", "1"};
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase2(false);
		param.loadDM(false);
		//param.createCrossValidation();
		param.createCrossValidation(1,0,0,1);

		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
