package ehupatras.webrecommendation.evaluator;

import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.distmatrix.Matrix;
import java.util.ArrayList;

public class ModelEvaluatorSuffixTreeGlobal 
				extends ModelEvaluatorSuffixTree {
	
	// CREATOR
	
	public ModelEvaluatorSuffixTreeGlobal(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL);
	}
	
	// BUILD MODEL
	
	public void buildModel(){
		// Build Suffix Trees for each fold
		m_suffixtreeAL = new ArrayList<MySuffixTree>();
		for(int i=0; i<m_nFolds; i++){
			m_suffixtreeAL.add(this.createSuffixTreeFromOriginalSequences(i));
		}
	}
	
	private MySuffixTree createSuffixTreeFromOriginalSequences(int indexFold){		
		ArrayList<Long> trainnames = m_trainAL.get(indexFold);
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames, m_datasetSplit!=null);
		ArrayList<String[]> sequences = new ArrayList<String[]>(); 
		for(int i=0; i<trainDMindexes.length; i++){
			int index = trainDMindexes[i];
			String[] seq = (getDataSet(m_datasetSplit!=null)).get(index);
			sequences.add(seq);
		}
		return createST(sequences);
	}
	
}
