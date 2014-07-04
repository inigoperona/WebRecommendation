package ehupatras.webrecommendation.evaluator;

import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.distmatrix.Matrix;
import java.util.ArrayList;

public class ModelEvaluatorSuffixTree 
				extends ModelEvaluator {

	// ATTRIBUTES
	
	protected ArrayList<MySuffixTree> m_suffixtreeAL = null;
	
	// CREATOR
	
	public ModelEvaluatorSuffixTree(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL);
	}
	
	// BUILD MODEL
	
	protected void buidSuffixTrees(ArrayList<ArrayList<String[]>> sequencesAL){
		m_suffixtreeAL = new ArrayList<MySuffixTree>();
		for(int i=0; i<m_nFolds; i++){
			ArrayList<String[]> sequences = sequencesAL.get(i);
			MySuffixTree st = new MySuffixTree(sequences);
			m_suffixtreeAL.add(st);
		}
	}
	
	// EVALUATION
	
	public TestSetEvaluator createTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluator(
						testseqs, 
						m_suffixtreeAL.get(iFold));
		return eval;
	}
	
}
