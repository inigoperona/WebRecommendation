package ehupatras.webrecommendation.evaluator.sequence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderMarkovChain;

public abstract class SequenceEvaluator {

	// ATTRIBUTES
	
	// Classes that simulates the navigation of the user
	protected Recommender m_recommender = null;
	protected ArrayList<String> m_sequence = null;
	protected ArrayList<String> m_sequenceURL = null;
	
	// Metrics
	protected int m_numberOfRecommendations = 0;
	private int m_nFailures = 0;
	private int[] m_homepages = null;
	// Topic's parameter
	private ArrayList<Integer> m_urlIds = null;
	private int[] m_url2topic = null;
	private float m_topicmatch = 0.5f;
	
	// HONEST MODE
	// URL level metrics
	private int m_hitscore = 0;
	private int m_clicksoonscore = 0;
	private float[] m_precision;
	private float[] m_recall;
	private float[] m_precisionModel;
	private float[] m_recallModel;	
	// TOPIC level metrics
	private float m_hitscoreTop = 0;
	private float m_clicksoonscoreTop = 0;
	private float[] m_precisionTop;
	private float[] m_recallTop;
	private float[] m_precisionModelTop;
	private float[] m_recallModelTop;
	
	// Index always correct
	// URL level metrics
	private int m_hitscore_OkHome = 0;
	private int m_clicksoonscore_OkHome = 0;
	private float[] m_precision_OkHome;
	private float[] m_recall_OkHome;
	private float[] m_precisionModel_OkHome;
	private float[] m_recallModel_OkHome;
	// TOPIC level metrics
	private float m_hitscoreTop_OkHome = 0;
	private float m_clicksoonscoreTop_OkHome = 0;
	private float[] m_precisionTop_OkHome;
	private float[] m_recallTop_OkHome;
	private float[] m_precisionModelTop_OkHome;
	private float[] m_recallModelTop_OkHome;
	
	// write the recommendations done in each step
	private String m_lineHeader = null;
	private BufferedWriter m_evalWriter = null;
	
	
	
	// CREATOR
	
	public SequenceEvaluator(ArrayList<String> sequence){
		this.constructor2(sequence);
	}
	
	public SequenceEvaluator(String[] sequence){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		this.constructor2(sequenceAL);
	}
	
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
		
		m_precision_OkHome = new float[sequence.size()];
		m_recall_OkHome = new float[sequence.size()];
		m_precisionModel_OkHome = new float[sequence.size()];
		m_recallModel_OkHome = new float[sequence.size()];		
		m_precisionTop_OkHome = new float[sequence.size()];
		m_recallTop_OkHome = new float[sequence.size()];
		m_precisionModelTop_OkHome = new float[sequence.size()];
		m_recallModelTop_OkHome = new float[sequence.size()];
	}
	
	private ArrayList<String> convertToArrayList(String[] strA){
		ArrayList<String> sequenceAL = new ArrayList<String>();
		for(int i=0; i<strA.length; i++){
			sequenceAL.add(strA[i]);
		}
		return sequenceAL;
	}
	
	
	// ABSTARCT FUNCTIONS
	
	public abstract Recommender getRecommender();
	
	
	// BODY //
	
	// mode = -1 : Unbounded
	// mode =  0 : Random, fixed number of requests
	// mode =  1 : Weighted the suffix tree only by weighted training sequences.
	// mode =  2 : Weighted the suffix tree only by original testing sequences (in exploitation).
	// mode =  3 : Weighted the suffix tree by weighted training sequences and original test sequences.
	// mode =  4 : Merge Markov Chain model and Suffix Tree models recommendations
	// mode =  5 : Weight the Suffix tree with original train sequences and in exploitation with original test sequences
	// mode =  6 : Weight the ST with Train sequences and enrich it with step1 URLs 
	public void computeSequenceMetrics(
			String mode, 
			int nrecos, long seed,
			int[] homepages,
			MarkovChain markovchain){
		m_homepages = homepages;
		m_recommender = this.getRecommender();
		m_recommender.reset();
		Recommender recM = null;
		ArrayList<String> waydone = new ArrayList<String>();
		ArrayList<String> list = null;
		
		if(mode.equals("unbounded")){ // -1
			list = m_recommender.getNextpossibleStepsUnbounded();
		} else if(mode.equals("random")){ // 0
			list = m_recommender.getNextpossibleStepsRandom(nrecos, seed);
		} else if(mode.equals("ST_wTrain") || mode.equals("weighted")){ // 1
			list = m_recommender.getNextpossibleStepsWeightedTrain(nrecos, waydone);
		} else if(mode.equals("ST_wTest")){ // 2
			list = m_recommender.getNextpossibleStepsWeightedTest(nrecos);
		} else if(mode.equals("ST_w")){ // 3
			list = m_recommender.getNextpossibleStepsWeighted(nrecos, waydone);
		} else if(mode.equals("ST_markov")){ // 4
			recM = new RecommenderMarkovChain(markovchain);
			ArrayList<String> listMarkov = recM.getNextpossibleStepsWeightedTest(nrecos);
			list = m_recommender.getNextpossibleStepsMarkov(nrecos, waydone, listMarkov);
		} else if(mode.equals("ST_wOrig")){ // 5
			list = m_recommender.getNextpossibleStepsWeightedByOriginalSequences(nrecos);
		} else if(mode.equals("ST_w_eS1")){ //6
			list = m_recommender.getNextpossibleStepsWeightedEnrichWithStep1(nrecos, waydone);
		}
		for(int i=0; i<m_sequence.size(); i++){
			// if we want to write recommendations
			// write the done recommendations in each step.
			if(m_lineHeader!=null){
				try{
					m_evalWriter.write(m_lineHeader + i + ":");
					if(list.size()>0){
						m_evalWriter.write(list.get(0));
					}
					for(int j=1; j<list.size(); j++){
						m_evalWriter.write("," + list.get(j));
					}
					m_evalWriter.write("\n");
				} catch(IOException ex){
					System.err.println("[ehupatras.webrecommendation.evaluator.SequenceEvaluator.computeSequenceMetrics] " +
							"Problems writing in the evaluation file.");
					System.err.println(ex.getMessage());
					System.exit(1);
				}
			}
			
			// update the prediction indicators
			this.computeStepMetrics(i, list);
			
			// do the step
			String nextstep = m_sequence.get(i);
			waydone = m_recommender.update(waydone, nextstep, true, true);
			
			// get the next recommendations
			if(mode.equals("unbounded")){
				list = m_recommender.getNextpossibleStepsUnbounded();
			} else if(mode.equals("random")){
				list = m_recommender.getNextpossibleStepsRandom(nrecos, seed);
			} else if(mode.equals("ST_wTrain") || mode.equals("weighted") ){
				list = m_recommender.getNextpossibleStepsWeightedTrain(nrecos, waydone);
			} else if(mode.equals("ST_wTest")){
				list = m_recommender.getNextpossibleStepsWeightedTest(nrecos);
			} else if(mode.equals("ST_w")){
				list = m_recommender.getNextpossibleStepsWeighted(nrecos, waydone);
			} else if(mode.equals("ST_markov")){
				recM.update(null, nextstep, false, false);
				ArrayList<String> listMarkov = recM.getNextpossibleStepsWeightedTest(nrecos);
				list = m_recommender.getNextpossibleStepsMarkov(nrecos, waydone, listMarkov);
			} else if(mode.equals("ST_wOrig")){
				list = m_recommender.getNextpossibleStepsWeightedByOriginalSequences(nrecos);
			} else if(mode.equals("ST_w_eS1")){
				list = m_recommender.getNextpossibleStepsWeightedEnrichWithStep1(nrecos, waydone);
			}
		}
		m_nFailures = m_recommender.getNumberOfFailures();
	}
	
	
	// To write the experimentation 
	
	public void setLineHeader(String lineHeader, BufferedWriter evalWriter){
		m_lineHeader = lineHeader;
		m_evalWriter = evalWriter;
	}
	
	
	
	
	
	// MAIN function //
	
	protected void computeStepMetrics(int stepIndex, ArrayList<String> recommendatios){		
		String step = m_sequenceURL.get(stepIndex);
		m_numberOfRecommendations = m_numberOfRecommendations + recommendatios.size();
		
		// URL level - HONEST
		this.computeHitScore(step, recommendatios);
		this.computeClickSoonScore(stepIndex, recommendatios);
		this.computeConfusionMatrix(stepIndex, recommendatios);
		
		// TOPIC level - HONEST
		this.computeHitScoreTop(step, recommendatios);
		this.computeClickSoonScoreTop(stepIndex, recommendatios);
		this.computeConfusionMatrixTop(stepIndex, recommendatios);
		
		// URL level - homepage always correct
		this.computeHitScore_OkHome(step, recommendatios);
		this.computeClickSoonScore_OkHome(stepIndex, recommendatios);
		this.computeConfusionMatrix_OkHome(stepIndex, recommendatios);
		
		// TOPIC level - homepage always correct
		this.computeHitScoreTop_OkHome(step, recommendatios);
		this.computeClickSoonScoreTop_OkHome(stepIndex, recommendatios);
		this.computeConfusionMatrixTop_OkHome(stepIndex, recommendatios);
	}
		
	
	
	
	
	// URL level functions - HONEST
	
	protected void computeHitScore(
			String step,
			ArrayList<String> recommendatios){
		for(int i=0; i<recommendatios.size(); i++){
			String recStr = recommendatios.get(i); 
			if(step.equals(recStr)){
				m_hitscore++;
				break;
			}
		}
	}
	
	protected void computeClickSoonScore(
			int stepIndex, 
			ArrayList<String> recommendatios){
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
	
	protected void computeConfusionMatrix(
			int stepIndex, 
			ArrayList<String> recommendatios){
		float pr = this.computePrecision(stepIndex, recommendatios);
		float re = this.computeRecall(stepIndex, recommendatios);
		m_precision[stepIndex] = pr;
		m_recall[stepIndex] = re;
		float prModel = this.computePrecision(0, recommendatios);
		float reModel = this.computeRecall(0, recommendatios);
		m_precisionModel[stepIndex] = prModel;
		m_recallModel[stepIndex] = reModel;
	}
	
	private float computePrecision(
			int stepIndex, 
			ArrayList<String> recommendatios){
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
	
	private float computeRecall(
			int stepIndex, 
			ArrayList<String> recommendatios){
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
	
		
	// TOPIC level functions - HONEST
	
	protected void computeHitScoreTop(
			String step, 
			ArrayList<String> recommendatios){
		// for each recommendations
		boolean hitURL = false;
		boolean hitTopic = false;
		for(int i=0; i<recommendatios.size(); i++){
			String rec = recommendatios.get(i);
			int stepInt = Integer.valueOf(step);
			int recInt = Integer.valueOf(rec);
			int stepInt2 = m_urlIds.indexOf(stepInt);
			int recInt2 = m_urlIds.indexOf(recInt);
			int stepTop = stepInt2==-1 ? -1 : m_url2topic[stepInt2];
			int recTop = recInt2==-1 ? -1 : m_url2topic[recInt2];
			
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
	
	protected void computeClickSoonScoreTop(
			int stepIndex,
			ArrayList<String> recommendatios){
		boolean hitURL = false;
		boolean hitTopic = false;		
		for(int i=0; i<recommendatios.size(); i++){
			String onereco = recommendatios.get(i);
			int onerecoInt = Integer.valueOf(onereco);
			int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
			int onerecoTop = onerecoInt2==-1 ? -1 : m_url2topic[onerecoInt2];
			
			for(int j=stepIndex; j<m_sequenceURL.size(); j++){
				String realstep = m_sequenceURL.get(j);
				int realstepInt = Integer.valueOf(realstep);
				int realstepInt2 = m_urlIds.indexOf(realstepInt);
				int realstepTop = realstepInt2==-1 ? -1 : m_url2topic[realstepInt2];
				
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
	
	protected void computeConfusionMatrixTop(
			int stepIndex, 
			ArrayList<String> recommendatios){
		float pr = this.computePrecisionTop(stepIndex, recommendatios);
		float re = this.computeRecallTop(stepIndex, recommendatios);
		m_precisionTop[stepIndex] = pr;
		m_recallTop[stepIndex] = re;
		float prModel = this.computePrecisionTop(0, recommendatios);
		float reModel = this.computeRecallTop(0, recommendatios);
		m_precisionModelTop[stepIndex] = prModel;
		m_recallModelTop[stepIndex] = reModel;
	}	
	
	private float computePrecisionTop(
			int stepIndex, 
			ArrayList<String> recommendatios){
		float prTP = 0;
		float prFP = 0;
		
		// compute precision related variables
		for(int i=0; i<recommendatios.size(); i++){
			boolean hitURL = false;
			boolean hitTopic = false;
			
			String onereco = recommendatios.get(i);
			int onerecoInt = Integer.valueOf(onereco);
			int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
			int onerecoTop =  onerecoInt2==-1 ? -1 : m_url2topic[onerecoInt2];
			
			for(int j=stepIndex; j<m_sequenceURL.size(); j++){
				String realstep = m_sequenceURL.get(j);
				int realstepInt = Integer.valueOf(realstep);
				int realstepInt2 = m_urlIds.indexOf(realstepInt);
				int realstepTop = realstepInt2==-1 ? -1 : m_url2topic[realstepInt2];
				
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
	
	private float computeRecallTop(
			int stepIndex, 
			ArrayList<String> recommendatios){
		float reTP = 0;
		float reFN = 0;
		
		// compute precision related variables
		for(int i=stepIndex; i<m_sequenceURL.size(); i++){
			boolean hitURL = false;
			boolean hitTopic = false;
			
			String realstep = m_sequenceURL.get(i);
			int realstepInt = Integer.valueOf(realstep);
			int realstepInt2 = m_urlIds.indexOf(realstepInt);
			int realstepTop = realstepInt2==-1 ? -1 : m_url2topic[realstepInt2];
			
			for(int j=0; j<recommendatios.size(); j++){
				String onereco = recommendatios.get(j);
				int onerecoInt = Integer.valueOf(onereco);
				int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
				int onerecoTop =  onerecoInt2==-1 ? -1 : m_url2topic[onerecoInt2];
				
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
	
	
	
	
	
	// URL level functions - homepage always correct
	
	protected void computeHitScore_OkHome(
			String step,
			ArrayList<String> recommendatios){
		for(int i=0; i<recommendatios.size(); i++){
			String recStr = recommendatios.get(i); 
			if(this.isHomepage(recStr)){
				m_hitscore_OkHome++;
				break;
			}
			if(step.equals(recStr)){
				m_hitscore_OkHome++;
				break;
			}
		}
	}
	
	protected void computeClickSoonScore_OkHome(
			int stepIndex, 
			ArrayList<String> recommendatios){
		boolean itWasUsed = false;
		for(int i=0; i<recommendatios.size(); i++){
			String onereco = recommendatios.get(i);
			if(this.isHomepage(onereco)){
				m_clicksoonscore_OkHome++;
				break;
			}
			for(int j=stepIndex; j<m_sequenceURL.size(); j++){
				String realstep = m_sequenceURL.get(j);
				if(onereco.equals(realstep)){
					m_clicksoonscore_OkHome++;
					itWasUsed = true;
					break;
				}
			}
			if(itWasUsed){break;}
		}
	}	
	
	protected void computeConfusionMatrix_OkHome(
			int stepIndex, 
			ArrayList<String> recommendatios){
		float pr = this.computePrecision_OkHome(stepIndex, recommendatios);
		float re = this.computeRecall_OkHome(stepIndex, recommendatios);
		m_precision_OkHome[stepIndex] = pr;
		m_recall_OkHome[stepIndex] = re;
		float prModel = this.computePrecision_OkHome(0, recommendatios);
		float reModel = this.computeRecall_OkHome(0, recommendatios);
		m_precisionModel_OkHome[stepIndex] = prModel;
		m_recallModel_OkHome[stepIndex] = reModel;
	}
	
	private float computePrecision_OkHome(
			int stepIndex, 
			ArrayList<String> recommendatios){
		int prTP = 0;
		int prFP = 0;
		// compute precision related variables
		for(int i=0; i<recommendatios.size(); i++){
			boolean itWasUsed = false;
			String onereco = recommendatios.get(i);
			
			if(this.isHomepage(onereco)){
				itWasUsed = true;
			} else {
				for(int j=stepIndex; j<m_sequenceURL.size(); j++){
					String realstep = m_sequenceURL.get(j);
					if(onereco.equals(realstep)){
						itWasUsed = true;
						break;
					}
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
	
	private float computeRecall_OkHome(
			int stepIndex, 
			ArrayList<String> recommendatios){
		int reTP = 0;
		int reFN = 0;
		// compute precision related variables
		for(int i=stepIndex; i<m_sequenceURL.size(); i++){
			boolean itWasUsed = false;
			String realstep = m_sequenceURL.get(i);
			
			if(this.isHomepage(realstep)){
				itWasUsed = true;
			} else {
				for(int j=0; j<recommendatios.size(); j++){
					String onereco = recommendatios.get(j);				
					if(realstep.equals(onereco)){
						itWasUsed = true;
						break;
					}
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
	
	
	// TOPIC level functions - homepage always correct
	
	protected void computeHitScoreTop_OkHome(
			String step, 
			ArrayList<String> recommendatios){
		// for each recommendations
		boolean hitURL = false;
		boolean hitTopic = false;
		
		for(int i=0; i<recommendatios.size(); i++){
			String rec = recommendatios.get(i);
			
			if(this.isHomepage(rec)){
				hitURL = true;
				break;
			}
			
			int stepInt = Integer.valueOf(step);
			int recInt = Integer.valueOf(rec);
			int stepInt2 = m_urlIds.indexOf(stepInt);
			int recInt2 = m_urlIds.indexOf(recInt);
			int stepTop = stepInt2==-1 ? -1 : m_url2topic[stepInt2];
			int recTop = recInt2==-1 ? -1 : m_url2topic[recInt2];
			
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
			m_hitscoreTop_OkHome = m_hitscoreTop_OkHome + 1f;
		} else if(hitTopic){
			m_hitscoreTop_OkHome = m_hitscoreTop_OkHome + m_topicmatch;
		}
	}	
	
	protected void computeClickSoonScoreTop_OkHome(
			int stepIndex,
			ArrayList<String> recommendatios){
		boolean hitURL = false;
		boolean hitTopic = false;
		
		for(int i=0; i<recommendatios.size(); i++){
			String onereco = recommendatios.get(i);
			
			if(this.isHomepage(onereco)){
				hitURL = true;
				break;
			}
			
			int onerecoInt = Integer.valueOf(onereco);
			int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
			int onerecoTop = onerecoInt2==-1 ? -1 : m_url2topic[onerecoInt2];
			
			for(int j=stepIndex; j<m_sequenceURL.size(); j++){
				String realstep = m_sequenceURL.get(j);
				int realstepInt = Integer.valueOf(realstep);
				int realstepInt2 = m_urlIds.indexOf(realstepInt);
				int realstepTop = realstepInt2==-1 ? -1 : m_url2topic[realstepInt2];
				
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
			m_clicksoonscoreTop_OkHome = m_clicksoonscoreTop_OkHome + 1f;
		} else if(hitTopic){
			m_clicksoonscoreTop_OkHome = m_clicksoonscoreTop_OkHome + m_topicmatch;
		}
	}	
	
	protected void computeConfusionMatrixTop_OkHome(
			int stepIndex, 
			ArrayList<String> recommendatios){
		float pr = this.computePrecisionTop_OkHome(stepIndex, recommendatios);
		float re = this.computeRecallTop_OkHome(stepIndex, recommendatios);
		m_precisionTop_OkHome[stepIndex] = pr;
		m_recallTop_OkHome[stepIndex] = re;
		float prModel = this.computePrecisionTop_OkHome(0, recommendatios);
		float reModel = this.computeRecallTop_OkHome(0, recommendatios);
		m_precisionModelTop_OkHome[stepIndex] = prModel;
		m_recallModelTop_OkHome[stepIndex] = reModel;
	}
	
	private float computePrecisionTop_OkHome(
			int stepIndex, 
			ArrayList<String> recommendatios){
		float prTP = 0;
		float prFP = 0;
		
		// compute precision related variables
		for(int i=0; i<recommendatios.size(); i++){
			boolean hitURL = false;
			boolean hitTopic = false;
			
			String onereco = recommendatios.get(i);
			
			if(this.isHomepage(onereco)){
				hitURL = true;
			} else {
			
				int onerecoInt = Integer.valueOf(onereco);
				int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
				int onerecoTop =  onerecoInt2==-1 ? -1 : m_url2topic[onerecoInt2];
			
				for(int j=stepIndex; j<m_sequenceURL.size(); j++){
					String realstep = m_sequenceURL.get(j);
					int realstepInt = Integer.valueOf(realstep);
					int realstepInt2 = m_urlIds.indexOf(realstepInt);
					int realstepTop = realstepInt2==-1 ? -1 : m_url2topic[realstepInt2];
				
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
	
	private float computeRecallTop_OkHome(
			int stepIndex, 
			ArrayList<String> recommendatios){
		float reTP = 0;
		float reFN = 0;
		
		// compute precision related variables
		for(int i=stepIndex; i<m_sequenceURL.size(); i++){
			boolean hitURL = false;
			boolean hitTopic = false;
			
			String realstep = m_sequenceURL.get(i);
			
			if(this.isHomepage(realstep)){
				hitURL = true;
			} else {
			
				int realstepInt = Integer.valueOf(realstep);
				int realstepInt2 = m_urlIds.indexOf(realstepInt);
				int realstepTop = realstepInt2==-1 ? -1 : m_url2topic[realstepInt2];
			
				for(int j=0; j<recommendatios.size(); j++){
					String onereco = recommendatios.get(j);
					int onerecoInt = Integer.valueOf(onereco);
					int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
					int onerecoTop =  onerecoInt2==-1 ? -1 : m_url2topic[onerecoInt2];
				
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
	
	// URL level metrics - HONEST	
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
	
	
	
	// TOPIC level metrics - HONEST	
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
	
	
	
	// URL level metrics - homepage always true
	public float getHitRatio_OkHome(){
		return (float)m_hitscore_OkHome/(float)m_sequence.size();
	}	
	public float getClickSoonRatio_OkHome(){
		return (float)m_clicksoonscore_OkHome/(float)m_sequence.size();
	}	
	public float[] getPrecissions_OkHome(){
		return m_precision_OkHome;
	}	
	public float[] getRecalls_OkHome(){
		return m_recall_OkHome;
	}	
	public float[] getPrecissionsModel_OkHome(){
		return m_precisionModel_OkHome;
	}	
	public float[] getRecallsModel_OkHome(){
		return m_recallModel_OkHome;
	}	
	public float[] getFmeasures_OkHome(float beta){
		return this.getFmeasures(beta, m_precision_OkHome, m_recall_OkHome);
	}	
	public float[] getFmeasuresModel_OkHome(float beta){
		return this.getFmeasures(beta, m_precisionModel_OkHome, m_recallModel_OkHome);
	}	
	public float getPrecisionAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_precision_OkHome[index];
	}	
	public float getRecallAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_recall_OkHome[index];
	}	
	public float getFmeasureAtPoint_OkHome(float beta, float point){
		float[] fmeasure = this.getFmeasures_OkHome(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}	
	public float getPrecisionModelAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_precisionModel_OkHome[index];
	}	
	public float getRecallModelAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_recallModel_OkHome[index];
	}	
	public float getFmeasureModelAtPoint_OkHome(float beta, float point){
		float[] fmeasure = this.getFmeasuresModel_OkHome(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	
	
	// TOPIC level metrics - - homepage always true
	public float getHitRatioTop_OkHome(){
		return (float)m_hitscoreTop_OkHome/(float)m_sequence.size();
	}	
	public float getClickSoonRatioTop_OkHome(){
		return (float)m_clicksoonscoreTop_OkHome/(float)m_sequence.size();
	}	
	public float[] getPrecissionsTop_OkHome(){
		return m_precisionTop_OkHome;
	}	
	public float[] getRecallsTop_OkHome(){
		return m_recallTop_OkHome;
	}	
	public float[] getPrecissionsModelTop_OkHome(){
		return m_precisionModelTop_OkHome;
	}	
	public float[] getRecallsModelTop_OkHome(){
		return m_recallModelTop_OkHome;
	}	
	public float[] getFmeasuresTop_OkHome(float beta){
		return this.getFmeasures(beta, m_precisionTop_OkHome, m_recallTop_OkHome);
	}	
	public float[] getFmeasuresModelTop_OkHome(float beta){		
		return this.getFmeasures(beta, m_precisionModelTop_OkHome, m_recallModelTop_OkHome);	
	}	
	public float getPrecisionTopAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_precisionTop_OkHome[index];
	}	
	public float getRecallTopAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_recallTop_OkHome[index];
	}	
	public float getFmeasureTopAtPoint_OkHome(float beta, float point){
		float[] fmeasure = this.getFmeasuresTop_OkHome(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}	
	public float getPrecisionModelTopAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_precisionModelTop_OkHome[index];
	}	
	public float getRecallModelTopAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_recallModelTop_OkHome[index];
	}	
	public float getFmeasureModelTopAtPoint_OkHome(float beta, float point){
		float[] fmeasure = this.getFmeasuresModelTop_OkHome(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	
	
	
	
	// Utilities
	
	private int getPosition(float point){
		float len = (float)m_precision.length;
		float index = len * point;
		float maxindex =  (float)m_precision.length - (float)1;
		float index2 = index>maxindex ? maxindex : index;
		return (int)Math.round(index2);
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
	
	private boolean isHomepage(String urlStr){
		int urlInt = Integer.valueOf(urlStr); 
		for(int i=0; i<m_homepages.length; i++){
			int homeInt = m_homepages[i];
			if(urlInt == homeInt){
				return true;
			}
		}
		return false;
	}
	
}
