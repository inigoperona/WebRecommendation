package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;

import ehupatras.sequentialpatternmining.MySPADE;
import ehupatras.webrecommendation.distmatrix.Matrix;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorSeqMinSPADE.
 */
public class ModelEvaluatorSeqMinSPADE
				extends ModelEvaluatorSeqMin {

	// CREATOR
	
	/**
	 * Instantiates a new model evaluator seq min spade.
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
	public ModelEvaluatorSeqMinSPADE(
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
	 * Spade.
	 *
	 * @param minsupport the minsupport
	 * @param workdir the workdir
	 * @param minSeqsFileTxt the min seqs file txt
	 * @param minSeqsFileJavaData the min seqs file java data
	 */
	public void spade(float minsupport, String workdir,
					String minSeqsFileTxt, String minSeqsFileJavaData){
		m_minsupport = minsupport;
		
		// compute spade sequences for each fold
		m_minedSeqs = new ArrayList<ArrayList<String[]>>();
		for(int i=0; i<m_nFolds; i++){
			m_minedSeqs.add(this.computeSpadeSeqs(i, workdir));
		}
		
		// write sequences
		super.writeWeightedSequences(minSeqsFileTxt);
		super.saveMinedSeqs(minSeqsFileJavaData);
	}
	
	/**
	 * Compute spade seqs.
	 *
	 * @param indexFold the index fold
	 * @param workdir the workdir
	 * @return the array list
	 */
	private ArrayList<String[]> computeSpadeSeqs(int indexFold, String workdir){
		// train sessions names
		ArrayList<String> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<String> trainsetnames2 = 
				m_distancematrix.getSessionIDs(trainsetnames, m_datasetSplit!=null);
		
		// assignment to each case
		int[] clindexes = m_clustersAL.get(indexFold);
		
		// maximum cluster index
		int climax = this.getMaxIndex(clindexes);
		
		// for each cluster
		ArrayList<String[]> seqs = new ArrayList<String[]>(); 
		for(int cli=0; cli<=climax; cli++){
			// take the sessions we are interested in
			ArrayList<String> names = new ArrayList<String>();
			for(int i=0; i<clindexes.length; i++){
				if(cli==clindexes[i]){
					names.add(trainsetnames2.get(i));
				}
			}
			
			// get SPADE sequences
			ArrayList<String[]> freqSeqs = this.getSpadeSequences(names, workdir);
			// add to the general list
			for(int i=0; i<freqSeqs.size(); i++){
				seqs.add(freqSeqs.get(i));
			}
		}
		
		// return SPADE sequences list
		return seqs;
	}
	
	/**
	 * Gets the max index.
	 *
	 * @param intArray the int array
	 * @return the max index
	 */
	protected int getMaxIndex(int[] intArray){
		int climax = Integer.MIN_VALUE;
		for(int i=0; i<intArray.length; i++){
			int cli = intArray[i];
			if(cli>climax){
				climax = cli;
			}
		}
		return climax;
	}
	
	/**
	 * Gets the spade sequences.
	 *
	 * @param sesIDs the ses i ds
	 * @param workdir the workdir
	 * @return the spade sequences
	 */
	protected ArrayList<String[]> getSpadeSequences(ArrayList<String> sesIDs, String workdir){
		// take the sequences
		int[] clusterDMind = m_distancematrix.getSessionIDsIndexes2(sesIDs, m_datasetSplit!=null);
		ArrayList<String[]> sequences = new ArrayList<String[]>(); 
		for(int i=0; i<clusterDMind.length; i++){
			int index = clusterDMind[i];
			String[] seq = this.getDataSet(m_datasetSplit!=null).get(index);
			sequences.add(seq);
		}
		
		// Create SPADE sequences
		MySPADE mspade = new MySPADE(sequences, m_minsupport);
		Object[] objA = mspade.getFrequentSequences(workdir);
		ArrayList<String[]> freqSeqs = (ArrayList<String[]>)objA[0];
		//ArrayList<Integer> freqSups = (ArrayList<Integer>)objA[1];
		
		return freqSeqs;
	}
	
	
}
