package ehupatras.webrecommendation.modelvalidation;

import ehupatras.webrecommendation.utils.*;

import java.util.*;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelValidationHoldOut.
 */
public class ModelValidationHoldOut extends ModelValidation {

	/** The m_train list. */
	private ArrayList<String> m_trainList = new ArrayList<String>();
	
	/** The m_validation list. */
	private ArrayList<String> m_validationList = new ArrayList<String>();
	
	/** The m_test list. */
	private ArrayList<String> m_testList = new ArrayList<String>();
	
	/**
	 * Instantiates a new model validation hold out.
	 */
	public ModelValidationHoldOut(){
		
	}
	
	/**
	 * Prepare data.
	 *
	 * @param sessionsID the sessions id
	 * @param ptrain the ptrain
	 * @param pvalidation the pvalidation
	 * @param ptest the ptest
	 */
	public void prepareData(ArrayList<String> sessionsID, int ptrain, int pvalidation, int ptest){
		// number of cases we have in the database
		int ncases = sessionsID.size();
		
		// until which position is each part
		int positionTrainUntil = Math.round((float)ncases*((float)ptrain/(float)100));
		int positionValUntil = Math.round((float)ncases*((float)pvalidation/(float)100));
		int positionTestUntil = Math.round((float)ncases*((float)ptest/(float)100));
		
		// create the parts we are interested with corresponding sessionIDs
		int i = 0;
		
		// train
		int until = i + positionTrainUntil;
		m_trainList = new ArrayList<String>();
		for(i=0; i<until; i++){
			m_trainList.add(sessionsID.get(i));
		}
		
		// parameters validation part
		until = i + positionValUntil;
		m_validationList = new ArrayList<String>();
		for( ; i<until; i++){
			m_validationList.add(sessionsID.get(i));
		}
		
		// test
		until = i + positionTestUntil;
		m_testList = new ArrayList<String>();
		for( ;i<until && i<ncases; i++){
			m_testList.add(sessionsID.get(i));
		}
	}
	
	/**
	 * Gets the train.
	 *
	 * @return the train
	 */
	public ArrayList<ArrayList<String>> getTrain(){
		ArrayList<ArrayList<String>> trainAL = new ArrayList<ArrayList<String>>();
		trainAL.add(m_trainList);
		return trainAL;
	}
	
	/**
	 * Gets the validation.
	 *
	 * @return the validation
	 */
	public ArrayList<ArrayList<String>> getValidation(){
		ArrayList<ArrayList<String>> validationAL = new ArrayList<ArrayList<String>>();
		validationAL.add(m_validationList);
		return validationAL;
	} 
	
	/**
	 * Gets the test.
	 *
	 * @return the test
	 */
	public ArrayList<ArrayList<String>> getTest(){
		ArrayList<ArrayList<String>> testAL = new ArrayList<ArrayList<String>>();
		testAL.add(m_testList);
		return testAL;
	}
	
	/**
	 * Save.
	 *
	 * @param workdirectory the workdirectory
	 */
	public void save(String workdirectory){
		m_workdirectory = workdirectory;
		SaveLoadObjects slo = new SaveLoadObjects();
		slo.save(m_trainList,      m_workdirectory + "/_holdoutTrain.javaData");
		slo.save(m_validationList, m_workdirectory + "/_holdoutValidation.javaData");
		slo.save(m_testList,       m_workdirectory + "/_holdoutTest.javaData");
	}
	
	/**
	 * Load.
	 *
	 * @param workdirectory the workdirectory
	 */
	public void load(String workdirectory){
		m_workdirectory = workdirectory;
		SaveLoadObjects slo = new SaveLoadObjects();
		m_trainList =      (ArrayList<String>)slo.load(m_workdirectory + "/_holdoutTrain.javaData");
		m_validationList = (ArrayList<String>)slo.load(m_workdirectory + "/_holdoutValidation.javaData");
		m_testList =       (ArrayList<String>)slo.load(m_workdirectory + "/_holdoutTest.javaData");
	}
	
	/**
	 * Prints the hold out.
	 */
	public void printHoldOut(){
		for(int i=0; i<m_trainList.size(); i++){
			System.out.println("train:" + m_trainList.get(i));
		}
		for(int i=0; i<m_validationList.size(); i++){
			System.out.println("validation:" + m_validationList.get(i));
		}
		for(int i=0; i<m_testList.size(); i++){
			System.out.println("test:" + m_testList.get(i));
		}
	}
	
}
