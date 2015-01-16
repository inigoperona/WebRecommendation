package ehupatras.webrecommendation.recommender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderSuffixTree2.
 */
public class RecommenderSuffixTree2 implements Recommender {

	// Suffix Tree
	/** The m_st. */
	private MySuffixTree m_st;
	
	// pointers
	/** The m_pointer node. */
	private int m_pointerNode = 0;
	
	/** The m_pointer label. */
	private int m_pointerLabel = 0;
	
	// utils
	/** The m_failure mode. */
	private int m_failureMode = 1;
	
	/** The m_max memory. */
	private int m_maxMemory = 1000;
	
	/** The m_n failures. */
	private int m_nFailures = 0;
	
	/** The m_normalization mode. */
	private int m_normalizationMode = 0;
	
	// creators
	/**
	 * Instantiates a new recommender suffix tree2.
	 *
	 * @param st the st
	 * @param failuremode the failuremode
	 * @param maxMemory the max memory
	 * @param normalizationMode the normalization mode
	 */
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
	
	/**
	 * Instantiates a new recommender suffix tree2.
	 *
	 * @param st the st
	 * @param failuremode the failuremode
	 */
	public RecommenderSuffixTree2(
				MySuffixTree st, 
				int failuremode){
		m_st = st;
		m_failureMode = failuremode;
		m_maxMemory = 1000;
		m_normalizationMode = 0;
	}
	
	// update method
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#update(java.util.ArrayList, java.lang.String, boolean, boolean)
	 */
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
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNumberOfFailures()
	 */
	public int getNumberOfFailures(){
		return m_nFailures;
	}
		
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#reset()
	 */
	public void reset(){
		this.gotoroot();
		m_nFailures = 0;
	}
	
	// FAILURE FUNCTIONS
	
	/**
	 * Gotoroot.
	 *
	 * @return the array list
	 */
	private ArrayList<String> gotoroot(){
		m_pointerNode = 0;
		m_pointerLabel = 0;
		return (new ArrayList<String>());
	}
	
	/**
	 * Goto longest suffixes.
	 *
	 * @param waydone the waydone
	 * @param newstep the newstep
	 * @return the array list
	 */
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
	
	/**
	 * Goto longest prefixes.
	 *
	 * @param waydone the waydone
	 * @param newstep the newstep
	 * @return the array list
	 */
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
	
	/**
	 * Gets the nextpossible steps.
	 *
	 * @return the nextpossible steps
	 */
	protected Object[] getNextpossibleSteps(){
		return this.getNextpossibleSteps(m_pointerNode, m_pointerLabel);
	}
	
	/**
	 * Gets the nextpossible steps.
	 *
	 * @param pointerNode the pointer node
	 * @param pointerLabel the pointer label
	 * @return the nextpossible steps
	 */
	protected Object[] getNextpossibleSteps(int pointerNode, int pointerLabel){
		Object[] result;
		if(m_normalizationMode==1){ // norm1
			result = m_st.getNextpossibleStepsNorm1(pointerNode, pointerLabel);
		} else if(m_normalizationMode==2){ // norm2, creating new edges
			result = m_st.getNextpossibleStepsNorm2(pointerNode, pointerLabel);
		} else { // frequencies
			result = m_st.getNextpossibleStepsFrequencies(pointerNode, pointerLabel);
		}		
		return result;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsUnbounded()
	 */
	public ArrayList<String> getNextpossibleStepsUnbounded(){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> listOfUrls = (ArrayList<String>)objA[0];
		return listOfUrls;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsRandom(int, long)
	 */
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
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedTrain(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone){
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> nextsteps = (ArrayList<String>)objA[0];
		ArrayList<Float> freqs = (ArrayList<Float>)objA[1];
		
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps.size());
		ArrayList<String> recos = RecommenderSuffixTree2.getTheMostWeightedFloatURLs(realNreco, nextsteps, freqs);		
		
		// return the most weighted sequences
		return recos;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedTest(int)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nRecos){
		return this.getNextpossibleStepsWeightedTrain(nRecos, null);
	}

	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeighted(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleStepsWeightedTrain(nRecos, waydone);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsMarkov(int, java.util.ArrayList, java.util.ArrayList)
	 */
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
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedByOriginalSequences(int)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nRecos){
		Object[] objA2 = this.getNextpossibleSteps();
		ArrayList<String> nextsteps = (ArrayList<String>)objA2[0];
		ArrayList<Float> listOfWeightsAL = (ArrayList<Float>)objA2[1];
		
		// select the most weighted sequences
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps.size());
		ArrayList<String> recos = RecommenderSuffixTree2.getTheMostWeightedFloatURLs(realNreco, nextsteps, listOfWeightsAL);
		
		// return the most weighted sequences
		return recos;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedEnrichWithStep1(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedEnrichWithStep1(int nRecos, ArrayList<String> waydone){
		// RECOMMENDATIONS DEPTH IN THE SUFFIX TREE //
		Object[] objA1 = this.getNextpossibleSteps();
		ArrayList<String> nextsteps1 = (ArrayList<String>)objA1[0];
		ArrayList<Float> listOfWeights1 = (ArrayList<Float>)objA1[1];
				
		// select the most weighted sequences
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps1.size());
		ArrayList<String> recos = RecommenderSuffixTree2.getTheMostWeightedFloatURLs(realNreco, nextsteps1, listOfWeights1);
		
		
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
			ArrayList<String> recos3 = RecommenderSuffixTree2.getTheMostWeightedFloatURLs(1000, nextsteps3, listOfWeights3);
		
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
	
	/**
	 * Gets the the most weighted int ur ls.
	 *
	 * @param nrec the nrec
	 * @param list the list
	 * @param frequencies the frequencies
	 * @return the the most weighted int ur ls
	 */
	protected static ArrayList<String> getTheMostWeightedIntURLs(
			int nrec, 
			ArrayList<String> list, 
			ArrayList<Integer> frequencies){
		ArrayList<Float> freqs = new ArrayList<Float>();
		for(int i=0; i<frequencies.size(); i++){
			int valI = frequencies.get(i).intValue();
			float valF = (float)valI;
			freqs.add(valF);
		}
		return RecommenderSuffixTree2.getTheMostWeightedFloatURLs(nrec, list, freqs);
	}
	
	/**
	 * Gets the the most weighted float ur ls.
	 *
	 * @param nrec the nrec
	 * @param list the list
	 * @param frequencies the frequencies
	 * @return the the most weighted float ur ls
	 */
	protected static ArrayList<String> getTheMostWeightedFloatURLs(
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
	
	/**
	 * Gets the suffix tree.
	 *
	 * @return the suffix tree
	 */
	public MySuffixTree getSuffixTree(){
		return m_st;
	}
	
}
