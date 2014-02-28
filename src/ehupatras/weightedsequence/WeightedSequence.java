package ehupatras.weightedsequence;

import java.util.HashMap;
import java.util.Set;

public class WeightedSequence {
	private String[][] alignment;
	private String[] alphabet;
	private Float[][] weights;
	private Float k;
	private String m_gap = "-";
	HashMap<String, Float> generatedStrings = new HashMap<String, Float>();

	public WeightedSequence(String[][] alignmentIn, Float rate) {
		this.alignment = null;
		this.alphabet = null;
		k = rate;
		setAlignment(alignmentIn);
	}
	
	private void setAlignment(String[][] alignment) {
		// define the gap length
		int gaplen = alignment[0][0].length();
		m_gap = "";
		for (int i = 0; i < gaplen; i++) {
			m_gap = m_gap + "-";
		}

		// set alignmetn
		this.alignment = alignment;
		HashMap<String, String> pages = new HashMap<String, String>();
		for (int i = 0; i < this.alignment.length; i++) {
			for (int j = 0; j < this.alignment[i].length; j++) {
				String p = this.alignment[i][j];
				pages.put(p, "");
			}
		}
		pages.remove(m_gap);
		this.alphabet = new String[pages.keySet().size()];
		int i = 0;
		for (Object s : pages.keySet()) {

			this.alphabet[i++] = s.toString();
		}

	}

	private void calculateWeights() throws Exception {
		if (this.alphabet == null || this.alphabet.length == 0
				|| this.alignment == null || this.alignment.length == 0) {
			throw new Exception("Can not Calculate Weights");
		}
		this.weights = new Float[this.alphabet.length][this.alignment[0].length];
		int rows = this.alignment.length;
		int columns = this.alignment[0].length;
		for (int j = 0; j < columns; j++) {
			HashMap<String, Integer> freq = new HashMap<String, Integer>();
			for (int i = 0; i < rows; i++) {
				if (freq.containsKey(alignment[i][j]) == true) {
					freq.put(alignment[i][j], freq.get(alignment[i][j]) + 1);
				} else {
					freq.put(alignment[i][j], 1);
				}
			}
			int symbols = 0;
			for (Object a : freq.keySet()) {
				if (a.equals(m_gap) == false) {
					symbols += freq.get(a);
				}
			}
			int i = 0;
			for (String a : this.alphabet) {
				int anum = 0;
				if (freq.get(a) != null) {
					anum = freq.get(a);
				}
				this.weights[i][j] = ((float) anum / symbols);
				i++;
			}
		}
	}

	
	public void process() throws Exception {	
		calculateWeights();	
		generateStrings();				
	}
	////////////////////// Print Methods ///////////////////////////////////////////////////

	private void printAlphabet() {
		if (this.alphabet == null || this.alphabet.length == 0)
			return;
		for (String a : this.alphabet) {
			System.out.println(a);
		}
	}
	
	
	private void printAlighment() {
		int rows = this.alignment.length;
		int columns = this.alignment[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				System.out.print(alignment[i][j] + " ");
			}
			System.out.println("");
		}
	}

	public void printWeights() {
		int rows = this.weights.length;
		int columns = this.weights[0].length;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				System.out.print(weights[i][j] + " ");
			}
			System.out.println("");
		}
	}
	
	private void printGeneratedStrings() {
		System.out.println("--------------------------------");
		for (String s : generatedStrings.keySet()) {
			System.out.println(s + ":" + generatedStrings.get(s));
		}
		System.out.println("--------------------------------");
	}

	
////////////////////String Generation Methods//////////////////////////////////////////////////

	public void generateStrings() {
		this.generatedStrings.clear();
		int rows = this.weights.length;
		int columns = this.weights[0].length;
		for (int j = 0; j < columns - 1; j++) {
			// System.out.println("==========================================");
			// System.out.println("Starting Pos:"+j);
			// System.out.println("==========================================");
			HashMap<String, Float> posGenStrings = new HashMap<String, Float>();
			for (int i = 0; i < rows; i++) {
				if (weights[i][j] >= k)
					posGenStrings.put(this.alphabet[i], weights[i][j]);
			}
			expandStrings(posGenStrings, j + 1);

		}
	}

	private void expandStrings(HashMap<String, Float> posGenStrings, int offset) {
		/*
		 * System.out.println("--------------------------------"); for(String
		 * s:posGenStrings.keySet()){ System.out.println(s +":"+
		 * posGenStrings.get(s)); }
		 * System.out.println("--------------------------------");
		 */
		int rows = this.weights.length;
		int columns = this.weights[0].length;
		HashMap<String, Float> extendedStrings = new HashMap<String, Float>();
		for (String s : posGenStrings.keySet()) {
			Float score = posGenStrings.get(s);
			boolean extended = false;
			for (int i = 0; i < rows; i++) {
				if (score * weights[i][offset] >= k) {
					extendedStrings.put(s + this.alphabet[i], score
							* weights[i][offset]);
					extended = true;
				}
			}

			if (score >= k && extended == false) {
				this.generatedStrings.put(s, score);
				//System.out.println("PUTTING:" + s + ", score:" + score);

			}

		}

		if (extendedStrings.size() > 0 && offset + 1 < columns) {
			expandStrings(extendedStrings, offset + 1);
		} else {
			if (extendedStrings.size() > 0) {
				for (String s : extendedStrings.keySet()) {
					Float score = extendedStrings.get(s);
					generatedStrings.put(s, score);
					//System.out.println("PUTTING:" + s + ", score:" + score);
				}
			}

		}

	}

	


	public String[] getGeneratedStrings(){
		String[] answer=new String[this.generatedStrings.keySet().size()];
		int i=0;
		for (String s:this.generatedStrings.keySet()){
			answer[i++]=s;
		}
		return answer;
	}
}
