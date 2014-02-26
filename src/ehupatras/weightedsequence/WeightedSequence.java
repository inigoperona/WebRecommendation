package ehupatras.weightedsequence;

import java.util.HashMap;

public class WeightedSequence {
	private String[][] alignment;
	private String[] alphabet;
	private Float[][] weights;
	private Float k;
	private String m_gap = "-";

	public WeightedSequence() {
		this.alignment = null;
		this.alphabet = null;
		k = 0.0f;
	}

	public void setAlignment(String[][] alignment) {
		// define the gap length
		int gaplen = alignment[0][0].length();
		m_gap = "";
		for(int i=0; i<gaplen; i++){ m_gap = m_gap + "-";}
		
		
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

	public void setK(Float k) {
		this.k = k;
	}

	public void printAlphabet() {
		if (this.alphabet == null || this.alphabet.length == 0)
			return;
		for (String a : this.alphabet) {
			System.out.println(a);
		}
	}

	public void calculateWeights() throws Exception {
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
			int symbols=0;
			for(Object a:freq.keySet()){
				if(a.equals(m_gap)==false){
					symbols+=freq.get(a);
				}
			}
			int i = 0;
			for (String a : this.alphabet) {
				int anum=0;
				if(freq.get(a)!=null){
					anum=freq.get(a);
				}
				this.weights[i][j] =  ((float)anum /symbols );
				i++;
			}
		}
	}

	public void printAlighment() {
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
}
