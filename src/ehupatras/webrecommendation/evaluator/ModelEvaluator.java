package ehupatras.webrecommendation.evaluator;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;

public abstract class ModelEvaluator {

	// ATTRIBUTES
	
	// Number of folds
	protected int m_nFolds;
	
	// database
	protected ArrayList<String[]> m_dataset = null; // without role tag
	protected ArrayList<String[]> m_datasetUHC = null; // with role tags
	protected ArrayList<String[]> m_datasetSplit = null;
	protected ArrayList<String[]> m_datasetSplitUHC = null;
	protected Matrix m_distancematrix;
	// To do the evaluation based on the database
	protected ArrayList<ArrayList<Long>> m_trainAL;
	protected ArrayList<ArrayList<Long>> m_valAL;
	protected ArrayList<ArrayList<Long>> m_testAL;
	
	
	// metrics' parameters
	private float[] m_confusionPoints = 
		{0.10f,0.25f,0.50f,0.75f,0.90f};
	private float m_fmeasurebeta = (float)0.5;
	// topic related parameters
	private ArrayList<Integer> m_urlIds = null;
	private int[] m_url2topic = null;
	private float m_topicmatch = 0.5f;
	
		
	// Write the recommendations
	private String m_lineHeader = null;
	private BufferedWriter m_evalWriter = null;
	

	
	
	// CONSTRUCTOR
	
	public ModelEvaluator(
					ArrayList<String[]> dataset,
					ArrayList<String[]> datasetSplit,
					Matrix dm,
					ArrayList<ArrayList<Long>> trainAL,
					ArrayList<ArrayList<Long>> valAL,
					ArrayList<ArrayList<Long>> testAL){
		m_dataset = this.removeUHCTagDB(dataset);
		m_datasetUHC = dataset;
		if(datasetSplit!=null){
			m_datasetSplit = this.removeUHCTagDB(datasetSplit);
			m_datasetSplitUHC = datasetSplit;
		}
		
		m_distancematrix = dm;
		
		m_trainAL = trainAL;
		m_valAL = valAL;
		m_testAL = testAL;
		
		m_nFolds = m_trainAL.size();
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
	
	protected void setDataSet(ArrayList<String[]> dataset){
		m_dataset = dataset;
	}
	
	protected void setDataSetSplit(ArrayList<String[]> dataset){
		m_datasetSplit = dataset;
	}
	
	protected ArrayList<String[]> getDataSet(boolean isSplit){
		if(!isSplit){
			return m_dataset;
		} else {
			return m_datasetSplit;
		}
	}
	
	protected ArrayList<String[]> getDataSetUHC(boolean isSplit){
		if(!isSplit){
			return m_datasetUHC;
		} else {
			return m_datasetSplitUHC;
		}
	}
	

	
	
	// ABSTRACT FUNCTIONS
	
	public abstract TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs);	
	
	public int getNumberOfNodes(int iFold){
		return 0;
	}
	public float getNumberOfEdges(int iFold){
		return 0f;
	}
	
	
	///////////////////////////////////////////////////
	
	// MODEL EVALUATION //
	
	public String computeEvaluationTest(int mode, 
			int nrecos,
			long seed){
		return this.computeEvaluation(m_testAL, mode, nrecos, seed);
	}
	
	public String computeEvaluationVal(int mode, 
			int nrecos,
			long seed){
		return this.computeEvaluation(m_valAL, mode, nrecos, seed);
	}
	
	
	// CARRY OUT THE EVALUATION
	
	private String computeEvaluation(
					ArrayList<ArrayList<Long>> evalAL,
					int mode, 
					int nrecos,
					long seed){
		
		// METRICS //
		int trnURLs = 0;
		int trnSeqs = 0;
		int tsnURLs = 0;
		int tsnSeqs = 0;
		int nNodes = 0;
		float nEdges = 0;
		float nrec = 0f;
		int nFailuresI = 0;
		float[] failuresHist = new float[12];
		
		// URL level metrics
		float hitratio = 0f;
		float clicksoonration = 0f;
		float[] prA = new float[m_confusionPoints.length];
		float[] reA = new float[m_confusionPoints.length];
		float[] fmA = new float[m_confusionPoints.length];
		float[] moPrA = new float[m_confusionPoints.length];
		float[] moReA = new float[m_confusionPoints.length];
		float[] moFmA = new float[m_confusionPoints.length];
		
		// TOPIC level metrics
		float hitratioTop = 0f;
		float clicksoonrationTop = 0f;
		float[] prATop = new float[m_confusionPoints.length];
		float[] reATop = new float[m_confusionPoints.length];
		float[] fmATop = new float[m_confusionPoints.length];
		float[] moPrATop = new float[m_confusionPoints.length];
		float[] moReATop = new float[m_confusionPoints.length];
		float[] moFmATop = new float[m_confusionPoints.length];
		
		
		
		// Create a markov chain
		ModelEvaluatorMarkovChain modelMC = new ModelEvaluatorMarkovChain(
				m_datasetUHC, m_datasetSplitUHC, 
				m_distancematrix, 
				m_trainAL, m_valAL, m_testAL);
		modelMC.buildMC();
		
		
		
		// for each fold obtain the metrics
		for(int i=0; i<m_nFolds; i++){
			
			// get the test sequences from sessionIDs
			ArrayList<Long> sessionIDs = evalAL.get(i); 
			int[] inds = m_distancematrix.getSessionIDsIndexes(sessionIDs, false);
			ArrayList<String[]> testseqs = new ArrayList<String[]>();
			for(int j=0; j<inds.length; j++){
				String[] seq = this.getDataSet(false).get(inds[j]);
				testseqs.add(seq);
			}
			
			
			
			// CREATE THE MODEL //
			TestSetEvaluator eval = this.getTestSetEvaluator(i, testseqs);
			
			
			
			// if we have to write the recommendations
			if(m_lineHeader!=null){
				eval.setLineHeader(m_lineHeader + "f" + i + ";",
						m_evalWriter);
			}
			
			
			// carry out the evaluation
			eval.setConfusionPoints(m_confusionPoints);
			eval.setFmeasureBeta(m_fmeasurebeta);
			eval.setTopicParameters(m_urlIds, m_url2topic, m_topicmatch);
			eval.computeEvaluation(
					mode, nrecos, seed, 
					modelMC.getMarkovChain(i));
			//eval.writeResults();
			
			
			
			// METRICS1
			
			// get the train sequences from sessionIDs
			ArrayList<Long> trsessionIDs = m_trainAL.get(i); 
			int[] trinds = m_distancematrix.getSessionIDsIndexes(trsessionIDs, m_datasetSplit!=null);
			ArrayList<String[]> trainseqs = new ArrayList<String[]>();
			for(int j=0; j<trinds.length; j++){
				String[] seq = this.getDataSet(m_datasetSplit!=null).get(trinds[j]);
				trainseqs.add(seq);
			}
			for(int j=0; j<trainseqs.size(); j++){ trnURLs = trnURLs + trainseqs.get(j).length; }
			trnSeqs = trnSeqs + trainseqs.size();
			
			// METRICS2
			
			tsnURLs = tsnURLs + eval.getNumberOfClicks();
			tsnSeqs = tsnSeqs + eval.getNumberOfSequences();
			
			// METRICS3
			
			nNodes = nNodes + this.getNumberOfNodes(i);
			nEdges = nEdges + this.getNumberOfEdges(i);
			
			// METRICS4
			
			nrec = nrec + eval.getNumberOfRecommendationsRatio();
			nFailuresI = nFailuresI + eval.getNumberOfFailures();
			float[] failuresHist2 = eval.getFailuresHistogram();
			for(int j=0; j<failuresHist2.length; j++){
				failuresHist[j] = failuresHist[j] + failuresHist2[j];
			}
			
			// URL level metrics
			
			hitratio = hitratio + eval.getHitRatio();
			clicksoonration = clicksoonration + eval.getClickSoonRatio();
			float[] prA2 = eval.getPrecisions();
			float[] reA2 = eval.getRecalls();
			float[] fmA2 = eval.getFmeasures();
			float[] moPrA2 = eval.getModelPrecisions();
			float[] moReA2 = eval.getModelRecalls();
			float[] moFmA2 = eval.getModelFmeasures();
			for(int j=0; j<m_confusionPoints.length; j++){
				prA[j] = prA[j] + prA2[j];
				reA[j] = reA[j] + reA2[j];
				fmA[j] = fmA[j] + fmA2[j];
				moPrA[j] = moPrA[j] + moPrA2[j];
				moReA[j] = moReA[j] + moReA2[j];
				moFmA[j] = moFmA[j] + moFmA2[j];
			}
			
			// TOPIC level metrics
			
			hitratioTop = hitratioTop + eval.getHitRatioTop();
			clicksoonrationTop = clicksoonrationTop + eval.getClickSoonRatioTop();
			float[] prA2Top = eval.getPrecisionsTop();
			float[] reA2Top = eval.getRecallsTop();
			float[] fmA2Top = eval.getFmeasuresTop();
			float[] moPrA2Top = eval.getModelPrecisionsTop();
			float[] moReA2Top = eval.getModelRecallsTop();
			float[] moFmA2Top = eval.getModelFmeasuresTop();
			for(int j=0; j<m_confusionPoints.length; j++){
				prATop[j] = prATop[j] + prA2Top[j];
				reATop[j] = reATop[j] + reA2Top[j];
				fmATop[j] = fmATop[j] + fmA2Top[j];
				moPrATop[j] = moPrATop[j] + moPrA2Top[j];
				moReATop[j] = moReATop[j] + moReA2Top[j];
				moFmATop[j] = moFmATop[j] + moFmA2Top[j];
			}
			
		}
		
		
		
		// AVERAGE //
		// compute the metric's average value of all folds
		
		// General metrics
		float trnURLsf = (float)trnURLs / (float)m_nFolds;
		float trnSeqsf = (float)trnSeqs / (float)m_nFolds;
		float tsnURLsf = (float)tsnURLs / (float)m_nFolds;
		float tsnSeqsf = (float)tsnSeqs / (float)m_nFolds;
		float nNodesf = (float)nNodes / (float)m_nFolds;
		float nEdgesf = (float)nEdges / (float)m_nFolds;
		nrec = nrec / (float)m_nFolds;
		float nFailures = (float)((double)nFailuresI / (double)m_nFolds);
		for(int j=0; j<failuresHist.length; j++){
			failuresHist[j] = failuresHist[j] / (float)m_nFolds; 
		}
		
		// URL level metrics
		hitratio = hitratio / (float)m_nFolds;
		clicksoonration = clicksoonration / (float)m_nFolds;
		for(int j=0; j<m_confusionPoints.length; j++){
			prA[j] = prA[j] / (float)m_nFolds;
			reA[j] = reA[j] / (float)m_nFolds;
			fmA[j] = fmA[j] / (float)m_nFolds;
			moPrA[j] = moPrA[j] / (float)m_nFolds;
			moReA[j] = moReA[j] / (float)m_nFolds;
			moFmA[j] = moFmA[j] / (float)m_nFolds;
		}
		
		// TOPIC level metrics
		hitratioTop = hitratioTop / (float)m_nFolds;
		clicksoonrationTop = clicksoonrationTop / (float)m_nFolds;
		for(int j=0; j<m_confusionPoints.length; j++){
			prATop[j] = prATop[j] / (float)m_nFolds;
			reATop[j] = reATop[j] / (float)m_nFolds;
			fmATop[j] = fmATop[j] / (float)m_nFolds;
			moPrATop[j] = moPrATop[j] / (float)m_nFolds;
			moReATop[j] = moReATop[j] / (float)m_nFolds;
			moFmATop[j] = moFmATop[j] / (float)m_nFolds;
		}
		
		
		
		// WRITE //
		// write metric's values
		
		// General statistics
		String results = String.valueOf(trnURLsf);
		results = results + "," + trnSeqsf;
		results = results + "," + tsnURLsf;
		results = results + "," + tsnSeqsf;
		results = results + "," + nNodesf;
		results = results + "," + nEdgesf;
		results = results + "," + nrec;
		results = results + "," + nFailures;
		for(int j=0; j<failuresHist.length; j++){results = results + "," + failuresHist[j];}
		
		// URL level statistics
		results = results + "," + hitratio;
		results = results + "," + clicksoonration;
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + prA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + reA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + fmA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moPrA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moReA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moFmA[j];}
		
		// TOPIC level statistics
		results = results + "," + hitratioTop;
		results = results + "," + clicksoonrationTop;
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + prATop[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + reATop[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + fmATop[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moPrATop[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moReATop[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moFmATop[j];}

		// return
		results = results + "\n";
		return results;
	}
	
	// set line header, to write done recommendations
	public void setLineHeader(String lineHeader, BufferedWriter evalWriter){
		m_lineHeader = lineHeader;
		m_evalWriter = evalWriter;
	}
	
	
	// HEADER of results 
	
	public String getEvaluationHeader(){
		
		// General statistics 
		String header = "trNClicks";
		header = header + ",trNSeqs";
		header = header + ",tsNClicks";
		header = header + ",tsNSeqs";
		header = header + ",nNodes";
		header = header + ",nEdges";
		header = header + ",nRecos";
		header = header + ",nFailures";
		for(int j=0; j<11; j++){header = header + ",f=" + j;}
		header = header + ",f>10";
		
		// URL level metrics
		header = header + ",hitratio";
		header = header + ",clicksoonration";
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",pr_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",re_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",fm" + m_fmeasurebeta + "_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mPr_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mRe_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mFm" + m_fmeasurebeta + "_" + m_confusionPoints[j];}
		
		// TOPIC level metrics
		header = header + ",hitratioTop";
		header = header + ",clicksoonrationTop";
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",prTop_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",reTop_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",fm" + m_fmeasurebeta + "Top_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mPrTop_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mReTop_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mFm" + m_fmeasurebeta + "Top_" + m_confusionPoints[j];}
		
		// return
		header = header + "\n";
		return header;
	}
	
	
	
	// Utilities
	
	public void setConfusionPoints(float[] confusionPoints){
		m_confusionPoints = confusionPoints;
	}
	public void setFmeasureBeta(float fmeasurebeta){
		m_fmeasurebeta = fmeasurebeta;
	}
	public void setTopicParameters(ArrayList<Integer> urlIds, int[] url2topic, float topicmatch){
		m_urlIds = urlIds;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
	}
	
}
