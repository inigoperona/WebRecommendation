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
	
	public RecommenderSuffixTree(SuffixTreeStringArray gST){
		m_gST = gST;
		m_pointerNode = m_gST.getRoot();
	}
	
	private boolean updatePointer(String newstep, boolean incrWeigh){
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
			updatefailure();
			return false;
		} else {
			return true;
		}
	}
	
	public boolean update(String newstep, boolean incrWeigh){
		return this.updatePointer(newstep, incrWeigh);
	}
	
	public boolean updatefailureAllSuffixes(String newstep, boolean incrWeigh){
		return this.updatePointer(newstep, incrWeigh);
	}
	
	public boolean updatefailureAllPrefixes(String newstep, boolean incrWeigh){
		return this.updatePointer(newstep, incrWeigh);
	}
		
	private void updatefailure(){
		this.gotoroot();
	}
	
	private void gotoroot(){
		m_pointerNode = m_gST.getRoot();
		m_pointerEdge = null;
		m_pointerPositionInTheEdge = 0;
	}
	
	public void reset(){
		this.gotoroot();
	}
	
	private Object[] getNextpossibleSteps(){
		ArrayList<String> listOfURLs = new ArrayList<String>();
		ArrayList<Integer> listOfWeights = new ArrayList<Integer>();
		
		// control if we are in the root or not
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
		// run the way done  (clickstream done until now) in the suffix tree
		// and create the sequence that it is runnable in the suffix tree
		ArrayList<String> waydone2 = this.getTheWayInSuffixTree(waydone);
		
		// compute the weight of the waydone + next step sequences
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> nextsteps = (ArrayList<String>)objA[0];
		int[] frequencies = new int[nextsteps.size()];
		ArrayList<String> way = (ArrayList<String>)waydone2.clone();
		for(int i=0; i<nextsteps.size(); i++){
			String nstep = nextsteps.get(i);
			way.add(nstep);
			
			// search
			ArrayList<Integer> seqs = m_gST.search(way);
			frequencies[i] = seqs.size();
			
			// put as before the sequence
			way = (ArrayList<String>)waydone2.clone();
		}

		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps.size());
		ArrayList<String> recos = this.getTheMostWeightedURLs(realNreco, nextsteps, frequencies);		
		
		// return the most weighted sequences
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
			boolean stepdone = this.updatePointer(step, false);
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
	
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nRecos){
		// take the weights
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> listOfUrls = (ArrayList<String>)objA[0];
		ArrayList<Integer> listOfWeights = (ArrayList<Integer>)objA[1];
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
		// Compute testset related node weights
		Object[] objA2 = this.getNextpossibleSteps();
		ArrayList<String> nextsteps = (ArrayList<String>)objA2[0];
		ArrayList<Integer> listOfWeights2 = (ArrayList<Integer>)objA2[1];
		
		// Compute trainset related node weights
		// run the way done  (clickstream done until now) in the suffix tree
		// and create the sequence that it is runnable in the suffix tree
		ArrayList<String> waydone2 = this.getTheWayInSuffixTree(waydone);
		// compute the weight of the waydone + next step sequences
		int[] frequencies1 = new int[nextsteps.size()];
		ArrayList<String> way = (ArrayList<String>)waydone2.clone();
		for(int i=0; i<nextsteps.size(); i++){
			String nstep = nextsteps.get(i);
			way.add(nstep);
			
			// search
			ArrayList<Integer> seqs = m_gST.search(way);
			frequencies1[i] = seqs.size();
			
			// put as before the sequence
			way = (ArrayList<String>)waydone2.clone();
		}
		
		// Sum both frequencies
		int[] listOfWeightsA = new int[nextsteps.size()];
		for(int i=0; i<nextsteps.size(); i++){
			listOfWeightsA[i] = listOfWeights2.get(i) + frequencies1[i];
		}
		
		// select the most weighted sequences
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps.size());
		ArrayList<String> recos = this.getTheMostWeightedURLs(realNreco, nextsteps, listOfWeightsA);
		
		// return the most weighted sequences
		return recos;
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
				
		// weights to int-array
		int[] listOfWeightsA = new int[nextsteps.size()];
		for(int i=0; i<nextsteps.size(); i++){ listOfWeightsA[i] = listOfWeightsAL.get(i); }
		
		// select the most weighted sequences
		// order the frequencies of searched sequences of the way
		int realNreco = Math.min(nRecos, nextsteps.size());
		ArrayList<String> recos = this.getTheMostWeightedURLs(realNreco, nextsteps, listOfWeightsA);
		
		// return the most weighted sequences
		return recos;
	}
	
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
        RecommenderSuffixTree rec = new RecommenderSuffixTree(st);
        
        Object[] objA;
        ArrayList<String> list;
        
		objA = rec.getNextpossibleSteps();
		list = (ArrayList<String>)objA[0];
        
        // recommendations from the root
        System.out.println("---");
        for(int i=0; i<list.size(); i++){ System.out.println(list.get(i)); }
        
        // paths that are exists
        rec.updatePointer("c", false);
        rec.updatePointer("a", false);
        objA = rec.getNextpossibleSteps();
		list = (ArrayList<String>)objA[0];
        rec.updatePointer("c", false);
        objA = rec.getNextpossibleSteps();
		list = (ArrayList<String>)objA[0];
        System.out.println("---");
        for(int i=0; i<list.size(); i++){ System.out.println(list.get(i)); }
	
        // paths that they not exists
        rec.updatePointer("z", false);
        objA = rec.getNextpossibleSteps();
		list = (ArrayList<String>)objA[0];
        System.out.println("---");
        for(int i=0; i<list.size(); i++){ System.out.println(list.get(i)); }
	}
	
}
