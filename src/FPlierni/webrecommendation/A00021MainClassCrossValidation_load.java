package FPlierni.webrecommendation;

import angelu.webrecommendation.A0000ParameterControl_angelu;

public class A00021MainClassCrossValidation_load {

	public static void main(String[] args) {
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase2();
		param.loadDM();
		param.createCrossValidation(7, 0, 3, 10);

		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
