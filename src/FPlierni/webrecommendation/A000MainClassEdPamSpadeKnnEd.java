package FPlierni.webrecommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import ehupatras.webrecommendation.A012MainClassDistanceMatrixED;
import ehupatras.webrecommendation.A001MainClassCreateDatabase;
import ehupatras.webrecommendation.A100MainClassAddContent;
import ehupatras.webrecommendation.modelvalidation.ModelValidationHoldOut;
import ehupatras.webrecommendation.structures.WebAccessSequences;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorClustPAM;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;

// TODO: Auto-generated Javadoc
/**
 * The Class A000MainClassEdPamSpadeKnnEd.
 */
public class A000MainClassEdPamSpadeKnnEd {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		// folders
		String var_base = "experiments_FPlierni_wr";
		String var_preprocessingWD = var_base + "/01_preprocess";
		//String var_preprocessingWD = args[1]
		String var_databaseWD = var_base + "/02_database";
		String var_dmWD = "/DM_ED";
		String var_validationWD = var_base + "/03_validation";
		String var_clustWD = "/pam_DM_ED";
		String var_profiWD = "/pam_DM_ED/spade1";
		// files
		String var_url2topicFile = "/Content/URLs_to_topic_TestuHutsa_th0_usageID.txt";
		String var_url2url_DM = "/Content/DM_similarityHellingerTopic1TestuHutsa_usageID.txt";
		String var_urlSimilarityMatrix = var_preprocessingWD + "/Content/similarityHellingerTopic1TestuHutsa.txt";
		String var_clusterPartitionFile = var_preprocessingWD + "/Content/ClusterPartitionTestuHutsa.txt";
		String var_usage2contentFile = var_preprocessingWD + "/Content/usa2cont.csv";
		String var_evalFile = "/evaluation.txt";
		// system's parameters
		int[] var_ks = {150}; // number of clusters
		float[] var_seqweights = {0.20f}; // Sequence Mining algorithm's minimum support
		// metrics' parameters
		int var_modePrRe = 0; // 0: strict - 1: relax, precision and recall computation
		float var_beta = 0.5f; // F-measured beta parameter
		float[] var_confusionPoints = new float[]{0.25f,0.50f,0.75f}; // the stages were the navigation will be analyzed
		int[] var_nrecsA = new int[]{4};
		ArrayList<Integer> var_noProposeUrls = new ArrayList<Integer>(); // not let recommending. very very frequent URLs. They bias the results. 
		//var_noProposeUrls.add(11); // homepage for example
		float[][] var_rolesW = {{ 0f, 0f, 0f},
								{ 0f, 0f, 0f},
								{ 0f, 0f, 0f}};
		// topic abstraction parameters
		float var_topicmatch = 1f; // topic match
		
		
		/////////////////////////////////////////////////////////////////////////////////////
		
		// LOAD PREPROCESS DATA
		Website.setWorkDirectory(var_preprocessingWD);
		Website.load(); // Load "preprocessingWD/_Website.javaData"
		WebAccessSequencesUHC.setWorkDirectory(var_preprocessingWD);
		//WebAccessSequences.loadStructure(); // Load "preprocessingWD/_i_requests.javaData"
		WebAccessSequences.loadSequences(); // Load "preprocessingWD/_sequences.javaData"
		
		// LOAD DATABASE
		// Load "databaseWD/_sessionIDs.javaData"
		// Load "databaseWD/_sequencesUHC.javaData"
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		database.loadDatabase(var_databaseWD);
		// sequences compound by request indexes:
		ArrayList<Long> var_sampleSessionIDs = database.getSessionsIDs();
		// sequences compound by instantiation of requests into URL and role:
		ArrayList<String[]> var_sequencesUHC = database.getInstantiatedSequences();
		
		// LOAD DISTANCE MATRIX
		A012MainClassDistanceMatrixED dm = new A012MainClassDistanceMatrixED();
		dm.loadDistanceMatrix(var_databaseWD + var_dmWD); // Load "databaseWD/dmWD/_matrix.javaData"
		Matrix var_matrix = dm.getMatrix();
		
		// LOAD HOLD-OUT
		// Load "validationWD/_holdoutTrain.javaData"
		// Load "validationWD/_holdoutValidation.javaData"
		// Load "validationWD/_holdoutTest.javaData"
		ModelValidationHoldOut honestmodelval = new ModelValidationHoldOut();
		honestmodelval.load(var_validationWD);
		// set of sequences compound by request indexes:
		ArrayList<ArrayList<Long>> var_trainAL = honestmodelval.getTrain();
		ArrayList<ArrayList<Long>> var_valAL   = honestmodelval.getValidation();
		ArrayList<ArrayList<Long>> var_testAL  = honestmodelval.getTest();
		
		// LOAD TOPIC INFORMATION
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objA = cont.loadUrlsTopic(var_preprocessingWD + var_url2topicFile, " ");
		ArrayList<Integer> var_urlIDs = (ArrayList<Integer>)objA[0];
		int[] var_url2topic = (int[])objA[1];
		int var_difftopics = (int)objA[2];
		
		///////////////////////////////////////////////////////////////////////
		
		
		
		// CLUSTERING: PAM
		ModelEvaluatorClustPAM modelevPAM = 
				new ModelEvaluatorClustPAM(
						var_sequencesUHC, null, 
						var_matrix,
						var_trainAL, var_valAL, var_testAL,
						var_modePrRe, var_usage2contentFile, var_urlSimilarityMatrix);
		for(int j=0; j<var_ks.length; j++){ // for each k
			int k = var_ks[j];
			String esperimentationStr = "pam" + k;
			System.out.println("[" + System.currentTimeMillis() + "] " + esperimentationStr);
			modelevPAM.buildPAM(k); // CREATE CLUSTERS
			modelevPAM.saveClusters(var_validationWD + var_clustWD + "/" + esperimentationStr + ".javaData");
			modelevPAM.writeClusters(var_validationWD + var_clustWD + "/" + esperimentationStr + ".txt");
		}
		
		// CREATE THE MEDOIDS+URLs MODEL and VALIDATE IT
		ModelEvaluatorMedoids modelevMed = 
					new ModelEvaluatorMedoids(
							var_sequencesUHC, null, 
							var_matrix,
							var_trainAL, var_valAL, var_testAL,
							var_modePrRe, var_usage2contentFile, var_urlSimilarityMatrix,
							var_noProposeUrls);
		modelevMed.setTopicParameters(
				var_urlIDs, var_url2topic, var_difftopics, 
				var_topicmatch, 
				var_clusterPartitionFile);
		modelevMed.setFmeasureBeta(var_beta);
		modelevMed.setConfusionPoints(var_confusionPoints);
		
		BufferedWriter evalWriter = A000MainClassEdPamSpadeKnnEd.openFile(var_validationWD + var_evalFile);
		// Results' header
		System.out.print("options," + modelevMed.getEvaluationHeader());
		for(int j=0; j<var_ks.length; j++){ // for each k: 150
			int k = var_ks[j];				
			String esperimentationStr = "pam" + k;
			String clustFile = var_validationWD + var_clustWD + "/" + esperimentationStr + ".javaData";
			modelevMed.loadClusters(clustFile); // LOAD CLUSTERS

			// for each SPADE: minsup: 0.2
			for(int l=0; l<var_seqweights.length; l++){
				float minsup = var_seqweights[l];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
				modelevMed.buildMedoids(minsup, true);
								
				// VALIDATION //
				String results = "";
				String resultInfo = "";	
				// for each number of recommendation
				// TEST //
				for(int ind=0; ind<var_nrecsA.length; ind++ ){ // nrec: 4
					int nrec = var_nrecsA[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";					
					modelevMed.setLineHeader(resultInfo + ";", evalWriter);
					modelevMed.setEsploitationParameters(true, var_rolesW, 100);
					results = modelevMed.computeEvaluationTest("weighted", nrec, (long)0);					
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}			
			}
		}
		
		A000MainClassEdPamSpadeKnnEd.closeFile(evalWriter);
	}
	
	
	// Utilities
	
	/**
	 * Open file.
	 *
	 * @param filename the filename
	 * @return the buffered writer
	 */
	private static BufferedWriter openFile(String filename){
		// open the file in which we save the evaluation process
		BufferedWriter evalWriter = null;
		try{
			evalWriter = new BufferedWriter(new FileWriter(filename));
		} catch(IOException ex){
			System.err.println("[FPlierni.webrecommendation.A000MainClassClustSilly] " +
					"Not possible to open the file: evaluation.txt");
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
	private static void closeFile(BufferedWriter writer){
		// close the file in which we save the evaluation process
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[[FPlierni.webrecommendation.A000MainClassClustSilly]] " +
					"Problems at closing the file: evaluation.txt");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
}
