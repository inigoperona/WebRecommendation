package ehupatras.webrecommendation.modelvalidation;

import ehupatras.webrecommendation.utils.*;
import java.util.*;

public class ModelValidationHoldOut {

	private String m_workdirectory = ".";
	private ArrayList<Integer> m_trainList = new ArrayList<Integer>();
	private ArrayList<Integer> m_validationList = new ArrayList<Integer>();
	private ArrayList<Integer> m_testList = new ArrayList<Integer>();
	
	public ModelValidationHoldOut(){
		
	}
	
	public void prepareData(ArrayList<Integer> sessionsID, int ptrain, int pvalidation, int ptest){
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
		m_trainList = new ArrayList<Integer>();
		for(i=0; i<until; i++){
			m_trainList.add(sessionsID.get(i));
		}
		
		// parameters validation part
		until = i + positionValUntil;
		m_validationList = new ArrayList<Integer>();
		for( ; i<until; i++){
			m_validationList.add(sessionsID.get(i));
		}
		
		// test
		until = i + positionTestUntil;
		m_testList = new ArrayList<Integer>();
		for( ; i<until; i++){
			m_testList.add(sessionsID.get(i));
		}
	}
	
	public ArrayList<Integer> getTrain(){
		return m_trainList;
	}
	
	public ArrayList<Integer> getValidation(){
		return m_validationList;
	} 
	
	public ArrayList<Integer> getTest(){
		return m_testList;
	}
	
	public void save(String workdirectory){
		m_workdirectory = workdirectory;
		SaveLoadObjects slo = new SaveLoadObjects();
		slo.save(m_trainList,      m_workdirectory + "/_holdoutTrain.javaData");
		slo.save(m_validationList, m_workdirectory + "/_holdoutValidation.javaData");
		slo.save(m_testList,       m_workdirectory + "/_holdoutTest.javaData");
	}
	
	public void load(String workdirectory){
		m_workdirectory = workdirectory;
		SaveLoadObjects slo = new SaveLoadObjects();
		m_trainList =      (ArrayList<Integer>)slo.load(m_workdirectory + "/_holdoutTrain.javaData");
		m_validationList = (ArrayList<Integer>)slo.load(m_workdirectory + "/_holdoutValidation.javaData");
		m_testList =       (ArrayList<Integer>)slo.load(m_workdirectory + "/_holdoutTest.javaData");
	}
	
}
