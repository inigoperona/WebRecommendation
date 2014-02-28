package ehupatras.webrecommendation.distmatrix;

import ehupatras.webrecommendation.utils.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Matrix {
	protected float[][] m_matrix;
	protected ArrayList<Integer> m_names;
	private String m_savefilename = "/_matrix.javaData";
	
	public abstract void computeMatrix(ArrayList<Integer> names, ArrayList<String[]> data);
	
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
}
