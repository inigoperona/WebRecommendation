package ehupatras.webrecommendation.distmatrix;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.io.Serializable;

public class MatrixStructure implements Serializable {
	private static final long serialVersionUID = 1L;	
	
	// distance matrix data
	protected ArrayList<float[]> m_maAL = null; 
	protected int m_length = -1;
	
	// swapping attributes
	private int m_maxLoadedSequences = 1000; //1000
	private int m_lastModulusLastIndex = 0;
	private int m_writtenModulus = 0;
	private int m_actualModulusInMemory = -1;
	private String m_basenamejavadata = "dm.javaData";
	private String m_workdir = "/home/burdinadar";
	// swapping memory
	private static int m_nMemory = 6; //10
	private static ArrayList<ArrayList<float[]>> m_matrixS = null;
	private static ArrayList<Integer> m_actualloadedmodulusS = null;
		
	
	public MatrixStructure(int size, String workdirectory){
		m_length = size;
		m_maAL = new ArrayList<float[]>();
		m_workdir = workdirectory;
		this.initmodulus();
	}
	
	public MatrixStructure(float[][] fmatrix){
		m_maxLoadedSequences = 1000000;
		m_length = fmatrix.length;
		m_maAL = new ArrayList<float[]>();
		for(int i=0; i<m_length; i++){
			for(int j=0; j<m_length; j++){
				this.addCell(i, j, fmatrix[i][j]);
			}
		}
	}
	
	private void initmodulus(){
		m_matrixS = null;
		m_actualloadedmodulusS = null;
		System.gc();
		m_matrixS = new ArrayList<ArrayList<float[]>>();
		m_actualloadedmodulusS = new ArrayList<Integer>();
		for(int i=0; i<m_nMemory; i++){
			m_actualloadedmodulusS.add(-1);
			m_matrixS.add(null);
		}
	}
	
	public void addCell(int i, int j, float value){
		// compute modulus indexes
		int imodulus = i / m_maxLoadedSequences;
		int iindex = i % m_maxLoadedSequences;
		
		if(m_lastModulusLastIndex>=m_maxLoadedSequences){
			this.saveModulus(m_writtenModulus);
			m_lastModulusLastIndex = 0;
			m_maAL = new ArrayList<float[]>();
			m_writtenModulus++;
			m_actualModulusInMemory = m_writtenModulus; 
		}
		// check if the row exists
		if(j==0){
			float[] row = new float[m_length];
			m_maAL.add(row);
		}
		if(j==(m_length-1)){
			m_lastModulusLastIndex++;
		}
		// add element
		float[] row = m_maAL.get(iindex);
		row[j] = value;
		m_maAL.set(iindex, row);
		
		// when the last element is inserted
		if(i==(m_length-1) && j==(m_length-1)){
			this.saveModulus(m_writtenModulus);
		}
	}
	
	public float getCell(int i, int j){
		// compute modulus indexes
		int imodulus = i / m_maxLoadedSequences;
		int iindex = i % m_maxLoadedSequences;
		
		// if we have in memory return it
		if(m_matrixS==null){
			this.initmodulus();
		}
		int iMem = m_actualloadedmodulusS.indexOf(new Integer(imodulus));
		if(iMem!=-1){
			return m_matrixS.get(iMem).get(iindex)[j];
		}
		
		// if we want data from the last modul take from the write-structure
		if(imodulus!=m_actualModulusInMemory){
			this.loadModulus(imodulus);
		}
		
		// return structure
		return m_maAL.get(iindex)[j];
	}
	
	public float getCelldump(int i, int j){
		return 0f;
	}
	
	public void setCell(int i, int j, double d){
		int imodulus = i / m_maxLoadedSequences;
		int iindex = i % m_maxLoadedSequences;
		
		// if we have in memory return it
		int iMem = m_actualloadedmodulusS.indexOf(new Integer(imodulus));
		if(iMem!=-1){
			//m_vectorsS.get(iMem).remove(iindex);
			//m_vectorsS.get(iMem).add(iindex, (float)d);
		}
	}
	
	public int getLength(){
		return m_length;
	}
	
	private void saveModulus(int mod){
		long starttime = System.currentTimeMillis();		
		String outputfilename = "_" + mod + "_" + m_basenamejavadata;
		
		// open the file
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(m_workdir + "/" + outputfilename);
		} catch (FileNotFoundException ex){
			System.err.println("[MatrixStructure.saveModulus] " +
				"Problems at opening the file: " + outputfilename + " to write.");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// write in the file
		ObjectOutputStream oos = null;
		try{
			Object[] objA = new Object[1];
			objA[0] = m_maAL;
			oos = new ObjectOutputStream(fos);
			oos.writeObject(objA);
		} catch (IOException ex){
			System.err.println("[MatrixStructure.saveModulus] " +
				"Problems at writing in the file: " + outputfilename);
			ex.printStackTrace();
			System.exit(1);
		}
		
		// close the file
		try{
			oos.close();
		} catch (IOException ex){
			System.err.println("[MatrixStructure.saveModulus] " +
				"Problems at closing the file: " + outputfilename + " after writing.");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// verbose
		long endtime = System.currentTimeMillis();
		System.out.println("  [" + endtime + "] Save modulus " + mod +
				" to the hard drive. " + (endtime-starttime)/1000 + " seconds.");
	}
	
	private void loadModulus(int mod){
		long starttime = System.currentTimeMillis();
		
		// save the loaded modulus in the memory
		int i1Mod = m_actualloadedmodulusS.get(0);
		if(i1Mod!=-1){
			// remove the last element
			int lastIndexMem = m_nMemory-1;
			int modtosave = m_actualloadedmodulusS.get(lastIndexMem);
			if(modtosave!=-1){ // remove the first module in going out
				m_actualloadedmodulusS.set(lastIndexMem, -1);
				m_matrixS.set(lastIndexMem, null);
			}
			// move all other modulus in the memory
			for(int i2Mem=m_nMemory-2; i2Mem>=0; i2Mem--){
				int i2Mod = m_actualloadedmodulusS.get(i2Mem);
				if(i2Mod==-1){
					continue;
				} else {
					ArrayList<float[]> filterlogAux2 = m_matrixS.get(i2Mem);
					m_matrixS.set(i2Mem+1, filterlogAux2);
					m_actualloadedmodulusS.set(i2Mem+1, i2Mod);
					m_matrixS.set(i2Mem, null);
					m_actualloadedmodulusS.set(i2Mem, -1);
				}
			}
		}
		if(m_maAL.size()>0){
			m_matrixS.set(0, m_maAL);
			m_actualloadedmodulusS.set(0, m_actualModulusInMemory);
		}
		
		String outputfilename = "_" + mod + "_" + m_basenamejavadata;
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(m_workdir + "/" + outputfilename);
		} catch (FileNotFoundException ex){
			System.err.println("[MatrixStructure.loadModulus] " +
					"Problems at opening the file: " + outputfilename + " to read.");
			ex.printStackTrace();
			System.exit(1);
		}
		
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(fis);
			Object[] objA = (Object[])ois.readObject();
			m_maAL = (ArrayList<float[]>)objA[0];
		} catch(IOException ex){
			System.err.println("[MatrixStructure.loadModulus] " +
					"Problems at reading the file: " + outputfilename);
			ex.printStackTrace();
			System.exit(1);
		} catch(ClassNotFoundException ex){
			System.err.println("[MatrixStructure.loadModulus] " +
					"Problems at casting to a specific object: " + outputfilename);
			ex.printStackTrace();
			System.exit(1);
		}
		
		try{
			ois.close();
		} catch(IOException ex){
			System.err.println("[MatrixStructure.loadModulus] " +
					"Problems closing the file: " + outputfilename + " after reading.");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// verbose
		long endtime = System.currentTimeMillis();
		System.out.print("  [" + endtime + "] Load modulus " + mod +
				" to the hard drive. " + (endtime-starttime)/1000 + " seconds. ");
		System.out.print("[" + m_actualModulusInMemory + "] (" +
				m_actualloadedmodulusS.get(0));
		for(int i=1; i<m_actualloadedmodulusS.size(); i++){
			System.out.print("," + m_actualloadedmodulusS.get(i));
		}
		System.out.println(")");
		
		// update the indexes
		m_actualModulusInMemory = mod;
	}
	
}
