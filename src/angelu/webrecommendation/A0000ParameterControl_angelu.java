package angelu.webrecommendation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import angelu.webrecommendation.evaluator.ModelEvaluatorMedoidsContent;
import angelu.webrecommendation.A000MainClassPreprocess;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorClustPAM;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import ehupatras.webrecommendation.A100MainClassAddContent;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.modelvalidation.ModelValidationCrossValidation;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccess;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.Website;

// TODO: Auto-generated Javadoc
/**
 * The Class A0000ParameterControl_angelu.
 */
public class A0000ParameterControl_angelu {

	/** The m_base. */
	protected String m_base;
	
	// preprocess
	/** The m_preprocessing wd. */
	protected String m_preprocessingWD;
	
	/** The m_logfile. */
	protected String m_logfile;
	
	// cross validation
	protected int m_nRuns = 1;
	protected String m_endingOfTheFileCV = ".javaData";
	
	// content
	// usageID
	/** The m_url2topic file. */
	protected String m_url2topicFile;
	
	/** The m_url2url_ dm. */
	protected String m_url2url_DM;
	// contentID
	/** The m_url similarity matrix. */
	protected String m_urlSimilarityMatrix;
	
	/** The m_url relation matrix. */
	protected String m_urlRelationMatrix;
	
	/** The m_cluster partition file. */
	protected String m_clusterPartitionFile;
	
	/** The m_usage2content file. */
	protected String m_usage2contentFile;
	// structures usageID
	/** The m_url i ds. */
	protected ArrayList<Integer> m_urlIDs = null;
	
	/** The m_url2topic. */
	protected int[] m_url2topic = null;
	
	/** The m_difftopics. */
	protected int m_difftopics = -1;
	
	// database
	/** The m_database wd. */
	protected String m_databaseWD;
	
	/** The m_dm wd. */
	protected String m_dmWD;
	
	/** The m_sample session i ds. */
	protected ArrayList<String> m_sampleSessionIDs = null;
	
	/** The m_sequences uhc. */
	protected ArrayList<String[]> m_sequencesUHC = null;
	
	/** The m_matrix. */
	protected Matrix m_matrix;
	
	/** The m_sample session i ds_split. */
	protected ArrayList<Long> m_sampleSessionIDs_split = null;
	
	/** The m_sequences uh c_split. */
	protected ArrayList<String[]> m_sequencesUHC_split = null;
	
	// size of the database
	protected int m_sizeDB = 10000;
	
	// holdout / cross-validation
	/** The m_n fold. */
	protected int m_nFold = 10;
	
	/** The m_ptrain. */
	protected int m_ptrain = 7;
	
	/** The m_pval. */
	protected int m_pval = 0;
	
	/** The m_ptest. */
	protected int m_ptest = 3;
	
	/** The m_train al. */
	protected ArrayList<ArrayList<String>> m_trainAL;
	
	/** The m_val al. */
	protected ArrayList<ArrayList<String>> m_valAL;
	
	/** The m_test al. */
	protected ArrayList<ArrayList<String>> m_testAL;
	
	// validation
	/** The m_validation wd. */
	protected String m_validationWD;
	
	/** The m_clust wd. */
	protected String m_clustWD;
	
	/** The m_profi wd. */
	protected String m_profiWD;
	
	/** The m_eval file. */
	protected String m_evalFile;
	
	/** The m_no propose urls. */
	protected ArrayList<Integer> m_noProposeUrls = new ArrayList<Integer>();
	
	/** The m_mode pr re. */
	protected int m_modePrRe;
	
	// Model Evaluator
	/** The m_modelev p. */
	protected ModelEvaluatorClustPAM m_modelevP = null;
	
	/** The m_modelev m. */
	protected ModelEvaluatorMedoids m_modelevM = null;
	
	/** The m_modelev mc. */
	protected ModelEvaluatorMedoidsContent m_modelevMC = null;
	
	
	// SYSTEM PARAMETERS //
	
	// PAM: k
	//int[] m_ks = {1000, 750, 500, 400, 300, 250, 200, 150, 100, 50};
	/** The m_ks. */
	//protected int[] m_ks = {200, 250};
	//protected int[] m_ks = {150, 200, 250};
	protected int[] m_ks = {100};
	
	// SPADE: minimum support
	//float[] seqweights = {0.05f, 0.10f, 0.15f, 0.20f};
	//float[] seqweights = {0.01f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.40f, 0.50f};
	//float[] seqweights = {0.15f, 0.20f, 0.25f, 0.30f};
	/** The m_seqweights. */
	protected float[] m_seqweights = {0.20f};
	
	// Weights among roles
	/** The m_roles w. */
	protected float[][] m_rolesW = {{ 0f, 0f, 0f},
  									{ 0f, 0f, 0f},
  									{ 0f, 0f, 0f}};
	
	// F-measure: beta
	/** The m_beta. */
	protected float m_beta = 1f;
	
	// Navigation stages to analyze
	/** The m_confusion points. */
	protected float[] m_confusionPoints = {0.25f,0.50f,0.75f};
	
	// topic match
	/** The m_topicmatch. */
	protected float m_topicmatch = 1f;
	
	// number of recommendations
	/** The m_nrecs a. */
	protected int[] m_nrecsA = new int[]{4};
	
	//////////////////////////////////
	
	
	// CREATOR
	/**
	 * Instantiates a new a0000 parameter control_angelu.
	 */
	public A0000ParameterControl_angelu(){
		this(new String[0]);
	}
	
	/**
	 * Instantiates a new a0000 parameter control_angelu.
	 *
	 * @param args the args
	 */
	public A0000ParameterControl_angelu(String[] args){
		System.out.println("------");
		System.out.println("Number of parameters: " + args.length);
		if(args.length==0){
			System.out.println("Parameters given by hand: ");
			this.exampleParameters2();
		} else {
			// print parameters
			this.printParameters(args);
			// load parameters
			this.readParameters(args);
		}
		System.out.println("------");
		this.initializeStructures();
	}
	
	/**
	 * Initialize structures.
	 */
	protected void initializeStructures(){
		WebAccess.setWorkDirectory(m_preprocessingWD);
		Website.setWorkDirectory(m_preprocessingWD);
	}
	
	
	
	// functions

	/**
	 * Read parameters.
	 *
	 * @param args the args
	 */
	protected void readParameters(String[] args){
		
		m_preprocessingWD = args[0];
		m_logfile = args[1];
		m_nRuns = Integer.valueOf(args[2]);
		m_endingOfTheFileCV = args[3];
		
		m_url2topicFile = args[4];
		m_url2url_DM = args[5];
		
		m_urlSimilarityMatrix = args[6];
		m_urlRelationMatrix = args[7];
		m_clusterPartitionFile = args[8];
		m_usage2contentFile = args[9];
		
		m_databaseWD = args[10];
		m_dmWD = args[11];
		
		m_validationWD = args[12];
		m_clustWD = args[13];
		m_profiWD = args[14];
		m_evalFile = args[15];
		String noRecURLsStr = args[16];
		String modePrReStr = args[17];

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
	
	protected void printParameters(String[] args){
		System.out.println("Class: " + this.getClass().getCanonicalName());
		System.out.println("Parameters given from command-line: ");
		for(int i=0; i<args.length; i++){
			System.out.println(args[i]);
		}
	}
	
	/**
	 * Example parameters.
	 */
	public void exampleParameters(){
		m_base = "experiments_angelu_wr_2";
		m_base = "experiments_angelu_wr_3";
		//m_base = "experiments_ehupatras_3";
		
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
	
	public void exampleParameters2(){
		m_base = "experiments_discapnet";
		
		m_preprocessingWD = m_base + "/01_preprocess";
		m_logfile = "/empty.txt";
		m_logfile = "/DB/DB9.txt"; // when we want to load session from a file
		
		m_url2topicFile = "/empty.txt";
		// Topic
		m_urlSimilarityMatrix = m_preprocessingWD + "/empty.txt";
		m_urlRelationMatrix = m_preprocessingWD + "/empty.txt";
		m_clusterPartitionFile = m_preprocessingWD + "/empty.txt";
		///
		m_usage2contentFile = m_preprocessingWD + "/empty.txt";
		
		m_databaseWD = m_base + "/02_database";
		m_dmWD = "/DM_04_edit";
		
		m_validationWD = m_base + "/03_validation";
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
	
	/**
	 * Preprocess logs.
	 */
	public void preprocessLogs(){
		A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		preprocess.preprocessLogs(m_preprocessingWD, m_logfile);
	}
	
	/**
	 * Load logs.
	 */
	public void loadLogs(){
		A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		preprocess.loadPreprocess();
		m_sizeDB = WebAccessSequences.getNumberOfSessions();
	}
	
	// database
	
	/**
	 * Creates the database.
	 */
	public void createDatabase(){
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		database.createDatabase(m_databaseWD, m_sizeDB);
		m_sampleSessionIDs = database.getSessionsIDs();
		m_sequencesUHC = database.getInstantiatedSequences();
	}
	
	/**
	 * Load database.
	 */
	public void loadDatabase(){
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		database.loadDatabase(m_databaseWD);
		m_sampleSessionIDs = database.getSessionsIDs();
		m_sequencesUHC = database.getInstantiatedSequences();
		
		for(int i=0; i<m_sequencesUHC.size(); i++){
			String[] seqA = m_sequencesUHC.get(i);
			for(int j=0; j<seqA.length; j++){
				System.out.print(seqA[j] + " ");
			}
			System.out.println();
		}
	}
	
	public void loadDatabase2(boolean loadSessionIDs){
		ArrayList<String[]> seqsDB = new ArrayList<String[]>();
		ArrayList<String> sesIdDB = new ArrayList<String>();
		
		BufferedReader br = null;
		try{
			long ind = 0;
			String sCurrentLine;
			br = new BufferedReader(new FileReader(m_preprocessingWD + m_logfile));
			while ((sCurrentLine = br.readLine()) != null) {
				String[] sequenceA = sCurrentLine.split(",");
				int seqlen = sequenceA.length - 1;
				
				// sessions IDs
				int sesID = 0;
				String sesIDstr = sequenceA[0];
				
				// sequence
				String[] seqA = new String[seqlen]; 
				for(int i=1; i<sequenceA.length; i++){
					String urlIDstr = sequenceA[i];
					int urlInt = -1;
					try {
						urlInt = Integer.valueOf(urlIDstr).intValue();
						urlIDstr = urlInt + "C";
					} catch(NumberFormatException ex){}
					seqA[i-1] = urlIDstr;
				}
				
				
				
				// add the sequence
				seqsDB.add(seqA);
				
				// session ID
				String indStr = String.valueOf(ind);
				if(!loadSessionIDs){
					sesIdDB.add(indStr);
				} else {
					sesIdDB.add(sesIDstr);
				}
				ind++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(br != null){
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	
		// save the structures
		m_sampleSessionIDs = sesIdDB;
		m_sequencesUHC = seqsDB;
	}
	
	// distance matrix
	
	/**
	 * Creates the dm.
	 */
	public void createDM(){
		A012MainClassDistanceMatrixED dm = new A012MainClassDistanceMatrixED();
		dm.createDistanceMatrix(m_databaseWD + m_dmWD, 
				m_sampleSessionIDs, m_sequencesUHC, 
				m_rolesW);
	}
	
	/**
	 * Load dm.
	 */
	public void loadDM(boolean isTXT){
		A012MainClassDistanceMatrixED dm = new A012MainClassDistanceMatrixED();
		dm.loadDistanceMatrix(m_databaseWD + m_dmWD, isTXT);
		m_matrix = dm.getMatrix();
	}

	
	// hold-out
	
	/**
	 * Creates the hold out.
	 */
	public void createHoldOut(){
		ModelValidationHoldOut honestmodelval = new ModelValidationHoldOut();
		honestmodelval.prepareData(m_sampleSessionIDs, m_ptrain*10, m_pval*10, m_ptest*10);
		honestmodelval.save(m_validationWD);
		m_trainAL = honestmodelval.getTrain();
		m_valAL   = honestmodelval.getValidation();
		m_testAL  = honestmodelval.getTest();
		honestmodelval.printHoldOut();
	}
	
	/**
	 * Load hold out.
	 */
	public void loadHoldOut(){
		ModelValidationHoldOut honestmodelval = new ModelValidationHoldOut();
		honestmodelval.load(m_validationWD);
		m_trainAL = honestmodelval.getTrain();
		m_valAL   = honestmodelval.getValidation();
		m_testAL  = honestmodelval.getTest();
		honestmodelval.printHoldOut();
	}
	
	// cross-validation
	
	/**
	 * Creates the cross validation.
	 */
	public void createCrossValidationRuns(){
		for(int j=0; j<m_nRuns; j++){
			// randomized the session IDs
			long seed = (long)j;
			Random rand = new Random(seed);
			int n = m_sampleSessionIDs.size();
			ArrayList<String> sessionIDs = new ArrayList<String>(n);
			ArrayList<String> sessionIDsSort = new ArrayList<String>(n);
			for(int i=0; i<n;){
				int i2 = rand.nextInt(n);
				String id = m_sampleSessionIDs.get(i2);
				int pos = Collections.binarySearch(sessionIDsSort, id);
				if(pos<0){
					sessionIDs.add(id);
					pos = Math.abs(pos+1);
					sessionIDsSort.add(pos, id);				
					i++;
				}
			}
			
			// create cross validation
			ModelValidationCrossValidation honestmodelval = new ModelValidationCrossValidation();
			honestmodelval.prepareData(sessionIDs, m_ptrain, m_pval, m_ptest, m_nFold);
			honestmodelval.save(m_validationWD, seed);
		}
	}
	public void createCrossValidation(){
		ModelValidationCrossValidation honestmodelval = new ModelValidationCrossValidation();
		honestmodelval.prepareData(m_sampleSessionIDs, m_ptrain, m_pval, m_ptest, m_nFold);
		honestmodelval.save(m_validationWD);
		m_trainAL = honestmodelval.getTrain();
		m_valAL   = honestmodelval.getValidation();
		m_testAL  = honestmodelval.getTest();
	}
	public void createCrossValidation(int ptrain, int pval, int ptest, int nFold){
		m_ptrain = ptrain;
		m_pval = pval;
		m_ptest = ptest;
		m_nFold = nFold;
		this.createCrossValidation();
	}
	public void saveCrossValidation(){
		ModelValidationCrossValidation honestmodelval = new ModelValidationCrossValidation();
		honestmodelval.save(m_validationWD);
	}
	
	/**
	 * Load hold out_cv.
	 */
	public void loadHoldOut_cv(){
		ModelValidationCrossValidation honestmodelval = new ModelValidationCrossValidation();
		honestmodelval.load(m_validationWD, m_endingOfTheFileCV);
		
		ArrayList<ArrayList<String>> trainALaux = honestmodelval.getTrain();
		m_trainAL = new ArrayList<ArrayList<String>>();
		m_trainAL.add(trainALaux.get(0));
		
		ArrayList<ArrayList<String>> valALaux  = honestmodelval.getValidation();
		m_valAL  = new ArrayList<ArrayList<String>>();
		m_valAL.add(valALaux.get(0));
		
		ArrayList<ArrayList<String>> testALaux  = honestmodelval.getTest();
		m_testAL = new ArrayList<ArrayList<String>>();
		m_testAL.add(testALaux.get(0));
	}
	
	/**
	 * Load cross validation.
	 */
	public void loadCrossValidation(){
		ModelValidationCrossValidation honestmodelval = new ModelValidationCrossValidation();
		honestmodelval.load(m_validationWD, m_endingOfTheFileCV);
		m_trainAL = honestmodelval.getTrain();
		m_valAL  = honestmodelval.getValidation();
		m_testAL  = honestmodelval.getTest();
	}

	// URL to Topic information
	
	/**
	 * Load topic inf.
	 */
	public void loadTopicInf(){
		if(!m_url2topicFile.contains("empty.txt")){
		  A100MainClassAddContent cont = new A100MainClassAddContent();
		  Object[] objA = cont.loadUrlsTopic(m_preprocessingWD + m_url2topicFile, " ");
		  m_urlIDs = (ArrayList<Integer>)objA[0];
		  m_url2topic = (int[])objA[1];
		  m_difftopics = (int)objA[2];
		}
	}
	
	
	
	
	// ModelEvaluatorClustPAM: DM+PAM
	
	/**
	 * Creates the model evaluator clust pam.
	 */
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
	
	/**
	 * Run model evaluator p.
	 */
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
	
	/**
	 * Creates the model evaluator medoids.
	 */
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

	/**
	 * Run model evaluator m_pam.
	 */
	public void runModelEvaluatorM_pam(){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_clustWD + m_evalFile);

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
	
	/**
	 * Creates the model evaluator medoids content.
	 */
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

	/**
	 * Run model evaluator mc.
	 *
	 * @param recommender the recommender
	 */
	public void runModelEvaluatorMC(String recommender){
		BufferedWriter evalWriter = this.openFile(m_validationWD + m_clustWD + m_evalFile);
		
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
	
	/**
	 * Open file.
	 *
	 * @param filename the filename
	 * @return the buffered writer
	 */
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
	
	/**
	 * Close file.
	 *
	 * @param writer the writer
	 */
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
	
	/**
	 * Sets the topic match weight.
	 *
	 * @param topicMatchWeight the new topic match weight
	 */
	public void setTopicMatchWeight(float topicMatchWeight){
		m_topicmatch = topicMatchWeight;
	}

}
