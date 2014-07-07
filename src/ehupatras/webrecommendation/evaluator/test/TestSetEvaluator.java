package ehupatras.webrecommendation.evaluator.test;

import java.io.BufferedWriter;
import java.util.ArrayList;

public class TestSetEvaluator {

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
	
	public TestSetEvaluator(ArrayList<String[]> sequences){
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
	
	
	
	// UTILITIES
	
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
