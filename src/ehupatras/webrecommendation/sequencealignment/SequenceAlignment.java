package ehupatras.webrecommendation.sequencealignment;

public interface SequenceAlignment {
	public float getScore(String[] seqA, String[] seqB);
	public float getTweakedScore(String[] seqA, String[] seqB);
	public float getTweakedDistance(String[] seqA, String[] seqB);
}
