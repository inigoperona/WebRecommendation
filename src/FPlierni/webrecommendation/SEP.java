package FPlierni.webrecommendation;

import java.util.ArrayList;

import ehupatras.clustering.sapehac.dendrogram.*;


public class SEP {
	
	public ArrayList<DendrogramNode> computeSEP(DendrogramNode node, Dendrogram dendrogram){
		ArrayList<DendrogramNode> clusterList = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> union = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> nodeArray = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> childSEP = new ArrayList<DendrogramNode>();
		
		int casesUnion = 0;
		
		COP copIndex = new COP(dendrogram);
		
		double unionCOP = 0.0, nodeCOP = 0.0;
		String nodeClassStr = node.getClass().toString();
		if(nodeClassStr.contains("ObservationNode")){
			ObservationNode onode = (ObservationNode)node;
			clusterList.add(onode);
			return clusterList;
		} else {
			childSEP = computeSEP(node.getLeft(), dendrogram);
			for (int j=0; j<childSEP.size(); j++){
				union.add(childSEP.get(j));
				casesUnion=casesUnion+childSEP.get(j).getObservationCount();
			}
			childSEP = computeSEP(node.getRight(), dendrogram);
			for (int j=0; j<childSEP.size(); j++){
				union.add(childSEP.get(j));
				casesUnion=casesUnion+childSEP.get(j).getObservationCount();
			}
			unionCOP = copIndex.computeCOP(union, this.getClustersFromNodes(union, casesUnion), );
			nodeArray.add(node);
			nodeCOP = copIndex.computeCOP(nodeArray, this.getClustersFromNodes(nodeArray, node.getObservationCount()), );
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
	
	//Funtzio honi deitzerakoan, cases moduan, dendrogramaren hosto kopurua sartu
	public int[] getSEPclusters(ArrayList<DendrogramNode> clusterList, int cases){
		int[] clustersA = getClustersFromNodes(clusterList, cases);
		return clustersA;
	}
	
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

}
