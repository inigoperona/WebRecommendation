package ehupatras.webrecommendation.evaluator;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.utils.SaveLoadObjects;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorClust.
 */
public abstract class ModelEvaluatorClust 
				extends ModelEvaluator {

	// ATTRIBUTES
	
	/** The m_clusters al. */
	protected ArrayList<int[]> m_clustersAL;
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator clust.
	 *
	 * @param dataset the dataset
	 * @param datasetSplit the dataset split
	 * @param dm the dm
	 * @param trainAL the train al
	 * @param valAL the val al
	 * @param testAL the test al
	 * @param modePrRe the mode pr re
	 * @param usage2contentFile the usage2content file
	 * @param resSimilarityFile the res similarity file
	 */
	protected ModelEvaluatorClust(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL,
			int modePrRe,
			String usage2contentFile,
			String resSimilarityFile){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe, usage2contentFile, resSimilarityFile);
	}
	
	// Clustering utils
	
	/**
	 * Write clusters.
	 *
	 * @param outfilename the outfilename
	 */
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

	/**
	 * Clustering to string.
	 *
	 * @param indexFold the index fold
	 * @return the string[]
	 */
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
	
	/**
	 * Save clusters.
	 *
	 * @param outfilename the outfilename
	 */
	public void saveClusters(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		so.save(m_clustersAL, outfilename);
	}
	
	/**
	 * Load clusters.
	 *
	 * @param outfilename the outfilename
	 */
	public void loadClusters(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		m_clustersAL = (ArrayList<int[]>)so.load(outfilename);
	}
	
	/**
	 * Gets the clusters.
	 *
	 * @return the clusters
	 */
	public ArrayList<int[]> getClusters(){
		return m_clustersAL;
	}
	
}
