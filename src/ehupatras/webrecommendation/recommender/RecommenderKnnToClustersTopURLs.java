package ehupatras.webrecommendation.recommender;

import java.util.*;

import ehupatras.clustering.sapehac.dendrogram.DendrogramNode;
import ehupatras.clustering.sapehac.dendrogram.MergeNode;
import ehupatras.clustering.sapehac.dendrogram.ObservationNode;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentCombineGlobalLocalDimopoulos2010;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLevenshtein;

// TODO: Auto-generated Javadoc
/**
 * The Class RecommenderKnnToClustersTopURLs.
 */
public class RecommenderKnnToClustersTopURLs
				implements Recommender {
	
	
	// ATTRIBUTES
	
	// the path done
	/** The m_waydone. */
	protected ArrayList<String> m_waydone = new ArrayList<String>();
	
	// the model
	/** The m_medoids. */
	private ArrayList<String[]> m_medoids;
	protected int[] m_clustersizes;
	
	/** The m_gmedoids. */
	private int[] m_gmedoids; 
	
	/** The m_recos in each cluster. */
	private ArrayList<Object[]> m_recosInEachCluster;
	
	// number of selected empty clusters
	/** The m_0recos clusters. */
	private int m_0recosClusters = 0;
	// distance/similarity related attributes
	/** The m_is distance. */
	private boolean m_isDistance = true;
	
	/** The m_roles w. */
	private float[][] m_rolesW = {{ 0f, 0f, 0f},
								  { 0f, 0f, 0f},
								  { 0f, 0f, 0f}};
	
	
	// CREATOR
	
	/**
	 * Instantiates a new recommender knn to clusters top ur ls.
	 *
	 * @param medoids the medoids
	 * @param globalMedoids the global medoids
	 * @param recosForEachMedoid the recos for each medoid
	 * @param isDistance the is distance
	 * @param rolesW the roles w
	 */
	public RecommenderKnnToClustersTopURLs(
			ArrayList<String[]> medoids, int[] clustersizes, int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid,
			boolean isDistance,
			float[][] rolesW){
		m_medoids = medoids;
		m_clustersizes = clustersizes;
		m_gmedoids = globalMedoids;
		m_recosInEachCluster = recosForEachMedoid;
		m_waydone = new ArrayList<String>();
		m_isDistance = isDistance;
	}
	
	
	// FUNCTIONS
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#reset()
	 */
	public void reset(){
		m_waydone = new ArrayList<String>();
	}
	
	// No recommendations in clusters
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNumberOfFailures()
	 */
	public int getNumberOfFailures(){
		return m_0recosClusters;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#update(java.util.ArrayList, java.lang.String, boolean, boolean)
	 */
	public ArrayList<String> update(ArrayList<String> waydone, String laststep, 
			boolean incrWeigh, boolean performFailureFunction){
		m_waydone.add(laststep);
		return m_waydone;
	}
	
	/**
	 * Gets the nextpossible steps.
	 *
	 * @param nRecos the n recos
	 * @return the nextpossible steps
	 */
	protected ArrayList<String> getNextpossibleSteps(int nRecos){
		//Object[] objA = this.getNextpossibleSteps_Info(nRecos);
		//lierni
		Object[] objA = this.getNextpossibleSteps_Info_Comb(nRecos);
		ArrayList<String> recosL = (ArrayList<String>)objA[0];
		//ArrayList<Integer> supportL = (ArrayList<Integer>)objA[1];
		//ArrayList<Integer> clustersL = (ArrayList<Integer>)objA[2];
		//ArrayList<Integer> clustersizesL = (ArrayList<Integer>)objA[3];
		//ArrayList<Float> distsL = (ArrayList<Float>)objA[4];
		return recosL;
	}
	
	/**
	 * Gets the nextpossible steps_ info.
	 *
	 * @param nRecos the n recos
	 * @return the nextpossible steps_ info
	 */
	protected Object[] getNextpossibleSteps_Info(int nRecos){
		
		// the elements we are interested in
		ArrayList<String> recosL = new ArrayList<String>();
		ArrayList<Integer> supportL = new ArrayList<Integer>();
		ArrayList<Integer> clustersL = new ArrayList<Integer>();
		ArrayList<Integer> clustersizesL = new ArrayList<Integer>();
		ArrayList<Float> distsL = new ArrayList<Float>();
		
		// medoids ordered from the nearest to farthest
		Object[] objAa = this.knnSimShort();
		int[] orderedMedoids = (int[])objAa[0];
		float[] orderedSims = (float[])objAa[1]; // it can be null
		
		// for each medoid take the recommendations
		boolean end = false;
		//int clusterKop = 0;
		for(int i=0; i<orderedMedoids.length; i++){
			int nearesCl = orderedMedoids[i];
			float dist2clust = 0f;
			if(orderedSims==null){
				if(m_isDistance){dist2clust = 0f;} 
				else{dist2clust = 1f;}
			} else {
				dist2clust = orderedSims[i];
			}
			
			Object[] objA = m_recosInEachCluster.get(nearesCl);
			ArrayList<String> recosCl = (ArrayList<String>)objA[0];
			ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
			
			if(recosCl.size()==0){m_0recosClusters++;}
			for(int j=0; j<recosCl.size(); j++){
				if(recosL.size()<nRecos){
					String reco = recosCl.get(j);
					if(!recosL.contains(reco)){
						recosL.add(reco);
						supportL.add(supports.get(j));
						clustersL.add(nearesCl);
						clustersizesL.add(m_clustersizes[nearesCl]);
						distsL.add(dist2clust);
					}
				} else {
					end = true;
					break;
				}
			}
			if(end){ break;}
			//clusterKop++;
		}
		
		//System.out.println("Erabilitako cluster kopurua: " + clusterKop);

		// return the list of recommendations
		Object[] objA = new Object[5];
		objA[0] = recosL;
		objA[1] = supportL;
		objA[2] = clustersL;
		objA[3] = clustersizesL;
		objA[4] = distsL;
		/*for (int n=0; n<recosL.size(); n++){
			System.out.print("Support: " + supportL.get(n) + " Clusters: " + clustersL.get(n) + " Sizes: " + clustersizesL.get(n) + " Distances: " + distsL.get(n));
		}*/
		return objA;
	}
	
	
	/**
	 * Gets the nextpossible steps_ info.
	 *
	 * @param nRecos the n recos
	 * @return the nextpossible steps_ info
	 */
	protected Object[] getNextpossibleSteps_Info_Comb(int nRecos){
		
		// the elements we are interested in
		ArrayList<String> recosL = new ArrayList<String>();
		ArrayList<Integer> supportL = new ArrayList<Integer>();
		ArrayList<Integer> clustersL = new ArrayList<Integer>();
		ArrayList<Integer> clustersizesL = new ArrayList<Integer>();
		ArrayList<Float> distsL = new ArrayList<Float>();
		
		ArrayList<String> recosAux = new ArrayList<String>();
		ArrayList<Integer> supportAux = new ArrayList<Integer>();
		ArrayList<Integer> clustersAux = new ArrayList<Integer>();
		ArrayList<Integer> clustersizesAux = new ArrayList<Integer>();
		ArrayList<Float> distsAux = new ArrayList<Float>();
		ArrayList<Float> valsAux = new ArrayList<Float>();
		
		// medoids ordered from the nearest to farthest
		Object[] objAa = this.knnSimShort();
		int[] orderedMedoids = (int[])objAa[0];
		float[] orderedSims = (float[])objAa[1]; // it can be null
		
		float value = 0.0f;
		for(int i=0; i<2; i++){
			int nearesCl = orderedMedoids[i];
			float dist2clust = 0f;
			if(orderedSims==null){
				if(m_isDistance){dist2clust = 0f;} 
				else{dist2clust = 1f;}
			} else {
				dist2clust = orderedSims[i];
			}
			
			Object[] objA = m_recosInEachCluster.get(nearesCl);
			ArrayList<String> recosCl = (ArrayList<String>)objA[0];
			ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
			if(recosCl.size()==0){m_0recosClusters++;}
			for(int j=0; j<recosCl.size(); j++){
				String reco = recosCl.get(j);
				recosAux.add(reco);
				supportAux.add(supports.get(j));
				clustersAux.add(nearesCl);
				clustersizesAux.add(m_clustersizes[nearesCl]);
				distsAux.add(dist2clust);
				value = (1.0f - dist2clust) + supports.get(j);
				valsAux.add(value);
			}
		}
		
		// order all the arrays taking into account the result of  (1-dist)*support
		Object[] objAux = this.sort(recosAux, supportAux, clustersAux, clustersizesAux, distsAux, valsAux);
		String[] recosAux2 = (String[]) objAux[0];
		String[] supportAux2 = (String[]) objAux[1];
		String[] clustersAux2 = (String[]) objAux[2];
		String[] clustersizesAux2 = (String[]) objAux[3];
		String[] distsAux2 = (String[]) objAux[4];		
		
		for (int i=0; i<recosAux2.length; i++){
			if (recosL.size()<nRecos){
				if(!recosL.contains(recosAux2[i])){
					recosL.add(recosAux2[i]);
					supportL.add(Integer.parseInt(supportAux2[i]));
					clustersL.add(Integer.parseInt(clustersAux2[i]));
					clustersizesL.add(Integer.parseInt(clustersizesAux2[i]));
					distsL.add(Float.parseFloat(distsAux2[i]));
				}
			}
			
		}	
		
		boolean end = false;
		if (recosL.size()<nRecos){
			for(int i=2; i<orderedMedoids.length; i++){
				int nearesCl = orderedMedoids[i];
				float dist2clust = 0f;
				if(orderedSims==null){
					if(m_isDistance){dist2clust = 0f;} 
					else{dist2clust = 1f;}
				} else {
					dist2clust = orderedSims[i];
				}
				
				Object[] objA = m_recosInEachCluster.get(nearesCl);
				ArrayList<String> recosCl = (ArrayList<String>)objA[0];
				ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
				
				if(recosCl.size()==0){m_0recosClusters++;}
				for(int j=0; j<recosCl.size(); j++){
					if(recosL.size()<nRecos){
						String reco = recosCl.get(j);
						if(!recosL.contains(reco)){
							recosL.add(reco);
							supportL.add(supports.get(j));
							clustersL.add(nearesCl);
							clustersizesL.add(m_clustersizes[nearesCl]);
							distsL.add(dist2clust);
						}
					} else {
						end = true;
						break;
					}
				}
				if(end){ break;}
				//clusterKop++;
			}
		}
		
		// return the list of recommendations
		Object[] objA = new Object[5];
		objA[0] = recosL;
		objA[1] = supportL;
		objA[2] = clustersL;
		objA[3] = clustersizesL;
		objA[4] = distsL;
		/*for (int n=0; n<recosL.size(); n++){
			System.out.print("Support: " + supportL.get(n) + " Clusters: " + clustersL.get(n) + " Sizes: " + clustersizesL.get(n) + " Distances: " + distsL.get(n));
		}*/
		return objA;
		
	}
	
	public Object[] sort(ArrayList<String> recosAux, ArrayList<Integer> supportAux,
			ArrayList<Integer> clustersAux, ArrayList<Integer> clustersizesAux,
			ArrayList<Float> distsAux, ArrayList<Float> valsAux) {
	    
		String[] recosArray = recosAux.toArray(new String[recosAux.size()]);
		String[] supportArray = new String[recosAux.size()];
		String[] clustersArray = new String[recosAux.size()];
		String[] clustersizesArray = new String[recosAux.size()];
		String[] distArray = new String[recosAux.size()];
		String[] valsArray = new String[recosAux.size()];
	    
		for (int i=0; i<recosArray.length; i++){
			supportArray[i] = supportAux.get(i).toString();
			clustersArray[i] = clustersAux.get(i).toString();
			clustersizesArray[i] = clustersizesAux.get(i).toString();
			distArray[i] = distsAux.get(i).toString();
			valsArray[i] = valsAux.get(i).toString();
		}
		

	    int lenD = valsArray.length;
	    for(int i=0;i<lenD;i++){
	        for(int k = i;k<lenD;k++){
	            if(Float.parseFloat(valsArray[i])<Float.parseFloat(valsArray[k])){
	            	permutation(valsArray, i, k);
	    	        permutation(recosArray, i, k);
	    	        permutation(supportArray, i, k);
	    	        permutation(clustersArray, i, k);
	    	        permutation(clustersizesArray, i, k);
	    	        permutation(distArray, i, k);
	            }
	        }
	        
	    }

	    Object[] objA = new Object[6];
		objA[0] = recosArray;
		objA[1] = supportArray;
		objA[2] = clustersArray;
		objA[3] = clustersizesArray;
		objA[4] = distArray;
		objA[5] = valsArray;
		
		return objA;
	    
	}

	private void permutation(String[] array, int i, int j) {
	    String tmp = array[i];
	    array[i] = array[j];
	    array[j] = tmp;
	}

	/**
	 * Gets the nextpossible steps w.
	 *
	 * @param nRecos the n recos
	 * @return the nextpossible steps w
	 */
	private ArrayList<String> getNextpossibleStepsW(int nRecos){
		int minsupfreq = 5;
		
		// the elements we are interested in
		ArrayList<String> recos = new ArrayList<String>();
		ArrayList<Float> weightsA = new ArrayList<Float>();
		
		// medoids ordered from the nearest to farthest
		Object[] objAa = this.knnSim();
		int[] orderedMedoids = (int[])objAa[0];
		float[] orderedSims = (float[])objAa[1]; // it can be null
		if(orderedSims==null){ return getNextpossibleSteps(nRecos); }
		
		// find maximum & minimum values of the similarities
		float maxsim = Float.MIN_VALUE;
		float minsim = Float.MAX_VALUE;
		for(int i=0; i<orderedSims.length; i++){
			float sim = orderedSims[i];
			if(maxsim<sim){maxsim = sim;}
			if(minsim>sim){minsim = sim;}
		}
		
		// take more URLs and after select the best ones
		int nRecos2 = Math.round((float)nRecos * 1.5f);
		
		// for each medoid take the reccommendations		
		boolean end = false;
		for(int i=0; i<orderedMedoids.length; i++){
			int nearesCl = orderedMedoids[i];
			float sim = orderedSims[i];
			float normsim = (sim-minsim)/(maxsim-minsim);
			
			// get the recommendations of the cluster
			Object[] objA = m_recosInEachCluster.get(nearesCl);
			ArrayList<String> recosCl = (ArrayList<String>)objA[0];
			ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
			
			// if it does not have recommendations
			if(recosCl.size()==0){m_0recosClusters++;}
			
			// sum all support-frequency values
			int sumsup = 0;
			for(int j=0; j<supports.size(); j++){ sumsup = sumsup + supports.get(j);}
			
			// take the URLs we are interested in with their weights
			int nurls = recosCl.size();
			for(int j=0; j<nurls; j++){
				if(recos.size()<nRecos2){
					String reco = recosCl.get(j);
					int sup = supports.get(j);
					
					float weight = 0f;
					if(sup>=minsupfreq){
						// compute the weight
						//float supDist = 1f - (float)nurls/(float)sumsup;
						float supf = sup/(float)sumsup;
						
						if(m_isDistance){
							weight = supf * (1f-normsim);
						} else {
							weight = supf * normsim;
						}
					}
					
					if(!recos.contains(reco)){
						recos.add(reco);
						weightsA.add(weight);
					} else {
						int index = recos.indexOf(reco);
						float w = weightsA.get(index) + weight;
						weightsA.set(index, w);
					}
				} else {
					end = true;
					break;
				}
			}
			if(end){ break;}
		}
		
		// take the most relevant URLs
		ArrayList<String> recos2 = new ArrayList<String>();
		boolean[] isTaken = new boolean[recos.size()];
		Arrays.fill(isTaken, false);
		for(int i=0; i<nRecos; i++){
			if(i>=recos.size()){ break;}
			float maxw = Float.MIN_VALUE;
			int maxj = -1;
			for(int j=0; j<weightsA.size(); j++){
				float w = weightsA.get(j);
				if(!isTaken[j] && maxw<w && w>0){
					maxw = w;
					maxj = j;
				}
			}
			String rec = recos.get(maxj);
			recos2.add(rec);
			isTaken[maxj] = true;
		}
		
		// return the list of recommendations
		return recos2;
	}
	
	/*
	protected Object[] getNextpossibleStepsW_AllInfo(int nRecos){
		int minsupfreq = 5;
		
		// the elements we are interested in
		ArrayList<String> recosL = new ArrayList<String>();
		ArrayList<Float> supportL = new ArrayList<Float>();
		ArrayList<Integer> clustersL = new ArrayList<Integer>();
		ArrayList<Float> distsL = new ArrayList<Float>();
		ArrayList<Float> weightsL = new ArrayList<Float>();
		
		// medoids ordered from the nearest to farthest
		Object[] objAa = this.knnSim();
		int[] orderedMedoids = (int[])objAa[0];
		float[] orderedSims = (float[])objAa[1]; // it can be null
		if(orderedSims==null){ return getNextpossibleSteps(nRecos); } // //////////////////  BEGIRATU!!!!!!
		
		// find maximum & minimum values of the similarities
		float maxsim = Float.MIN_VALUE;
		float minsim = Float.MAX_VALUE;
		for(int i=0; i<orderedSims.length; i++){
			float sim = orderedSims[i];
			if(maxsim<sim){maxsim = sim;}
			if(minsim>sim){minsim = sim;}
		}
		
		// take more URLs and after select the best ones
		int nRecos2 = Math.round((float)nRecos * 1.5f);
		
		// for each medoid take the reccommendations		
		boolean end = false;
		for(int i=0; i<orderedMedoids.length; i++){
			int nearesCl = orderedMedoids[i];
			float sim = orderedSims[i];
			float normsim = (sim-minsim)/(maxsim-minsim);
			
			// get the recommendations of the cluster
			Object[] objA = m_recosInEachCluster.get(nearesCl);
			ArrayList<String> recosCl = (ArrayList<String>)objA[0];
			ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
			
			// if it does not have recommendations
			if(recosCl.size()==0){m_0recosClusters++;}
			
			// sum all support-frequency values
			int sumsup = 0;
			for(int j=0; j<supports.size(); j++){ sumsup = sumsup + supports.get(j);}
			
			// take the URLs we are interested in with their weights
			int nurls = recosCl.size();
			for(int j=0; j<nurls; j++){
				if(recosL.size()<nRecos2){
					String reco = recosCl.get(j);
					int sup = supports.get(j);
					float supf = sup/(float)sumsup;
					
					float weight = 0f; 
					if(sup>=minsupfreq){
						// compute the final weight between support and distance to the medoid
						if(m_isDistance){
							weight = supf * (1f-normsim);
						} else {
							weight = supf * normsim;
						}
					}
					
					if(!recosL.contains(reco)){
						recosL.add(reco);
						supportL.add(supf);
						clustersL.add(nearesCl);
						if(m_isDistance){ distsL.add(1f-normsim); } else { distsL.add(normsim); }
						weightsL.add(weight);
					} else {
						int index = recosL.indexOf(reco);
						float w = weightsL.get(index) + weight;
						weightsL.set(index, w);
					}
				} else {
					end = true;
					break;
				}
			}
			if(end){ break;}
		}
		
		
		// take the most relevant URLs
		ArrayList<String> recos2L = new ArrayList<String>();
		ArrayList<Float> support2L = new ArrayList<Float>();
		ArrayList<Integer> clusters2L = new ArrayList<Integer>();
		ArrayList<Float> dists2L = new ArrayList<Float>();
		ArrayList<Float> weights2L = new ArrayList<Float>();
		
		boolean[] isTaken = new boolean[recosL.size()];
		Arrays.fill(isTaken, false);
		
		for(int i=0; i<nRecos; i++){
			if(i>=recosL.size()){ break;}
			
			float maxw = Float.MIN_VALUE;
			int maxj = -1;
			for(int j=0; j<weightsL.size(); j++){
				float w = weightsL.get(j);
				if(!isTaken[j] && maxw<w && w>0){
					maxw = w;
					maxj = j;
				}
			}
			recos2L.add(recosL.get(maxj));
			support2L.add(supportL.get(maxj));
			clusters2L.add(clustersL.get(maxj));
			dists2L.add(dists2L.get(maxj));
			weights2L.add(weightsL.get(maxj));
			isTaken[maxj] = true;
		}
		
		// return the list of recommendations
		Object[] objA = new Object[5];
		objA[0] = recos2L;
		objA[1] = support2L;
		objA[2] = clusters2L;
		objA[3] = dists2L;
		objA[4] = weights2L;
		return objA;
	}
	*/
	
	/**
	 * Knn sim.
	 *
	 * @return the object[]
	 */
	protected Object[] knnSim(){
		// if we do not know nothing about the navigation
		// return the most centered medoid in the database
		if(m_waydone.size()==0){
			Object[] objA = new Object[2];
			objA[0] = m_gmedoids;
			objA[1] = null;
			return objA;
		}
		
		// Else find the nearest medoid
		// waydone to String[]
		String[] waydone = new String[m_waydone.size()];
		for(int i=0; i<m_waydone.size(); i++){ waydone[i] = m_waydone.get(i); }

		// compute the similarities with the medoids
		float[] simA = new float[m_medoids.size()];
		for(int i=0; i<m_medoids.size(); i++){
			String[] medoid = m_medoids.get(i);
			SequenceAlignment seqalign;
			if(m_isDistance){
				seqalign = new SequenceAlignmentLevenshtein();
				seqalign.setRoleWeights(m_rolesW);
			} else{
				seqalign = new SequenceAlignmentCombineGlobalLocalDimopoulos2010();
				seqalign.setRoleWeights(m_rolesW);
			}
			//lierni
			//Medoideari bukaerako letra kendu
			String[] medoidId = new String[medoid.length];
			for (int m=0; m<medoid.length; m++){
				String url = medoid[m].substring(0, medoid[m].length()-1);
				medoidId[m] = url;
			}
			float sim = seqalign.getScore(waydone, medoid);
			simA[i] = sim;
		}
		
		// order the similarities or the distance
		float[] simAord = this.orderSim(simA);
		
		// return the index of medoids ordered
		Object[] objA = this.orderSimilarities(simA, simAord);
		//int[] orderedMedoids = (int[])objA[0];
		//float[] orderedSims = (float[])objA[1];
		return objA;
	}
	
	/**
	 * Knn sim. with shorter medoids
	 *
	 * @return the object[]
	 */
	//lierni
	protected Object[] knnSimShort(){
		// if we do not know nothing about the navigation
		// return the most centered medoid in the database
		if(m_waydone.size()==0){
			Object[] objA = new Object[2];
			objA[0] = m_gmedoids;
			objA[1] = null;
			return objA;
		}
		
		// Else find the nearest medoid
		// waydone to String[]
		String[] waydone = new String[m_waydone.size()];
		for(int i=0; i<m_waydone.size(); i++){ waydone[i] = m_waydone.get(i); }

		// compute the similarities with the medoids
		float[] simA = new float[m_medoids.size()];
		int max = 0; 
		for(int i=0; i<m_medoids.size(); i++){
			String[] medoid = {m_medoids.get(i)[0]};
			if (m_medoids.get(i).length<=m_waydone.size()){
				medoid = m_medoids.get(i);
				max = m_waydone.size();
			} else {
				//medoid = Arrays.copyOfRange(m_medoids.get(i), 0, m_waydone.size());
				if (m_medoids.get(i).length==m_waydone.size()+1){
					medoid = Arrays.copyOfRange(m_medoids.get(i), 0, m_waydone.size()+1);
					max = m_waydone.size()+1;
				} else {
					medoid = Arrays.copyOfRange(m_medoids.get(i), 0, m_waydone.size()+2);
					max = m_waydone.size()+2;
				}
			}
			SequenceAlignment seqalign;
			if(m_isDistance){
				seqalign = new SequenceAlignmentLevenshtein();
				seqalign.setRoleWeights(m_rolesW);
			} else{
				seqalign = new SequenceAlignmentCombineGlobalLocalDimopoulos2010();
				seqalign.setRoleWeights(m_rolesW);
			}
			//lierni
			//Medoideari bukaerako letra kendu
			String[] medoidId = new String[medoid.length];
			for (int m=0; m<medoid.length; m++){
				String url = medoid[m].substring(0, medoid[m].length()-1);
				medoidId[m] = url;
			}
			float sim = seqalign.getScore(waydone, medoidId);
			//simA[i] = sim;
			simA[i] = sim/max;
		}
		
		// order the similarities or the distance
		float[] simAord = this.orderSim(simA);
		
		// return the index of medoids ordered
		Object[] objA = this.orderSimilarities(simA, simAord);
		//int[] orderedMedoids = (int[])objA[0];
		//float[] orderedSims = (float[])objA[1];
		return objA;
	}
	
	/**
	 * Order sim.
	 *
	 * @param sims the sims
	 * @return the float[]
	 */
	private float[] orderSim(float[] sims){
		// order the similarities
		float[] simAord = sims.clone();
		Arrays.sort(simAord);
		
		if(m_isDistance){
			float[] simAordCopy = simAord.clone();
			// reverse the simAord array
			int iaux = 0;
			for(int i=simAordCopy.length-1; i>=0; i--){
				simAord[iaux] = simAordCopy[i];
				iaux++;
			}
		}
		
		return simAord;
	}
	
	/**
	 * Order similarities.
	 *
	 * @param sims the sims
	 * @param orderSims the order sims
	 * @return the object[]
	 */
	private Object[] orderSimilarities(float[] sims, float[] orderSims){
		// order from biggest similarity value to the minimum
		// initialize the result array
		int[] orderedMedoids = new int[sims.length];
		float[] orderedSims = new float[sims.length];
		int ind = 0;
		
		// to check if we have used before
		boolean[] isOrderA = new boolean[sims.length];
		Arrays.fill(isOrderA, false);
		// for the largest to smallest find the medoid index
		for(int i=orderSims.length-1; i>=0; i--){
			float sim = orderSims[i];
			for(int j=0; j<sims.length; j++){
				if(!isOrderA[j] && sim==sims[j]){
					orderedMedoids[ind] = j;
					orderedSims[ind] = sim;
					ind++;
					isOrderA[j] = true;
					break;
				}
			}
		}
		
		// return the index of medoids ordered
		Object[] objA = new Object[2];
		objA[0] = orderedMedoids;
		objA[1] = orderedSims;
		return objA;
	}

	
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsUnbounded()
	 */
	public ArrayList<String> getNextpossibleStepsUnbounded(){
		return this.getNextpossibleSteps(100);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsRandom(int, long)
	 */
	public ArrayList<String> getNextpossibleStepsRandom(int nReco, long seed){
		return this.getNextpossibleSteps(nReco);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedTrain(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTrain(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleSteps(nRecos);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedTest(int)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedTest(int nrecos){
		return this.getNextpossibleSteps(nrecos);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeighted(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeighted(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleSteps(nRecos);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsMarkov(int, java.util.ArrayList, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsMarkov(int nRecos, ArrayList<String> waydone, ArrayList<String> listMarkov){
		return this.getNextpossibleSteps(nRecos);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedByOriginalSequences(int)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedByOriginalSequences(int nRecos){
		return this.getNextpossibleSteps(nRecos);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.webrecommendation.recommender.Recommender#getNextpossibleStepsWeightedEnrichWithStep1(int, java.util.ArrayList)
	 */
	public ArrayList<String> getNextpossibleStepsWeightedEnrichWithStep1(int nRecos, ArrayList<String> waydone){
		return this.getNextpossibleSteps(nRecos);
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		
		// create medoids list
		ArrayList<String[]> medoids = new ArrayList<String[]>();
		String[] medoid1 = new String[]{"1H","2H","3C"};
		String[] medoid2 = new String[]{"3C","2C","1C"};
		medoids.add(medoid1);
		medoids.add(medoid2);
		
		// create global-medoids index array
		int[] gmedoids = new int[]{1,0};
		
		// create recos
		ArrayList<Object[]> recos = new ArrayList<Object[]>();

		ArrayList<String> recs1 = new ArrayList<String>();
		recs1.add("1");
		recs1.add("2");
		ArrayList<Integer> supp1 = new ArrayList<Integer>();
		supp1.add(4);
		supp1.add(3);
		Object[] objA1 = new Object[2];
		objA1[0] = recs1;
		objA1[1] = supp1;
		
		ArrayList<String> recs2 = new ArrayList<String>();
		recs2.add("3");
		recs2.add("2");
		ArrayList<Integer> supp2 = new ArrayList<Integer>();
		supp2.add(3);
		supp2.add(2);
		Object[] objA2 = new Object[2];
		objA2[0] = recs2;
		objA2[1] = supp2;
		
		recos.add(objA1);
		recos.add(objA2);
		
		// Run the classs
		ArrayList<String> list;
		ArrayList<String> waydone = new ArrayList<String>();
		float[][] rolesW = {{ 0f, 0f, 0f},
							{ 0f, 0f, 0f},
							{ 0f, 0f, 0f}};
		RecommenderKnnToClustersTopURLs rkt5 = 
				new RecommenderKnnToClustersTopURLs(
						medoids, null, gmedoids, 
						recos, true, rolesW);
		
		// STEP0
		list = rkt5.getNextpossibleSteps(5);
		System.out.println("STEP0");
		for(int i=0; i<list.size(); i++){ System.out.println("reco: " + list.get(i));}
		
		// STEP1
		waydone.add("1H");
		rkt5.update(waydone, null, false, false);
		
		// STEP2
		waydone.add("2H");
		rkt5.update(waydone, null, false, false);
		list = rkt5.getNextpossibleSteps(5);
		System.out.println("STEP2");
		for(int i=0; i<list.size(); i++){ System.out.println("reco: " + list.get(i));}

	}
	
}
