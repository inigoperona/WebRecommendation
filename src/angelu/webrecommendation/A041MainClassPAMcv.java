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
