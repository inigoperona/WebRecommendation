package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.Matrix;

public class ModelEvaluatorUHC 
				extends ModelEvaluator {

	public ModelEvaluatorUHC(
			ArrayList<String[]> dataset,
			Matrix dm,
			ArrayList<ArrayList<Integer>> trainAL,
			ArrayList<ArrayList<Integer>> valAL,
			ArrayList<ArrayList<Integer>> testAL){
		super(dataset, dm, trainAL, valAL, testAL);
		// Remove the role tag to the URL
		ArrayList<String[]> dataset2 = new ArrayList<String[]>();
		for(int i=0; i<dataset.size(); i++){
			String[] strA = dataset.get(i);
			String[] strB = this.removeUHCtag(strA);
			dataset2.add(strB);
		}
		super.setDataSet(dataset2);
	}
	
	private String[] removeUHCtag(String[] seq){
		String[] seq2 = new String[seq.length];
		for(int i=0; i<seq.length; i++){
			String urlRole = seq[i];
			String url = urlRole.substring(0, urlRole.length()-1);
			seq2[i] = url;
		}
		return seq2;
	}
	
}
