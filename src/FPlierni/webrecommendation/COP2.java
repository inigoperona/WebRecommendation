package FPlierni.webrecommendation;

import java.util.ArrayList;

import ehupatras.clustering.sapehac.dendrogram.Dendrogram;
import ehupatras.clustering.sapehac.dendrogram.DendrogramNode;
import ehupatras.clustering.sapehac.dendrogram.ObservationNode;

public class COP2 {
	
	public float[][] m_distanceMatrix;
	public Dendrogram m_dendrogram;
	
	public COP2(float[][] distanceMatrix, Dendrogram dendrogram){
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

	public double computeCOP2(ArrayList<DendrogramNode> m_clusters){
		ArrayList<ObservationNode> leafsList = new ArrayList<ObservationNode>();
		ArrayList<ObservationNode> restLeafs = new ArrayList<ObservationNode>();
		ArrayList<ObservationNode> medoids = new ArrayList<ObservationNode>();
		double copvalue=0.0;
		double clustercop=0.0;
		double intracop, intercop;
		double dist;
		double cases = 0;
		
		medoids = computeMedoids(m_clusters);
		
		if (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot()){
			return 1.0;
		}
		/*
		if ((m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getLeft()) 
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getRight())){
			return 1.1;
		}
		
		if ((m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getLeft().getLeft()) 
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getLeft().getRight())
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getRight().getLeft())
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getRight().getRight())){
			return 1.1;
		}
		
		if ((m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getLeft().getLeft().getLeft())
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getLeft().getLeft().getRight())
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getLeft().getRight().getLeft())
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getLeft().getRight().getRight())
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getRight().getLeft().getLeft())
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getRight().getLeft().getRight())
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getRight().getRight().getLeft())
				|| (m_clusters.size()==1 && m_clusters.get(0)==m_dendrogram.getRoot().getRight().getRight().getRight())){
			return 1.1;
		}*/
		
		
		for (int i=0; i<m_clusters.size(); i++){
			cases = cases + m_clusters.get(i).getObservationCount();
			String nodeClassStr = m_clusters.get(i).getClass().toString();
			if(nodeClassStr.contains("ObservationNode")){
				clustercop=clustercop+1.0;
			} else {
				//Get the observationNodes of the cluster
				leafsList = getLeafs(m_clusters.get(i));
				//Calculate intraCOP
				intracop=0.0;
				for (int j=0; j<leafsList.size(); j++){
					intracop = intracop + m_distanceMatrix[leafsList.get(j).getObservation()][medoids.get(i).getObservation()];
				}
				intracop=intracop/leafsList.size();
				//Get the leafs that are not in the cluster
				restLeafs = getRest(m_clusters.get(i));
				//Calculate interCOP
				intercop = Double.MAX_VALUE;
				for (int m=0; m<restLeafs.size(); m++){
					ArrayList<ObservationNode> oneLeaf = new ArrayList<ObservationNode>();
					oneLeaf.add(restLeafs.get(m));
					dist = max(leafsList, oneLeaf);
					if (dist<intercop){
						intercop=dist;
					}
				}
				//Add actual clusters value
				if (intercop!=0.0){
					clustercop=clustercop+(m_clusters.get(i).getObservationCount()*(intracop/intercop));
				}
			}
		}
		//Calculate COP
		copvalue=clustercop/cases;
		return copvalue;
	}
	
	public double max(ArrayList<ObservationNode> node1leafs, ArrayList<ObservationNode> restleafs){
		double max=0.0;
		double dist=0.0;
		
		for (int j=0; j<node1leafs.size(); j++){
			for (int k=0; k<restleafs.size(); k++){
				dist = m_distanceMatrix[node1leafs.get(j).getObservation()][restleafs.get(k).getObservation()];
				if (dist > max){
					max = dist;
				}
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
