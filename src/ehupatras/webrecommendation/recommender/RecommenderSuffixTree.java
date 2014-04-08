package ehupatras.webrecommendation.recommender;

import ehupatras.suffixtree.stringarray.Edge;
import ehupatras.suffixtree.stringarray.EdgeBag;
import ehupatras.suffixtree.stringarray.Node;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import java.util.*;

public class RecommenderSuffixTree 
				implements Recommender {
	
	private SuffixTreeStringArray m_gST;
	private Node m_pointerNode = null;
	private Edge m_pointerEdge = null;
	private int m_pointerPositionInTheEdge = 0;
	
	// failure function
	private int m_failureMode = 0;
	private int m_maxMemory = 100;
	private int m_nFailures = 0;
	
	public RecommenderSuffixTree(SuffixTreeStringArray gST){
		this(gST, 0);
	}
	
	public RecommenderSuffixTree(SuffixTreeStringArray gST, int failuremode){
		this(gST, failuremode, 100);
	}
	
	public RecommenderSuffixTree(SuffixTreeStringArray gST, int failuremode, int maxMemory){
		m_gST = gST;
		m_pointerNode = m_gST.getRoot();
		m_failureMode = failuremode;
		m_maxMemory = maxMemory;
	}
	
	
	
	// UPDATE POINTER //
	
	private ArrayList<String> updatePointer(ArrayList<String> waydone, 
						String newstep, 
						boolean incrWeigh, 
						boolean performFailureFunction){
		boolean isupdate = false;
		
		if(m_pointerNode==null && m_pointerEdge!=null){
			ArrayList<String> label = m_pointerEdge.getLabel();
			if(label.size()>m_pointerPositionInTheEdge){
				String stepST = label.get(m_pointerPositionInTheEdge);
				if(stepST.equals(newstep)){
					//m_pointerNode = null;
					//m_pointerEdge = m_pointerEdge;
					m_pointerPositionInTheEdge++;
					isupdate = true;
				}
			} else {
				m_pointerNode = m_pointerEdge.getDest();
				if(incrWeigh){
					m_gST.incrementNodeWeight(m_pointerNode);
				}
				m_pointerEdge = null;
				m_pointerPositionInTheEdge = 0;
			}
		}
		
		if(m_pointerEdge==null && m_pointerNode!=null){
			// the possible edges
			EdgeBag edges = m_pointerNode.getEdges();
			ArrayList<Edge> edgesA = edges.values();
			
			// find the correct edge
			// and initialize it in the first position
			for(int i=0; i<edgesA.size(); i++){ // for each edge
				Edge e = edgesA.get(i);
				ArrayList<String> label = e.getLabel();
				String stepST = label.get(m_pointerPositionInTheEdge);
				if(stepST.equals(newstep)){
					m_pointerNode = null;
					m_pointerEdge = e;
					m_pointerPositionInTheEdge++;
					isupdate = true;
					break;
				}
			}
		}
		
		// it was impossible to move the pointer
		if(!isupdate){
			ArrayList<String> wayRunInST;
			if(performFailureFunction){
				wayRunInST = updatefailure(waydone, newstep);
				m_nFailures++;
			} else {
				wayRunInST = updatefailure(waydone, newstep, 0);
			}
			return wayRunInST;
		} else {
			if(waydone==null){waydone = new ArrayList<String>();}
			waydone.add(newstep);
			return waydone;
		}
	}
	
	
	
	// FAILURE FUNCTIONS //
	
	private ArrayList<String> updatefailure(ArrayList<String> waydone, String nextstep, int failuremode){
		ArrayList<String> wayInST = new ArrayList<String>();
		if(failuremode==0){
			wayInST = this.gotoroot();
		} else if (failuremode==1){
			wayInST = this.gotoLongestSuffixes(waydone, nextstep);
		} else if(failuremode==2){
			wayInST = this.gotoLongestPrefixes(waydone, nextstep);
		}
		return wayInST;
	}
	
	private ArrayList<String> updatefailure(ArrayList<String> waydone, String nextstep){
		return this.updatefailure(waydone, nextstep, m_failureMode);
	}
	
	private ArrayList<String> gotoroot(){
		m_pointerNode = m_gST.getRoot();
		m_pointerEdge = null;
		m_pointerPositionInTheEdge = 0;
		return (new ArrayList<String>());
	}
	
	public ArrayList<String> update(ArrayList<String> waydone, String newstep, 
					boolean incrWeigh, boolean performFailureFunction){
		return this.updatePointer(waydone, newstep, incrWeigh, performFailureFunction);
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
			runnable = this.performTheWayInSuffixTree(suffix);
			if(runnable){break;}
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
			runnable = this.performTheWayInSuffixTree(preffix);
			if(runnable){break;}
		}
		
		// return the way we have done in the suffix tree
		if(runnable){
			return preffix;
		} else {
			return (new ArrayList<String>());
		}
	}
	
	public int getNumberOfFailures(){
		return m_nFailures;
	}
		
	public void reset(){
		this.gotoroot();
		m_nFailures = 0;
	}
	
	
	
	// GET RECOMMENDATIONS //
	
	protected Object[] getNextpossibleSteps(){
		ArrayList<String> listOfURLs = new ArrayList<String>();
		ArrayList<Integer> listOfWeights = new ArrayList<Integer>();
		
		// control if we are in a node or not
		Node destnode = null;
		ArrayList<String> label = null;
		if(m_pointerEdge!=null){
			label = m_pointerEdge.getLabel();
			destnode = m_pointerEdge.getDest();
		} else {
			destnode = m_pointerNode;
		}
		
		// take the possible steps
		if(m_pointerEdge!=null && label.size()>m_pointerPositionInTheEdge){
			listOfURLs.add(label.get(m_pointerPositionInTheEdge));
			listOfWeights.add(0);
		} else {
			// take the first element of each edge label
			EdgeBag edges = destnode.getEdges();
			ArrayList<Edge> edgesA = edges.values();
			for(int i=0; i<edgesA.size(); i++){ // for each edge
				Edge e = edgesA.get(i);
				Node d = e.getDest();
				ArrayList<String> label2 = e.getLabel();
				listOfURLs.add(label2.get(0));
				listOfWeights.add(m_gST.getNodeWeight(d));
			}
		}
		
		// return the recommendations with their weights
		Object[] objA = new Object[2];
		objA[0] = listOfURLs;
		objA[1] = listOfWeights;
		return objA;
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
		// compute the weight of the waydone + next step sequences
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> nextsteps = (ArrayList<String>)objA[0];
		ArrayList<Integer> freqs = (ArrayList<Integer>)objA[1];
		// Ok. Here if we want to weight the sequences only with train sequences
		// no test sequences, fill the ArrayList freqs to 0. 
		
		// get the next steps by weights
		Object[] objA2 = this.getUrlWeights(waydone, nextsteps, freqs);
		ArrayList<String> nextsteps2 = (ArrayList<String>)objA2[0];
		ArrayList<Integer> freqs2 = (ArrayList<Integer>)objA2[1];		
		// convert freqs to int[]
		int[] frequencies = new int[freqs2.size()];
		for(int i=0; i<freqs2.size(); i++){ frequencies[i] = freqs2.get(i); }

		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps2.size());
		ArrayList<String> recos = this.getTheMostWeightedURLs(realNreco, nextsteps, frequencies);		
		
		// return the most weighted sequences
		return recos;
	}
	
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nRecos){
		// take the weights
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> listOfUrls = (ArrayList<String>)objA[0];
		ArrayList<Integer> listOfWeights = (ArrayList<Integer>)objA[1];
		// convert frequencies to int[]
		int[] listOfWeightsA = new int[listOfWeights.size()];
		for(int i=0; i<listOfWeights.size(); i++){ listOfWeightsA[i] = listOfWeights.get(i); }
		
		// select the most weighted sequences
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, listOfUrls.size());
		ArrayList<String> recos = this.getTheMostWeightedURLs(realNreco, listOfUrls, listOfWeightsA);
		
		// return the most weighted sequences
		return recos;
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
		// Compute testset related node weights
		Object[] objA2 = this.getNextpossibleSteps();
		ArrayList<String> nextsteps = (ArrayList<String>)objA2[0];
		ArrayList<Integer> listOfWeightsAL = (ArrayList<Integer>)objA2[1];
		// convert  weights to int[]
		int[] listOfWeightsA = new int[nextsteps.size()];
		for(int i=0; i<nextsteps.size(); i++){ listOfWeightsA[i] = listOfWeightsAL.get(i); }
		
		// select the most weighted sequences
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps.size());
		ArrayList<String> recos = this.getTheMostWeightedURLs(realNreco, nextsteps, listOfWeightsA);
		
		// return the most weighted sequences
		return recos;
	}
	
	public ArrayList<String> getNextpossibleStepsWeightedEnrichWithStep1(int nRecos, ArrayList<String> waydone){
		// RECOMMENDATIONS DEPTH IN THE SUFFIX TREE //
		
		// Compute testset related node weights
		Object[] objA1 = this.getNextpossibleSteps();
		ArrayList<String> nextsteps1 = (ArrayList<String>)objA1[0];
		ArrayList<Integer> listOfWeights1 = (ArrayList<Integer>)objA1[1];
		
		// update the weights
		Object[] objA2 = this.getUrlWeights(waydone, nextsteps1, listOfWeights1);
		ArrayList<String> nextsteps2 = (ArrayList<String>)objA2[0];
		ArrayList<Integer> listOfWeights2 = (ArrayList<Integer>)objA2[1];
		// convert weights to int[]
		int[] listOfWeights2A = new int[listOfWeights2.size()];
		for(int i=0; i<listOfWeights2.size(); i++){ listOfWeights2A[i] = listOfWeights2.get(i); }
		
		// select the most weighted sequences
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps2.size());
		ArrayList<String> recos = this.getTheMostWeightedURLs(realNreco, nextsteps2, listOfWeights2A);
		
		
		// RECOMMENDATIONS SHALLOW (1-step) IN THE SUFFIX TREE //
		
		// Enrich the recommendations with the URLs available in the 1st step
		if(recos.size()<nRecos && waydone.size()>0){
			// get 1-step recommendations
			String laststep = waydone.get(waydone.size()-1);
			Object[] objA3 = this.getStep1Recommendations(laststep);
			ArrayList<String> nextsteps3 = (ArrayList<String>)objA3[0];
			ArrayList<Integer> listOfWeights3 = (ArrayList<Integer>)objA3[1];
			
			// get the recommendations ordered
			int[] listOfWeights3A = new int[listOfWeights3.size()];
			for(int i=0; i<listOfWeights3.size(); i++){ listOfWeights3A[i] = listOfWeights3.get(i); }
			ArrayList<String> recos3 = this.getTheMostWeightedURLs(1000, nextsteps3, listOfWeights3A);
		
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
	
	protected Object[] getStep1Recommendations(String step){
		// create path of one step
		ArrayList<String> waydone1step = new ArrayList<String>();  
		waydone1step.add(step);
		
		// save the pointers values
		Node pointerNode = m_pointerNode;
		Edge pointerEdge = m_pointerEdge;
		int pointerPositionInTheEdge = m_pointerPositionInTheEdge;
		
		// perform 1-step in the suffix tree
		this.performTheWayInSuffixTree(waydone1step);
		
		// get the recommendations
		Object[] objA3 = this.getNextpossibleSteps();
		ArrayList<String> nextsteps3 = (ArrayList<String>)objA3[0];
		ArrayList<Integer> listOfWeights3 = (ArrayList<Integer>)objA3[1];
		
		// update the weights
		Object[] objA4 = this.getUrlWeights(waydone1step, nextsteps3, listOfWeights3);
		ArrayList<String> nextsteps4 = (ArrayList<String>)objA4[0];
		ArrayList<Integer> listOfWeights4 = (ArrayList<Integer>)objA4[1];
		
		// restore the pointers
		m_pointerNode = pointerNode;
		m_pointerEdge = pointerEdge;
		m_pointerPositionInTheEdge = pointerPositionInTheEdge;
		
		// return 
		return objA4;
	}
	
	protected Object[] getUrlWeights(
					ArrayList<String> waydone,
					ArrayList<String> nextsteps,
					ArrayList<Integer> frequencies
					){
		// update the weights
		ArrayList<Integer> freqsR = new ArrayList<Integer>(); 
		for(int i=0; i<nextsteps.size(); i++){
			String url = nextsteps.get(i);
			int freq = frequencies.get(i);
			ArrayList<String> waydone2 = (ArrayList<String>)waydone.clone(); 
			waydone2.add(url);
			ArrayList<Integer> seqs = m_gST.search(waydone2);
			int freqi = seqs.size();
			freqsR.add(freq+freqi);
		}
		
		// return 
		Object[] objA = new Object[2];
		objA[0] = nextsteps;
		objA[1] = freqsR;
		return objA;
	}
	
	
	
	// UTILS //
	
	private boolean performTheWayInSuffixTree(ArrayList<String> waydone){		
		// run the way done  (clickstream done until now) in the suffix tree
		// and create the sequence that it is runnable in the suffix tree
		boolean runnableway = true;
		this.gotoroot();
		for(int i=0; i<waydone.size(); i++){
			String step = waydone.get(i);
			boolean stepdone = this.updatePointer(null, step, false, false).size()>0;
			if(stepdone){
				runnableway = true;
			} else {
				runnableway = false;
				break;
			}
		}
		
		if(!runnableway){
			this.gotoroot();
		}
		
		// return if it is runnable the way given
		return runnableway;
	}
	
	private ArrayList<String> getTheMostWeightedURLs(int nrec, ArrayList<String> list, int[] frequencies){
		ArrayList<String> recos = new ArrayList<String>();
		
		// order the frequencies of searched sequences of the way 
		int[] frequencies2 = frequencies.clone();
		Arrays.sort(frequencies2);
		
		boolean[] isusedA = new boolean[frequencies.length];
		Arrays.fill(isusedA, false);
		
		for(int i=frequencies2.length-1; i>=0; i--){
			int freqmax = frequencies2[i];
			for(int j=0; j<frequencies.length; j++){
				if(!isusedA[j]){
					if(freqmax==frequencies[j]){
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
	
	private ArrayList<String> getTheWayInSuffixTree(ArrayList<String> waydone){
		// save the pointers values
		Node pointerNode = m_pointerNode;
		Edge pointerEdge = m_pointerEdge;
		int pointerPositionInTheEdge = m_pointerPositionInTheEdge;
		
		// run the way done  (clickstream done until now) in the suffix tree
		// and create the sequence that it is runnable in the suffix tree
		ArrayList<String> waydone2 = new ArrayList<String>(); 
		this.gotoroot();
		for(int i=0; i<waydone.size(); i++){
			String step = waydone.get(i);
			boolean stepdone = this.updatePointer(null, step, false, false).size()>0;
			if(!stepdone){
				// if we have reset the run into the suffix tree
				// reset also the clikstream done until now
				waydone2 = new ArrayList<String>();
			} else {
				waydone2.add(step);
			}
		}
		
		// restore the pointers
		m_pointerNode = pointerNode;
		m_pointerEdge = pointerEdge;
		m_pointerPositionInTheEdge = pointerPositionInTheEdge;
		
		// return the runnable clickstream in the suffix tree
		return waydone2;
	}
	
	public SuffixTreeStringArray getSuffixTree(){
		return m_gST;
	}
	
	
	
	// MAIN TO TEST THE CLASS //
	
	public static void main(String[] args){
		// create the suffix tree
		SuffixTreeStringArray st = new SuffixTreeStringArray();
        String[] word1 = {"c", "a", "c", "a", "o"};
        String[] word2 = {"b", "a", "n", "a", "n", "a"};
        String[] word3 = {"m", "i", "l", "o"};
        String[] word4 = {"c", "a", "r"};
        st.putSequence(word1, 0);
        st.putSequence(word2, 1);
        st.putSequence(word3, 2);
        st.putSequence(word4, 3);
        st.printSuffixTree();
        
        // recommendations
        RecommenderSuffixTree rec = new RecommenderSuffixTree(st,0);
        
        Object[] objA;
        ArrayList<String> list;
        
		objA = rec.getNextpossibleSteps();
		list = (ArrayList<String>)objA[0];
        
        // recommendations from the root
        System.out.println("---");
        for(int i=0; i<list.size(); i++){ System.out.println(list.get(i)); }
        
        // paths that are exists
        rec.updatePointer(null, "c", false, false);
        rec.updatePointer(null, "a", false, false);
        objA = rec.getNextpossibleSteps();
		list = (ArrayList<String>)objA[0];
        rec.updatePointer(null, "c", false, false);
        objA = rec.getNextpossibleSteps();
		list = (ArrayList<String>)objA[0];
        System.out.println("---");
        for(int i=0; i<list.size(); i++){ System.out.println(list.get(i)); }
	
        // paths that they not exists
        rec.updatePointer(null, "z", false, false);
        objA = rec.getNextpossibleSteps();
		list = (ArrayList<String>)objA[0];
        System.out.println("---");
        for(int i=0; i<list.size(); i++){ System.out.println(list.get(i)); }
	}
	
}
