package ehupatras.webrecommendation.evaluator;

import ehupatras.clustering.ClusteringHierarchical;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import ehupatras.webrecommendation.sequencealignment.multiplealignment.MultipleSequenceAlignment;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.weightedsequence.WeightedSequence;

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
	private String m_AgglemerativeMethodClassName = "ehupatras.clustering.sapehac.agglomeration.WardLinkage";
	private int m_pCutDendrogramDiss = 50;
	private ArrayList<int[]> m_clustersAL;
	
	// Multiple Sequence Alignment
	private ArrayList<ArrayList<String[][]>> m_msaAL;
	
	// Weighted Sequences
	private float m_minsupport = (float)0.25;
	private ArrayList<ArrayList<String[]>> m_weightedSequences;
	
	// Generalized Suffix tree
	private ArrayList<SuffixTreeStringArray> m_suffixtreeAL;
	
	// to evaluate the model
	private ArrayList<ArrayList<Integer>> m_valAL;
	private ArrayList<ArrayList<Integer>> m_testAL;
	
	// metrics
	private float[] m_confusionPoints = 
		{0.00f,0.10f,0.25f,
		 0.50f,0.75f,0.90f,1.00f};
	private float m_fmeasurebeta = (float)0.5;
	
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
	
	public void buildClusters(int pCutDendrogramDiss, String agglemerativeMethodString){
		m_pCutDendrogramDiss = pCutDendrogramDiss;
		m_AgglemerativeMethodClassName = agglemerativeMethodString;
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
		clustering.computeHierarchicalClustering(distmatrix,trainDMindexes,m_AgglemerativeMethodClassName);
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
	
	public void saveAlignments(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		so.save(m_msaAL, outfilename);
	}
	
	public void loadAlignments(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		m_msaAL = (ArrayList<ArrayList<String[][]>>)so.load(outfilename);
	}
	
	
	
	// WEIGHTED SEQUENCES //
	
	public void extractWeightedSequences(float minsupport){
		m_minsupport = minsupport;
		
		// Extract Weighted Sequences for each fold
		m_weightedSequences = new ArrayList<ArrayList<String[]>>();
		for(int i=0; i<m_nFolds; i++){
			m_weightedSequences.add(this.extractWeightedSequencesFold(i));
		}
	}
	
	private ArrayList<String[]> extractWeightedSequencesFold(int indexFold){
		ArrayList<String[]> seqList = new ArrayList<String[]>();
		ArrayList<String[][]> alignseqs = m_msaAL.get(indexFold);
		for(int i=0; i<alignseqs.size(); i++){ // for each cluster
			String[][] alignseqsi = alignseqs.get(i);
			WeightedSequence ws = new WeightedSequence(alignseqsi, m_minsupport);
			try{
				ws.process();
			} catch (Exception ex){
				System.err.append("Exception in extracting Weighted Sequences. " +
						"[ehupatras.webrecommendation.evaluator.ModelEvaluator.extractWeightedSequencesFold]");
				System.err.println(ex.getMessage());
				System.exit(1);
			}
			ArrayList<String[]> generatedStrings = ws.getGeneratedSequences();
			
			// insert generated sequences in all sequences list
			for(int j=0; j<generatedStrings.size(); j++){
				String[] seq = generatedStrings.get(j);
				seqList.add(seq);
			}
		}
		return seqList;
	}
	
	public void writeWeightedSequences(String outfilename){
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
				ArrayList<String[]> wsFold = m_weightedSequences.get(i); 
				for(int j=0; j<wsFold.size(); j++){
					String[] seq = wsFold.get(j);
					String line = "fold:" + i + ":";
					line = line + seq[0];
					for(int k=1; k<seq.length; k++){
						line = line + "," + seq[k];
					}
					line = line + "\n";
					writer.write(line);
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


	
	// GENERALIZED SUFFIX TREE //
	
	public void buildSuffixTrees(){
		// Build Suffox Trees for each fold
		m_suffixtreeAL = new ArrayList<SuffixTreeStringArray>();
		for(int i=0; i<m_nFolds; i++){
			m_suffixtreeAL.add(this.createSuffixTree(i));
		}
	}

	private SuffixTreeStringArray createSuffixTree(int indexFold){
		SuffixTreeStringArray suffixtree = new SuffixTreeStringArray();
		ArrayList<String[]> sequences = m_weightedSequences.get(indexFold);
		for(int i=0; i<sequences.size(); i++){
			suffixtree.putSequence(sequences.get(i), i);
		}
		return suffixtree;
	}
	
	
	
	// MODEL EVALUATION
	
	public String computeEvaluationTest(int mode, int nrecos, long seed){				
		// metrics
		int nNodes = 0;
		float nEdges = 0;
		float nrec = 0f;
		float hitratio = 0f;
		float clicksoonration = 0f;
		float[] prA = new float[m_confusionPoints.length];
		float[] reA = new float[m_confusionPoints.length];
		float[] fmA = new float[m_confusionPoints.length];
		float[] moPrA = new float[m_confusionPoints.length];
		float[] moReA = new float[m_confusionPoints.length];
		float[] moFmA = new float[m_confusionPoints.length];
		
		// for each fold obtain the metrics
		for(int i=0; i<m_nFolds; i++){
			// get the suffix tree
			SuffixTreeStringArray suffixtree = m_suffixtreeAL.get(i);
			
			// get the test sequences from sessionIDs
			ArrayList<Integer> sessionIDs = m_testAL.get(i); 
			int[] inds = m_distancematrix.getSessionIDsIndexes(sessionIDs);
			ArrayList<String[]> testseqs = new ArrayList<String[]>();
			for(int j=0; j<inds.length; j++){ testseqs.add(m_dataset.get(inds[j])); }
			
			// carry out the evaluation
			TestSetEvaluator eval = new TestSetEvaluator(testseqs, suffixtree);
			eval.setConfusionPoints(m_confusionPoints);
			eval.setFmeasureBeta(m_fmeasurebeta);
			eval.computeEvaluation(mode, nrecos, seed);
			//eval.writeResults();
			
			nNodes = nNodes + suffixtree.getNumberOfNodes();
			nEdges = nEdges + suffixtree.getNumberOfEdges();
			nrec = nrec + eval.getNumberOfRecommendationsRatio();
			hitratio = hitratio + eval.getHitRatio();
			clicksoonration = clicksoonration + eval.getClickSoonRatio();
			float[] prA2 = eval.getPrecisions();
			float[] reA2 = eval.getRecalls();
			float[] fmA2 = eval.getFmeasures();
			float[] moPrA2 = eval.getModelPrecisions();
			float[] moReA2 = eval.getModelRecalls();
			float[] moFmA2 = eval.getModelFmeasures();
			for(int j=0; j<m_confusionPoints.length; j++){
				prA[j] = prA[j] + prA2[j];
				reA[j] = reA[j] + reA2[j];
				fmA[j] = fmA[j] + fmA2[j];
				moPrA[j] = moPrA[j] + moPrA2[j];
				moReA[j] = moReA[j] + moReA2[j];
				moFmA[j] = moFmA[j] + moFmA2[j];
			}
		}
		
		// compute the metric's average value of all folds
		float nNodesf = (float)nNodes / (float)m_nFolds;
		float nEdgesf = (float)nEdges / (float)m_nFolds;
		nrec = nrec / (float)m_nFolds;
		hitratio = hitratio / (float)m_nFolds;
		clicksoonration = clicksoonration / (float)m_nFolds;
		for(int j=0; j<m_confusionPoints.length; j++){
			prA[j] = prA[j] / (float)m_nFolds;
			reA[j] = reA[j] / (float)m_nFolds;
			fmA[j] = fmA[j] / (float)m_nFolds;
			moPrA[j] = moPrA[j] / (float)m_nFolds;
			moReA[j] = moReA[j] / (float)m_nFolds;
			moFmA[j] = moFmA[j] / (float)m_nFolds;
		}
		
		// write metric's values
		String results = String.valueOf(nNodesf);
		results = results + "," + nEdgesf;
		results = results + "," + nrec;
		results = results + "," + hitratio;
		results = results + "," + clicksoonration;
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + prA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + reA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + fmA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moPrA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moReA[j];}
		for(int j=0; j<m_confusionPoints.length; j++){results = results + "," + moFmA[j];}
		results = results + "\n";
		return results;
	}
	
	public String getEvaluationHeader(){
		String header = "nNodes";
		header = header + ",nEdges";
		header = header + ",nRecos";
		header = header + ",hitratio";
		header = header + ",clicksoonration";
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",pr_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",re_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",fm" + m_fmeasurebeta + "_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mPr_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mRe_" + m_confusionPoints[j];}
		for(int j=0; j<m_confusionPoints.length; j++){header = header + ",mFm" + m_fmeasurebeta + "_" + m_confusionPoints[j];}
		header = header + "\n";
		return header;
	}
	
	public void setConfusionPoints(float[] confusionPoints){
		m_confusionPoints = confusionPoints;
	}
	public void setFmeasureBeta(float fmeasurebeta){
		m_fmeasurebeta = fmeasurebeta;
	}
	
}