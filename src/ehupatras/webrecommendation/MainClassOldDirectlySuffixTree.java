package ehupatras.webrecommendation;

import java.util.ArrayList;

import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import ehupatras.webrecommendation.evaluator.TestSetEvaluator;

public class MainClassOldDirectlySuffixTree {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		// Parameter control
		//String basedirectory = "/home/burdinadar/eclipse_workdirectory/DATA";
		String basedirectory = args[0];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(basedirectory);
		Website.setWorkDirectory(basedirectory);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		// LOAD PREPROCESSED LOGS
		A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		preprocess.loadPreprocess();
		
		// LOAD TRAIN / VALIDATION / TEST data.
		ModelValidationHoldOut modelval = new ModelValidationHoldOut();
		modelval.load(basedirectory);
		// Get the splited data
		ArrayList<Integer> train = modelval.getTrain().get(0);
		ArrayList<Integer> val   = modelval.getValidation().get(0);
		ArrayList<Integer> test  = modelval.getTest().get(0);
		// get training sequences
		ArrayList<String[]> sequencesUHC_train = WebAccessSequencesUHC.getSequencesInstanciated(train);
		ArrayList<String[]> sequencesUHC_val = WebAccessSequencesUHC.getSequencesInstanciated(val);
		ArrayList<String[]> sequencesUHC_test = WebAccessSequencesUHC.getSequencesInstanciated(test);

		// SUFFIX TREE
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start creating the suffix tree.");
		// Put all sequences in the suffix tree
		SuffixTreeStringArray suffixtree = new SuffixTreeStringArray();
		for(int i=0; i<sequencesUHC_train.size(); i++){
			String[] seq = sequencesUHC_train.get(i);
			suffixtree.putSequence(seq, i);
		}
		System.out.println("  Number of nodes: " + suffixtree.getNumberOfNodes());
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: " 
					+ (endtime-starttime)/1000 + " seconds.");
		
		// EVALUATION
			starttime = System.currentTimeMillis();
			System.out.println("[" + starttime + "] Start the evaluation.");
		TestSetEvaluator eval = new TestSetEvaluator(sequencesUHC_test, suffixtree);
		eval.computeEvaluation(-1, -1, (long)0, null, 0, 100);
		eval.writeResults();
			endtime = System.currentTimeMillis();
			System.out.println("[" + endtime + "] End. Elapsed time: " 
				+ (endtime-starttime)/1000 + " seconds.");
	}

}
