package ehupatras.webrecommendation.evaluator;

import ehupatras.webrecommendation.recommender.*;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.markovmodel.MarkovChain;
import ehupatras.markovmodel.hmm.HiddenMarkovModel;
import java.util.ArrayList;

public class SequenceEvaluator {

	// Classes that simulates the navigation of the user
	protected Recommender m_recommender = null;
	protected ArrayList<String> m_sequence = null;
	protected ArrayList<String> m_sequenceURL = null;
	
	// Metrics
	protected int m_numberOfRecommendations = 0;
	private int m_nFailures = 0;
	
	// URL level metrics
	private int m_hitscore = 0;
	private int m_clicksoonscore = 0;
	private float[] m_precision;
	private float[] m_recall;
	private float[] m_precisionModel;
	private float[] m_recallModel;
	
	// TOPIC level metrics
	private ArrayList<Integer> m_urlIds = null;
	private int[] m_url2topic = null;
	private float m_topicmatch = 0.5f;
	private float m_hitscoreTop = 0;
	private float m_clicksoonscoreTop = 0;
	private float[] m_precisionTop;
	private float[] m_recallTop;
	private float[] m_precisionModelTop;
	private float[] m_recallModelTop;
	
	
	
	// CREATOR FUNCTION FOR SUFFIX TREES //
	
	public SequenceEvaluator(
				String[] sequence,
				MySuffixTree suffixtree){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructorST(sequenceAL, suffixtree, 0);
	}

	public SequenceEvaluator(
				String[] sequence, 
				MySuffixTree suffixtree,
				int failuremode){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructorST(sequenceAL, suffixtree, failuremode);
	}

	public SequenceEvaluator(
				String[] sequence, 
				MySuffixTree suffixtree,
				int failuremode,
				int maxMemory,
				int normMode){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructorST(sequenceAL, suffixtree, failuremode, maxMemory, normMode);
	}

	public SequenceEvaluator(
				ArrayList<String> sequence, 
				MySuffixTree suffixtree){
		this.constructorST(sequence, suffixtree, 0);
	}
	
	public SequenceEvaluator(ArrayList<String> sequence, 
				MySuffixTree suffixtree,
				int failuremode){
		this.constructorST(sequence, suffixtree, failuremode);
	}
	
	public SequenceEvaluator(ArrayList<String> sequence, 
				MySuffixTree suffixtree,
				int failuremode,
				int maxMemory,
				int normMode){
		this.constructorST(sequence, suffixtree, failuremode, maxMemory, normMode);
	}
	
	private void constructorST(ArrayList<String> sequence, 
					MySuffixTree suffixtree,
					int failuremode){
		m_recommender = new RecommenderSuffixTree2(suffixtree, failuremode);
		this.constructor2(sequence);
	}
	
	private void constructorST(ArrayList<String> sequence, 
					MySuffixTree suffixtree,
					int failuremode,
					int maxMemory,
					int normMode){
		m_recommender = new RecommenderSuffixTree2(suffixtree, failuremode, maxMemory, normMode);
		this.constructor2(sequence);
	}
	
	
	
	// CREATOR FUNCTION FOR MARKOV CHAIN //
	
	public SequenceEvaluator(String[] sequence, 
				MarkovChain markovchain){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructorMC(sequenceAL, markovchain);
	}
	
	public SequenceEvaluator(ArrayList<String> sequence,
				MarkovChain markovchain){
		m_recommender = new RecommenderMarkovChain(markovchain);
		this.constructor2(sequence);
	}
	
	private void constructorMC(ArrayList<String> sequence,
					MarkovChain markovchain){
		m_recommender = new RecommenderMarkovChain(markovchain);
		this.constructor2(sequence);
	}
	
	
	
	// CREATOR FUNCTION FOR LIST OF RECOMMENDATIONS FOR EACH CLUSTER //
	
	public SequenceEvaluator(String[] sequence, 
				ArrayList<String[]> medoids,
				int[] gmedoids,
				ArrayList<Object[]> medoidsRecos,
				boolean isDistance,
				float[][] rolesW){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructorClRecos(sequenceAL, medoids, gmedoids, medoidsRecos,
				isDistance, rolesW);
	}
	
	public SequenceEvaluator(ArrayList<String> sequence, 
				ArrayList<String[]> medoids,
				int[] gmedoids,
				ArrayList<Object[]> medoidsRecos,
				boolean isDistance,
				float[][] rolesW){
		this.constructorClRecos(sequence, medoids, gmedoids, medoidsRecos,
				isDistance, rolesW);
	}
	
	private void constructorClRecos(ArrayList<String> sequence,
						ArrayList<String[]> medoids,
						int[] gMedoids,
						ArrayList<Object[]> medoidsRecos,
						boolean isDistance,
						float[][] rolesW){
		m_recommender = new RecommenderKnnToClustersTopURLs(medoids, gMedoids, medoidsRecos,
				isDistance, rolesW);
		this.constructor2(sequence);
	}
	
	
	
	// MODULAR APPROACH: CREATOR FUNCTION FOR STs FOR EACH CLUSTER //
	
	public SequenceEvaluator(
				String[] sequence, 
				ArrayList<MySuffixTree> suffixtreeAL){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructorST(sequenceAL, suffixtreeAL);
	}

	private void constructorST(ArrayList<String> sequence, 
				ArrayList<MySuffixTree> suffixtreeAL){
		m_recommender = new RecommenderClustersSuffixTree2(suffixtreeAL);
		this.constructor2(sequence);
	}
	
	
	
	// MODULAR APPROACH: K-NN TO THE NEAREST CLUSTER-ST //
	
	public SequenceEvaluator(String[] sequence, 
				ArrayList<String[]> medoids,
				int[] gMedoids,
				int k,
				boolean isDistance,
				float[][] rolesW,
				ArrayList<MySuffixTree> suffixtreeAL){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructorKnnST(sequenceAL, medoids, gMedoids, k, isDistance, rolesW, suffixtreeAL);
	}

	private void constructorKnnST(ArrayList<String> sequence, 
					ArrayList<String[]> medoids,
					int[] gMedoids,
					int k,
					boolean isDistance,
					float[][] rolesW,
					ArrayList<MySuffixTree> suffixtreeAL){
		m_recommender = new RecommenderKnnToSuffixTrees2(medoids, gMedoids, suffixtreeAL, isDistance, rolesW, k);
		this.constructor2(sequence);
	}
	
	
	
	// CREATOR clust+HMM //
	
	public SequenceEvaluator(String[] sequence, 
			HiddenMarkovModel hmm,
			int nNextSteps){
		m_recommender = new RecommenderHMM(hmm, nNextSteps);
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructor2(sequenceAL);
	}
	
	public SequenceEvaluator(ArrayList<String> sequence, 
			HiddenMarkovModel hmm,
			int nNextSteps){
		m_recommender = new RecommenderHMM(hmm, nNextSteps);
		this.constructor2(sequence);
	}
	
	
	
	
	// UTILS //
	
	private void constructor2(ArrayList<String> sequence){
		m_sequence = sequence;
		m_sequenceURL = sequence;
		
		m_precision = new float[sequence.size()];
		m_recall = new float[sequence.size()];
		m_precisionModel = new float[sequence.size()];
		m_recallModel = new float[sequence.size()];
		
		m_precisionTop = new float[sequence.size()];
		m_recallTop = new float[sequence.size()];
		m_precisionModelTop = new float[sequence.size()];
		m_recallModelTop = new float[sequence.size()];
	}
	
	private ArrayList<String> convertToArrayList(String[] strA){
		ArrayList<String> sequenceAL = new ArrayList<String>();
		for(int i=0; i<strA.length; i++){
			sequenceAL.add(strA[i]);
		}
		return sequenceAL;
	}
	
	
	
	// BODY //
	
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
		} else if(mode==6){
			list = m_recommender.getNextpossibleStepsWeightedEnrichWithStep1(nrecos, waydone);
		}
		for(int i=0; i<m_sequence.size(); i++){
			// update the prediction indicators
			this.computeStepMetrics(i, list);
			
			// do the step
			String nextstep = m_sequence.get(i);
			waydone = m_recommender.update(waydone, nextstep, true, true);
			
			// get the next recommendations
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
				recM.update(null, nextstep, false, false);
				ArrayList<String> listMarkov = recM.getNextpossibleStepsWeightedTest(nrecos);
				list = m_recommender.getNextpossibleStepsMarkov(nrecos, waydone, listMarkov);
			} else if(mode==5){
				list = m_recommender.getNextpossibleStepsWeightedByOriginalSequences(nrecos);
			} else if(mode==6){
				list = m_recommender.getNextpossibleStepsWeightedEnrichWithStep1(nrecos, waydone);
			}
		}
		m_nFailures = m_recommender.getNumberOfFailures();
	}
	
	
	
	// Main function to measure the prediction ability
	
	protected void computeStepMetrics(int stepIndex, ArrayList<String> recommendatios){		
		String step = m_sequenceURL.get(stepIndex);
		m_numberOfRecommendations = m_numberOfRecommendations + recommendatios.size();
		
		// URL level
		this.computeHitScore(step, recommendatios);
		this.computeClickSoonScore(stepIndex, recommendatios);
		this.computeConfusionMatrix(stepIndex, recommendatios);
		
		// TOPIC level
		this.computeHitScoreTop(step, recommendatios);
		this.computeClickSoonScoreTop(stepIndex, recommendatios);
		this.computeConfusionMatrixTop(stepIndex, recommendatios);
	}
	
	
	
	// URL level functions
	
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
	
	
	
	// TOPIC level functions
	
	protected void computeHitScoreTop(String step, ArrayList<String> recommendatios){
		// for each recommendations
		boolean hitURL = false;
		boolean hitTopic = false;
		for(int i=0; i<recommendatios.size(); i++){
			String rec = recommendatios.get(i);
			int stepInt = Integer.valueOf(step);
			int recInt = Integer.valueOf(rec);
			int stepInt2 = m_urlIds.indexOf(stepInt);
			int recInt2 = m_urlIds.indexOf(recInt);
			int stepTop = m_url2topic[stepInt2];
			int recTop = m_url2topic[recInt2];
			
			if(stepInt == recInt){
				hitURL = true;
				break;
			} else{
				if(stepTop!=-1 && recTop!=-1 && 
						stepTop == recTop){
					hitTopic = true;
				}
			}
		}
		
		// update the m_hitscoreTop
		if(hitURL){
			m_hitscoreTop = m_hitscoreTop + 1f;
		} else if(hitTopic){
			m_hitscoreTop = m_hitscoreTop + m_topicmatch;
		}
	}
	
	protected void computeClickSoonScoreTop(int stepIndex, ArrayList<String> recommendatios){
		boolean hitURL = false;
		boolean hitTopic = false;		
		for(int i=0; i<recommendatios.size(); i++){
			String onereco = recommendatios.get(i);
			int onerecoInt = Integer.valueOf(onereco);
			int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
			int onerecoTop =  m_url2topic[onerecoInt2];
			
			for(int j=stepIndex; j<m_sequenceURL.size(); j++){
				String realstep = m_sequenceURL.get(j);
				int realstepInt = Integer.valueOf(realstep);
				int realstepInt2 = m_urlIds.indexOf(realstepInt);
				int realstepTop = m_url2topic[realstepInt2];
				
				if(onerecoInt == realstepInt){
					hitURL = true;
					break;
				} else {
					if(onerecoTop!=-1 && realstepTop!=-1 && 
							onerecoTop == realstepTop){
						hitTopic = true;
					}
				}
			}
			if(hitURL){break;}
		}
		
		// update the m_clicksoonscoreTop
		if(hitURL){
			m_clicksoonscoreTop = m_clicksoonscoreTop + 1f;
		} else if(hitTopic){
			m_clicksoonscoreTop = m_clicksoonscoreTop + m_topicmatch;
		}
	}
	
	protected void computeConfusionMatrixTop(int stepIndex, ArrayList<String> recommendatios){
		float pr = this.computePrecisionTop(stepIndex, recommendatios);
		float re = this.computeRecallTop(stepIndex, recommendatios);
		m_precisionTop[stepIndex] = pr;
		m_recallTop[stepIndex] = re;
		float prModel = this.computePrecisionTop(0, recommendatios);
		float reModel = this.computeRecallTop(0, recommendatios);
		m_precisionModelTop[stepIndex] = prModel;
		m_recallModelTop[stepIndex] = reModel;
	}
	
	private float computePrecisionTop(int stepIndex, ArrayList<String> recommendatios){
		float prTP = 0;
		float prFP = 0;
		
		// compute precision related variables
		for(int i=0; i<recommendatios.size(); i++){
			boolean hitURL = false;
			boolean hitTopic = false;
			
			String onereco = recommendatios.get(i);
			int onerecoInt = Integer.valueOf(onereco);
			int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
			int onerecoTop =  m_url2topic[onerecoInt2];
			
			for(int j=stepIndex; j<m_sequenceURL.size(); j++){
				String realstep = m_sequenceURL.get(j);
				int realstepInt = Integer.valueOf(realstep);
				int realstepInt2 = m_urlIds.indexOf(realstepInt);
				int realstepTop = m_url2topic[realstepInt2];
				
				if(onerecoInt == realstepInt){
					hitURL = true;
					break;
				} else {
					if(onerecoTop!=-1 && realstepTop!=-1 &&
							onerecoTop == realstepTop){
						hitTopic = true;
					}
				}
			}
			
			if(hitURL){
				prTP = prTP + 1f;
			} else if(hitTopic){				
				prTP = prTP + m_topicmatch;
			} else {
				prFP = prFP + 1f;
			}
		}
		
		if(prTP==0f && prFP==0f){
			return 0f;
		} else {
			return prTP/(prTP+prFP);
		}
	}
	
	private float computeRecallTop(int stepIndex, ArrayList<String> recommendatios){
		float reTP = 0;
		float reFN = 0;
		
		// compute precision related variables
		for(int i=stepIndex; i<m_sequenceURL.size(); i++){
			boolean hitURL = false;
			boolean hitTopic = false;
			
			String realstep = m_sequenceURL.get(i);
			int realstepInt = Integer.valueOf(realstep);
			int realstepInt2 = m_urlIds.indexOf(realstepInt);
			int realstepTop = m_url2topic[realstepInt2];
			
			for(int j=0; j<recommendatios.size(); j++){
				String onereco = recommendatios.get(j);
				int onerecoInt = Integer.valueOf(onereco);
				int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
				int onerecoTop =  m_url2topic[onerecoInt2];
				
				if(realstepInt == onerecoInt){
					hitURL = true;
					break;
				} else {
					if(onerecoTop!=-1 && realstepTop!=-1 &&
							onerecoTop == realstepTop){
						hitTopic = true;
					}
				}
			}
			
			if(hitURL){
				reTP = reTP + 1f;
			} else if(hitTopic){
				reTP = reTP + m_topicmatch;
			} else {
				reFN = reFN + 1f;
			}
		}
		
		if(reTP==0f && reFN==0f){
			return 0f;
		} else {
			return reTP/(reTP+reFN);
		}
	}
	
	
	
	// GET parameters/metrics of this class
	
	public float getNumberOfRecommendationsRatio(){
		return (float)m_numberOfRecommendations / (float)m_sequence.size();
	}
	
	public int getNumberOfFailures(){
		return m_nFailures;
	}
	
	// URL level metrics
	
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
		return this.getFmeasures(beta, m_precision, m_recall);
	}
	
	public float[] getFmeasuresModel(float beta){
		return this.getFmeasures(beta, m_precisionModel, m_recallModel);		
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
	
	// TOPIC level metrics
	
	public void setTopicParameters(ArrayList<Integer> urlIds, int[] url2topic, float topicmatch){
		m_urlIds = urlIds;
		m_url2topic = url2topic;
		m_topicmatch = topicmatch;
	}
	
	public float getHitRatioTop(){
		return (float)m_hitscoreTop/(float)m_sequence.size();
	}
	
	public float getClickSoonRatioTop(){
		return (float)m_clicksoonscoreTop/(float)m_sequence.size();
	}
	
	public float[] getPrecissionsTop(){
		return m_precisionTop;
	}
	
	public float[] getRecallsTop(){
		return m_recallTop;
	}
	
	public float[] getPrecissionsModelTop(){
		return m_precisionModelTop;
	}
	
	public float[] getRecallsModelTop(){
		return m_recallModelTop;
	}
	
	public float[] getFmeasuresTop(float beta){
		return this.getFmeasures(beta, m_precisionTop, m_recallTop);		
	}
	
	public float[] getFmeasuresModelTop(float beta){		
		return this.getFmeasures(beta, m_precisionModelTop, m_recallModelTop);		
	}
	
	public float getPrecisionTopAtPoint(float point){
		int index = this.getPosition(point);
		return m_precisionTop[index];
	}
	
	public float getRecallTopAtPoint(float point){
		int index = this.getPosition(point);
		return m_recallTop[index];
	}
	
	public float getFmeasureTopAtPoint(float beta, float point){
		float[] fmeasure = this.getFmeasuresTop(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	public float getPrecisionModelTopAtPoint(float point){
		int index = this.getPosition(point);
		return m_precisionModelTop[index];
	}
	
	public float getRecallModelTopAtPoint(float point){
		int index = this.getPosition(point);
		return m_recallModelTop[index];
	}
	
	public float getFmeasureModelTopAtPoint(float beta, float point){
		float[] fmeasure = this.getFmeasuresModelTop(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	
	
	// Utilities
	
	private int getPosition(float point){
		float len = (float)m_precision.length - (float)1;
		float index = len * point;
		return (int)Math.round(index);
	}
	
	private float[] getFmeasures(float beta, float[] prA, float[] reA){
		float[] fmeasure = new float[prA.length];
		for(int i=0; i<prA.length; i++){
			float b2 = (float)Math.pow(beta, 2);
			float pr = prA[i];
			float re = reA[i];
			if(pr==(float)0 && re==(float)0){
				fmeasure[i] = (float)0;
			} else {
				fmeasure[i] = ((float)1+b2) * (pr*re) / ((b2*pr)+re);
			}
		}
		return fmeasure;
	}
	
	
	
	// The main class to test it
	
	public static void main(String[] args){
		// create the suffix tree
        String[] word1 = {"c", "a", "c", "a", "o"};
        String[] word2 = {"b", "a", "n", "a", "n", "a"};
        String[] word3 = {"m", "i", "l", "o"};
        String[] word4 = {"c", "a", "r"};
        ArrayList<String[]> sequences = new ArrayList<String[]>();
        sequences.add(word1);
        sequences.add(word2);
        sequences.add(word3);
        sequences.add(word4);
        
        // create ST
        MySuffixTree st = new MySuffixTree(sequences);
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
