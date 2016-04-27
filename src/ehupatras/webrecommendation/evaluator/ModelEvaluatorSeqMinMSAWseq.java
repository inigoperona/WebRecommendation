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

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorSeqMinMSAWseq.
 */
public class ModelEvaluatorSeqMinMSAWseq
				extends ModelEvaluatorSeqMin {

	// ATTRIBUTES

	/** The m_msa al. */
	private ArrayList<ArrayList<String[][]>> m_msaAL;
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator seq min msa wseq.
	 *
	 * @param dataset the dataset
	 * @param datasetSplit the dataset split
	 * @param dm the dm
	 * @param trainAL the train al
	 * @param valAL the val al
	 * @param testAL the test al
	 * @param modePrRe the mode pr re
	 * @param usage2contentFile the usage2content file
	 * @param resSimilarityFile the res similarity file
	 */
	public ModelEvaluatorSeqMinMSAWseq(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<String>> trainAL,
			ArrayList<ArrayList<String>> valAL,
			ArrayList<ArrayList<String>> testAL,
			int modePrRe,
			String usage2contentFile,
			String resSimilarityFile){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe, usage2contentFile, resSimilarityFile);
	}
	
	// BUILD MODEL
	
	/**
	 * Msa.
	 *
	 * @param msaFileTxt the msa file txt
	 * @param msaFileJavaData the msa file java data
	 */
	public void msa(String msaFileTxt, String msaFileJavaData){
		this.clustersSequenceAlignment();
		this.writeAlignments(msaFileTxt);
		this.saveAlignments(msaFileJavaData);
	}
	
	/**
	 * Wseq.
	 *
	 * @param minsupport the minsupport
	 * @param minSeqsFileTxt the min seqs file txt
	 * @param minSeqsFileJavaData the min seqs file java data
	 */
	public void wseq(float minsupport, String minSeqsFileTxt, String minSeqsFileJavaData){
		m_minsupport = minsupport;
		this.extractWeightedSequences();
		super.writeWeightedSequences(minSeqsFileTxt);
		super.saveMinedSeqs(minSeqsFileJavaData);
	}
	
	
	// MULTIPLE SEQUENCE ALIGNMENT //
	
	/**
	 * Clusters sequence alignment.
	 */
	private void clustersSequenceAlignment(){
		// Multiple Sequence Alignment for each fold
		m_msaAL = new ArrayList<ArrayList<String[][]>>();
		for(int i=0; i<m_nFolds; i++){
			m_msaAL.add(this.msa(i));
		}
	}
	
	/**
	 * Msa.
	 *
	 * @param indexFold the index fold
	 * @return the array list
	 */
	private ArrayList<String[][]> msa(int indexFold){
		// Web Access Sequences (WAS)
		ArrayList<String> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<String> trainsetnames2 = 
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
			ArrayList<String> names = new ArrayList<String>();
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
	
	/**
	 * Write alignments.
	 *
	 * @param outfilename the outfilename
	 */
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
	
	/**
	 * Alignment to string.
	 *
	 * @param indexFold the index fold
	 * @return the string[]
	 */
	private String[] alignmentToString(int indexFold){
		ArrayList<String[][]> mAligns = m_msaAL.get(indexFold);
		int[] clustersID = m_clustersAL.get(indexFold);
		ArrayList<String> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<String> trainsetnames2 = 
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
	
	/**
	 * Save alignments.
	 *
	 * @param outfilename the outfilename
	 */
	private void saveAlignments(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		so.save(m_msaAL, outfilename);
	}
	
	/**
	 * Load alignments.
	 *
	 * @param outfilename the outfilename
	 */
	private void loadAlignments(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		m_msaAL = (ArrayList<ArrayList<String[][]>>)so.load(outfilename);
	}
	
	
	
	
	// WEIGHTED SEQUENCES //
	
	/**
	 * Extract weighted sequences.
	 */
	private void extractWeightedSequences(){
		// Extract Weighted Sequences for each fold
		m_minedSeqs = new ArrayList<ArrayList<String[]>>();
		for(int i=0; i<m_nFolds; i++){
			m_minedSeqs.add(this.extractWeightedSequencesFold(i));
		}
	}
	
	/**
	 * Extract weighted sequences fold.
	 *
	 * @param indexFold the index fold
	 * @return the array list
	 */
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
