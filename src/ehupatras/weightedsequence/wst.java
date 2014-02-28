package ehupatras.weightedsequence;

public class wst {

	public static void main(String[] args) throws Exception {
		
		String[][] alighment=new String[3][4];
		alighment[0]=new String[]{"A","A","C","B"};
		alighment[1]=new String[]{"B","A","-","C"};
		alighment[2]=new String[]{"-","-","C","C"};

		WeightedSequence ws=new WeightedSequence(alighment, 0.25f);
		ws.process();
		String[] generatedStrings=ws.getGeneratedStrings();
		
		for(String s:generatedStrings){
			System.out.println(s);
		}
		
		
	}

}
