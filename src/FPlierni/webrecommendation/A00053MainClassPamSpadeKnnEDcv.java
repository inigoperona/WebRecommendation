package FPlierni.webrecommendation;

import angelu.webrecommendation.A0000ParameterControl_angelu;

public class A00053MainClassPamSpadeKnnEDcv {

	public static void main(String[] args) {
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase2(false);
		param.loadDM(false);
		param.loadCrossValidation();
		//param.loadTopicInf();
		param.createModelEvaluatorMedoids();
		param.runModelEvaluatorM_pam();
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
