package FPlierni.webrecommendation;

import java.util.ArrayList;

import ehupatras.clustering.cvi.CVI;
import ehupatras.clustering.sapehac.dendrogram.*;
import ehupatras.clustering.ClusteringHierarchical.*;


public class SEP {
	
	public ArrayList<DendrogramNode> computeSEP(CVI cop, DendrogramNode node){
		ArrayList<DendrogramNode> clusterList = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> union = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> childSEP = new ArrayList<DendrogramNode>();
		double unionCOP=0.0, unionCOPl=0.0, unionCOPr=0.0;
		String nodeClassStr = node.getClass().toString();
		if(nodeClassStr.contains("ObservationNode")){
			ObservationNode onode = (ObservationNode)node;
			clusterList.add(onode);
		} else {
			childSEP = computeSEP(cop, node.getLeft());
			for (int j=0; j<childSEP.size(); j++){
				union.add(childSEP.get(j));
				unionCOPl=unionCOPl+childSEP.get(j).getCOP();
			}
			unionCOPl=unionCOPl/childSEP.size();
			childSEP = computeSEP(cop, node.getRight());
			for (int j=0; j<childSEP.size(); j++){
				union.add(childSEP.get(j));
				unionCOPr=unionCOPr+childSEP.get(j).getCOP();
			}
			unionCOPr=unionCOPr/childSEP.size();
			//Zati 2 behar do??
			unionCOP=(unionCOPl+unionCOPr)/2;
			/*for (int i=0; i<node.getObservationCount(); i++){
				//GAIZKII
				
				childSEP = computeSEP(cop, node);
				for (int j=0; j<childSEP.size(); j++){
					union.add(childSEP.get(j));
					unionCOP=unionCOP+childSEP.get(j).getCOP();
				}
			}*/
		}
		if (node.getCOP()< unionCOP){
			clusterList.add(node);
		} else{
			for (int l=0; l<union.size(); l++){
				clusterList.add(union.get(l));
			}
		}
		return clusterList;
		
	}
	
	//Funtzio honi deitzerakoan, cases moduan, dendrogramaren hosto kopurua sartu
	public int[] getSEPclusters(ArrayList<DendrogramNode> clusterList, int cases){
		int[] clustersA = getClustersFromNodes(clusterList, cases);
		return clustersA;
	}
	
	private int[] getClustersFromNodes(ArrayList<DendrogramNode> clusterList, int cases){
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
