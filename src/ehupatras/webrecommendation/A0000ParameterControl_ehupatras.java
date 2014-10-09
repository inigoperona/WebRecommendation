package ehupatras.webrecommendation;

import java.io.BufferedWriter;
import java.util.ArrayList;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorClustPAM;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorClustHclust;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorSeqMinMSAWseq;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorSeqMinSPADE;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorSuffixTreeGlobal;
import angelu.webrecommendation.A0000ParameterControl_angelu;

public class A0000ParameterControl_ehupatras extends A0000ParameterControl_angelu {

	// Model Evaluator
	protected ModelEvaluatorSuffixTreeGlobal m_modelevSTG = null;
	protected ModelEvaluatorClustHclust m_modelevH = null;
	protected ModelEvaluatorClustPAM m_modelevP = null;
	protected ModelEvaluatorSeqMinMSAWseq m_modelevCMS = null;
	protected ModelEvaluatorSeqMinSPADE m_modelevCSS = null;
	
	// database
	protected ArrayList<Long> m_sampleSessionIDs_split = null;
	protected ArrayList<String[]> m_sequencesUHC_split = null;
	
	
	// SYSTEM PARAMETERS //
	
	public void initializeSystemParameters(){
		// PAM: k
		m_ks = new int[]{150, 200, 250, 300};
		
		// SPADE: minimum support
		m_seqweights = new float[]{0.10f, 0.15f, 0.20f};
		
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
		m_nrecsA = new int[]{2,3,4,5,10,15,20};
	}
	
	// Hclust: cutthA
	//float[] cutthA = {0.1f, 0.2f, 0.4f, 0.6f, 0.8f};
	//float[] cutthA = {4f, 10f, 15f, 20f, 25f};
	protected float[] m_cutthA = {4f, 10f, 15f, 20f, 25f};
	protected String[] m_linkages = 
		{"ehupatras.clustering.sapehac.agglomeration.AverageLinkage",
		 "ehupatras.clustering.sapehac.agglomeration.CentroidLinkage",
		 "ehupatras.clustering.sapehac.agglomeration.CompleteLinkage",
		 "ehupatras.clustering.sapehac.agglomeration.MedianLinkage",
		 "ehupatras.clustering.sapehac.agglomeration.SingleLinkage",
		 "ehupatras.clustering.sapehac.agglomeration.WardLinkage"};
	
	//////////////////////////////////
	
	
	
	// CREATOR
	
	public A0000ParameterControl_ehupatras(){
		this.exampleParameters();
		this.initializeSystemParameters();
		this.initializeStructures();
	}
	public A0000ParameterControl_ehupatras(String[] args){
		//this.readParameters(args);
		this.exampleParameters();
		this.initializeSystemParameters();
		this.initializeStructures();
	}
	
	
	
	// FUNCTIONS
	
	public void exampleParameters(){
		m_base = "experiments_ehupatras";
		
		m_preprocessingWD = m_base + "/01_preprocess";
		m_logfile = "/log20000.log";
		
		m_url2topicFile = "/URLs_to_topic.txt";
		m_urlSimilarityMatrix = "contentEnrichment/TopicSimilarity/similarityHellingerTopic1Testuhutsa.txt";
		m_urlRelationMatrix = "-";
		m_clusterPartitionFile = "contentEnrichment/OntologySimilarity/clusterPartitions/ClusterPartitionTestuHutsa.txt";
		m_usage2contentFile = "convert_UrlIDs_content2usage/usa2cont.csv";
		
		m_databaseWD = m_base + "/02_DATABASE_5";
		m_dmWD = "/DM_04_edit";
		
		m_validationWD = m_base + "/03_VALIDATION_5";
		m_clustWD = "/pam_DM_04_edit";
		m_profiWD = "/pam_DM_04_edit/spade1";
		m_evalFile = "/evaluation.txt";
		
		m_modePrRe = 0;
	}
	
	public void loadHoldOut(){
		A020MainClassHoldOut ho = new A020MainClassHoldOut();
		ho.loadParts(m_validationWD, m_sampleSessionIDs);
		ModelValidationHoldOut mv = ho.getParts();
		m_trainAL = mv.getTrain();
		m_valAL   = mv.getValidation();
		m_testAL  = mv.getTest();
	}

	public void loadDM_split(String splitedFile){
		Object[] objA = m_matrix.readSeqs(m_databaseWD + m_dmWD + splitedFile);
		m_sampleSessionIDs_split = (ArrayList<Long>)objA[0];
		m_sequencesUHC_split = (ArrayList<String[]>)objA[1];		
	}
	
	
	
	// ModelEvaluatorSuffixTreeGlobal
	
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
	
	
	
	// ModelEvaluatorClustHclust
	
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
	
	
	
	// ModelEvaluatorClustHclust
	
	public void createModelEvaluatorClustPAM(){
		ModelEvaluatorClustPAM modelev = 
				new ModelEvaluatorClustPAM(
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
		m_modelevP = modelev;
	}
	
	public void runModelEvaluatorP(){
		for(int j=0; j<m_ks.length; j++){ // for each height
			int k = m_ks[j];
			String esperimentationStr = "pam" + k;
			System.out.println("[" + System.currentTimeMillis() + "] " + esperimentationStr);
			m_modelevP.buildPAM(k);
			m_modelevP.saveClusters(m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData");
			m_modelevP.writeClusters(m_validationWD + m_clustWD + "/" + esperimentationStr + ".txt");
		}
	}
	
	
	
	// A0500MainClassHclustMsaSt
	
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
	
	
	// ModelEvaluatorSeqMinSPADE
	
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

	
	
	// ModelEvaluatorMedoids
	
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

	
}
