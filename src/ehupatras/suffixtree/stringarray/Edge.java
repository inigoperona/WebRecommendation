 
package ehupatras.suffixtree.stringarray;

import java.util.ArrayList;

/**
 * Represents an Edge in the Suffix Tree.
 * It has a label and a destination Node
 */
class Edge {
    private ArrayList<String> label;
    private Node dest;

    public ArrayList<String> getLabel() {
        return label;
    }

    public void setLabel(ArrayList<String> label) {
        this.label = label;
    }

    public Node getDest() {
        return dest;
    }

    public void setDest(Node dest) {
        this.dest = dest;
    }

    public Edge(ArrayList<String> label, Node dest) {
        this.label = label;
        this.dest = dest;
    }

}
