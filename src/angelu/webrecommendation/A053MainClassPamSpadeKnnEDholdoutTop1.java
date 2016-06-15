package angelu.webrecommendation;

// TODO: Auto-generated Javadoc
/**
 * The Class A053MainClassPamSpadeKnnEDholdoutTop1.
 */
public class A053MainClassPamSpadeKnnEDholdoutTop1 {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		String[] args2 = {"experiments_gieus/00_preprocess", "xxx", "5", "_s0.javaData",
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				"experiments_gieus/01_database", "/DM_ed",
				"experiments_gieus/03_validation", "/pam_DM_ED", "/pam_DM_ED/spade1", "evaluation_pam.txt",
				"-", "1"};
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.loadDM(false);
		param.loadHoldOut_cv();
		param.loadTopicInf();
		param.createModelEvaluatorMedoids();
		param.runModelEvaluatorM_pam();
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		
	}
	
}
