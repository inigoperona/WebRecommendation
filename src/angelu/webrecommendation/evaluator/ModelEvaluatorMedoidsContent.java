package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;

public class ModelEvaluatorMedoidsContent 
				extends ModelEvaluatorMedoids {

	// ATTRIBUTES
	
	private String m_enrichementStrategy = "ContentsA1";
	private String[][] m_UrlRelationMatrix = null;
	
	
	// CREATOR
	
	public ModelEvaluatorMedoidsContent(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL,
			int modePrRe,
			String usage2contentFile,
			String resSimilarityFile,
			ArrayList<Integer> noProposeURLs){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe, usage2contentFile, resSimilarityFile, noProposeURLs);
	}
	
	// GET TEST EVALUATOR
	
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorMedContent(
						testseqs, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						
						m_medoidsAL.get(iFold),
						m_gmedoidsAL.get(iFold),
						m_recosAL.get(iFold),
						m_isDistance, m_rolesW, m_knn,
						m_enrichementStrategy,
						m_UrlRelationMatrix,
						m_UrlClusteringDict,
						m_noProposeURLs);
		return eval;
	}
	
	public void setEsploitationParameters(
			String selectedEnrichementStrategy,
			String resRelationsFile){
		m_enrichementStrategy = selectedEnrichementStrategy;
		this.readRelationsMatrix(resRelationsFile);
		
	}
	
	private void readRelationsMatrix(String resRelationsFile){
		ArrayList<String> linebyline = this.readLineByLine(resRelationsFile);
		// parse the lines
		int nURL = linebyline.size();
		m_UrlRelationMatrix = new String[nURL][nURL];
		for(int i=0; i<linebyline.size(); i++){
			String line = linebyline.get(i);
			String[] lineA = line.split(";");
			for(int j=0; j<lineA.length; j++){
				m_UrlRelationMatrix[i][j] = lineA[j]; 
			}
		}
	}
	
}
