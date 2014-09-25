package angelu.webrecommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import angelu.webrecommendation.evaluator.ModelEvaluatorMedoidsContent;
import ehupatras.webrecommendation.A100MainClassAddContent;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.modelvalidation.ModelValidationCrossValidation;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A062MainClassPamSpadeKnnEDhoTop1ContA2_noCl_En2 {

	public static void main(String[] args) {
		
		// Parameter control
		String base = "experiments_ehupatras";
		String preprocessingWD = base + "/01_preprocess";
		String logfile = "/log20000.log";
		String url2topicFile = "/URLs_to_topic.txt";
		//String urlSimilarityMatrix = "contentEnrichment/ResultadosTestuHutsa/ResSimilarity.txt";
		String urlSimilarityMatrix = "contentEnrichment/ResultadosTestuHutsa/ResSimilarity.txt";
		String urlRelationMatrix = "contentEnrichment/ResultadosTestuHutsa/ResRelations.txt";
		String clusterPartitionFile = "contentEnrichment/clusterPartitions/ClusterPartitionModua0.txt";
		String usage2contentFile = "convert_UrlIDs_content2usage/usa2cont.csv";
		String databaseWD = base + "/02_DATABASE_5";
		String dmWD = "/DM_04_edit";
		String validationWD = base + "/03_VALIDATION_5";
		String clustWD = "/pam_DM_04_edit";
		String profiWD = "/pam_DM_04_edit/spade1";
		String evalFile = "/evaluation.txt";
		String noRecURLsStr = "11,74,7,89,152";
		
		preprocessingWD = args[0];
		logfile = args[1];
		url2topicFile = args[2];
		urlSimilarityMatrix = args[3];
		urlRelationMatrix = args[4];
		clusterPartitionFile = args[5];
		usage2contentFile = args[6];
		databaseWD = args[7];
		dmWD = args[8];
		validationWD = args[9];
		clustWD = args[10];
		profiWD = args[11];
		evalFile = args[12];
		noRecURLsStr = args[13];

		
		// the URLs we do not have to recommend
		ArrayList<Integer> noProposeUrls = new ArrayList<Integer>();
		if(!noRecURLsStr.equals("-")){
			String[] noRecURLsA = noRecURLsStr.split(",");
			for(int i=0; i<noRecURLsA.length; i++){
				int norec = Integer.valueOf(noRecURLsA[i]);
				noProposeUrls.add(norec);
			}
		}
		
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		Website.load();
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		
		
		// LOAD PREPROCESSED LOGS //
		//A000MainClassPreprocess preprocess = new A000MainClassPreprocess();
		//preprocess.preprocessLogs(preprocessingWD, logfile);
		//preprocess.loadPreprocess();
		
		
		// LOAD DATABASE //
		A001MainClassCreateDatabase database = new A001MainClassCreateDatabase();
		//database.createDatabase(databaseWD);
		database.loadDatabase(databaseWD);
		ArrayList<Long> sampleSessionIDs = database.getSessionsIDs();
		ArrayList<String[]> sequencesUHC = database.getInstantiatedSequences();
		
		
		// DISTANCE MATRIX //
		A012MainClassDistanceMatrixED dm = new A012MainClassDistanceMatrixED();
		dm.loadDistanceMatrix(databaseWD + dmWD);
		Matrix matrix = dm.getMatrix();

		
		// HOLD-OUT //
		A021MainClassCrossValidation ho = new A021MainClassCrossValidation();
		ho.loadParts(validationWD, sampleSessionIDs);
		ModelValidationCrossValidation mv = ho.getParts();
		
		ArrayList<ArrayList<Long>> trainALaux = mv.getTrain();
		ArrayList<ArrayList<Long>> trainAL = new ArrayList<ArrayList<Long>>();
		trainAL.add(trainALaux.get(0));
		
		ArrayList<ArrayList<Long>> valALaux  = mv.getValidation();
		ArrayList<ArrayList<Long>> valAL  = new ArrayList<ArrayList<Long>>();
		valAL.add(valALaux.get(0));
		
		ArrayList<ArrayList<Long>> testALaux  = mv.getTest();
		ArrayList<ArrayList<Long>> testAL = new ArrayList<ArrayList<Long>>();
		testAL.add(testALaux.get(0));

		
		// MODEL VALIDATION //
		
		// Parameters to play with
		//int[] ks = {1000, 750, 500, 400, 300, 250, 200, 150, 100, 50};
		int[] ks = {150};
		//float[] seqweights = {0.05f, 0.10f, 0.15f, 0.20f};
		//float[] seqweights = {0.01f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.40f, 0.50f};
		//float[] seqweights = {0.15f, 0.20f, 0.25f, 0.30f};
		float[] seqweights = {0.20f};
		float[][] rolesW = {{ 0f, 0f, 0f},
				  			{ 0f, 0f, 0f},
				  			{ 0f, 0f, 0f}};
		
		// initialize the model evaluator
		ModelEvaluatorMedoidsContent modelev = new ModelEvaluatorMedoidsContent(
				sequencesUHC, null, 
				matrix,
				trainAL, valAL, testAL,
				noProposeUrls);
		
		// evaluation parameters
		modelev.setFmeasureBeta(1f);
		float[] confusionPoints = {0.25f,0.50f,0.75f};
		modelev.setConfusionPoints(confusionPoints);
		
		// load topic information
		A100MainClassAddContent cont = new A100MainClassAddContent();
		Object[] objA = cont.loadUrlsTopic(preprocessingWD + url2topicFile, " ");
		ArrayList<Integer> urlIDs = (ArrayList<Integer>)objA[0];
		int[] url2topic = (int[])objA[1];
		int difftopics = (int)objA[2];
		modelev.setTopicParameters(urlIDs, url2topic, difftopics, 1f, clusterPartitionFile);
				
		
		// PAM + MySPADE //
		
		// Results' header
		System.out.print("options," + modelev.getEvaluationHeader());
		
		// open the file in which we save the evaluation process
		BufferedWriter evalWriter = null;
		try{
			evalWriter = new BufferedWriter(new FileWriter(validationWD + evalFile));
		} catch(IOException ex){
			System.err.println("[angelu.webrecommendation.A053MainClassPamSpadeKnnEDholdoutTop05] " +
					"Not possible to open the file: " + evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Start generating and evaluating the model
		for(int j=0; j<ks.length; j++){
			int k = ks[j];
				
			String esperimentationStr = "pam" + k;
			
			// Load clustering
			String clustFile = validationWD + clustWD + "/" + esperimentationStr + ".javaData";
			modelev.loadClusters(clustFile);

			
			// SPADE
			for(int l=0; l<seqweights.length; l++){
				float minsup = seqweights[l];
				String esperimentationStr2 = esperimentationStr + "_minsup" + minsup;
				
				// MEDOIDS models //
				modelev.buildMedoids(minsup, true);
				
				
				// Evaluation
				String results;
				
				
				
				// VALIDATION //
				String resultInfo;
				
				// weighted by construction sequences (test sequences)
				//int[] nrecsWSTv = new int[]{4,5,10,20};
				int[] nrecsWSTv = new int[]{4};
				for(int ind=0; ind<nrecsWSTv.length; ind++ ){
					int nrec = nrecsWSTv[ind];
					// Write recommendations
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_val";
					
					modelev.setLineHeader(resultInfo + ";", evalWriter);
					modelev.setEsploitationParameters(true, rolesW, 100);
					modelev.setEsploitationParameters("ContentsA2_noCl_En2", 
							urlSimilarityMatrix, urlRelationMatrix, usage2contentFile);
					results = modelev.computeEvaluationVal("weighted", nrec, (long)0);
					
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}
			
				/*
				// unbounded
				resultInfo = esperimentationStr2 + "_unbounded_val";
				
				modelev.setLineHeader(resultInfo + ";", evalWriter);
				modelev.setEsploitationParameters(true, rolesW, 100);
				modelev.setEsploitationParameters("ContentsB2",
						urlSimilarityMatrix, urlRelationMatrix, clusterPartitionFile, usage2contentFile);
				results = modelev.computeEvaluationVal("unbounded", -1, (long)0);
				
				System.out.print(resultInfo + ",");
				System.out.print(results);
				*/
				
				
				// TEST //
				
				// weighted by construction sequences (test sequences)
				//int[] nrecsWST = new int[]{4,5,10,20};
				int[] nrecsWST = new int[]{4};
				for(int ind=0; ind<nrecsWST.length; ind++ ){
					int nrec = nrecsWST[ind];
					resultInfo = esperimentationStr2 + "_weighted" + nrec + "_test";
					
					modelev.setLineHeader(resultInfo + ";", evalWriter);
					modelev.setEsploitationParameters(true, rolesW, 100);
					modelev.setEsploitationParameters("ContentsA2_noCl_En2",
							urlSimilarityMatrix, urlRelationMatrix, usage2contentFile);
					results = modelev.computeEvaluationTest("weighted", nrec, (long)0);
					
					System.out.print(resultInfo + ",");
					System.out.print(results);
				}

				/*
				// unbounded
				resultInfo = esperimentationStr2 + "_unbounded_test";
				
				modelev.setLineHeader(resultInfo + ";", evalWriter);
				modelev.setEsploitationParameters(true, rolesW, 100);
				modelev.setEsploitationParameters("ContentsB2",
						urlSimilarityMatrix, urlRelationMatrix, clusterPartitionFile, usage2contentFile);
				results = modelev.computeEvaluationTest("unbounded", -1, (long)0);
				
				System.out.print(resultInfo + ",");
				System.out.print(results);
				*/
			}

		}
		
		// close the file in which we save the evaluation process
		try{
			evalWriter.close();
		} catch (IOException ex){
			System.err.println("[[angelu.webrecommendation.A053MainClassPamSpadeKnnEDholdoutTop05]] " +
					"Problems at closing the file: " + evalFile);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
					
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
		
	}
	
}
