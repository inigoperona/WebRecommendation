package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.suffixtree.stringarray.myst.MySuffixTree;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorST;

public class TestSetEvaluatorST 
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	private MySuffixTree m_suffixtree;
	
	private int m_failuremode = 0;
	private int m_maxMemory = 100;
	private int m_normMode = 0;
	
	// CREATOR
	
	public TestSetEvaluatorST(
			ArrayList<String[]> sequences,
			int modePrRe,
			MySuffixTree suffixtree,
			int failuremode,
			int maxMemory,
			int normMode){
		super(sequences, modePrRe);
		m_suffixtree = suffixtree;
		m_failuremode = failuremode;
		m_maxMemory = maxMemory;
		m_normMode = normMode;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorST(sequence, m_modePrRe,
						m_suffixtree,
						m_failuremode, m_maxMemory, m_normMode);
		return seqEva;
	}
	
}
