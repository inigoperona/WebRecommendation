package ehupatras.webrecommendation;

public class A040MainClassHclustcv2 {

	public static void main(String[] args) {

		String wd = "20160613_experiments_BT";
		String[] args2 = {wd+"/00_preprocess", "/sequences_urlIDurlRole3_9325_txikia.txt", "/empty.txt", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "/DM_LG",
				wd+"/02_validation", "/hclust_DM_LG", "xxx", "xxx", "-", "1"};
		
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase2(false);
		param.loadDM(false);
		param.loadCrossValidation();
		param.createModelEvaluatorClustHclust();
		param.runModelEvaluatorH(5);
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");		
	}
	
}