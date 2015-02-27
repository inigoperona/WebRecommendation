package FPlierni.webrecommendation;

import java.util.ArrayList;

import ehupatras.clustering.sapehac.dendrogram.Dendrogram;
import ehupatras.clustering.sapehac.dendrogram.DendrogramNode;
import ehupatras.clustering.sapehac.dendrogram.MergeNode;
import ehupatras.clustering.sapehac.dendrogram.ObservationNode;

public class SEP {
	
	public Dendrogram m_dendrogram;
	public float[][] m_distanceMatrix;
	
	public SEP(Dendrogram dendrogram, float[][] distanceMatrix){
		m_dendrogram = dendrogram;
		m_distanceMatrix = distanceMatrix;
	}
	
	public ArrayList<DendrogramNode> computeSEP (DendrogramNode node){
		ArrayList<DendrogramNode> clusterList = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> union = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> nodeArray = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> childSEP = new ArrayList<DendrogramNode>();
		int casesUnion=0;
		float unionCOP=0, nodeCOP=0;
		
		COP copIndex = new COP(m_distanceMatrix);
		
		String nodeClassStr = node.getClass().toString();
		if(nodeClassStr.contains("ObservationNode")){
			ObservationNode onode = (ObservationNode)node;
			clusterList.add(onode);
			return clusterList;
		} else {
			childSEP = computeSEP(node.getLeft());
			for (int j=0; j<childSEP.size(); j++){
				union.add(childSEP.get(j));
				casesUnion=casesUnion+childSEP.get(j).getObservationCount();
			}
			childSEP = computeSEP(node.getRight());
			for (int j=0; j<childSEP.size(); j++){
				union.add(childSEP.get(j));
				casesUnion=casesUnion+childSEP.get(j).getObservationCount();
			}
			unionCOP = copIndex.computeCOP(union);
			nodeArray.add(node);
			nodeCOP = copIndex.computeCOP(nodeArray);
			if (nodeCOP<unionCOP){
				clusterList.add(node);
			} else{
				for (int l=0; l<union.size(); l++){
					clusterList.add(union.get(l));
				}
			}
			return clusterList;
		}		
	}
	
	public int[] getClustersFromNodes(ArrayList<DendrogramNode> clusterList){
		// See which cases are in the sub tree and assign the cluster
		int[] clustersA = new int[m_dendrogram.getRoot().getObservationCount()];
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

}
