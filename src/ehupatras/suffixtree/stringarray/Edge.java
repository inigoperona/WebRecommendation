 
package ehupatras.suffixtree.stringarray;

/**
 * Represents an Edge in the Suffix Tree.
 * It has a label and a destination Node
 */
class Edge {
    private StringArray label;
    private Node dest;

    public StringArray getLabel() {
    	return label;
    }
    
    public String getLabelString() {
    	String labelstr = "";
    	for(int i=0; i<label.length(); i++){
    		labelstr = labelstr + label.charAt(i);
    	}
        return labelstr;
    } 

    public void setLabel(StringArray label) {
        this.label = label;
    }

    public Node getDest() {
        return dest;
    }

    public void setDest(Node dest) {
        this.dest = dest;
    }

    public Edge(StringArray label, Node dest) {
        this.label = label;
        this.dest = dest;
    }

}
