package ehupatras.clustering;

import ehupatras.clustering.sapehac.HierarchicalAgglomerativeClusterer;
import ehupatras.clustering.sapehac.agglomeration.AgglomerationMethod;
import ehupatras.clustering.sapehac.agglomeration.AverageLinkage;
import ehupatras.clustering.sapehac.dendrogram.*;
import ehupatras.clustering.sapehac.experiment.DissimilarityMeasure;
import ehupatras.clustering.sapehac.experiment.DissimilarityMeasureEhupatras;
import ehupatras.clustering.sapehac.experiment.Experiment;
import ehupatras.clustering.sapehac.experiment.ExperimentEhuPatras;
import java.util.*;

public class ClusteringHierarchical {

	private Dendrogram m_dendrogram;
	private int m_ncases;
	
	public ClusteringHierarchical(int ncases, float[][] matrix){
		m_ncases = ncases;
		Experiment experiment = new ExperimentEhuPatras(ncases);
		DissimilarityMeasure dissimilarityMeasure = new DissimilarityMeasureEhupatras(matrix);
		//AgglomerationMethod agglomerationMethod = new SingleLinkage();
		AgglomerationMethod agglomerationMethod = new AverageLinkage();
		DendrogramBuilder dendrogramBuilder = new DendrogramBuilder(experiment.getNumberOfObservations());
		HierarchicalAgglomerativeClusterer clusterer = new HierarchicalAgglomerativeClusterer(experiment, dissimilarityMeasure, agglomerationMethod);
		clusterer.cluster(dendrogramBuilder);
		m_dendrogram = dendrogramBuilder.getDendrogram();
	}
	
	public void writeDendrogram(){
		ArrayList<DendrogramNode> nodesList = new ArrayList<DendrogramNode>();
		ArrayList<Integer> depthList = new ArrayList<Integer>();
		DendrogramNode root = m_dendrogram.getRoot();
		nodesList.add(root);
		depthList.add(0);
		for(int i=0; i<nodesList.size(); i++){
			DendrogramNode node = nodesList.get(i);
			int depth = depthList.get(i);
			String nodeClassStr = node.getClass().toString();
			if(nodeClassStr.contains("MergeNode")){
				MergeNode mnode = (MergeNode)node;
				for(int k=0; k<depth; k++){System.out.print(" ");}
				System.out.println(i + " : MergeNode" 
						+ "; NumberOfCases: " + mnode.getObservationCount() 
						+ "; Dissimilarity: " + mnode.getDissimilarity()
						+ "; Depth: " + depth);
				nodesList.add(mnode.getLeft());
				depthList.add(depth+1);
				nodesList.add(mnode.getRight());
				depthList.add(depth+1);
			}
			if(nodeClassStr.contains("ObservationNode")){
				ObservationNode onode = (ObservationNode)node;
				for(int k=0; k<depth; k++){System.out.print(" ");}
				System.out.println(i + " : ObservationNode" 
						+ "; NummberOfCases: " + onode.getObservationCount() 
						+ "; TheObservation: " + onode.getObservation()
						+ "; Depth: " + depth);
			}
		}
	}
	
	private int getDendrogramDepth(){
		ArrayList<DendrogramNode> nodesList = new ArrayList<DendrogramNode>();
		ArrayList<Integer> depthList = new ArrayList<Integer>();
		DendrogramNode root = m_dendrogram.getRoot();
		nodesList.add(root);
		depthList.add(0);
		for(int i=0; i<nodesList.size(); i++){
			DendrogramNode node = nodesList.get(i);
			int depth = depthList.get(i);
			String nodeClassStr = node.getClass().toString();
			if(nodeClassStr.contains("MergeNode")){
				MergeNode mnode = (MergeNode)node;
				nodesList.add(mnode.getLeft());
				depthList.add(depth+1);
				nodesList.add(mnode.getRight());
				depthList.add(depth+1);
			}
			if(nodeClassStr.contains("ObservationNode")){
				ObservationNode onode = (ObservationNode)node;
			}
		}
		
		// select the maximum value of depthList
		return depthList.get(depthList.size()-1);
	}
	
	public int getCutDepth(float pheight){
		int maxdepth = getDendrogramDepth();
		int cutdepth = Math.round((float)maxdepth*(pheight/(float)100));
		return cutdepth;
	}
	
	public int[] cutDendrogram(float pheight){
		// convert from percentage to depth
		int cutdepth = getCutDepth(pheight);
		
		// scan the dendrogram to find the the nodes we are interested in.
		ArrayList<DendrogramNode> clusterList = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> nodesList = new ArrayList<DendrogramNode>();
		ArrayList<Integer> depthList = new ArrayList<Integer>();
		DendrogramNode root = m_dendrogram.getRoot();
		nodesList.add(root);
		depthList.add(0);
		for(int i=0; i<nodesList.size(); i++){
			DendrogramNode node = nodesList.get(i);
			int depth = depthList.get(i);
			String nodeClassStr = node.getClass().toString();
			if(depth<=cutdepth){
				if(nodeClassStr.contains("MergeNode")){
					MergeNode mnode = (MergeNode)node;
					nodesList.add(mnode.getLeft());
					depthList.add(depth+1);
					nodesList.add(mnode.getRight());
					depthList.add(depth+1);
					if(depth==cutdepth){
						clusterList.add(mnode);
					}
				}
				if(nodeClassStr.contains("ObservationNode")){
					ObservationNode onode = (ObservationNode)node;
					clusterList.add(onode);
				}
			} else {
				break;
			}
		}
		
		// See which cases are in the sub tree and assign the cluster
		int[] clustersA = new int[m_ncases];
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
