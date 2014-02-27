package ehupatras.webrecommendation.evaluator;

import java.util.ArrayList;

import ehupatras.suffixtree.stringarray.test.SuffixTreeStringArray;

public class SequenceEvaluatorUHC 
				extends SequenceEvaluator {

	public SequenceEvaluatorUHC(String[] sequence, SuffixTreeStringArray suffixtree){
		super(sequence, suffixtree);
		m_sequenceURL = this.removeLastUHC(m_sequence);
	}
	
	public SequenceEvaluatorUHC(ArrayList<String> sequence, SuffixTreeStringArray suffixtree){
		super(sequence, suffixtree);
		m_sequenceURL = this.removeLastUHC(m_sequence);
	}
	
	protected void computeStepMetrics(int stepIndex, ArrayList<String> recommendatios){		
		String step = m_sequence.get(stepIndex);
		step = step.substring(0, step.length()-1);
		ArrayList<String> recommendatios2 = this.removeLastUHC(recommendatios);
		ArrayList<String> recommendatios3 = this.uniqueTheRecommendations(recommendatios2);
		m_numberOfRecommendations = m_numberOfRecommendations + recommendatios3.size();
		super.computeHitScore(step, recommendatios3);
		super.computeClickSoonScore(stepIndex, recommendatios3);
		super.computeConfusionMatrix(stepIndex, recommendatios3);
	}
	
	private ArrayList<String> removeLastUHC(ArrayList<String> list){
		ArrayList<String> newlist = new ArrayList<String>();
		for(int i=0; i<list.size(); i++){
			String reco = list.get(i);
			String url = reco.substring(0, reco.length()-1);
			newlist.add(url);
		}
		return newlist;
	}
	
	private ArrayList<String> uniqueTheRecommendations(ArrayList<String> list){
		ArrayList<String> newlist = new ArrayList<String>();
		for(int i=0; i<list.size(); i++){
			String reco = list.get(i);
			
			// check if the recommendation is already in the list
			boolean isthere = false;
			for(int j=0; j<newlist.size(); j++){
				if(newlist.get(j).equals(reco)){
					isthere = true;
					break;
				}
			}
			
			// if it is not the URL already in the list add it
			if(!isthere){
				newlist.add(reco);
			}
		}
		return newlist;
	}
	
	public static void main(String[] args){
		// create the suffix tree
		SuffixTreeStringArray st = new SuffixTreeStringArray();
        String[] word1 = {"01U", "02H", "03C", "01H", "02U"};
        String[] word2 = {"01C", "02U", "02H", "01C", "02C", "03U"};
        String[] word3 = {"01U", "02H", "03H", "01H"};
        st.putSequence(word1, 0);
        st.putSequence(word2, 1);
        st.putSequence(word3, 2);
        st.printSuffixTree();
        
        // sequence to test
        ArrayList<String> seq = new ArrayList<String>();
        seq.add("01U");
        seq.add("02H");
        seq.add("03U");
        
        // get the metrics
        SequenceEvaluator se = new SequenceEvaluatorUHC(seq, st);
        se.computeSequenceMetrics();
        System.out.println("HR: " + se.getHitRatio());
        System.out.println("CR: " + se.getClickSoonRatio());
        System.out.println("---");
        se.printPrecision();
        se.printRecall();
        se.printFmeasure((float)1);
        se.printFmeasure((float)0.5);
        System.out.println("---");
        System.out.println("Pr0.00: " + se.getPrecisionAtPoint((float)0));
        System.out.println("Pr0.10: " + se.getPrecisionAtPoint((float)0.10));
        System.out.println("Pr0.25: " + se.getPrecisionAtPoint((float)0.25));
        System.out.println("Pr0.50: " + se.getPrecisionAtPoint((float)0.50));
        System.out.println("Pr0.75: " + se.getPrecisionAtPoint((float)0.75));
        System.out.println("Pr0.90: " + se.getPrecisionAtPoint((float)0.90));
        System.out.println("Pr1.00: " + se.getPrecisionAtPoint((float)1.00));
	}
	
}
