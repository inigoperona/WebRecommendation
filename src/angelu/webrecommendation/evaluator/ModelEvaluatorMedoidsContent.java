package angelu.webrecommendation.evaluator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileNotFoundException;
import java.io.IOException;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.webrecommendation.distmatrix.Matrix;
import ehupatras.webrecommendation.evaluator.ModelEvaluatorMedoids;
import ehupatras.webrecommendation.evaluator.test.TestSetEvaluator;

public class ModelEvaluatorMedoidsContent 
				extends ModelEvaluatorMedoids {

	// ATTRIBUTES
	
	private String m_enrichementStrategy = "ContentsA1";
	private int m_nURLs = 0;
	private float[][] m_UrlSimilarityMatrix = null;
	private String[][] m_UrlRelationMatrix = null;
	private URLconverterUsaCon m_conv = null; 
	
	
	// CREATOR
	
	public ModelEvaluatorMedoidsContent(
			ArrayList<String[]> dataset,
			ArrayList<String[]> datasetSplit,
			Matrix dm,
			ArrayList<ArrayList<Long>> trainAL,
			ArrayList<ArrayList<Long>> valAL,
			ArrayList<ArrayList<Long>> testAL,
			int modePrRe,
			ArrayList<Integer> noProposeURLs){
		super(dataset, datasetSplit, dm, trainAL, valAL, testAL, modePrRe, noProposeURLs);
	}
	
	// GET TEST EVALUATOR
	
	public TestSetEvaluator getTestSetEvaluator(
			int iFold, 
			ArrayList<String[]> testseqs){
		TestSetEvaluator eval = 
				new TestSetEvaluatorMedContent(
						testseqs, m_modePrRe,
						m_medoidsAL.get(iFold),
						m_gmedoidsAL.get(iFold),
						m_recosAL.get(iFold),
						m_isDistance, m_rolesW, m_knn,
						m_enrichementStrategy,
						m_nURLs,
						m_UrlSimilarityMatrix,
						m_UrlRelationMatrix,
						m_UrlClusteringDict,
						m_conv,
						m_noProposeURLs);
		return eval;
	}
	
	public void setEsploitationParameters(
			String selectedEnrichementStrategy,
			String resSimilarityFile,
			String resRelationsFile,
			String m_usage2contentFile){
		m_enrichementStrategy = selectedEnrichementStrategy;
		this.readSimilarityMatrix(resSimilarityFile);
		this.readRelationsMatrix(resRelationsFile);
		m_nURLs = m_UrlSimilarityMatrix.length;
		m_conv = new URLconverterUsaCon(m_usage2contentFile);
	}
	
	private ArrayList<String> readLineByLine(String filename){
		ArrayList<String> linebyline = new ArrayList<String>();
		FileInputStream fstream = null;
		try{
			fstream = new FileInputStream(filename);
		} catch (FileNotFoundException ex){
			System.err.println(
					"[angelu.webrecommendation.evaluator." +
					"ModelEvaluatorMedoidsContent.readLineByLine] " +
					"File not found: " + filename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;
		try{
			while ((line= br.readLine())!= null){
				linebyline.add(line);
			}
			br.close();
		} catch(IOException ex){
			System.err.println(
					"[angelu.webrecommendation.evaluator." +
					"ModelEvaluatorMedoidsContent.readLineByLine] " +
					"Problems reading the file: " + filename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		return linebyline;
	}
	
	private void readSimilarityMatrix(String similarityMatrix){
		ArrayList<String> linebyline = this.readLineByLine(similarityMatrix);
		// parse the lines
		int nURL = linebyline.size();
		m_UrlSimilarityMatrix = new float[nURL][nURL];
		for(int i=0; i<linebyline.size(); i++){
			String line = linebyline.get(i);
			String[] lineA = line.split(";");
			for(int j=0; j<lineA.length; j++){
				m_UrlSimilarityMatrix[i][j] = this.parseToFloat(lineA[j], '.'); 
			}
		} 
	}
	
	private float parseToFloat(String floatStr, char sep){
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator(sep);
		DecimalFormat format = new DecimalFormat();
		format.setDecimalFormatSymbols(symbols);
		float f = -1f;
		try {
			f = format.parse(floatStr).floatValue();
		} catch(ParseException ex){
			System.err.println(
					"[angelu.webrecommendation.evaluator." +
					"RecommenderKnnToClustersTopURLsAndContentsA1] " +
					"Problems parsing to float: " + floatStr);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		return f;
	}
	
	private void readRelationsMatrix(String resRelationsFile){
		ArrayList<String> linebyline = this.readLineByLine(resRelationsFile);
		// parse the lines
		int nURL = linebyline.size();
		m_UrlRelationMatrix = new String[nURL][nURL];
		for(int i=0; i<linebyline.size(); i++){
			String line = linebyline.get(i);
			String[] lineA = line.split(";");
			for(int j=0; j<lineA.length; j++){
				m_UrlRelationMatrix[i][j] = lineA[j]; 
			}
		}
	}
	
}
