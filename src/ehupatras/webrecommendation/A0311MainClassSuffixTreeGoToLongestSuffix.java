package ehupatras.webrecommendation;

// TODO: Auto-generated Javadoc
/**
 * The Class A0311MainClassSuffixTreeGoToLongestSuffix.
 */
public class A0311MainClassSuffixTreeGoToLongestSuffix {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
	
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.loadDM();
		param.loadHoldOut();
		param.loadTopicInf();		
		param.setTopicMatchWeight(0.5f);
		param.createModelEvaluatorSuffixTreeGlobal();
		param.runModelEvaluatorSTG(1,1000,"ST_wTrain");
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
