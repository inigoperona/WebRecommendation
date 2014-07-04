package ehupatras.webrecommendation.evaluator;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.sequencealignment.multiplealignment.MultipleSequenceAlignment;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import ehupatras.weightedsequence.WeightedSequence;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ModelEvaluatorSeqMinMSAWseq
				extends ModelEvaluatorSeqMin {

	// ATTRIBUTES

	private ArrayList<int[]> m_clustersAL;
	private ArrayList<ArrayList<String[][]>> m_msaAL;
	private String m_clustFile = null;
	private String m_msaFileTxt = null;
	private String m_msaFileJavaData = null;
	
	// CREATOR
	
	public ModelEvaluatorSeqMinMSAWseq(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL,
			String clustFile,
			String msaFileTxt, String msaFileJavaData,
			float minsupport,
			String wseqFileTxt,	String wseqFileJavaData){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL);
		m_clustFile = clustFile;
		m_msaFileTxt = msaFileTxt;
		m_msaFileJavaData = msaFileJavaData;
		m_minsupport = minsupport;
		m_minSeqsFileTxt = wseqFileTxt;
		m_minSeqsFileJavaData = wseqFileJavaData;
	}
	
	// BUILD MODEL
	
	public void buildModel(){
		// read clusters
		ModelEvaluatorClust modelClust = new ModelEvaluatorClust();
		modelClust.loadClusters(m_clustFile);
		m_clustersAL = modelClust.getClusters();
		
		// MSA
		this.clustersSequenceAlignment();
		this.writeAlignments(m_msaFileTxt);
		this.saveAlignments(m_msaFileJavaData);
		
		// Wseq
		this.extractWeightedSequences();
		super.writeWeightedSequences(m_minSeqsFileTxt);
		super.saveMinedSeqs(m_minSeqsFileJavaData);
		
		// ST
		super.buidSuffixTrees(m_minedSeqs);
	}
	
	
	// MULTIPLE SEQUENCE ALIGNMENT //
	
	private void clustersSequenceAlignment(){
		// Multiple Sequence Alignment for each fold
		m_msaAL = new ArrayList<ArrayList<String[][]>>();
		for(int i=0; i<m_nFolds; i++){
			m_msaAL.add(this.msa(i));
		}
	}
	
	private ArrayList<String[][]> msa(int indexFold){
		// Web Access Sequences (WAS)
		ArrayList<Long> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<Long> trainsetnames2 = 
				m_distancematrix.getSessionIDs(trainsetnames, m_datasetSplit!=null);
		
		// Cluster-IDs for each WAS
		int[] clusters = m_clustersAL.get(indexFold);
		int[] clustersCopy = clusters.clone();
		Arrays.sort(clustersCopy);
		int maxcluster = clustersCopy[clusters.length-1];
		
		// Multiple Sequence Alignment
		ArrayList<String[][]> multAlignsList = new ArrayList<String[][]>(); 
		MultipleSequenceAlignment malign = new MultipleSequenceAlignment();
		for(int cli=0; cli<=maxcluster; cli++){
			ArrayList<Long> names = new ArrayList<Long>();
			for(int i=0; i<clusters.length; i++){
				if(cli==clusters[i]){
					names.add(trainsetnames2.get(i));
				}
			}
			int[] clusteri = m_distancematrix.getSessionIDsIndexes2(names, m_datasetSplit!=null);
			malign.msa(clusteri, 
					m_distancematrix.getMatrix(m_datasetSplit!=null), 
					this.getDataSet(m_datasetSplit!=null));
			String[][] multAlign = malign.getMultipleSequenceAlignment();
			multAlignsList.add(multAlign);
		}
		return multAlignsList;
	}
	
	private void writeAlignments(String outfilename){
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
		ArrayList<Long> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<Long> trainsetnames2 = 
				m_distancematrix.getSessionIDs(trainsetnames, m_datasetSplit!=null);
		
		String[] strA = new String[clustersID.length];
		for(int cli=0; cli<mAligns.size(); cli++){
			int index = 0;
			String[][] clusterAlign = mAligns.get(cli);
			for(int j=0; j<clustersID.length; j++){
				if(clustersID[j]==cli){
					String str = "fold:" + indexFold + ":" +
							trainsetnames2.get(j) + ":" + 
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
	
	private void saveAlignments(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		so.save(m_msaAL, outfilename);
	}
	
	private void loadAlignments(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		m_msaAL = (ArrayList<ArrayList<String[][]>>)so.load(outfilename);
	}
	
	
	
	
	// WEIGHTED SEQUENCES //
	
	private void extractWeightedSequences(){
		// Extract Weighted Sequences for each fold
		m_minedSeqs = new ArrayList<ArrayList<String[]>>();
		for(int i=0; i<m_nFolds; i++){
			m_minedSeqs.add(this.extractWeightedSequencesFold(i));
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
	
}
