package ehupatras.webrecommendation.similaritymatrix;

import ehupatras.webrecommendation.sequencealignment.*;
import java.util.*;

public class SimilarityMatrix {
	
	public float[][] getSimilarityMatrix(ArrayList<String[]> data){
		float[][] similaritymatrix = new float[data.size()][data.size()];
		for(int i=0; i<data.size(); i++){
			String[] seqA = data.get(i);
			for(int j=0; j<data.size(); j++){
				String[] seqB = data.get(j);
				SequenceAlignmentCombineGlobalLocal seqalign = new SequenceAlignmentCombineGlobalLocal();
				similaritymatrix[i][j] = seqalign.getScore(seqA, seqB);
			}
		}
		return similaritymatrix;
	}
	
}
