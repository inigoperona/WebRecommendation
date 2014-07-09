package angelu.webrecommendation.evaluator;

import java.util.ArrayList;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorMed;

public class ModelEvaluatorMedoidsContent 
				extends ModelEvaluatorMedoids {

	// ATTRIBUTES
	
	private String m_enrichementStrategy = "Contents001";
	
	
	// CREATOR
	
	public ModelEvaluatorMedoidsContent(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL);
	}
	
	// GET TEST EVALUATOR
	
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorMedContent(
						testseqs, 
						m_medoidsAL.get(iFold),
						m_gmedoidsAL.get(iFold),
						m_recosAL.get(iFold),
						m_isDistance, m_rolesW, m_knn,
						m_enrichementStrategy);
		return eval;
	}
	
	public void setEsploitationParameters(
			String selectedEnrichementStrategy){
		m_enrichementStrategy = selectedEnrichementStrategy;
	}
	
}
