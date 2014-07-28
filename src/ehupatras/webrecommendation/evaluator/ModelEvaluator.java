package ehupatras.webrecommendation.evaluator;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;
import ehupatras.webrecommendation.structures.Website;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;

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
		// topic1: based on url to topic distribution matrix
	private int[] m_url2topic = null;
	private float m_topicmatch = 0.5f;
		// topic2: based on url clustering
	protected HashMap<Integer,Integer> m_UrlClusteringDict = null;
	// index pages
	private int[] m_homepages = null;
	
		
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
		// prepare all database
		m_dataset = this.removeUHCTagDB(dataset);
		m_datasetUHC = dataset;
		if(datasetSplit!=null){
			m_datasetSplit = this.removeUHCTagDB(datasetSplit);
			m_datasetSplitUHC = datasetSplit;
		}
		// the distance matrix of all database
		m_distancematrix = dm;
		
		// divide the database to evaluate
		m_trainAL = trainAL;
		m_valAL = valAL;
		m_testAL = testAL;
		
		// number of folds
		m_nFolds = m_trainAL.size();
		
		// have identifyed the homepage-index pages
		m_homepages = Website.getHomePages();
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
	
	
	////////////////////////////////////////////////////////////////////
	
	// MODEL EVALUATION //
	
	public String computeEvaluationTest(
			String mode, 
			int nrecos,
			long seed){
		return this.computeEvaluation(m_testAL, mode, nrecos, seed);
	}
	
	public String computeEvaluationVal(
			String mode, 
			int nrecos,
			long seed){
		return this.computeEvaluation(m_valAL, mode, nrecos, seed);
	}
	
	
	// CARRY OUT THE EVALUATION
	
	private String computeEvaluation(
					ArrayList<ArrayList<Long>> evalAL,
					String mode, 
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
		
		// HONEST METRICS 
		// URL level metrics
		float hitratio = 0f;
		float clicksoonration = 0f;
		float[] prA = new float[m_confusionPoints.length];
		float[] reA = new float[m_confusionPoints.length];
		float[] fmA = new float[m_confusionPoints.length];
		float[] moPrA = new float[m_confusionPoints.length];
		float[] moReA = new float[m_confusionPoints.length];
		float[] moFmA = new float[m_confusionPoints.length];		
		// TOPIC1 level metrics
		float hitratioTop1 = 0f;
		float clicksoonrationTop1 = 0f;
		float[] prATop1 = new float[m_confusionPoints.length];
		float[] reATop1 = new float[m_confusionPoints.length];
		float[] fmATop1 = new float[m_confusionPoints.length];
		float[] moPrATop1 = new float[m_confusionPoints.length];
		float[] moReATop1 = new float[m_confusionPoints.length];
		float[] moFmATop1 = new float[m_confusionPoints.length];
		// TOPIC2 level metrics
		float hitratioTop2 = 0f;
		float clicksoonrationTop2 = 0f;
		float[] prATop2 = new float[m_confusionPoints.length];
		float[] reATop2 = new float[m_confusionPoints.length];
		float[] fmATop2 = new float[m_confusionPoints.length];
		float[] moPrATop2 = new float[m_confusionPoints.length];
		float[] moReATop2 = new float[m_confusionPoints.length];
		float[] moFmATop2 = new float[m_confusionPoints.length];
		
		// COUNT HOMEPAGE/INDEX ALWAYS CORRECT
		// URL level metrics
		float hitratio_OkHome = 0f;
		float clicksoonration_OkHome = 0f;
		float[] prA_OkHome = new float[m_confusionPoints.length];
		float[] reA_OkHome = new float[m_confusionPoints.length];
		float[] fmA_OkHome = new float[m_confusionPoints.length];
		float[] moPrA_OkHome = new float[m_confusionPoints.length];
		float[] moReA_OkHome = new float[m_confusionPoints.length];
		float[] moFmA_OkHome = new float[m_confusionPoints.length];
		
		// TOPIC level metrics
		float hitratioTop_OkHome = 0f;
		float clicksoonrationTop_OkHome = 0f;
		float[] prATop_OkHome = new float[m_confusionPoints.length];
		float[] reATop_OkHome = new float[m_confusionPoints.length];
		float[] fmATop_OkHome = new float[m_confusionPoints.length];
		float[] moPrATop_OkHome = new float[m_confusionPoints.length];
		float[] moReATop_OkHome = new float[m_confusionPoints.length];
		float[] moFmATop_OkHome = new float[m_confusionPoints.length];
		
		
		// Create a markov chain
		ModelEvaluatorMarkovChain modelMC = null;
		if(mode.equals("ST_markov")){
			modelMC = new ModelEvaluatorMarkovChain(
				m_datasetUHC, m_datasetSplitUHC, 
				m_distancematrix, 
				m_trainAL, m_valAL, m_testAL);
			modelMC.buildMC();
		}
		
		
		
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
			eval.setTopicParameters(
					m_urlIds, 
					m_url2topic, m_topicmatch,
					m_UrlClusteringDict);
			eval.computeEvaluation(
					mode, 
					nrecos, seed, 
					m_homepages,
					(modelMC!=null ? modelMC.getMarkovChain(i) : null)
							);
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
			
			// URL level metrics - HONEST
			
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
			
			// TOPIC1 level metrics
			
			hitratioTop1 = hitratioTop1 + eval.getHitRatioTop1();
			clicksoonrationTop1 = clicksoonrationTop1 + eval.getClickSoonRatioTop1();
			float[] prA2Top1 = eval.getPrecisionsTop1();
			float[] reA2Top1 = eval.getRecallsTop1();
			float[] fmA2Top1 = eval.getFmeasuresTop1();
			float[] moPrA2Top1 = eval.getModelPrecisionsTop1();
			float[] moReA2Top1 = eval.getModelRecallsTop1();
			float[] moFmA2Top1 = eval.getModelFmeasuresTop1();
			for(int j=0; j<m_confusionPoints.length; j++){
				prATop1[j] = prATop1[j] + prA2Top1[j];
				reATop1[j] = reATop1[j] + reA2Top1[j];
				fmATop1[j] = fmATop1[j] + fmA2Top1[j];
				moPrATop1[j] = moPrATop1[j] + moPrA2Top1[j];
				moReATop1[j] = moReATop1[j] + moReA2Top1[j];
				moFmATop1[j] = moFmATop1[j] + moFmA2Top1[j];
			}
			
			// TOPIC1 level metrics
			
			hitratioTop2 = hitratioTop2 + eval.getHitRatioTop2();
			clicksoonrationTop2 = clicksoonrationTop2 + eval.getClickSoonRatioTop2();
			float[] prA2Top2 = eval.getPrecisionsTop2();
			float[] reA2Top2 = eval.getRecallsTop2();
			float[] fmA2Top2 = eval.getFmeasuresTop2();
			float[] moPrA2Top2 = eval.getModelPrecisionsTop2();
			float[] moReA2Top2 = eval.getModelRecallsTop2();
			float[] moFmA2Top2 = eval.getModelFmeasuresTop2();
			for(int j=0; j<m_confusionPoints.length; j++){
				prATop2[j] = prATop2[j] + prA2Top2[j];
				reATop2[j] = reATop2[j] + reA2Top2[j];
				fmATop2[j] = fmATop2[j] + fmA2Top2[j];
				moPrATop2[j] = moPrATop2[j] + moPrA2Top2[j];
				moReATop2[j] = moReATop2[j] + moReA2Top2[j];
				moFmATop2[j] = moFmATop2[j] + moFmA2Top2[j];
			}
			
			// URL level metrics - homepage always true
			
			hitratio_OkHome = hitratio_OkHome + eval.getHitRatio_OkHome();
			clicksoonration_OkHome = clicksoonration_OkHome + eval.getClickSoonRatio_OkHome();
			float[] prA2_OkHome   = eval.getPrecisions_OkHome();
			float[] reA2_OkHome   = eval.getRecalls_OkHome();
			float[] fmA2_OkHome   = eval.getFmeasures_OkHome();
			float[] moPrA2_OkHome = eval.getModelPrecisions_OkHome();
			float[] moReA2_OkHome = eval.getModelRecalls_OkHome();
			float[] moFmA2_OkHome = eval.getModelFmeasures_OkHome();
			for(int j=0; j<m_confusionPoints.length; j++){
				prA_OkHome[j]   = prA_OkHome[j]   + prA2_OkHome[j];
				reA_OkHome[j]   = reA_OkHome[j]   + reA2_OkHome[j];
				fmA_OkHome[j]   = fmA_OkHome[j]   + fmA2_OkHome[j];
				moPrA_OkHome[j] = moPrA_OkHome[j] + moPrA2_OkHome[j];
				moReA_OkHome[j] = moReA_OkHome[j] + moReA2_OkHome[j];
				moFmA_OkHome[j] = moFmA_OkHome[j] + moFmA2_OkHome[j];
			}
			
			// TOPIC level metrics - homepage always true
			
			hitratioTop_OkHome = hitratioTop_OkHome + eval.getHitRatioTop_OkHome();
			clicksoonrationTop_OkHome = clicksoonrationTop_OkHome + eval.getClickSoonRatioTop_OkHome();
			float[] prA2Top_OkHome   = eval.getPrecisionsTop_OkHome();
			float[] reA2Top_OkHome   = eval.getRecallsTop_OkHome();
			float[] fmA2Top_OkHome   = eval.getFmeasuresTop_OkHome();
			float[] moPrA2Top_OkHome = eval.getModelPrecisionsTop_OkHome();
			float[] moReA2Top_OkHome = eval.getModelRecallsTop_OkHome();
			float[] moFmA2Top_OkHome = eval.getModelFmeasuresTop_OkHome();
			for(int j=0; j<m_confusionPoints.length; j++){
				prATop_OkHome[j]   = prATop_OkHome[j]   + prA2Top_OkHome[j];
				reATop_OkHome[j]   = reATop_OkHome[j]   + reA2Top_OkHome[j];
				fmATop_OkHome[j]   = fmATop_OkHome[j]   + fmA2Top_OkHome[j];
				moPrATop_OkHome[j] = moPrATop_OkHome[j] + moPrA2Top_OkHome[j];
				moReATop_OkHome[j] = moReATop_OkHome[j] + moReA2Top_OkHome[j];
				moFmATop_OkHome[j] = moFmATop_OkHome[j] + moFmA2Top_OkHome[j];
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
		
		// URL level metrics - HONEST
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
		
		// TOPIC1 level metrics
		hitratioTop1 = hitratioTop1 / (float)m_nFolds;
		clicksoonrationTop1 = clicksoonrationTop1 / (float)m_nFolds;
		for(int j=0; j<m_confusionPoints.length; j++){
			prATop1[j] = prATop1[j] / (float)m_nFolds;
			reATop1[j] = reATop1[j] / (float)m_nFolds;
			fmATop1[j] = fmATop1[j] / (float)m_nFolds;
			moPrATop1[j] = moPrATop1[j] / (float)m_nFolds;
			moReATop1[j] = moReATop1[j] / (float)m_nFolds;
			moFmATop1[j] = moFmATop1[j] / (float)m_nFolds;
		}
		
		// TOPIC2 level metrics
		hitratioTop2 = hitratioTop2 / (float)m_nFolds;
		clicksoonrationTop2 = clicksoonrationTop2 / (float)m_nFolds;
		for(int j=0; j<m_confusionPoints.length; j++){
			prATop2[j] = prATop2[j] / (float)m_nFolds;
			reATop2[j] = reATop2[j] / (float)m_nFolds;
			fmATop2[j] = fmATop2[j] / (float)m_nFolds;
			moPrATop2[j] = moPrATop2[j] / (float)m_nFolds;
			moReATop2[j] = moReATop2[j] / (float)m_nFolds;
			moFmATop2[j] = moFmATop2[j] / (float)m_nFolds;
		}
		
		// URL level metrics - homepage always true
		hitratio_OkHome = hitratio_OkHome / (float)m_nFolds;
		clicksoonration_OkHome = clicksoonration_OkHome / (float)m_nFolds;
		for(int j=0; j<m_confusionPoints.length; j++){
			prA_OkHome[j]   = prA_OkHome[j]   / (float)m_nFolds;
			reA_OkHome[j]   = reA_OkHome[j]   / (float)m_nFolds;
			fmA_OkHome[j]   = fmA_OkHome[j]   / (float)m_nFolds;
			moPrA_OkHome[j] = moPrA_OkHome[j] / (float)m_nFolds;
			moReA_OkHome[j] = moReA_OkHome[j] / (float)m_nFolds;
			moFmA_OkHome[j] = moFmA_OkHome[j] / (float)m_nFolds;
		}
		
		// TOPIC level metrics - homepage always true
		hitratioTop_OkHome = hitratioTop_OkHome / (float)m_nFolds;
		clicksoonrationTop_OkHome = clicksoonrationTop_OkHome / (float)m_nFolds;
		for(int j=0; j<m_confusionPoints.length; j++){
			prATop_OkHome[j] = prATop_OkHome[j] / (float)m_nFolds;
			reATop_OkHome[j] = reATop_OkHome[j] / (float)m_nFolds;
			fmATop_OkHome[j] = fmATop_OkHome[j] / (float)m_nFolds;
			moPrATop_OkHome[j] = moPrATop_OkHome[j] / (float)m_nFolds;
			moReATop_OkHome[j] = moReATop_OkHome[j] / (float)m_nFolds;
			moFmATop_OkHome[j] = moFmATop_OkHome[j] / (float)m_nFolds;
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
		
		// URL level statistics - HONEST
		results = results + "," + hitratio;
		results = results + "," + clicksoonration;
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + prA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + reA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + fmA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moPrA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moReA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moFmA[j];}
		
		// TOPIC1 level statistics
		results = results + "," + hitratioTop1;
		results = results + "," + clicksoonrationTop1;
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + prATop1[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + reATop1[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + fmATop1[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moPrATop1[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moReATop1[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moFmATop1[j];}

		// TOPIC2 level statistics
		results = results + "," + hitratioTop2;
		results = results + "," + clicksoonrationTop2;
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + prATop2[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + reATop2[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + fmATop2[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moPrATop2[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moReATop2[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moFmATop2[j];}

		
		// URL level statistics - homepage always true
		results = results + "," + hitratio_OkHome;
		results = results + "," + clicksoonration_OkHome;
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + prA_OkHome[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + reA_OkHome[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + fmA_OkHome[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moPrA_OkHome[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moReA_OkHome[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moFmA_OkHome[j];}
		
		// TOPIC level statistics - homepage always true
		results = results + "," + hitratioTop_OkHome;
		results = results + "," + clicksoonrationTop_OkHome;
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + prATop_OkHome[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + reATop_OkHome[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + fmATop_OkHome[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moPrATop_OkHome[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moReATop_OkHome[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moFmATop_OkHome[j];}		
		
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
		
		// URL level metrics - HONEST
		header = header + ",hitratio";
		header = header + ",clicksoonration";
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",pr_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",re_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",fm" + m_fmeasurebeta + "_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mPr_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mRe_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mFm" + m_fmeasurebeta + "_" + m_confusionPoints[j];}
		
		// TOPIC1 level metrics
		header = header + ",hitratioTop1";
		header = header + ",clicksoonrationTop1";
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",prTop1_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",reTop1_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",fm" + m_fmeasurebeta + "Top1_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mPrTop1_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mReTop1_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mFm" + m_fmeasurebeta + "Top1_" + m_confusionPoints[j];}
		
		// TOPIC1 level metrics
		header = header + ",hitratioTop2";
		header = header + ",clicksoonrationTop2";
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",prTop2_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",reTop2_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",fm" + m_fmeasurebeta + "Top2_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mPrTop2_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mReTop2_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mFm" + m_fmeasurebeta + "Top2_" + m_confusionPoints[j];}
		
		// URL level metrics - homepage always true
		header = header + ",hitratioOkH";
		header = header + ",clicksoonrationOkH";
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",prOkH_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",reOkH_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",fm" + m_fmeasurebeta + "OkH_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mPrOkH_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mReOkH_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mFm" + m_fmeasurebeta + "OkH_" + m_confusionPoints[j];}
		
		// TOPIC level metrics - homepage always true
		header = header + ",hitratioTop";
		header = header + ",clicksoonrationTop";
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",prTopOkH_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",reTopOkH_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",fm" + m_fmeasurebeta + "TopOkH_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mPrTopOkH_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mReTopOkH_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mFm" + m_fmeasurebeta + "TopOkH_" + m_confusionPoints[j];}
		
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
	public void setTopicParameters(
			ArrayList<Integer> urlIds, 
			int[] url2topic, float topicmatch,
			String urlClusteringDictFile){
		m_urlIds = urlIds;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
		this.readUrlClustering(urlClusteringDictFile);
	}
	
	
	// read URL to cluster file
	
	private void readUrlClustering(String clusterPartitionFile){
		ArrayList<String> linebyline = this.readLineByLine(clusterPartitionFile);
		// parse the lines
		m_UrlClusteringDict = new HashMap<Integer,Integer>();
		for(int i=0; i<linebyline.size(); i++){
			String line = linebyline.get(i);
			String[] lineA = line.split(";");
			int urlIndex = Integer.valueOf(lineA[0]) - 1;
			int clIndex = Integer.valueOf(lineA[1]);
			if(!m_UrlClusteringDict.containsKey(urlIndex)){
				m_UrlClusteringDict.put(urlIndex, clIndex);
			}
		}
	}
	
	private ArrayList<String> readLineByLine(String filename){
		ArrayList<String> linebyline = new ArrayList<String>();
		FileInputStream fstream = null;
		try{
			fstream = new FileInputStream(filename);
		} catch (FileNotFoundException ex){
			System.err.println(
					"[angelu.webrecommendation.evaluator." +
					"ModelEvaluatorMedoidsContent.readLineByLine] " +
					"File not found: " + filename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		try{
			while ((line= br.readLine())!= null){
				linebyline.add(line);
			}
			br.close();
		} catch(IOException ex){
			System.err.println(
					"[angelu.webrecommendation.evaluator." +
					"ModelEvaluatorMedoidsContent.readLineByLine] " +
					"Problems reading the file: " + filename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		return linebyline;
	}
	
	
}
