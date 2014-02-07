package ehupatras.webrecommendation.modelvalidation;

import java.util.*;

public class ModelValidationHoldOut {

	public Object[] getTrainTest(ArrayList<Integer> sessionsID, int ptrain, int pvalidation, int ptest){
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
		ArrayList<Integer> trainlist = new ArrayList<Integer>();
		for(i=0; i<until; i++){
			trainlist.add(sessionsID.get(i));
		}
		// parameters validation part
		until = i + positionValUntil;
		ArrayList<Integer> validationlist = new ArrayList<Integer>();
		for( ; i<until; i++){
			validationlist.add(sessionsID.get(i));
		}
		// test
		until = i + positionTestUntil;
		ArrayList<Integer> testlist = new ArrayList<Integer>();
		for( ; i<until; i++){
			testlist.add(sessionsID.get(i));
		}
		
		// return the parts we have created
		Object[] parts = new Object[3];
		parts[0] = trainlist;
		parts[1] = validationlist;
		parts[2] = testlist;
		return parts;
	}
	
}
