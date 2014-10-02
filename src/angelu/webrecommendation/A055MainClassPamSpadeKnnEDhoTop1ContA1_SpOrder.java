package angelu.webrecommendation;

public class A055MainClassPamSpadeKnnEDhoTop1ContA1_SpOrder {

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
		param.runModelEvaluatorMC("ContentsA1_SpOrder");
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
