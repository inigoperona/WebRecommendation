package ehupatras.webrecommendation;

public class A054MainClassHclustSpadeStcv {

	public static void main(String[] args) {
		
		String wd = "20160525_experiments_BT";
		String[] args2 = {wd+"/00_preprocess", "xxx", "/empty.txt", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "/DM_LG",
				wd+"/02_validation", "/hclust_DM_LG", "/hclust_DM_LG/spade", "/hclust_spade_st_eval.txt", "-", "1"};
		
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.loadDM();
		param.loadCrossValidation();
		param.createModelEvaluatorSeqMinSPADE();
		param.runModelEvaluatorHclustSpadeST(5, "ST_w_eS1");
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
