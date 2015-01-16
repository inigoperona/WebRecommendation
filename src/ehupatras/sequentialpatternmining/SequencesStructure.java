package ehupatras.sequentialpatternmining;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class SequencesStructure.
 */
public class SequencesStructure {

	// The data of the class
	// WRITE
	/** The m_seqs w. */
	private ArrayList<String[]> m_seqsW = new ArrayList<String[]>();
	
	/** The m_seq sups w. */
	private ArrayList<Integer> m_seqSupsW = new ArrayList<Integer>();
	
	/** The m_inds alw. */
	private ArrayList<int[]> m_indsALW = new ArrayList<int[]>();
	// READ
	/** The m_seqs r. */
	private ArrayList<String[]> m_seqsR = null;
	
	/** The m_seq sups r. */
	private ArrayList<Integer> m_seqSupsR = null;
	
	/** The m_inds alr. */
	private ArrayList<int[]> m_indsALR = null;

	// information to write in disk
	/** The m_workdir. */
	private String m_workdir = "/home/burdinadar";
	
	/** The m_basenamejavadata. */
	private String m_basenamejavadata = "spade.javaData";
	
	// indexes
	/** The m_max loaded sequences. */
	private int m_maxLoadedSequences = 1000;
	// indexes related with saving modulus
	/** The m_written modulus. */
	private int m_writtenModulus = 0;
	
	/** The m_last modulus last index. */
	private int m_lastModulusLastIndex = 0;
	// indexes related with reading modulus
	/** The m_actual modulus in memory. */
	private int m_actualModulusInMemory = -1;
	
	
	/**
	 * Instantiates a new sequences structure.
	 *
	 * @param workdir the workdir
	 */
	public SequencesStructure(String workdir){
		m_workdir = workdir;
	}
	
	/**
	 * Adds the.
	 *
	 * @param sequence the sequence
	 * @param support the support
	 * @param indexes the indexes
	 */
	public void add(String[] sequence, int support, int[] indexes){
		// we are in the last modulus, but it is full
		if(m_lastModulusLastIndex>=m_maxLoadedSequences){
			this.saveModulus(m_writtenModulus);
			// initialize parameters
			m_lastModulusLastIndex = 0;
			m_seqsW = new ArrayList<String[]>();
			m_seqSupsW = new ArrayList<Integer>();
			m_indsALW = new ArrayList<int[]>();
			System.gc();
			// update counters
			m_writtenModulus++;
		}
		// add the new sequence
		m_seqsW.add(sequence);
		m_seqSupsW.add(support);
		m_indsALW.add(indexes);
		m_lastModulusLastIndex++;		
	}
	
	/**
	 * Gets the sequence.
	 *
	 * @param i the i
	 * @return the sequence
	 */
	public Object[] getSequence(int i) {
		// compute modulus indexes
		int imodulus = i / m_maxLoadedSequences;
		int iindex = i % m_maxLoadedSequences;
		
		// the structure we are going to return
		Object[] objA = new Object[3];

		// if we want data from the last modul take from the write-structure
		if(imodulus==m_writtenModulus){
			objA[0] = m_seqsW.get(iindex);
			objA[1] = m_seqSupsW.get(iindex);
			objA[2] = m_indsALW.get(iindex);
		} else {
			if(imodulus!=m_actualModulusInMemory){
				this.loadModulus(imodulus);
			}
			objA[0] = m_seqsR.get(iindex);
			objA[1] = m_seqSupsR.get(iindex);
			objA[2] = m_indsALR.get(iindex);
		}
		
		// return structure
		return objA;
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return m_writtenModulus*m_maxLoadedSequences + m_lastModulusLastIndex;
	}
	
	/**
	 * Gets the sequences.
	 *
	 * @return the sequences
	 */
	public ArrayList<String[]> getSequences(){
		ArrayList<String[]> seqs = new ArrayList<String[]>();
		for(int i=0; i<this.size(); i++){
			Object[] objA = this.getSequence(i);
			String[] seq = (String[])objA[0];
			seqs.add(seq);
		}
		return seqs;
	}
	
	/**
	 * Gets the sequences supports.
	 *
	 * @return the sequences supports
	 */
	public ArrayList<Integer> getSequencesSupports(){
		ArrayList<Integer> sups = new ArrayList<Integer>();
		for(int i=0; i<this.size(); i++){
			Object[] objA = this.getSequence(i);
			int sup = (int)objA[1];
			sups.add(sup);
		}
		return sups;
	}
	
	
	
	// SAVE AND LOAD THE SPADE SEQUENCES //
	
	/**
	 * Save modulus.
	 *
	 * @param mod the mod
	 */
	private void saveModulus(int mod){
		String outputfilename = "_" + mod + "_" + m_basenamejavadata;
		// open the file
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(m_workdir + "/" + outputfilename);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.sequentialpatternmining.SequencesStructure.saveModulus] " +
				"Problems at opening the file: " + outputfilename + " to write.");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// write in the file
		ObjectOutputStream oos = null;
		try{
			Object[] objA = new Object[3];
			objA[0] = m_seqsW;
			objA[1] = m_seqSupsW;
			objA[2] = m_indsALW;
			oos = new ObjectOutputStream(fos);
			oos.writeObject(objA);
		} catch (IOException ex){
			System.err.println("[ehupatras.sequentialpatternmining.SequencesStructure.saveModulus] " +
				"Problems at writing in the file: " + outputfilename);
			ex.printStackTrace();
			System.exit(1);
		}
		
		// close the file
		try{
			oos.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.sequentialpatternmining.SequencesStructure.saveModulus] " +
				"Problems at closing the file: " + outputfilename + " after writing.");
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Load modulus.
	 *
	 * @param mod the mod
	 */
	private void loadModulus(int mod){
		String outputfilename = "_" + mod + "_" + m_basenamejavadata;
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(m_workdir + "/" + outputfilename);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.sequentialpatternmining.SequencesStructure.loadModulus] " +
					"Problems at opening the file: " + outputfilename + " to read.");
			ex.printStackTrace();
			System.exit(1);
		}
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(fis);
			Object[] objA = (Object[])ois.readObject();
			m_seqsR = (ArrayList<String[]>)objA[0];
			m_seqSupsR = (ArrayList<Integer>)objA[1];
			m_indsALR = (ArrayList<int[]>)objA[2];
		} catch(IOException ex){
			System.err.println("[ehupatras.sequentialpatternmining.SequencesStructure.loadModulus] " +
					"Problems at reading the file: " + outputfilename);
			ex.printStackTrace();
			System.exit(1);
		} catch(ClassNotFoundException ex){
			System.err.println("[ehupatras.sequentialpatternmining.SequencesStructure.loadModulus] " +
					"Problems at casting to a specific object: " + outputfilename);
			ex.printStackTrace();
			System.exit(1);
		}
		try{
			ois.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.sequentialpatternmining.SequencesStructure.loadModulus] " +
					"Problems closing the file: " + outputfilename + " after reading.");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// update the indexes
		m_actualModulusInMemory = mod;
	}
	
}
