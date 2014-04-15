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
	protected float[][] m_matrix;
	protected ArrayList<Integer> m_names;
	private String m_savefilename = "/_matrix.javaData";
	
	// attributes to work with topics
	protected ArrayList<Integer> m_UrlIDs = null;
	protected ArrayList<float[]> m_topicDistribution = null;
		// URL 2 URL distance
	protected float[][] m_UrlsDM = null;
	protected float m_urlsEqualnessThreshold = 0.6f;
		// URL 2 TOPIC
	protected int[] m_url2topic; // -1 no topic assigned
	protected float m_topicmatch = 0.5f;
	
	
	public abstract void computeMatrix(ArrayList<Integer> names, 
							ArrayList<String[]> data,
							float[][] roleWeights);
	
	public float[][] getMatrix(){
		return m_matrix;
	}
	
	public ArrayList<Integer> getNames(){
		return m_names;
	}
	
	public float getDistance(int rowSesID, int colSesID){
		int rowind = m_names.indexOf(rowSesID);
		int colind = m_names.indexOf(colSesID);
		return m_matrix[rowind][colind];
	}
	
	public int[] getSessionIDsIndexes(){
		return this.getSessionIDsIndexes(m_names);
	}
	
	public int[] getSessionIDsIndexes(ArrayList<Integer> sessionIDs){
		int[] indexes = new int[sessionIDs.size()];
		for(int i=0; i<sessionIDs.size(); i++){
			int sesID = sessionIDs.get(i);
			indexes[i] = m_names.indexOf(sesID);
		}
		return indexes;
	}
	
	public void writeMatrix(String outfilename){
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
			int n = m_matrix.length;
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					writer.write(m_matrix[i][j] + " ");
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
		Object[] objA = new Object[2];
		objA[0] = m_matrix;
		objA[1] = m_names;
		slo.save(objA, wordirectory + m_savefilename);
	}
	
	public void load(String wordirectory){
		SaveLoadObjects slo = new SaveLoadObjects();
		Object[] objA = (Object[])slo.load(wordirectory + m_savefilename);
		m_matrix = (float[][])objA[0];
		m_names = (ArrayList<Integer>)objA[1];
	}
	
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
    
    
    
}
