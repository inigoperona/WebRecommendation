package ehupatras.webrecommendation.evaluator;

import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.utils.SaveLoadObjects;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class ModelEvaluatorSeqMin.
 */
public class ModelEvaluatorSeqMin 
				extends ModelEvaluatorSuffixTree {

	// ATTRIBUTES
	
	/** The m_minsupport. */
	protected float m_minsupport = (float)0.25;
	
	/** The m_mined seqs. */
	protected ArrayList<ArrayList<String[]>> m_minedSeqs;
	
	// CREATOR
	
	/**
	 * Instantiates a new model evaluator seq min.
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
	protected ModelEvaluatorSeqMin(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<String>> trainAL,
			ArrayList<ArrayList<String>> valAL,
			ArrayList<ArrayList<String>> testAL,
			int modePrRe,
			String usage2contentFile,
			String resSimilarityFile){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe, usage2contentFile, resSimilarityFile);
	}
	
	// FUNCTIONS
	
	/**
	 * Builds the st.
	 */
	public void buildST(){
		super.buidSuffixTrees(m_minedSeqs);
	}
	
	// Mined Sequences utilities
	
	/**
	 * Write weighted sequences.
	 *
	 * @param outfilename the outfilename
	 */
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
	
	/**
	 * Save mined seqs.
	 *
	 * @param outfilename the outfilename
	 */
	protected void saveMinedSeqs(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		so.save(m_minedSeqs, outfilename);
	}
	
	/**
	 * Load mined seqs.
	 *
	 * @param outfilename the outfilename
	 */
	protected void loadMinedSeqs(String outfilename){
		SaveLoadObjects so = new SaveLoadObjects();
		m_minedSeqs = (ArrayList<ArrayList<String[]>>)so.load(outfilename);
	}
	
}
