package ehupatras.ncd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

// TODO: Auto-generated Javadoc
/**
 * The Class NcdGzip.
 */
public class NcdGzip extends NCD {

	/**
	 * Instantiates a new ncd gzip.
	 *
	 * @param nbytes the nbytes
	 */
	public NcdGzip(int nbytes){
		super(nbytes);
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.ncd.NCD#compress(int[])
	 */
	protected int[] compress(int[] intA){
		// save the original size in bytes of intA
		int origLen = 0;
		
		// to store the compressed byte-array
		ByteArrayOutputStream out = new ByteArrayOutputStream(m_buffersize); 
		
		// compress
		GzipCompressorOutputStream bzOut = null;		
		try{
			bzOut = new GzipCompressorOutputStream(out);
			for(int i=0; i<intA.length; i++){
				int integer = intA[i];
				byte[] bytes = this.int2byteA(integer);
				bzOut.write(bytes);
				origLen = origLen + bytes.length;
			}
			out.close();
			bzOut.close();
		} catch (IOException ex){
			System.err.println("IOException at compressing.");
			ex.printStackTrace();
			System.exit(1);
		}
		
		// compression rate
		byte[] bytesA2 = out.toByteArray();
		int[] re = new int[2];
		re[0] = origLen;
		re[1] = bytesA2.length;
		return re;
	}
	
}
