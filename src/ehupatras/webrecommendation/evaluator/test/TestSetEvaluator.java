package ehupatras.webrecommendation.evaluator.test;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;

import ehupatras.markovmodel.MarkovChain;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;

// TODO: Auto-generated Javadoc
/**
 * The Class TestSetEvaluator.
 */
public abstract class TestSetEvaluator {

	// ATTRIBUTES
	
	// test sequences 
	/** The m_sequences. */
	private ArrayList<String[]> m_sequences;
	
	// mode to compute precision and recall
	/** The m_mode pr re. */
	protected int m_modePrRe = 0;
	
	// Validation metrics
	/** The m_points. */
	private float[] m_points = {0.10f, 0.25f, 0.50f, 0.75f, 0.90f};
	
	/** The m_beta. */
	private float m_beta = (float)0.5;
		
	// failure counts
	/** The m_number of recommendations ratio. */
	private float m_numberOfRecommendationsRatio = (float)0;
	
	/** The m_number of failures. */
	private int m_numberOfFailures = 0;
	
	/** The m_failures hist. */
	private float[] m_failuresHist = new float[12]; 
	// topic relate attributes
	/** The m_url ids. */
	private ArrayList<Integer> m_urlIds = null;
		// topic1: based on url to topìc distribution
	/** The m_url2topic. */
		private int[] m_url2topic = null;
	
	/** The m_n diff topics. */
	private int m_nDiffTopics = 10;
	
	/** The m_topicmatch. */
	private float m_topicmatch = 0.5f;
		// topic2: based on url clustering
	/** The m_url clustering dict. */
		protected HashMap<Integer,Integer> m_urlClusteringDict = null;
	
	/** The m_n diff clusters. */
	private int m_nDiffClusters = 10;
	
	// Similarity matrix
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
	
	// URL level metrics - HONEST
	/** The m_hitratio. */
	private float m_hitratio = 0f;
	
	/** The m_clicksoonratio. */
	private float m_clicksoonratio = 0f;
	
	/** The m_precision. */
	private float[] m_precision;
	
	/** The m_recall. */
	private float[] m_recall;
	
	/** The m_fmeasure. */
	private float[] m_fmeasure;
	
	/** The m_cosine similarity. */
	private float[] m_cosineSimilarity;
	
	/** The m_one n nmetric. */
	private float[] m_oneNNmetric;
	
	/** The m_one n nmetric norm1. */
	private float[] m_oneNNmetricNorm1;
	
	/** The m_one n nmetric norm2. */
	private float[] m_oneNNmetricNorm2;
	
	/** The m_one n nmetric rank. */
	private float[] m_oneNNmetricRank;
	
	/** The m_ model precision. */
	private float[] m_ModelPrecision;
	
	/** The m_ model recall. */
	private float[] m_ModelRecall;
	
	/** The m_ model fmeasure. */
	private float[] m_ModelFmeasure;
	
	/** The m_ model cosine similarity. */
	private float[] m_ModelCosineSimilarity;
	
	/** The m_ model one n nmetric. */
	private float[] m_ModelOneNNmetric;
	
	/** The m_ model one n nmetric norm1. */
	private float[] m_ModelOneNNmetricNorm1;
	
	/** The m_ model one n nmetric norm2. */
	private float[] m_ModelOneNNmetricNorm2;
	
	/** The m_ model one n nmetric rank. */
	private float[] m_ModelOneNNmetricRank;
	
	// Topic1 level metrics
	/** The m_hitratio top1. */
	private float m_hitratioTop1 = 0f;
	
	/** The m_clicksoonratio top1. */
	private float m_clicksoonratioTop1 = 0f;
	
	/** The m_precision top1. */
	private float[] m_precisionTop1;
	
	/** The m_recall top1. */
	private float[] m_recallTop1;
	
	/** The m_fmeasure top1. */
	private float[] m_fmeasureTop1;
	
	/** The m_cosine sim top1. */
	private float[] m_cosineSimTop1;
	
	/** The m_ model precision top1. */
	private float[] m_ModelPrecisionTop1;
	
	/** The m_ model recall top1. */
	private float[] m_ModelRecallTop1;
	
	/** The m_ model fmeasure top1. */
	private float[] m_ModelFmeasureTop1;
	
	/** The m_ model cosine sim top1. */
	private float[] m_ModelCosineSimTop1;
	// Topic2 level metrics
	/** The m_hitratio top2. */
	private float m_hitratioTop2 = 0f;
	
	/** The m_clicksoonratio top2. */
	private float m_clicksoonratioTop2 = 0f;
	
	/** The m_precision top2. */
	private float[] m_precisionTop2;
	
	/** The m_recall top2. */
	private float[] m_recallTop2;
	
	/** The m_fmeasure top2. */
	private float[] m_fmeasureTop2;
	
	/** The m_cosine sim top2. */
	private float[] m_cosineSimTop2;
	
	/** The m_ model precision top2. */
	private float[] m_ModelPrecisionTop2;
	
	/** The m_ model recall top2. */
	private float[] m_ModelRecallTop2;
	
	/** The m_ model fmeasure top2. */
	private float[] m_ModelFmeasureTop2;
	
	/** The m_ model cosine sim top2. */
	private float[] m_ModelCosineSimTop2;
	
	// URL level metrics - homepage always true
	/** The m_hitratio_ ok home. */
	private float m_hitratio_OkHome = 0f;
	
	/** The m_clicksoonratio_ ok home. */
	private float m_clicksoonratio_OkHome = 0f;
	
	/** The m_precision_ ok home. */
	private float[] m_precision_OkHome;
	
	/** The m_recall_ ok home. */
	private float[] m_recall_OkHome;
	
	/** The m_fmeasure_ ok home. */
	private float[] m_fmeasure_OkHome;
	
	/** The m_ model precision_ ok home. */
	private float[] m_ModelPrecision_OkHome;
	
	/** The m_ model recall_ ok home. */
	private float[] m_ModelRecall_OkHome;
	
	/** The m_ model fmeasure_ ok home. */
	private float[] m_ModelFmeasure_OkHome;
	// Topic level metrics - homepage always true
	/** The m_hitratio top_ ok home. */
	private float m_hitratioTop_OkHome = 0f;
	
	/** The m_clicksoonratio top_ ok home. */
	private float m_clicksoonratioTop_OkHome = 0f;
	
	/** The m_precision top_ ok home. */
	private float[] m_precisionTop_OkHome;
	
	/** The m_recall top_ ok home. */
	private float[] m_recallTop_OkHome;
	
	/** The m_fmeasure top_ ok home. */
	private float[] m_fmeasureTop_OkHome;
	
	/** The m_ model precision top_ ok home. */
	private float[] m_ModelPrecisionTop_OkHome;
	
	/** The m_ model recall top_ ok home. */
	private float[] m_ModelRecallTop_OkHome;
	
	/** The m_ model fmeasure top_ ok home. */
	private float[] m_ModelFmeasureTop_OkHome;
	
	// To write recommendations
	/** The m_line header. */
	private String m_lineHeader = null;
	
	/** The m_eval writer. */
	private BufferedWriter m_evalWriter = null;
	
	
	
	// CREATOR
	
	/**
	 * Instantiates a new test set evaluator.
	 *
	 * @param sequences the sequences
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 */
	public TestSetEvaluator(ArrayList<String[]> sequences, 
			int modePrRe, URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min){
		this.constructor(sequences, modePrRe, conv, 
				nURLs, urlSimilarityMatrix, 
				urlSimilarityMatrix_Usage, urlSimilarityMatrix_Usage_max, urlSimilarityMatrix_Usage_min);
	}
	
	/**
	 * Constructor.
	 *
	 * @param sequences the sequences
	 * @param modePrRe the mode pr re
	 * @param conv the conv
	 * @param nURLs the n ur ls
	 * @param urlSimilarityMatrix the url similarity matrix
	 * @param urlSimilarityMatrix_Usage the url similarity matrix_ usage
	 * @param urlSimilarityMatrix_Usage_max the url similarity matrix_ usage_max
	 * @param urlSimilarityMatrix_Usage_min the url similarity matrix_ usage_min
	 */
	private void constructor(ArrayList<String[]> sequences, 
			int modePrRe, URLconverterUsaCon conv,
			int nURLs, float[][] urlSimilarityMatrix,
			float[][] urlSimilarityMatrix_Usage, float[] urlSimilarityMatrix_Usage_max, float[] urlSimilarityMatrix_Usage_min){
		m_sequences = sequences;
		m_modePrRe = modePrRe;
		m_conv = conv;
		m_nURLs = nURLs;
		m_UrlSimilarityMatrix_Content = urlSimilarityMatrix;
		m_UrlSimilarityMatrix_Usage = urlSimilarityMatrix_Usage;
		m_UrlSimilarityMatrix_Usage_max = urlSimilarityMatrix_Usage_max;
		m_UrlSimilarityMatrix_Usage_min = urlSimilarityMatrix_Usage_min;
		this.constructor2();
	}
	
	/**
	 * Constructor2.
	 */
	private void constructor2(){
		// HONEST
		m_precision = new float[m_points.length];
		m_recall = new float[m_points.length];
		m_fmeasure = new float[m_points.length];
		m_cosineSimilarity = new float[m_points.length];
		m_oneNNmetric = new float[m_points.length];
		m_oneNNmetricNorm1 = new float[m_points.length];
		m_oneNNmetricNorm2 = new float[m_points.length];
		m_oneNNmetricRank = new float[m_points.length];
		m_ModelPrecision = new float[m_points.length];
		m_ModelRecall = new float[m_points.length];
		m_ModelFmeasure = new float[m_points.length];
		m_ModelCosineSimilarity = new float[m_points.length];
		m_ModelOneNNmetric = new float[m_points.length];
		m_ModelOneNNmetricNorm1 = new float[m_points.length];
		m_ModelOneNNmetricNorm2 = new float[m_points.length];
		m_ModelOneNNmetricRank = new float[m_points.length];
		
		m_precisionTop1 = new float[m_points.length];
		m_recallTop1 = new float[m_points.length];
		m_fmeasureTop1 = new float[m_points.length];
		m_cosineSimTop1 = new float[m_points.length];
		m_ModelPrecisionTop1 = new float[m_points.length];
		m_ModelRecallTop1 = new float[m_points.length];
		m_ModelFmeasureTop1 = new float[m_points.length];
		m_ModelCosineSimTop1 = new float[m_points.length];
		m_precisionTop2 = new float[m_points.length];
		m_recallTop2 = new float[m_points.length];
		m_fmeasureTop2 = new float[m_points.length];
		m_cosineSimTop2 = new float[m_points.length];
		m_ModelPrecisionTop2 = new float[m_points.length];
		m_ModelRecallTop2 = new float[m_points.length];
		m_ModelFmeasureTop2 = new float[m_points.length];
		m_ModelCosineSimTop2 = new float[m_points.length];
		
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
	
	/**
	 * Gets the sequence evaluator.
	 *
	 * @param sequence the sequence
	 * @return the sequence evaluator
	 */
	public abstract SequenceEvaluator getSequenceEvaluator(String[] sequence);
	
	
	
	// BODY
	
	/**
	 * Compute evaluation.
	 *
	 * @param mode the mode
	 * @param nrecos the nrecos
	 * @param seed the seed
	 * @param homepages the homepages
	 * @param markovchain the markovchain
	 */
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
		float[] cosineSim = new float[m_points.length];
		float[] oneNNmetric = new float[m_points.length];
		float[] oneNNmetricNorm1 = new float[m_points.length];
		float[] oneNNmetricNorm2 = new float[m_points.length];
		float[] oneNNmetricRank = new float[m_points.length];
		float[] modelPrecision = new float[m_points.length];
		float[] modelRecall = new float[m_points.length];
		float[] modelFmeasure = new float[m_points.length];
		float[] modelCosineSim = new float[m_points.length];
		float[] modelOneNNmetric = new float[m_points.length];
		float[] modelOneNNmetricNorm1 = new float[m_points.length];
		float[] modelOneNNmetricNorm2 = new float[m_points.length];
		float[] modelOneNNmetricRank = new float[m_points.length];
		
		// TOPIC level metrics
		float hitratioTop1 = 0f;
		float clicksoonratioTop1 = 0f;
		float[] precissionTop1 = new float[m_points.length];
		float[] recallTop1 = new float[m_points.length];
		float[] fmeasureTop1 = new float[m_points.length];
		float[] cosineSimTop1 = new float[m_points.length];
		float[] modelPrecisionTop1 = new float[m_points.length];
		float[] modelRecallTop1 = new float[m_points.length];
		float[] modelFmeasureTop1 = new float[m_points.length];
		float[] modelCosineSimTop1 = new float[m_points.length];
		
		// TOPIC2 level metrics
		float hitratioTop2 = 0f;
		float clicksoonratioTop2 = 0f;
		float[] precissionTop2 = new float[m_points.length];
		float[] recallTop2 = new float[m_points.length];
		float[] fmeasureTop2 = new float[m_points.length];
		float[] cosineSimTop2 = new float[m_points.length];
		float[] modelPrecisionTop2 = new float[m_points.length];
		float[] modelRecallTop2 = new float[m_points.length];
		float[] modelFmeasureTop2 = new float[m_points.length];
		float[] modelCosineSimTop2 = new float[m_points.length];
		
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
		
		int[] numOfSequencesA = new int[m_points.length];
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
					m_url2topic, m_nDiffTopics, m_topicmatch,
					m_urlClusteringDict, m_nDiffClusters);
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
			
			
			// in the case we are computing metrics in a relaxed way
			boolean noCountSeq[] = new boolean[m_points.length];
			Arrays.fill(noCountSeq, false);
			
			// URL level metrics - HONEST
			hitratio = hitratio + seqEv.getHitRatio();
			clicksoonratio = clicksoonratio + seqEv.getClickSoonRatio();
			for(int j=0; j<m_points.length; j++){
				// check if we need to count the 
				float val = seqEv.getPrecisionAtPoint(m_points[j]);
				noCountSeq[j] = (val!=-1f);
				
				// prediction
				if(noCountSeq[j]){
					precission[j]       = precission[j]       + seqEv.getPrecisionAtPoint(m_points[j]);
					recall[j]           = recall[j]           + seqEv.getRecallAtPoint(m_points[j]);
					fmeasure[j]         = fmeasure[j]         + seqEv.getFmeasureAtPoint(m_beta, m_points[j]);
					oneNNmetric[j]      = oneNNmetric[j]      + seqEv.getOneNNmetricAtPoint(m_points[j]);
					oneNNmetricNorm1[j] = oneNNmetricNorm1[j] + seqEv.getOneNNmetricNorm1AtPoint(m_points[j]);
					oneNNmetricNorm2[j] = oneNNmetricNorm2[j] + seqEv.getOneNNmetricNorm2AtPoint(m_points[j]);
					oneNNmetricRank[j]  = oneNNmetricRank[j]  + seqEv.getOneNNmetricRankAtPoint(m_points[j]);
				}
				cosineSim[j]      = cosineSim[j]      + seqEv.getCosineSimilarityAtPoint(m_points[j]);
				
				// model
				if(noCountSeq[j]){
					modelPrecision[j]        = modelPrecision[j]        + seqEv.getPrecisionModelAtPoint(m_points[j]);
					modelRecall[j]           = modelRecall[j]           + seqEv.getRecallModelAtPoint(m_points[j]);
					modelFmeasure[j]         = modelFmeasure[j]         + seqEv.getFmeasureModelAtPoint(m_beta, m_points[j]);
					modelOneNNmetric[j]      = modelOneNNmetric[j]      + seqEv.getOneNNmetricModelAtPoint(m_points[j]);
					modelOneNNmetricNorm1[j] = modelOneNNmetricNorm1[j] + seqEv.getOneNNmetricNorm1ModelAtPoint(m_points[j]);
					modelOneNNmetricNorm2[j] = modelOneNNmetricNorm2[j] + seqEv.getOneNNmetricNorm2ModelAtPoint(m_points[j]);
					modelOneNNmetricRank[j]  = modelOneNNmetricRank[j]  + seqEv.getOneNNmetricRankModelAtPoint(m_points[j]);
				}
				modelCosineSim[j] = modelCosineSim[j] + seqEv.getCosineSimilarityModelAtPoint(m_points[j]);
				
				// count number of sequences
				if(noCountSeq[j]){
					numOfSequencesA[j]++;
				}
			}
			
			// TOPIC1 level metrics
			hitratioTop1 = hitratioTop1 + seqEv.getHitRatioTop1();
			clicksoonratioTop1 = clicksoonratioTop1 + seqEv.getClickSoonRatioTop1();
			for(int j=0; j<m_points.length; j++){
				if(noCountSeq[j]){
					precissionTop1[j]     = precissionTop1[j]     + seqEv.getPrecisionTopAtPoint1(m_points[j]);
					recallTop1[j]         = recallTop1[j]         + seqEv.getRecallTopAtPoint1(m_points[j]);
					fmeasureTop1[j]       = fmeasureTop1[j]       + seqEv.getFmeasureTopAtPoint1(m_beta, m_points[j]);
				}
				cosineSimTop1[j]      = cosineSimTop1[j]      + seqEv.getCosineSimTopAtPoint1(m_points[j]);
				
				if(noCountSeq[j]){
					modelPrecisionTop1[j] = modelPrecisionTop1[j] + seqEv.getPrecisionModelTopAtPoint1(m_points[j]);
					modelRecallTop1[j]    = modelRecallTop1[j]    + seqEv.getRecallModelTopAtPoint1(m_points[j]);
					modelFmeasureTop1[j]  = modelFmeasureTop1[j]  + seqEv.getFmeasureModelTopAtPoint1(m_beta, m_points[j]);
				}
				modelCosineSimTop1[j] = modelCosineSimTop1[j] + seqEv.getCosineSimModelTopAtPoint1(m_points[j]);
			}
			
			// TOPIC2 level metrics
			hitratioTop2 = hitratioTop2 + seqEv.getHitRatioTop2();
			clicksoonratioTop2 = clicksoonratioTop2 + seqEv.getClickSoonRatioTop2();
			for(int j=0; j<m_points.length; j++){
				if(noCountSeq[j]){
					precissionTop2[j]     = precissionTop2[j]     + seqEv.getPrecisionTopAtPoint2(m_points[j]);
					recallTop2[j]         = recallTop2[j]         + seqEv.getRecallTopAtPoint2(m_points[j]);
					fmeasureTop2[j]       = fmeasureTop2[j]       + seqEv.getFmeasureTopAtPoint2(m_beta, m_points[j]);
				}
				cosineSimTop2[j]      = cosineSimTop2[j]      + seqEv.getCosineSimTopAtPoint2(m_points[j]);
				
				if(noCountSeq[j]){
					modelPrecisionTop2[j] = modelPrecisionTop2[j] + seqEv.getPrecisionModelTopAtPoint2(m_points[j]);
					modelRecallTop2[j]    = modelRecallTop2[j]    + seqEv.getRecallModelTopAtPoint2(m_points[j]);
					modelFmeasureTop2[j]  = modelFmeasureTop2[j]  + seqEv.getFmeasureModelTopAtPoint2(m_beta, m_points[j]);
				}
				modelCosineSimTop2[j] = modelCosineSimTop2[j] + seqEv.getCosineSimModelTopAtPoint2(m_points[j]);
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
			float seqlen = m_modePrRe==1 ? (float)numOfSequencesA[j] : (float)m_sequences.size();
			
			m_precision[j]        = precission[j]       / seqlen;
			m_recall[j]           = recall[j]           / seqlen;
			m_fmeasure[j]         = fmeasure[j]         / seqlen;
			m_cosineSimilarity[j] = cosineSim[j]        / (float)m_sequences.size();
			m_oneNNmetric[j]      = oneNNmetric[j]      / seqlen;
			m_oneNNmetricNorm1[j] = oneNNmetricNorm1[j] / seqlen;
			m_oneNNmetricNorm2[j] = oneNNmetricNorm2[j] / seqlen;
			m_oneNNmetricRank[j]  = oneNNmetricRank[j]  / seqlen;
			
			m_ModelPrecision[j]        = modelPrecision[j]        / seqlen;
			m_ModelRecall[j]           = modelRecall[j]           / seqlen;
			m_ModelFmeasure[j]         = modelFmeasure[j]         / seqlen;
			m_ModelCosineSimilarity[j] = modelCosineSim[j]        / (float)m_sequences.size();
			m_ModelOneNNmetric[j]      = modelOneNNmetric[j]      / seqlen;
			m_ModelOneNNmetricNorm1[j] = modelOneNNmetricNorm1[j] / seqlen;
			m_ModelOneNNmetricNorm2[j] = modelOneNNmetricNorm2[j] / seqlen;
			m_ModelOneNNmetricRank[j]  = modelOneNNmetricRank[j]  / seqlen;
		}
		
		// TOPIC1 level metrics - HONEST
		m_hitratioTop1 = hitratioTop1/(float)m_sequences.size();
		m_clicksoonratioTop1 = clicksoonratioTop1/(float)m_sequences.size();
		for(int j=0; j<m_points.length; j++){
			float seqlen = m_modePrRe==1 ? (float)numOfSequencesA[j] : (float)m_sequences.size();
			
			m_precisionTop1[j]      = precissionTop1[j]     / seqlen;
			m_recallTop1[j]         = recallTop1[j]         / seqlen;
			m_fmeasureTop1[j]       = fmeasureTop1[j]       / seqlen;
			m_cosineSimTop1[j]      = cosineSimTop1[j]      / (float)m_sequences.size();
			
			m_ModelPrecisionTop1[j] = modelPrecisionTop1[j] / seqlen;
			m_ModelRecallTop1[j]    = modelRecallTop1[j]    / seqlen;
			m_ModelFmeasureTop1[j]  = modelFmeasureTop1[j]  / seqlen;
			m_ModelCosineSimTop1[j] = modelCosineSimTop1[j] / (float)m_sequences.size();
		}
		
		// TOPIC2 level metrics - HONEST
		m_hitratioTop2 = hitratioTop2/(float)m_sequences.size();
		m_clicksoonratioTop2 = clicksoonratioTop2/(float)m_sequences.size();
		for(int j=0; j<m_points.length; j++){
			float seqlen = m_modePrRe==1 ? (float)numOfSequencesA[j] : (float)m_sequences.size();
			
			m_precisionTop2[j]      = precissionTop2[j]     / seqlen;
			m_recallTop2[j]         = recallTop2[j]         / seqlen;
			m_fmeasureTop2[j]       = fmeasureTop2[j]       / seqlen;
			m_cosineSimTop2[j]      = cosineSimTop2[j]      / (float)m_sequences.size();
			
			m_ModelPrecisionTop2[j] = modelPrecisionTop2[j] / seqlen;
			m_ModelRecallTop2[j]    = modelRecallTop2[j]    / seqlen;
			m_ModelFmeasureTop2[j]  = modelFmeasureTop2[j]  / seqlen;
			m_ModelCosineSimTop2[j] = modelCosineSimTop2[j] / (float)m_sequences.size();
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
	
	/**
	 * Sets the confusion points.
	 *
	 * @param confusionPoints the new confusion points
	 */
	public void setConfusionPoints(float[] confusionPoints){
		m_points = confusionPoints;
		this.constructor2();
	}
	
	/**
	 * Sets the fmeasure beta.
	 *
	 * @param beta the new fmeasure beta
	 */
	public void setFmeasureBeta(float beta){
		m_beta = beta;
	}
	
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
		m_urlClusteringDict = urlClusteringDict;
		m_nDiffClusters = nDiffClusters;
	}
	
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
	
	// GET class attributes
	
	/**
	 * Gets the number of sequences.
	 *
	 * @return the number of sequences
	 */
	public int getNumberOfSequences(){
		return m_sequences.size();
	}
	
	/**
	 * Gets the number of clicks.
	 *
	 * @return the number of clicks
	 */
	public int getNumberOfClicks(){
		int nURLs = 0;
		for(int i=0; i<m_sequences.size(); i++){
			nURLs = nURLs + m_sequences.get(i).length;
		}
		return nURLs;
	}
	
	/**
	 * Gets the number of recommendations ratio.
	 *
	 * @return the number of recommendations ratio
	 */
	public float getNumberOfRecommendationsRatio(){
		return m_numberOfRecommendationsRatio;
	}
	
	/**
	 * Gets the number of failures.
	 *
	 * @return the number of failures
	 */
	public int getNumberOfFailures(){
		return m_numberOfFailures;
	}
	
	/**
	 * Gets the failures histogram.
	 *
	 * @return the failures histogram
	 */
	public float[] getFailuresHistogram(){
		return m_failuresHist;
	}
	
	
	// URL level attributes - HONEST
	
	/**
	 * Gets the hit ratio.
	 *
	 * @return the hit ratio
	 */
	public float getHitRatio(){
		return m_hitratio;
	}
	
	/**
	 * Gets the click soon ratio.
	 *
	 * @return the click soon ratio
	 */
	public float getClickSoonRatio(){
		return m_clicksoonratio;
	}
	
	/**
	 * Gets the precisions.
	 *
	 * @return the precisions
	 */
	public float[] getPrecisions(){
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
	 * @return the fmeasures
	 */
	public float[] getFmeasures(){
		return m_fmeasure;
	}
	
	/**
	 * Gets the cosine similarity.
	 *
	 * @return the cosine similarity
	 */
	public float[] getCosineSimilarity(){
		return m_cosineSimilarity;
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
	 * Gets the model precisions.
	 *
	 * @return the model precisions
	 */
	public float[] getModelPrecisions(){
		return m_ModelPrecision;
	}
	
	/**
	 * Gets the model recalls.
	 *
	 * @return the model recalls
	 */
	public float[] getModelRecalls(){
		return m_ModelRecall;
	}
	
	/**
	 * Gets the model fmeasures.
	 *
	 * @return the model fmeasures
	 */
	public float[] getModelFmeasures(){
		return m_ModelFmeasure;
	}
	
	/**
	 * Gets the model cosine similarity.
	 *
	 * @return the model cosine similarity
	 */
	public float[] getModelCosineSimilarity(){
		return m_ModelCosineSimilarity;
	}
	
	/**
	 * Gets the model one n nmetric.
	 *
	 * @return the model one n nmetric
	 */
	public float[] getModelOneNNmetric(){
		return m_ModelOneNNmetric;
	}
	
	/**
	 * Gets the model one n nmetric norm1.
	 *
	 * @return the model one n nmetric norm1
	 */
	public float[] getModelOneNNmetricNorm1(){
		return m_ModelOneNNmetricNorm1;
	}
	
	/**
	 * Gets the model one n nmetric norm2.
	 *
	 * @return the model one n nmetric norm2
	 */
	public float[] getModelOneNNmetricNorm2(){
		return m_ModelOneNNmetricNorm2;
	}
	
	/**
	 * Gets the model one n nmetric rank.
	 *
	 * @return the model one n nmetric rank
	 */
	public float[] getModelOneNNmetricRank(){
		return m_ModelOneNNmetricRank;
	}
	
	
	// TOPIC1 level attributes
	
	/**
	 * Gets the hit ratio top1.
	 *
	 * @return the hit ratio top1
	 */
	public float getHitRatioTop1(){
		return m_hitratioTop1;
	}
	
	/**
	 * Gets the click soon ratio top1.
	 *
	 * @return the click soon ratio top1
	 */
	public float getClickSoonRatioTop1(){
		return m_clicksoonratioTop1;
	}
	
	/**
	 * Gets the precisions top1.
	 *
	 * @return the precisions top1
	 */
	public float[] getPrecisionsTop1(){
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
	 * Gets the fmeasures top1.
	 *
	 * @return the fmeasures top1
	 */
	public float[] getFmeasuresTop1(){
		return m_fmeasureTop1;
	}
	
	/**
	 * Gets the cosine sim top1.
	 *
	 * @return the cosine sim top1
	 */
	public float[] getCosineSimTop1(){
		return m_cosineSimTop1;
	}
	
	/**
	 * Gets the model precisions top1.
	 *
	 * @return the model precisions top1
	 */
	public float[] getModelPrecisionsTop1(){
		return m_ModelPrecisionTop1;
	}
	
	/**
	 * Gets the model recalls top1.
	 *
	 * @return the model recalls top1
	 */
	public float[] getModelRecallsTop1(){
		return m_ModelRecallTop1;
	}
	
	/**
	 * Gets the model fmeasures top1.
	 *
	 * @return the model fmeasures top1
	 */
	public float[] getModelFmeasuresTop1(){
		return m_ModelFmeasureTop1;
	}
	
	/**
	 * Gets the model cosine sim top1.
	 *
	 * @return the model cosine sim top1
	 */
	public float[] getModelCosineSimTop1(){
		return m_ModelCosineSimTop1;
	}
	
	
	// TOPIC2 level attributes
	
	/**
	 * Gets the hit ratio top2.
	 *
	 * @return the hit ratio top2
	 */
	public float getHitRatioTop2(){
		return m_hitratioTop2;
	}
	
	/**
	 * Gets the click soon ratio top2.
	 *
	 * @return the click soon ratio top2
	 */
	public float getClickSoonRatioTop2(){
		return m_clicksoonratioTop2;
	}
	
	/**
	 * Gets the precisions top2.
	 *
	 * @return the precisions top2
	 */
	public float[] getPrecisionsTop2(){
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
	 * Gets the fmeasures top2.
	 *
	 * @return the fmeasures top2
	 */
	public float[] getFmeasuresTop2(){
		return m_fmeasureTop2;
	}
	
	/**
	 * Gets the cosine sim top2.
	 *
	 * @return the cosine sim top2
	 */
	public float[] getCosineSimTop2(){
		return m_cosineSimTop2;
	}
	
	/**
	 * Gets the model precisions top2.
	 *
	 * @return the model precisions top2
	 */
	public float[] getModelPrecisionsTop2(){
		return m_ModelPrecisionTop2;
	}
	
	/**
	 * Gets the model recalls top2.
	 *
	 * @return the model recalls top2
	 */
	public float[] getModelRecallsTop2(){
		return m_ModelRecallTop2;
	}
	
	/**
	 * Gets the model fmeasures top2.
	 *
	 * @return the model fmeasures top2
	 */
	public float[] getModelFmeasuresTop2(){
		return m_ModelFmeasureTop2;
	}
	
	/**
	 * Gets the model cosine sim top2.
	 *
	 * @return the model cosine sim top2
	 */
	public float[] getModelCosineSimTop2(){
		return m_ModelCosineSimTop2;
	}
	
	
	// URL level attributes - homepage always true
	
	/**
	 * Gets the hit ratio_ ok home.
	 *
	 * @return the hit ratio_ ok home
	 */
	public float getHitRatio_OkHome(){
		return m_hitratio_OkHome;
	}
	
	/**
	 * Gets the click soon ratio_ ok home.
	 *
	 * @return the click soon ratio_ ok home
	 */
	public float getClickSoonRatio_OkHome(){
		return m_clicksoonratio_OkHome;
	}
	
	/**
	 * Gets the precisions_ ok home.
	 *
	 * @return the precisions_ ok home
	 */
	public float[] getPrecisions_OkHome(){
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
	 * Gets the fmeasures_ ok home.
	 *
	 * @return the fmeasures_ ok home
	 */
	public float[] getFmeasures_OkHome(){
		return m_fmeasure_OkHome;
	}
	
	/**
	 * Gets the model precisions_ ok home.
	 *
	 * @return the model precisions_ ok home
	 */
	public float[] getModelPrecisions_OkHome(){
		return m_ModelPrecision_OkHome;
	}
	
	/**
	 * Gets the model recalls_ ok home.
	 *
	 * @return the model recalls_ ok home
	 */
	public float[] getModelRecalls_OkHome(){
		return m_ModelRecall_OkHome;
	}
	
	/**
	 * Gets the model fmeasures_ ok home.
	 *
	 * @return the model fmeasures_ ok home
	 */
	public float[] getModelFmeasures_OkHome(){
		return m_ModelFmeasure_OkHome;
	}
	
	
	// TOPIC level attributes - homepage always true
	
	/**
	 * Gets the hit ratio top_ ok home.
	 *
	 * @return the hit ratio top_ ok home
	 */
	public float getHitRatioTop_OkHome(){
		return m_hitratioTop_OkHome;
	}
	
	/**
	 * Gets the click soon ratio top_ ok home.
	 *
	 * @return the click soon ratio top_ ok home
	 */
	public float getClickSoonRatioTop_OkHome(){
		return m_clicksoonratioTop_OkHome;
	}
	
	/**
	 * Gets the precisions top_ ok home.
	 *
	 * @return the precisions top_ ok home
	 */
	public float[] getPrecisionsTop_OkHome(){
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
	 * Gets the fmeasures top_ ok home.
	 *
	 * @return the fmeasures top_ ok home
	 */
	public float[] getFmeasuresTop_OkHome(){
		return m_fmeasureTop_OkHome;
	}
	
	/**
	 * Gets the model precisions top_ ok home.
	 *
	 * @return the model precisions top_ ok home
	 */
	public float[] getModelPrecisionsTop_OkHome(){
		return m_ModelPrecisionTop_OkHome;
	}
	
	/**
	 * Gets the model recalls top_ ok home.
	 *
	 * @return the model recalls top_ ok home
	 */
	public float[] getModelRecallsTop_OkHome(){
		return m_ModelRecallTop_OkHome;
	}
	
	/**
	 * Gets the model fmeasures top_ ok home.
	 *
	 * @return the model fmeasures top_ ok home
	 */
	public float[] getModelFmeasuresTop_OkHome(){
		return m_ModelFmeasureTop_OkHome;
	}
	
	
	// Utilities
	
	/**
	 * Write results.
	 */
	/*public void writeResults(){
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
		System.out.println();*/
	public void writeResults(int j){
		//VERSION: LIERNI
		// write headers
		System.out.print(" ,fold_" + j);
		for (int l=0; l<19; l++){
			System.out.print(", ");
		}
		
		// write results
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
		
		for (int l=0; l<15; l++){
			System.out.print(", ");
		}
		
		for(int i=0; i<m_points.length; i++){
			System.out.print("," + m_ModelPrecision[i]);
		}
		for(int i=0; i<m_points.length; i++){
			System.out.print("," + m_ModelRecall[i]);
		}
		for(int i=0; i<m_points.length; i++){
			System.out.print("," + m_ModelFmeasure[i]);
		}
		
		
		
		System.out.println();
	}

}
