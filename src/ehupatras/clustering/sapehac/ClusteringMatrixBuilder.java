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
package ehupatras.clustering.sapehac;


// TODO: Auto-generated Javadoc
/**
 * A ClusteringMatrixBuilder builds a matrix in which 
 * each row represents a step in the clustering
 * and each column represents an observation or cluster.
 * In the first step (row 0), each column represents an observation.
 * In the last step, each column refers to the same cluster.
 * Each step represents a copy of the step above, 
 * with two clusters merged into one.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public final class ClusteringMatrixBuilder implements ClusteringBuilder {

    /** The Constant INVALID. */
    private static final int INVALID = -1;
    
    /** The clustering. */
    private final int[][] clustering;
    
    /** The current step. */
    private int currentStep;


    /**
     * Instantiates a new clustering matrix builder.
     *
     * @param nObservations the n observations
     */
    public ClusteringMatrixBuilder(final int nObservations) {
        final int nSteps = nObservations;
        clustering = new int[nSteps][nObservations];
        for (int observation = 0; observation<nObservations; observation++) {
            // initialize original step (each observation is its own cluster)
            clustering[0][observation] = observation;
            // initialize subsequent steps to "invalid"
            for (int step = 1; step<nSteps; step++) {
                clustering[step][observation] = INVALID;
            }
        }
        currentStep = 0;
    }

    /* (non-Javadoc)
     * @see ehupatras.clustering.sapehac.ClusteringBuilder#merge(int, int, double)
     */
    public void merge(final int i, final int j, final double dissimilarity) {
        final int previousStep = currentStep;
        currentStep++;
        for (int observation = 0; observation<clustering.length; observation++) {
            final int previousCluster = clustering[previousStep][observation];
            if (previousCluster==j) {
                clustering[currentStep][observation] = i;
            } else {
                clustering[currentStep][observation] = previousCluster;
            }
        }
    }

    /**
     * Gets the clustering.
     *
     * @return the clustering
     */
    public int[][] getClustering() {
        return clustering;
    }

}
