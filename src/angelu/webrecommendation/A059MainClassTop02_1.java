package angelu.webrecommendation;

public class A059MainClassTop02_1 {

	public static void main(String[] args) {
		
		A0000ParameterControl param = new A0000ParameterControl(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.loadDM();
		param.loadHoldOut();
		param.loadTopicInf();
		param.createModelEvaluatorMedoidsContent();
		param.runModelEvaluatorMC("Top02_1");
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		
	}
	
}
