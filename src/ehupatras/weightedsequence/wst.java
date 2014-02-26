package ehupatras.weightedsequence;

public class wst {

	public static void main(String[] args) throws Exception {
		WeightedSequence ws=new WeightedSequence();
		String[][] alighment=new String[3][4];
		alighment[0]=new String[]{"A","A","C","B"};
		alighment[1]=new String[]{"B","A","-","C"};
		alighment[2]=new String[]{"-","-","C","C"};

		ws.setAlignment(alighment);
		ws.printAlphabet();
		ws.calculateWeights();
		ws.printAlighment();
		ws.printWeights();
	}

}
