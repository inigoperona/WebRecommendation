package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLevenshteinTopics2;

public class DistanceMatrixEditTopics2 
				extends DistanceMatrixEdit {

	public DistanceMatrixEditTopics2(ArrayList<Long> names, String urls2topicfile, float topicmatch){
		super(names);
		m_topicmatch = topicmatch;
		loadUrlsTopic(urls2topicfile);
	}
	
	public void computeMatrix(ArrayList<String[]> data,
					float[][] roleWeights,
					float topicmatch,
					boolean isplit){
		if(!isplit){
			m_matrix = new float[data.size()][data.size()];
		} else {
			m_matrixSplit = new float[data.size()][data.size()];
		}
		// create the similarity matrix
		for(int i=0; i<data.size(); i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing distances of row " + i);
			String[] seqA = data.get(i);
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentLevenshteinTopics2(m_UrlIDs, m_url2topic, topicmatch);
				seqalign.setRoleWeights(roleWeights);
				float dist = seqalign.getScore(seqA, seqB);
				if(!isplit){
					m_matrix[i][j] = dist;
				} else {
					m_matrixSplit[i][j] = dist;
				}
			}
		}
	}

}
