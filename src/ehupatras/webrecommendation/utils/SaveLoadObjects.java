package ehupatras.webrecommendation.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SaveLoadObjects {

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
			obj = ois.readObject();
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
