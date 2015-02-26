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
 * An ObservationNode represents a leaf node in a Dendrogram.
 * It corresponds to a singleton cluster of one observation.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class ObservationNode 
						implements DendrogramNode, Serializable {

	// it requires to be serializable
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The observation. */
	private final int observation;

	
	/**
	 * Instantiates a new observation node.
	 *
	 * @param observation the observation
	 */
	public ObservationNode(final int observation) {
		this.observation = observation;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.clustering.sapehac.dendrogram.DendrogramNode#getLeft()
	 */
	public final DendrogramNode getLeft() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.clustering.sapehac.dendrogram.DendrogramNode#getRight()
	 */
	public final DendrogramNode getRight() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.clustering.sapehac.dendrogram.DendrogramNode#getObservationCount()
	 */
	public int getObservationCount() {
		return 1;
	}
	
	/**
	 * Gets the observation.
	 *
	 * @return the observation
	 */
	public final int getObservation() {
		return observation;
	}


}