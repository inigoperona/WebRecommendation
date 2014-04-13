package ehupatras.webrecommendation.evaluator;

import ehupatras.clustering.ClusteringHierarchical;
import ehupatras.clustering.ClusteringPAM;
import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;
import ehupatras.webrecommendation.sequencealignment.multiplealignment.MultipleSequenceAlignment;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.weightedsequence.WeightedSequence;
import ehupatras.markovmodel.MarkovChain;
import ehupatras.clustering.cvi.CVI;
import ehupatras.sequentialpatternmining.MySPADE;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.ArrayList;

public class ModelEvaluator {

	// Number of folds
	private int m_nFolds;
	
	// database
	private ArrayList<String[]> m_dataset;
	private ArrayList<String[]> m_dataset2;
	private Matrix m_distancematrix;
	
	// To create the model: trainset
	private ArrayList<ArrayList<Integer>> m_trainAL;
	
	// Hierarchical Clustering
	private String m_AgglomerativeMethodClassName = "ehupatras.clustering.sapehac.agglomeration.WardLinkage";
	private float m_pCutDendrogramDiss = 50f;
	private ArrayList<int[]> m_clustersAL;
	
	// PAM: Partitioning Around Medoids
	private int m_k;
	
	// Multiple Sequence Alignment
	private ArrayList<ArrayList<String[][]>> m_msaAL;
	
	// Weighted Sequences
	private float m_minsupport = (float)0.25;
	private ArrayList<ArrayList<String[]>> m_weightedSequences;
	
	// Generalized Suffix tree
	private ArrayList<SuffixTreeStringArray> m_suffixtreeAL = null;
	
	// Modular approach Cluster-ST version
	private ArrayList<ArrayList<SuffixTreeStringArray>> m_clustSuffixTreeAL = null;
	
	// Markov Chain
	private ArrayList<MarkovChain> m_markovChainAL = null;
	
	// Medoids of clusters with each recommendations
	private ArrayList<ArrayList<String[]>> m_medoidsAL = null;
	private ArrayList<int[]> m_gmedoidsAL = null;
	private ArrayList<ArrayList<Object[]>> m_recosAL = null;
	private int m_knn = 100;
	
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
	
	public void setDataSet2(ArrayList<String[]> dataset){
		m_dataset2 = dataset;
	}
	
	public void resetModels(){
		m_suffixtreeAL = null;
		m_markovChainAL = null;
	}
	
	
	
	// HIERARCHICAL CLUSTERING //
	
	public void buildClustersH(float pCutDendrogramDiss, String agglemerativeMethodString){
		m_pCutDendrogramDiss = pCutDendrogramDiss;
		m_AgglomerativeMethodClassName = agglemerativeMethodString;
		this.buildClustersH();
	}
	
	public void buildClustersH(){
		// Clustering for each fold
		m_clustersAL = new ArrayList<int[]>();
		for(int i=0; i<m_nFolds; i++){
			m_clustersAL.add(this.clusteringH(i));
		}
	}
	
	public void setCutDendrogramDissimilarityThreshold(float pCutDendrogramDiss){
		m_pCutDendrogramDiss = pCutDendrogramDiss;
	}
	
	private int[] clusteringH(int indexFold){
		ArrayList<Integer> trainnames = m_trainAL.get(indexFold);
		// hierarchical clustering: http://sape.inf.usi.ch/hac
		ClusteringHierarchical clustering = new ClusteringHierarchical();
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames);
		float[][] distmatrix = m_distancematrix.getMatrix();
		clustering.computeHierarchicalClustering(distmatrix,trainDMindexes,m_AgglomerativeMethodClassName);
		// cut dendrogram
		int[] clustersA = clustering.cutDendrogramByDissimilarity(m_pCutDendrogramDiss);
		return clustersA;
	}
	
	
	// PAM CLUSTERING //
	
	public void buildClustersPAM(int k){
		m_k = k;
		
		// Clustering for each fold
		m_clustersAL = new ArrayList<int[]>();
		for(int i=0; i<m_nFolds; i++){
			m_clustersAL.add(this.clusteringPAM(i));
		}
	}
	
	private int[] clusteringPAM(int indexFold){
		ArrayList<Integer> trainnames = m_trainAL.get(indexFold);
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames);
		float[][] distmatrix = m_distancematrix.getMatrix();
		
		ClusteringPAM pam = new ClusteringPAM(m_k, distmatrix, trainDMindexes);
		pam.runPAM();
		
		return pam.getMedoidAssignment();
	}
	
	
	// Clustering UTILS //
	
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
		// Build Suffix Trees for each fold
		m_suffixtreeAL = new ArrayList<SuffixTreeStringArray>();
		for(int i=0; i<m_nFolds; i++){
			m_suffixtreeAL.add(this.createSuffixTreeNoWeights(i));
		}
	}

	public void buildSuffixTreesFromOriginalSequences(){
		// Build Suffix Trees for each fold
		m_suffixtreeAL = new ArrayList<SuffixTreeStringArray>();
		for(int i=0; i<m_nFolds; i++){
			m_suffixtreeAL.add(this.createSuffixTreeFromOriginalSequences(i));
		}
	}
	
	public SuffixTreeStringArray createSuffixTreeFromOriginalSequences(int indexFold){		
		ArrayList<Integer> trainnames = m_trainAL.get(indexFold);
		int[] trainDMindexes = m_distancematrix.getSessionIDsIndexes(trainnames);
		ArrayList<String[]> sequences = new ArrayList<String[]>(); 
		for(int i=0; i<trainDMindexes.length; i++){
			sequences.add(m_dataset.get(i));
		}
		return this.createSuffixTree(sequences);
	}
	
	private SuffixTreeStringArray createSuffixTree(ArrayList<String[]> sequences){
		SuffixTreeStringArray suffixtree = new SuffixTreeStringArray();
		for(int i=0; i<sequences.size(); i++){
			suffixtree.putSequence(sequences.get(i), i);
		}
		return suffixtree;
	}
	
	private SuffixTreeStringArray createSuffixTreeNoWeights(int indexFold){
		ArrayList<String[]> sequences = m_weightedSequences.get(indexFold);
		SuffixTreeStringArray suffixtree = this.createSuffixTree(sequences);
		return suffixtree;
	}
	
	private SuffixTreeStringArray createSuffixTreePlusWeights(int indexFold){
		// create the suffix tree without weight
		ArrayList<String[]> sequences = m_weightedSequences.get(indexFold);
		SuffixTreeStringArray suffixtree = this.createSuffixTree(sequences);
		
		// take original train sequences to weight the nodes
		ArrayList<Integer> sessionIDs = m_trainAL.get(indexFold); 
		int[] inds = m_distancematrix.getSessionIDsIndexes(sessionIDs);
		ArrayList<String[]> trainseqs = new ArrayList<String[]>();
		for(int j=0; j<inds.length; j++){ trainseqs.add(m_dataset.get(inds[j])); }

		// Weight the suffix tree by training sequences
		suffixtree.weightTheSuffixTree(trainseqs);
		return suffixtree;
	}
	
	
	
	// Modular Approach: Cluster-SuffixTree //
	
	public void buildClustersSuffixTrees(){
		// Build Cluster-SuffixTrees for each fold
		m_clustSuffixTreeAL = new ArrayList<ArrayList<SuffixTreeStringArray>>();
		for(int i=0; i<m_nFolds; i++){
			m_clustSuffixTreeAL.add(this.createClustersSuffixTrees(i));
		}
	}
	
	public ArrayList<SuffixTreeStringArray> createClustersSuffixTrees(int indexFold){
		// train sessions names
		ArrayList<Integer> trainsetnames = m_trainAL.get(indexFold);
		
		// assignment to each case
		int[] clindexes = m_clustersAL.get(indexFold);
		// maximum cluster index
		int climax = Integer.MIN_VALUE;
		for(int i=0; i<clindexes.length; i++){
			int cli = clindexes[i];
			if(cli>climax){
				climax = cli;
			}
		}
		
		// for each cluster
		ArrayList<SuffixTreeStringArray> stAL = new ArrayList<SuffixTreeStringArray>(); 
		for(int cli=0; cli<=climax; cli++){
			// take the sessions we are interested in
			ArrayList<Integer> names = new ArrayList<Integer>();
			for(int i=0; i<clindexes.length; i++){
				if(cli==clindexes[i]){
					names.add(trainsetnames.get(i));
				}
			}
			
			// take the sequences
			int[] clusterDMind = m_distancematrix.getSessionIDsIndexes(names);
			ArrayList<String[]> sequences = new ArrayList<String[]>(); 
			for(int i=0; i<clusterDMind.length; i++){
				sequences.add(m_dataset.get(i));
			}
			
			// create the Suffix Tree
			SuffixTreeStringArray st = this.createSuffixTree(sequences);
			stAL.add(st);
		}
		
		return stAL;
	}
	
	
	// MARKOV CHAIN //
	
	public void buildMarkovChains(){
		// compute markov chain for each fold
		m_markovChainAL = new ArrayList<MarkovChain>();
		for(int i=0; i<m_nFolds; i++){
			m_markovChainAL.add(this.getMarkovChain(i));
		}
	}
	
	public MarkovChain getMarkovChain(int indexFold){
		// get the train sequences from sessionIDs
		ArrayList<Integer> sessionIDs = m_trainAL.get(indexFold); 
		int[] inds = m_distancematrix.getSessionIDsIndexes(sessionIDs);
		ArrayList<String[]> trainseqs = new ArrayList<String[]>();
		for(int j=0; j<inds.length; j++){ trainseqs.add(m_dataset.get(inds[j])); }
		MarkovChain mchain = new MarkovChain(trainseqs);
		return mchain;
	}
	
	
	
	// MEDOIDS and its recommendations //
	
	public void buildMedoidsModels(float minsup){
		// compute medoids for each fold
		m_medoidsAL = new ArrayList<ArrayList<String[]>>();
		m_gmedoidsAL = new ArrayList<int[]>();
		m_recosAL = new ArrayList<ArrayList<Object[]>>();
		for(int i=0; i<m_nFolds; i++){
			Object[] medObjA = this.getMedoids(i);
			ArrayList<String[]> medoids = (ArrayList<String[]>)medObjA[0];
			int[] gmedoids = (int[])medObjA[1];
			m_medoidsAL.add(medoids);
			m_gmedoidsAL.add(gmedoids);
			m_recosAL.add(this.getRecommendations(i, minsup));
		}
	}
	
	private Object[] getMedoids(int indexFold){
		// train cases indexes
		ArrayList<Integer> trSesIDs = m_trainAL.get(indexFold);
		int[] inds = m_distancematrix.getSessionIDsIndexes(trSesIDs);
		
		// cluster indexes
		int[] clusters = m_clustersAL.get(indexFold);
		
		// get medoids & global medoids
		CVI cvindex = new CVI(inds,clusters);
		cvindex.computeMedoids(m_distancematrix.getMatrix());
		// treat medoids
		int[] medoids = cvindex.getMedoids();
		ArrayList<String[]> medoidSeqs = new ArrayList<String[]>();
		for(int i=0; i<medoids.length; i++){
			String[] medSeq = m_dataset2.get(medoids[i]);
			medoidSeqs.add(medSeq);
		}
		// treat global medoids
		int[] gmedoids = cvindex.getGlobalMedoids();
		
		// Return medoids
		Object[] objA = new Object[2];
		objA[0] = medoidSeqs;
		objA[1] = gmedoids;
		return objA;
	}
	
	private ArrayList<Object[]> getRecommendations(int indexFold, float minsup){
		// train cases indexes
		ArrayList<Integer> trSesIDs = m_trainAL.get(indexFold);
		int[] inds = m_distancematrix.getSessionIDsIndexes(trSesIDs);
		
		// cluster indexes
		int[] clusters = m_clustersAL.get(indexFold);
		// find the maximum index
		int max = Integer.MIN_VALUE;
		for(int i=0; i<clusters.length; i++){
			int clInd = clusters[i];
			if(max<clInd){
				max = clInd;
			}
		}
		
		// for each cluster extract the most common URLs
		ArrayList<Object[]> recos = new ArrayList<Object[]>();
		for(int i=0; i<=max; i++){
			ArrayList<String[]> trainseqs = new ArrayList<String[]>();
			// get train sequences
			for(int j=0; j<clusters.length; j++){
				if(i==clusters[j]){
					trainseqs.add(m_dataset.get(inds[j]));
				}
			}
			// Frequent patter mining
			MySPADE sp = new MySPADE(trainseqs, minsup);
			Object[] objA = sp.getFrequentSequencesLength1();
			//ArrayList<String> freqseqs1 = (ArrayList<String>)objA[0];
			//ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
			recos.add(objA);
		}
		
		return recos;
	}
	
	public void setKnn(int knn){
		m_knn = knn;
	}
	
	
	
	
	
	// MODEL EVALUATION //
	
	public String computeEvaluationTest(int mode, int nrecos, long seed,
			int failuremode, int maxMemory,
			boolean isDistance, float[][] rolesW){				
		// metrics
		int trnURLs = 0;
		int trnSeqs = 0;
		int tsnURLs = 0;
		int tsnSeqs = 0;
		int nNodes = 0;
		float nEdges = 0;
		float nrec = 0f;
		int nFailuresI = 0;
		float[] failuresHist = new float[12];
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
			// get the test sequences from sessionIDs
			ArrayList<Integer> sessionIDs = m_testAL.get(i); 
			int[] inds = m_distancematrix.getSessionIDsIndexes(sessionIDs);
			ArrayList<String[]> testseqs = new ArrayList<String[]>();
			for(int j=0; j<inds.length; j++){ testseqs.add(m_dataset.get(inds[j])); }
			
			// SELECT THE MODEL
			TestSetEvaluator eval = null;
			SuffixTreeStringArray suffixtree = null;
			if(m_suffixtreeAL!=null){ // Suffix Tree
				suffixtree = m_suffixtreeAL.get(i);
				eval = new TestSetEvaluator(testseqs, suffixtree);
			} else if(m_medoidsAL!=null && m_clustSuffixTreeAL!=null){
				eval = new TestSetEvaluator(testseqs,
						m_medoidsAL.get(i),
						m_gmedoidsAL.get(i),
						m_knn,
						isDistance, rolesW,
						m_clustSuffixTreeAL.get(i));
			} else if(m_medoidsAL!=null) { // Medoids
				eval = new TestSetEvaluator(testseqs,
								m_medoidsAL.get(i),
								m_gmedoidsAL.get(i),
								m_recosAL.get(i),
								isDistance, rolesW);
			} else if(m_clustSuffixTreeAL!=null){
				eval = new TestSetEvaluator(testseqs, m_clustSuffixTreeAL.get(i));
			} else { // Markov Chain
				MarkovChain markovchain = m_markovChainAL.get(i);
				eval = new TestSetEvaluator(testseqs, markovchain);
			}
			
			// carry out the evaluation
			eval.setConfusionPoints(m_confusionPoints);
			eval.setFmeasureBeta(m_fmeasurebeta);
			eval.computeEvaluation(mode, nrecos, seed, 
					m_markovChainAL.get(i),
					failuremode,
					maxMemory);
			//eval.writeResults();
			
			// METRICS1
			// get the train sequences from sessionIDs
			ArrayList<Integer> trsessionIDs = m_trainAL.get(i); 
			int[] trinds = m_distancematrix.getSessionIDsIndexes(trsessionIDs);
			ArrayList<String[]> trainseqs = new ArrayList<String[]>();
			for(int j=0; j<trinds.length; j++){ trainseqs.add(m_dataset.get(trinds[j])); }
			for(int j=0; j<trainseqs.size(); j++){ trnURLs = trnURLs + trainseqs.get(j).length; }
			trnSeqs = trnSeqs + trainseqs.size();
			
			// METRICS2
			tsnURLs = tsnURLs + eval.getNumberOfClicks();
			tsnSeqs = tsnSeqs + eval.getNumberOfSequences();
			
			// METRICS3
			if(m_suffixtreeAL!=null){
				nNodes = nNodes + suffixtree.getNumberOfNodes();
				nEdges = nEdges + suffixtree.getNumberOfEdges();
			}
			
			// METRICS4
			nrec = nrec + eval.getNumberOfRecommendationsRatio();
			nFailuresI = nFailuresI + eval.getNumberOfFailures();
			float[] failuresHist2 = eval.getFailuresHistogram();
			for(int j=0; j<failuresHist2.length; j++){
				failuresHist[j] = failuresHist[j] + failuresHist2[j];
			}
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
		float trnURLsf = (float)trnURLs / (float)m_nFolds;
		float trnSeqsf = (float)trnSeqs / (float)m_nFolds;
		float tsnURLsf = (float)tsnURLs / (float)m_nFolds;
		float tsnSeqsf = (float)tsnSeqs / (float)m_nFolds;
		float nNodesf = (float)nNodes / (float)m_nFolds;
		float nEdgesf = (float)nEdges / (float)m_nFolds;
		nrec = nrec / (float)m_nFolds;
		float nFailures = (float)((double)nFailuresI / (double)m_nFolds);
		for(int j=0; j<failuresHist.length; j++){
			failuresHist[j] = failuresHist[j] / (float)m_nFolds; 
		}
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
		String results = String.valueOf(trnURLsf);
		results = results + "," + trnSeqsf;
		results = results + "," + tsnURLsf;
		results = results + "," + tsnSeqsf;
		results = results + "," + nNodesf;
		results = results + "," + nEdgesf;
		results = results + "," + nrec;
		results = results + "," + nFailures;
		for(int j=0; j<failuresHist.length; j++){results = results + "," + failuresHist[j];}
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
		String header = "trNClicks";
		header = header + ",trNSeqs";
		header = header + ",tsNClicks";
		header = header + ",tsNSeqs";
		header = header + ",nNodes";
		header = header + ",nEdges";
		header = header + ",nRecos";
		header = header + ",nFailures";
		for(int j=0; j<11; j++){header = header + ",f=" + j;}
		header = header + ",f>10";
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
