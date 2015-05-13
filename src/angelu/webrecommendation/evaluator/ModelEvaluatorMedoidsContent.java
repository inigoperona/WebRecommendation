package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorMedoidsContent.
 */
public class ModelEvaluatorMedoidsContent 
				extends ModelEvaluatorMedoids {

	// ATTRIBUTES
	
	/** The m_enrichement strategy. */
	private String m_enrichementStrategy = "ContentsA1";
	
	/** The m_ url relation matrix. */
	private String[][] m_UrlRelationMatrix = null;
	
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator medoids content.
	 *
	 * @param dataset the dataset
	 * @param datasetSplit the dataset split
	 * @param dm the dm
	 * @param trainAL the train al
	 * @param valAL the val al
	 * @param testAL the test al
	 * @param modePrRe the mode pr re
	 * @param usage2contentFile the usage2content file
	 * @param resSimilarityFile the res similarity file
	 * @param noProposeURLs the no propose ur ls
	 */
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
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids#getTestSetEvaluator(int, java.util.ArrayList)
	 */
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorMedContent(
						testseqs, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						
						m_medoidsAL.get(iFold), m_clustersAL.get(iFold), m_gmedoidsAL.get(iFold),
						m_recosAL.get(iFold),
						m_isDistance, m_rolesW, m_knn,
						m_enrichementStrategy,
						m_UrlRelationMatrix,
						m_UrlClusteringDict,
						m_noProposeURLs);
		return eval;
	}
	
	/**
	 * Sets the esploitation parameters.
	 *
	 * @param selectedEnrichementStrategy the selected enrichement strategy
	 * @param resRelationsFile the res relations file
	 */
	public void setEsploitationParameters(
			String selectedEnrichementStrategy,
			String resRelationsFile){
		m_enrichementStrategy = selectedEnrichementStrategy;
		if(!resRelationsFile.contains("empty.txt")){
			this.readRelationsMatrix(resRelationsFile);
		}
		
	}
	
	/**
	 * Read relations matrix.
	 *
	 * @param resRelationsFile the res relations file
	 */
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
