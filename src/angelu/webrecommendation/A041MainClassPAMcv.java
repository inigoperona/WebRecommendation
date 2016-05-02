package angelu.webrecommendation;

// TODO: Auto-generated Javadoc
/**
 * The Class A041MainClassPAMcv.
 */
public class A041MainClassPAMcv {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		String[] args2 = {"20160502_experiments_BT/00_preprocess", "xxx", "/empty.txt", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				"20160502_experiments_BT/01_databases", "/DM_ED",
				"20160502_experiments_BT/03_validation", "/03_pam_DM_ED", "xxx", "/eval.txt", "-", "1"};
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.loadDM();
		param.loadCrossValidation();
		param.createModelEvaluatorClustPAM();
		param.runModelEvaluatorP();
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
