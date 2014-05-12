package ehupatras.sequentialpatternmining;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import ehupatras.webrecommendation.structures.Request;

public class SequencesStructure {

	private ArrayList<String[]> m_seqs = new ArrayList<String[]>();
	private ArrayList<Integer> m_seqSups = new ArrayList<Integer>();
	private ArrayList<int[]> m_indsAL = new ArrayList<int[]>();
	
	private String m_workdir;
	private String m_basenamejavadata = "spade.javaData";
	private int m_writtenModulus = 0;
	private int m_actualModulusInMemory = 0;
	private int m_actualLoadedSequence = 0;
	private int m_maxLoadedSequences = 100;
	private int m_lastModulusLastIndex = 0;
	
	public void add(String[] sequence, int support, int[] indexes){
		
		// we are not in the last mosulus to add
		if(m_writtenModulus>m_actualModulusInMemory){
			this.saveModulus(m_actualModulusInMemory);
			this.loadModulus(m_writtenModulus);
		}
		
		// we are in the last modulus, but it is full
		if(m_actualLoadedSequence>=m_maxLoadedSequences){
			this.saveModulus(m_writtenModulus);
			// update counters
			m_writtenModulus++;
			m_actualLoadedSequence++;
			// initialize parameters
			m_actualLoadedSequence = 0;
			m_seqs = new ArrayList<String[]>();
			m_seqSups = new ArrayList<Integer>();
			m_indsAL = new ArrayList<int[]>();
			System.gc();
		}
		
		// add the new sequence
		m_seqs.add(sequence);
		m_seqSups.add(support);
		m_indsAL.add(indexes);
		m_actualLoadedSequence++;
		
		// save the last modulus last index
		m_lastModulusLastIndex = m_actualLoadedSequence;
	}
	
	
	
	// SAVE AND LOAD THE SPADE SEQUENCES //
	
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
			objA[0] = m_seqs;
			objA[1] = m_seqSups;
			objA[2] = m_indsAL;
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
			m_seqs = (ArrayList<String[]>)objA[0];
			m_seqSups= (ArrayList<Integer>)objA[1];
			m_indsAL = (ArrayList<int[]>)objA[3];
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
