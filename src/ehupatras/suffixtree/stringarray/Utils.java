 package ehupatras.suffixtree.stringarray;

import java.util.HashSet;
import java.util.Set;

// TODO: Auto-generated Javadoc
/**
 * The Class Utils.
 */
public class Utils {
    
    /**
     * Normalize an input string.
     *
     * @param in the input string to normalize
     * @return <tt>in</tt> all lower-case, without any non alphanumeric character
     */
    public static String normalize(String in) {
        StringBuilder out = new StringBuilder();
        String l = in.toLowerCase();
        for (int i = 0; i < l.length(); ++i) {
            char c = l.charAt(i);
            if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
                out.append(c);
            }
        }
        return out.toString();
    }

    /**
     * Computes the set of all the substrings contained within the <tt>str</tt>
     * 
     * It is fairly inefficient, but it is used just in tests ;).
     *
     * @param str the string to compute substrings of
     * @return the set of all possible substrings of str
     */
    public static Set<String> getSubstrings(String str) {
        Set<String> ret = new HashSet<String>();
        // compute all substrings
        for (int len = 1; len <= str.length(); ++len) {
            for (int start = 0; start + len <= str.length(); ++start) {
                String itstr = str.substring(start, start + len);
                ret.add(itstr);
            }
        }

        return ret;
    }
    
    /**
     * Gets the ident.
     *
     * @param cnt the cnt
     * @return the ident
     */
    public static String getIdent(int cnt){
    	String r="";
    	for (int i=0;i<cnt;i++){
    		r+="\t";
    	}
    	return r;
    }
    
    /**
     * Shorten name.
     *
     * @param node the node
     * @return the string
     */
    public static String shortenName(String node){
    	return node.replace("ehupatras.suffixtree.Node", "");
    }
}
