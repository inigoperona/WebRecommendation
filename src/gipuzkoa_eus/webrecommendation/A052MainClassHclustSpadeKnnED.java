package gipuzkoa_eus.webrecommendation;

import ehupatras.webrecommendation.A0000ParameterControl_ehupatras;

public class A052MainClassHclustSpadeKnnED {

	public static void main(String[] args) {
		
		String[] args2 = {"experiments_gieus/00_preprocess", "xxx",
		"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
		"experiments_gieus/01_database", "/DM_ed",
		"experiments_gieus/03_validation", "/hclust_DM_ED", "/hclust_DM_ED/spade1", "evaluation_hclust.txt",
		"-", "1"};
		
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase();
		param.loadDM();
		param.loadHoldOut_cv();
		param.createModelEvaluatorMedoids();
		param.runModelEvaluatorM_hclust(5);
				
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
