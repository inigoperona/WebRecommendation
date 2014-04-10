package ehupatras.webrecommendation.distmatrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ehupatras.webrecommendation.sequencealignment.SequenceAlignment;
import ehupatras.webrecommendation.sequencealignment.SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics;

public class SimilarityMatrixInverseTopics 
				extends SimilarityMatrixInverse {

	private ArrayList<Integer> m_UrlIDs = null;
	private float[][] m_UrlsDM = null;
	private float m_urlsEqualnessThreshold = 0.6f;
	
	public SimilarityMatrixInverseTopics(String urlsDMfile, float urlsEqualnessThreshold){
		m_urlsEqualnessThreshold = urlsEqualnessThreshold;
		this.loadUrlsDM(urlsDMfile);
	}
	
	public void computeMatrix(ArrayList<Integer> names, 
			ArrayList<String[]> data,
			float[][] roleWeights){
		m_names = names;
		m_matrix = new float[data.size()][data.size()];

		float[][] simmatrix = new float[data.size()][data.size()];
		for(int i=0; i<data.size(); i++){
			System.out.println("  [" + System.currentTimeMillis() + "] Computing similarities of row " + i);
			// compute each row similarities
			// and check the maximum and minimum values to create the distance
			String[] seqA = data.get(i);
			float[] similaritiesrow = new float[data.size()];
			float maxsim = Float.NEGATIVE_INFINITY;
			float minsim = Float.POSITIVE_INFINITY;
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignment seqalign = new SequenceAlignmentCombineGlobalLocalDimopoulos2010Topics(m_UrlIDs, m_UrlsDM, m_urlsEqualnessThreshold);
				seqalign.setRoleWeights(roleWeights);
				float sim = seqalign.getScore(seqA, seqB);
				if(sim>maxsim){ maxsim = sim; }
				if(sim<minsim){ minsim = sim; }
				similaritiesrow[j] = sim;
			}
			// put the similarity between 0 and 1 (normalize it)
			for(int j=0; j<data.size(); j++){
				float sim = similaritiesrow[j];
				simmatrix[i][j] = (sim-minsim) / Math.abs(maxsim-minsim);
			}
		}

		// convert to distance by inversing the value
		for(int i=0; i<data.size(); i++){
			for(int j=0; j<data.size(); j++){
				float sim = simmatrix[i][j];
				m_matrix[i][j] = 1f - sim; 
			}
		}
	}
	
	public void setUrlsEqualnessThreshold(float urlsEqualnessThreshold){
		m_urlsEqualnessThreshold = urlsEqualnessThreshold;
	}
	
    private void loadUrlsDM(String urlsDMfile){
    	// load the distance matrix of URL's similarity
    	m_UrlIDs = new ArrayList<Integer>();
    	ArrayList<float[]> urlsDM = new ArrayList<float[]>(); 
		try {
			BufferedReader br = new BufferedReader(new FileReader(urlsDMfile));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(" ");
				m_UrlIDs.add(Integer.valueOf(line[0]));
				int nURLs = line.length-1;
				float[] urldist = new float[nURLs];
				for(int i=1; i<line.length; i++){
					urldist[i-1] = Float.valueOf(line[i]);
				}
				urlsDM.add(urldist);
			}
			br.close();
		} catch (IOException ex){
			System.err.println("Exception at reading URLs' distance matrix. " + 
					"[ehupatras.webrecommendation.distmatrix.SimilarityMatrixInverse.setUrlsDM]");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// convert too float-matrix
		int nURLs = m_UrlIDs.size();
		m_UrlsDM = new float[nURLs][nURLs];
		for(int i=0; i<urlsDM.size(); i++){
			m_UrlsDM[i] = urlsDM.get(i);
		}
    }
	
}
