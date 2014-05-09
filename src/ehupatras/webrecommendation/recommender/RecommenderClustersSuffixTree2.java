package ehupatras.webrecommendation.recommender;

import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RecommenderClustersSuffixTree2 implements Recommender {

	private ArrayList<RecommenderSuffixTree2> m_recSuffixTreeAL;
	private boolean[] m_validSTs;
	private ArrayList<String> m_waydone;
	
	// CREATOR
	
	public RecommenderClustersSuffixTree2(ArrayList<MySuffixTree> stAL){
		// Create RecommenderSuffixTree for each tree
		m_recSuffixTreeAL = new ArrayList<RecommenderSuffixTree2>();
		for(int i=0; i<stAL.size(); i++){
			MySuffixTree st = stAL.get(i);
			RecommenderSuffixTree2 rst = new RecommenderSuffixTree2(st, 1);
			m_recSuffixTreeAL.add(rst);
		}
		
		// initialize the done way
		m_waydone = new ArrayList<String>();
		
		// initially all are candidate STs
		m_validSTs = new boolean[stAL.size()];
		Arrays.fill(m_validSTs, true);
	}
	

	// UPDATE FUNCTION
	
	// Select STs with longest suffix runnable path
	public ArrayList<String> update(
				ArrayList<String> waydone,
				String newstep, 
				boolean incrWeigh,
				boolean performFailureFunction){
		
		// run the way in all suffix trees
		ArrayList<ArrayList<String>> waydoneAL = new ArrayList<ArrayList<String>>(); 
		for(int i=0; i<m_recSuffixTreeAL.size(); i++){
			RecommenderSuffixTree2 rst = m_recSuffixTreeAL.get(i);
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
	
	
	// UTILS
	
	public int getNumberOfFailures(){
		int nFailures = 0;
		for(int i=0; i<m_recSuffixTreeAL.size(); i++){
			RecommenderSuffixTree2 rst = m_recSuffixTreeAL.get(i);
			nFailures = nFailures + rst.getNumberOfFailures();
		}
		return nFailures;
	}
	
	public void reset(){
		for(int i=0; i<m_recSuffixTreeAL.size(); i++){
			RecommenderSuffixTree2 rst = m_recSuffixTreeAL.get(i);
			rst.reset();
		}
		m_waydone = new ArrayList<String>();
	}
	
	
	
	// GET RECOMMENDATIONS //
	
	// Mix evenly all valid STs' recommendations
	protected Object[] getNextpossibleSteps(){
		// take the biggest supports
		ArrayList<ArrayList<String>> alllistOfURLs = new ArrayList<ArrayList<String>>();
		for(int i=0; i<m_validSTs.length; i++){
			if(m_validSTs[i]){
				// get the recommendations
				RecommenderSuffixTree2 rst = m_recSuffixTreeAL.get(i);
				Object[] objA1 = rst.getNextpossibleSteps();
				ArrayList<String> listOfURLs1 = (ArrayList<String>)objA1[0];
				ArrayList<Float> listOfWeights1 = (ArrayList<Float>)objA1[1];
				// save the list
				ArrayList<String> listOfURLsOrdered = RecommenderSuffixTree2.getTheMostWeightedFloatURLs(1000, listOfURLs1, listOfWeights1);
				alllistOfURLs.add(listOfURLsOrdered);
			}
		}
		
		// create the final list
		ArrayList<String> listOfURLs = new ArrayList<String>();
		ArrayList<Float> listOfWeights = new ArrayList<Float>();
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
						listOfWeights.add(1000f-(float)pos);
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
		ArrayList<Float> weig = (ArrayList<Float>)objA[1];
		
		// get the most weighted URLs
		ArrayList<String> listOfURLs = RecommenderSuffixTree2.getTheMostWeightedFloatURLs(nRecos, list, weig);
		if(listOfURLs.size()>=nRecos){
			return listOfURLs;
		}
		
		// else add URLs from step1
		// add step1 recommendations
		Object[] objA2 = this.getStep1Recommendations(listOfURLs);
		ArrayList<String> listOfURLsStep1 = (ArrayList<String>)objA2[0];
		ArrayList<Float> listOfWeightsStep1 = (ArrayList<Float>)objA2[1];
		ArrayList<String> addlist = RecommenderSuffixTree2.getTheMostWeightedFloatURLs(nRecos-listOfURLs.size(), listOfURLsStep1, listOfWeightsStep1);
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
	
	
	protected Object[] getStep1Recommendations(ArrayList<String> listOfURLs){
		ArrayList<String> listOfURLsStep1 = new ArrayList<String>();
		ArrayList<Float> listOfWeightsStep1 = new ArrayList<Float>();
		for(int i=0; i<m_recSuffixTreeAL.size(); i++){
			if(m_validSTs[i]){
				RecommenderSuffixTree2 rst = m_recSuffixTreeAL.get(i);
				Object[] objA2;
				if(m_waydone.size()>0){
					// we have some runnable way in the actual ST so propose:
					MySuffixTree st = rst.getSuffixTree();
					// get 1-step recommendations
					String laststep = m_waydone.get(m_waydone.size()-1);
					int[] pointers = st.doStep(0, 0, laststep);
					int pointerNode = pointers[0];
					int pointerLabel = pointers[1];
					if(pointerNode!=-1){
						objA2 = rst.getNextpossibleSteps(pointerNode, pointerLabel);
					} else {
						rst.reset();
						objA2 = rst.getNextpossibleSteps();
					}
				} else { // we do not have any performable way, so propose from the root
					rst.reset();
					objA2 = rst.getNextpossibleSteps();
				}
				ArrayList<String> listOfURLs2 = (ArrayList<String>)objA2[0];
				ArrayList<Float> listOfWeights2 = (ArrayList<Float>)objA2[1];
				
				// accumulate all clusters-STs URLs
				for(int j=0; j<listOfURLs2.size(); j++){
					// the next URL
					String url = listOfURLs2.get(j);
					float w = listOfWeights2.get(j);
					
					if(!listOfURLs.contains(url)){
						if(!listOfURLsStep1.contains(url)){
							listOfURLsStep1.add(url);
							listOfWeightsStep1.add(w);
						} else {
							int jAux = listOfURLsStep1.indexOf(url); 
							float wAux = listOfWeightsStep1.get(jAux);
							listOfWeightsStep1.set(jAux, wAux+w);
						}
					}
				}
			}
		}
		
		// return
		Object[] objA = new Object[2];
		objA[0] = listOfURLsStep1;
		objA[1] = listOfWeightsStep1;
		return objA;
	}
	
	public void setValidSTs(boolean[] validSTs){
		m_validSTs =  validSTs;
	}
	
}
