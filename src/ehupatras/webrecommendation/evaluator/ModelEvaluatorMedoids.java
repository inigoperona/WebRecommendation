package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;

import ehupatras.clustering.cvi.CVI;
import ehupatras.sequentialpatternmining.MySPADE;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorMed;

public class ModelEvaluatorMedoids 
				extends ModelEvaluatorSeqMinSPADE {

	// ATTRIBUTES
	
	protected ArrayList<ArrayList<String[]>> m_medoidsAL = null;
	protected ArrayList<int[]> m_gmedoidsAL = null;	
	protected ArrayList<ArrayList<Object[]>> m_recosAL = null;
	
	protected boolean m_isDistance = true;
	protected float[][] m_rolesW = {{ 0f, 0f, 0f},
									{ 0f, 0f, 0f},
									{ 0f, 0f, 0f}};
	protected int m_knn = 100;
	
	// CREATOR
	
	public ModelEvaluatorMedoids(
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
				new TestSetEvaluatorMed(
						testseqs, 
						m_medoidsAL.get(iFold),
						m_gmedoidsAL.get(iFold),
						m_recosAL.get(iFold),
						m_isDistance, m_rolesW, m_knn);
		return eval;
	}
	
	public void setEsploitationParameters(
			boolean isDistance,
			float[][] rolesW,
			int knn){
		m_isDistance = isDistance;
		m_rolesW = rolesW;
		m_knn = knn;
	}
	
	// BUILD MODEL
	
	public void buildMedoids(float minsup, boolean computeRecos){
		// compute medoids for each fold
		m_medoidsAL = new ArrayList<ArrayList<String[]>>();
		m_gmedoidsAL = new ArrayList<int[]>();
		if(computeRecos){m_recosAL = new ArrayList<ArrayList<Object[]>>();}
		for(int i=0; i<m_nFolds; i++){
			Object[] medObjA = this.getMedoids(i);
			ArrayList<String[]> medoids = (ArrayList<String[]>)medObjA[0];
			int[] gmedoids = (int[])medObjA[1];
			m_medoidsAL.add(medoids);
			m_gmedoidsAL.add(gmedoids);
			if(computeRecos){m_recosAL.add(this.getRecommendations(i, minsup));}
		}
	}
	
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
	
	private ArrayList<Object[]> getRecommendations(int indexFold, float minsup){
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
			//ArrayList<String> freqseqs1 = (ArrayList<String>)objA[0];
			//ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
			recos.add(objA);
		}
		
		return recos;
	}
	
	// utilities
	
	public int getNumberOfNodes(int iFold){
		return 0;
	}
	
	public float getNumberOfEdges(int iFold){
		return 0f;
	}
	
}
