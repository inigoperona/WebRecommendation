package ehupatras.webrecommendation.evaluator;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ModelEvaluatorSeqMin 
				extends ModelEvaluatorSuffixTree {

	// ATTRIBUTES
	
	protected ArrayList<int[]> m_clustersAL;
	protected float m_minsupport = (float)0.25;
	protected ArrayList<ArrayList<String[]>> m_minedSeqs;
	protected String m_minSeqsFileTxt = null;
	protected String m_minSeqsFileJavaData = null;
	
	// CREATOR
	
	protected ModelEvaluatorSeqMin(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL);
	}
	
	public ModelEvaluatorSeqMin(){
		// creator to load clusters
		super(null, null, null, 
				new ArrayList<ArrayList<Long>>(0), 
				new ArrayList<ArrayList<Long>>(0), 
				new ArrayList<ArrayList<Long>>(0));
	}
	
	// FUNCTIONS
	
	public void loadClusters(String clustFile){
		ModelEvaluatorClust modelClust = new ModelEvaluatorClust();
		modelClust.loadClusters(clustFile);
		m_clustersAL = modelClust.getClusters();
	}
	
	public void buildST(){
		super.buidSuffixTrees(m_minedSeqs);
	}
	
	// Mined Sequences utilities
	
	protected void writeWeightedSequences(String outfilename){
		// Open the given file
		BufferedWriter writer = null;
		try{
			writer = new BufferedWriter(new FileWriter(outfilename));
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.evaluator.ModelEvaluatorSeqMin] " +
					"Not possible to open the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// Write the sequences in a file line by line
		try{
			for(int i=0; i<m_nFolds; i++){
				ArrayList<String[]> wsFold = m_minedSeqs.get(i); 
				for(int j=0; j<wsFold.size(); j++){
					String[] seq = wsFold.get(j);
					String line = "fold:" + i + ":";
					line = line + seq[0];
					for(int k=1; k<seq.length; k++){
						line = line + "," + seq[k];
					}
					line = line + "\n";
					writer.write(line);
				}
			}
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.evaluator.ModelEvaluatorSeqMin] " +
					"Problems writing to the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		// close the file
		try{
			writer.close();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.evaluator.ModelEvaluatorSeqMin] " +
					"Problems at closing the file: " + outfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	protected void saveMinedSeqs(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		so.save(m_minedSeqs, outfilename);
	}
	
	protected void loadMinedSeqs(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		m_minedSeqs = (ArrayList<ArrayList<String[]>>)so.load(outfilename);
	}
	
}
