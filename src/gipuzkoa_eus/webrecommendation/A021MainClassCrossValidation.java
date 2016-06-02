package gipuzkoa_eus.webrecommendation;

import angelu.webrecommendation.A0000ParameterControl_angelu;

public class A021MainClassCrossValidation {

	public static void main(String[] args) {
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.loadDM(false);
		param.createCrossValidation(7,2,1,10); // we have 83000 cases

		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
