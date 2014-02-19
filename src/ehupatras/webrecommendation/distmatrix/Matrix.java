package ehupatras.webrecommendation.distmatrix;

import ehupatras.webrecommendation.utils.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Matrix {
	protected float[][] m_matrix;
	private String m_savefilename = "/_matrix.javaData";
	
	public abstract void computeMatrix(ArrayList<String[]> data);
	
	public float[][] getMatrix(){
		return m_matrix;
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
		slo.save(m_matrix, wordirectory + m_savefilename);
	}
	
	public void load(String wordirectory){
		SaveLoadObjects slo = new SaveLoadObjects();
		m_matrix = (float[][])slo.load(wordirectory + m_savefilename);
	}
}
