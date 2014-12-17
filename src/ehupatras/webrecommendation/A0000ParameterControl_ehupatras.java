package ehupatras.webrecommendation;

import java.io.BufferedWriter;
import java.util.ArrayList;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorClustHclust;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorHMM;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMarkovChain;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorSeqMinMSAWseq;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorSeqMinSPADE;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorSuffixTreeGlobal;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorModularGST;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorModularSpadeST;
import angelu.webrecommendation.A0000ParameterControl_angelu;
import angelu.webrecommendation.A012MainClassDistanceMatrixED;

public class A0000ParameterControl_ehupatras extends A0000ParameterControl_angelu {

	// Model Evaluator
	protected ModelEvaluatorMarkovChain m_modelevMC = null;
	protected ModelEvaluatorSuffixTreeGlobal m_modelevSTG = null;
	protected ModelEvaluatorClustHclust m_modelevH = null;
	protected ModelEvaluatorSeqMinMSAWseq m_modelevCMS = null;
	protected ModelEvaluatorSeqMinSPADE m_modelevCSS = null;
	protected ModelEvaluatorModularGST m_modelevMGST = null;
	protected ModelEvaluatorModularSpadeST m_modelevMSpST = null;
	protected ModelEvaluatorHMM m_modelevHMM = null;
	
	
	// SYSTEM PARAMETERS //
	
	public void initializeSystemParameters(){
		// hold-out
		m_nFold = 10;
		m_ptrain = 7;
		m_pval = 0;
		m_ptest = 3;
		
		// PAM: k
		//m_ks = new int[]{150, 200, 250, 300};
		m_ks = new int[]{150, 200, 250};
		
		// SPADE: minimum support
		//m_seqweights = new float[]{0.10f, 0.15f, 0.20f};
		m_seqweights = new float[]{0.20f};
		
		// Weights among roles
		m_rolesW = new float[][]{	{ 0f, 0f, 0f},
	  								{ 0f, 0f, 0f},
	  								{ 0f, 0f, 0f}};
		
		// F-measure: beta
		m_beta = 0.5f;
		
		// Navigation stages to analyze
		m_confusionPoints = new float[]{0.25f,0.50f,0.75f};
		
		// topic match
		m_topicmatch = 1f;
		
		// number of recommendations
		//m_nrecsA = new int[]{2,3,4,5,10,15,20,50};
		m_nrecsA = new int[]{2,3,4,5,10,15};
	}
	
	// DB with topics
	protected float m_urlsEqualnessThreshold = 0.5f;
	
	// Hclust: cutthA
	//float[] cutthA = {0.1f, 0.2f, 0.4f, 0.6f, 0.8f};
	//float[] cutthA = {4f, 10f, 15f, 20f, 25f};
	protected float[] m_cutthA = {4f, 10f, 15f};
	protected String[] m_linkages = 
		{"ehupatras.clustering.sapehac.agglomeration.AverageLinkage",
		 "ehupatras.clustering.sapehac.agglomeration.CentroidLinkage",
		 "ehupatras.clustering.sapehac.agglomeration.CompleteLinkage",
		 "ehupatras.clustering.sapehac.agglomeration.MedianLinkage",
		 "ehupatras.clustering.sapehac.agglomeration.SingleLinkage",
		 "ehupatras.clustering.sapehac.agglomeration.WardLinkage"};
	
	
	// Modular
	protected int[] m_knnA = {1,2,5,10};
	
	//////////////////////////////////
	
	
	
	// CREATOR
	
	public A0000ParameterControl_ehupatras(){
		this.exampleParameters();
		this.initializeSystemParameters();
		super.initializeStructures();
	}
	public A0000ParameterControl_ehupatras(String[] args){
		this.initializeSystemParameters();
		if(args.length==0){
			this.exampleParameters2();
		} else {
			super.readParameters(args);
		}
		super.initializeStructures();
	}
	
	
	
	// FUNCTIONS
	
	public void exampleParameters2(){
		m_base = "experiments_ehupatras";
		
		m_preprocessingWD = m_base + "/01_preprocess";
		m_logfile = "/log500000.txt";
		
		m_url2topicFile = "/Content/Topic/URLs_to_topic_th0/URLs_to_topic_TestuHutsa_th0_usageID.txt";
		m_url2url_DM = "/Content/Topic/DM_similarityHellingerTopic1TestuHutsa_usageID.txt";
		
		m_urlSimilarityMatrix = m_preprocessingWD + "/Content/Topic/similarityHellingerTopic1TestuHutsa.txt";
		m_urlRelationMatrix = m_preprocessingWD + "/Content/Topic/relationMatrixTopic1TestuHutsa.txt";
		m_clusterPartitionFile = m_preprocessingWD + "/Content/Topic/clusterPartitions/ClusterPartitionTestuHutsa.txt";
		m_usage2contentFile = m_preprocessingWD + "/Content/usa2cont.csv";
		
		m_databaseWD = m_base + "/02_DATABASE_5";
		//m_dmWD = "/DM_04_edit";
		//m_dmWD = "/DM_00_norole_dist";
		//m_dmWD = "/DM_00_norole_dist_TopicCont";
		m_dmWD = "/DM_04_edit";
		
		m_validationWD = m_base + "/03_VALIDATION_5";
		//m_clustWD = "/pam_DM_04_edit";
		//m_profiWD = "/pam_DM_04_edit/spade1";
		m_clustWD = "/pam_DM_04_edit";
		m_profiWD = m_profiWD + "/spade1";
		m_evalFile = "/evaluation.txt";
		
		m_noProposeUrls = new ArrayList<Integer>();
		
		m_modePrRe = 0;
	}
	
	public void exampleParameters(){
		m_base = "experiments_ehupatras_2";
		
		m_preprocessingWD = m_base + "/01_preprocess";
		m_logfile = "/log20000.log";
		
		m_url2topicFile = "/Content/Topic/URLs_to_topic_th0/URLs_to_topic_TestuHutsa_th0_usageID.txt";
		m_url2url_DM = "/Content/Topic/DM_similarityHellingerTopic1TestuHutsa_usageID.txt";
		
		m_urlSimilarityMatrix = m_preprocessingWD + "/Content/Topic/similarityHellingerTopic1TestuHutsa.txt";
		m_urlRelationMatrix = m_preprocessingWD + "/Content/Topic/relationMatrixTopic1TestuHutsa.txt";
		m_clusterPartitionFile = m_preprocessingWD + "/Content/Topic/clusterPartitions/ClusterPartitionTestuHutsa.txt";
		m_usage2contentFile = m_preprocessingWD + "/Content/usa2cont.csv";
		
		m_databaseWD = m_base + "/02_DATABASE_5";
		//m_dmWD = "/DM_04_edit";
		//m_dmWD = "/DM_00_norole_dist";
		//m_dmWD = "/DM_00_norole_dist_TopicCont";
		m_dmWD = "/DM_04_edit_TopicCont";
		
		m_validationWD = m_base + "/03_VALIDATION_5";
		//m_clustWD = "/pam_DM_04_edit";
		//m_profiWD = "/pam_DM_04_edit/spade1";
		m_clustWD = "/pam_DM_04_edit_TopicCont";
		m_profiWD = m_profiWD + "/spade1";
		m_evalFile = "/evaluation.txt";

		m_noProposeUrls = new ArrayList<Integer>();
		
		m_modePrRe = 0;

		// no from command line
		m_ks = new int[]{200};
	}
	
	// preprocessing
	
	public void preprocessLogs(){
		A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		preprocess.preprocessLogs(m_preprocessingWD, m_logfile);
	}
	
	// distance matrix
	
	public void createDM(String strategyNormalize, float[][] rolesW, 
			String dmWD, int[] sessionBreakers){
		this.loadDatabase();
		dmWD = dmWD.equals("-") ? m_dmWD : dmWD;
		if(strategyNormalize.equals("SimilarityMatrixAsDataMatrix")){
			A010MainClassDistanceMatrixLGEuclidean dm = new A010MainClassDistanceMatrixLGEuclidean();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
				m_sampleSessionIDs, m_sequencesUHC, 
				rolesW);
		} else if(strategyNormalize.equals("SimilarityMatrixNormalize")) {
			A011MainClassDistanceMatrixLGInverse dm = new A011MainClassDistanceMatrixLGInverse();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
					m_sampleSessionIDs, m_sequencesUHC, 
					rolesW);
		} else if(strategyNormalize.equals("SimilarityMatrixNormalize_Split")){
			A011MainClassDistanceMatrixLGInverseSplit dm = new A011MainClassDistanceMatrixLGInverseSplit();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
					m_sampleSessionIDs, m_sequencesUHC, 
					rolesW, sessionBreakers);
		} else if(strategyNormalize.equals("EditDistance")){
			A012MainClassDistanceMatrixED dm = new A012MainClassDistanceMatrixED();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
					m_sampleSessionIDs, m_sequencesUHC, 
					rolesW);
		} else if(strategyNormalize.equals("EditDistance_Split")){
			A012MainClassDistanceMatrixEDSplit dm = new A012MainClassDistanceMatrixEDSplit();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
					m_sampleSessionIDs, m_sequencesUHC, 
					rolesW, sessionBreakers);
		} else if(strategyNormalize.equals("NCD_bzip2")){
			A013MainClassDistanceMatrixNcdBzip2 dm = new A013MainClassDistanceMatrixNcdBzip2();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
					m_sampleSessionIDs, m_sequencesUHC, 
					rolesW);
		} else if(strategyNormalize.equals("NCD_gzip")){
			A014MainClassDistanceMatrixNcdGzip dm = new A014MainClassDistanceMatrixNcdGzip();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
					m_sampleSessionIDs, m_sequencesUHC, 
					rolesW);
			
		} else if(strategyNormalize.equals("SimilarityMatrixNormalize_TopicCont")){
			A111MainClassDistanceMatrixInverseTopics dm = new A111MainClassDistanceMatrixInverseTopics();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
					m_sampleSessionIDs, m_sequencesUHC, 
					rolesW,
					m_preprocessingWD + m_url2url_DM, 0.6f);
		} else if(strategyNormalize.equals("EditDistance_TopicCont")){
			A112MainClassDistanceMatrixEDTopics dm = new A112MainClassDistanceMatrixEDTopics();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
					m_sampleSessionIDs, m_sequencesUHC, 
					rolesW,
					m_preprocessingWD + m_url2url_DM, 0.6f);
		} else if(strategyNormalize.equals("SimilarityMatrixNormalize_TopicDisc")){
			A113MainClassDistanceMatrixInverseTopics2 dm = new A113MainClassDistanceMatrixInverseTopics2();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
					m_sampleSessionIDs, m_sequencesUHC, 
					rolesW,
					m_preprocessingWD + m_url2topicFile, 0.3f);
		} else if(strategyNormalize.equals("EditDistance_TopicDisc")){
			A114MainClassDistanceMatrixEDTopics2 dm = new A114MainClassDistanceMatrixEDTopics2();
			dm.createDistanceMatrix(m_databaseWD + dmWD, 
					m_sampleSessionIDs, m_sequencesUHC, 
					rolesW,
					m_preprocessingWD + m_url2topicFile, 0.3f);
		}
	}
	
	public void loadDM_split(String splitedFile){
		Object[] objA = m_matrix.readSeqs(m_databaseWD + m_dmWD + splitedFile);
		m_sampleSessionIDs_split = (ArrayList<Long>)objA[0];
		m_sequencesUHC_split = (ArrayList<String[]>)objA[1];		
	}
	
	
	
	
	// MODELS //
	
	// ModelEvaluatorMarkovChain: MC
	
	public void createModelEvaluatorMarkovChain(){
		ModelEvaluatorMarkovChain modelev = new ModelEvaluatorMarkovChain(
				m_sequencesUHC, m_sequencesUHC_split, 
				m_matrix, 
				m_trainAL, m_valAL, m_testAL,
				m_modePrRe, m_usage2contentFile, m_urlSimilarityMatrix);
		modelev.setFmeasureBeta(m_beta);
		modelev.setConfusionPoints(m_confusionPoints);
		modelev.setTopicParameters(
				m_urlIDs, m_url2topic, m_difftopics, 
				m_topicmatch, 
				m_clusterPartitionFile);
		m_modelevMC = modelev;
	}
	
	public void runModelEvaluatorMC(){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);
		
		// Results' header
		System.out.print("options," + m_modelevMC.getEvaluationHeader());		
		// RUN THE EXPERIMENTS
		m_modelevMC.buildMC();		
		String esperimentationStr = "markovchain";
		// TEST //
		String results = "";
		String resultInfo = "";	
		for(int ind=0; ind<m_nrecsA.length; ind++ ){
			int nrec = m_nrecsA[ind];
			resultInfo = esperimentationStr + "_weighted" + nrec + "_test";
			m_modelevMC.setLineHeader(resultInfo + ";", evalWriter);
			results = m_modelevMC.computeEvaluationTest("weighted", nrec, (long)0);
			System.out.print(resultInfo + ",");
			System.out.print(results);
		}
		
		this.closeFile(evalWriter);
	}
	
	
	
	// ModelEvaluatorSuffixTreeGlobal: GST
	
	public void createModelEvaluatorSuffixTreeGlobal(){
		ModelEvaluatorSuffixTreeGlobal modelev = 
				new ModelEvaluatorSuffixTreeGlobal(
						m_sequencesUHC, m_sequencesUHC_split, 
						m_matrix,
						m_trainAL, m_valAL, m_testAL,
						m_modePrRe, m_usage2contentFile, m_urlSimilarityMatrix);
		modelev.setFmeasureBeta(m_beta);
		modelev.setConfusionPoints(m_confusionPoints);
		modelev.setTopicParameters(
				m_urlIDs, m_url2topic, m_difftopics, 
				m_topicmatch, 
				m_clusterPartitionFile);
		m_modelevSTG = modelev;
	}
	
	public void runModelEvaluatorSTG(int fmode, int gtmem, String strategyST){
		long seed = 0l;
		// mode = 0: GoToRoot
		// mode = 1: GoToLongestSuffix
		// mode = 2: GoToLongestPrefix
		// gtmem (gotomemory 1000)
		
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);
		
		// Results' header
		System.out.print("options," + m_modelevSTG.getEvaluationHeader());		
		// RUN THE EXPERIMENTS
		m_modelevSTG.buildGST();
		String esperimentationStr = "suffixtree";
		String esperimentationStr2 = esperimentationStr + "_failure" + fmode;
		String esperimentationStr3 = esperimentationStr2 + "_gt" + gtmem;
		
		// TEST //
		String results = "";
		String resultInfo = "";	
		for(int ind=0; ind<m_nrecsA.length; ind++ ){
			int nrec = m_nrecsA[ind];
			resultInfo = esperimentationStr3 + "_weighted" + nrec + "_test";
			m_modelevSTG.setLineHeader(resultInfo + ";", evalWriter);
			m_modelevSTG.setEsploitationParameters(fmode, gtmem, 0);
			results = m_modelevSTG.computeEvaluationTest(strategyST, nrec, seed);
			System.out.print(resultInfo + ",");
			System.out.print(results);
		}
		
		this.closeFile(evalWriter);
	}
	
	
	
	// ModelEvaluatorClustHclust: DM+Hclust
	
	public void createModelEvaluatorClustHclust(){
		ModelEvaluatorClustHclust modelev = 
				new ModelEvaluatorClustHclust(
						m_sequencesUHC, m_sequencesUHC_split, 
						m_matrix,
						m_trainAL, m_valAL, m_testAL,
						m_modePrRe, m_usage2contentFile, m_urlSimilarityMatrix);
		modelev.setFmeasureBeta(m_beta);
		modelev.setConfusionPoints(m_confusionPoints);
		modelev.setTopicParameters(
				m_urlIDs, m_url2topic, m_difftopics, 
				m_topicmatch, 
				m_clusterPartitionFile);
		m_modelevH = modelev;
	}
	
	public void runModelEvaluatorH(int intLinkage){
		String linkageClassName = m_linkages[intLinkage];
		for(int j=0; j<m_cutthA.length; j++){ // for each height
			float cutth = m_cutthA[j];
			String esperimentationStr = "agglo" + intLinkage + "_cl" + cutth;
			System.out.println("[" + System.currentTimeMillis() + "] " + esperimentationStr);
			m_modelevH.buildDendrograms(linkageClassName);
			m_modelevH.cutDendrograms(cutth);
			m_modelevH.saveClusters(m_validationWD  + m_clustWD + "/" + esperimentationStr + ".javaData");
			m_modelevH.writeClusters(m_validationWD + m_clustWD + "/" + esperimentationStr + ".txt");
		}
	}

	
	
	
	// ModelEvaluatorSeqMinMSAWseq: DM+Clust+MSA+Wseq+ST
	
	public void createModelEvaluatorSeqMinMSAWseq(){
		ModelEvaluatorSeqMinMSAWseq modelev = 
				new ModelEvaluatorSeqMinMSAWseq(
						m_sequencesUHC, m_sequencesUHC_split, 
						m_matrix,
						m_trainAL, m_valAL, m_testAL,
						m_modePrRe, m_usage2contentFile, m_urlSimilarityMatrix);
		modelev.setFmeasureBeta(m_beta);
		modelev.setConfusionPoints(m_confusionPoints);
		modelev.setTopicParameters(
				m_urlIDs, m_url2topic, m_difftopics, 
				m_topicmatch, 
				m_clusterPartitionFile);
		m_modelevCMS = modelev;
	}

	// DM+hclust+MSA+Wseq+ST
	public void runModelEvaluatorHclustMS(int indLinkage, String strategyST){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);

		// Results' header
		System.out.print("options," + m_modelevCMS.getEvaluationHeader());
		// Start generating and evaluating the model
		for(int j=0; j<m_cutthA.length; j++){
			float cutth = m_cutthA[j];
			String esperimentationStr = "agglo" + indLinkage + "_cl" + cutth;
			// load clustering
			String clustFile = m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData";
			m_modelevCMS.loadClusters(clustFile);
			// MSA
			String msaFileTxt = m_validationWD + m_profiWD + "/" + esperimentationStr + "_alignments.txt";
			String msaFileJav = m_validationWD + m_profiWD + "/" + esperimentationStr + "_alignments.javaData";
			m_modelevCMS.msa(msaFileTxt, msaFileJav);
			// Weighted Sequences
			for(int k=0; k<m_seqweights.length; k++){
				float minsup = m_seqweights[k];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
				String wseqFileTxt = m_validationWD + m_profiWD + "/" + esperimentationStr2 + ".txt";
				String wseqFileJav = m_validationWD + m_profiWD + "/" + esperimentationStr2 + ".javaData";
				m_modelevCMS.wseq(minsup, wseqFileTxt, wseqFileJav);
				// build suffix tree
				m_modelevCMS.buildST();
		
				// Evaluation
				String results;
				String resultInfo;
				for(int ind=0; ind<m_nrecsA.length; ind++ ){
					int nrec = m_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";
					m_modelevCMS.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevCMS.setEsploitationParameters(1, 1000, 0);
					results = m_modelevCMS.computeEvaluationTest(strategyST, nrec, (long)0);
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}
			}
		}
		
		this.closeFile(evalWriter);
	}
	
	// DM+PAM+MSA+Wseq+ST
	public void runModelEvaluatorPamMS(int indLinkage, String strategyST){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);

		// Results' header
		System.out.print("options," + m_modelevCMS.getEvaluationHeader());
		// Start generating and evaluating the model
		for(int j=0; j<m_ks.length; j++){
			int kcl = m_ks[j];
			String esperimentationStr = "pam" + kcl;
			// load clustering
			String clustFile = m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData";
			m_modelevCMS.loadClusters(clustFile);
			// MSA
			String msaFileTxt = m_validationWD + m_profiWD + "/" + esperimentationStr + "_alignments.txt";
			String msaFileJav = m_validationWD + m_profiWD + "/" + esperimentationStr + "_alignments.javaData";
			m_modelevCMS.msa(msaFileTxt, msaFileJav);
			// Weighted Sequences
			for(int k=0; k<m_seqweights.length; k++){
				float minsup = m_seqweights[k];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
				String wseqFileTxt = m_validationWD + m_profiWD + "/" + esperimentationStr2 + ".txt";
				String wseqFileJav = m_validationWD + m_profiWD + "/" + esperimentationStr2 + ".javaData";
				m_modelevCMS.wseq(minsup, wseqFileTxt, wseqFileJav);
				// build suffix tree
				m_modelevCMS.buildST();
		
				// Evaluation
				String results;
				String resultInfo;
				for(int ind=0; ind<m_nrecsA.length; ind++ ){
					int nrec = m_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";
					m_modelevCMS.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevCMS.setEsploitationParameters(1, 1000, 0);
					results = m_modelevCMS.computeEvaluationTest(strategyST, nrec, (long)0);
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}
			}
		}
		
		this.closeFile(evalWriter);
	}
	
	
	
	// ModelEvaluatorSeqMinSPADE: DM+Clust+SPADE+ST
	
	public void createModelEvaluatorSeqMinSPADE(){
		ModelEvaluatorSeqMinSPADE modelev = 
				new ModelEvaluatorSeqMinSPADE(
						m_sequencesUHC, m_sequencesUHC_split, 
						m_matrix,
						m_trainAL, m_valAL, m_testAL,
						m_modePrRe, m_usage2contentFile, m_urlSimilarityMatrix);
		modelev.setFmeasureBeta(m_beta);
		modelev.setConfusionPoints(m_confusionPoints);
		modelev.setTopicParameters(
				m_urlIDs, m_url2topic, m_difftopics, 
				m_topicmatch, 
				m_clusterPartitionFile);
		m_modelevCSS = modelev;
	}
	
	public void runModelEvaluatorCSS(int indLinkage, String strategyST){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);

		// Results' header
		System.out.print("options," + m_modelevCSS.getEvaluationHeader());
		// Start generating and evaluating the model
		for(int j=0; j<m_cutthA.length; j++){
			float cutth = m_cutthA[j];
			String esperimentationStr = "agglo" + indLinkage + "_cl" + cutth;
			// load clustering
			String clustFile = m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData";
			m_modelevCSS.loadClusters(clustFile);
			// SPADE
			for(int k=0; k<m_seqweights.length; k++){
				float minsup = m_seqweights[k];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
				String spadeFileTxt = m_validationWD + m_profiWD + "/" + esperimentationStr2 + ".txt";
				String spadeFileJav = m_validationWD + m_profiWD + "/" + esperimentationStr2 + ".javaData";
				m_modelevCSS.spade(minsup, m_validationWD + m_profiWD, spadeFileTxt, spadeFileJav);
				// build suffix tree
				m_modelevCSS.buildST();
		
				// Evaluation
				String results;
				String resultInfo;
				for(int ind=0; ind<m_nrecsA.length; ind++ ){
					int nrec = m_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";
					m_modelevCSS.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevCSS.setEsploitationParameters(1, 1000, 0);
					results = m_modelevCSS.computeEvaluationTest(strategyST, nrec, (long)0);
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}
			}
		}
		
		this.closeFile(evalWriter);
	}

	
	
	// ModelEvaluatorMedoids: DM+hclust+SPADE1(+knnED)
	
	public void createModelEvaluatorMedoids(){
		ModelEvaluatorMedoids modelev = 
				new ModelEvaluatorMedoids(
						m_sequencesUHC, null, 
						m_matrix,
						m_trainAL, m_valAL, m_testAL,
						m_modePrRe, m_usage2contentFile, m_urlSimilarityMatrix,
						m_noProposeUrls);
		modelev.setFmeasureBeta(m_beta);
		modelev.setConfusionPoints(m_confusionPoints);
		modelev.setTopicParameters(
				m_urlIDs, m_url2topic, m_difftopics, 
				m_topicmatch, 
				m_clusterPartitionFile);
		m_modelevM = modelev;
	}
	
	public void runModelEvaluatorM_hclust(int indLinkage){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);

		// Results' header
		System.out.print("options," + m_modelevM.getEvaluationHeader());
		// RUN THE EXPERIMENTS
		// for each PAM: k
		for(int j=0; j<m_cutthA.length; j++){
			float cutth = m_cutthA[j];				
			String esperimentationStr = "agglo" + indLinkage + "_cl" + cutth;
			// load clusters
			String clustFile = m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData";
			m_modelevM.loadClusters(clustFile);
			// SPADE
			for(int l=0; l<m_seqweights.length; l++){
				float minsup = m_seqweights[l];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
				m_modelevM.buildMedoids(minsup, true);								
				// TEST //
				String results = "";
				String resultInfo = "";	
				for(int ind=0; ind<m_nrecsA.length; ind++ ){
					int nrec = m_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";					
					m_modelevM.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevM.setEsploitationParameters(true, m_rolesW, 100);
					results = m_modelevM.computeEvaluationTest("weighted", nrec, (long)0);					
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}			
			}
		}
		
		this.closeFile(evalWriter);
	}
	
	public void runModelEvaluatorM_pam(){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);

		// Results' header
		System.out.print("options," + m_modelevM.getEvaluationHeader());
		// RUN THE EXPERIMENTS
		// for each PAM: k
		for(int j=0; j<m_ks.length; j++){
			int k = m_ks[j];				
			String esperimentationStr = "pam" + k;
			String clustFile = m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData";
			m_modelevM.loadClusters(clustFile);

			// for each SPADE: minsup
			for(int l=0; l<m_seqweights.length; l++){
				float minsup = m_seqweights[l];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
				m_modelevM.buildMedoids(minsup, true);
	
				// TEST //
				String results = "";
				String resultInfo = "";
				for(int ind=0; ind<m_nrecsA.length; ind++ ){
					int nrec = m_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";					
					m_modelevM.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevM.setEsploitationParameters(true, m_rolesW, 100);
					results = m_modelevM.computeEvaluationTest("weighted", nrec, (long)0);					
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}			
			}
		}
		
		this.closeFile(evalWriter);
	}

	
	
	// ModelEvaluatorModularGST
	
	public void createModelEvaluatorModularGST(){
		ModelEvaluatorModularGST modelev = 
				new ModelEvaluatorModularGST(
						m_sequencesUHC, m_sequencesUHC_split, 
						m_matrix,
						m_trainAL, m_valAL, m_testAL,
						m_modePrRe, m_usage2contentFile, m_urlSimilarityMatrix);
		modelev.setFmeasureBeta(m_beta);
		modelev.setConfusionPoints(m_confusionPoints);
		modelev.setTopicParameters(
				m_urlIDs, m_url2topic, m_difftopics, 
				m_topicmatch, 
				m_clusterPartitionFile);
		m_modelevMGST = modelev;
	}
	
	// DM+Modular(hclust+ST)+(nearest:fit)
	public void runModelEvaluatorMGST_fit(int indLinkage){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);

		// Results' header
		System.out.print("options," + m_modelevMGST.getEvaluationHeader());
		// RUN THE EXPERIMENTS
		// for each hclust height
		for(int j=0; j<m_cutthA.length; j++){
			float cutth = m_cutthA[j];				
			String esperimentationStr = "agglo" + indLinkage + "_cl" + cutth;
			// load clusters
			String clustFile = m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData";
			m_modelevMGST.loadClusters(clustFile);
			// modular
			m_modelevMGST.buildClustersSuffixTrees();
			// TEST //
			String results = "";
			String resultInfo = "";	
			for(int ind=0; ind<m_nrecsA.length; ind++ ){
				int nrec = m_nrecsA[ind];
				resultInfo = esperimentationStr + "_weighted" + nrec + "_test";					
				m_modelevMGST.setLineHeader(resultInfo + ";", evalWriter);
				m_modelevMGST.setEsploitationParameters(1, 1000, 0);
				m_modelevMGST.setEsploitationParameters(true, m_rolesW, 100);
				results = m_modelevMGST.computeEvaluationTest("weighted", nrec, (long)0);					
				System.out.print(resultInfo + ",");
				System.out.print(results);
			}
		}
		
		this.closeFile(evalWriter);			
	}
	
	// DM+Modular(hclust+ST)+(nearest:knnED)
	public void runModelEvaluatorMGST_knn(int indLinkage){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);

		// Results' header
		System.out.print("options," + m_modelevMGST.getEvaluationHeader());
		// RUN THE EXPERIMENTS
		// for each hclust height
		for(int j=0; j<m_cutthA.length; j++){
			float cutth = m_cutthA[j];				
			String esperimentationStr = "agglo" + indLinkage + "_cl" + cutth;
			// load clusters
			String clustFile = m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData";
			m_modelevMGST.loadClusters(clustFile);
			// modular
			m_modelevMGST.buildClustersSuffixTrees();
			m_modelevMGST.buildMedoids(0.5f, false);
			// TEST //
			for(int k=0; k<m_knnA.length; k++){
				int knn = m_knnA[k];
				String esperimentationStr2 = esperimentationStr + "_knn" + knn;
				m_modelevMGST.setEsploitationParameters(true, m_rolesW, knn);
				String results = "";
				String resultInfo = "";
				for(int ind=0; ind<m_nrecsA.length; ind++ ){
					int nrec = m_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";					
					m_modelevMGST.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevMGST.setEsploitationParameters(1, 1000, 0);
					results = m_modelevMGST.computeEvaluationTest("weighted", nrec, (long)0);					
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}
			}
		}
		
		this.closeFile(evalWriter);		
	}
	
	
	
	// ModelEvaluatorModularSpadeST
	
	public void createModelEvaluatorModularSpadeST(){
		ModelEvaluatorModularSpadeST modelev = 
				new ModelEvaluatorModularSpadeST(
						m_sequencesUHC, m_sequencesUHC_split, 
						m_matrix,
						m_trainAL, m_valAL, m_testAL,
						m_modePrRe, m_usage2contentFile, m_urlSimilarityMatrix);
		modelev.setFmeasureBeta(m_beta);
		modelev.setConfusionPoints(m_confusionPoints);
		modelev.setTopicParameters(
				m_urlIDs, m_url2topic, m_difftopics, 
				m_topicmatch, 
				m_clusterPartitionFile);
		m_modelevMSpST = modelev;
	}
	
	// DM+Modular(hclust+SPADE+ST)+(nearest:knnED)
	public void runModelEvaluatorMSpST_knn(int indLinkage){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);

		// Results' header
		System.out.print("options," + m_modelevMSpST.getEvaluationHeader());
		// RUN THE EXPERIMENTS
		// for each hclust height
		for(int j=0; j<m_cutthA.length; j++){
			float cutth = m_cutthA[j];				
			String esperimentationStr = "agglo" + indLinkage + "_cl" + cutth;
			// load clusters
			String clustFile = m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData";
			m_modelevMSpST.loadClusters(clustFile);
			// modular
			m_modelevMSpST.buildClustersSpadeSuffixTrees(0.5f, m_validationWD + m_profiWD);
			m_modelevMSpST.buildMedoids(0.5f, false);
			// TEST //
			for(int k=0; k<m_knnA.length; k++){
				int knn = m_knnA[k];
				String esperimentationStr2 = esperimentationStr + "_knn" + knn;
				m_modelevMSpST.setEsploitationParameters(true, m_rolesW, knn);
				String results = "";
				String resultInfo = "";
				for(int ind=0; ind<m_nrecsA.length; ind++ ){
					int nrec = m_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";					
					m_modelevMSpST.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevMSpST.setEsploitationParameters(1, 1000, 0);
					results = m_modelevMSpST.computeEvaluationTest("weighted", nrec, (long)0);					
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}
			}
		}
		
		this.closeFile(evalWriter);
	}
	
	
	
	// ModelEvaluatorHMM
	
	public void createModelEvaluatorHMM(){
		ModelEvaluatorHMM modelev = 
				new ModelEvaluatorHMM(
						m_sequencesUHC, m_sequencesUHC_split, 
						m_matrix,
						m_trainAL, m_valAL, m_testAL,
						m_modePrRe, m_usage2contentFile, m_urlSimilarityMatrix);
		modelev.setFmeasureBeta(m_beta);
		modelev.setConfusionPoints(m_confusionPoints);
		modelev.setTopicParameters(
				m_urlIDs, m_url2topic, m_difftopics, 
				m_topicmatch, 
				m_clusterPartitionFile);
		m_modelevHMM = modelev;
	}
	
	// DM+Modular(hclust+SPADE+ST)+(nearest:knnED)
	public void runModelEvaluatorHMM(int indLinkage, int hmmMode){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);

		// Results' header
		System.out.print("options," + m_modelevHMM.getEvaluationHeader());
		// RUN THE EXPERIMENTS
		// for each hclust height
		for(int j=0; j<m_cutthA.length; j++){
			float cutth = m_cutthA[j];				
			String esperimentationStr = "agglo" + indLinkage + "_cl" + cutth;
			// load clusters
			String clustFile = m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData";
			m_modelevHMM.loadClusters(clustFile);
			// HMM
			m_modelevHMM.buildHiddenMarkovModels(m_validationWD + m_profiWD + "/" + esperimentationStr, hmmMode);
			m_modelevHMM.setEsploitationParameters(3);
			
			// TEST //
			String results = "";
			String resultInfo = "";
			for(int ind=0; ind<m_nrecsA.length; ind++ ){
				int nrec = m_nrecsA[ind];
				resultInfo = esperimentationStr + "_weighted" + nrec + "_test";					
				m_modelevHMM.setLineHeader(resultInfo + ";", evalWriter);
				results = m_modelevHMM.computeEvaluationTest("weighted", nrec, (long)0);					
				System.out.print(resultInfo + ",");
				System.out.print(results);
			}
		}
		
		this.closeFile(evalWriter);
	}
	
}
