package ehupatras.webrecommendation.evaluator;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.utils.SaveLoadObjects;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public abstract class ModelEvaluatorClust 
				extends ModelEvaluator {

	// ATTRIBUTES
	
	protected ArrayList<int[]> m_clustersAL;
	
	// CREATOR
	
	protected ModelEvaluatorClust(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL);
	}
	
	// Clustering utils
	
	public void writeClusters(String outfilename){
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Write the sequences in a file line by line
		try{
			for(int i=0; i<m_nFolds; i++){
				String[] clusteringStrA = this.clusteringToString(i);
				for(int j=0; j<clusteringStrA.length; j++){
					String line = clusteringStrA[j];
					writer.write(String.valueOf(line));
				}
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeFilteredLog] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.structures.WebAccessSequences.writeSequences] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}

	private String[] clusteringToString(int indexFold){
		int[] clustersID = m_clustersAL.get(indexFold);
		ArrayList<Long> trainsetnames = m_trainAL.get(indexFold);
		ArrayList<Long> trainsetnames2 = 
				m_distancematrix.getSessionIDs(trainsetnames, m_datasetSplit!=null);
		
		String[] strA = new String[clustersID.length];
		for(int i=0; i<clustersID.length; i++){
			strA[i] = "fold:" + indexFold + ":" + 
			trainsetnames2.get(i) + ":" + clustersID[i] + "\n";
		}
		return strA;
	}
	
	public void saveClusters(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		so.save(m_clustersAL, outfilename);
	}
	
	public void loadClusters(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		m_clustersAL = (ArrayList<int[]>)so.load(outfilename);
	}
	
	public ArrayList<int[]> getClusters(){
		return m_clustersAL;
	}
	
}
