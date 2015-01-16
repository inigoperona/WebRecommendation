 
package ehupatras.suffixtree.chararray;

// TODO: Auto-generated Javadoc
/**
 * Represents an Edge in the Suffix Tree.
 * It has a label and a destination Node
 */
class Edge {
    
    /** The label. */
    private String label;
    
    /** The dest. */
    private Node dest;

    /**
     * Gets the label.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label.
     *
     * @param label the new label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Gets the dest.
     *
     * @return the dest
     */
    public Node getDest() {
        return dest;
    }

    /**
     * Sets the dest.
     *
     * @param dest the new dest
     */
    public void setDest(Node dest) {
        this.dest = dest;
    }

    /**
     * Instantiates a new edge.
     *
     * @param label the label
     * @param dest the dest
     */
    public Edge(String label, Node dest) {
        this.label = label;
        this.dest = dest;
    }

}
