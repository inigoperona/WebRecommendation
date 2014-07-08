package ehupatras.webrecommendation.evaluator.sequence;

import java.util.ArrayList;
import ehupatras.webrecommendation.recommender.Recommender;
import ehupatras.webrecommendation.recommender.RecommenderKnnToClustersTopURLs;

public class SequenceEvaluatorMed 
				extends SequenceEvaluator {

	// ATTRIBUTES
	
	protected ArrayList<String[]> m_medoids = null;
	protected int[] m_gmedoids = null;
	protected ArrayList<Object[]> m_recos = null;
	
	protected boolean m_isDistance = true;
	protected float[][] m_rolesW = {{ 0f, 0f, 0f},
									{ 0f, 0f, 0f},
									{ 0f, 0f, 0f}};
	protected int m_knn = 100;
	
	// CREATOR
	
	public SequenceEvaluatorMed(
			String[] sequence,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			ArrayList<Object[]> recos,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequence);
		m_medoids = medoids;
		m_gmedoids = gmedoids;
		m_recos = recos;
		m_isDistance = isDistance;
		m_rolesW = rolesW;
		m_knn = knn;
	}
	
	protected SequenceEvaluatorMed(
			String[] sequence){
		super(sequence);
	}
	
	protected SequenceEvaluatorMed(
			String[] sequence,
			ArrayList<String[]> medoids,
			int[] gmedoids,
			boolean isDistance,
			float[][] rolesW,
			int knn){
		super(sequence);
		m_medoids = medoids;
		m_gmedoids = gmedoids;
		m_isDistance = isDistance;
		m_rolesW = rolesW;
		m_knn = knn;
	}
	
	// GET RECOMMENDER
	
	public Recommender getRecommender(){
		Recommender recommender = 
				new RecommenderKnnToClustersTopURLs(
						m_medoids, m_gmedoids, m_recos,
						m_isDistance, m_rolesW);
		return recommender;
	}
	
}
