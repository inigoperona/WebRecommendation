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
		ArrayList<DendrogramNode> union = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> nodeArray = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> childSEP = new ArrayList<DendrogramNode>();
		double unionCOP=0, nodeCOP=0;
		
		COP copIndex = new COP(m_distanceMatrix, m_dendrogram);
		
		String nodeClassStr = node.getClass().toString();
		if(nodeClassStr.contains("ObservationNode")){
			ObservationNode onode = (ObservationNode)node;
			nodeArray.add(onode);
			return nodeArray;
		} else {
			childSEP = computeSEP(node.getLeft());
			for (int j=0; j<childSEP.size(); j++){
				union.add(childSEP.get(j));
			}
			childSEP = computeSEP(node.getRight());
			for (int j=0; j<childSEP.size(); j++){
				union.add(childSEP.get(j));
			}
			unionCOP = copIndex.computeCOP(union);
			nodeArray.add(node);
			nodeCOP = copIndex.computeCOP(nodeArray);
			if (nodeCOP<=unionCOP){
				return nodeArray;
			} else{
				return union;
			}
		}		
	}
	
	public int[] computeSEPSeq(){
		ArrayList<DendrogramNode> nodes = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> nodeId = new ArrayList<DendrogramNode>();
		ArrayList<Double> unionC = new ArrayList<Double>();
		ArrayList<Double> nodeC = new ArrayList<Double>();
		ArrayList<ArrayList<DendrogramNode>> bestPartition = new ArrayList<ArrayList<DendrogramNode>>();
		double unionCOP=0, nodeCOP=0;
		
		//get the list of nodes
		nodes.add(m_dendrogram.getRoot());
		for (int i=0; i<nodes.size(); i++){
			DendrogramNode dnode = nodes.get(i);
			String nodeClassStr = nodes.get(i).getClass().toString();
			if(!nodeClassStr.contains("ObservationNode")){
				nodes.add(dnode.getRight());
				nodes.add(dnode.getLeft());
			}
		}
		
		COP copIndex = new COP(m_distanceMatrix, m_dendrogram);
		
		for (int l=nodes.size()-1; l>=0; l--){
			DendrogramNode node = nodes.get(l);
			String nodeClassStr = node.getClass().toString();
			if(nodeClassStr.contains("ObservationNode")){
				ObservationNode onode = (ObservationNode)node;
				ArrayList<DendrogramNode> nodeArray = new ArrayList<DendrogramNode>();
				nodeArray.add(onode);
				nodeId.add(node);
				bestPartition.add(nodeArray);
				unionC.add(1.0);
				nodeC.add(1.0);
			} else {
				ArrayList<DendrogramNode> union = new ArrayList<DendrogramNode>();
				DendrogramNode left = node.getLeft();
				int indexl = nodeId.indexOf(left);
				union.addAll(bestPartition.get(indexl));
				DendrogramNode right = node.getRight();
				int indexr = nodeId.indexOf(right);
				union.addAll(bestPartition.get(indexr));
				unionCOP = copIndex.computeCOP(union);
				ArrayList<DendrogramNode> nodeArray = new ArrayList<DendrogramNode>();
				nodeArray.removeAll(nodeArray);
				nodeArray.add(node);
				nodeCOP = copIndex.computeCOP(nodeArray);
				nodeId.add(node);
				unionC.add(unionCOP);
				nodeC.add(nodeCOP);
				if (nodeCOP<unionCOP){
					bestPartition.add(nodeArray);
				} else{
					bestPartition.add(union);
				}
			}		
		}	
		int indexro = nodeId.indexOf(m_dendrogram.getRoot());
		System.out.println("ClusterKop: " + bestPartition.get(indexro).size());
		/*System.out.print("unionCOP: ");
		for (int k=0; k<unionC.size(); k++){
			System.out.print(unionC.get(k) + ", ");
		}
		System.out.println();
		System.out.print("nodeCOP: ");
		for (int k=0; k<nodeC.size(); k++){
			System.out.print(nodeC.get(k) + ", ");
		}
		System.out.println();*/
		int[] clustersA = getClustersFromNodes(bestPartition.get(indexro));
		return clustersA;
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
