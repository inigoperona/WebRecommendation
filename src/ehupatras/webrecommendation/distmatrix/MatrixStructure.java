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
	
	// distance matrix in vector
	private boolean m_createVectorMatrix = false;
	protected float[] m_maArray = null;
	
	// distance matrix in memory
	private float[][] m_maA2 = null;
	private ArrayList<Integer> m_indexesA2 = null;
	private boolean m_inMemory = false;
	
	// swapping attributes
	private int m_maxLoadedSequences = 1000; //1000
	private int m_lastModulusLastIndex = 0;
	private int m_writtenModulus = 0;
	private int m_actualModulusInMemory = -1;
	private String m_basenamejavadata = "dm.javaData";
	private String m_workdir = ".";
	// swapping memory
	private static int m_nMemory = 10; //10
	private static ArrayList<ArrayList<float[]>> m_matrixS = null;
	private static ArrayList<Integer> m_actualloadedmodulusS = null;

	
	public MatrixStructure(boolean createVectorMatrix, int size, String workdirectory){
		m_createVectorMatrix = createVectorMatrix;
		if(!createVectorMatrix){
			m_length = size;
			m_maAL = new ArrayList<float[]>();
			m_workdir = workdirectory;
			this.initmodulus();
		} else {
			m_length = size;
			m_workdir = workdirectory;
			
			// initialize the vector matrix
			float vectorLen = (float)(size+1)*((float)size/2f);
			int vectorLenInt = Math.round(vectorLen);
			m_maArray = new float[vectorLenInt];
		}
	}
	
	public MatrixStructure(boolean createVectorMatrix, float[][] fmatrix, String workdirectory){
		m_createVectorMatrix = createVectorMatrix;
		if(!createVectorMatrix){
			m_maxLoadedSequences = 1000000;
			m_workdir = workdirectory;
			m_length = fmatrix.length;
			m_maAL = new ArrayList<float[]>();
			for(int i=0; i<m_length; i++){
				for(int j=0; j<m_length; j++){
					this.addCell(i, j, fmatrix[i][j]);
				}
			}
		} else {
			m_length = fmatrix.length;
			m_workdir = workdirectory;
			
			// initialize the vector matrix
			float vectorLen = (float)(m_length+1)*((float)m_length/2f);
			int vectorLenInt = Math.round(vectorLen);
			m_maArray = new float[vectorLenInt];
			int kont = 0;
			for(int i=0; i<m_length; i++){
				for(int j=i; j<m_length; j++){
					m_maArray[kont] = fmatrix[i][j];
					kont++;
				}
			}
		}
	}

	public void setLoadInMemory(int[] indexesA, String workdirectory){
		// the same as in creator
		m_length = indexesA.length;
		m_maAL = new ArrayList<float[]>();
		m_workdir = workdirectory;
		m_actualModulusInMemory = -1;
		this.initmodulus();
		
		// initialize in memory
		m_inMemory = false;
		m_indexesA2 = null;
		
		// create float[][] matrix
		int n = indexesA.length;
		float[][] mA2 = new float[n][n];
		for(int i=0; i<n; i++){
			int ind1 = indexesA[i]; // get row
			for(int j=0; j<n; j++){
				int ind2 = indexesA[j]; // get col
				float val = this.getCell(ind1, ind2);
				mA2[i][j] = val;
			}
		}
		
		// save indexes
		m_indexesA2 = new ArrayList<Integer>();
		for(int i=0; i<indexesA.length; i++){
			int rowi = indexesA[i];
			m_indexesA2.add(rowi);
		}
		m_length = m_indexesA2.size();
		
		// matrixes
		m_maA2 = mA2;
		m_maAL = null;
		
		// boolean to control if we are loading in the memory
		m_inMemory = true;
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
		if(m_inMemory){
			return m_maA2[i][j];
		} else {
			// real indexes
			int i2 = i; 
			int j2 = j;
			
			// compute modulus indexes
			int imodulus = i2 / m_maxLoadedSequences;
			int iindex = i2 % m_maxLoadedSequences;
			
			// if we have in memory return it
			if(m_matrixS==null){
				this.initmodulus();
			}
			int iMem = m_actualloadedmodulusS.indexOf(new Integer(imodulus));
			if(iMem!=-1){
				return m_matrixS.get(iMem).get(iindex)[j2];
			}
			
			// if we want data from the last modul take from the write-structure
			if(imodulus!=m_actualModulusInMemory){
				this.loadModulus(imodulus);
			}
			
			// return structure
			return m_maAL.get(iindex)[j2];
		}
	}
	
	public void setCell(int i, int j, double d){
		if(m_inMemory){
			m_maA2[i][j] = (float)d;
		} else {
			// real indexes
			int i2 = i; 
			int j2 = j;
						
			// compute modulus indexes
			int imodulus = i2 / m_maxLoadedSequences;
			int iindex = i2 % m_maxLoadedSequences;
			
			// if we have in memory return it
			int iMem = m_actualloadedmodulusS.indexOf(new Integer(imodulus));
			if(iMem!=-1){
				ArrayList<float[]> rows = m_matrixS.get(iMem);
				float[] row = rows.get(iindex);
				row[j2] = (float)d;
				rows.set(iindex, row);
			} else {
				// do the set part
			}
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
