package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;

import ehupatras.sequentialpatternmining.MySPADE;
import ehupatras.webrecommendation.distmatrix.Matrix;

public class ModelEvaluatorSeqMinSPADE
				extends ModelEvaluatorSeqMin {

	// CREATOR
	
	public ModelEvaluatorSeqMinSPADE(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL);
	}
	
	// BUILD MODEL
	
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
	
	private ArrayList<String[]> computeSpadeSeqs(int indexFold, String workdir){
		// train sessions names
		ArrayList<Long> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<Long> trainsetnames2 = 
				m_distancematrix.getSessionIDs(trainsetnames, m_datasetSplit!=null);
		
		// assignment to each case
		int[] clindexes = m_clustersAL.get(indexFold);
		
		// maximum cluster index
		int climax = this.getMaxIndex(clindexes);
		
		// for each cluster
		ArrayList<String[]> seqs = new ArrayList<String[]>(); 
		for(int cli=0; cli<=climax; cli++){
			// take the sessions we are interested in
			ArrayList<Long> names = new ArrayList<Long>();
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
	
	protected ArrayList<String[]> getSpadeSequences(ArrayList<Long> sesIDs, String workdir){
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
