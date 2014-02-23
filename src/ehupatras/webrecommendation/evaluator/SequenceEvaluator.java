package ehupatras.webrecommendation.evaluator;

import ehupatras.webrecommendation.recommender.*;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import java.util.ArrayList;

public class SequenceEvaluator {

	private Recommender m_recommender = null;
	private ArrayList<String> m_sequence = null;
	
	private int m_hitscore = 0;
	private int m_clicksoonscore = 0;
	private float[] m_precision;
	private float[] m_recall;
	
	public SequenceEvaluator(ArrayList<String> sequence, SuffixTreeStringArray  suffixtree){
		m_recommender = new Recommender(suffixtree);
		m_sequence = sequence;
		m_precision = new float[sequence.size()];
		m_recall = new float[sequence.size()];
		this.computeSequenceMetrics();
	}
	
	private void computeSequenceMetrics(){
		m_recommender.gotoroot();
		ArrayList<String> list = m_recommender.getNextpossibleSteps();
		for(int i=0; i<m_sequence.size(); i++){
			String step = m_sequence.get(i);
			this.computeStepMetrics(i, list);
			list = m_recommender.moveAndGetRecommendations(step);
		}
	}
	
	private void computeStepMetrics(int stepIndex, ArrayList<String> recommendatios){		
		String step = m_sequence.get(stepIndex);
		this.computeHitScore(step, recommendatios);
		this.computeClickSoonScore(stepIndex, recommendatios);
		this.computeConfusionMatrix(stepIndex, recommendatios);
	}
	
	private void computeHitScore(String step, ArrayList<String> recommendatios){
		for(int i=0; i<recommendatios.size(); i++){
			if(step.equals(recommendatios.get(i))){
				m_hitscore++;
				break;
			}
		}
	}
	
	private void computeClickSoonScore(int stepIndex, ArrayList<String> recommendatios){
		boolean itWasUsed = false;
		for(int i=0; i<recommendatios.size(); i++){
			String onereco = recommendatios.get(i);
			for(int j=stepIndex; j<m_sequence.size(); j++){
				String realstep = m_sequence.get(j);
				if(onereco.equals(realstep)){
					m_clicksoonscore++;
					itWasUsed = true;
					break;
				}
			}
			if(itWasUsed){break;}
		}
	}
	
	private void computeConfusionMatrix(int stepIndex, ArrayList<String> recommendatios){
		this.computePrecision(stepIndex, recommendatios);
		this.computeRecall(stepIndex, recommendatios);
	}
	
	private void computePrecision(int stepIndex, ArrayList<String> recommendatios){
		int prTP = 0;
		int prFP = 0;
		// compute precision related variables
		for(int i=0; i<recommendatios.size(); i++){
			boolean itWasUsed = false;
			String onereco = recommendatios.get(i);
			for(int j=stepIndex; j<m_sequence.size(); j++){
				String realstep = m_sequence.get(j);
				if(onereco.equals(realstep)){
					itWasUsed = true;
					break;
				}
			}
			if(itWasUsed){
				prTP++;
			} else {
				prFP++;
			}
		}
		m_precision[stepIndex] = (float)prTP/((float)prTP+(float)prFP);
	}
	
	private void computeRecall(int stepIndex, ArrayList<String> recommendatios){
		int reTP = 0;
		int reFN = 0;
		// compute precision related variables
		for(int i=stepIndex; i<m_sequence.size(); i++){
			String realstep = m_sequence.get(i);
			boolean itWasUsed = false;
			for(int j=0; j<recommendatios.size(); j++){
				String onereco = recommendatios.get(j);				
				if(realstep.equals(onereco)){
					itWasUsed = true;
					break;
				}
			}
			if(itWasUsed){
				reTP++;
			} else {
				reFN++;
			}
		}
		m_recall[stepIndex] = (float)reTP/((float)reTP+(float)reFN);
	}
	
	public float getHitRatio(){
		return (float)m_hitscore/(float)m_sequence.size();
	}
	
	public float getClickSoonRatio(){
		return (float)m_clicksoonscore/(float)m_sequence.size();
	}
	
	private void printPrecision(){
		System.out.print("Precision: ");
		for(int i=0; i<m_precision.length; i++){
			System.out.print(m_precision[i] + "; ");
		}
		System.out.println();
	}
	
	private void printRecall(){
		System.out.print("Recall: ");
		for(int i=0; i<m_recall.length; i++){
			System.out.print(m_recall[i] + "; ");
		}
		System.out.println();
	}
	
	public static void main(String[] args){
		// create the suffix tree
		SuffixTreeStringArray st = new SuffixTreeStringArray();
        String[] word1 = {"c", "a", "c", "a", "o"};
        String[] word2 = {"b", "a", "n", "a", "n", "a"};
        String[] word3 = {"m", "i", "l", "o"};
        String[] word4 = {"c", "a", "r"};
        st.putSequence(word1, 0);
        st.putSequence(word2, 1);
        st.putSequence(word3, 2);
        st.putSequence(word4, 3);
        st.printSuffixTree();
        
        // recommendations
        ArrayList<String> seq = new ArrayList<String>();
        seq.add("a");
        seq.add("b");
        seq.add("c");
        SequenceEvaluator se = new SequenceEvaluator(seq, st);
        System.out.println("HR: " + se.getHitRatio());
        System.out.println("CR: " + se.getClickSoonRatio());
        se.printPrecision();
        se.printRecall();
	}
	
}
