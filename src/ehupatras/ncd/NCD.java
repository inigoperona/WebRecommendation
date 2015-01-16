package ehupatras.ncd;

// TODO: Auto-generated Javadoc
/**
 * The Class NCD.
 */
public abstract class NCD {
	
	// number of bytes we are going to use to represent an integer
	/** The m_nbytes. */
	protected int m_nbytes = 1;
	
	// in 1000 bytes we can save, for example 250 integers of 4 bytes
	/** The m_buffersize. */
	protected int m_buffersize = 10000;
	
	/**
	 * Instantiates a new ncd.
	 *
	 * @param nbytes the nbytes
	 */
	public NCD(int nbytes){
		m_nbytes = nbytes;
	}
	
	/**
	 * Compress.
	 *
	 * @param intA the int a
	 * @return the int[]
	 */
	protected abstract int[] compress(int[] intA);
	
	/**
	 * Gets the ncd.
	 *
	 * @param intA1 the int a1
	 * @param intA2 the int a2
	 * @return the ncd
	 */
	public float getNCD(int[] intA1, int[] intA2){
		int[] re1 = this.compress(intA1);
		int l1Orig = re1[0];
		int l1Zip = re1[1];
		
		int[] re2 = this.compress(intA2);
		int l2Orig = re2[0];
		int l2Zip = re2[1];
		
		int[] intA12 = new int[intA1.length+intA2.length];
		for(int i=0; i<intA1.length; i++){ intA12[i] = intA1[i]; }
		for(int i=0; i<intA2.length; i++){ intA12[intA1.length+i] = intA2[i]; }
		int[] re12 = this.compress(intA12);
		int l12Orig = re12[0];
		int l12Zip = re12[1];
		
		// compute ncd
		float ncd = ((float)l12Zip - (float)Math.min(l1Zip, l2Zip)) / 
				(float)Math.max(l1Zip, l2Zip);
		
		//System.out.print( l1Orig + " : " + l2Orig + " : " + l12Orig + " ::: ");
		//System.out.print( l1Zip + " : " + l2Zip + " : " + l12Zip);
		//System.out.println( " = " + ncd );
		
		return ncd;
	}
	
	
	/**
	 * Int2byte a.
	 *
	 * @param integer the integer
	 * @return the byte[]
	 */
	protected byte[] int2byteA(int integer){
		//int nbytes = this.intLog(integer, 256);
		return this.int2byteA(integer, m_nbytes);
	}
	
	/**
	 * Int log.
	 *
	 * @param value the value
	 * @param base the base
	 * @return the int
	 */
	private int intLog(int value, int base){
		int pow = 1;
		while(true){
			int value2 = (int)Math.pow(base, pow);
			if(value2>value){
				return pow;
			}
			pow++;
		}
	}
	
	/**
	 * Int2byte a.
	 *
	 * @param integer the integer
	 * @param allocate the allocate
	 * @return the byte[]
	 */
	private byte[] int2byteA(int integer, int allocate){
		byte[] rbytes = new byte[allocate];
		for(int i=0; i<allocate; i++){
			int move = allocate - (i+1);
			rbytes[i] = (byte)(integer >>> move*8);
		}
		return rbytes;
	}
	
	/**
	 * Prints the byte array.
	 *
	 * @param bytes the bytes
	 */
	private void printByteArray(byte[] bytes){
		for(byte b : bytes) {
			System.out.format("0x%x ", b);
		}
		System.out.println();
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		
		// NCD
		NCD d = new NcdBzip2(1);
		
		// converting integers to bytes
		for(int i=1000; i<1020; i++){
			System.out.print(i + " : ");
			byte[] bA1 = d.int2byteA(i);
			d.printByteArray(bA1);
		}
		
		// NCD distance
		int[] intA0 = new int[]{0,1,2,3,4,5}; // each int 1 byte
		int[] intA1 = new int[]{120,121,122,123,124,125}; // each int 1 byte
		int[] intA2 = new int[]{128,129,130,131,132,133}; // each int 2 byte
		int[] intA3 = new int[]{256,257,258,259,260,261}; // each int 2 byte
		int[] intA4 = new int[]{0, 10, 100, 1000, 10000, 65535, 65536};
		int[] intA5 = new int[]{0, 256, 512, 768, 1024, 1280, 1536};
		
		// get the ncd distance
		float ncd0 = d.getNCD(intA0, intA0);
		float ncd1 = d.getNCD(intA1, intA1);
		float ncd2 = d.getNCD(intA2, intA2);
		float ncd3 = d.getNCD(intA3, intA3);
		float ncd5 = d.getNCD(intA5, intA5);
	}
	
}
