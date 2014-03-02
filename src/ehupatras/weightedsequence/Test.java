package ehupatras.weightedsequence;

import java.util.ArrayList;

public class Test {

	public static void main(String[] args) throws Exception {
		
		String[][] alighment=new String[3][4];
		alighment[0]=new String[]{"A1","A1","C1","B1"};
		alighment[1]=new String[]{"B1","A1","--","C1"};
		alighment[2]=new String[]{"--","--","C1","C1"};

		WeightedSequence ws=new WeightedSequence(alighment, 0.25f);
		ws.process();
		String[] generatedStrings=ws.getGeneratedStrings();
		
		for(String s:generatedStrings){
			System.out.println(s);
		}
		
		ArrayList<String[]> seqL = ws.getGeneratedSequences();
		for(int i=0; i<seqL.size(); i++){
			String[] seq = seqL.get(i);
			for(int j=0; j<seq.length; j++){System.out.print("." + seq[j]);}
			System.out.println();
		}
	}

}
