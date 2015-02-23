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
 * A MergeNode represents an interior node in a Dendrogram.
 * It corresponds to a (non-singleton) cluster of observations.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class MergeNode 
					implements DendrogramNode, Serializable {
	
	// it requires to be serializable
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The left. */
	private final DendrogramNode left;
	
	/** The right. */
	private final DendrogramNode right;
	
	/** The dissimilarity. */
	private final double dissimilarity;
	
	/** The observation count. */
	private final int observationCount;
	
	/** COP value of the node */
	public double copValue;

	
	
	/**
	 * Instantiates a new merge node.
	 *
	 * @param left the left
	 * @param right the right
	 * @param dissimilarity the dissimilarity
	 */
	public MergeNode(final DendrogramNode left, final DendrogramNode right, final double dissimilarity) {
		this.left = left;
		this.right = right;
		this.dissimilarity = dissimilarity;
		this.copValue=1.0;
		observationCount = left.getObservationCount()+right.getObservationCount();
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.clustering.sapehac.dendrogram.DendrogramNode#getObservationCount()
	 */
	public int getObservationCount() {
		return observationCount;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.clustering.sapehac.dendrogram.DendrogramNode#getLeft()
	 */
	public final DendrogramNode getLeft() {
		return left;
	}
	
	/* (non-Javadoc)
	 * @see ehupatras.clustering.sapehac.dendrogram.DendrogramNode#getRight()
	 */
	public final DendrogramNode getRight() {
		return right;
	}
	
	/**
	 * Gets the dissimilarity.
	 *
	 * @return the dissimilarity
	 */
	public final double getDissimilarity() {
		return dissimilarity;
	}
	
	/**
	 * Gets the COP value.
	 *
	 * @return the COP value
	 */
	public double getCOP(){
		return copValue;
	}
	
	/**
	 * Sets the COP value.
	 *
	 */
	public void setCOP(double cop){
		this.copValue = cop;
	}

}