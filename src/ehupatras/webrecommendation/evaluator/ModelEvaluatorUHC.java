package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.Matrix;

public class ModelEvaluatorUHC 
				extends ModelEvaluator {

	public ModelEvaluatorUHC(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL);
		
		// Remove the role tag to the URL creating dataset
		ArrayList<String[]> dataset2 = this.removeUHCTagDB(dataset);
		super.setDataSet(dataset2);
		
		// Remove the role tag to the URL creating datasetSplit
		if(datasetSplit!=null){
			ArrayList<String[]> datasetSplit2 = this.removeUHCTagDB(datasetSplit);
			super.setDataSetSplit(datasetSplit2);
		}
	}
	
	private ArrayList<String[]> removeUHCTagDB(ArrayList<String[]> dataset){
		ArrayList<String[]> dataset2 = new ArrayList<String[]>();
		for(int i=0; i<dataset.size(); i++){
			String[] strA = dataset.get(i);
			String[] strB = this.removeUHCtagSeq(strA);
			dataset2.add(strB);
		}
		return dataset2; 
	}
	
	private String[] removeUHCtagSeq(String[] seq){
		String[] seq2 = new String[seq.length];
		for(int i=0; i<seq.length; i++){
			String urlRole = seq[i];
			String url = urlRole.substring(0, urlRole.length()-1);
			seq2[i] = url;
		}
		return seq2;
	}
	
}
