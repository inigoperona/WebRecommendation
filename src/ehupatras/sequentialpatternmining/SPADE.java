package ehupatras.sequentialpatternmining;

import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.candidatePatternsGeneration.CandidateGenerator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.creators.AbstractionCreator_Qualitative;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.database.SequenceDatabase;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.dataStructures.*;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.idLists.creators.IdListCreator_FatBitmap;
import ca.pfv.spmf.algorithms.sequentialpatterns.spade_spam_AGP.EquivalenceClass;
import java.io.IOException;
import java.util.*;

/**
 * Example of how to use the algorithm SPADE, saving the results in the 
 * main  memory
 * 
 * @author agomariz
 */
public class SPADE {

	private double m_minsupport = 0.5d;
	private SequenceDatabase m_sequenceDB;
	private MyAlgoSPADE m_algorithm;
	
	public SPADE(ArrayList<String[]> sequences, double minsup, boolean len1){
		m_minsupport = minsup;
		
		// initialization
	    AbstractionCreator abstractionCreator = AbstractionCreator_Qualitative.getInstance();
	    IdListCreator idListCreator = IdListCreator_FatBitmap.getInstance();
	    CandidateGenerator candidateGenerator = CandidateGenerator_Qualitative.getInstance();
	    m_sequenceDB = new SequenceDatabase(abstractionCreator, idListCreator);
	    
	    // insert sequences
	    for(int i=0; i<sequences.size(); i++){
	    	String[] seq = sequences.get(i);
	    	if(len1){
	    		String[] seq2 = this.convertSequenceToProperFormatLength1(seq);
	    		m_sequenceDB.addSequence(seq2);
	    	} else{
	    		m_sequenceDB.addSequence(seq);
	    	}
	    }
	    this.removeNotFrequentURLs();
	    this.reduceDatabase(m_sequenceDB.getFrequentItems().keySet());
	    
	    // run SPADE
		boolean dfs=true;
		m_algorithm = new MyAlgoSPADE(m_minsupport, dfs, abstractionCreator);
		boolean keepPatterns = true;
		boolean verbose = false;
		try{
			m_algorithm.runAlgorithm(m_sequenceDB, candidateGenerator, keepPatterns, verbose, null);
		} catch(IOException ex){
			System.err.println("[ehupatras.sequentialpatternmining.Spade] " +
					"IOException runnung the algorithm Spade.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	private String[] convertSequenceToProperFormatLength1(String[] sequence){
		// put the separator -1
		ArrayList<String> seq2 = new ArrayList<String>();
		int ind;
		for(ind=0; ind<sequence.length-1; ind++){
			seq2.add(sequence[ind]);
			seq2.add("-1");
		}
		seq2.add(sequence[ind]);
		seq2.add("-2");		
		
		// convert to String[]
		String[] seq = new String[seq2.size()];
		for(int i=0; i<seq2.size(); i++){ seq[i] = seq2.get(i); }
		return seq;
	}
	
	public Object[] getFrequentSequencesLength1(){
		Object[] objA = getFrequentSequences(1);
    	ArrayList<ArrayList<String>> freqseqs = (ArrayList<ArrayList<String>>)objA[0];
    	ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
    	ArrayList<String> freqseq1 = new ArrayList<String>();
    	for(int i=0; i<freqseqs.size(); i++){
    		freqseq1.add( (freqseqs.get(i)).get(0) );
    	}
    	
    	// return the value
    	Object[] objA2 = new Object[2];
    	objA2[0] = freqseq1;
    	objA2[1] = supports;
		return objA2;
	}
	
	public Object[] getFrequentSequences(int patterLen){
		return m_algorithm.getFrequentSequences(patterLen);
	}
	
	public void printSequencesDatabase(){
		System.out.println(m_sequenceDB.toString());
	}
	
	public void printStatistics(){
		System.out.println("Minimum support (relative) = " + m_minsupport);
		System.out.println(m_algorithm.getNumberOfFrequentPatterns() + " frequent patterns.");
		System.out.println(m_algorithm.printStatistics());
	}
	
    private void removeNotFrequentURLs(){
        double sup = (int) Math.ceil(m_minsupport * m_sequenceDB.size());
        Set<Item> frequentItemsSet = m_sequenceDB.getFrequentItems().keySet();
        Set<Item> itemsToRemove = new HashSet<Item>();
        //We remove those items that are not frequent
        for (Item frequentItem : frequentItemsSet) {
            //From the item set of frequent items
            EquivalenceClass equivalenceClass = m_sequenceDB.getFrequentItems().get(frequentItem);
            if (equivalenceClass.getIdList().getSupport() < sup) {
                itemsToRemove.add(frequentItem);
            } else {
                equivalenceClass.getIdList().setAppearingSequences(equivalenceClass.getClassIdentifier());
            }
        }
        for (Item itemToRemove : itemsToRemove) {
        	m_sequenceDB.getFrequentItems().remove(itemToRemove);
        }
    }
    
    /**
     * It reduces the original database to just frequent items.
     * @param keySet the set of frequent items that should be kept.
     */
    private void reduceDatabase(Set<Item> keySet) {
        for (Sequence sequence : m_sequenceDB.getSequences()) {
            for (int i = 0; i < sequence.size(); i++) {
                Itemset itemset = sequence.get(i);
                for (int j = 0; j < itemset.size(); j++) {
                    Item item = itemset.get(j);
                    if (!keySet.contains(item)) {
                        sequence.remove(i, j);
                        j--;
                    }
                }
                if (itemset.size() == 0) {
                    sequence.remove(i);
                    i--;
                }
            }
        }
    }

    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
    	
    	// sequences
    	String[] seq1 = {"1", "-1", "1", "2", "3", "-1", "1", "3", "-1", "4", "-1", "3", "6", "-1", "-2"};
    	String[] seq2 = {"1", "4", "-1", "3", "-1", "2", "3", "-1", "1", "5", "-1", "-2"};
    	String[] seq3 ={"5", "6", "-1", "1", "2", "-1", "4", "6", "-1", "3", "-1", "2", "-1", "-2"};
    	String[] seq4 = {"5", "-1", "7", "-1", "1", "6", "-1", "3", "-1", "2", "-1", "3", "-1", "-2"};
    	ArrayList<String[]> list = new ArrayList<String[]>();
    	list.add(seq1);
    	list.add(seq2);
    	list.add(seq3);
    	list.add(seq4);
    	
    	// sequences2
    	String[] seq11 = {"1", "1", "2", "3", "1", "3", "4", "3", "6"};
    	String[] seq12 = {"1", "4", "3", "2", "3", "1", "5"};
    	String[] seq13 ={"5", "6", "1", "2", "4", "6", "3", "2"};
    	String[] seq14 = {"5", "7", "1", "6", "3", "2", "3"};
    	ArrayList<String[]> list2 = new ArrayList<String[]>();
    	list2.add(seq11);
    	list2.add(seq12);
    	list2.add(seq13);
    	list2.add(seq14);
    	
    	// SPADE
    	SPADE sp;
    	//sp = new Spade(list, 0.5d, false);
    	sp = new SPADE(list2, 0.5d, true);
    	//sp.printSequencesDatabase();
    	//sp.printStatistics();
    	
    	Object[] objA;
    	
    	System.out.println("___Len1___");
    	objA = sp.getFrequentSequencesLength1();
    	ArrayList<String> freqseqs1 = (ArrayList<String>)objA[0];
    	ArrayList<Integer> supports = (ArrayList<Integer>)objA[1];
    	for(int i=0; i<freqseqs1.size(); i++){
    		System.out.print("(" + supports.get(i) + ")");
    		System.out.print(" " + freqseqs1.get(i));
    		System.out.println();
    	}
    	
    	System.out.println("___Len2___");
    	sp.getFrequentSequences(2);
    	System.out.println("___Len3___");
    	sp.getFrequentSequences(3);
    }
    
}