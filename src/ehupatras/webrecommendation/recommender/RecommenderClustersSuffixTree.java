package ehupatras.webrecommendation.recommender;

import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RecommenderClustersSuffixTree 
				implements Recommender {
	
	private ArrayList<RecommenderSuffixTree> m_recSuffixTreeAL;
	private boolean[] m_validSTs;
	private ArrayList<String> m_waydone;
	
	public RecommenderClustersSuffixTree(ArrayList<SuffixTreeStringArray> stAL){
		// Create RecommenderSuffixTree for each tree
		m_recSuffixTreeAL = new ArrayList<RecommenderSuffixTree>();
		for(int i=0; i<stAL.size(); i++){
			SuffixTreeStringArray st = stAL.get(i);
			RecommenderSuffixTree rst = new RecommenderSuffixTree(st, 1, 1000);
			m_recSuffixTreeAL.add(rst);
		}
		// way done
		m_waydone = new ArrayList<String>();
		// candidate STs
		m_validSTs = new boolean[stAL.size()];
		Arrays.fill(m_validSTs, true);
	}
	
	public ArrayList<String> update(ArrayList<String> waydone, String newstep, 
			boolean incrWeigh, boolean performFailureFunction){
		// run the way in all suffix trees
		ArrayList<ArrayList<String>> waydoneAL = new ArrayList<ArrayList<String>>(); 
		for(int i=0; i<m_recSuffixTreeAL.size(); i++){
			RecommenderSuffixTree rst = m_recSuffixTreeAL.get(i);
			ArrayList<String> waydone2 = (ArrayList<String>)waydone.clone(); 
			ArrayList<String> way = rst.update(waydone2, newstep, incrWeigh, performFailureFunction);
			m_recSuffixTreeAL.set(i, rst);
			waydoneAL.add(way);
		}
		
		// return the runnable longest path
		int maxlen = -1;
		int maxi = 0;
		for(int i=0; i<waydoneAL.size(); i++){
			ArrayList<String> path = waydoneAL.get(i);
			int pathlen = path.size(); 
			if(maxlen<pathlen){
				maxlen = pathlen;
				maxi = i;
			}
		}
		m_waydone = waydoneAL.get(maxi);
		
		// update the m_validSTs array with candidate STs
		Arrays.fill(m_validSTs, false);
		for(int i=0; i<waydoneAL.size(); i++){
			ArrayList<String> path = waydoneAL.get(i);
			if(path.size()==maxlen){
				m_validSTs[i] = true;
			}
		}
		
		// return the performed longest way
		return m_waydone;
	}
	
	public int getNumberOfFailures(){
		int nFailures = 0;
		for(int i=0; i<m_recSuffixTreeAL.size(); i++){
			RecommenderSuffixTree rst = m_recSuffixTreeAL.get(i);
			nFailures = nFailures + rst.getNumberOfFailures();
		}
		return nFailures;
	}
	
	public void reset(){
		for(int i=0; i<m_recSuffixTreeAL.size(); i++){
			RecommenderSuffixTree rst = m_recSuffixTreeAL.get(i);
			rst.reset();
		}
		m_waydone = new ArrayList<String>();
	}
	
	
	
	// GET RECOMMENDATIONS //
	
	private Object[] getNextpossibleSteps(){
		// select the biggest clusters
		float[] supportsA = new float[m_recSuffixTreeAL.size()];
		if(m_waydone.size()==0){
			for(int i=0; i<m_recSuffixTreeAL.size(); i++){
				RecommenderSuffixTree rst = m_recSuffixTreeAL.get(i);
				SuffixTreeStringArray st = rst.getSuffixTree();
				float sup = (float)st.getNumberOfConstructionSequences();
				supportsA[i] = sup;
			}
			int nst = Math.round( ((float)supportsA.length-1f) * 0.9f);
			float[] supportsA2 = supportsA.clone();
			Arrays.sort(supportsA2);
			float supTh = supportsA2[nst];
			for(int i=0; i<supportsA.length; i++){
				if(supportsA[i]>=supTh){
					m_validSTs[i] = true;
				}
			}
		}
		
		// take the biggest supports
		ArrayList<ArrayList<String>> alllistOfURLs = new ArrayList<ArrayList<String>>();
		for(int i=0; i<m_validSTs.length; i++){
			if(m_validSTs[i]){	
				// get the recommendations
				RecommenderSuffixTree rst = m_recSuffixTreeAL.get(i);
				//sumNseqs = sumNseqs + rst.getSuffixTree().getNumberOfConstructionSequences();
				Object[] objA1 = rst.getNextpossibleSteps();
				ArrayList<String> listOfURLs1 = (ArrayList<String>)objA1[0];
				ArrayList<Integer> listOfWeights1 = (ArrayList<Integer>)objA1[1];
				
				// update weights
				Object[] objA2 = rst.getUrlWeights(m_waydone, listOfURLs1, listOfWeights1);
				ArrayList<String> listOfURLs2 = (ArrayList<String>)objA2[0];
				ArrayList<Integer> listOfWeights2 = (ArrayList<Integer>)objA2[1];

				// save the list
				ArrayList<String> listOfURLsOrdered = this.getTheMostWeightedURLs(1000, listOfURLs2, listOfWeights2);
				alllistOfURLs.add(listOfURLsOrdered);
			}
		}
		
		// create the final list
		ArrayList<String> listOfURLs = new ArrayList<String>();
		ArrayList<Integer> listOfWeights = new ArrayList<Integer>();
		boolean[] moreElemsA = new boolean[alllistOfURLs.size()];
		Arrays.fill(moreElemsA, true);
		int pos = 0;
		while(true){
			for(int i=0; i<alllistOfURLs.size(); i++){
				ArrayList<String> list = alllistOfURLs.get(i);
				if(pos<list.size()){
					String url = list.get(pos);
					if(!listOfURLs.contains(url)){
						listOfURLs.add(url);
						listOfWeights.add(1000-pos);
					}
				} else {
					moreElemsA[i] = false;
				}
			}
			// there is more elements?
			boolean allfalse = true;
			for(int i=0; i<moreElemsA.length; i++){
				if(moreElemsA[i]){
					allfalse = false;
				}
			}
			if(allfalse){break;}
			
			pos++;
		}
		
		// return the recommendations with their weights
		Object[] objA = new Object[2];
		objA[0] = listOfURLs;
		objA[1] = listOfWeights;
		return objA;
	}
	
	private Object[] getNextpossibleSteps1(){
		// compute the support given by each cluster-ST to the actual position
		float[] supportsA = new float[m_recSuffixTreeAL.size()];
		float maxsup = -1f;
		for(int i=0; i<m_recSuffixTreeAL.size(); i++){
			if(m_validSTs[i]){
				RecommenderSuffixTree rst = m_recSuffixTreeAL.get(i);
				SuffixTreeStringArray st = rst.getSuffixTree();
				ArrayList<Integer> seqs = st.search(m_waydone);
				int nseqs = st.getNumberOfConstructionSequences();
				float sup = (float)seqs.size() / (float)nseqs;
				if(maxsup<sup){
					maxsup = sup;
				}
				supportsA[i] = sup;
			} else {
				supportsA[i] = 0f;
			}
		}
		
		// take the biggest supports
		ArrayList<String> listOfURLs = new ArrayList<String>();
		ArrayList<Integer> listOfWeights = new ArrayList<Integer>();
		int sumNseqs = 0;
		for(int i=0; i<supportsA.length; i++){
			if(supportsA[i]==maxsup){
				m_validSTs[i] = true;
				
				// get the recommendations
				RecommenderSuffixTree rst = m_recSuffixTreeAL.get(i);
				//sumNseqs = sumNseqs + rst.getSuffixTree().getNumberOfConstructionSequences();
				Object[] objA1 = rst.getNextpossibleSteps();
				ArrayList<String> listOfURLs1 = (ArrayList<String>)objA1[0];
				ArrayList<Integer> listOfWeights1 = (ArrayList<Integer>)objA1[1];
				
				// update weights
				Object[] objA2 = rst.getUrlWeights(m_waydone, listOfURLs1, listOfWeights1);
				ArrayList<String> listOfURLs2 = (ArrayList<String>)objA2[0];
				ArrayList<Integer> listOfWeights2 = (ArrayList<Integer>)objA2[1];
				
				// accumulate all clusters-STs URLs
				for(int j=0; j<listOfURLs2.size(); j++){
					// the next URL
					String url = listOfURLs2.get(j);
					int w = listOfWeights2.get(j);
					
					if(!listOfURLs.contains(url)){
						listOfURLs.add(url);
						listOfWeights.add(w);
					} else {
						int jAux = listOfURLs.indexOf(url); 
						int wAux = listOfWeights.get(jAux);
						listOfWeights.set(jAux, wAux+w);
					}
				}
			} else {
				m_validSTs[i] = false;
			}
		}
		
		// return the recommendations with their weights
		Object[] objA = new Object[2];
		objA[0] = listOfURLs;
		objA[1] = listOfWeights;
		return objA;
	}
	
	private Object[] getNextpossibleSteps0(){
		// compute the support given by each cluster-ST to the actual position
		float[] supportsA = new float[m_recSuffixTreeAL.size()];
		float maxsup = -1f;
		for(int i=0; i<m_recSuffixTreeAL.size(); i++){
			if(m_validSTs[i]){
				RecommenderSuffixTree rst = m_recSuffixTreeAL.get(i);
				SuffixTreeStringArray st = rst.getSuffixTree();
				ArrayList<Integer> seqs = st.search(m_waydone);
				//int nseqs = st.getNumberOfConstructionSequences();
				float sup = (float)seqs.size();
				if(maxsup<sup){
					maxsup = sup;
				}
				supportsA[i] = sup;
			} else {
				supportsA[i] = 0f;
			}
		}
		
		// take the biggest supports
		ArrayList<String> listOfURLs = new ArrayList<String>();
		ArrayList<Integer> listOfWeights = new ArrayList<Integer>();
		int sumNseqs = 0;
		for(int i=0; i<supportsA.length; i++){
			if(supportsA[i]==maxsup){
				m_validSTs[i] = true;
				
				// get the recommendations
				RecommenderSuffixTree rst = m_recSuffixTreeAL.get(i);
				//sumNseqs = sumNseqs + rst.getSuffixTree().getNumberOfConstructionSequences();
				Object[] objA1 = rst.getNextpossibleSteps();
				ArrayList<String> listOfURLs1 = (ArrayList<String>)objA1[0];
				ArrayList<Integer> listOfWeights1 = (ArrayList<Integer>)objA1[1];
				
				// update weights
				Object[] objA2 = rst.getUrlWeights(m_waydone, listOfURLs1, listOfWeights1);
				ArrayList<String> listOfURLs2 = (ArrayList<String>)objA2[0];
				ArrayList<Integer> listOfWeights2 = (ArrayList<Integer>)objA2[1];
				
				// accumulate all clusters-STs URLs
				for(int j=0; j<listOfURLs2.size(); j++){
					// the next URL
					String url = listOfURLs2.get(j);
					int w = listOfWeights2.get(j);
					
					if(!listOfURLs.contains(url)){
						listOfURLs.add(url);
						listOfWeights.add(w);
					} else {
						int jAux = listOfURLs.indexOf(url); 
						int wAux = listOfWeights.get(jAux);
						listOfWeights.set(jAux, wAux+w);
					}
				}
			} else {
				m_validSTs[i] = false;
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
	
	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone){
		// get the possible URLs in the actual position
		Object[] objA = this.getNextpossibleSteps();
		ArrayList<String> list = (ArrayList<String>)objA[0];
		ArrayList<Integer> weig = (ArrayList<Integer>)objA[1];
		
		// get the most weighted URLs
		ArrayList<String> listOfURLs = this.getTheMostWeightedURLs(nRecos, list, weig);
		if(listOfURLs.size()>=nRecos){
			return listOfURLs;
		}
		// else add URLs from step1
		
		// add step1 recommendations
		ArrayList<String> listOfURLsStep1 = new ArrayList<String>();
		ArrayList<Integer> listOfWeightsStep1 = new ArrayList<Integer>();
		for(int i=0; i<m_recSuffixTreeAL.size(); i++){
			if(m_validSTs[i]){
				RecommenderSuffixTree rst = m_recSuffixTreeAL.get(i);
				String laststep = m_waydone.get(m_waydone.size()-1);
				Object[] objA2 = rst.getStep1Recommendations(laststep);
				ArrayList<String> listOfURLs2 = (ArrayList<String>)objA2[0];
				ArrayList<Integer> listOfWeights2 = (ArrayList<Integer>)objA2[1];
				
				// accumulate all clusters-STs URLs
				for(int j=0; j<listOfURLs2.size(); j++){
					// the next URL
					String url = listOfURLs2.get(j);
					int w = listOfWeights2.get(j);
					
					if(!listOfURLs.contains(url)){
						if(!listOfURLsStep1.contains(url)){
							listOfURLsStep1.add(url);
							listOfWeightsStep1.add(w);
						} else {
							int jAux = listOfURLsStep1.indexOf(url); 
							int wAux = listOfWeightsStep1.get(jAux);
							listOfWeightsStep1.set(jAux, wAux+w);
						}
					}
					
				}
			}
		}
		ArrayList<String> addlist = this.getTheMostWeightedURLs(nRecos-listOfURLs.size(), listOfURLsStep1, listOfWeightsStep1);
		for(int i=0; i<addlist.size(); i++){
			listOfURLs.add(addlist.get(i));
		}
		
		// return
		return listOfURLs;
	}
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nrecos){
		return this.getNextpossibleStepsWeighted(nrecos, null);
	}
	public ArrayList<String> getNextpossibleStepsMarkov(int nRecos, ArrayList<String> waydone, ArrayList<String> listMarkov){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nRecos){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	public ArrayList<String> getNextpossibleStepsWeightedEnrichWithStep1(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleStepsWeighted(nRecos, null);
	}
	
	
	// UTILS //
	
	private ArrayList<String> getTheMostWeightedURLs(
			int nrec, 
			ArrayList<String> list, 
			ArrayList<Integer> frequencies){
		ArrayList<String> recos = new ArrayList<String>();

		// order the frequencies of searched sequences of the way 
		int[] frequencies1 = new int[frequencies.size()];
		for(int i=0; i<frequencies.size(); i++){ frequencies1[i] = frequencies.get(i);}
		int[] frequencies2 = frequencies1.clone();
		Arrays.sort(frequencies2);

		boolean[] isusedA = new boolean[frequencies1.length];
		Arrays.fill(isusedA, false);

		for(int i=frequencies2.length-1; i>=0; i--){
			int freqmax = frequencies2[i];
			for(int j=0; j<frequencies1.length; j++){
				if(!isusedA[j]){
					if(freqmax==frequencies1[j]){
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


}
