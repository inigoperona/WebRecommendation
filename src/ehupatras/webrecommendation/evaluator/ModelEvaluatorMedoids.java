package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;

import ehupatras.clustering.cvi.CVI;
import ehupatras.sequentialpatternmining.MySPADE;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorMed;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorMedoids.
 */
public class ModelEvaluatorMedoids 
				extends ModelEvaluatorSeqMinSPADE {

	// ATTRIBUTES
	
	/** The m_medoids al. */
	protected ArrayList<ArrayList<String[]>> m_medoidsAL = null;
	
	/** The m_gmedoids al. */
	protected ArrayList<int[]> m_gmedoidsAL = null;	
	
	/** The m_recos al. */
	protected ArrayList<ArrayList<Object[]>> m_recosAL = null;
	
	/** The m_is distance. */
	protected boolean m_isDistance = true;
	
	/** The m_roles w. */
	protected float[][] m_rolesW = {{ 0f, 0f, 0f},
									{ 0f, 0f, 0f},
									{ 0f, 0f, 0f}};
	
	/** The m_knn. */
	protected int m_knn = 100;
	
	/** The m_no propose ur ls. */
	protected ArrayList<Integer> m_noProposeURLs = new ArrayList<Integer>();
	
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator medoids.
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
	public ModelEvaluatorMedoids(
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
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe, usage2contentFile, resSimilarityFile);
		m_noProposeURLs = noProposeURLs;
	}
	
	// GET TEST EVALUATOR
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluatorSuffixTree#getTestSetEvaluator(int, java.util.ArrayList)
	 */
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorMed(
						testseqs, m_modePrRe, m_conv,
						m_nURLs, m_UrlSimilarityMatrix_Content,
						m_UrlSimilarityMatrix_Usage, m_UrlSimilarityMatrix_Usage_max, m_UrlSimilarityMatrix_Usage_min,
						
						m_medoidsAL.get(iFold),
						m_gmedoidsAL.get(iFold),
						m_recosAL.get(iFold),
						m_isDistance, m_rolesW, m_knn);
		return eval;
	}
	
	/**
	 * Sets the esploitation parameters.
	 *
	 * @param isDistance the is distance
	 * @param rolesW the roles w
	 * @param knn the knn
	 */
	public void setEsploitationParameters(
			boolean isDistance,
			float[][] rolesW,
			int knn){
		m_isDistance = isDistance;
		m_rolesW = rolesW;
		m_knn = knn;
	}
	
	// BUILD MODEL
	
	/**
	 * Builds the medoids.
	 *
	 * @param minsup the minsup
	 * @param computeRecos the compute recos
	 */
	public void buildMedoids(float minsup, boolean computeRecos){
		// compute medoids for each fold
		m_medoidsAL = new ArrayList<ArrayList<String[]>>();
		m_gmedoidsAL = new ArrayList<int[]>();
		if(computeRecos){m_recosAL = new ArrayList<ArrayList<Object[]>>();}
		
		//lierni
		/*double luzera = 0.0, batezbeste = 0.0, cases = 0.0;
		int luze = 0, motz = 100;*/
		
		for(int i=0; i<m_nFolds; i++){
			Object[] medObjA = this.getMedoids(i);
			ArrayList<String[]> medoids = (ArrayList<String[]>)medObjA[0];
			int[] gmedoids = (int[])medObjA[1];
			m_medoidsAL.add(medoids);
			m_gmedoidsAL.add(gmedoids);
			if(computeRecos){m_recosAL.add(this.getRecommendations(i, minsup, m_noProposeURLs));}
		
			//lierni
			/*cases = cases + m_medoidsAL.get(i).size();
			for (int j=0; j<m_medoidsAL.get(i).size(); j++){
				luzera = luzera + m_medoidsAL.get(i).get(j).length;
				if (m_medoidsAL.get(i).get(j).length>luze){
					luze = m_medoidsAL.get(i).get(j).length;
				}
				if (m_medoidsAL.get(i).get(j).length<motz){
					motz = m_medoidsAL.get(i).get(j).length;
				}
			}*/
		}
		
		//Lierni
		/*batezbeste = luzera / cases;
		System.out.println("Motzena: " + motz + " Luzeena: " + luze);
		System.out.println("Medoideen batezbesteko luzera: " + batezbeste);*/
	}
	
	/**
	 * Gets the medoids.
	 *
	 * @param indexFold the index fold
	 * @return the medoids
	 */
	private Object[] getMedoids(int indexFold){
		// train cases indexes
		ArrayList<Long> trSesIDs = m_trainAL.get(indexFold);
		int[] inds = m_distancematrix.getSessionIDsIndexes(trSesIDs, m_datasetSplit!=null);
		
		// cluster indexes
		int[] clusters = m_clustersAL.get(indexFold);
		
		// get medoids & global medoids
		CVI cvindex = new CVI(inds,clusters);
		cvindex.computeMedoids(m_distancematrix.getMatrix(m_datasetSplit!=null));
		// treat medoids
		int[] medoids = cvindex.getMedoids();
		ArrayList<String[]> medoidSeqs = new ArrayList<String[]>();
		for(int i=0; i<medoids.length; i++){
			String[] medSeq = super.getDataSetUHC(m_datasetSplit!=null).get(medoids[i]);
			medoidSeqs.add(medSeq);
		}
		// treat global medoids
		int[] gmedoids = cvindex.getGlobalMedoids();
		
		// Return medoids
		Object[] objA = new Object[2];
		objA[0] = medoidSeqs;
		objA[1] = gmedoids;
		return objA;
	}
	
	/**
	 * Gets the recommendations.
	 *
	 * @param indexFold the index fold
	 * @param minsup the minsup
	 * @param noExtractA the no extract a
	 * @return the recommendations
	 */
	private ArrayList<Object[]> getRecommendations(int indexFold, float minsup, ArrayList<Integer> noExtractA){
		// train cases indexes
		ArrayList<Long> trSesIDs = m_trainAL.get(indexFold);
		int[] inds = m_distancematrix.getSessionIDsIndexes(trSesIDs, m_datasetSplit!=null);
		
		// cluster indexes
		int[] clusters = m_clustersAL.get(indexFold);
		// find the maximum index
		int max = Integer.MIN_VALUE;
		for(int i=0; i<clusters.length; i++){
			int clInd = clusters[i];
			if(max<clInd){
				max = clInd;
			}
		}
		
		// for each cluster extract the most common URLs
		ArrayList<Object[]> recos = new ArrayList<Object[]>();
		for(int i=0; i<=max; i++){
			ArrayList<String[]> trainseqs = new ArrayList<String[]>();
			// get train sequences
			for(int j=0; j<clusters.length; j++){
				if(i==clusters[j]){
					String[] seq = this.getDataSet(m_datasetSplit!=null).get(inds[j]);
					trainseqs.add(seq);
				}
			}
			// Frequent patter mining
			MySPADE sp = new MySPADE(trainseqs, minsup);
			Object[] objA = sp.getFrequentSequencesLength1();
			ArrayList<String> freqseqs1 = (ArrayList<String>)objA[0];
			ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
			ArrayList<String> freqseqs1_2 = new ArrayList<String>();
			ArrayList<Integer> supports_2 = new ArrayList<Integer>();
			for(int j=0; j<freqseqs1.size(); j++){
				String urlStr = freqseqs1.get(j);
				int urlId = Integer.valueOf(urlStr);
				if(!noExtractA.contains(urlId)){
					freqseqs1_2.add(urlStr);
					int sup = supports.get(j);
					supports_2.add(sup);
				}
			}
			Object[] objA_2 = new Object[2];
			objA_2[0] = freqseqs1_2;
			objA_2[1] = supports_2;
			recos.add(objA_2);
		}
		
		return recos;
	}
	
	// utilities
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluatorSuffixTree#getNumberOfNodes(int)
	 */
	public int getNumberOfNodes(int iFold){
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.evaluator.ModelEvaluatorSuffixTree#getNumberOfEdges(int)
	 */
	public float getNumberOfEdges(int iFold){
		return 0f;
	}
	
}
