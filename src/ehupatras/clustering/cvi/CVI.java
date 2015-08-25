package ehupatras.clustering.cvi;

import java.util.ArrayList;
import java.util.Arrays;
import ehupatras.webrecommendation.distmatrix.MatrixStructure;

// TODO: Auto-generated Javadoc
/**
 * The Class CVI.
 */
public class CVI {
	
	/** The m_data inds. */
	private int[] m_dataInds;
	
	/** The m_clusters. */
	private int[] m_clusters;
	
	/** The m_medoids. */
	private int[] m_medoids;
	private int[] m_clustersizes;
	
	/** The m_global medoids. */
	private int[] m_globalMedoids;
	
	/**
	 * Instantiates a new cvi.
	 *
	 * @param dataInds the data inds
	 * @param clusters the clusters
	 */
	public CVI(int[] dataInds, int[] clusters){
		m_dataInds = dataInds;
		m_clusters = clusters;
	}
	
	/**
	 * Compute medoids.
	 *
	 * @param distancematrix the distancematrix
	 */
	public void computeMedoids(MatrixStructure distancematrix){
		// compute the index of the biggest cluster
		int maxcli = -1;
		for(int i=0; i<m_clusters.length; i++){
			int cli = m_clusters[i];
			if(maxcli<cli){
				maxcli = cli;
			}
		}
		m_medoids = new int[maxcli+1];
		int[] clustersizes = new int[maxcli+1]; 
		
		// compute the MEDOIDS
		for(int j=0; j<=maxcli; j++){
			// Take the indexes of the cases which are in the cluster
			ArrayList<Integer> inds = new ArrayList<Integer>();
			for(int i=0; i<m_clusters.length; i++){
				int cli = m_clusters[i];
				if(j==cli){
					inds.add(m_dataInds[i]);
				}
			}
			
			// The distances among them
			float[] avgdist = new float[inds.size()];
			for(int i1=0; i1<inds.size(); i1++){
				int ind1 = inds.get(i1);
				float sumdists = 0f;
				for(int i2=0; i2<inds.size(); i2++){
					int ind2 = inds.get(i2);
					float d = distancematrix.getCell(ind1, ind2);
					sumdists = sumdists + d;
				}
				avgdist[i1] = sumdists / (float)inds.size(); 
			}
			
			// Take the case that minimize the distance to all its cluster members as a medoid
			float mindist = Float.MAX_VALUE;
			int minind = -1;
			for(int i=0; i<avgdist.length; i++){
				float d = avgdist[i];
				if(mindist>d){
					mindist = d;
					minind = inds.get(i);
				}
			}
			m_medoids[j] = minind;
			clustersizes[j] = inds.size();
		}
		
		// remove the clusters that does not exists
		ArrayList<Integer> medoidsA = new ArrayList<Integer>();
		ArrayList<Integer> clustersizesA = new ArrayList<Integer>();
		for(int i=0; i<m_medoids.length; i++){
			if(m_medoids[i]>=0){
				medoidsA.add(m_medoids[i]);
				clustersizesA.add(clustersizes[i]);
			}
		}
		m_medoids = new int[medoidsA.size()];
		m_clustersizes = new int[medoidsA.size()];
		for(int i=0; i<medoidsA.size(); i++){
			m_medoids[i] = medoidsA.get(i);
			m_clustersizes[i] = clustersizesA.get(i);
			//System.out.println("m_" + m_medoids[i]);
		}
		
		// compute the GLOBAL-MEDOIDS
		float[] avgdist = new float[m_medoids.length];
		for(int i=0; i<m_medoids.length; i++){
			int ind1 = m_medoids[i];
			float sumdists = 0f;
			for(int j=0; j<m_medoids.length; j++){
				int ind2 = m_medoids[j];
				float d = distancematrix.getCell(ind1, ind2);
				sumdists = sumdists + d;
			}
			avgdist[i] = sumdists / (float)m_medoids.length; 
		}
		
		// order the medoids from the nearest to farthest
		m_globalMedoids = new int[m_medoids.length];
		boolean[] isOrdered = new boolean[m_medoids.length];
		Arrays.fill(isOrdered, false);
		for(int j=0; j<m_medoids.length; j++){
			// Take the case that minimize the distance to all its cluster members as a medoid
			float mindist = Float.MAX_VALUE;
			int minind = -1;
			for(int i=0; i<avgdist.length; i++){
				float d = avgdist[i];
				if(!isOrdered[i] && mindist>d){
					mindist = d;
					minind = i;
				}
			}
			m_globalMedoids[j] = minind;
			isOrdered[minind] = true;
		}
		
	}
	
	/**
	 * Gets the medoids.
	 *
	 * @return the medoids
	 */
	public int[] getMedoids(){
		return m_medoids;
	}
	
	public int[] getClusterSizes(){
		return m_clustersizes;
	}
	
	/**
	 * Gets the global medoids.
	 *
	 * @return the global medoids
	 */
	public int[] getGlobalMedoids(){
		return m_globalMedoids;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		// data points indexes and their respective cluster indexes
		int[] dataInds = new int[]{1,2,3,4};
		int[] clusters = new int[]{0,0,2,2};
		
		// the distance matrix
		float[][] dm = 
				new float[][]{	{ 0f,.3f,.4f,.5f,.2f},
								{.3f, 0f,.8f,.1f,.2f},
								{.4f,.8f, 0f,.2f,.6f},
								{.5f,.1f,.2f, 0f,.2f},
								{.2f,.2f,.6f,.2f, 0f}};
		MatrixStructure ms = new MatrixStructure(dm);
		
		CVI cvindex = new CVI(dataInds,clusters);
		cvindex.computeMedoids(ms);
		int[] meds  = cvindex.getMedoids();
		int[] gmeds = cvindex.getGlobalMedoids();
		System.out.println("medoids");
		for(int i=0; i<meds.length; i++){ System.out.println(meds[i]); }
		System.out.println("gmedoids");
		for(int i=0; i<gmeds.length; i++){ System.out.println(gmeds[i]); }
	}
	
}
