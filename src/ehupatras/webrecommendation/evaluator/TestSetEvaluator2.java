package ehupatras.webrecommendation.evaluator;

import java.io.BufferedWriter;
import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.markovmodel.MarkovChain;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;

public class TestSetEvaluator2 {

	// ATTRIBUTES
	
	// test sequences 
	private ArrayList<String[]> m_sequences;
	
	// Validation metrics
	private float[] m_points = {0.10f, 0.25f, 0.50f, 0.75f, 0.90f};
	private float m_beta = (float)0.5;
	
	// failure counts
	private float m_numberOfRecommendationsRatio = (float)0;
	private int m_numberOfFailures = 0;
	private float[] m_failuresHist = new float[12]; 
	
	// URL level metrics
	private float m_hitratio = 0f;
	private float m_clicksoonratio = 0f;
	private float[] m_precision;
	private float[] m_recall;
	private float[] m_fmeasure;
	private float[] m_ModelPrecision;
	private float[] m_ModelRecall;
	private float[] m_ModelFmeasure;
	
	// Topic level metrics
	private ArrayList<Integer> m_urlIds = null;
	private int[] m_url2topic = null;
	private float m_topicmatch = 0.5f;
	private float m_hitratioTop = 0f;
	private float m_clicksoonratioTop = 0f;
	private float[] m_precisionTop;
	private float[] m_recallTop;
	private float[] m_fmeasureTop;
	private float[] m_ModelPrecisionTop;
	private float[] m_ModelRecallTop;
	private float[] m_ModelFmeasureTop;
	
	// To write recommendations
	private String m_lineHeader = null;
	private BufferedWriter m_evalWriter = null;
	
	
	
	// CREATOR
	
	public TestSetEvaluator2(ArrayList<String[]> sequences){
		this.constructor(sequences);
	}
	
	private void constructor(ArrayList<String[]> sequences){
		m_sequences = sequences;
		
		m_precision = new float[m_points.length];
		m_recall = new float[m_points.length];
		m_fmeasure = new float[m_points.length];
		m_ModelPrecision = new float[m_points.length];
		m_ModelRecall = new float[m_points.length];
		m_ModelFmeasure = new float[m_points.length];
		
		m_precisionTop = new float[m_points.length];
		m_recallTop = new float[m_points.length];
		m_fmeasureTop = new float[m_points.length];
		m_ModelPrecisionTop = new float[m_points.length];
		m_ModelRecallTop = new float[m_points.length];
		m_ModelFmeasureTop = new float[m_points.length];
	}
	
	
	
	// BODY
	
	public void computeEvaluation(
			MarkovChain markovchain){
	}
	
	public void computeEvaluation(
			MySuffixTree suffixtree){
	}
	
	public void computeEvaluation(
			ArrayList<String[]> medoids,
			int[] gmedoids,
			ArrayList<Object[]> recos,
			boolean isDistance,
			float[][] rolesW){
	}
	
	public void computeEvaluation(
			ArrayList<MySuffixTree> suffixtreeAL){
	}
	
	public void computeEvaluation(
			ArrayList<MySuffixTree> suffixtreeAL,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
	}
	
	public void computeEvaluation(
			HiddenMarkovModel hmm){
	}
	
	public void computeEvaluation(
			int mode, int nrecos, long seed, 
			MarkovChain markovchain,
			int failureMode,
			int maxMemory,
			int normMode){
		
		float numberOfRecommendationsRatio = 0f;
		int numberOfFailures = 0;
		int[] failuresHist = new int[m_failuresHist.length];
		
		// URL level metrics
		float hitratio = 0f;
		float clicksoonratio = 0f;
		float[] precission = new float[m_points.length];
		float[] recall = new float[m_points.length];
		float[] fmeasure = new float[m_points.length];
		float[] modelPrecision = new float[m_points.length];
		float[] modelRecall = new float[m_points.length];
		float[] modelFmeasure = new float[m_points.length];
		
		// TOPIC level metrics
		float hitratioTop = 0f;
		float clicksoonratioTop = 0f;
		float[] precissionTop = new float[m_points.length];
		float[] recallTop = new float[m_points.length];
		float[] fmeasureTop = new float[m_points.length];
		float[] modelPrecisionTop = new float[m_points.length];
		float[] modelRecallTop = new float[m_points.length];
		float[] modelFmeasureTop = new float[m_points.length];
		
		
		for(int i=0; i<m_sequences.size(); i++){
			String[] seq = m_sequences.get(i);
			
			
			
			// select the model
			SequenceEvaluator seqEv = this.getSequenceEvaluator();
			
			/*
			if(m_st!=null){
				// GST & clust+MSA+Wseq+ST
				seqEv = new SequenceEvaluator(seq, m_st, failureMode, maxMemory, normMode);
			} else if(m_medoids!=null && m_STAL!=null){
				// clust+ST+knn
				seqEv = new SequenceEvaluator(seq, 
							m_medoids, m_gMedoids, m_knn, m_isDistance, m_rolesW,
							m_STAL);
			} else if(m_STAL!=null){
				// clust+ST+fit
				seqEv = new SequenceEvaluator(seq, m_STAL); 
			} else if(m_medoids!=null){
				// clust+SPADE
				seqEv = new SequenceEvaluator(seq, m_medoids, m_gMedoids, m_recosAL, 
								m_isDistance, m_rolesW);
			} else if(m_hmm!=null){
				// clust+HMM
				seqEv = new SequenceEvaluator(seq, m_hmm, maxMemory);
			} else {
				// Markov Chain
				seqEv = new SequenceEvaluator(seq, m_markovchain);
			}
			*/
			
			
			
			// if we want to write recommendations
			if(m_lineHeader!=null){
				String seqStr = "-";
				if(seq.length>0){
					seqStr = seq[0];
				}
				for(int j=1; j<seq.length; j++){
					seqStr = seqStr + "," + seq[j];
				}
				seqEv.setLineHeader(m_lineHeader + i + ":" + seqStr + ";",
						m_evalWriter);
			}
			
			
			
			// METRICS //
			seqEv.setTopicParameters(m_urlIds, m_url2topic, m_topicmatch);
			seqEv.computeSequenceMetrics(mode, nrecos, seed, markovchain);
			
	
			// number of recommendations
			numberOfRecommendationsRatio = numberOfRecommendationsRatio + seqEv.getNumberOfRecommendationsRatio();
			
			// failure functions
			int nfails = seqEv.getNumberOfFailures();
			numberOfFailures = numberOfFailures + nfails;
			switch(nfails){
				case 0: failuresHist[0]++;
						break;
				case 1: failuresHist[1]++;
						break;
				case 2: failuresHist[2]++;
						break;
				case 3: failuresHist[3]++;
						break;
				case 4: failuresHist[4]++;
						break;
				case 5: failuresHist[5]++;
						break;
				case 6: failuresHist[6]++;
						break;
				case 7: failuresHist[7]++;
						break;
				case 8: failuresHist[8]++;
						break;
				case 9: failuresHist[9]++;
						break;
				case 10: failuresHist[10]++;
						break;
				default: failuresHist[11]++;
						break;
			}
			
			// URL level metrics
			hitratio = hitratio + seqEv.getHitRatio();
			clicksoonratio = clicksoonratio + seqEv.getClickSoonRatio();
			for(int j=0; j<m_points.length; j++){
				precission[j] = precission[j] + seqEv.getPrecisionAtPoint(m_points[j]);
				recall[j] = recall[j] + seqEv.getRecallAtPoint(m_points[j]);
				fmeasure[j] = fmeasure[j] + seqEv.getFmeasureAtPoint(m_beta, m_points[j]);
				modelPrecision[j] = modelPrecision[j] + seqEv.getPrecisionModelAtPoint(m_points[j]);
				modelRecall[j] = modelRecall[j] + seqEv.getRecallModelAtPoint(m_points[j]);
				modelFmeasure[j] = modelFmeasure[j] + seqEv.getFmeasureModelAtPoint(m_beta, m_points[j]);
			}
			
			// TOPIC level metrics
			hitratioTop = hitratioTop + seqEv.getHitRatioTop();
			clicksoonratioTop = clicksoonratioTop + seqEv.getClickSoonRatioTop();
			for(int j=0; j<m_points.length; j++){
				precissionTop[j] = precissionTop[j] + seqEv.getPrecisionTopAtPoint(m_points[j]);
				recallTop[j] = recallTop[j] + seqEv.getRecallTopAtPoint(m_points[j]);
				fmeasureTop[j] = fmeasureTop[j] + seqEv.getFmeasureTopAtPoint(m_beta, m_points[j]);
				modelPrecisionTop[j] = modelPrecisionTop[j] + seqEv.getPrecisionModelTopAtPoint(m_points[j]);
				modelRecallTop[j] = modelRecallTop[j] + seqEv.getRecallModelTopAtPoint(m_points[j]);
				modelFmeasureTop[j] = modelFmeasureTop[j] + seqEv.getFmeasureModelTopAtPoint(m_beta, m_points[j]);
			}
			
		}
		
		// Compute the average values
		m_numberOfRecommendationsRatio = numberOfRecommendationsRatio/(float)m_sequences.size();
		
		// Failure functions
		m_numberOfFailures = numberOfFailures;
		for(int j=0; j<failuresHist.length; j++){
			m_failuresHist[j] = (float)failuresHist[j] / (float)m_sequences.size();
		}
		
		// URL level metrics
		m_hitratio = hitratio/(float)m_sequences.size();
		m_clicksoonratio = clicksoonratio/(float)m_sequences.size();
		for(int j=0; j<m_points.length; j++){
			m_precision[j] = precission[j]/(float)m_sequences.size();
			m_recall[j] = recall[j]/(float)m_sequences.size();
			m_fmeasure[j] = fmeasure[j]/(float)m_sequences.size();
			m_ModelPrecision[j] = modelPrecision[j]/(float)m_sequences.size();
			m_ModelRecall[j] = modelRecall[j]/(float)m_sequences.size();
			m_ModelFmeasure[j] = modelFmeasure[j]/(float)m_sequences.size();
		}
		
		// TOPIC level metrics
		m_hitratioTop = hitratioTop/(float)m_sequences.size();
		m_clicksoonratioTop = clicksoonratioTop/(float)m_sequences.size();
		for(int j=0; j<m_points.length; j++){
			m_precisionTop[j] = precissionTop[j]/(float)m_sequences.size();
			m_recallTop[j] = recallTop[j]/(float)m_sequences.size();
			m_fmeasureTop[j] = fmeasureTop[j]/(float)m_sequences.size();
			m_ModelPrecisionTop[j] = modelPrecisionTop[j]/(float)m_sequences.size();
			m_ModelRecallTop[j] = modelRecallTop[j]/(float)m_sequences.size();
			m_ModelFmeasureTop[j] = modelFmeasureTop[j]/(float)m_sequences.size();
		}

	}
	
	
	
	// SET class attributes
	
	public void setConfusionPoints(float[] confusionPoints){
		m_points = confusionPoints;
	}
	public void setFmeasureBeta(float beta){
		m_beta = beta;
	}
	public void setTopicParameters(ArrayList<Integer> urlIds, int[] url2topic, float topicmatch){
		m_urlIds = urlIds;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
	}
	public void setLineHeader(String lineHeader, BufferedWriter evalWriter){
		m_lineHeader = lineHeader;
		m_evalWriter = evalWriter;
	}
	
	// GET class attributes
	
	public int getNumberOfSequences(){
		return m_sequences.size();
	}
	public int getNumberOfClicks(){
		int nURLs = 0;
		for(int i=0; i<m_sequences.size(); i++){
			nURLs = nURLs + m_sequences.get(i).length;
		}
		return nURLs;
	}
	public float getNumberOfRecommendationsRatio(){
		return m_numberOfRecommendationsRatio;
	}
	public int getNumberOfFailures(){
		return m_numberOfFailures;
	}
	public float[] getFailuresHistogram(){
		return m_failuresHist;
	}
	
	
	// URL level attributes
	
	public float getHitRatio(){
		return m_hitratio;
	}
	public float getClickSoonRatio(){
		return m_clicksoonratio;
	}
	public float[] getPrecisions(){
		return m_precision;
	}
	public float[] getRecalls(){
		return m_recall;
	}
	public float[] getFmeasures(){
		return m_fmeasure;
	}
	public float[] getModelPrecisions(){
		return m_ModelPrecision;
	}
	public float[] getModelRecalls(){
		return m_ModelRecall;
	}
	public float[] getModelFmeasures(){
		return m_ModelFmeasure;
	}
	
	
	// TOPIC level attributes
	
	public float getHitRatioTop(){
		return m_hitratioTop;
	}
	public float getClickSoonRatioTop(){
		return m_clicksoonratioTop;
	}
	public float[] getPrecisionsTop(){
		return m_precisionTop;
	}
	public float[] getRecallsTop(){
		return m_recallTop;
	}
	public float[] getFmeasuresTop(){
		return m_fmeasureTop;
	}
	public float[] getModelPrecisionsTop(){
		return m_ModelPrecisionTop;
	}
	public float[] getModelRecallsTop(){
		return m_ModelRecallTop;
	}
	public float[] getModelFmeasuresTop(){
		return m_ModelFmeasureTop;
	}
	
	
	// Utilities
	
	public void writeResults(){
		// write headers
		System.out.print("numberOfRecommendationsRatio,hitratio,clicksoonratio");
		for(int i=0; i<m_points.length; i++){
			System.out.print(",pr_" + m_points[i]);
		}
		for(int i=0; i<m_points.length; i++){
			System.out.print(",re_" + m_points[i]);
		}
		for(int i=0; i<m_points.length; i++){
			System.out.print(",fm" + m_beta + "_" + m_points[i]);
		}
		System.out.println();
		
		// write results
		System.out.print(m_numberOfRecommendationsRatio);
		System.out.print("," + m_hitratio);
		System.out.print("," + m_clicksoonratio);
		for(int i=0; i<m_points.length; i++){
			System.out.print("," + m_precision[i]);
		}
		for(int i=0; i<m_points.length; i++){
			System.out.print("," + m_recall[i]);
		}
		for(int i=0; i<m_points.length; i++){
			System.out.print("," + m_fmeasure[i]);
		}
		System.out.println();
	}
}
