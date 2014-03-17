package ehupatras.webrecommendation.evaluator;

import ehupatras.webrecommendation.recommender.*;
import ehupatras.clustering.sapehac.agglomeration.AgglomerationMethod;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import ehupatras.markovmodel.MarkovChain;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Random;

public class SequenceEvaluator {

	protected Recommender m_recommender = null;
	protected ArrayList<String> m_sequence = null;
	protected ArrayList<String> m_sequenceURL = null;
	
	protected int m_numberOfRecommendations = 0;
	private int m_nFailures = 0;
	private int m_hitscore = 0;
	private int m_clicksoonscore = 0;
	private float[] m_precision;
	private float[] m_recall;
	private float[] m_precisionModel;
	private float[] m_recallModel;
	
	public SequenceEvaluator(String[] sequence, SuffixTreeStringArray suffixtree){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructor(sequenceAL, suffixtree, 0);
	}

	public SequenceEvaluator(String[] sequence, 
				SuffixTreeStringArray suffixtree,
				int failuremode){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructor(sequenceAL, suffixtree, failuremode);
	}
	
	public SequenceEvaluator(ArrayList<String> sequence, SuffixTreeStringArray suffixtree){
		this.constructor(sequence, suffixtree, 0);
	}
	
	public SequenceEvaluator(ArrayList<String> sequence, 
			SuffixTreeStringArray suffixtree,
			int failuremode){
		this.constructor(sequence, suffixtree, failuremode);
	}
	
	private void constructor(ArrayList<String> sequence, 
					SuffixTreeStringArray suffixtree,
					int failuremode){
		m_recommender = new RecommenderSuffixTree(suffixtree, failuremode);
		m_sequence = sequence;
		m_sequenceURL = sequence;
		m_precision = new float[sequence.size()];
		m_recall = new float[sequence.size()];
		m_precisionModel = new float[sequence.size()];
		m_recallModel = new float[sequence.size()];
	}
	
	public SequenceEvaluator(String[] sequence, MarkovChain markovchain){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructor(sequenceAL, markovchain);
	}
	
	public SequenceEvaluator(ArrayList<String> sequence, MarkovChain markovchain){
		this.constructor(sequence, markovchain);
	}
	
	private void constructor(ArrayList<String> sequence, MarkovChain markovchain){
		m_recommender = new RecommenderMarkovChain(markovchain);
		m_sequence = sequence;
		m_sequenceURL = sequence;
		m_precision = new float[sequence.size()];
		m_recall = new float[sequence.size()];
		m_precisionModel = new float[sequence.size()];
		m_recallModel = new float[sequence.size()];
	}
	
	private ArrayList<String> convertToArrayList(String[] strA){
		ArrayList<String> sequenceAL = new ArrayList<String>();
		for(int i=0; i<strA.length; i++){
			sequenceAL.add(strA[i]);
		}
		return sequenceAL;
	}
	
	// mode = -1 : Unbounded
	// mode =  0 : Random, fixed number of requests
	// mode =  1 : Weighted the suffix tree only by weighted training sequences.
	// mode =  2 : Weighted the suffix tree only by original testing sequences (in exploitation).
	// mode =  3 : Weighted the suffix tree by weighted training sequences and original test sequences.
	// mode =  4 : Merge Markov Chain model and Suffix Tree models recommendations
	// mode =  5 : Weight the Suffix tree with original train sequences and in exploitation with original test sequences
	public void computeSequenceMetrics(int mode, int nrecos, long seed, MarkovChain markovchain){
		m_recommender.reset();
		Recommender recM = null;
		ArrayList<String> waydone = new ArrayList<String>();
		ArrayList<String> list = null;
		if(mode==-1){ // Unbounded
			list = m_recommender.getNextpossibleStepsUnbounded();
		} else if(mode==0){ // Random
			list = m_recommender.getNextpossibleStepsRandom(nrecos, seed);
		} else if(mode==1){ // Weighted Suffix Tree with Train-Clusters-WeightedSequences
			list = m_recommender.getNextpossibleStepsWeightedTrain(nrecos, waydone);
		} else if(mode==2){
			list = m_recommender.getNextpossibleStepsWeightedTest(nrecos);
		} else if(mode==3){
			list = m_recommender.getNextpossibleStepsWeighted(nrecos, waydone);
		} else if(mode==4){
			recM = new RecommenderMarkovChain(markovchain);
			ArrayList<String> listMarkov = recM.getNextpossibleStepsWeightedTest(nrecos);
			list = m_recommender.getNextpossibleStepsMarkov(nrecos, waydone, listMarkov);
		} else if(mode==5){
			list = m_recommender.getNextpossibleStepsWeightedByOriginalSequences(nrecos);
		}
		for(int i=0; i<m_sequence.size(); i++){
			String step = m_sequence.get(i);
			this.computeStepMetrics(i, list);
			
			// do the step and get the next recommendations
			waydone = m_recommender.update(waydone, step, true, true);
			if(mode==-1){ // BASELINE
				list = m_recommender.getNextpossibleStepsUnbounded();
			} else if(mode==0){ // BASELINE
				list = m_recommender.getNextpossibleStepsRandom(nrecos, seed);
			} else if(mode==1){
				list = m_recommender.getNextpossibleStepsWeightedTrain(nrecos, waydone);
			} else if(mode==2){
				list = m_recommender.getNextpossibleStepsWeightedTest(nrecos);
			} else if(mode==3){ // OUR PROPOSED METHOD
				list = m_recommender.getNextpossibleStepsWeighted(nrecos, waydone);
			} else if(mode==4){
				recM.update(null, step, false, false);
				ArrayList<String> listMarkov = recM.getNextpossibleStepsWeightedTest(nrecos);
				list = m_recommender.getNextpossibleStepsMarkov(nrecos, waydone, listMarkov);
			} else if(mode==5){
				list = m_recommender.getNextpossibleStepsWeightedByOriginalSequences(nrecos);
			}
		}
		m_nFailures = m_recommender.getNumberOfFailures();
	}
	
	protected void computeStepMetrics(int stepIndex, ArrayList<String> recommendatios){		
		String step = m_sequenceURL.get(stepIndex);
		m_numberOfRecommendations = m_numberOfRecommendations + recommendatios.size();
		this.computeHitScore(step, recommendatios);
		this.computeClickSoonScore(stepIndex, recommendatios);
		this.computeConfusionMatrix(stepIndex, recommendatios);
	}
	
	protected void computeHitScore(String step, ArrayList<String> recommendatios){
		for(int i=0; i<recommendatios.size(); i++){
			if(step.equals(recommendatios.get(i))){
				m_hitscore++;
				break;
			}
		}
	}
	
	protected void computeClickSoonScore(int stepIndex, ArrayList<String> recommendatios){
		boolean itWasUsed = false;
		for(int i=0; i<recommendatios.size(); i++){
			String onereco = recommendatios.get(i);
			for(int j=stepIndex; j<m_sequenceURL.size(); j++){
				String realstep = m_sequenceURL.get(j);
				if(onereco.equals(realstep)){
					m_clicksoonscore++;
					itWasUsed = true;
					break;
				}
			}
			if(itWasUsed){break;}
		}
	}
	
	protected void computeConfusionMatrix(int stepIndex, ArrayList<String> recommendatios){
		float pr = this.computePrecision(stepIndex, recommendatios);
		float re = this.computeRecall(stepIndex, recommendatios);
		m_precision[stepIndex] = pr;
		m_recall[stepIndex] = re;
		float prModel = this.computePrecision(0, recommendatios);
		float reModel = this.computeRecall(0, recommendatios);
		m_precisionModel[stepIndex] = prModel;
		m_recallModel[stepIndex] = reModel;
	}
	
	private float computePrecision(int stepIndex, ArrayList<String> recommendatios){
		int prTP = 0;
		int prFP = 0;
		// compute precision related variables
		for(int i=0; i<recommendatios.size(); i++){
			boolean itWasUsed = false;
			String onereco = recommendatios.get(i);
			for(int j=stepIndex; j<m_sequenceURL.size(); j++){
				String realstep = m_sequenceURL.get(j);
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
		
		if(prTP==(float)0 && prFP==(float)0){
			return (float)0;
		} else {
			return (float)prTP/((float)prTP+(float)prFP);
		}
	}
	
	private float computeRecall(int stepIndex, ArrayList<String> recommendatios){
		int reTP = 0;
		int reFN = 0;
		// compute precision related variables
		for(int i=stepIndex; i<m_sequenceURL.size(); i++){
			String realstep = m_sequenceURL.get(i);
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
		
		if(reTP==(float)0 && reFN==(float)0){
			return (float)0;
		} else {
			return (float)reTP/((float)reTP+(float)reFN);
		}
	}
	
	public float getNumberOfRecommendationsRatio(){
		return (float)m_numberOfRecommendations / (float)m_sequence.size();
	}
	
	public int getNumberOfFailures(){
		return m_nFailures;
	}
	
	public float getHitRatio(){
		return (float)m_hitscore/(float)m_sequence.size();
	}
	
	public float getClickSoonRatio(){
		return (float)m_clicksoonscore/(float)m_sequence.size();
	}
	
	public float[] getPrecissions(){
		return m_precision;
	}
	
	public float[] getRecalls(){
		return m_recall;
	}
	
	public float[] getPrecissionsModel(){
		return m_precisionModel;
	}
	
	public float[] getRecallsModel(){
		return m_recallModel;
	}
	
	public float[] getFmeasures(float beta){
		float[] fmeasure = new float[m_precision.length];
		for(int i=0; i<m_precision.length; i++){
			float b2 = (float)Math.pow(beta, 2);
			float pr = m_precision[i];
			float re = m_recall[i];
			if(pr==(float)0 && re==(float)0){
				fmeasure[i] = (float)0;
			} else {
				fmeasure[i] = ((float)1+b2) * (pr*re) / ((b2*pr)+re);
			}
		}
		return fmeasure;
	}
	
	public float[] getFmeasuresModel(float beta){
		float[] fmeasure = new float[m_precisionModel.length];
		for(int i=0; i<m_precisionModel.length; i++){
			float b2 = (float)Math.pow(beta, 2);
			float pr = m_precisionModel[i];
			float re = m_recallModel[i];
			if(pr==(float)0 && re==(float)0){
				fmeasure[i] = (float)0;
			} else {
				fmeasure[i] = ((float)1+b2) * (pr*re) / ((b2*pr)+re);
			}
		}
		return fmeasure;
	}
	
	protected void printPrecision(){
		System.out.print("Precision: ");
		for(int i=0; i<m_precision.length; i++){
			System.out.print(m_precision[i] + "; ");
		}
		System.out.println();
	}
	
	protected void printRecall(){
		System.out.print("Recall: ");
		for(int i=0; i<m_recall.length; i++){
			System.out.print(m_recall[i] + "; ");
		}
		System.out.println();
	}
	
	protected void printFmeasure(float beta){
		float fmeasure[] = this.getFmeasures(beta);
		System.out.print("F" + beta + "-measure: ");
		for(int i=0; i<fmeasure.length; i++){
			System.out.print(fmeasure[i] + "; ");
		}
		System.out.println();
	}
	
	public float getPrecisionAtPoint(float point){
		int index = this.getPosition(point);
		return m_precision[index];
	}
	
	public float getRecallAtPoint(float point){
		int index = this.getPosition(point);
		return m_recall[index];
	}
	
	public float getFmeasureAtPoint(float beta, float point){
		float[] fmeasure = this.getFmeasures(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	public float getPrecisionModelAtPoint(float point){
		int index = this.getPosition(point);
		return m_precisionModel[index];
	}
	
	public float getRecallModelAtPoint(float point){
		int index = this.getPosition(point);
		return m_recallModel[index];
	}
	
	public float getFmeasureModelAtPoint(float beta, float point){
		float[] fmeasure = this.getFmeasuresModel(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	private int getPosition(float point){
		float len = (float)m_precision.length - (float)1;
		float index = len * point;
		return (int)Math.round(index);
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
        
        // sequence to test
        ArrayList<String> seq = new ArrayList<String>();
        seq.add("a");
        seq.add("b");
        seq.add("c");
        
        // get the metrics
        SequenceEvaluator se = new SequenceEvaluator(seq, st);
        se.computeSequenceMetrics(-1, -1, (long)0, null);
        System.out.println("HR: " + se.getHitRatio());
        System.out.println("CR: " + se.getClickSoonRatio());
        System.out.println("---");
        se.printPrecision();
        se.printRecall();
        se.printFmeasure((float)1);
        se.printFmeasure((float)0.5);
        System.out.println("---");
        System.out.println("Pr0.00: " + se.getPrecisionAtPoint((float)0));
        System.out.println("Pr0.10: " + se.getPrecisionAtPoint((float)0.10));
        System.out.println("Pr0.25: " + se.getPrecisionAtPoint((float)0.25));
        System.out.println("Pr0.50: " + se.getPrecisionAtPoint((float)0.50));
        System.out.println("Pr0.75: " + se.getPrecisionAtPoint((float)0.75));
        System.out.println("Pr0.90: " + se.getPrecisionAtPoint((float)0.90));
        System.out.println("Pr1.00: " + se.getPrecisionAtPoint((float)1.00));
        System.out.println("---");
        System.out.println("Re0.00: " + se.getRecallAtPoint((float)0));
        System.out.println("Re0.10: " + se.getRecallAtPoint((float)0.10));
        System.out.println("Re0.25: " + se.getRecallAtPoint((float)0.25));
        System.out.println("Re0.50: " + se.getRecallAtPoint((float)0.50));
        System.out.println("Re0.75: " + se.getRecallAtPoint((float)0.75));
        System.out.println("Re0.90: " + se.getRecallAtPoint((float)0.90));
        System.out.println("Re1.00: " + se.getRecallAtPoint((float)1.00));
        System.out.println("---");
        System.out.println("Fm0.00: " + se.getFmeasureAtPoint((float)0.5,(float)0));
        System.out.println("Fm0.10: " + se.getFmeasureAtPoint((float)0.5,(float)0.10));
        System.out.println("Fm0.25: " + se.getFmeasureAtPoint((float)0.5,(float)0.25));
        System.out.println("Fm0.50: " + se.getFmeasureAtPoint((float)0.5,(float)0.50));
        System.out.println("Fm0.75: " + se.getFmeasureAtPoint((float)0.5,(float)0.75));
        System.out.println("Fm0.90: " + se.getFmeasureAtPoint((float)0.5,(float)0.90));
        System.out.println("Fm1.00: " + se.getFmeasureAtPoint((float)0.5,(float)1.00));
	}
	
}
