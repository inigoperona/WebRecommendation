package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;

public class TestSetEvaluator {

	private ArrayList<String[]> m_sequences;
	private SuffixTreeStringArray m_gST;
	
	private float[] m_points = {(float)0.0, (float)0.10, (float)0.25, 
			(float)0.50, (float)0.75, (float)0.90, (float)1.00};
	private float m_beta = (float)0.5;
	
	private float m_numberOfRecommendationsRatio = (float)0;
	private float m_hitratio = (float)0;
	private float m_clicksoonratio = (float)0;
	private float[] m_precision;
	private float[] m_recall;
	private float[] m_fmeasure;
	private float[] m_ModelPrecision;
	private float[] m_ModelRecall;
	private float[] m_ModelFmeasure;
	
	public TestSetEvaluator(ArrayList<String[]> sequences, SuffixTreeStringArray suffixtree){
		m_sequences = sequences;
		m_gST = suffixtree;
		m_precision = new float[m_points.length];
		m_recall = new float[m_points.length];
		m_fmeasure = new float[m_points.length];
		m_ModelPrecision = new float[m_points.length];
		m_ModelRecall = new float[m_points.length];
		m_ModelFmeasure = new float[m_points.length];
	}
	
	public void computeEvaluation(int mode, int nrecos, long seed){
		float numberOfRecommendationsRatio = (float)0;
		float hitratio = (float)0;
		float clicksoonratio = (float)0;
		float[] precission = new float[m_points.length];
		float[] recall = new float[m_points.length];
		float[] fmeasure = new float[m_points.length];
		float[] modelPrecision = new float[m_points.length];
		float[] modelRecall = new float[m_points.length];
		float[] modelFmeasure = new float[m_points.length];
		for(int i=0; i<m_sequences.size(); i++){
			String[] seq = m_sequences.get(i);
			SequenceEvaluator seqEv = new SequenceEvaluator(seq, m_gST);
			seqEv.computeSequenceMetrics(mode, nrecos, seed);
			numberOfRecommendationsRatio = numberOfRecommendationsRatio + seqEv.getNumberOfRecommendationsRatio();
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
		}
		
		// compute the average values
		m_numberOfRecommendationsRatio = numberOfRecommendationsRatio/(float)m_sequences.size();
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
	}
	
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
		
		// wirte results
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
	
	public void setConfusionPoints(float[] confusionPoints){
		m_points = confusionPoints;
	}
	public void setFmeasureBeta(float beta){
		m_beta = beta;
	}
	
	
	public float getNumberOfRecommendationsRatio(){
		return m_numberOfRecommendationsRatio;
	}
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
}