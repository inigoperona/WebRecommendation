package ehupatras.webrecommendation.evaluator;

import ehupatras.clustering.ClusteringHierarchical;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import ehupatras.webrecommendation.sequencealignment.multiplealignment.MultipleSequenceAlignment;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.distmatrix.Matrix;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;

public class ModelEvaluator {

	// Number of folds
	private int m_nFolds;
	
	// To create the model
	private ArrayList<String[]> m_dataset;
	private Matrix m_distancematrix;
	private ArrayList<ArrayList<Integer>> m_trainAL;
	
	// Clustering
	private int m_pCutDendrogramDiss = 50;
	private ArrayList<int[]> m_clustersAL;
	
	// Multiple Sequence Alignment
	private ArrayList<ArrayList<String[][]>> m_msaAL;
	
	// Weighted Sequences
	
	// Generalized Suffix tree
	private ArrayList<SuffixTreeStringArray> m_suffixtreeAL;
	
	// to evaluate the model
	private ArrayList<ArrayList<Integer>> m_valAL;
	private ArrayList<ArrayList<Integer>> m_testAL;
	
	// metrics
	private int[] m_numberOfNodes;
	private float[] m_numberOfEdges;
	private float[] m_numberOfRecommendationsRatio;
	private float[] m_hitratio;
	private float[] m_clicksoonratio;
	
	public ModelEvaluator(
					ArrayList<String[]> dataset,
					Matrix dm,
					ArrayList<ArrayList<Integer>> trainAL,
					ArrayList<ArrayList<Integer>> valAL,
					ArrayList<ArrayList<Integer>> testAL){
		m_dataset = dataset;
		m_distancematrix = dm;
		m_trainAL = trainAL;
		m_valAL = valAL;
		m_testAL = testAL;
		m_nFolds = m_trainAL.size();
	}
	
	protected void setDataSet(ArrayList<String[]> dataset){
		m_dataset = dataset;
	}
	
	public void createModels(){
		this.buildClusters();
		this.clustersSequenceAlignment();
	}
	
	
	// CLUSTERING //
	
	public void buildClusters(int pCutDendrogramDiss){
		m_pCutDendrogramDiss = pCutDendrogramDiss;
		this.buildClusters();
	}
	
	public void buildClusters(){
		// Clustering for each fold
		m_clustersAL = new ArrayList<int[]>();
		for(int i=0; i<m_nFolds; i++){
			m_clustersAL.add(this.clustering(i));
		}
	}
	
	public void setCutDendrogramDissimilarityThreshold(int pCutDendrogramDiss){
		m_pCutDendrogramDiss = pCutDendrogramDiss;
	}
	
	private int[] clustering(int indexFold){
		ArrayList<Integer> trainnames = m_trainAL.get(indexFold);
		// hierarchical clustering: http://sape.inf.usi.ch/hac
		ClusteringHierarchical clustering = new ClusteringHierarchical();
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames);
		float[][] distmatrix = m_distancematrix.getMatrix();
		clustering.computeHierarchicalClustering(distmatrix,trainDMindexes);
		// cut dendrogram
		int[] clustersA = clustering.cutDendrogramByDissimilarity(m_pCutDendrogramDiss);
		return clustersA;
	}
	
	public void writeClusters(String outfilename){
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Write the sequences in a file line by line
		try{
			for(int i=0; i<m_nFolds; i++){
				String[] clusteringStrA = this.clusteringToString(i);
				for(int j=0; j<clusteringStrA.length; j++){
					String line = clusteringStrA[j];
					writer.write(String.valueOf(line));
				}
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeFilteredLog] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}

	private String[] clusteringToString(int indexFold){
		int[] clustersID = m_clustersAL.get(indexFold);
		ArrayList<Integer> trainsetnames = m_trainAL.get(indexFold);
		String[] strA = new String[clustersID.length];
		for(int i=0; i<clustersID.length; i++){
			strA[i] = "fold:" + indexFold + ":" + 
			trainsetnames.get(i) + ":" + clustersID[i] + "\n";
		}
		return strA;
	}
	
	public void saveClusters(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		so.save(m_clustersAL, outfilename);
	}
	
	public void loadClusters(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		m_clustersAL = (ArrayList<int[]>)so.load(outfilename);
	}
	
	
	
	// MULTIPLE SEQUENCE ALIGNMENT //
	
	public void clustersSequenceAlignment(){
		// Multiple Sequence Alignment for each fold
		m_msaAL = new ArrayList<ArrayList<String[][]>>();
		for(int i=0; i<m_nFolds; i++){
			m_msaAL.add(this.msa(i));
		}
	}
	
	private ArrayList<String[][]> msa(int indexFold){
		// Web Access Sequences (WAS)
		ArrayList<Integer> trainsetnames = m_trainAL.get(indexFold);
		
		// Cluster-IDs for each WAS
		int[] clusters = m_clustersAL.get(indexFold);
		int[] clustersCopy = clusters.clone();
		Arrays.sort(clustersCopy);
		int maxcluster = clustersCopy[clusters.length-1];
		
		// Multiple Sequence Alignment
		ArrayList<String[][]> multAlignsList = new ArrayList<String[][]>(); 
		MultipleSequenceAlignment malign = new MultipleSequenceAlignment();
		for(int cli=0; cli<=maxcluster; cli++){
			ArrayList<Integer> names = new ArrayList<Integer>();
			for(int i=0; i<clusters.length; i++){
				if(cli==clusters[i]){
					names.add(trainsetnames.get(i));
				}
			}
			int[] clusteri = m_distancematrix.getSessionIDsIndexes(names);
			malign.msa(clusteri, m_distancematrix.getMatrix(), m_dataset);
			String[][] multAlign = malign.getMultipleSequenceAlignment();
			multAlignsList.add(multAlign);
		}
		return multAlignsList;
	}
	
	public void writeAlignments(String outfilename){
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Write the sequences in a file line by line
		try{
			for(int i=0; i<m_nFolds; i++){
				String[] strA = this.alignmentToString(i);
				for(int j=0; j<strA.length; j++){
					String line = strA[j];
					writer.write(String.valueOf(line));
				}
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeFilteredLog] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	private String[] alignmentToString(int indexFold){
		ArrayList<String[][]> mAligns = m_msaAL.get(indexFold);
		int[] clustersID = m_clustersAL.get(indexFold);
		ArrayList<Integer> trainsetnames = m_trainAL.get(indexFold);
		String[] strA = new String[clustersID.length];
		for(int cli=0; cli<mAligns.size(); cli++){
			int index = 0;
			String[][] clusterAlign = mAligns.get(cli);
			for(int j=0; j<clustersID.length; j++){
				if(clustersID[j]==cli){
					String str = "fold:" + indexFold + ":" +
							trainsetnames.get(j) + ":" + 
							clustersID[j];
					String align = clusterAlign[index][0];
					for(int k=1; k<clusterAlign[index].length-1; k++){
						align = align + "," + clusterAlign[index][k];
					}
					strA[j] = str + ":" + align + "\n";
					index++;
				}
			}
		}
		return strA;
	}
	
}
