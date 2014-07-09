package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import ehupatras.webrecommendation.evaluator.sequence.SequenceEvaluatorMed;
import ehupatras.webrecommendation.recommender.Recommender;

public class SequenceEvaluatorMedContent 
				extends SequenceEvaluatorMed {

	// ATTRIBUTES
	
	private String m_enrichStrategy = "Contents001";
	
	// CREATOR
	
	public SequenceEvaluatorMedContent(
			String[] sequence,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			ArrayList<Object[]> recos,
			boolean isDistance,
			float[][] rolesW,
			int knn,
			String enrichStrategy){
		super(sequence, medoids, gmedoids, recos, isDistance, rolesW, knn);
		m_enrichStrategy = enrichStrategy;
	}
	
	// GET RECOMMENDER
	
	public Recommender getRecommender(){
		Recommender recommender = null;
		
		if(m_enrichStrategy.equals("Contents001")){
			recommender = new RecommenderKnnToClustersTopURLsAndContents001(
								m_medoids, m_gmedoids, m_recos,
								m_isDistance, m_rolesW);
		}
		
		return recommender;
	}
	
}
