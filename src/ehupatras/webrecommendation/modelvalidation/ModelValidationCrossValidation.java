package ehupatras.webrecommendation.modelvalidation;

import java.util.ArrayList;

import ehupatras.webrecommendation.utils.SaveLoadObjects;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelValidationCrossValidation.
 */
public class ModelValidationCrossValidation extends ModelValidation {

	/** The m_train list. */
	private ArrayList<ArrayList<Long>> m_trainList = new ArrayList<ArrayList<Long>>();
	
	/** The m_validation list. */
	private ArrayList<ArrayList<Long>> m_validationList = new ArrayList<ArrayList<Long>>();
	
	/** The m_test list. */
	private ArrayList<ArrayList<Long>> m_testList = new ArrayList<ArrayList<Long>>();
	
	/** The m_n fold. */
	private int m_nFold = 10;
	
	/**
	 * Prepare data.
	 *
	 * @param sessionsID the sessions id
	 * @param nftrain the nftrain
	 * @param nfvalidation the nfvalidation
	 * @param nftest the nftest
	 * @param nFold the n fold
	 */
	public void prepareData(ArrayList<Long> sessionsID, int nftrain, int nfvalidation, int nftest, int nFold){
		m_nFold = nFold;
		
		// number of cases we have in the database
		int ncases = sessionsID.size();
		
		// create folds
		ArrayList<ArrayList<Long>> folds = new ArrayList<ArrayList<Long>>();
		int fsize = Math.round((float)ncases/(float)m_nFold);
		int until = 0;
		for(int i=0; i<m_nFold; i++){
			ArrayList<Long> foldi = new ArrayList<Long>();
			for(int j=until; j<(until+fsize); j++){
				foldi.add(sessionsID.get(j));
			}
			folds.add(foldi);
			until = until + fsize;
		}
		
		// mix folds
		int start = 0;
		for(int i=0; i<m_nFold; i++){
			int start2 = start;
			
			// train
			ArrayList<Long> trL = new ArrayList<Long>();
			for(int j=0; j<nftrain; j++){
				ArrayList<Long> fo = folds.get(start2);
				for(int k=0; k<fo.size(); k++){
					trL.add(fo.get(k));
				}
				start2 = this.getNextFold(start2);
			}
			
			// validation
			ArrayList<Long> vaL = new ArrayList<Long>();
			for(int j=0; j<nfvalidation; j++){
				ArrayList<Long> fo = folds.get(start2);
				for(int k=0; k<fo.size(); k++){
					vaL.add(fo.get(k));
				}
				start2 = this.getNextFold(start2);
			}
			
			// test
			ArrayList<Long> tsL = new ArrayList<Long>();
			for(int j=0; j<nftest; j++){
				ArrayList<Long> fo = folds.get(start2);
				for(int k=0; k<fo.size(); k++){
					tsL.add(fo.get(k));
				}
				start2 = this.getNextFold(start2);
			}
			
			// save the sessions indexes
			m_trainList.add(trL);
			m_validationList.add(vaL);
			m_testList.add(tsL);
			
			// update the circular
			start = this.getNextFold(start);
		}
		
	}


	/**
	 * Gets the next fold.
	 *
	 * @param ifold the ifold
	 * @return the next fold
	 */
	private int getNextFold(int ifold){
		int nextfold = (ifold + 1) % m_nFold;
		return nextfold;
	}
	
	/**
	 * Gets the train.
	 *
	 * @return the train
	 */
	public ArrayList<ArrayList<Long>> getTrain(){
		return m_trainList;
	}
	
	/**
	 * Gets the validation.
	 *
	 * @return the validation
	 */
	public ArrayList<ArrayList<Long>> getValidation(){
		return m_validationList;
	} 
	
	/**
	 * Gets the test.
	 *
	 * @return the test
	 */
	public ArrayList<ArrayList<Long>> getTest(){
		return m_testList;
	}
	
	/**
	 * Save.
	 *
	 * @param workdirectory the workdirectory
	 */
	public void save(String workdirectory){
		m_workdirectory = workdirectory;
		SaveLoadObjects slo = new SaveLoadObjects();
		slo.save(m_trainList,      m_workdirectory + "/_crossvalTrain.javaData");
		slo.save(m_validationList, m_workdirectory + "/_crossvalValidation.javaData");
		slo.save(m_testList,       m_workdirectory + "/_crossvalTest.javaData");
	}
	
	/**
	 * Load.
	 *
	 * @param workdirectory the workdirectory
	 */
	public void load(String workdirectory){
		m_workdirectory = workdirectory;
		SaveLoadObjects slo = new SaveLoadObjects();
		m_trainList =      (ArrayList<ArrayList<Long>>)slo.load(m_workdirectory + "/_crossvalTrain.javaData");
		m_validationList = (ArrayList<ArrayList<Long>>)slo.load(m_workdirectory + "/_crossvalValidation.javaData");
		m_testList =       (ArrayList<ArrayList<Long>>)slo.load(m_workdirectory + "/_crossvalTest.javaData");
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		ModelValidationCrossValidation mvcv = new ModelValidationCrossValidation();
		System.out.println("0: " + mvcv.getNextFold(0));
		System.out.println("1: " + mvcv.getNextFold(1));
		System.out.println("2: " + mvcv.getNextFold(2));
		System.out.println("8: " + mvcv.getNextFold(8));
		System.out.println("9: " + mvcv.getNextFold(9));
		System.out.println("10: " + mvcv.getNextFold(10));
		
	}
	
}
