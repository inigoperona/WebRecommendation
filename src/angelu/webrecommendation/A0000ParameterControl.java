package angelu.webrecommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import angelu.webrecommendation.evaluator.ModelEvaluatorMedoidsContent;
import ehupatras.webrecommendation.A100MainClassAddContent;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.modelvalidation.ModelValidationCrossValidation;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A0000ParameterControl {

	private String m_base;
	
	// preprocess
	private String m_preprocessingWD;
	private String m_logfile;
	
	// content
	private String m_url2topicFile;
	private String m_urlSimilarityMatrix;
	private String m_urlRelationMatrix;
	private String m_clusterPartitionFile;
	private String m_usage2contentFile;
	private ArrayList<Integer> m_urlIDs;
	private int[] m_url2topic;
	private int m_difftopics;
	
	// database
	private String m_databaseWD;
	private String m_dmWD;
	private ArrayList<Long> m_sampleSessionIDs;
	private ArrayList<String[]> m_sequencesUHC;
	private Matrix m_matrix;
	private ArrayList<ArrayList<Long>> m_trainAL;
	private ArrayList<ArrayList<Long>> m_valAL;
	private ArrayList<ArrayList<Long>> m_testAL;
	
	// validation
	private String m_validationWD;
	private String m_clustWD;
	private String m_profiWD;
	private String m_evalFile;
	private ArrayList<Integer> m_noProposeUrls;
	private int m_modePrRe;
	private ModelEvaluatorMedoids m_modelevM = null;
	private ModelEvaluatorMedoidsContent m_modelevMC = null;
	
	
	// SYSTEM PARAMETERS //
	
	// PAM: k
	//int[] m_ks = {1000, 750, 500, 400, 300, 250, 200, 150, 100, 50};
	private int[] m_ks = {150};
	
	// SPADE: minimum support
	//float[] seqweights = {0.05f, 0.10f, 0.15f, 0.20f};
	//float[] seqweights = {0.01f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.40f, 0.50f};
	//float[] seqweights = {0.15f, 0.20f, 0.25f, 0.30f};
	private float[] m_seqweights = {0.20f};
	
	// Weights among roles
	private float[][] m_rolesW = {	{ 0f, 0f, 0f},
  									{ 0f, 0f, 0f},
  									{ 0f, 0f, 0f}};
	
	// F-measure: beta
	private float m_beta = 1f;
	
	// Navigation stages to analyze
	private float[] m_confusionPoints = {0.25f,0.50f,0.75f};
	
	// topic match
	private float m_topicmatch = 1f;
	
	// number of recommendations
	//int[] nrecsA = new int[]{4,5,10,20};
	int[] nrecsA = new int[]{4};
	
	//////////////////////////////////
	
	
	// CREATOR
	public A0000ParameterControl(){
		this.exampleParameters();
		this.initializeStructures();
	}
	public A0000ParameterControl(String[] args){
		this.readParameters(args);
		this.initializeStructures();
	}
	private void initializeStructures(){
		WebAccessSequencesUHC.setWorkDirectory(m_preprocessingWD);
		Website.setWorkDirectory(m_preprocessingWD);
	}
	
	
	
	// functions

	private void readParameters(String[] args){
		
		m_preprocessingWD = args[0];
		m_logfile = args[1];
		
		m_url2topicFile = args[2];
		m_urlSimilarityMatrix = args[3];
		m_urlRelationMatrix = args[4];
		m_clusterPartitionFile = args[5];
		m_usage2contentFile = args[6];
		
		m_databaseWD = args[7];
		m_dmWD = args[8];
		
		m_validationWD = args[9];
		m_clustWD = args[10];
		m_profiWD = args[11];
		m_evalFile = args[12];
		String noRecURLsStr = args[13];
		String modePrReStr = args[14];

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
		m_base = "experiments_angelu/experiments";
		
		m_preprocessingWD = m_base + "/01_preprocess";
		m_logfile = "/log20000.log";
		
		m_url2topicFile = "/URLs_to_topic.txt";
		m_urlSimilarityMatrix = "contentEnrichment/OntologySimilarity/ResultadosTestuHutsa/ResSimilarity.txt";
		m_urlRelationMatrix = "contentEnrichment/OntologySimilarity/ResultadosTestuHutsa/ResRelations.txt";
		m_clusterPartitionFile = "contentEnrichment/OntologySimilarity/clusterPartitions/ClusterPartitionTestuHutsa.txt";
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
	
	public void preprocessLogs(){
		A000MainClassPreprocess main = new A000MainClassPreprocess();
		main.preprocessLogs(m_preprocessingWD, m_logfile);
	}
	
	public void createDatabase(){
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		database.createDatabase(m_databaseWD);
		m_sampleSessionIDs = database.getSessionsIDs();
		m_sequencesUHC = database.getInstantiatedSequences();
	}
	
	public void loadDatabase(){
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		//database.createDatabase(databaseWD);
		database.loadDatabase(m_databaseWD);
		m_sampleSessionIDs = database.getSessionsIDs();
		m_sequencesUHC = database.getInstantiatedSequences();
	}
	
	public void loadDM(){
		A012MainClassDistanceMatrixED dm = new A012MainClassDistanceMatrixED();
		dm.loadDistanceMatrix(m_databaseWD + m_dmWD);
		m_matrix = dm.getMatrix();
	}

	public void loadHoldOut(){
		A021MainClassCrossValidation ho = new A021MainClassCrossValidation();
		ho.loadParts(m_validationWD, m_sampleSessionIDs);
		ModelValidationCrossValidation mv = ho.getParts();
		
		ArrayList<ArrayList<Long>> trainALaux = mv.getTrain();
		m_trainAL = new ArrayList<ArrayList<Long>>();
		m_trainAL.add(trainALaux.get(0));
		
		ArrayList<ArrayList<Long>> valALaux  = mv.getValidation();
		m_valAL  = new ArrayList<ArrayList<Long>>();
		m_valAL.add(valALaux.get(0));
		
		ArrayList<ArrayList<Long>> testALaux  = mv.getTest();
		m_testAL = new ArrayList<ArrayList<Long>>();
		m_testAL.add(testALaux.get(0));
	}

	public void loadTopicInf(){
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objA = cont.loadUrlsTopic(m_preprocessingWD + m_url2topicFile, " ");
		m_urlIDs = (ArrayList<Integer>)objA[0];
		m_url2topic = (int[])objA[1];
		m_difftopics = (int)objA[2];		
	}
	
	
	
	// ModelEvaluatorMedoids
	
	public void createModelEvaluatorMedoids(){
		ModelEvaluatorMedoids modelev = 
				new ModelEvaluatorMedoids(
						m_sequencesUHC, null, 
						m_matrix,
						m_trainAL, m_valAL, m_testAL,
						m_modePrRe,
						m_noProposeUrls);
		modelev.setFmeasureBeta(m_beta);
		modelev.setConfusionPoints(m_confusionPoints);
		modelev.setTopicParameters(
				m_urlIDs, m_url2topic, m_difftopics, 
				m_topicmatch, 
				m_clusterPartitionFile);
		m_modelevM = modelev;
	}	

	public void runModelEvaluatorM(){
		// Results' header
		System.out.print("options," + m_modelevMC.getEvaluationHeader());
		
		// open the file in which we save the evaluation process
		BufferedWriter evalWriter = null;
		try{
			evalWriter = new BufferedWriter(new FileWriter(m_validationWD + m_evalFile));
		} catch(IOException ex){
			System.err.println("[angelu.webrecommendation.A0000ParameterControl] " +
					"Not possible to open the file: " + m_evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
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
				for(int ind=0; ind<nrecsA.length; ind++ ){
					int nrec = nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_val";					
					m_modelevMC.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevMC.setEsploitationParameters(true, m_rolesW, 100);
					results = m_modelevMC.computeEvaluationVal("weighted", nrec, (long)0);
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}				
				// TEST //
				for(int ind=0; ind<nrecsA.length; ind++ ){
					int nrec = nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";					
					m_modelevMC.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevMC.setEsploitationParameters(true, m_rolesW, 100);
					results = m_modelevMC.computeEvaluationTest("weighted", nrec, (long)0);					
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}			
			}
		}
		
		// close the file in which we save the evaluation process
		try{
			evalWriter.close();
		} catch (IOException ex){
			System.err.println("[[angelu.webrecommendation.A0000ParameterControl]] " +
					"Problems at closing the file: " + m_evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	
	// ModelEvaluatorMedoidsContent
	
	public void createModelEvaluatorMedoidsContent(){
		ModelEvaluatorMedoidsContent modelev = 
				new ModelEvaluatorMedoidsContent(
						m_sequencesUHC, null, 
						m_matrix,
						m_trainAL, m_valAL, m_testAL,
						m_modePrRe,
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
		// Results' header
		System.out.print("options," + m_modelevMC.getEvaluationHeader());
		
		// open the file in which we save the evaluation process
		BufferedWriter evalWriter = null;
		try{
			evalWriter = new BufferedWriter(new FileWriter(m_validationWD + m_evalFile));
		} catch(IOException ex){
			System.err.println("[angelu.webrecommendation.A0000ParameterControl] " +
					"Not possible to open the file: " + m_evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
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
				for(int ind=0; ind<nrecsA.length; ind++ ){
					int nrec = nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_val";					
					m_modelevMC.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevMC.setEsploitationParameters(true, m_rolesW, 100);
					m_modelevMC.setEsploitationParameters(recommender, 
							m_urlSimilarityMatrix, m_urlRelationMatrix, m_usage2contentFile);
					results = m_modelevMC.computeEvaluationVal("weighted", nrec, (long)0);
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}				
				// TEST //
				for(int ind=0; ind<nrecsA.length; ind++ ){
					int nrec = nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";					
					m_modelevMC.setLineHeader(resultInfo + ";", evalWriter);
					m_modelevMC.setEsploitationParameters(true, m_rolesW, 100);
					m_modelevMC.setEsploitationParameters(recommender,
							m_urlSimilarityMatrix, m_urlRelationMatrix, m_usage2contentFile);
					results = m_modelevMC.computeEvaluationTest("weighted", nrec, (long)0);					
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}			
			}
		}
		
		// close the file in which we save the evaluation process
		try{
			evalWriter.close();
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
