package FPlierni.webrecommendation;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import ehupatras.clustering.cvi.CVI;
import ehupatras.clustering.sapehac.dendrogram.Dendrogram;
import ehupatras.clustering.sapehac.dendrogram.DendrogramNode;
import ehupatras.clustering.sapehac.dendrogram.MergeNode;
import ehupatras.clustering.sapehac.dendrogram.ObservationNode;


public class COP {

	/** The m_dendrogram. */
	private Dendrogram m_dendrogram;
	
	/** The m_cop values. */
	private float[] m_cop;

	
	
	/**
	 * Computes the COP values for each node
	 *
	 * @return the COP values
	 */
	public float[] computeCOP(float[][] distancematrix){
		ArrayList<DendrogramNode> nodesList = new ArrayList<DendrogramNode>();
		ArrayList<DendrogramNode> clusterList = new ArrayList<DendrogramNode>();
		CVI copCVI = new CVI(int[] dataInds, int[] clusters);
		
		double cop=0.0;
		double intracop=0.0;
		double intercop=0.0;
		copCVI.computeMedoids(distancematrix);
		
		DendrogramNode root = m_dendrogram.getRoot();
		nodesList.add(root);
		for(int i=0; i<nodesList.size(); i++){
			DendrogramNode node = nodesList.get(i);
			String nodeClassStr = node.getClass().toString();
			//compute the COP value
			for (int ob=0; ob<node.getObservationCount(); ob++){
				intracop=
			}
			
			
			cop=1/node.getObservationCount();
			
			
			if(nodeClassStr.contains("MergeNode")){
				MergeNode mnode = (MergeNode)node;
				nodesList.add(mnode.getLeft());
				nodesList.add(mnode.getRight());
				mnode.setCOP(cop);
			}
			if(nodeClassStr.contains("ObservationNode")){
				ObservationNode onode = (ObservationNode)node;
				onode.setCOP(cop);
			}
		
		return m_cop;
		}

	

}
