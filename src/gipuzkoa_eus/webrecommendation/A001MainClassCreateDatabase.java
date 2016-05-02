package gipuzkoa_eus.webrecommendation;

import angelu.webrecommendation.A0000ParameterControl_angelu;
import ehupatras.webrecommendation.structures.WebAccess;

public class A001MainClassCreateDatabase {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		String[] args2 = {"experiments_gieus/00_preprocess", "xxx", "/empty.txt", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				"experiments_gieus/01_database", "/DM_ed",
				"xxx", "xxx", "xxx", "xxx", "-", "1"};		
		
		A0000ParameterControl_angelu param = new A0000ParameterControl_angelu(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		WebAccess.changeToOrderedRequests();
		param.loadLogs();
		param.createDatabase();
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
