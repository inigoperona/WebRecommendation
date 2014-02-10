package ehupatras.clustering.sapehac.experiment;

public class ExperimentEhuPatras implements Experiment {
	
	private int m_numberOfcases = 0;
	
	public ExperimentEhuPatras(int numberOfcases){
		m_numberOfcases = numberOfcases;
	}
	
	public int getNumberOfObservations(){
		return m_numberOfcases;
	}
}
