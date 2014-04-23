package ehupatras.ncd;

import org.apache.commons.compress.compressors.bzip2.*;
import org.apache.commons.compress.compressors.gzip.*;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

public class NCD {
	
	private int m_nbytes = 1;
	
	public NCD(int nbytes){
		m_nbytes = nbytes;
	}
	
	public float getNCD(int[] intA1, int[] intA2){
		int[] re1 = this.compressBzip2(intA1);
		int l1Orig = re1[0];
		int l1Zip = re1[1];
		
		int[] re2 = this.compressBzip2(intA2);
		int l2Orig = re2[0];
		int l2Zip = re2[1];
		
		int[] intA12 = new int[intA1.length+intA2.length];
		for(int i=0; i<intA1.length; i++){ intA12[i] = intA1[i]; }
		for(int i=0; i<intA2.length; i++){ intA12[intA1.length+i] = intA2[i]; }
		int[] re12 = this.compressBzip2(intA12);
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
	
	
	private byte[] int2byteA(int integer){
		//int nbytes = this.intLog(integer, 256);
		return this.int2byteA(integer, m_nbytes);
	}
	
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
	
	private byte[] int2byteA(int integer, int allocate){
		byte[] rbytes = new byte[allocate];
		for(int i=0; i<allocate; i++){
			int move = allocate - (i+1);
			rbytes[i] = (byte)(integer >>> move*8);
		}
		return rbytes;
	}
	
	private void printByteArray(byte[] bytes){
		for(byte b : bytes) {
			System.out.format("0x%x ", b);
		}
		System.out.println();
	}
	
	private int[] compressBzip2(int[] intA){
		// save the original size in bytes of intA
		int origLen = 0;
		
		// in 1000 bytes we can save, for example 250 integers of 4 bytes
		ByteArrayOutputStream out = new ByteArrayOutputStream(1000); 
		
		// compress
		BZip2CompressorOutputStream bzOut = null;		
		try{
			bzOut = new BZip2CompressorOutputStream(out);
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
	
	
	
	public static void main(String[] args){
		
		// NCD
		NCD d = new NCD(1);
		
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
