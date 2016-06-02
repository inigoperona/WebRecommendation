package ehupatras.webrecommendation.evaluator.sequence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderMarkovChain;

// TODO: Auto-generated Javadoc
/**
 * The Class SequenceEvaluator.
 */
public abstract class SequenceEvaluator {

	// ATTRIBUTES
	
	// mode to compute precision and recall
	/** The m_mode pr re. */
	protected int m_modePrRe = 0;
	// URLs to not proposed neither take into account in the evaluation phase
	/** The m_no propose ur ls. */
	protected ArrayList<Integer> m_noProposeURLs = new ArrayList<Integer>();
	
	// Classes that simulates the navigation of the user
	/** The m_recommender. */
	protected Recommender m_recommender = null;
	
	/** The m_sequence. */
	protected ArrayList<String> m_sequence = null;
	
	/** The m_sequence url. */
	protected ArrayList<String> m_sequenceURL = null;
	
	// Metrics
	/** The m_number of recommendations. */
	protected int m_numberOfRecommendations = 0;
	
	/** The m_n failures. */
	private int m_nFailures = 0;
	
	/** The m_homepages. */
	private int[] m_homepages = null;
	// Topic's parameter
	/** The m_url ids. */
	private ArrayList<Integer> m_urlIds = null;
		// topic1: based on url to topic distribution
	/** The m_url2topic. */
		private int[] m_url2topic = null;
	
	/** The m_n diff topics. */
	private int m_nDiffTopics = 10;
	
	/** The m_topicmatch. */
	private float m_topicmatch = 0.5f;
		// topic2: based on url clustering
	/** The m_ url clustering dict. */
		private HashMap<Integer,Integer> m_UrlClusteringDict = null;
	
	/** The m_n diff clusters. */
	private int m_nDiffClusters = 10;
	
	// Similarity matrixes
	/** The m_usage ur ls. */
	protected ArrayList<Integer> m_usageURLs = null;
	
	/** The m_conv. */
	protected URLconverterUsaCon m_conv = null;
	
	/** The m_ url similarity matrix_ content. */
	protected float[][] m_UrlSimilarityMatrix_Content = null;
	
	/** The m_n ur ls. */
	protected int m_nURLs = 0;
	
	/** The m_ url similarity matrix_ usage. */
	protected float[][] m_UrlSimilarityMatrix_Usage = null;
	
	/** The m_ url similarity matrix_ usage_max. */
	protected float[] m_UrlSimilarityMatrix_Usage_max = null;
	
	/** The m_ url similarity matrix_ usage_min. */
	protected float[] m_UrlSimilarityMatrix_Usage_min = null;	
	
	// HONEST MODE
	// URL level metrics
	/** The m_hitscore. */
	private int m_hitscore = 0;
	
	/** The m_clicksoonscore. */
	private int m_clicksoonscore = 0;
	
	/** The m_precision. */
	private float[] m_precision;
	
	/** The m_recall. */
	private float[] m_recall;
	
	/** The m_cosine sim. */
	private float[] m_cosineSim;
	
	/** The m_one n nmetric. */
	private float[] m_oneNNmetric;
	
	/** The m_one n nmetric norm1. */
	private float[] m_oneNNmetricNorm1;
	
	/** The m_one n nmetric norm2. */
	private float[] m_oneNNmetricNorm2;
	
	/** The m_one n nmetric rank. */
	private float[] m_oneNNmetricRank;
	
	/** The m_precision model. */
	private float[] m_precisionModel;
	
	/** The m_recall model. */
	private float[] m_recallModel;
	
	/** The m_cosine sim model. */
	private float[] m_cosineSimModel;
	
	/** The m_one n nmetric model. */
	private float[] m_oneNNmetricModel;
	
	/** The m_one n nmetric norm1 model. */
	private float[] m_oneNNmetricNorm1Model;
	
	/** The m_one n nmetric norm2 model. */
	private float[] m_oneNNmetricNorm2Model;
	
	/** The m_one n nmetric rank model. */
	private float[] m_oneNNmetricRankModel;
	// TOPIC1 level metrics
	/** The m_hitscore top1. */
	private float m_hitscoreTop1 = 0;
	
	/** The m_clicksoonscore top1. */
	private float m_clicksoonscoreTop1 = 0;
	
	/** The m_precision top1. */
	private float[] m_precisionTop1;
	
	/** The m_recall top1. */
	private float[] m_recallTop1;
	
	/** The m_cosine sim top1. */
	private float[] m_cosineSimTop1;
	
	/** The m_precision model top1. */
	private float[] m_precisionModelTop1;
	
	/** The m_recall model top1. */
	private float[] m_recallModelTop1;
	
	/** The m_cosine sim model top1. */
	private float[] m_cosineSimModelTop1;
	// TOPIC1 level metrics
	/** The m_hitscore top2. */
	private float m_hitscoreTop2 = 0;
	
	/** The m_clicksoonscore top2. */
	private float m_clicksoonscoreTop2 = 0;
	
	/** The m_precision top2. */
	private float[] m_precisionTop2;
	
	/** The m_recall top2. */
	private float[] m_recallTop2;
	
	/** The m_cosine sim top2. */
	private float[] m_cosineSimTop2;
	
	/** The m_precision model top2. */
	private float[] m_precisionModelTop2;
	
	/** The m_recall model top2. */
	private float[] m_recallModelTop2;
	
	/** The m_cosine sim model top2. */
	private float[] m_cosineSimModelTop2;
	
	// Index always correct
	// URL level metrics
	/** The m_hitscore_ ok home. */
	private int m_hitscore_OkHome = 0;
	
	/** The m_clicksoonscore_ ok home. */
	private int m_clicksoonscore_OkHome = 0;
	
	/** The m_precision_ ok home. */
	private float[] m_precision_OkHome;
	
	/** The m_recall_ ok home. */
	private float[] m_recall_OkHome;
	
	/** The m_precision model_ ok home. */
	private float[] m_precisionModel_OkHome;
	
	/** The m_recall model_ ok home. */
	private float[] m_recallModel_OkHome;
	// TOPIC level metrics
	/** The m_hitscore top_ ok home. */
	private float m_hitscoreTop_OkHome = 0;
	
	/** The m_clicksoonscore top_ ok home. */
	private float m_clicksoonscoreTop_OkHome = 0;
	
	/** The m_precision top_ ok home. */
	private float[] m_precisionTop_OkHome;
	
	/** The m_recall top_ ok home. */
	private float[] m_recallTop_OkHome;
	
	/** The m_precision model top_ ok home. */
	private float[] m_precisionModelTop_OkHome;
	
	/** The m_recall model top_ ok home. */
	private float[] m_recallModelTop_OkHome;
	
	
	// write the recommendations done in each step
	/** The m_line header. */
	private String m_lineHeader = null;
	
	/** The m_eval writer. */
	private BufferedWriter m_evalWriter = null;
	
	
	
	// CREATOR
	
	/**
	 * Instantiates a new sequence evaluator.
	 *
	 * @param sequence the sequence
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 */
	public SequenceEvaluator(ArrayList<String> sequence, 
			int modePrRe, URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min){
		m_modePrRe = modePrRe;
		m_conv = conv;
		m_nURLs = nURLs;
		m_UrlSimilarityMatrix_Content = urlSimilarityMatrix;
		m_UrlSimilarityMatrix_Usage = urlSimilarityMatrix_Usage;
		m_UrlSimilarityMatrix_Usage_max = urlSimilarityMatrix_Usage_max;
		m_UrlSimilarityMatrix_Usage_min = urlSimilarityMatrix_Usage_min;
		this.constructor2(sequence);
	}
	
	/**
	 * Instantiates a new sequence evaluator.
	 *
	 * @param sequence the sequence
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 */
	public SequenceEvaluator(String[] sequence, 
			int modePrRe, URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min){
		ArrayList<String> sequenceAL = this.convertToArrayList(sequence);
		m_modePrRe = modePrRe;
		m_conv = conv;
		m_nURLs = nURLs;
		m_UrlSimilarityMatrix_Content = urlSimilarityMatrix;
		m_UrlSimilarityMatrix_Usage = urlSimilarityMatrix_Usage;
		m_UrlSimilarityMatrix_Usage_max = urlSimilarityMatrix_Usage_max;
		m_UrlSimilarityMatrix_Usage_min = urlSimilarityMatrix_Usage_min;
		this.constructor2(sequenceAL);
	}
	
	/**
	 * Constructor2.
	 *
	 * @param sequence the sequence
	 */
	private void constructor2(ArrayList<String> sequence){
		if(m_conv!=null){
			m_usageURLs = m_conv.getUsageUrls();
		} else {
			m_usageURLs = null;
		}
		
		m_sequence = sequence;
		m_sequenceURL = sequence;
		
		m_precision = new float[sequence.size()];
		m_recall = new float[sequence.size()];
		m_cosineSim = new float[sequence.size()];
		m_oneNNmetric = new float[sequence.size()];
		m_oneNNmetricNorm1 = new float[sequence.size()];
		m_oneNNmetricNorm2 = new float[sequence.size()];
		m_oneNNmetricRank = new float[sequence.size()];
		m_precisionModel = new float[sequence.size()];
		m_recallModel = new float[sequence.size()];
		m_cosineSimModel = new float[sequence.size()];
		m_oneNNmetricModel = new float[sequence.size()];
		m_oneNNmetricNorm1Model = new float[sequence.size()];
		m_oneNNmetricNorm2Model = new float[sequence.size()];
		m_oneNNmetricRankModel = new float[sequence.size()];
		
		m_precisionTop1 = new float[sequence.size()];
		m_recallTop1 = new float[sequence.size()];
		m_cosineSimTop1 = new float[sequence.size()];
		m_precisionModelTop1 = new float[sequence.size()];
		m_recallModelTop1 = new float[sequence.size()];
		m_cosineSimModelTop1 = new float[sequence.size()];
		m_precisionTop2 = new float[sequence.size()];
		m_recallTop2 = new float[sequence.size()];
		m_cosineSimTop2 = new float[sequence.size()];
		m_precisionModelTop2 = new float[sequence.size()];
		m_recallModelTop2 = new float[sequence.size()];
		m_cosineSimModelTop2 = new float[sequence.size()];
		
		m_precision_OkHome = new float[sequence.size()];
		m_recall_OkHome = new float[sequence.size()];
		m_precisionModel_OkHome = new float[sequence.size()];
		m_recallModel_OkHome = new float[sequence.size()];
		m_precisionTop_OkHome = new float[sequence.size()];
		m_recallTop_OkHome = new float[sequence.size()];
		m_precisionModelTop_OkHome = new float[sequence.size()];
		m_recallModelTop_OkHome = new float[sequence.size()];
		
		m_oneNNmetric = new float[sequence.size()];
	}
	
	/**
	 * Convert to array list.
	 *
	 * @param strA the str a
	 * @return the array list
	 */
	private ArrayList<String> convertToArrayList(String[] strA){
		ArrayList<String> sequenceAL = new ArrayList<String>();
		for(int i=0; i<strA.length; i++){
			sequenceAL.add(strA[i]);
		}
		return sequenceAL;
	}
	
	
	// ABSTARCT FUNCTIONS
	
	/**
	 * Gets the recommender.
	 *
	 * @return the recommender
	 */
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
	/**
	 * Compute sequence metrics.
	 *
	 * @param mode the mode
	 * @param nrecos the nrecos
	 * @param seed the seed
	 * @param homepages the homepages
	 * @param markovchain the markovchain
	 */
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
	
	/**
	 * Sets the line header.
	 *
	 * @param lineHeader the line header
	 * @param evalWriter the eval writer
	 */
	public void setLineHeader(String lineHeader, BufferedWriter evalWriter){
		m_lineHeader = lineHeader;
		m_evalWriter = evalWriter;
	}
	
	
	
	
	
	// MAIN function //
	
	/**
	 * Compute step metrics.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 */
	protected void computeStepMetrics(int stepIndex, ArrayList<String> recommendatios){		
		String step = m_sequenceURL.get(stepIndex);
		m_numberOfRecommendations = m_numberOfRecommendations + recommendatios.size();
		
		// URL level - HONEST
		this.computeHitScore(step, recommendatios);
		this.computeClickSoonScore(stepIndex, recommendatios);
		this.computeConfusionMatrix(stepIndex, recommendatios);
		if(m_conv!=null){
			this.cosineEvaluation(stepIndex, recommendatios);
		
			// TOPIC1 level
			this.computeHitScoreTop(step, recommendatios, true);
			this.computeClickSoonScoreTop(stepIndex, recommendatios, true);
			this.computeConfusionMatrixTop(stepIndex, recommendatios, true);
		
			// TOPIC2 level
			this.computeHitScoreTop(step, recommendatios, false);
			this.computeClickSoonScoreTop(stepIndex, recommendatios, false);
			this.computeConfusionMatrixTop(stepIndex, recommendatios, false);
		
			// URL level - homepage always correct
			this.computeHitScore_OkHome(step, recommendatios);
			this.computeClickSoonScore_OkHome(stepIndex, recommendatios);
			this.computeConfusionMatrix_OkHome(stepIndex, recommendatios);
		
			// TOPIC level - homepage always correct
			this.computeHitScoreTop_OkHome(step, recommendatios);
			this.computeClickSoonScoreTop_OkHome(stepIndex, recommendatios);
			this.computeConfusionMatrixTop_OkHome(stepIndex, recommendatios);
		}
	}
	
	
	
	// remove prohibited URLs
	
	/**
	 * Removes the prohibited ur ls.
	 *
	 * @param from the from
	 * @param sequenceURL the sequence url
	 * @return the array list
	 */
	private ArrayList<String> removeProhibitedURLs(int from, ArrayList<String> sequenceURL){
		ArrayList<String> sequenceURL2 = new ArrayList<String>(); 
		for(int i=from; i<sequenceURL.size(); i++){
			String stepStr = sequenceURL.get(i);
			int stepInt = Integer.valueOf(stepStr);
			if( !m_noProposeURLs.contains(new Integer(stepInt)) ){
				sequenceURL2.add(stepStr);
			}
		}
		return sequenceURL2;
	}
	
	
	
	// URL level functions - HONEST
	
	/**
	 * Compute hit score.
	 *
	 * @param step the step
	 * @param recommendatios the recommendatios
	 */
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
	
	/**
	 * Compute click soon score.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 */
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
	
	/**
	 * Compute confusion matrix.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 */
	protected void computeConfusionMatrix(
			int stepIndex, 
			ArrayList<String> recommendatios){
		float pr = this.computePrecision(stepIndex, recommendatios);
		float re = this.computeRecall(stepIndex, recommendatios);
		m_precision[stepIndex] = pr;
		m_recall[stepIndex] = re;
		if(m_conv!=null){
			float cs = this.cosineEvaluation(stepIndex, recommendatios);
			m_cosineSim[stepIndex] = cs;
			float onenn = this.oneNNmetric(stepIndex, recommendatios);
			m_oneNNmetric[stepIndex] = onenn;
			float onennN1 = this.oneNNmetricNorm1(stepIndex, recommendatios);
			m_oneNNmetricNorm1[stepIndex] = onennN1;
			float onennN2 = this.oneNNmetricNorm2(stepIndex, recommendatios);
			m_oneNNmetricNorm2[stepIndex] = onennN2;
			float onennRank = this.oneNNmetricRank(stepIndex, recommendatios);
			m_oneNNmetricRank[stepIndex] = onennRank;
		}
		
		float prModel = this.computePrecision(0, recommendatios);
		float reModel = this.computeRecall(0, recommendatios);
		m_precisionModel[stepIndex] = prModel;
		m_recallModel[stepIndex] = reModel;
		if(m_conv!=null){
			float csModel = this.cosineEvaluation(0, recommendatios);
			m_cosineSimModel[stepIndex] = csModel;
			float onennModel = this.oneNNmetric(0, recommendatios);
			m_oneNNmetricModel[stepIndex] = onennModel;
			float onennNorm1Model = this.oneNNmetricNorm1(0, recommendatios);
			m_oneNNmetricNorm1Model[stepIndex] = onennNorm1Model;
			float onennNorm2Model = this.oneNNmetricNorm2(0, recommendatios);
			m_oneNNmetricNorm2Model[stepIndex] = onennNorm2Model;
			float onennRankModel = this.oneNNmetricRank(0, recommendatios);
			m_oneNNmetricRankModel[stepIndex] = onennRankModel;
		}
	}
	
	/**
	 * Compute precision.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
	private float computePrecision(
			int stepIndex, 
			ArrayList<String> recommendatios){
		// take the 2nd part of the test sequence & remove prohibited URLs
		ArrayList<String> sequenceURL = this.removeProhibitedURLs(stepIndex, m_sequenceURL);
		if(sequenceURL.size()==0){return -1f;}
		
		// initialize variables
		int prTP = 0;
		int prFP = 0;
		
		// compute precision related variables
		for(int i=0; i<recommendatios.size(); i++){
			boolean itWasUsed = false;
			String onereco = recommendatios.get(i);
			for(int j=0; j<sequenceURL.size(); j++){
				String realstep = sequenceURL.get(j);
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
			return 0f;
		} else {
			float denominator = (float)prTP+(float)prFP;
			if(m_modePrRe==1){
				int recLen = recommendatios.size();
				int seqLen = sequenceURL.size();
				int len = Math.min(recLen, seqLen);
				denominator = (float)len;
			}
			return (float)prTP/denominator;
		}
	}
	
	/**
	 * Compute recall.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
	private float computeRecall(
			int stepIndex, 
			ArrayList<String> recommendatios){
		// take the 2nd part of the test sequence & remove prohibited URLs
		ArrayList<String> sequenceURL = this.removeProhibitedURLs(stepIndex, m_sequenceURL);
		if(sequenceURL.size()==0){return -1f;}
		
		// initialize variables
		int reTP = 0;
		int reFN = 0;
		// compute precision related variables
		for(int i=0; i<sequenceURL.size(); i++){
			String realstep = sequenceURL.get(i);
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
			float denominator = (float)reTP+(float)reFN;
			return (float)reTP/denominator;
		}
	}
	
	/**
	 * Cosine evaluation.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
	private float cosineEvaluation(
			int stepIndex, 
			ArrayList<String> recommendatios){

		// way
		int[] wayA = new int[m_urlIds.size()];
		for(int i=stepIndex; i<m_sequenceURL.size(); i++){
			String realstep = m_sequenceURL.get(i);
			int realstepInt = Integer.valueOf(realstep);
			int ind = m_urlIds.indexOf(realstepInt);
			if(ind!=-1){ wayA[ind] = 1; }
		}
		
		// recommendations
		int[] recosA = new int[m_urlIds.size()];
		for(int i=0; i<recommendatios.size(); i++){
			String recStr = recommendatios.get(i);
			int recInt = Integer.valueOf(recStr);
			int ind = m_urlIds.indexOf(recInt);
			if(ind!=-1){ recosA[ind] = 1; }
		}
		
		// cosine similarity
		return this.cosineSimilarity(wayA, recosA);
	}
	
	/**
	 * Cosine similarity.
	 *
	 * @param vec1 the vec1
	 * @param vec2 the vec2
	 * @return the float
	 */
	private float cosineSimilarity(int[] vec1, int[] vec2){
		int n = Math.min(vec1.length, vec2.length);
		
		// A · B
		int sum = 0;
		for(int i=0; i<n; i++){
			sum = sum + (vec1[i] * vec2[i]);
		}
		
		// ||A||
		int sumA = 0;
		for(int i=0; i<n; i++){
			int pow = (int)Math.pow(vec1[i], 2);
			sumA = sumA + pow;
		}
		double sumAd = Math.sqrt(sumA);
		
		// ||B||
		int sumB = 0;
		for(int i=0; i<n; i++){
			int pow = (int)Math.pow(vec2[i], 2);
			sumB = sumB + pow;
		}
		double sumBd = Math.sqrt(sumB);
		
		// result
		double sim = 0d;
		if(sumAd!=0d && sumBd!=0d){
			sim = (double)sum / (sumAd * sumBd);
		}
		return (float)sim;
	}
	
	
	
	// the average of each proposed URL' similarity to the navigated URLs
	// between proposed and 
	/**
	 * One n nmetric.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
	private float oneNNmetric(int stepIndex, 
					ArrayList<String> recommendatios){
		// take the 2nd part of the test sequence & remove prohibited URLs
		ArrayList<String> sequenceURL = this.removeProhibitedURLs(stepIndex, m_sequenceURL);
		if(sequenceURL.size()==0){return -1f;}
		if(recommendatios.size()==0){return -1f;}

		// initialize variables
		int recLen = recommendatios.size();
		float[] simsum = new float[recLen];

		// compute 1-NN similarity average 
		for(int i=0; i<recLen; i++){
			String recStr = recommendatios.get(i);
			int recInt = Integer.valueOf(recStr);
			int recind = m_usageURLs.indexOf(recInt);
			float maxsim = 0f;
			for(int j=0; j<sequenceURL.size(); j++){
				String stepStr = sequenceURL.get(j);
				int stepInt = Integer.valueOf(stepStr);
				int stepind = m_usageURLs.indexOf(stepInt);
				float sim = 0f;
				if(recind!=-1 && stepind!=-1){
					sim = m_UrlSimilarityMatrix_Usage[recind][stepind];
				}
				if(maxsim<sim){
					maxsim = sim;
				}
			}
			simsum[i] = maxsim;
		}
		
		// average of similarities
		float sum = 0f;
		int k = recLen;
		if(m_modePrRe==1){
			int seqLen = sequenceURL.size();
			k = Math.min(recLen, seqLen);
		}
		float[] simsum2 = this.orderArrayR(simsum, k);
		for(int i=0; i<simsum2.length; i++){
			sum = sum + simsum2[i];
		}
		float result = sum/(float)simsum2.length; 
		return result;
	}
	
	/**
	 * Order array r.
	 *
	 * @param arr the arr
	 * @param k the k
	 * @return the float[]
	 */
	private float[] orderArrayR(float[] arr, int k){
		// order array values
		ArrayList<Float> arr2AL = this.orderR_AL(arr);
		// convert to float
		int l2 = Math.min(arr.length, k);
		float[] arr2 = new float[l2];
		for(int i=0; i<l2; i++){
			arr2[i] = arr2AL.get(i);
		}
		return arr2;
	}
	
	/**
	 * Order array.
	 *
	 * @param arr the arr
	 * @param k the k
	 * @return the float[]
	 */
	private float[] orderArray(float[] arr, int k){
		// order array values
		ArrayList<Float> arr2AL = this.orderR_AL(arr);
		// convert to float
		int l = arr.length;
		int l2 = Math.min(l, k);
		float[] arr2 = new float[l2];
		int i2 = 0;
		for(int i=l-1; i>=l-l2; i--){
			arr2[i2] = arr2AL.get(i);
			i2++;
		}
		return arr2;
	}
	
	/**
	 * Order r_ al.
	 *
	 * @param arr the arr
	 * @return the array list
	 */
	private ArrayList<Float> orderR_AL(float[] arr){
		ArrayList<Float> arr2AL = new ArrayList<Float>();
		for(int i=0; i<arr.length; i++){
			float sim = arr[i];
			int j=0;
			for(;j<arr2AL.size();j++){
				float sim2 = arr2AL.get(j);
				if(sim2<sim){
					break;
				}
			}
			arr2AL.add(j,sim);
		}
		return arr2AL;
	}
	
	/**
	 * One n nmetric norm1.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
	private float oneNNmetricNorm1(int stepIndex, 
			ArrayList<String> recommendatios){
		// take the 2nd part of the test sequence & remove prohibited URLs
		ArrayList<String> sequenceURL = this.removeProhibitedURLs(stepIndex, m_sequenceURL);
		if(sequenceURL.size()==0){return -1f;}
		if(recommendatios.size()==0){return -1f;}

		// initialize variables
		int recLen = recommendatios.size();
		float[] simsum = new float[recLen];

		// compute 1-NN similarity average 
		for(int i=0; i<recLen; i++){
			String recStr = recommendatios.get(i);
			int recInt = Integer.valueOf(recStr);
			int recind = m_usageURLs.indexOf(recInt);
			float maxsim = 0f;
			for(int j=0; j<sequenceURL.size(); j++){
				String stepStr = sequenceURL.get(j);
				int stepInt = Integer.valueOf(stepStr);
				int stepind = m_usageURLs.indexOf(stepInt);
				float sim = 0f;
				if(recind!=-1 && stepind!=-1){
					sim = m_UrlSimilarityMatrix_Usage[recind][stepind];
				}
				if(maxsim<sim){
					maxsim = sim;
				}
			}
			float maxsimToNorm = recind==-1 ? 1f : m_UrlSimilarityMatrix_Usage_max[recind];
			simsum[i] = (maxsim/maxsimToNorm);
		}
		
		// average of similarities
		float sum = 0f;
		int k = recLen;
		if(m_modePrRe==1){
			int seqLen = sequenceURL.size();
			k = Math.min(recLen, seqLen);
		}
		float[] simsum2 = this.orderArrayR(simsum, k);
		for(int i=0; i<simsum2.length; i++){
			sum = sum + simsum2[i];
		}
		return sum/(float)simsum2.length;
	}
	
	/**
	 * One n nmetric norm2.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
	private float oneNNmetricNorm2(int stepIndex, 
			ArrayList<String> recommendatios){
		// take the 2nd part of the test sequence & remove prohibited URLs
		ArrayList<String> sequenceURL = this.removeProhibitedURLs(stepIndex, m_sequenceURL);
		if(sequenceURL.size()==0){return -1f;}
		if(recommendatios.size()==0){return -1f;}

		// initialize variables
		int recLen = recommendatios.size();
		float[] simsum = new float[recLen];

		// compute 1-NN similarity average 
		for(int i=0; i<recLen; i++){
			String recStr = recommendatios.get(i);
			int recInt = Integer.valueOf(recStr);
			int recind = m_usageURLs.indexOf(recInt);
			float maxsim = 0f;
			for(int j=0; j<sequenceURL.size(); j++){
				String stepStr = sequenceURL.get(j);
				int stepInt = Integer.valueOf(stepStr);
				int stepind = m_usageURLs.indexOf(stepInt);
				float sim = 0f;
				if(recind!=-1 && stepind!=-1){
					sim = m_UrlSimilarityMatrix_Usage[recind][stepind];
				}
				if(maxsim<sim){
					maxsim = sim;
				}
			}
			float maxsimToNorm = recind==-1 ? 1f : m_UrlSimilarityMatrix_Usage_max[recind];
			float minsimToNorm = recind==-1 ? 1f : m_UrlSimilarityMatrix_Usage_min[recind];
			simsum[i] = (maxsim-minsimToNorm)/(maxsimToNorm-minsimToNorm);
		}
		
		// average of similarities
		float sum = 0f;
		int k = recLen;
		if(m_modePrRe==1){
			int seqLen = sequenceURL.size();
			k = Math.min(recLen, seqLen);
		}
		float[] simsum2 = this.orderArrayR(simsum, k);
		for(int i=0; i<simsum2.length; i++){
			sum = sum + simsum2[i];
		}
		return sum/(float)simsum2.length;	
	}
	
	/**
	 * One n nmetric rank.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
	private float oneNNmetricRank(int stepIndex, 
			ArrayList<String> recommendatios){
		// take the 2nd part of the test sequence & remove prohibited URLs
		ArrayList<String> sequenceURL = this.removeProhibitedURLs(stepIndex, m_sequenceURL);
		if(sequenceURL.size()==0){return -1f;}
		if(recommendatios.size()==0){return -1f;}

		// initialize variables
		int recLen = recommendatios.size();
		float[] ranksum = new float[recLen];

		// compute 1-NN similarity average 
		for(int i=0; i<recLen; i++){
			String recStr = recommendatios.get(i);
			int recInt = Integer.valueOf(recStr);
			int recind = m_usageURLs.indexOf(recInt);
			float maxsim = 0f;
			for(int j=0; j<sequenceURL.size(); j++){
				String stepStr = sequenceURL.get(j);
				int stepInt = Integer.valueOf(stepStr);
				int stepind = m_usageURLs.indexOf(stepInt);
				float sim = 0f;
				if(recind!=-1 && stepind!=-1){
					sim = m_UrlSimilarityMatrix_Usage[recind][stepind];
				}
				if(maxsim<sim){
					maxsim = sim;
				}
			}
			
			// compute the rank of the value
			int rank = 1;
			for(int j=0; j<m_UrlSimilarityMatrix_Usage.length; j++){
				float val = recind==-1 ? 1f : m_UrlSimilarityMatrix_Usage[recind][j];
				if(val>maxsim){
					rank++;
				}
			}
			
			// sum of ranks
			ranksum[i] = (float)rank;
		}
		
		// average of similarities
		float sum = 0f;
		int k = recLen;
		if(m_modePrRe==1){
			int seqLen = sequenceURL.size();
			k = Math.min(recLen, seqLen);
		}
		float[] simsum2 = this.orderArray(ranksum, k);
		for(int i=0; i<simsum2.length; i++){
			sum = sum + simsum2[i];
		}
		return sum/(float)simsum2.length;
	}
	
	
		
	// TOPIC GLOBAL functions.
	
	/**
	 * Gets the topic id.
	 *
	 * @param urlID the url id
	 * @param isTopic the is topic
	 * @return the topic id
	 */
	private int getTopicID(int urlID, boolean isTopic){
		if(isTopic){
			return this.getTopicID_1(urlID);
		} else {
			return this.getTopicID_2(urlID);
		}
	}
	
	/**
	 * Gets the topic i d_1.
	 *
	 * @param urlID the url id
	 * @return the topic i d_1
	 */
	private int getTopicID_1(int urlID){
		int topicID = 0;
		if(urlID==-1){
			topicID = -1;
		} else {
			topicID = m_url2topic[urlID];
		}
		return topicID;
	}
	
	/**
	 * Gets the topic i d_2.
	 *
	 * @param urlID the url id
	 * @return the topic i d_2
	 */
	private int getTopicID_2(int urlID){
		int topicID = 0;
		if(urlID==-1){
			topicID = -1;
		} else {
			topicID = m_UrlClusteringDict.get(urlID);
		}
		return topicID;
	}
	
	/**
	 * Compute hit score top.
	 *
	 * @param step the step
	 * @param recommendatios the recommendatios
	 * @param isTopic the is topic
	 */
	private void computeHitScoreTop(
			String step, 
			ArrayList<String> recommendatios,
			boolean isTopic){
		// for each recommendations
		boolean hitURL = false;
		boolean hitTopic = false;
		for(int i=0; i<recommendatios.size(); i++){
			String rec = recommendatios.get(i);
			int stepInt = Integer.valueOf(step);
			int recInt = Integer.valueOf(rec);
			int stepInt2 = m_urlIds.indexOf(stepInt);
			int recInt2 = m_urlIds.indexOf(recInt);
			int stepTop = this.getTopicID(stepInt2, isTopic);
			int recTop = this.getTopicID(recInt2, isTopic);
			
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
			if(isTopic){
				m_hitscoreTop1 = m_hitscoreTop1 + 1f;
			} else {
				m_hitscoreTop2 = m_hitscoreTop2 + 1f;
			}
		} else if(hitTopic){
			if(isTopic){
				m_hitscoreTop1 = m_hitscoreTop1 + m_topicmatch;
			} else {
				m_hitscoreTop2 = m_hitscoreTop2 + m_topicmatch;
			}
		}
	}
	
	/**
	 * Compute click soon score top.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @param isTopic the is topic
	 */
	private void computeClickSoonScoreTop(
			int stepIndex,
			ArrayList<String> recommendatios,
			boolean isTopic){
		boolean hitURL = false;
		boolean hitTopic = false;		
		for(int i=0; i<recommendatios.size(); i++){
			String onereco = recommendatios.get(i);
			int onerecoInt = Integer.valueOf(onereco);
			int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
			int onerecoTop = this.getTopicID(onerecoInt2, isTopic);
			
			for(int j=stepIndex; j<m_sequenceURL.size(); j++){
				String realstep = m_sequenceURL.get(j);
				int realstepInt = Integer.valueOf(realstep);
				int realstepInt2 = m_urlIds.indexOf(realstepInt);
				int realstepTop = this.getTopicID(realstepInt2, isTopic);
				
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
			if(isTopic){
				m_clicksoonscoreTop1 = m_clicksoonscoreTop1 + 1f;
			} else {
				m_clicksoonscoreTop2 = m_clicksoonscoreTop2 + 1f;
			}
		} else if(hitTopic){
			if(isTopic){
				m_clicksoonscoreTop1 = m_clicksoonscoreTop1 + m_topicmatch;
			} else {
				m_clicksoonscoreTop2 = m_clicksoonscoreTop2 + m_topicmatch;
			}
		}
	}
	
	/**
	 * Compute confusion matrix top.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @param isTopic the is topic
	 */
	private void computeConfusionMatrixTop(
			int stepIndex, 
			ArrayList<String> recommendatios,
			boolean isTopic){
		float pr = this.computePrecisionTop(stepIndex, recommendatios, isTopic);
		float re = this.computeRecallTop(stepIndex, recommendatios, isTopic);
		float cs = this.cosineEvaluationTop(stepIndex, recommendatios, isTopic);
		if(isTopic){
			m_precisionTop1[stepIndex] = pr;
			m_recallTop1[stepIndex] = re;
			m_cosineSimTop1[stepIndex] = cs;
		} else {
			m_precisionTop2[stepIndex] = pr;
			m_recallTop2[stepIndex] = re;
			m_cosineSimTop2[stepIndex] = cs;
		}
		float prModel = this.computePrecisionTop(0, recommendatios, isTopic);
		float reModel = this.computeRecallTop(0, recommendatios, isTopic);
		float csModel = this.cosineEvaluationTop(0, recommendatios, isTopic);
		if(isTopic){
			m_precisionModelTop1[stepIndex] = prModel;
			m_recallModelTop1[stepIndex] = reModel;
			m_cosineSimModelTop1[stepIndex] = csModel;
		} else {
			m_precisionModelTop2[stepIndex] = prModel;
			m_recallModelTop2[stepIndex] = reModel;
			m_cosineSimModelTop2[stepIndex] = csModel;
		}
	}
	
	/**
	 * Compute precision top.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @param isTopic the is topic
	 * @return the float
	 */
	private float computePrecisionTop(
			int stepIndex, 
			ArrayList<String> recommendatios,
			boolean isTopic){
		// take the 2nd part of the test sequence & remove prohibited URLs
		ArrayList<String> sequenceURL = this.removeProhibitedURLs(stepIndex, m_sequenceURL);
		if(sequenceURL.size()==0){return -1f;}
		
		// initialize variables
		float prTP = 0f;
		float prFP = 0f;
		float recLen = 0f;
		
		// compute precision related variables
		for(int i=0; i<recommendatios.size(); i++){
			boolean hitURL = false;
			boolean hitTopic = false;
			
			String onereco = recommendatios.get(i);
			int onerecoInt = Integer.valueOf(onereco);
			int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
			int onerecoTop =  this.getTopicID(onerecoInt2, isTopic);
			
			for(int j=0; j<sequenceURL.size(); j++){
				String realstep = sequenceURL.get(j);
				int realstepInt = Integer.valueOf(realstep);
				int realstepInt2 = m_urlIds.indexOf(realstepInt);
				int realstepTop = this.getTopicID(realstepInt2, isTopic);
				
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
				recLen = recLen + 1f;
			} else if(hitTopic){				
				prTP = prTP + m_topicmatch;
				recLen = recLen + m_topicmatch;
			} else {
				prFP = prFP + 1f;
				recLen = recLen + 1f;
			}
		}
		
		if(prTP==0f && prFP==0f){
			return 0f;
		} else {
			float denominator = prTP+prFP;
			if(m_modePrRe==1){
				float seqLen = (float)sequenceURL.size();
				float len = Math.min(recLen, seqLen);
				denominator = (float)len;
			}
			return prTP/denominator;
		}
	}	
	
	/**
	 * Compute recall top.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @param isTopic the is topic
	 * @return the float
	 */
	private float computeRecallTop(
			int stepIndex, 
			ArrayList<String> recommendatios,
			boolean isTopic){
		// take the 2nd part of the test sequence & remove prohibited URLs
		ArrayList<String> sequenceURL = this.removeProhibitedURLs(stepIndex, m_sequenceURL);
		if(sequenceURL.size()==0){return -1f;}

		// initialize variables
		float reTP = 0f;
		float reFN = 0f;
		
		// compute precision related variables
		for(int i=0; i<sequenceURL.size(); i++){
			boolean hitURL = false;
			boolean hitTopic = false;
			
			String realstep = sequenceURL.get(i);
			int realstepInt = Integer.valueOf(realstep);
			int realstepInt2 = m_urlIds.indexOf(realstepInt);
			int realstepTop = this.getTopicID(realstepInt2, isTopic);
			
			for(int j=0; j<recommendatios.size(); j++){
				String onereco = recommendatios.get(j);
				int onerecoInt = Integer.valueOf(onereco);
				int onerecoInt2 = m_urlIds.indexOf(onerecoInt);
				int onerecoTop =  this.getTopicID(onerecoInt2, isTopic);
				
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
			float denominator = reTP+reFN;
			return reTP/denominator;
		}
	}
	
	/**
	 * Cosine evaluation top.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @param isTopic the is topic
	 * @return the float
	 */
	private float cosineEvaluationTop(
			int stepIndex, 
			ArrayList<String> recommendatios,
			boolean isTopic){
		int n = isTopic ? m_nDiffTopics : m_nDiffClusters;

		// way
		int[] wayA = new int[n];
		for(int i=stepIndex; i<m_sequenceURL.size(); i++){
			String realstep = m_sequenceURL.get(i);
			int realstepInt = Integer.valueOf(realstep);
			int realstepInt2 = m_urlIds.indexOf(realstepInt);
			int realstepTop = this.getTopicID(realstepInt2, isTopic);

			if(realstepTop!=-1){
				int ind = isTopic ? realstepTop : realstepTop-1;
				wayA[ind] = 1;
			}
		}
		
		// recommendations
		int[] recosA = new int[n];
		for(int i=0; i<recommendatios.size(); i++){
			String recStr = recommendatios.get(i);
			int recInt = Integer.valueOf(recStr);
			int recInt2 = m_urlIds.indexOf(recInt);
			int recTop = this.getTopicID(recInt2, isTopic);
			
			if(recTop!=-1){
				int ind = isTopic ? recTop : recTop-1;
				recosA[ind] = 1;
			}
		}
		
		// cosine similarity
		return this.cosineSimilarity(wayA, recosA);
	}

	
	
	
	
	// URL level functions - homepage always correct
	
	/**
	 * Compute hit score_ ok home.
	 *
	 * @param step the step
	 * @param recommendatios the recommendatios
	 */
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
	
	/**
	 * Compute click soon score_ ok home.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 */
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
	
	/**
	 * Compute confusion matrix_ ok home.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 */
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
	
	/**
	 * Compute precision_ ok home.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
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
	
	/**
	 * Compute recall_ ok home.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
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
	
	/**
	 * Compute hit score top_ ok home.
	 *
	 * @param step the step
	 * @param recommendatios the recommendatios
	 */
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
	
	/**
	 * Compute click soon score top_ ok home.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 */
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
	
	/**
	 * Compute confusion matrix top_ ok home.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 */
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
	
	/**
	 * Compute precision top_ ok home.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
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
	
	/**
	 * Compute recall top_ ok home.
	 *
	 * @param stepIndex the step index
	 * @param recommendatios the recommendatios
	 * @return the float
	 */
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
	/**
	 * Gets the number of recommendations ratio.
	 *
	 * @return the number of recommendations ratio
	 */
	public float getNumberOfRecommendationsRatio(){
		return (float)m_numberOfRecommendations / (float)m_sequence.size();
	}	
	
	/**
	 * Gets the number of failures.
	 *
	 * @return the number of failures
	 */
	public int getNumberOfFailures(){
		return m_nFailures;
	}
	
	// URL level metrics - HONEST	
	/**
	 * Gets the hit ratio.
	 *
	 * @return the hit ratio
	 */
	public float getHitRatio(){
		return (float)m_hitscore/(float)m_sequence.size();
	}	
	
	/**
	 * Gets the click soon ratio.
	 *
	 * @return the click soon ratio
	 */
	public float getClickSoonRatio(){
		return (float)m_clicksoonscore/(float)m_sequence.size();
	}	
	
	/**
	 * Gets the precissions.
	 *
	 * @return the precissions
	 */
	public float[] getPrecissions(){
		return m_precision;
	}	
	
	/**
	 * Gets the recalls.
	 *
	 * @return the recalls
	 */
	public float[] getRecalls(){
		return m_recall;
	}
	
	/**
	 * Gets the fmeasures.
	 *
	 * @param beta the beta
	 * @return the fmeasures
	 */
	public float[] getFmeasures(float beta){
		return this.getFmeasures(beta, m_precision, m_recall);
	}
	
	/**
	 * Gets the cosine similarities.
	 *
	 * @return the cosine similarities
	 */
	public float[] getCosineSimilarities(){
		return m_cosineSim;
	}
	
	/**
	 * Gets the one n nmetric.
	 *
	 * @return the one n nmetric
	 */
	public float[] getOneNNmetric(){
		return m_oneNNmetric;
	}
	
	/**
	 * Gets the one n nmetric norm1.
	 *
	 * @return the one n nmetric norm1
	 */
	public float[] getOneNNmetricNorm1(){
		return m_oneNNmetricNorm1;
	}
	
	/**
	 * Gets the one n nmetric norm2.
	 *
	 * @return the one n nmetric norm2
	 */
	public float[] getOneNNmetricNorm2(){
		return m_oneNNmetricNorm2;
	}
	
	/**
	 * Gets the one n nmetric rank.
	 *
	 * @return the one n nmetric rank
	 */
	public float[] getOneNNmetricRank(){
		return m_oneNNmetricRank;
	}
	
	/**
	 * Gets the precissions model.
	 *
	 * @return the precissions model
	 */
	public float[] getPrecissionsModel(){
		return m_precisionModel;
	}	
	
	/**
	 * Gets the recalls model.
	 *
	 * @return the recalls model
	 */
	public float[] getRecallsModel(){
		return m_recallModel;
	}
	
	/**
	 * Gets the fmeasures model.
	 *
	 * @param beta the beta
	 * @return the fmeasures model
	 */
	public float[] getFmeasuresModel(float beta){
		return this.getFmeasures(beta, m_precisionModel, m_recallModel);		
	}
	
	/**
	 * Gets the cosine similarities model.
	 *
	 * @return the cosine similarities model
	 */
	public float[] getCosineSimilaritiesModel(){
		return m_cosineSimModel;
	}
	
	/**
	 * Gets the one n nmetric model.
	 *
	 * @return the one n nmetric model
	 */
	public float[] getOneNNmetricModel(){
		return m_oneNNmetricModel;
	}
	
	/**
	 * Gets the one n nmetric norm1 model.
	 *
	 * @return the one n nmetric norm1 model
	 */
	public float[] getOneNNmetricNorm1Model(){
		return m_oneNNmetricNorm1Model;
	}
	
	/**
	 * Gets the one n nmetric norm2 model.
	 *
	 * @return the one n nmetric norm2 model
	 */
	public float[] getOneNNmetricNorm2Model(){
		return m_oneNNmetricNorm2Model;
	}
	
	/**
	 * Gets the one n nmetric rank model.
	 *
	 * @return the one n nmetric rank model
	 */
	public float[] getOneNNmetricRankModel(){
		return m_oneNNmetricRankModel;
	}
	
	/**
	 * Prints the precision.
	 */
	protected void printPrecision(){
		System.out.print("Precision: ");
		for(int i=0; i<m_precision.length; i++){
			System.out.print(m_precision[i] + "; ");
		}
		System.out.println();
	}	
	
	/**
	 * Prints the recall.
	 */
	protected void printRecall(){
		System.out.print("Recall: ");
		for(int i=0; i<m_recall.length; i++){
			System.out.print(m_recall[i] + "; ");
		}
		System.out.println();
	}	
	
	/**
	 * Prints the fmeasure.
	 *
	 * @param beta the beta
	 */
	protected void printFmeasure(float beta){
		float fmeasure[] = this.getFmeasures(beta);
		System.out.print("F" + beta + "-measure: ");
		for(int i=0; i<fmeasure.length; i++){
			System.out.print(fmeasure[i] + "; ");
		}
		System.out.println();
	}	
	
	/**
	 * Gets the precision at point.
	 *
	 * @param point the point
	 * @return the precision at point
	 */
	public float getPrecisionAtPoint(float point){
		int index = this.getPosition(point);
		return m_precision[index];
	}	
	
	/**
	 * Gets the recall at point.
	 *
	 * @param point the point
	 * @return the recall at point
	 */
	public float getRecallAtPoint(float point){
		int index = this.getPosition(point);
		return m_recall[index];
	}	
	
	/**
	 * Gets the fmeasure at point.
	 *
	 * @param beta the beta
	 * @param point the point
	 * @return the fmeasure at point
	 */
	public float getFmeasureAtPoint(float beta, float point){
		float[] fmeasure = this.getFmeasures(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	/**
	 * Gets the cosine similarity at point.
	 *
	 * @param point the point
	 * @return the cosine similarity at point
	 */
	public float getCosineSimilarityAtPoint(float point){
		int index = this.getPosition(point);
		return m_cosineSim[index];
	}
	
	/**
	 * Gets the one n nmetric at point.
	 *
	 * @param point the point
	 * @return the one n nmetric at point
	 */
	public float getOneNNmetricAtPoint(float point){
		int index = this.getPosition(point);
		return m_oneNNmetric[index];
	}
	
	/**
	 * Gets the one n nmetric norm1 at point.
	 *
	 * @param point the point
	 * @return the one n nmetric norm1 at point
	 */
	public float getOneNNmetricNorm1AtPoint(float point){
		int index = this.getPosition(point);
		return m_oneNNmetricNorm1[index];
	}
	
	/**
	 * Gets the one n nmetric norm2 at point.
	 *
	 * @param point the point
	 * @return the one n nmetric norm2 at point
	 */
	public float getOneNNmetricNorm2AtPoint(float point){
		int index = this.getPosition(point);
		return m_oneNNmetricNorm2[index];
	}
	
	/**
	 * Gets the one n nmetric rank at point.
	 *
	 * @param point the point
	 * @return the one n nmetric rank at point
	 */
	public float getOneNNmetricRankAtPoint(float point){
		int index = this.getPosition(point);
		return m_oneNNmetricRank[index];
	}
	
	/**
	 * Gets the precision model at point.
	 *
	 * @param point the point
	 * @return the precision model at point
	 */
	public float getPrecisionModelAtPoint(float point){
		int index = this.getPosition(point);
		return m_precisionModel[index];
	}	
	
	/**
	 * Gets the recall model at point.
	 *
	 * @param point the point
	 * @return the recall model at point
	 */
	public float getRecallModelAtPoint(float point){
		int index = this.getPosition(point);
		return m_recallModel[index];
	}	
	
	/**
	 * Gets the fmeasure model at point.
	 *
	 * @param beta the beta
	 * @param point the point
	 * @return the fmeasure model at point
	 */
	public float getFmeasureModelAtPoint(float beta, float point){
		float[] fmeasure = this.getFmeasuresModel(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	/**
	 * Gets the cosine similarity model at point.
	 *
	 * @param point the point
	 * @return the cosine similarity model at point
	 */
	public float getCosineSimilarityModelAtPoint(float point){
		int index = this.getPosition(point);
		return m_cosineSimModel[index];
	}
	
	/**
	 * Gets the one n nmetric model at point.
	 *
	 * @param point the point
	 * @return the one n nmetric model at point
	 */
	public float getOneNNmetricModelAtPoint(float point){
		int index = this.getPosition(point);
		return m_oneNNmetricModel[index];
	}
	
	/**
	 * Gets the one n nmetric norm1 model at point.
	 *
	 * @param point the point
	 * @return the one n nmetric norm1 model at point
	 */
	public float getOneNNmetricNorm1ModelAtPoint(float point){
		int index = this.getPosition(point);
		return m_oneNNmetricNorm1Model[index];
	}
	
	/**
	 * Gets the one n nmetric norm2 model at point.
	 *
	 * @param point the point
	 * @return the one n nmetric norm2 model at point
	 */
	public float getOneNNmetricNorm2ModelAtPoint(float point){
		int index = this.getPosition(point);
		return m_oneNNmetricNorm2Model[index];
	}
	
	/**
	 * Gets the one n nmetric rank model at point.
	 *
	 * @param point the point
	 * @return the one n nmetric rank model at point
	 */
	public float getOneNNmetricRankModelAtPoint(float point){
		int index = this.getPosition(point);
		return m_oneNNmetricRankModel[index];
	}
	
	
	
	// TOPIC level metrics	
	/**
	 * Sets the topic parameters.
	 *
	 * @param urlIds the url ids
	 * @param url2topic the url2topic
	 * @param nDiffTopics the n diff topics
	 * @param topicmatch the topicmatch
	 * @param urlClusteringDict the url clustering dict
	 * @param nDiffClusters the n diff clusters
	 */
	public void setTopicParameters(
			ArrayList<Integer> urlIds, 
			int[] url2topic, int nDiffTopics, float topicmatch,
			HashMap<Integer,Integer> urlClusteringDict, int nDiffClusters){
		m_urlIds = urlIds;
		m_url2topic = url2topic;
		m_nDiffTopics = nDiffTopics;
		m_topicmatch = topicmatch;
		m_UrlClusteringDict = urlClusteringDict;
		m_nDiffClusters = nDiffClusters;
	}
	
	// topic1: based on URL to topic distribution matrix
	/**
	 * Gets the hit ratio top1.
	 *
	 * @return the hit ratio top1
	 */
	public float getHitRatioTop1(){
		return (float)m_hitscoreTop1/(float)m_sequence.size();
	}	
	
	/**
	 * Gets the click soon ratio top1.
	 *
	 * @return the click soon ratio top1
	 */
	public float getClickSoonRatioTop1(){
		return (float)m_clicksoonscoreTop1/(float)m_sequence.size();
	}	
	
	/**
	 * Gets the precissions top1.
	 *
	 * @return the precissions top1
	 */
	public float[] getPrecissionsTop1(){
		return m_precisionTop1;
	}	
	
	/**
	 * Gets the recalls top1.
	 *
	 * @return the recalls top1
	 */
	public float[] getRecallsTop1(){
		return m_recallTop1;
	}
	
	/**
	 * Gets the cosine sims top1.
	 *
	 * @return the cosine sims top1
	 */
	public float[] getcosineSimsTop1(){
		return m_cosineSimTop1;
	}	
	
	/**
	 * Gets the precissions model top1.
	 *
	 * @return the precissions model top1
	 */
	public float[] getPrecissionsModelTop1(){
		return m_precisionModelTop1;
	}	
	
	/**
	 * Gets the recalls model top1.
	 *
	 * @return the recalls model top1
	 */
	public float[] getRecallsModelTop1(){
		return m_recallModelTop1;
	}
	
	/**
	 * Gets the cosine sims model top1.
	 *
	 * @return the cosine sims model top1
	 */
	public float[] getCosineSimsModelTop1(){
		return m_cosineSimModelTop1;
	}
	
	/**
	 * Gets the fmeasures top1.
	 *
	 * @param beta the beta
	 * @return the fmeasures top1
	 */
	public float[] getFmeasuresTop1(float beta){
		return this.getFmeasures(beta, m_precisionTop1, m_recallTop1);		
	}	
	
	/**
	 * Gets the fmeasures model top1.
	 *
	 * @param beta the beta
	 * @return the fmeasures model top1
	 */
	public float[] getFmeasuresModelTop1(float beta){		
		return this.getFmeasures(beta, m_precisionModelTop1, m_recallModelTop1);		
	}	
	
	/**
	 * Gets the precision top at point1.
	 *
	 * @param point the point
	 * @return the precision top at point1
	 */
	public float getPrecisionTopAtPoint1(float point){
		int index = this.getPosition(point);
		return m_precisionTop1[index];
	}	
	
	/**
	 * Gets the recall top at point1.
	 *
	 * @param point the point
	 * @return the recall top at point1
	 */
	public float getRecallTopAtPoint1(float point){
		int index = this.getPosition(point);
		return m_recallTop1[index];
	}
	
	/**
	 * Gets the cosine sim top at point1.
	 *
	 * @param point the point
	 * @return the cosine sim top at point1
	 */
	public float getCosineSimTopAtPoint1(float point){
		int index = this.getPosition(point);
		return m_cosineSimTop1[index];
	}
	
	/**
	 * Gets the fmeasure top at point1.
	 *
	 * @param beta the beta
	 * @param point the point
	 * @return the fmeasure top at point1
	 */
	public float getFmeasureTopAtPoint1(float beta, float point){
		float[] fmeasure = this.getFmeasuresTop1(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}	
	
	/**
	 * Gets the precision model top at point1.
	 *
	 * @param point the point
	 * @return the precision model top at point1
	 */
	public float getPrecisionModelTopAtPoint1(float point){
		int index = this.getPosition(point);
		return m_precisionModelTop1[index];
	}	
	
	/**
	 * Gets the recall model top at point1.
	 *
	 * @param point the point
	 * @return the recall model top at point1
	 */
	public float getRecallModelTopAtPoint1(float point){
		int index = this.getPosition(point);
		return m_recallModelTop1[index];
	}
	
	/**
	 * Gets the cosine sim model top at point1.
	 *
	 * @param point the point
	 * @return the cosine sim model top at point1
	 */
	public float getCosineSimModelTopAtPoint1(float point){
		int index = this.getPosition(point);
		return m_cosineSimModelTop1[index];
	}	
	
	/**
	 * Gets the fmeasure model top at point1.
	 *
	 * @param beta the beta
	 * @param point the point
	 * @return the fmeasure model top at point1
	 */
	public float getFmeasureModelTopAtPoint1(float beta, float point){
		float[] fmeasure = this.getFmeasuresModelTop1(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	// topic2: based on URL clustering
	/**
	 * Gets the hit ratio top2.
	 *
	 * @return the hit ratio top2
	 */
	public float getHitRatioTop2(){
		return (float)m_hitscoreTop2/(float)m_sequence.size();
	}
	
	/**
	 * Gets the click soon ratio top2.
	 *
	 * @return the click soon ratio top2
	 */
	public float getClickSoonRatioTop2(){
		return (float)m_clicksoonscoreTop2/(float)m_sequence.size();
	}
	
	/**
	 * Gets the precissions top2.
	 *
	 * @return the precissions top2
	 */
	public float[] getPrecissionsTop2(){
		return m_precisionTop2;
	}	
	
	/**
	 * Gets the recalls top2.
	 *
	 * @return the recalls top2
	 */
	public float[] getRecallsTop2(){
		return m_recallTop2;
	}
	
	/**
	 * Gets the cosine sims top2.
	 *
	 * @return the cosine sims top2
	 */
	public float[] getCosineSimsTop2(){
		return m_cosineSimTop2;
	}
	
	/**
	 * Gets the precissions model top2.
	 *
	 * @return the precissions model top2
	 */
	public float[] getPrecissionsModelTop2(){
		return m_precisionModelTop2;
	}
	
	/**
	 * Gets the recalls model top2.
	 *
	 * @return the recalls model top2
	 */
	public float[] getRecallsModelTop2(){
		return m_recallModelTop2;
	}
	
	/**
	 * Gets the cosine sims model top2.
	 *
	 * @return the cosine sims model top2
	 */
	public float[] getCosineSimsModelTop2(){
		return m_cosineSimModelTop2;
	}
	
	/**
	 * Gets the fmeasures top2.
	 *
	 * @param beta the beta
	 * @return the fmeasures top2
	 */
	public float[] getFmeasuresTop2(float beta){
		return this.getFmeasures(beta, m_precisionTop2, m_recallTop2);		
	}	
	
	/**
	 * Gets the fmeasures model top2.
	 *
	 * @param beta the beta
	 * @return the fmeasures model top2
	 */
	public float[] getFmeasuresModelTop2(float beta){		
		return this.getFmeasures(beta, m_precisionModelTop2, m_recallModelTop2);		
	}	
	
	/**
	 * Gets the precision top at point2.
	 *
	 * @param point the point
	 * @return the precision top at point2
	 */
	public float getPrecisionTopAtPoint2(float point){
		int index = this.getPosition(point);
		return m_precisionTop2[index];
	}	
	
	/**
	 * Gets the recall top at point2.
	 *
	 * @param point the point
	 * @return the recall top at point2
	 */
	public float getRecallTopAtPoint2(float point){
		int index = this.getPosition(point);
		return m_recallTop2[index];
	}
	
	/**
	 * Gets the cosine sim top at point2.
	 *
	 * @param point the point
	 * @return the cosine sim top at point2
	 */
	public float getCosineSimTopAtPoint2(float point){
		int index = this.getPosition(point);
		return m_cosineSimTop2[index];
	}
	
	/**
	 * Gets the fmeasure top at point2.
	 *
	 * @param beta the beta
	 * @param point the point
	 * @return the fmeasure top at point2
	 */
	public float getFmeasureTopAtPoint2(float beta, float point){
		float[] fmeasure = this.getFmeasuresTop2(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}	
	
	/**
	 * Gets the precision model top at point2.
	 *
	 * @param point the point
	 * @return the precision model top at point2
	 */
	public float getPrecisionModelTopAtPoint2(float point){
		int index = this.getPosition(point);
		return m_precisionModelTop2[index];
	}	
	
	/**
	 * Gets the recall model top at point2.
	 *
	 * @param point the point
	 * @return the recall model top at point2
	 */
	public float getRecallModelTopAtPoint2(float point){
		int index = this.getPosition(point);
		return m_recallModelTop2[index];
	}
	
	/**
	 * Gets the cosine sim model top at point2.
	 *
	 * @param point the point
	 * @return the cosine sim model top at point2
	 */
	public float getCosineSimModelTopAtPoint2(float point){
		int index = this.getPosition(point);
		return m_cosineSimModelTop2[index];
	}	
	
	/**
	 * Gets the fmeasure model top at point2.
	 *
	 * @param beta the beta
	 * @param point the point
	 * @return the fmeasure model top at point2
	 */
	public float getFmeasureModelTopAtPoint2(float beta, float point){
		float[] fmeasure = this.getFmeasuresModelTop2(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	
	
	
	// URL level metrics - homepage always true
	/**
	 * Gets the hit ratio_ ok home.
	 *
	 * @return the hit ratio_ ok home
	 */
	public float getHitRatio_OkHome(){
		return (float)m_hitscore_OkHome/(float)m_sequence.size();
	}	
	
	/**
	 * Gets the click soon ratio_ ok home.
	 *
	 * @return the click soon ratio_ ok home
	 */
	public float getClickSoonRatio_OkHome(){
		return (float)m_clicksoonscore_OkHome/(float)m_sequence.size();
	}	
	
	/**
	 * Gets the precissions_ ok home.
	 *
	 * @return the precissions_ ok home
	 */
	public float[] getPrecissions_OkHome(){
		return m_precision_OkHome;
	}	
	
	/**
	 * Gets the recalls_ ok home.
	 *
	 * @return the recalls_ ok home
	 */
	public float[] getRecalls_OkHome(){
		return m_recall_OkHome;
	}	
	
	/**
	 * Gets the precissions model_ ok home.
	 *
	 * @return the precissions model_ ok home
	 */
	public float[] getPrecissionsModel_OkHome(){
		return m_precisionModel_OkHome;
	}	
	
	/**
	 * Gets the recalls model_ ok home.
	 *
	 * @return the recalls model_ ok home
	 */
	public float[] getRecallsModel_OkHome(){
		return m_recallModel_OkHome;
	}	
	
	/**
	 * Gets the fmeasures_ ok home.
	 *
	 * @param beta the beta
	 * @return the fmeasures_ ok home
	 */
	public float[] getFmeasures_OkHome(float beta){
		return this.getFmeasures(beta, m_precision_OkHome, m_recall_OkHome);
	}	
	
	/**
	 * Gets the fmeasures model_ ok home.
	 *
	 * @param beta the beta
	 * @return the fmeasures model_ ok home
	 */
	public float[] getFmeasuresModel_OkHome(float beta){
		return this.getFmeasures(beta, m_precisionModel_OkHome, m_recallModel_OkHome);
	}	
	
	/**
	 * Gets the precision at point_ ok home.
	 *
	 * @param point the point
	 * @return the precision at point_ ok home
	 */
	public float getPrecisionAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_precision_OkHome[index];
	}	
	
	/**
	 * Gets the recall at point_ ok home.
	 *
	 * @param point the point
	 * @return the recall at point_ ok home
	 */
	public float getRecallAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_recall_OkHome[index];
	}	
	
	/**
	 * Gets the fmeasure at point_ ok home.
	 *
	 * @param beta the beta
	 * @param point the point
	 * @return the fmeasure at point_ ok home
	 */
	public float getFmeasureAtPoint_OkHome(float beta, float point){
		float[] fmeasure = this.getFmeasures_OkHome(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}	
	
	/**
	 * Gets the precision model at point_ ok home.
	 *
	 * @param point the point
	 * @return the precision model at point_ ok home
	 */
	public float getPrecisionModelAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_precisionModel_OkHome[index];
	}	
	
	/**
	 * Gets the recall model at point_ ok home.
	 *
	 * @param point the point
	 * @return the recall model at point_ ok home
	 */
	public float getRecallModelAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_recallModel_OkHome[index];
	}	
	
	/**
	 * Gets the fmeasure model at point_ ok home.
	 *
	 * @param beta the beta
	 * @param point the point
	 * @return the fmeasure model at point_ ok home
	 */
	public float getFmeasureModelAtPoint_OkHome(float beta, float point){
		float[] fmeasure = this.getFmeasuresModel_OkHome(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	
	
	// TOPIC level metrics - - homepage always true
	/**
	 * Gets the hit ratio top_ ok home.
	 *
	 * @return the hit ratio top_ ok home
	 */
	public float getHitRatioTop_OkHome(){
		return (float)m_hitscoreTop_OkHome/(float)m_sequence.size();
	}	
	
	/**
	 * Gets the click soon ratio top_ ok home.
	 *
	 * @return the click soon ratio top_ ok home
	 */
	public float getClickSoonRatioTop_OkHome(){
		return (float)m_clicksoonscoreTop_OkHome/(float)m_sequence.size();
	}	
	
	/**
	 * Gets the precissions top_ ok home.
	 *
	 * @return the precissions top_ ok home
	 */
	public float[] getPrecissionsTop_OkHome(){
		return m_precisionTop_OkHome;
	}	
	
	/**
	 * Gets the recalls top_ ok home.
	 *
	 * @return the recalls top_ ok home
	 */
	public float[] getRecallsTop_OkHome(){
		return m_recallTop_OkHome;
	}	
	
	/**
	 * Gets the precissions model top_ ok home.
	 *
	 * @return the precissions model top_ ok home
	 */
	public float[] getPrecissionsModelTop_OkHome(){
		return m_precisionModelTop_OkHome;
	}	
	
	/**
	 * Gets the recalls model top_ ok home.
	 *
	 * @return the recalls model top_ ok home
	 */
	public float[] getRecallsModelTop_OkHome(){
		return m_recallModelTop_OkHome;
	}	
	
	/**
	 * Gets the fmeasures top_ ok home.
	 *
	 * @param beta the beta
	 * @return the fmeasures top_ ok home
	 */
	public float[] getFmeasuresTop_OkHome(float beta){
		return this.getFmeasures(beta, m_precisionTop_OkHome, m_recallTop_OkHome);
	}	
	
	/**
	 * Gets the fmeasures model top_ ok home.
	 *
	 * @param beta the beta
	 * @return the fmeasures model top_ ok home
	 */
	public float[] getFmeasuresModelTop_OkHome(float beta){		
		return this.getFmeasures(beta, m_precisionModelTop_OkHome, m_recallModelTop_OkHome);	
	}	
	
	/**
	 * Gets the precision top at point_ ok home.
	 *
	 * @param point the point
	 * @return the precision top at point_ ok home
	 */
	public float getPrecisionTopAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_precisionTop_OkHome[index];
	}	
	
	/**
	 * Gets the recall top at point_ ok home.
	 *
	 * @param point the point
	 * @return the recall top at point_ ok home
	 */
	public float getRecallTopAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_recallTop_OkHome[index];
	}	
	
	/**
	 * Gets the fmeasure top at point_ ok home.
	 *
	 * @param beta the beta
	 * @param point the point
	 * @return the fmeasure top at point_ ok home
	 */
	public float getFmeasureTopAtPoint_OkHome(float beta, float point){
		float[] fmeasure = this.getFmeasuresTop_OkHome(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}	
	
	/**
	 * Gets the precision model top at point_ ok home.
	 *
	 * @param point the point
	 * @return the precision model top at point_ ok home
	 */
	public float getPrecisionModelTopAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_precisionModelTop_OkHome[index];
	}	
	
	/**
	 * Gets the recall model top at point_ ok home.
	 *
	 * @param point the point
	 * @return the recall model top at point_ ok home
	 */
	public float getRecallModelTopAtPoint_OkHome(float point){
		int index = this.getPosition(point);
		return m_recallModelTop_OkHome[index];
	}	
	
	/**
	 * Gets the fmeasure model top at point_ ok home.
	 *
	 * @param beta the beta
	 * @param point the point
	 * @return the fmeasure model top at point_ ok home
	 */
	public float getFmeasureModelTopAtPoint_OkHome(float beta, float point){
		float[] fmeasure = this.getFmeasuresModelTop_OkHome(beta);
		int index = this.getPosition(point);
		return fmeasure[index];
	}
	
	
	
	
	
	// Utilities
	
	/**
	 * Gets the position.
	 *
	 * @param point the point
	 * @return the position
	 */
	private int getPosition(float point){
		float len = (float)m_precision.length;
		float index = len * point;
		float maxindex =  (float)m_precision.length - (float)1;
		float index2 = index>maxindex ? maxindex : index;
		int index3 = (int)Math.round(index2);
		return index3;
	}
	
	/**
	 * Gets the fmeasures.
	 *
	 * @param beta the beta
	 * @param prA the pr a
	 * @param reA the re a
	 * @return the fmeasures
	 */
	private float[] getFmeasures(float beta, float[] prA, float[] reA){
		float[] fmeasure = new float[prA.length];
		for(int i=0; i<prA.length; i++){
			float b2 = (float)Math.pow(beta, 2);
			float pr = prA[i];
			float re = reA[i];
			if(pr==-1 || re==-1){
				fmeasure[i] = -1f;
			} else if(pr==(float)0 && re==(float)0){
				fmeasure[i] = 0f;
			} else {
				fmeasure[i] = (1f+b2) * (pr*re) / ((b2*pr)+re);
			}
		}
		return fmeasure;
	}
	
	/**
	 * Checks if is homepage.
	 *
	 * @param urlStr the url str
	 * @return true, if is homepage
	 */
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
