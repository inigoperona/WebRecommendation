package ehupatras.webrecommendation;

public class A040MainClassHclustcv {

	public static void main(String[] args) {

		String[] args2 = {"20160502_experiments_BT/00_preprocess", "xxx", "/empty.txt", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				"20160502_experiments_BT/01_databases", "/DM_LG",
				"20160502_experiments_BT/03_validation", "/03_hclust_DM_LG", "xxx", "xxx", "-", "1"};
		
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.loadDM();
		param.loadCrossValidation();
		param.createModelEvaluatorClustHclust();
		param.runModelEvaluatorH(5);
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");		
	}
	
}
