package ehupatras.clustering.sapehac.experiment;

// TODO: Auto-generated Javadoc
/**
 * The Class ExperimentEhuPatras.
 */
public class ExperimentEhuPatras implements Experiment {
	
	/** The m_number ofcases. */
	private int m_numberOfcases = 0;
	
	/**
	 * Instantiates a new experiment ehu patras.
	 *
	 * @param numberOfcases the number ofcases
	 */
	public ExperimentEhuPatras(int numberOfcases){
		m_numberOfcases = numberOfcases;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.clustering.sapehac.experiment.Experiment#getNumberOfObservations()
	 */
	public int getNumberOfObservations(){
		return m_numberOfcases;
	}
}
