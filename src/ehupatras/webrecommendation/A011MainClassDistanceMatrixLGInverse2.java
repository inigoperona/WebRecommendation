package ehupatras.webrecommendation;

public class A011MainClassDistanceMatrixLGInverse2 {

	public static void main(String[] args){
		
		String wd = "20160613_experiments_BT";
		String[] args2 = {wd+"/00_preprocess", "/sequences_urlIDurlRole3_9325_txikia.txt", "-1", "xxx", 
				"/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt", "/empty.txt",
				wd+"/01_databases", "/DM_LG",
				"xxx", "xxx", "xxx", "xxx", "-", "1"};
		
		A0000ParameterControl_ehupatras param = new A0000ParameterControl_ehupatras(args);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();

		// RUN
		param.loadDatabase2(false);
		
		// No role: equal_UHC
 		float[][] rolesW = 
 				new float[][]{	{ 1f, 1f, 1f},
	    						{ 1f, 1f, 1f},
	    						{ 1f, 1f, 1f}};
 		param.createDM("SimilarityMatrixNormalize", rolesW, param.getDMdirectory(), new int[0]);
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
