/*
 * This file is licensed to You under the "Simplified BSD License".
 * You may not use this software except in compliance with the License. 
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/bsd-license.php
 * 
 * See the COPYRIGHT file distributed with this work for information
 * regarding copyright ownership.
 */
package ehupatras.clustering.sapehac.dendrogram;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * A Dendrogram represents the results of hierachical agglomerative clustering.
 * The root represents a single cluster containing all observations.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class Dendrogram 
						implements Serializable {

    // It is necessary to define this attribute to implements Serializable
	/** The Constant serialVersionUID. */
    private static final long serialVersionUID = 1L;
	
	/** The root. */
	private final DendrogramNode root;


    /**
     * Instantiates a new dendrogram.
     *
     * @param root the root
     */
    public Dendrogram(final DendrogramNode root) {
        this.root = root;
    }

    /**
     * Gets the root.
     *
     * @return the root
     */
    public DendrogramNode getRoot() {
        return root;
    }

    /**
     * Dump.
     */
    public void dump() {
        dumpNode("  ", root);
    }

    /**
     * Dump node.
     *
     * @param indent the indent
     * @param node the node
     */
    private void dumpNode(final String indent, final DendrogramNode node) {
        if (node==null) {
            System.out.println(indent+"<null>");
        } else if (node instanceof ObservationNode) {
            System.out.println(indent+"Observation: "+node);
        } else if (node instanceof MergeNode) {
            System.out.println(indent+"Merge:");
            dumpNode(indent+"  ", ((MergeNode)node).getLeft());
            dumpNode(indent+"  ", ((MergeNode)node).getRight());
        }
    }
}
