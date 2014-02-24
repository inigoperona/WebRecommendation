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
	
	public TestSetEvaluator(ArrayList<String[]> sequences, SuffixTreeStringArray suffixtree){
		m_sequences = sequences;
		m_gST = suffixtree;
		m_precision = new float[m_points.length];
		m_recall = new float[m_points.length];
		m_fmeasure = new float[m_points.length];
	}
	
	public void computeEvaluation(){
		float numberOfRecommendationsRatio = (float)0;
		float hitratio = (float)0;
		float clicksoonratio = (float)0;
		float[] precission = new float[m_points.length];
		float[] recall = new float[m_points.length];
		float[] fmeasure = new float[m_points.length];
		for(int i=0; i<m_sequences.size(); i++){
			String[] seq = m_sequences.get(i);
			SequenceEvaluator seqEv = new SequenceEvaluatorUCH(seq, m_gST);
			seqEv.computeSequenceMetrics();
			numberOfRecommendationsRatio = numberOfRecommendationsRatio + seqEv.getNumberOfRecommendationsRatio();
			hitratio = hitratio + seqEv.getHitRatio();
			clicksoonratio = clicksoonratio + seqEv.getClickSoonRatio();
			for(int j=0; j<m_points.length; j++){
				precission[j] = precission[j] + seqEv.getPrecisionAtPoint(m_points[j]);
				recall[j] = recall[j] + seqEv.getRecallAtPoint(m_points[j]);
				fmeasure[j] = fmeasure[j] + seqEv.getFmeasureAtPoint(m_beta, m_points[j]);
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
	
}
