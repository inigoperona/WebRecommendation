package ehupatras.webrecommendation.distmatrix;

public class MatrixStructure {

	// distance matrix data
	/** The m_matrix. */
	protected float[][] m_m = null;
	
	protected int m_length = -1;
	
	
	public MatrixStructure(int size){
		m_length = size;
		m_m = new float[size][size];
	}
	
	public MatrixStructure(float[][] fmatrix){
		m_length = fmatrix.length;
		m_m = fmatrix;
	}
	
	public float getCell(int i, int j){
		return m_m[i][j];
	}
	
	public void setCell(int i, int j, float value){
		m_m[i][j] = value;
	}
	
	public int getLength(){
		return m_length;
	}
	
}
