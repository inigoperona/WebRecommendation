package ehupatras.webrecommendation;

public class A0314MainClassSuffixTreeGoToLongestSuffixEnrichLength1Suffixcv {

		public static void main(String[] args) {

			String wd = "20160525_experiments_BT";
			String[] args2 = {wd+"/00_preprocess", "xxx", "/empty.txt", 
					"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
					wd+"/01_databases", "/DM_ED",
					wd+"/02_validation", "/01_gst", "xxx", "/eval.txt", "-", "1"};
			
			A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args2);
			
			// take the start time of the program
			long starttimeprogram = System.currentTimeMillis();

			// RUN
			param.loadDatabase();
			param.loadDM();
			param.loadCrossValidation();
			param.createModelEvaluatorSuffixTreeGlobal();
			param.runModelEvaluatorSTG(1,1,"ST_w_eS1");
					
			// ending the program
			long endtimeprogram = System.currentTimeMillis();
			System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		}
	
}
