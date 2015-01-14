package ehupatras.clustering;

import java.util.ArrayList;

public class ClusteringSilly {

	private int m_k = 10;
	private ArrayList<Integer> m_medoids;
	private int[] m_clusterassignment;
		
	public ClusteringSilly(int k, float[][] dm, int[] realIndexes){
		// number of clusters (k) must be smaller than size of the training set 
		if(k>realIndexes.length){
			m_k = realIndexes.length;
		} else {
			m_k = k;
		}
		
		// assign the cluster/class
		m_clusterassignment = new int[realIndexes.length];
		int clustersize = realIndexes.length / m_k;
		int iclust = 0;
		for(int i=0; i<realIndexes.length; i++){
			if(i % clustersize == 0){
				m_medoids.add(i);
				iclust++;
			}
			m_clusterassignment[i] = iclust;
		}
	}
	
	public int[] getClustering(){
		return m_clusterassignment;
	}
	
	public static void main(String[] args){
		int[] casesIndexes = new int[100];
		ClusteringSilly cs = new ClusteringSilly(10, null, casesIndexes);
		int[] clustering = cs.getClustering();
		for(int i=0; i<clustering.length; i++){
			System.out.println(i + ":" + clustering[i]);
		}
	}
	
}
