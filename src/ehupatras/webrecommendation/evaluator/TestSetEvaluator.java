package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;

import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;

public class TestSetEvaluator {

	private ArrayList<String[]> m_sequences;
	private SuffixTreeStringArray m_gST;
	
	private float[] m_points = {(float)0.0, (float)0.10, (float)0.25, 
			(float)0.50, (float)0.75, (float)0.90, (float)1.00};
	private float m_beta = (float)0.5;
	
	private float m_hitratio;
	private float m_clicksoonratio;
	private float[] m_precission;
	private float[] m_recall;
	private float[] m_fmeasure;
	
	public TestSetEvaluator(ArrayList<String[]> sequences, SuffixTreeStringArray suffixtree){
		m_sequences = sequences;
		m_gST = suffixtree;
	}
	
	public void computeEvaluation(){
		float hitratio = (float)0;
		float clicksoonratio = (float)0;
		float[] precission = new float[m_points.length];
		float[] recall = new float[m_points.length];
		float[] fmeasure = new float[m_points.length];
		for(int i=0; i<m_sequences.size(); i++){
			String[] seq = m_sequences.get(i);
			SequenceEvaluator seqEv = new SequenceEvaluator(seq, m_gST);
			hitratio = hitratio + seqEv.getHitRatio();
			clicksoonratio = clicksoonratio + seqEv.getClickSoonRatio();
			for(int j=0; j<m_points.length; j++){
				precission[j] = precission[j] + seqEv.getPrecisionAtPoint(m_points[j]);
				recall[j] = recall[j] + seqEv.getRecallAtPoint(m_points[j]);
				fmeasure[j] = fmeasure[j] + seqEv.getFmeasureAtPoint(m_beta, m_points[j]);
			}
		}
		m_hitratio = hitratio/(float)m_sequences.size();
		m_clicksoonratio = clicksoonratio/(float)m_sequences.size();
		for(int j=0; j<m_points.length; j++){
			m_precission[j] = precission[j]/(float)m_sequences.size();
			m_recall[j] = recall[j]/(float)m_sequences.size();
			m_fmeasure[j] = fmeasure[j]/(float)m_sequences.size();
		}
	}
	
}
