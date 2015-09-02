package angelu.webrecommendation;

// TODO: Auto-generated Javadoc
/**
 * The Class A041MainClassPAMholdout.
 */
public class A041MainClassPAMholdout {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		String[] args2 = {"experiments_gieus/00_preprocess", "xxx",
				  "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				  "experiments_gieus/01_database", "/DM_ed",
				  "experiments_gieus/03_validation", "/pam_DM_ED", "xxx", "xxx",
				  "-", "1"};
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.loadDM();
		param.loadHoldOut_cv();
		param.createModelEvaluatorClustPAM();
		param.runModelEvaluatorP();
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}

