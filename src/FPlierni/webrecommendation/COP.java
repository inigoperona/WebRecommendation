package FPlierni.webrecommendation;

import java.util.ArrayList;

import ehupatras.clustering.sapehac.dendrogram.DendrogramNode;
import ehupatras.clustering.sapehac.dendrogram.ObservationNode;

public class COP {
	
	public float[][] m_distanceMatrix;
	
	public COP(float[][] distanceMatrix){
		m_distanceMatrix = distanceMatrix;
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
				avgdist[i1] = sumdists / (float)leafs.size(); 
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

	public float computeCOP(ArrayList<DendrogramNode> m_clusters){
		ArrayList<ObservationNode> leafsList = new ArrayList<ObservationNode>();
		ArrayList<ObservationNode> medoids = new ArrayList<ObservationNode>();
		float copvalue=0;
		float clustercop=0;
		float intracop, intercop;
		float dist;
		
		copvalue=1/m_clusters.size();
		
		medoids = computeMedoids(m_clusters);
		
		for (int i=0; i<m_clusters.size(); i++){
			//Get the observationNodes of the cluster
			leafsList = getLeafs(m_clusters.get(i));
			//Calculate intraCOP
			intracop=0;
			for (int j=0; j<leafsList.size(); j++){
				intracop = intracop + m_distanceMatrix[leafsList.get(j).getObservation()][medoids.get(i).getObservation()];
			}
			intracop=intracop/leafsList.size();
			//Calculate interCOP
			intercop=0;
			for (int l=0; l<i; l++){
				dist = max(m_clusters.get(i),m_clusters.get(l));
				if (dist<intercop){
					intercop=dist;
				}
			}
			for (int l=i; l<m_clusters.size(); l++){
				dist = max(m_clusters.get(i),m_clusters.get(l));
				if (dist<intercop){
					intercop=dist;
				}
			}			
			//Add actual clusters value
			clustercop=clustercop+(m_clusters.get(i).getObservationCount()*(intracop/intercop));
		}
		//Calculate COP
		copvalue=copvalue*clustercop;
		return copvalue;
	}
	
	public float max(DendrogramNode node1, DendrogramNode node2){
		float max=0;
		float dist=0;
		ArrayList<ObservationNode> node1leafs = new ArrayList<ObservationNode>();
		ArrayList<ObservationNode> node2leafs = new ArrayList<ObservationNode>();
		
		node1leafs = getLeafs(node1);
		node2leafs = getLeafs(node2);
		
		for (int j=0; j<node1leafs.size(); j++){
			for (int k=0; k<node2leafs.size(); k++){
				dist = m_distanceMatrix[node1leafs.get(j).getObservation()][node2leafs.get(k).getObservation()];
				if (dist > max){
					max = dist;
				}
			}
		}
		return max;
	}
	
	public ArrayList<ObservationNode> getLeafs(DendrogramNode node){
		ArrayList<DendrogramNode> nodes = new ArrayList<DendrogramNode>();
		ArrayList<ObservationNode> leafs = new ArrayList<ObservationNode>();
		nodes.add(node);
		for (int i=0; i<nodes.size(); i++){
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
	
	
	
}
