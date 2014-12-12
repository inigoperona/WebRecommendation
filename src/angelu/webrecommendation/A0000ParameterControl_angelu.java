package angelu.webrecommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import angelu.webrecommendation.evaluator.ModelEvaluatorMedoidsContent;
import angelu.webrecommendation.A000MainClassPreprocess;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorClustPAM;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import ehupatras.webrecommendation.A100MainClassAddContent;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.modelvalidation.ModelValidationCrossValidation;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A0000ParameterControl_angelu {

	protected String m_base;
	
	// preprocess
	protected String m_preprocessingWD;
	protected String m_logfile;
	
	// content
	// usageID
	protected String m_url2topicFile;
	protected String m_url2url_DM;
	// contentID
	protected String m_urlSimilarityMatrix;
	protected String m_urlRelationMatrix;
	protected String m_clusterPartitionFile;
	protected String m_usage2contentFile;
	// structures usageID
	protected ArrayList<Integer> m_urlIDs;
	protected int[] m_url2topic;
	protected int m_difftopics;
	
	// database
	protected String m_databaseWD;
	protected String m_dmWD;
	protected ArrayList<Long> m_sampleSessionIDs;
	protected ArrayList<String[]> m_sequencesUHC;
	protected Matrix m_matrix;
	protected ArrayList<Long> m_sampleSessionIDs_split = null;
	protected ArrayList<String[]> m_sequencesUHC_split = null;
	
	// holdout / cross-validation
	protected int m_nFold = 10;
	protected int m_ptrain = 7;
	protected int m_pval = 2;
	protected int m_ptest = 1;
	protected ArrayList<ArrayList<Long>> m_trainAL;
	protected ArrayList<ArrayList<Long>> m_valAL;
	protected ArrayList<ArrayList<Long>> m_testAL;
	
	// validation
	protected String m_validationWD;
	protected String m_clustWD;
	protected String m_profiWD;
	protected String m_evalFile;
	protected ArrayList<Integer> m_noProposeUrls = new ArrayList<Integer>();
	protected int m_modePrRe;
	
	// Model Evaluator
	protected ModelEvaluatorClustPAM m_modelevP = null;
	protected ModelEvaluatorMedoids m_modelevM = null;
	protected ModelEvaluatorMedoidsContent m_modelevMC = null;
	
	
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
	protected float[][] m_rolesW = {{ 0f, 0f, 0f},
  									{ 0f, 0f, 0f},
  									{ 0f, 0f, 0f}};
	
	// F-measure: beta
	protected float m_beta = 1f;
	
	// Navigation stages to analyze
	protected float[] m_confusionPoints = {0.25f,0.50f,0.75f};
	
	// topic match
	protected float m_topicmatch = 1f;
	
	// number of recommendations
	protected int[] m_nrecsA = new int[]{4};
	
	//////////////////////////////////
	
	
	// CREATOR
	public A0000ParameterControl_angelu(){
		this.exampleParameters();
		this.initializeStructures();
	}
	public A0000ParameterControl_angelu(String[] args){
		if(args.length==0){
			this.exampleParameters();
		} else {
			this.readParameters(args);
		}
		this.initializeStructures();
	}
	protected void initializeStructures(){
		WebAccessSequencesUHC.setWorkDirectory(m_preprocessingWD);
		Website.setWorkDirectory(m_preprocessingWD);
	}
	
	
	
	// functions

	protected void readParameters(String[] args){
		
		m_preprocessingWD = args[0];
		m_logfile = args[1];
		
		m_url2topicFile = args[2];
		m_url2url_DM = args[3];
		
		m_urlSimilarityMatrix = args[4];
		m_urlRelationMatrix = args[5];
		m_clusterPartitionFile = args[6];
		m_usage2contentFile = args[7];
		
		m_databaseWD = args[8];
		m_dmWD = args[9];
		
		m_validationWD = args[10];
		m_clustWD = args[11];
		m_profiWD = args[12];
		m_evalFile = args[13];
		String noRecURLsStr = args[14];
		String modePrReStr = args[15];

		// the URLs we do not have to recommend
		m_noProposeUrls = new ArrayList<Integer>();
		if(!noRecURLsStr.equals("-")){
			String[] noRecURLsA = noRecURLsStr.split(",");
			for(int i=0; i<noRecURLsA.length; i++){
				int norec = Integer.valueOf(noRecURLsA[i]);
				m_noProposeUrls.add(norec);
			}
		}
		
		// how to treat the prohibited URLs
		m_modePrRe = Integer.valueOf(modePrReStr);
	}
	
	public void exampleParameters(){
		m_base = "experiments_angelu_wr_2";
		
		m_preprocessingWD = m_base + "/01_preprocess";
		m_logfile = "/log20000.log";
		
		m_url2topicFile = "/Content/Topic/URLs_to_topic_th0/URLs_to_topic_TestuHutsa_th0_usageID.txt";
		///
		// Ontology
		//m_urlSimilarityMatrix = "contentEnrichment/Ont3_025_060/ResultadosTestuHutsa/ResSimilarity.txt";
		//m_urlRelationMatrix = "contentEnrichment/Ont3_025_060/ResultadosTestuHutsa/ResRelations.txt";
		//m_clusterPartitionFile = "contentEnrichment/Ont3_025_060/clusterPartitions/ClusterPartitionTestuHutsa.txt";
		// Topic
		m_urlSimilarityMatrix = "contentEnrichment/Topic/similarityHellingerTopic1TestuHutsa.txt";
		m_urlRelationMatrix = "contentEnrichment/Topic/relationMatrixTopic1TestuHutsa.txt";
		//m_urlRelationMatrix = "contentEnrichment/Topic/relationMatrixDisjoinment.txt";
		//m_clusterPartitionFile = "contentEnrichment/Topic/clusterPartitions/ClusterPartitionTestuHutsa.txt";
		m_clusterPartitionFile = "contentEnrichment/Topic/URLs_to_topic_th0/URLs_to_topic_TestuHutsa_th0_contID_cl.txt";
		///
		m_usage2contentFile = "convert_UrlIDs_content2usage/usa2cont.csv";
		
		m_databaseWD = m_base + "/02_DATABASE_5";
		m_dmWD = "/DM_04_edit";
		
		m_validationWD = m_base + "/03_VALIDATION_5";
		m_clustWD = "/pam_DM_04_edit";
		m_profiWD = "/pam_DM_04_edit/spade1";
		m_evalFile = "/evaluation.txt";
		
		m_noProposeUrls = new ArrayList<Integer>();
		m_noProposeUrls.add(11);
		m_noProposeUrls.add(74);
		m_noProposeUrls.add(7);
		m_noProposeUrls.add(89);
		m_noProposeUrls.add(152);
		
		m_modePrRe = 1;
	}
	
	// preprocess
	
	public void preprocessLogs(){
		A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		preprocess.preprocessLogs(m_preprocessingWD, m_logfile);
	}
	
	public void loadLogs(){
		A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		preprocess.loadPreprocess();
	}
	
	// database
	
	public void createDatabase(){
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		database.createDatabase(m_databaseWD);
		m_sampleSessionIDs = database.getSessionsIDs();
		m_sequencesUHC = database.getInstantiatedSequences();
	}
	
	public void loadDatabase(){
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		database.loadDatabase(m_databaseWD);
		m_sampleSessionIDs = database.getSessionsIDs();
		m_sequencesUHC = database.getInstantiatedSequences();
	}
	
	// distance matrix
	
	public void createDM(){
		this.loadDatabase();
		A012MainClassDistanceMatrixED dm = new A012MainClassDistanceMatrixED();
		dm.createDistanceMatrix(m_databaseWD + m_dmWD, 
				m_sampleSessionIDs, m_sequencesUHC, 
				m_rolesW);
	}
	
	public void loadDM(){
		A012MainClassDistanceMatrixED dm = new A012MainClassDistanceMatrixED();
		dm.loadDistanceMatrix(m_databaseWD + m_dmWD);
		m_matrix = dm.getMatrix();
	}

	
	// hold-out
	
	public void createHoldOut(){
		ModelValidationHoldOut honestmodelval = new ModelValidationHoldOut();
		honestmodelval.prepareData(m_sampleSessionIDs, m_ptrain*10, m_pval*10, m_ptest*10);
		honestmodelval.save(m_validationWD);
		m_trainAL = honestmodelval.getTrain();
		m_valAL   = honestmodelval.getValidation();
		m_testAL  = honestmodelval.getTest();
	}
	
	public void loadHoldOut(){
		ModelValidationHoldOut honestmodelval = new ModelValidationHoldOut();
		honestmodelval.load(m_validationWD);
		m_trainAL = honestmodelval.getTrain();
		m_valAL   = honestmodelval.getValidation();
		m_testAL  = honestmodelval.getTest();
	}
	
	// cross-validation
	
	public void createCrossValidation(){
		ModelValidationCrossValidation honestmodelval = new ModelValidationCrossValidation();
		honestmodelval.prepareData(m_sampleSessionIDs, m_ptrain, m_pval, m_ptest, m_nFold);
		honestmodelval.save(m_validationWD);
		m_trainAL = honestmodelval.getTrain();
		m_valAL   = honestmodelval.getValidation();
		m_testAL  = honestmodelval.getTest();
	}
	
	public void loadHoldOut_cv(){
		ModelValidationCrossValidation honestmodelval = new ModelValidationCrossValidation();
		honestmodelval.load(m_validationWD);
		
		ArrayList<ArrayList<Long>> trainALaux = honestmodelval.getTrain();
		m_trainAL = new ArrayList<ArrayList<Long>>();
		m_trainAL.add(trainALaux.get(0));
		
		ArrayList<ArrayList<Long>> valALaux  = honestmodelval.getValidation();
		m_valAL  = new ArrayList<ArrayList<Long>>();
		m_valAL.add(valALaux.get(0));
		
		ArrayList<ArrayList<Long>> testALaux  = honestmodelval.getTest();
		m_testAL = new ArrayList<ArrayList<Long>>();
		m_testAL.add(testALaux.get(0));
	}
	
	public void loadCrossValidation(){
		ModelValidationCrossValidation honestmodelval = new ModelValidationCrossValidation();
		honestmodelval.load(m_validationWD);
		m_trainAL = honestmodelval.getTrain();
		m_valAL  = honestmodelval.getValidation();
		m_testAL  = honestmodelval.getTest();
	}

	// URL to Topic information
	
	public void loadTopicInf(){
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objA = cont.loadUrlsTopic(m_preprocessingWD + m_url2topicFile, " ");
		m_urlIDs = (ArrayList<Integer>)objA[0];
		m_url2topic = (int[])objA[1];
		m_difftopics = (int)objA[2];		
	}
	
	
	
	
	// ModelEvaluatorClustPAM: DM+PAM
	
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
	
	
	// ModelEvaluatorMedoids
	
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
								
				// VALIDATION //
				String results = "";
				String resultInfo = "";	
				// for each number of recommendation
				for(int ind=0; ind<m_nrecsA.length; ind++ ){
					int nrec = m_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_val";					
					m_modelevM.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevM.setEsploitationParameters(true, m_rolesW, 100);
					results = m_modelevM.computeEvaluationVal("weighted", nrec, (long)0);
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}				
				// TEST //
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
	
	
	// ModelEvaluatorMedoidsContent
	
	public void createModelEvaluatorMedoidsContent(){
		ModelEvaluatorMedoidsContent modelev = 
				new ModelEvaluatorMedoidsContent(
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
		m_modelevMC = modelev;
	}

	public void runModelEvaluatorMC(String recommender){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_evalFile);
		
		// Results' header
		System.out.print("options," + m_modelevMC.getEvaluationHeader());
		// RUN THE EXPERIMENTS
		// for each PAM: k
		for(int j=0; j<m_ks.length; j++){
			int k = m_ks[j];				
			String esperimentationStr = "pam" + k;
			String clustFile = m_validationWD + m_clustWD + "/" + esperimentationStr + ".javaData";
			m_modelevMC.loadClusters(clustFile);

			// for each SPADE: minsup
			for(int l=0; l<m_seqweights.length; l++){
				float minsup = m_seqweights[l];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
				m_modelevMC.buildMedoids(minsup, true);
								
				// VALIDATION //
				String results = "";
				String resultInfo = "";	
				// for each number of recommendation
				for(int ind=0; ind<m_nrecsA.length; ind++ ){
					int nrec = m_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_val";					
					m_modelevMC.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevMC.setEsploitationParameters(true, m_rolesW, 100);
					m_modelevMC.setEsploitationParameters(recommender, 
							m_urlRelationMatrix);
					results = m_modelevMC.computeEvaluationVal("weighted", nrec, (long)0);
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}				
				// TEST //
				for(int ind=0; ind<m_nrecsA.length; ind++ ){
					int nrec = m_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";					
					m_modelevMC.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevMC.setEsploitationParameters(true, m_rolesW, 100);
					m_modelevMC.setEsploitationParameters(recommender,
							m_urlRelationMatrix);
					results = m_modelevMC.computeEvaluationTest("weighted", nrec, (long)0);					
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}			
			}
		}
		
		this.closeFile(evalWriter);
	}
	
	
	
	// utilities
	
	protected BufferedWriter openFile(String filename){
		// open the file in which we save the evaluation process
		BufferedWriter evalWriter = null;
		try{
			evalWriter = new BufferedWriter(new FileWriter(filename));
		} catch(IOException ex){
			System.err.println("[angelu.webrecommendation.A0000ParameterControl] " +
					"Not possible to open the file: " + m_evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		return evalWriter;
	}
	
	protected void closeFile(BufferedWriter writer){
		// close the file in which we save the evaluation process
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[[angelu.webrecommendation.A0000ParameterControl]] " +
					"Problems at closing the file: " + m_evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	
	
	// change parameters
	
	public void setTopicMatchWeight(float topicMatchWeight){
		m_topicmatch = topicMatchWeight;
	}

}
