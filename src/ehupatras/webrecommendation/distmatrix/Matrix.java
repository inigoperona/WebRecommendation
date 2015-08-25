package ehupatras.webrecommendation.distmatrix;

import ehupatras.webrecommendation.utils.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Matrix.
 */
public abstract class Matrix {
	
	// distance matrix data
	/** The m_matrix. */
	protected MatrixStructure m_matrix = null;
	
	/** The m_names. */
	protected ArrayList<Long> m_names = null;
	
	/** The m_savefilename. */
	private String m_savefilename = "/_matrix.javaData";
	
	// split distance matrix data
	/** The m_starters. */
	private int[] m_starters = new int[]{11};
	
	/** The m_minimum activity. */
	private int m_minimumActivity = 3;
	
	/** The m_names split. */
	protected ArrayList<Long> m_namesSplit = null;
	
	/** The m_matrix split. */
	protected MatrixStructure m_matrixSplit = null;
	
	// attributes to work with topics
	/** The m_ url i ds. */
	protected ArrayList<Integer> m_UrlIDs = null;
	
	/** The m_topic distribution. */
	protected ArrayList<float[]> m_topicDistribution = null;
		// URL 2 URL distance
	/** The m_ urls dm. */
	protected float[][] m_UrlsDM = null;
	
	/** The m_urls equalness threshold. */
	protected float m_urlsEqualnessThreshold = 0.6f;
		// URL 2 TOPIC
	/** The m_url2topic. */
	protected int[] m_url2topic; // -1 no topic assigned
	
	/** The m_topicmatch. */
	protected float m_topicmatch = 0.5f;
	
	
	/**
	 * Compute matrix.
	 *
	 * @param data the data
	 * @param roleWeights the role weights
	 * @param isSplit the is split
	 */
	public abstract void computeMatrix(ArrayList<String[]> data,
							float[][] roleWeights,
							boolean isSplit);
	
	/**
	 * Gets the matrix.
	 *
	 * @param isSplit the is split
	 * @return the matrix
	 */
	public MatrixStructure getMatrix(boolean isSplit){
		if(!isSplit){
			return m_matrix;
		} else {
			return m_matrixSplit;
		}
	}
	
	/*
	public ArrayList<Long> getNames(boolean isSplit){
		return m_names;
	}
	*/
	
	/*
	public float getDistance(long rowSesID, long colSesID){
		int rowind = m_names.indexOf(rowSesID);
		int colind = m_names.indexOf(colSesID);
		return m_matrix[rowind][colind];
	}
	*/
		
	/**
	 * Gets the session i ds indexes.
	 *
	 * @param sessionIDs the session i ds
	 * @param isSplit the is split
	 * @return the session i ds indexes
	 */
	public int[] getSessionIDsIndexes(ArrayList<Long> sessionIDs, boolean isSplit){
		int[] indexes = null;
		if(!isSplit){
			// compute the positions of session-IDs in the distance matrix
			indexes = new int[sessionIDs.size()];
			for(int i=0; i<sessionIDs.size(); i++){
				long sesID = sessionIDs.get(i);
				int j;
				for(j=0; j<m_names.size(); j++){
					long sesID2 = m_names.get(j);
					if(sesID==sesID2){
						break;
					}
				}
				indexes[i] = j;
			}
		} else {
			// compute the positions of session-IDs in the split distance matrix
			ArrayList<Integer> indexesAL = new ArrayList<Integer>();
			for(int i=0; i<sessionIDs.size(); i++){
				long sesID = sessionIDs.get(i);
				for(int j=0; j<m_namesSplit.size(); j++){
					long sesIDSplit = m_namesSplit.get(j);
					long sesID2 = sesIDSplit / 100l;
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
	
	/**
	 * Gets the session i ds indexes2.
	 *
	 * @param sessionIDs the session i ds
	 * @param isSplit the is split
	 * @return the session i ds indexes2
	 */
	public int[] getSessionIDsIndexes2(ArrayList<Long> sessionIDs, boolean isSplit){
		ArrayList<Long> names;
		if(!isSplit){
			names = m_names;
		} else {
			names = m_namesSplit;
		}
		
		int[] indexes = new int[sessionIDs.size()];
		for(int i=0; i<sessionIDs.size(); i++){
			long sesID = sessionIDs.get(i);
			indexes[i] = names.indexOf(sesID);
		}

		return indexes;
	}
	
	
	/**
	 * Gets the session i ds.
	 *
	 * @param sessionIDs the session i ds
	 * @param isSplit the is split
	 * @return the session i ds
	 */
	public ArrayList<Long> getSessionIDs(ArrayList<Long> sessionIDs, boolean isSplit){
		if(!isSplit){
			return sessionIDs;
		} else {
			ArrayList<Long> sessionIDs2 = new ArrayList<Long>();
			for(int i=0; i<sessionIDs.size(); i++){
				long sesID = sessionIDs.get(i);
				for(int j=0; j<m_namesSplit.size(); j++){
					long sesIDSplit = m_namesSplit.get(j);
					long sesID2 = sesIDSplit / 100l;
					if(sesID==sesID2){
						sessionIDs2.add(sesIDSplit);
					}
				}
			}
			return sessionIDs2;
		}
	}
	
	
	
	// SAVE & LOAD MATRIX
	
	/**
	 * Write matrix.
	 *
	 * @param matrix the matrix
	 * @param outfilename the outfilename
	 */
	public void writeMatrix(MatrixStructure matrix, String outfilename){
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
			int n = matrix.getLength();
			for(int i=0; i<n; i++){
				for(int j=0; j<n; j++){
					writer.write(matrix.getCell(i,j) + " ");
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
	
	/**
	 * Save.
	 *
	 * @param wordirectory the wordirectory
	 */
	public void save(String wordirectory){
		SaveLoadObjects slo = new SaveLoadObjects();
		Object[] objA = new Object[4];
		objA[0] = m_matrix;
		objA[1] = m_names;
		objA[2] = m_matrixSplit;
		objA[3] = m_namesSplit;
		slo.save(objA, wordirectory + m_savefilename);
	}
	
	/**
	 * Load.
	 *
	 * @param wordirectory the wordirectory
	 */
	public void load(String wordirectory){
		SaveLoadObjects slo = new SaveLoadObjects();
		Object[] objA = (Object[])slo.load(wordirectory + m_savefilename);
		m_matrix = (MatrixStructure)objA[0];
		m_names = (ArrayList<Long>)objA[1];
		if(objA.length>2){
			m_matrixSplit = (MatrixStructure)objA[2];
			m_namesSplit = (ArrayList<Long>)objA[3];
		}
	}
    
    
    
    // SPLIT THE SEQUENCES
    
    /**
     * Split sequences.
     *
     * @param data the data
     * @return the object[]
     */
    public Object[] splitSequences(ArrayList<String[]> data){
    	ArrayList<Long> namesSplit = new ArrayList<Long>();
    	ArrayList<String[]> dataSplit = new ArrayList<String[]>();
    	int nseq = m_names.size();
    	for(int i=0; i<nseq; i++){
    		// original session-id and the original sequence
    		long sesID = m_names.get(i);
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
            			long subseqID = (long)sesID*100l+(long)subseq;
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
    			long subseqID = (long)sesID*100l+(long)subseq;
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
    
    /**
     * Write seqs.
     *
     * @param outputFilename the output filename
     * @param names the names
     * @param seqs the seqs
     */
    public void writeSeqs(String outputFilename, 
    		ArrayList<Long> names, 
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
				long sesID = names.get(i);
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
    
    /**
     * Read seqs.
     *
     * @param inputFilename the input filename
     * @return the object[]
     */
    public Object[] readSeqs(String inputFilename){
    	ArrayList<Long> names = new ArrayList<Long>();
    	ArrayList<String[]> seqs = new ArrayList<String[]>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputFilename));
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				String[] line = sCurrentLine.split(",");
				names.add(Long.valueOf(line[0]));
				int nURLs = line.length-1;
				String[] seq = new String[nURLs];
				for(int i=1; i<line.length; i++){
					seq[i-1] = line[i];
				}
				seqs.add(seq);
			}
			br.close();
		} catch (IOException ex){
			System.err.println("Exception at reading URLs' distance matrix. " + 
					"[ehupatras.webrecommendation.distmatrix.Matrix.readSeqs]");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// return value
		Object[] objA = new Object[2];
		objA[0] = names;
		objA[1] = seqs;
		return objA;
    }
    
    
    /**
     * Checks if is starter.
     *
     * @param urlID the url id
     * @return true, if is starter
     */
    private boolean isStarter(int urlID){
    	for(int i=0; i<m_starters.length; i++){
    		if(m_starters[i]==urlID){
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Sets the split parameters.
     *
     * @param starters the starters
     * @param minimumActivity the minimum activity
     */
    public void setSplitParameters(int[] starters, int minimumActivity){
    	m_starters = starters;
    	m_minimumActivity = minimumActivity;
    }
    
}
