package ehupatras.webrecommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorSuffixTreeGlobal;
import angelu.webrecommendation.A0000ParameterControl_angelu;

public class A0000ParameterControl_ehupatras extends A0000ParameterControl_angelu {

	// Model Evaluator
	protected ModelEvaluatorSuffixTreeGlobal m_modelevSTG = null;
	
	
	
	// SYSTEM PARAMETERS //
	
	// PAM: k
	//int[] m_ks = {1000, 750, 500, 400, 300, 250, 200, 150, 100, 50};
	protected int[] m_ks = {150};
	
	// SPADE: minimum support
	//float[] seqweights = {0.05f, 0.10f, 0.15f, 0.20f};
	//float[] seqweights = {0.01f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.40f, 0.50f};
	//float[] seqweights = {0.15f, 0.20f, 0.25f, 0.30f};
	protected float[] m_seqweights = {0.20f};
	
	// Weights among roles
	protected float[][] m_rolesW = {	{ 0f, 0f, 0f},
  									{ 0f, 0f, 0f},
  									{ 0f, 0f, 0f}};
	
	// F-measure: beta
	protected float m_beta = 0.5f;
	
	// Navigation stages to analyze
	protected float[] m_confusionPoints = {0.25f,0.50f,0.75f};
	
	// topic match
	protected float m_topicmatch = 1f;
	
	// number of recommendations
	protected int[] m_nrecsA = new int[]{2,3,4,5,10,15,20};
	
	//////////////////////////////////
	
	// CREATOR
	
	public A0000ParameterControl_ehupatras(){
		this.exampleParameters();
		this.initializeStructures();
	}
	public A0000ParameterControl_ehupatras(String[] args){
		this.readParameters(args);
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

	
	
	// ModelEvaluatorSuffixTreeGlobal
	
	public void createModelEvaluatorSuffixTreeGlobal(){
		ModelEvaluatorSuffixTreeGlobal modelev = 
				new ModelEvaluatorSuffixTreeGlobal(
						m_sequencesUHC, null, 
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
	
	public void runModelEvaluatorSTG(int fmode, int gtmem, String strategy){
		long seed = 0l;
		// mode = 0: GoToRoot
		// mode = 1: GoToLongestSuffix
		// mode = 2: GoToLongestPrefix
		// gtmem (gotomemory 1000):  
		
		// Results' header
		System.out.print("options," + m_modelevSTG.getEvaluationHeader());
		
		// open the file in which we save the evaluation process
		BufferedWriter evalWriter = null;
		try{
			evalWriter = new BufferedWriter(new FileWriter(m_validationWD + m_evalFile));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.A0000ParameterControl_ehupatras] " +
					"Not possible to open the file: " + m_evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// RUN THE EXPERIMENTS
		m_modelevSTG.buildGST();
		String esperimentationStr = "suffixtree";
		String esperimentationStr2 = esperimentationStr + "_failure" + fmode;
		String esperimentationStr3 = esperimentationStr2 + "_gt" + gtmem;
		
		// TEST //
		String results = "";
		for(int ind=0; ind<m_nrecsA.length; ind++ ){
			int nrec = m_nrecsA[ind];
			m_modelevSTG.setEsploitationParameters(fmode, gtmem, 0);
			results = m_modelevSTG.computeEvaluationTest(strategy, nrec, seed);
			System.out.print(esperimentationStr3 + "_weighted" + nrec + ",");
			System.out.print(results);
		}
		
		// close the file in which we save the evaluation process
		try{
			evalWriter.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.A0000ParameterControl_ehupatras] " +
					"Problems at closing the file: " + m_evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
}
