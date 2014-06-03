package angelu.webrecommendation;

import java.util.ArrayList;
import ehupatras.webrecommendation.modelvalidation.ModelValidationCrossValidation;
import ehupatras.webrecommendation.structures.WebAccessSequencesUHC;
import ehupatras.webrecommendation.structures.Website;

public class A021MainClassCrossValidation {
	
	private ModelValidationCrossValidation m_parts;
	
	public void createParts(String validationWD,
			ArrayList<Long> sampleSessionIDs){
		System.out.println("CROSS VALIDATION");
		ModelValidationCrossValidation honestmodelval = new ModelValidationCrossValidation();
		honestmodelval.prepareData(sampleSessionIDs, 70, 0, 30, 10);
		honestmodelval.save(validationWD);
		m_parts = honestmodelval;
	}
	
	public void loadParts(String validationWD,
			ArrayList<Long> sampleSessionIDs){
		System.out.println("CROSS VALIDATION");
		ModelValidationCrossValidation honestmodelval = new ModelValidationCrossValidation();
		honestmodelval.load(validationWD);
		m_parts = honestmodelval;
	}
	
	
	public ModelValidationCrossValidation getParts(){
		return m_parts;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Parameter control
		String preprocessingWD = "/home/aizea/ecplipse20140529/experiments/01_preprocess";
		String logfile = "/kk.log";
		String databaseWD = "/home/aizea/ecplipse20140529/experiments/02_DATABASE_5";
		String dmWD = "/DM_04_edit";
		dmWD = "";
		String validationWD = "/home/aizea/ecplipse20140529/experiments/03_VALIDATION_5";
		preprocessingWD = args[0];
		logfile = args[1];
		databaseWD = args[2];
		dmWD = args[3];
		validationWD = args[4];
		
		// initialize the data structure
		WebAccessSequencesUHC.setWorkDirectory(preprocessingWD);
		Website.setWorkDirectory(preprocessingWD);
		
		// take the start time of the program
		long starttimeprogram = System.currentTimeMillis();
		long starttime;
		long endtime;
		
		
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
		//A010MainClassDistanceMatrixEuclidean dm = new A010MainClassDistanceMatrixEuclidean();
		//Matrix matrix = dm.getMatrix();

		
		// HOLD-OUT //
		A021MainClassCrossValidation ho = new A021MainClassCrossValidation();
		ho.createParts(validationWD, sampleSessionIDs);
		ModelValidationCrossValidation mv = ho.getParts();
		ArrayList<ArrayList<Long>> trainAL = mv.getTrain();
		ArrayList<ArrayList<Long>> valAL   = mv.getValidation();
		ArrayList<ArrayList<Long>> testAL  = mv.getTest();
		
		
		// ending the program
		long endtimeprogram = System.currentTimeMillis();
		System.out.println("The program has needed " + (endtimeprogram-starttimeprogram)/1000 + " seconds.");
	}
	
}
