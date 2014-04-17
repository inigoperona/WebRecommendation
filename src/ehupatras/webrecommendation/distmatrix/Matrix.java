package ehupatras.webrecommendation.distmatrix;

import ehupatras.webrecommendation.utils.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Matrix {
	
	// distance matrix data
	protected float[][] m_matrix = null;
	protected ArrayList<Integer> m_names = null;
	private String m_savefilename = "/_matrix.javaData";
	
	// split distance matrix data
	private int[] m_starters = new int[]{11};
	private int m_minimumActivity = 3;
	protected ArrayList<Integer> m_namesSplit = null;
	protected float[][] m_matrixSplit = null;
	
	// attributes to work with topics
	protected ArrayList<Integer> m_UrlIDs = null;
	protected ArrayList<float[]> m_topicDistribution = null;
		// URL 2 URL distance
	protected float[][] m_UrlsDM = null;
	protected float m_urlsEqualnessThreshold = 0.6f;
		// URL 2 TOPIC
	protected int[] m_url2topic; // -1 no topic assigned
	protected float m_topicmatch = 0.5f;
	
	
	public abstract void computeMatrix(ArrayList<String[]> data,
							float[][] roleWeights,
							boolean isSplit);
	
	public float[][] getMatrix(boolean isSplit){
		if(!isSplit){
			return m_matrix;
		} else {
			return m_matrixSplit;
		}
	}
	
	public ArrayList<Integer> getNames(boolean isSplit){
		return m_names;
	}
	
	public float getDistance(int rowSesID, int colSesID){
		int rowind = m_names.indexOf(rowSesID);
		int colind = m_names.indexOf(colSesID);
		return m_matrix[rowind][colind];
	}
		
	public int[] getSessionIDsIndexes(ArrayList<Integer> sessionIDs, boolean isSplit){
		int[] indexes = null;
		if(!isSplit){
			// compute the positions of session-IDs in the distance matrix
			indexes = new int[sessionIDs.size()];
			for(int i=0; i<sessionIDs.size(); i++){
				int sesID = sessionIDs.get(i);
				indexes[i] = m_names.indexOf(sesID);
			}
		} else {
			// compute the positions of session-IDs in the split distance matrix
			ArrayList<Integer> indexesAL = new ArrayList<Integer>();
			for(int i=0; i<sessionIDs.size(); i++){
				int sesID = sessionIDs.get(i);
				for(int j=0; j<m_namesSplit.size(); j++){
					int sesIDSplit = m_namesSplit.get(j);
					int sesID2 = sesIDSplit / 100;
					if(sesID==sesID2){
						indexesAL.add(j);
					}
				}
			}
			// convert to int[]
			indexes = new int[indexesAL.size()];
			for(int i=0; i<indexesAL.size(); i++){
				indexes[i] = indexesAL.get(i);
			}
		}
		return indexes;
	}
	
	
	
	// SAVE & LOAD MATRIX
	
	public void writeMatrix(float[][] matrix, String outfilename){
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.distmatrix.Matrix.writeMatrix] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	
		// Write in a file line by line
		try{
			int n = matrix.length;
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					writer.write(matrix[i][j] + " ");
				}
				writer.write("\n");
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.distmatrix.Matrix.writeMatrix] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.distmatrix.Matrix.writeMatrix] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	public void save(String wordirectory){
		SaveLoadObjects slo = new SaveLoadObjects();
		Object[] objA = new Object[4];
		objA[0] = m_matrix;
		objA[1] = m_names;
		objA[2] = m_matrixSplit;
		objA[3] = m_namesSplit;
		slo.save(objA, wordirectory + m_savefilename);
	}
	
	public void load(String wordirectory){
		SaveLoadObjects slo = new SaveLoadObjects();
		Object[] objA = (Object[])slo.load(wordirectory + m_savefilename);
		m_matrix = (float[][])objA[0];
		m_names = (ArrayList<Integer>)objA[1];
		m_matrixSplit = (float[][])objA[2];
		m_namesSplit = (ArrayList<Integer>)objA[3];
	}
	
	
	
	// TOPICS
	
    protected void loadUrlsDM(String urlsDMfile){
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
					"[ehupatras.webrecommendation.distmatrix.SimilarityMatrixInverse.loadUrlsDM]");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// convert to float-matrix
		int nURLs = m_UrlIDs.size();
		m_UrlsDM = new float[nURLs][nURLs];
		for(int i=0; i<urlsDM.size(); i++){
			m_UrlsDM[i] = urlsDM.get(i);
		}
    }
    
    protected void loadUrlsTopic(String urlsTopicfile){
    	// load the distance matrix of URL's similarity
    	m_UrlIDs = new ArrayList<Integer>();
    	ArrayList<Integer> urls2topic = new ArrayList<Integer>(); 
		try {
			BufferedReader br = new BufferedReader(new FileReader(urlsTopicfile));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(" ");
				m_UrlIDs.add(Integer.valueOf(line[0]));
				urls2topic.add(Integer.valueOf(line[1]));
			}
			br.close();
		} catch (IOException ex){
			System.err.println("Exception at reading URLs' distance matrix. " + 
					"[ehupatras.webrecommendation.distmatrix.SimilarityMatrixInverse.loadUrlsTopic]");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// convert to integer-array
		int nURLs = m_UrlIDs.size();		
		m_url2topic = new int[nURLs];
		for(int i=0; i<m_url2topic.length; i++){
			m_url2topic[i] = urls2topic.get(i);
		}
    }
    
    
    
    // SPLIT THE SEQUENCES
    
    public Object[] splitSequences(ArrayList<String[]> data){
    	ArrayList<Integer> namesSplit = new ArrayList<Integer>();
    	ArrayList<String[]> dataSplit = new ArrayList<String[]>();
    	int nseq = m_names.size();
    	for(int i=0; i<nseq; i++){
    		// original session-id and the original sequence
    		int sesID = m_names.get(i);
    		String[] seq = data.get(i);
    		
    		// put the first element in the new sequence
    		ArrayList<String> seqSplit = new ArrayList<String>();
        	seqSplit.add(seq[0]);
        	
        	// split the sequence and ensure the minimum activity
    		int elemLen = seq[0].length();
    		int subseq = 0;
    		for(int j=1; j<seq.length; j++){
            	String urlA = seq[j].substring(0,elemLen-1);
            	int urlAi = Integer.valueOf(urlA);
            	if(isStarter(urlAi)){
            		if(seqSplit.size()>=m_minimumActivity){
            			// data sequence
            			String[] seqSplitA = new String[seqSplit.size()];
            			for(int k=0; k<seqSplitA.length; k++){
            				seqSplitA[k] = seqSplit.get(k);
            			}
            			dataSplit.add(seqSplitA);
            			// update the subsequence identifier
            			int subseqID = sesID*100+subseq;
            			namesSplit.add(subseqID);
            			// increment the subsequence counter
            			subseq++;
            		}
            		seqSplit = new ArrayList<String>();
            	}
            	seqSplit.add(seq[j]);
    		}
    		if(seqSplit.size()>=m_minimumActivity){
    			// data sequence
    			String[] seqSplitA = new String[seqSplit.size()];
    			for(int k=0; k<seqSplitA.length; k++){
    				seqSplitA[k] = seqSplit.get(k);
    			}
    			dataSplit.add(seqSplitA);
    			// update the subsequence identifier
    			int subseqID = sesID*100+subseq;
    			namesSplit.add(subseqID);
    		}
    		
    	}
    	
    	// return object
    	m_namesSplit = namesSplit; 
    	Object[] objA = new Object[2];
    	objA[0] = namesSplit;
    	objA[1] = dataSplit;
    	return objA;
    }
    
    public void writeSeqs(String outputFilename, 
    		ArrayList<Integer> names, 
    		ArrayList<String[]> seqs){
    	
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outputFilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.distmatrix.Matrix.writeSeqs] " +
					"Not possible to open the file: " + outputFilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	
		// Write in a file line by line
		try{
			for(int i=0; i<names.size(); i++){
				int sesID = names.get(i);
				writer.write(String.valueOf(sesID));
				String[] seq = seqs.get(i);
				for(int j=0; j<seq.length; j++){
					writer.write("," + seq[j]);
				}
				writer.write("\n");
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.distmatrix.Matrix.writeSeqs] " +
					"Problems writing to the file: " + outputFilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.distmatrix.Matrix.writeSeqs] " +
					"Problems at closing the file: " + outputFilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
    }
    
    private boolean isStarter(int urlID){
    	for(int i=0; i<m_starters.length; i++){
    		if(m_starters[i]==urlID){
    			return true;
    		}
    	}
    	return false;
    }
    
    public void setSplitParameters(int[] starters, int minimumActivity){
    	m_starters = starters;
    	m_minimumActivity = minimumActivity;
    }
    
}
