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
package ehupatras.clustering.sapehac.experiment;


// TODO: Auto-generated Javadoc
/**
 * Computes the dissimilarity between two observations in an experiment.
 * 
 * @author Matthias.Hauswirth@usi.ch
 */
public interface DissimilarityMeasure {

    /**
     * Compute dissimilarity.
     *
     * @param experiment the experiment
     * @param observation1 the observation1
     * @param observation2 the observation2
     * @return the double
     */
    public double computeDissimilarity(Experiment experiment, int observation1, int observation2);

}
