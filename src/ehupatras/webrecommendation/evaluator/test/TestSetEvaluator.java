package ehupatras.webrecommendation.evaluator.test;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;

import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;

public abstract class TestSetEvaluator {

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
	// topic relate attributes
	private ArrayList<Integer> m_urlIds = null;
		// topic1: based on url to topìc distribution
	private int[] m_url2topic = null;
	private float m_topicmatch = 0.5f;
		// topic2: based on url clustering
	private HashMap<Integer,Integer> m_urlClusteringDict = null;
	
	// URL level metrics - HONEST
	private float m_hitratio = 0f;
	private float m_clicksoonratio = 0f;
	private float[] m_precision;
	private float[] m_recall;
	private float[] m_fmeasure;
	private float[] m_ModelPrecision;
	private float[] m_ModelRecall;
	private float[] m_ModelFmeasure;
	private float m_cosineSimilarity = 0f;
	// Topic1 level metrics
	private float m_hitratioTop1 = 0f;
	private float m_clicksoonratioTop1 = 0f;
	private float[] m_precisionTop1;
	private float[] m_recallTop1;
	private float[] m_fmeasureTop1;
	private float[] m_ModelPrecisionTop1;
	private float[] m_ModelRecallTop1;
	private float[] m_ModelFmeasureTop1;
	// Topic2 level metrics
	private float m_hitratioTop2 = 0f;
	private float m_clicksoonratioTop2 = 0f;
	private float[] m_precisionTop2;
	private float[] m_recallTop2;
	private float[] m_fmeasureTop2;
	private float[] m_ModelPrecisionTop2;
	private float[] m_ModelRecallTop2;
	private float[] m_ModelFmeasureTop2;
	
	// URL level metrics - homepage always true
	private float m_hitratio_OkHome = 0f;
	private float m_clicksoonratio_OkHome = 0f;
	private float[] m_precision_OkHome;
	private float[] m_recall_OkHome;
	private float[] m_fmeasure_OkHome;
	private float[] m_ModelPrecision_OkHome;
	private float[] m_ModelRecall_OkHome;
	private float[] m_ModelFmeasure_OkHome;
	// Topic level metrics - homepage always true
	private float m_hitratioTop_OkHome = 0f;
	private float m_clicksoonratioTop_OkHome = 0f;
	private float[] m_precisionTop_OkHome;
	private float[] m_recallTop_OkHome;
	private float[] m_fmeasureTop_OkHome;
	private float[] m_ModelPrecisionTop_OkHome;
	private float[] m_ModelRecallTop_OkHome;
	private float[] m_ModelFmeasureTop_OkHome;
	
	// To write recommendations
	private String m_lineHeader = null;
	private BufferedWriter m_evalWriter = null;
	
	
	
	// CREATOR
	
	public TestSetEvaluator(ArrayList<String[]> sequences){
		this.constructor(sequences);
	}
	
	private void constructor(ArrayList<String[]> sequences){
		m_sequences = sequences;
		this.constructor2();
	}
	
	private void constructor2(){
		// HONEST
		m_precision = new float[m_points.length];
		m_recall = new float[m_points.length];
		m_fmeasure = new float[m_points.length];
		m_ModelPrecision = new float[m_points.length];
		m_ModelRecall = new float[m_points.length];
		m_ModelFmeasure = new float[m_points.length];		
		m_precisionTop1 = new float[m_points.length];
		m_recallTop1 = new float[m_points.length];
		m_fmeasureTop1 = new float[m_points.length];
		m_ModelPrecisionTop1 = new float[m_points.length];
		m_ModelRecallTop1 = new float[m_points.length];
		m_ModelFmeasureTop1 = new float[m_points.length];
		m_precisionTop2 = new float[m_points.length];
		m_recallTop2 = new float[m_points.length];
		m_fmeasureTop2 = new float[m_points.length];
		m_ModelPrecisionTop2 = new float[m_points.length];
		m_ModelRecallTop2 = new float[m_points.length];
		m_ModelFmeasureTop2 = new float[m_points.length];
		
		// homepage always true
		m_precision_OkHome = new float[m_points.length];
		m_recall_OkHome = new float[m_points.length];
		m_fmeasure_OkHome = new float[m_points.length];
		m_ModelPrecision_OkHome = new float[m_points.length];
		m_ModelRecall_OkHome = new float[m_points.length];
		m_ModelFmeasure_OkHome = new float[m_points.length];		
		m_precisionTop_OkHome = new float[m_points.length];
		m_recallTop_OkHome = new float[m_points.length];
		m_fmeasureTop_OkHome = new float[m_points.length];
		m_ModelPrecisionTop_OkHome = new float[m_points.length];
		m_ModelRecallTop_OkHome = new float[m_points.length];
		m_ModelFmeasureTop_OkHome = new float[m_points.length];
	}
	
	
	
	// ABSTRACT FUNCTIONS
	
	public abstract SequenceEvaluator getSequenceEvaluator(String[] sequence);
	
	
	
	// BODY
	
	public void computeEvaluation(
			String mode, 
			int nrecos, long seed,
			int[] homepages,
			MarkovChain markovchain){
		
		// ATRRIBUTES
		
		float numberOfRecommendationsRatio = 0f;
		int numberOfFailures = 0;
		int[] failuresHist = new int[m_failuresHist.length];
		
		// URL level metrics - HONEST
		float hitratio = 0f;
		float clicksoonratio = 0f;
		float[] precission = new float[m_points.length];
		float[] recall = new float[m_points.length];
		float[] fmeasure = new float[m_points.length];
		float[] modelPrecision = new float[m_points.length];
		float[] modelRecall = new float[m_points.length];
		float[] modelFmeasure = new float[m_points.length];
		float cosineSim = 0f;
		
		// TOPIC level metrics
		float hitratioTop1 = 0f;
		float clicksoonratioTop1 = 0f;
		float[] precissionTop1 = new float[m_points.length];
		float[] recallTop1 = new float[m_points.length];
		float[] fmeasureTop1 = new float[m_points.length];
		float[] modelPrecisionTop1 = new float[m_points.length];
		float[] modelRecallTop1 = new float[m_points.length];
		float[] modelFmeasureTop1 = new float[m_points.length];
		
		// TOPIC2 level metrics
		float hitratioTop2 = 0f;
		float clicksoonratioTop2 = 0f;
		float[] precissionTop2 = new float[m_points.length];
		float[] recallTop2 = new float[m_points.length];
		float[] fmeasureTop2 = new float[m_points.length];
		float[] modelPrecisionTop2 = new float[m_points.length];
		float[] modelRecallTop2 = new float[m_points.length];
		float[] modelFmeasureTop2 = new float[m_points.length];
		
		// URL level metrics - homepage always true
		float hitratio_OkHome = 0f;
		float clicksoonratio_OkHome = 0f;
		float[] precission_OkHome = new float[m_points.length];
		float[] recall_OkHome = new float[m_points.length];
		float[] fmeasure_OkHome = new float[m_points.length];
		float[] modelPrecision_OkHome = new float[m_points.length];
		float[] modelRecall_OkHome = new float[m_points.length];
		float[] modelFmeasure_OkHome = new float[m_points.length];
		
		// TOPIC level metrics - homepage always true
		float hitratioTop_OkHome = 0f;
		float clicksoonratioTop_OkHome = 0f;
		float[] precissionTop_OkHome = new float[m_points.length];
		float[] recallTop_OkHome = new float[m_points.length];
		float[] fmeasureTop_OkHome = new float[m_points.length];
		float[] modelPrecisionTop_OkHome = new float[m_points.length];
		float[] modelRecallTop_OkHome = new float[m_points.length];
		float[] modelFmeasureTop_OkHome = new float[m_points.length];
		
		
		for(int i=0; i<m_sequences.size(); i++){
			String[] seq = m_sequences.get(i);
			
			
			
			// select the model
			SequenceEvaluator seqEv = this.getSequenceEvaluator(seq);
			
			
			
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
			seqEv.setTopicParameters(
					m_urlIds, 
					m_url2topic, m_topicmatch,
					m_urlClusteringDict);
			seqEv.computeSequenceMetrics(
					mode, 
					nrecos, seed,
					homepages,
					(markovchain!=null ? markovchain : null)
					);
			
	
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
			
			// URL level metrics - HONEST
			hitratio = hitratio + seqEv.getHitRatio();
			clicksoonratio = clicksoonratio + seqEv.getClickSoonRatio();
			for(int j=0; j<m_points.length; j++){
				precission[j]     = precission[j]     + seqEv.getPrecisionAtPoint(m_points[j]);
				recall[j]         = recall[j]         + seqEv.getRecallAtPoint(m_points[j]);
				fmeasure[j]       = fmeasure[j]       + seqEv.getFmeasureAtPoint(m_beta, m_points[j]);
				modelPrecision[j] = modelPrecision[j] + seqEv.getPrecisionModelAtPoint(m_points[j]);
				modelRecall[j]    = modelRecall[j]    + seqEv.getRecallModelAtPoint(m_points[j]);
				modelFmeasure[j]  = modelFmeasure[j]  + seqEv.getFmeasureModelAtPoint(m_beta, m_points[j]);
			}
			cosineSim = cosineSim + seqEv.getCosineSimilarity();
			
			// TOPIC1 level metrics
			hitratioTop1 = hitratioTop1 + seqEv.getHitRatioTop1();
			clicksoonratioTop1 = clicksoonratioTop1 + seqEv.getClickSoonRatioTop1();
			for(int j=0; j<m_points.length; j++){
				precissionTop1[j]     = precissionTop1[j]     + seqEv.getPrecisionTopAtPoint1(m_points[j]);
				recallTop1[j]         = recallTop1[j]         + seqEv.getRecallTopAtPoint1(m_points[j]);
				fmeasureTop1[j]       = fmeasureTop1[j]       + seqEv.getFmeasureTopAtPoint1(m_beta, m_points[j]);
				modelPrecisionTop1[j] = modelPrecisionTop1[j] + seqEv.getPrecisionModelTopAtPoint1(m_points[j]);
				modelRecallTop1[j]    = modelRecallTop1[j]    + seqEv.getRecallModelTopAtPoint1(m_points[j]);
				modelFmeasureTop1[j]  = modelFmeasureTop1[j]  + seqEv.getFmeasureModelTopAtPoint1(m_beta, m_points[j]);
			}
			
			// TOPIC2 level metrics
			hitratioTop2 = hitratioTop2 + seqEv.getHitRatioTop2();
			clicksoonratioTop2 = clicksoonratioTop2 + seqEv.getClickSoonRatioTop2();
			for(int j=0; j<m_points.length; j++){
				precissionTop2[j]     = precissionTop2[j]     + seqEv.getPrecisionTopAtPoint2(m_points[j]);
				recallTop2[j]         = recallTop2[j]         + seqEv.getRecallTopAtPoint2(m_points[j]);
				fmeasureTop2[j]       = fmeasureTop2[j]       + seqEv.getFmeasureTopAtPoint2(m_beta, m_points[j]);
				modelPrecisionTop2[j] = modelPrecisionTop2[j] + seqEv.getPrecisionModelTopAtPoint2(m_points[j]);
				modelRecallTop2[j]    = modelRecallTop2[j]    + seqEv.getRecallModelTopAtPoint2(m_points[j]);
				modelFmeasureTop2[j]  = modelFmeasureTop2[j]  + seqEv.getFmeasureModelTopAtPoint2(m_beta, m_points[j]);
			}
			
			// URL level metrics - homepage always true
			hitratio_OkHome = hitratio_OkHome + seqEv.getHitRatio_OkHome();
			clicksoonratio_OkHome = clicksoonratio_OkHome + seqEv.getClickSoonRatio_OkHome();
			for(int j=0; j<m_points.length; j++){
				precission_OkHome[j]     = precission_OkHome[j]     + seqEv.getPrecisionAtPoint_OkHome(m_points[j]);
				recall_OkHome[j]         = recall_OkHome[j]         + seqEv.getRecallAtPoint_OkHome(m_points[j]);
				fmeasure_OkHome[j]       = fmeasure_OkHome[j]       + seqEv.getFmeasureAtPoint_OkHome(m_beta, m_points[j]);
				modelPrecision_OkHome[j] = modelPrecision_OkHome[j] + seqEv.getPrecisionModelAtPoint_OkHome(m_points[j]);
				modelRecall_OkHome[j]    = modelRecall_OkHome[j]    + seqEv.getRecallModelAtPoint_OkHome(m_points[j]);
				modelFmeasure_OkHome[j]  = modelFmeasure_OkHome[j]  + seqEv.getFmeasureModelAtPoint_OkHome(m_beta, m_points[j]);
			}
			
			// TOPIC level metrics - homepage always true
			hitratioTop_OkHome = hitratioTop_OkHome + seqEv.getHitRatioTop_OkHome();
			clicksoonratioTop_OkHome = clicksoonratioTop_OkHome + seqEv.getClickSoonRatioTop_OkHome();
			for(int j=0; j<m_points.length; j++){
				precissionTop_OkHome[j]     = precissionTop_OkHome[j]     + seqEv.getPrecisionTopAtPoint_OkHome(m_points[j]);
				recallTop_OkHome[j]         = recallTop_OkHome[j]         + seqEv.getRecallTopAtPoint_OkHome(m_points[j]);
				fmeasureTop_OkHome[j]       = fmeasureTop_OkHome[j]       + seqEv.getFmeasureTopAtPoint_OkHome(m_beta, m_points[j]);
				modelPrecisionTop_OkHome[j] = modelPrecisionTop_OkHome[j] + seqEv.getPrecisionModelTopAtPoint_OkHome(m_points[j]);
				modelRecallTop_OkHome[j]    = modelRecallTop_OkHome[j]    + seqEv.getRecallModelTopAtPoint_OkHome(m_points[j]);
				modelFmeasureTop_OkHome[j]  = modelFmeasureTop_OkHome[j]  + seqEv.getFmeasureModelTopAtPoint_OkHome(m_beta, m_points[j]);
			}
			
		}
		
		// Compute the average values
		m_numberOfRecommendationsRatio = numberOfRecommendationsRatio/(float)m_sequences.size();
		
		// Failure functions
		m_numberOfFailures = numberOfFailures;
		for(int j=0; j<failuresHist.length; j++){
			m_failuresHist[j] = (float)failuresHist[j] / (float)m_sequences.size();
		}
		
		// URL level metrics - HONEST
		m_hitratio = hitratio/(float)m_sequences.size();
		m_clicksoonratio = clicksoonratio/(float)m_sequences.size();
		for(int j=0; j<m_points.length; j++){
			m_precision[j]      = precission[j]     / (float)m_sequences.size();
			m_recall[j]         = recall[j]         / (float)m_sequences.size();
			m_fmeasure[j]       = fmeasure[j]       / (float)m_sequences.size();
			m_ModelPrecision[j] = modelPrecision[j] / (float)m_sequences.size();
			m_ModelRecall[j]    = modelRecall[j]    / (float)m_sequences.size();
			m_ModelFmeasure[j]  = modelFmeasure[j]  / (float)m_sequences.size();
		}
		m_cosineSimilarity = cosineSim/(float)m_sequences.size();
		
		// TOPIC1 level metrics - HONEST
		m_hitratioTop1 = hitratioTop1/(float)m_sequences.size();
		m_clicksoonratioTop1 = clicksoonratioTop1/(float)m_sequences.size();
		for(int j=0; j<m_points.length; j++){
			m_precisionTop1[j]      = precissionTop1[j]     / (float)m_sequences.size();
			m_recallTop1[j]         = recallTop1[j]         / (float)m_sequences.size();
			m_fmeasureTop1[j]       = fmeasureTop1[j]       / (float)m_sequences.size();
			m_ModelPrecisionTop1[j] = modelPrecisionTop1[j] / (float)m_sequences.size();
			m_ModelRecallTop1[j]    = modelRecallTop1[j]    / (float)m_sequences.size();
			m_ModelFmeasureTop1[j]  = modelFmeasureTop1[j]  / (float)m_sequences.size();
		}
		
		// TOPIC2 level metrics - HONEST
		m_hitratioTop2 = hitratioTop2/(float)m_sequences.size();
		m_clicksoonratioTop2 = clicksoonratioTop2/(float)m_sequences.size();
		for(int j=0; j<m_points.length; j++){
			m_precisionTop2[j]      = precissionTop2[j]     / (float)m_sequences.size();
			m_recallTop2[j]         = recallTop2[j]         / (float)m_sequences.size();
			m_fmeasureTop2[j]       = fmeasureTop2[j]       / (float)m_sequences.size();
			m_ModelPrecisionTop2[j] = modelPrecisionTop2[j] / (float)m_sequences.size();
			m_ModelRecallTop2[j]    = modelRecallTop2[j]    / (float)m_sequences.size();
			m_ModelFmeasureTop2[j]  = modelFmeasureTop2[j]  / (float)m_sequences.size();
		}
		
		// URL level metrics - homepage always true
		m_hitratio_OkHome = hitratio_OkHome/(float)m_sequences.size();
		m_clicksoonratio_OkHome = clicksoonratio_OkHome/(float)m_sequences.size();
		for(int j=0; j<m_points.length; j++){
			m_precision_OkHome[j]      = precission_OkHome[j]     / (float)m_sequences.size();
			m_recall_OkHome[j]         = recall_OkHome[j]         / (float)m_sequences.size();
			m_fmeasure_OkHome[j]       = fmeasure_OkHome[j]       / (float)m_sequences.size();
			m_ModelPrecision_OkHome[j] = modelPrecision_OkHome[j] / (float)m_sequences.size();
			m_ModelRecall_OkHome[j]    = modelRecall_OkHome[j]    / (float)m_sequences.size();
			m_ModelFmeasure_OkHome[j]  = modelFmeasure_OkHome[j]  / (float)m_sequences.size();
		}
		
		// TOPIC level metrics - homepage always true
		m_hitratioTop_OkHome = hitratioTop_OkHome/(float)m_sequences.size();
		m_clicksoonratioTop_OkHome = clicksoonratioTop_OkHome/(float)m_sequences.size();
		for(int j=0; j<m_points.length; j++){
			m_precisionTop_OkHome[j]      = precissionTop_OkHome[j]     / (float)m_sequences.size();
			m_recallTop_OkHome[j]         = recallTop_OkHome[j]         / (float)m_sequences.size();
			m_fmeasureTop_OkHome[j]       = fmeasureTop_OkHome[j]       / (float)m_sequences.size();
			m_ModelPrecisionTop_OkHome[j] = modelPrecisionTop_OkHome[j] / (float)m_sequences.size();
			m_ModelRecallTop_OkHome[j]    = modelRecallTop_OkHome[j]    / (float)m_sequences.size();
			m_ModelFmeasureTop_OkHome[j]  = modelFmeasureTop_OkHome[j]  / (float)m_sequences.size();
		}

	}
	
	
	
	// UTILITIES
	
	// SET class attributes
	
	public void setConfusionPoints(float[] confusionPoints){
		m_points = confusionPoints;
		this.constructor2();
	}
	public void setFmeasureBeta(float beta){
		m_beta = beta;
	}
	public void setTopicParameters(
			ArrayList<Integer> urlIds, 
			int[] url2topic, float topicmatch,
			HashMap<Integer,Integer> urlClusteringDict){
		m_urlIds = urlIds;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
		m_urlClusteringDict = urlClusteringDict;
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
	
	
	// URL level attributes - HONEST
	
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
	public float getCosineSimilarity(){
		return m_cosineSimilarity;
	}
	
	
	// TOPIC1 level attributes
	
	public float getHitRatioTop1(){
		return m_hitratioTop1;
	}
	public float getClickSoonRatioTop1(){
		return m_clicksoonratioTop1;
	}
	public float[] getPrecisionsTop1(){
		return m_precisionTop1;
	}
	public float[] getRecallsTop1(){
		return m_recallTop1;
	}
	public float[] getFmeasuresTop1(){
		return m_fmeasureTop1;
	}
	public float[] getModelPrecisionsTop1(){
		return m_ModelPrecisionTop1;
	}
	public float[] getModelRecallsTop1(){
		return m_ModelRecallTop1;
	}
	public float[] getModelFmeasuresTop1(){
		return m_ModelFmeasureTop1;
	}
	
	
	// TOPIC2 level attributes
	
	public float getHitRatioTop2(){
		return m_hitratioTop2;
	}
	public float getClickSoonRatioTop2(){
		return m_clicksoonratioTop2;
	}
	public float[] getPrecisionsTop2(){
		return m_precisionTop2;
	}
	public float[] getRecallsTop2(){
		return m_recallTop2;
	}
	public float[] getFmeasuresTop2(){
		return m_fmeasureTop2;
	}
	public float[] getModelPrecisionsTop2(){
		return m_ModelPrecisionTop2;
	}
	public float[] getModelRecallsTop2(){
		return m_ModelRecallTop2;
	}
	public float[] getModelFmeasuresTop2(){
		return m_ModelFmeasureTop2;
	}
	
	
	// URL level attributes - homepage always true
	
	public float getHitRatio_OkHome(){
		return m_hitratio_OkHome;
	}
	public float getClickSoonRatio_OkHome(){
		return m_clicksoonratio_OkHome;
	}
	public float[] getPrecisions_OkHome(){
		return m_precision_OkHome;
	}
	public float[] getRecalls_OkHome(){
		return m_recall_OkHome;
	}
	public float[] getFmeasures_OkHome(){
		return m_fmeasure_OkHome;
	}
	public float[] getModelPrecisions_OkHome(){
		return m_ModelPrecision_OkHome;
	}
	public float[] getModelRecalls_OkHome(){
		return m_ModelRecall_OkHome;
	}
	public float[] getModelFmeasures_OkHome(){
		return m_ModelFmeasure_OkHome;
	}
	
	
	// TOPIC level attributes - homepage always true
	
	public float getHitRatioTop_OkHome(){
		return m_hitratioTop_OkHome;
	}
	public float getClickSoonRatioTop_OkHome(){
		return m_clicksoonratioTop_OkHome;
	}
	public float[] getPrecisionsTop_OkHome(){
		return m_precisionTop_OkHome;
	}
	public float[] getRecallsTop_OkHome(){
		return m_recallTop_OkHome;
	}
	public float[] getFmeasuresTop_OkHome(){
		return m_fmeasureTop_OkHome;
	}
	public float[] getModelPrecisionsTop_OkHome(){
		return m_ModelPrecisionTop_OkHome;
	}
	public float[] getModelRecallsTop_OkHome(){
		return m_ModelRecallTop_OkHome;
	}
	public float[] getModelFmeasuresTop_OkHome(){
		return m_ModelFmeasureTop_OkHome;
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
