package ehupatras.suffixtree.stringarray;

import java.util.*;

public class StringArray {
	private ArrayList<String> m_strarray;
	
	// Constructors 
	
	public StringArray(){
		m_strarray = new ArrayList<String>();
	}
	
	public StringArray(String[] strArray){
		this();
		for(int i=0; i<strArray.length; i++){
			m_strarray.add(strArray[i]);
		}
	}
	
	public StringArray(ArrayList<String> strList){
		m_strarray = strList;
	}
	
	// Functions
	
	public int length(){
		return m_strarray.size();
	}
	
	public String charAt(int index){
		return m_strarray.get(index);
	}
	
	public boolean regionMatches(	int toffset,
            						StringArray other,
            						int ooffset,
            						int len){
		// if the main string is smaller. There are not equals.
		if(m_strarray.size()<len){ return false; }
		
		// See if they are equals
		boolean equalregion = true;
		for(int i=0; i<len; i++){
			if(!(m_strarray.get(i+toffset)).equals(other.charAt(i))){
				equalregion = false;
				break;
			}
		}
		return equalregion;
	}
	
	public StringArray concat(String str){
		m_strarray.add(str);
		return this;
	}
	
	public StringArray concat(StringArray strarray){
		for(int i=0; i<strarray.length(); i++){
			m_strarray.add(strarray.charAt(i));
		}
		return this;
	}
	
	public StringArray substring(int indexStart, int indexEndNoInclusive){
		ArrayList<String> sublist = new ArrayList<String>();
		for(int i=indexStart; i<indexEndNoInclusive; i++){
			String elem = m_strarray.get(i);
			sublist.add(elem);
		}
		return (new StringArray(sublist));
	}
	
	public StringArray substring(int index){
		return this.substring(index, m_strarray.size());
	}
	
	public boolean startsWith(StringArray str){
		return this.regionMatches(0, str, 0, str.length());
	}
	
	public String toString(){
		String str = "";
		for(int i=0; i<m_strarray.size(); i++){
			str = str + m_strarray.get(i);
		}
		return str;
	}
	
}
