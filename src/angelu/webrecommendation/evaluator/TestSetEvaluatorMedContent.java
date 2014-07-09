package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluator;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluatorMed;

public class TestSetEvaluatorMedContent 
				extends TestSetEvaluatorMed {

	// ATTRIBUTES
	
	private String m_enrichStrategy = "Contents001";
	
	// CREATOR
	
	public TestSetEvaluatorMedContent(
			ArrayList<String[]> sequences,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			ArrayList<Object[]> recos,
			boolean isDistance,
			float[][] rolesW,
			int knn,
			String enrichStrategy){
		super(sequences, medoids, gmedoids, recos, isDistance, rolesW, knn);
		m_enrichStrategy = enrichStrategy;
	}
	
	// GET SEQUENCE EVALUATOR
	
	public SequenceEvaluator getSequenceEvaluator(String[] sequence){
		SequenceEvaluator seqEva = 
				new SequenceEvaluatorMedContent(sequence, 
						m_medoids, m_gmedoids, m_recos,
						m_isDistance, m_rolesW, m_knn,
						m_enrichStrategy);
		return seqEva;
	}
	
}
