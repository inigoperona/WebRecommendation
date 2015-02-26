package FPlierni.webrecommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ehupatras.clustering.cvi.CVI;
import ehupatras.clustering.sapehac.dendrogram.Dendrogram;
import ehupatras.clustering.sapehac.dendrogram.DendrogramNode;
import ehupatras.clustering.sapehac.dendrogram.MergeNode;
import ehupatras.clustering.sapehac.dendrogram.ObservationNode;
import FPlierni.webrecommendation.SEP;


public class COP {

	/** The m_dendrogram. */
	private Dendrogram m_dendrogram;
	
	/** The m_distanceMatrix. */
	private float[][] m_distanceMatrix;

	/**
	 * Instantiates a new COP.
	 *
	 * @param dataInds the data inds
	 * @param clusters the clusters
	 */
	public COP(Dendrogram dendrogram, float[][] distanceMatrix){
		m_dendrogram = dendrogram;
		m_distanceMatrix = distanceMatrix;
	}
	
	/**
	 * Computes the COP values for each node
	 *
	 * @return the COP values
	 */
	
	public int[] getClustersFromNodes(ArrayList<DendrogramNode> clusterList, int cases){
		// See which cases are in the sub tree and assign the cluster
		int[] clustersA = new int[cases];
		for(int i=0; i<clusterList.size(); i++){
			ArrayList<DendrogramNode> subClusterList = new ArrayList<DendrogramNode>();
			DendrogramNode node = clusterList.get(i);
			subClusterList.add(node);
			for(int j=0; j<subClusterList.size(); j++){
				DendrogramNode subnode = subClusterList.get(j);
				String nodeClassStr = subnode.getClass().toString();
				if(nodeClassStr.contains("MergeNode")){
					MergeNode mnode = (MergeNode)subnode;
					subClusterList.add(mnode.getLeft());
					subClusterList.add(mnode.getRight());
				}
				if(nodeClassStr.contains("ObservationNode")){
					ObservationNode onode = (ObservationNode)subnode;
					int obs = onode.getObservation();
					clustersA[obs] = i;
				}
			}
		}
		return clustersA;
	}
	
	public double computeCOP(ArrayList<DendrogramNode> parPartition, int[] m_clusters, int[] dataset, int cases){
		ArrayList<DendrogramNode> nodesList = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> clusterList = new ArrayList<DendrogramNode>();
		
		
		
		CVI copCVI = new CVI(dataset, int[] clusters);
		
		double cop=0.0;
		double intracop=0.0;
		double intercop=0.0;
		double dist;

		copCVI.computeMedoids(m_distanceMatrix, m_clusters, cases);
		
		for (int i=0; i<parPartition.size();i++){
			
			for (int j=0; j<parPartition.get(i).getObservationCount(); j++){
				intracop=intracop+distancematrix[][];
			}
			intracop=intracop/parPartition.get(i).getObservationCount();
			for (int l=0; l<i; l++){
				dist = max(parPartition.get(i),parPartition.get(l));
				if (dist<intercop){
					intercop=dist;
				}
			}
			for (int l=i; l<parPartition.size(); l++){
				dist = max(parPartition.get(i),parPartition.get(l));
				if (dist<intercop){
					intercop=dist;
				}
			}
			cop=cop+(parPartition.get(i).getObservationCount()*intracop/intercop);
		}
		cop=cop/parPartition.size();
		return cop;
	}
	
	public double max(DendrogramNode node1, DendrogramNode node2){
		double max=0.0;
		return max;
	}
	
	public void computeMedoids(float[][] distancematrix, int[] m_clusters, int cases){
		// compute the index of the biggest cluster
		int maxcli = -1;
		for(int i=0; i<m_clusters.length; i++){
			int cli = m_clusters[i];
			if(maxcli<cli){
				maxcli = cli;
			}
		}
		m_medoids = new int[maxcli+1];
		
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
					float d = distancematrix[ind1][ind2];
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
		}

		
		
		

		
	}

	

}
