package ehupatras.webrecommendation.distmatrix;

import java.util.ArrayList;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentLevenshteinTopics;

public class DistanceMatrixEditTopics 
				extends DistanceMatrixEdit {

	public DistanceMatrixEditTopics(String urlsDMfile, float urlsEqualnessThreshold){
		m_urlsEqualnessThreshold = urlsEqualnessThreshold;
		loadUrlsDM(urlsDMfile);
	}
	
	public void computeMatrix(ArrayList<Integer> names, 
			ArrayList<String[]> data,
			float[][] roleWeights){
		m_names = names;
		m_matrix = new float[data.size()][data.size()];

		// create the similarity matrix
		for(int i=0; i<data.size(); i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing distances of row " + i);
			String[] seqA = data.get(i);
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentLevenshteinTopics(m_UrlIDs, m_UrlsDM, m_urlsEqualnessThreshold);
				//seqalign.setRoleWeights(roleWeights);
				float dist = seqalign.getScore(seqA, seqB);
				m_matrix[i][j] = dist;
			}
		}
	}
	
}
