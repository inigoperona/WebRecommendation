package FPlierni.webrecommendation;

import java.util.ArrayList;

import ehupatras.clustering.sapehac.dendrogram.Dendrogram;
import ehupatras.clustering.sapehac.dendrogram.DendrogramNode;
import ehupatras.clustering.sapehac.dendrogram.ObservationNode;

public class SEP2 {
	
	public Dendrogram m_dendrogram;
	public float[][] m_distanceMatrix;
	public ArrayList<ObservationNode> dataset;
	
	public ArrayList<DendrogramNode> computeSEP2 (DendrogramNode node){
		ArrayList<DendrogramNode> clusterList = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> union = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> nodeArray = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> childSEP = new ArrayList<DendrogramNode>();
		int casesUnion=0;
		float unionCOP=0, nodeCOP=0;
		
		COP2 copIndex = new COP2();
		
		String nodeClassStr = node.getClass().toString();
		if(nodeClassStr.contains("ObservationNode")){
			ObservationNode onode = (ObservationNode)node;
			clusterList.add(onode);
			return clusterList;
		} else {
			childSEP = computeSEP2(node.getLeft());
			for (int j=0; j<childSEP.size(); j++){
				union.add(childSEP.get(j));
				casesUnion=casesUnion+childSEP.get(j).getObservationCount();
			}
			childSEP = computeSEP2(node.getRight());
			for (int j=0; j<childSEP.size(); j++){
				union.add(childSEP.get(j));
				casesUnion=casesUnion+childSEP.get(j).getObservationCount();
			}
			unionCOP = copIndex.computeCOP2(union, dataset);
			nodeArray.add(node);
			nodeCOP = copIndex.computeCOP2(nodeArray, dataset);
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

}
