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

import ehupatras.clustering.sapehac.ClusteringBuilder;

// TODO: Auto-generated Javadoc
/**
 * A DendrogramBuilder creates a Dendrogram consisting of ObservationNodes and
 * MergeNodes.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class DendrogramBuilder 
						implements ClusteringBuilder {

    /** The nodes. */
    private final DendrogramNode[] nodes;
    
    /** The last merge node. */
    private MergeNode lastMergeNode;
    
    /**
     * Instantiates a new dendrogram builder.
     *
     * @param nObservations the n observations
     */
    public DendrogramBuilder(final int nObservations) {
        nodes = new DendrogramNode[nObservations];
        for (int i = 0; i<nObservations; i++) {
            nodes[i] = new ObservationNode(i);
        }
    }

    /* (non-Javadoc)
     * @see ehupatras.clustering.sapehac.ClusteringBuilder#merge(int, int, double)
     */
    public final void merge(final int i, final int j, final double dissimilarity) {
        final MergeNode node = new MergeNode(nodes[i], nodes[j], dissimilarity);
        nodes[i] = node;
        lastMergeNode = node;
    }

    /**
     * Gets the dendrogram.
     *
     * @return the dendrogram
     */
    public final Dendrogram getDendrogram() {
        if (nodes.length==1) {
            return new Dendrogram(nodes[0]);
        } else {
            return new Dendrogram(lastMergeNode);
        }
    }
    
}
