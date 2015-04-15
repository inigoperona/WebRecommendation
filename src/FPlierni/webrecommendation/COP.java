package FPlierni.webrecommendation;

import java.util.ArrayList;

import ehupatras.clustering.sapehac.dendrogram.Dendrogram;
import ehupatras.clustering.sapehac.dendrogram.DendrogramNode;
import ehupatras.clustering.sapehac.dendrogram.ObservationNode;

public class COP {
	
	public float[][] m_distanceMatrix;
	public Dendrogram m_dendrogram;
	
	public COP(float[][] distanceMatrix, Dendrogram dendrogram){
		m_distanceMatrix = distanceMatrix;
		m_dendrogram = dendrogram;
	}
	
	public ArrayList<ObservationNode> computeMedoids(ArrayList<DendrogramNode> m_clusters){
		ArrayList<ObservationNode> m_medoids = new ArrayList<ObservationNode>();
		
		// compute the MEDOIDS
		for(int j=0; j<m_clusters.size(); j++){
			// Take the leafs of the cluster
			ArrayList<ObservationNode> leafs = new ArrayList<ObservationNode>();
			leafs = getLeafs(m_clusters.get(j));
			
			// The distances among them
			float[] avgdist = new float[leafs.size()];
			for(int i1=0; i1<leafs.size(); i1++){
				int ind1 = leafs.get(i1).getObservation();
				float sumdists = 0f;
				for(int i2=0; i2<leafs.size(); i2++){
					int ind2 = leafs.get(i2).getObservation();
					float d = m_distanceMatrix[ind1][ind2];
					sumdists = sumdists + d;
				}
				avgdist[i1] = sumdists / (float)(leafs.size()-1); 
			}
			
			// Take the case that minimize the distance to all its cluster members as a medoid
			float mindist = Float.MAX_VALUE;
			ObservationNode minind = (ObservationNode)leafs.get(0);
			for(int i=0; i<avgdist.length; i++){
				float d = avgdist[i];
				if(mindist>d){
					mindist = d;
					minind = (ObservationNode)leafs.get(i);
				}
			}
			m_medoids.add(minind);
		}
		return m_medoids;
		
	}

	public double computeCOP(ArrayList<DendrogramNode> m_clusters){
		ArrayList<ObservationNode> medoids = new ArrayList<ObservationNode>();
		ArrayList<ObservationNode> leafs = new ArrayList<ObservationNode>();
		DendrogramNode cluster;
		int cases = 0;
		double intraCOP=0.0, interCOP=0.0, clusterCOP=0.0, copValue=0.0;
		
		if (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot()){
			return 1.0;
		}
		
		medoids = computeMedoids(m_clusters);
		
		for (int i=0; i<m_clusters.size(); i++){
			cluster = m_clusters.get(i);
			cases = cases + cluster.getObservationCount();
			String nodeClassStr = cluster.getClass().toString();
			if(nodeClassStr.contains("ObservationNode")){
				clusterCOP = clusterCOP + 1.0;
			} else {
				leafs = getLeafs(cluster);
				intraCOP = intracop(leafs, medoids.get(i));
				interCOP = intercop(m_clusters.get(i), leafs);
				if (interCOP!=0.0){
					clusterCOP = clusterCOP + cluster.getObservationCount() * intraCOP / interCOP;
				} 
				//if interCOP==0.0 means that the two clusters are in the same place so we want them to be together in one cluster
				//that's why we give that cluster the best COP value possible: 0
			}
		}
		copValue = clusterCOP / cases;
		return copValue;
	}
	
	public double intracop(ArrayList<ObservationNode> cluster, ObservationNode medoid){
		double distSum = 0.0;
		
		for (int i=0; i<cluster.size(); i++){
			distSum = distSum + m_distanceMatrix[cluster.get(i).getObservation()][medoid.getObservation()];
		}
		return distSum / cluster.size();
	}
	
	public double intercop(DendrogramNode cluster, ArrayList<ObservationNode> leafs){
		ArrayList<ObservationNode> restLeafs = new ArrayList<ObservationNode>();
		double dist = 0.0, min = Double.MAX_VALUE;
		
		restLeafs = getRest(cluster);
		
		for (int i=0; i<restLeafs.size(); i++){
			dist = max(leafs, restLeafs.get(i));
			if (dist<min){
				min=dist;
			}
		}
		return min;
	}
	
	public double max(ArrayList<ObservationNode> leafs, ObservationNode node){
		double max=0.0;
		double dist=0.0;
		
		for (int i=0; i<leafs.size(); i++){
			dist = m_distanceMatrix[leafs.get(i).getObservation()][node.getObservation()];
			if (dist > max){
				max = dist;
			}
		}
		return max;
	}
	
	public ArrayList<ObservationNode> getLeafs(DendrogramNode dnode){
		ArrayList<DendrogramNode> nodes = new ArrayList<DendrogramNode>();
		ArrayList<ObservationNode> leafs = new ArrayList<ObservationNode>();
		nodes.add(dnode);
		for (int i=0; i<nodes.size(); i++){
			DendrogramNode node = nodes.get(i);
			String nodeClassStr = node.getClass().toString();
			if(nodeClassStr.contains("ObservationNode")){
				ObservationNode onode = (ObservationNode)node;
				leafs.add(onode);
			} else {
				nodes.add(node.getLeft());
				nodes.add(node.getRight());
			}
		}
		return leafs;
	}
	
	public ArrayList<ObservationNode> getRest(DendrogramNode dnode){
		ArrayList<DendrogramNode> nodes = new ArrayList<DendrogramNode>();
		ArrayList<ObservationNode> rest = new ArrayList<ObservationNode>();
		nodes.add(m_dendrogram.getRoot());
		for (int i=0; i<nodes.size(); i++){
			DendrogramNode node = nodes.get(i);
			if (!node.equals(dnode)){
				String nodeClassStr = node.getClass().toString();
				if(nodeClassStr.contains("ObservationNode")){
					ObservationNode onode = (ObservationNode)node;
					rest.add(onode);
				} else {
					nodes.add(node.getLeft());
					nodes.add(node.getRight());
				}
			}
		}
		return rest;
	}	
}
