package ehupatras.webrecommendation.evaluator.test;

import java.util.ArrayList;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;

public class TestSetEvaluatorMed
				extends TestSetEvaluator {

	// ATTRIBUTES
	
	private ArrayList<String[]> m_medoids = null;
	private int[] m_gmedoids = null;
	private ArrayList<Object[]> m_recos = null;
	
	// CREATOR
	
	public TestSetEvaluatorMed(
			ArrayList<String[]> sequences,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			ArrayList<Object[]> recos){
		super(sequences);
		m_medoids = medoids;
		m_gmedoids = gmedoids;
		m_recos = recos;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(){
		
	}
	
}
