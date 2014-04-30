package ehupatras.webrecommendation.recommender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;

public class RecommenderSuffixTree2 implements Recommender {

	// Suffix Tree
	private MySuffixTree m_st;
	
	// pointers
	private int m_pointerNode = 0;
	private int m_pointerLabel = 0;
	
	// utils
	private int m_failureMode = 1;
	private int m_maxMemory = 1000;
	private int m_nFailures = 0;
	private int m_normalizationMode = 0;
	
	// creators
	public RecommenderSuffixTree2(
				MySuffixTree st, 
				int failuremode, 
				int maxMemory,
				int normalizationMode){
		m_st = st;
		m_failureMode = failuremode;
		m_maxMemory = maxMemory;
		m_normalizationMode = normalizationMode;
	}
	
	public RecommenderSuffixTree2(
				MySuffixTree st, 
				int failuremode){
		m_st = st;
		m_failureMode = failuremode;
		m_maxMemory = 1000;
		m_normalizationMode = 0;
	}
	
	// update method
	public ArrayList<String> update(
				ArrayList<String> waydone, 
				String newstep, 
				boolean incrWeigh,
				boolean performFailureFunction){
		// do the step
		int[] pointers = m_st.doStep(m_pointerNode, m_pointerLabel, newstep);
		int pointerNode = pointers[0];
		int pointerLabel = pointers[1];
		
		if(pointerNode==-1){ //
			// it is not possible to run the way
			// failure function
			m_nFailures++;
			ArrayList<String> subway;
			if(m_failureMode==1){
				subway = this.gotoLongestSuffixes(waydone, newstep);
			} else if(m_failureMode==2){
				subway = this.gotoLongestPrefixes(waydone, newstep);
			} else {
				subway = this.gotoroot();
			}
			return subway;
		} else {
			// is runnable the way
			m_pointerNode = pointerNode;
			m_pointerLabel = pointerLabel;
			waydone.add(newstep);
			return waydone;
		}
	}
	
	// UTILS
	
	public int getNumberOfFailures(){
		return m_nFailures;
	}
		
	public void reset(){
		this.gotoroot();
		m_nFailures = 0;
	}
	
	// FAILURE FUNCTIONS
	
	private ArrayList<String> gotoroot(){
		m_pointerNode = 0;
		m_pointerLabel = 0;
		return (new ArrayList<String>());
	}
	
	private ArrayList<String> gotoLongestSuffixes(ArrayList<String> waydone, String newstep){
		// the way we want to perform in the suffix tree
		ArrayList<String> waydone2 = (ArrayList<String>)waydone.clone();
		waydone2.add(newstep);
		// take the last clicks as a suffix
		int startInd = waydone2.size() - m_maxMemory;
		startInd = startInd<0 ? 0 : startInd; 
		// the running of the way in the Suffix Tree
		boolean runnable = false;
		this.gotoroot();
		ArrayList<String> suffix = new ArrayList<String>();
		for(int i=startInd; i<waydone2.size(); i++){ // for each try
			suffix = new ArrayList<String>();
			for(int j=i; j<waydone2.size(); j++){ // create a smaller suffix
				String step = waydone2.get(j);
				suffix.add(step);
			}
			int[] pointers = m_st.performWay(suffix);
			int pointerNode = pointers[0];
			int pointerLabel = pointers[1];
			if(pointerNode!=-1){
				m_pointerNode = pointerNode;
				m_pointerLabel = pointerLabel;
				runnable = true;
				break;
			}
		}
		// return the way we have done in the suffix tree
		if(runnable){
			return suffix;
		} else {
			return (new ArrayList<String>());
		}
	}
	
	private ArrayList<String> gotoLongestPrefixes(ArrayList<String> waydone, String newstep){
		// the way we want to perform in the suffix tree
		ArrayList<String> waydone2 = (ArrayList<String>)waydone.clone();
		waydone2.add(newstep);
		// the running of the way in the Suffix Tree
		boolean runnable = false;
		this.gotoroot();
		ArrayList<String> preffix = new ArrayList<String>();
		for(int i=waydone2.size()-1; i>=0; i--){ // for each try
			preffix = new ArrayList<String>();
			for(int j=0; j<=i; j++){ // create a smaller suffix
				String step = waydone2.get(j);
				preffix.add(step);
			}
			int[] pointers = m_st.performWay(preffix);
			int pointerNode = pointers[0];
			int pointerLabel = pointers[1];
			if(pointerNode!=-1){
				m_pointerNode = pointerNode;
				m_pointerLabel = pointerLabel;
				runnable = true;
				break;
			}
		}
		// return the way we have done in the suffix tree
		if(runnable){
			return preffix;
		} else {
			return (new ArrayList<String>());
		}
	}
	
	// RECOMMENDATIONS
	
	protected Object[] getNextpossibleSteps(){
		return this.getNextpossibleSteps(m_pointerNode, m_pointerLabel);
	}
	
	protected Object[] getNextpossibleSteps(int pointerNode, int pointerLabel){
		if(m_normalizationMode==1){ // norm1
			return m_st.getNextpossibleStepsNorm1(m_pointerNode, m_pointerLabel);
		} else if(m_normalizationMode==2){ // norm2, creating new edges
			return m_st.getNextpossibleStepsNorm2(m_pointerNode, m_pointerLabel);
		} else { // frequencies
			return m_st.getNextpossibleStepsFrequencies(pointerNode, pointerLabel);
		}
	}
	
	public ArrayList<String> getNextpossibleStepsUnbounded(){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> listOfUrls = (ArrayList<String>)objA[0];
		return listOfUrls;
	}
	
	public ArrayList<String> getNextpossibleStepsRandom(int nReco, long seed){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> list = (ArrayList<String>)objA[0];
		int realNreco = Math.min(nReco, list.size());
		ArrayList<String> list2 = new ArrayList<String>(); 
		Random rand = new Random(seed);
		for(int i=0; i<realNreco; i++){
			int pos = rand.nextInt(list.size());
			list2.add(list.get(pos));
		}
		return list2;
	}
	
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> nextsteps = (ArrayList<String>)objA[0];
		ArrayList<Float> freqs = (ArrayList<Float>)objA[1];
		
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps.size());
		ArrayList<String> recos = this.getTheMostWeightedURLs(realNreco, nextsteps, freqs);		
		
		// return the most weighted sequences
		return recos;
	}
	
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nRecos){
		return this.getNextpossibleStepsWeightedTrain(nRecos, null);
	}

	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleStepsWeightedTrain(nRecos, waydone);
	}
	
	public ArrayList<String> getNextpossibleStepsMarkov(int nRecos, ArrayList<String> waydone, ArrayList<String> listMarkov){
		// get Suffix Tree recommendation
		ArrayList<String> listST = this.getNextpossibleStepsWeightedTrain(nRecos, waydone);

		// merge the two recommendations
		ArrayList<String> recos = new ArrayList<String>();
		int counter = 0;
		int i = 0;
		while(counter<nRecos){
			boolean hasMoreElemsST = listST.size()>i;
			boolean hasMoreElemsM = listMarkov.size()>i;
			if(!hasMoreElemsST && !hasMoreElemsM){ break;}
			if(hasMoreElemsST){
				String url1 = listST.get(i);
				if(!recos.contains(url1)){
					recos.add(url1);
					counter++;
				}
			}
			if(hasMoreElemsM){
				String url2 = listMarkov.get(i);
				if(!recos.contains(url2)){
					recos.add(url2);
					counter++;
				}
			}
			i++;
		}
		
		// return the most weighted sequences
		return recos;
	}
	
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nRecos){
		Object[] objA2 = this.getNextpossibleSteps();
		ArrayList<String> nextsteps = (ArrayList<String>)objA2[0];
		ArrayList<Float> listOfWeightsAL = (ArrayList<Float>)objA2[1];
		
		// select the most weighted sequences
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps.size());
		ArrayList<String> recos = this.getTheMostWeightedURLs(realNreco, nextsteps, listOfWeightsAL);
		
		// return the most weighted sequences
		return recos;
	}
	
	public ArrayList<String> getNextpossibleStepsWeightedEnrichWithStep1(int nRecos, ArrayList<String> waydone){
		// RECOMMENDATIONS DEPTH IN THE SUFFIX TREE //
		
		Object[] objA1 = this.getNextpossibleSteps();
		ArrayList<String> nextsteps1 = (ArrayList<String>)objA1[0];
		ArrayList<Float> listOfWeights1 = (ArrayList<Float>)objA1[1];
				
		// select the most weighted sequences
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps1.size());
		ArrayList<String> recos = this.getTheMostWeightedURLs(realNreco, nextsteps1, listOfWeights1);
		
		
		// RECOMMENDATIONS SHALLOW (1-step) IN THE SUFFIX TREE //
		
		// Enrich the recommendations with the URLs available in the 1st step
		if(recos.size()<nRecos && waydone.size()>0){
			// get 1-step recommendations
			String laststep = waydone.get(waydone.size()-1);
			int[] pointers = m_st.doStep(0, 0, laststep);
			int pointerNode = pointers[0];
			int pointerLabel = pointers[1];
			Object[] objA3 = this.getNextpossibleSteps(pointerNode, pointerLabel);
			ArrayList<String> nextsteps3 = (ArrayList<String>)objA3[0];
			ArrayList<Float> listOfWeights3 = (ArrayList<Float>)objA3[1];
			
			// get the recommendations ordered
			ArrayList<String> recos3 = this.getTheMostWeightedURLs(1000, nextsteps3, listOfWeights3);
		
			// ADD THE RECOMMENDATIONS TO THE LIST //
			int i = 0;
			while(recos.size()<nRecos){
				if(i<recos3.size()){
					String url = recos3.get(i);
					if(!recos.contains(url)){
						recos.add(url);
					}
				} else {
					break;
				}
				i++;
			}
		}
		
		// return the most weighted sequences
		return recos;
	}
	
	protected ArrayList<String> getTheMostWeightedURLs(
				int nrec, 
				ArrayList<String> list, 
				ArrayList<Float> frequencies){
		ArrayList<String> recos = new ArrayList<String>();
		
		// order the frequencies of searched sequences of the way 
		float[] frequencies2 = new float[frequencies.size()];
		for(int i=0; i<frequencies.size(); i++){
			frequencies2[i] = frequencies.get(i);
		}
		Arrays.sort(frequencies2);
		
		boolean[] isusedA = new boolean[frequencies.size()];
		Arrays.fill(isusedA, false);	
		for(int i=frequencies2.length-1; i>=0; i--){
			float freqmax = frequencies2[i];
			for(int j=0; j<frequencies.size(); j++){
				if(!isusedA[j]){
					if(freqmax==frequencies.get(j)){
						recos.add(list.get(j));
						isusedA[j] = true;
						break;
					}
				}
			}
			if(recos.size()==nrec){
				break;
			}
		}	
		return recos;
	}
	
	public MySuffixTree getSuffixTree(){
		return m_st;
	}
	
}
