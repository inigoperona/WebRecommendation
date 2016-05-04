package ehupatras.webrecommendation.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectOutput;
import java.io.ByteArrayOutputStream;


// TODO: Auto-generated Javadoc
/**
 * The Class SaveLoadObjects.
 */
public class SaveLoadObjects {

	/**
	 * Save.
	 *
	 * @param obj the obj
	 * @param output the output
	 */
	public void save(Object obj, String output){
		// Write to disk with FileOutputStream
		FileOutputStream f_out = null;
		try{
			f_out = new FileOutputStream(output);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.utils.SaveLoadObjects.save] " +
					"Problems at opening the file: " + output + " to write.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
			
		// Write object with ObjectOutputStream
		// Write object out to disk
		ObjectOutputStream obj_out = null;
		try{
			obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject( obj );
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.utils.SaveLoadObjects.save] " +
					"Problems at writing the file: " + output);
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		// close
		try{
			obj_out.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.utils.SaveLoadObjects.save] " +
					"Problems closing the file: " + output + " after writing.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
	}
	
	public int getSize(Object obj){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput obj_out = null;
		byte[] objBytesA = null;
		try{
			obj_out = new ObjectOutputStream(bos);
			obj_out.writeObject( obj );
			objBytesA = bos.toByteArray();
		} catch (IOException ex){
			System.err.println("[ehupatras.webrecommendation.utils.getSize] writing.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}

		// close
		try{
			obj_out.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.utils.getSize] closing.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		return objBytesA.length;
	}
	
	/**
	 * Load.
	 *
	 * @param outputfilename the outputfilename
	 * @return the object
	 */
	public Object load(String outputfilename){
		Object obj = null;
		
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(outputfilename);
		} catch (FileNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.utils.SaveLoadObject.load] " +
					"Problems at opening the file: " + outputfilename + " to read.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		ObjectInputStream ois = null;
		try{
			ois = new ObjectInputStream(fis);
			obj = (Object)ois.readObject();
		} catch(IOException ex){
			
		} catch(ClassNotFoundException ex){
			System.err.println("[ehupatras.webrecommendation.utils.SaveLoadObject.load] " +
					"Problems at reading the file: " + outputfilename);
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		try{
			ois.close();
		} catch(IOException ex){
			System.err.println("[ehupatras.webrecommendation.utils.SaveLoadObject.load] " +
					"Problems closing the file: " + outputfilename + " after reading.");
			System.err.println(ex.getMessage());
			System.exit(1);
		}
		
		return obj;
	}
	
}
