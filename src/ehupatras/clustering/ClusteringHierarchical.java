package ehupatras.clustering;

import ehupatras.clustering.sapehac.HierarchicalAgglomerativeClusterer;
import ehupatras.clustering.sapehac.agglomeration.AgglomerationMethod;
import ehupatras.clustering.sapehac.agglomeration.WardLinkage;
import ehupatras.clustering.sapehac.dendrogram.*;
import ehupatras.clustering.sapehac.experiment.DissimilarityMeasure;
import ehupatras.clustering.sapehac.experiment.DissimilarityMeasureEhupatras;
import ehupatras.clustering.sapehac.experiment.Experiment;
import ehupatras.clustering.sapehac.experiment.ExperimentEhuPatras;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import java.util.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class ClusteringHierarchical {

	private Dendrogram m_dendrogram;
	private int m_ncases;
	private String m_savefilename = "/_dendrogram.javaData";
	
	public void computeHierarchicalClustering(float[][] matrix, int[] selectedcases,
			String agglomerationMethodClassName) {
		m_ncases = selectedcases.length;
		Experiment experiment = new ExperimentEhuPatras(m_ncases);
		
		// Define the dissimilarity matrix
		DissimilarityMeasure dissimilarityMeasure = new DissimilarityMeasureEhupatras(matrix, selectedcases);

		// Define agglomeration-linkage method
		// AgglomerationMethod agglomerationMethod = new WardLinkage();
		Class<AgglomerationMethod> clazz = null;
		try{
			clazz = (Class<AgglomerationMethod>)Class.forName(agglomerationMethodClassName);
		} catch (ClassNotFoundException ex){
		}
		Constructor<AgglomerationMethod> ctor = null;
		try{
			ctor = clazz.getConstructor(String.class);
		} catch (NoSuchMethodException ex){
		}
		AgglomerationMethod agglomerationMethod = null;
		try{
			agglomerationMethod = ctor.newInstance();
		
		} catch (InstantiationException ex){
		} catch (IllegalAccessException ex){
		} catch (IllegalArgumentException ex){
		} catch (InvocationTargetException ex){
		}
		
		// Compute the dendrogram
		DendrogramBuilder dendrogramBuilder = new DendrogramBuilder(experiment.getNumberOfObservations());
		HierarchicalAgglomerativeClusterer clusterer = new HierarchicalAgglomerativeClusterer(experiment, dissimilarityMeasure, agglomerationMethod);
		clusterer.cluster(dendrogramBuilder);
		m_dendrogram = dendrogramBuilder.getDendrogram();
	}
	
	public void save(String workdirectory){
		Object[] obj = new Object[2];
		obj[0] = m_dendrogram;
		obj[1] =m_ncases;
		SaveLoadObjects slo = new SaveLoadObjects();
		slo.save(obj, workdirectory + m_savefilename);
	}

	public void load(String workdirectory){
		SaveLoadObjects slo = new SaveLoadObjects();
		slo.load(workdirectory + m_savefilename);
		Object[] obj = (Object[])slo.load(workdirectory + m_savefilename);
		m_dendrogram = (Dendrogram)obj[0];
		m_ncases = (int)obj[1]; 
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
		}
		
		// select the maximum value of depthList, the last element
		// that is to say the maximum depth
		return depthList.get(depthList.size()-1);
	}
	
	private int getCutDepth(float pheight){
		int maxdepth = getDendrogramDepth();
		int cutdepth = Math.round((float)maxdepth*(pheight/(float)100));
		return cutdepth;
	}
	
	public int[] cutDendrogramByHeight(float pheight){
		// convert from percentage to depth to cut the dendrogram
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
		
		// return the clustering
		int[] clustersA = getClustersFromNodes(clusterList);
		return clustersA;
	}
	
	private int[] getClustersFromNodes(ArrayList<DendrogramNode> clusterList){
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
	
	private double getCutDissimilarity(float pDissimilarity){
		// take all dissimilarities of the dendrogram
		ArrayList<DendrogramNode> nodesList = new ArrayList<DendrogramNode>();
		ArrayList<Double> dissimilarityList = new ArrayList<Double>();
		DendrogramNode root = m_dendrogram.getRoot();
		nodesList.add(root);
		for(int i=0; i<nodesList.size(); i++){
			DendrogramNode node = nodesList.get(i);
			String nodeClassStr = node.getClass().toString();
			if(nodeClassStr.contains("MergeNode")){
				MergeNode mnode = (MergeNode)node;
				double diss = mnode.getDissimilarity();
				if(diss != (double)0.0){
					dissimilarityList.add(diss);
					nodesList.add(mnode.getLeft());
					nodesList.add(mnode.getRight());
				}
			}
		}
		
		// Order the dissimilarityList 
		// and take the element at pDissimilarity percentage position
		int position = Math.round(((float)dissimilarityList.size()-1)*(1-pDissimilarity/100));
		Collections.sort(dissimilarityList);
		return dissimilarityList.get(position);
	}
	
	public int[] cutDendrogramByDissimilarity(float pDissimilarity){
		// get the dissimilarity threshold to cut the dendrogram
		double dissimilarityThreshold = getCutDissimilarity(pDissimilarity);
		
		// scan the dendrogram to find the the nodes we are interested in
		ArrayList<DendrogramNode> clusterList = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> nodesList = new ArrayList<DendrogramNode>();
		DendrogramNode root = m_dendrogram.getRoot();
		nodesList.add(root);
		for(int i=0; i<nodesList.size(); i++){
			DendrogramNode node = nodesList.get(i);
			String nodeClassStr = node.getClass().toString();
			if(nodeClassStr.contains("MergeNode")){
				MergeNode mnode = (MergeNode)node;
				double dissim = mnode.getDissimilarity();
				if(dissim>dissimilarityThreshold){
					nodesList.add(mnode.getLeft());
					nodesList.add(mnode.getRight());
				} else {
					clusterList.add(mnode);
				}
			}
			if(nodeClassStr.contains("ObservationNode")){
				ObservationNode onode = (ObservationNode)node;
				clusterList.add(onode);
			}
		}
		
		// return the clustering
		int[] clustersA = getClustersFromNodes(clusterList);
		return clustersA;
	}

}
