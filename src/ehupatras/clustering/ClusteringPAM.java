package ehupatras.clustering;

import java.util.ArrayList;
import java.util.Hashtable;

// TODO: Auto-generated Javadoc
/**
 * The Class ClusteringPAM.
 */
public class ClusteringPAM {

	/** The m_k. */
	private int m_k = 10;
	
	/** The m_dm. */
	private float[][] m_dm;
	
	/** The m_min dm. */
	private float m_minDM = Float.MAX_VALUE;
	
	/** The m_max dm. */
	private float m_maxDM = Float.MAX_VALUE;
	
	/** The m_real inds. */
	private int[] m_realInds;
	
	/** The m_medoids. */
	private ArrayList<Integer> m_medoids;
	
	/** The m_cost. */
	private double m_cost;
	
	/**
	 * Instantiates a new clustering pam.
	 *
	 * @param k the k
	 * @param dm the dm
	 * @param realIndexes the real indexes
	 */
	public ClusteringPAM(int k, float[][] dm, int[] realIndexes){
		if(k>realIndexes.length){
			m_k = realIndexes.length;
		} else {
			m_k = k;
		}
		
		m_dm = dm;
		for(int i=0;i<m_dm.length;i++){
			for(int j=0;j<m_dm[i].length;j++){
				float value = m_dm[i][j];
				if(value<m_minDM){
					m_minDM = value;
				}
				if(value>m_minDM){
					m_maxDM = value;
				}
			}
		}
		m_realInds = realIndexes;
		
		// create the medoids array
		m_medoids = new ArrayList<Integer>();
		
		// compute medoids
		this.runPAM();
	}
	
	/**
	 * Run pam.
	 */
	public void runPAM(){
		this.build();
		this.swap();
	}
	
	/**
	 * Database medoid.
	 *
	 * @return the int
	 */
	private int databaseMedoid(){
		int nSeq = numberOfCases();
		
		// Find the most centered case
		double minsumdist = Double.MAX_VALUE;
		int medoidIndex = -1;
		for(int i=0; i<nSeq; i++){
			double rowsum = 0f;
		    for(int j=0; j<nSeq; j++){
		    	rowsum = rowsum + (double)getDM(i,j);
		    }
		    if(minsumdist>=rowsum){
		      minsumdist = rowsum;
		      medoidIndex = i;
		    }
		}
		
		return medoidIndex;
	}
	
	
	/**
	 * Builds the.
	 */
	private void build(){
		int nSeq = numberOfCases();
		System.out.println("BUILD phase");
		
		// find the center-medoid of the database
		m_medoids.add(this.databaseMedoid());
		
		// Select the other medoids: from medoids[1] to medoids[clusterK-1]
		for(int i=1; i<m_k; i++){
			double contributionMAX = Double.MIN_VALUE;
		    int representant = -1;
		    
		    // nearest medoid for each case
		    float[] nearest1 = this.findNearests1();
		    
		    // compute each case contribution
		    for(int j=0; j<nSeq; j++){
		    	// j is a no-medoid case
		    	if(m_medoids.contains(j)){ continue; }
		      
		    	// compute the contribution j does
		    	double contribution = 0d;
		    	for(int h=0; h<nSeq; h++){
		    		// h is a no-medoid case
		    		if(m_medoids.contains(h)){ continue; }
		        
		    		// compute the contribution hj does
		    		float distmedh = nearest1[h];
		    		float rest = distmedh - getDM(j,h);
		    		float chj = rest>0f ? rest : 0f;
		    		contribution = contribution + chj;
		    	}
		    	if(contributionMAX<=contribution){ // maximize the contribution
		    		contributionMAX = contribution;
		    		representant = j;
		    	}
		    }
		    m_medoids.add(representant);
		}
		
		// print the medoids
		System.out.print(" after build: medoids are ");
		this.printMedoids();
		
		// print the cost (objective function)
		m_cost = this.objectivefunc();
		System.out.println("   objective function: " + m_cost);
	}
	
	
	/**
	 * Find nearests1.
	 *
	 * @return the float[]
	 */
	private float[] findNearests1(){
		int nSeq = numberOfCases();
		
		// Find the nearest medoid for each case
		float[] nearest1 = new float[nSeq];
		for(int i=0; i<nSeq; i++){
		    float minDistH1 = Float.MAX_VALUE;
		    for(int j=0; j<m_medoids.size(); j++){
		    	int medoidInd = m_medoids.get(j);
		        float dij = getDM(i,medoidInd);
		        if(minDistH1 > dij){
		        	minDistH1 = dij;
		        }
		    }
		    nearest1[i] = minDistH1;
		}
		
		return nearest1;
	}
	
	
	/**
	 * Swap.
	 */
	private void swap(){
		int nSeq = numberOfCases();
		System.out.println("SWAP phase");

		while(true){
		//for(int ii=0; ii<10; ii++){
			double contributionMIN = Double.MAX_VALUE;
			int medoidiMIN = -1;
			int medoidhMIN = -1;
			
			Object[] objA = this.findnearests2();
			float[] nearest1 = (float[])objA[0];
			float[] nearest2 = (float[])objA[1];
			
			// for each medoid
			for(int i=0; i<m_medoids.size(); i++){
				// i is a medoid case
				int medoid = m_medoids.get(i);

				// for each no-medoid
				for(int h=0; h<nSeq; h++){
					// h is no medoids case
					if(m_medoids.contains(h)){ continue; }

					// compute the contribution of swaping between medoid[i] and h
					double contribution = 0d;
					for(int j=0; j<nSeq; j++){
						// contribution of j
						float contj = 0f;

						if(getDM(medoid,j)==nearest1[j]){
							// is j's medoid
							float small = nearest2[j]>getDM(h,j) ? getDM(h,j) : nearest2[j];
							contj = small - nearest1[j];
						} else {
							// is not j's medoid
							if(getDM(h,j) < nearest1[j]) {
								contj = getDM(h,j) - nearest1[j];
							}
						}

						// total sum of contributions
						contribution = contribution + (double)contj;
					}

					// which j has the minimum contribution value?
					if(contributionMIN>contribution){ // minimize the contribution
						contributionMIN = contribution;
						medoidiMIN = i; // medoid index
						medoidhMIN = h; // no-medoid case
					}
				}
			}

		   // end criteria
		   if(contributionMIN<0){
			   double cost2 = m_cost + contributionMIN;
			   double cost = objectivefunc();
		     
			   // print the cost improvement information
			   System.out.print(" [" + System.currentTimeMillis() + "]" +
		    		 " swp new " + medoidhMIN + 
		    		 " <-> " + m_medoids.get(medoidiMIN) + 
		    		 " old; decreasing diss. " + m_cost + 
		    		 " by " + contributionMIN + " = " + cost2 + 
		    		 " (" + cost + "): ");
			   m_medoids.set(medoidiMIN, medoidhMIN); // SWAP
		     
			   // print the medoids
			   System.out.print(m_medoids.get(0));
				for(int i=1; i<m_medoids.size(); i++){
					System.out.print("," + m_medoids.get(i));
				}
				System.out.println();
				
				// update the cost
				m_cost = cost2;
		   } else {
		     double cost = objectivefunc();
		     System.out.println(" [" + System.currentTimeMillis() + 
		    		 "] no swp. (" + cost + ")");
		     break;
		   }
		}
		
		// print the medoids
		System.out.print(" after swap: medoids are ");
		this.printMedoids();
		
		// print the assignment of medoids
		System.out.println(" the assignment of medoids are: ");
		this.printMedoidAssignment();
	}
	
	/**
	 * Objectivefunc.
	 *
	 * @return the double
	 */
	private double objectivefunc(){
		int nSeq = numberOfCases();
		
		// the objective function
		double cost = 0d;
		for(int i=0; i<nSeq;i++){
		    float mindist = Float.MAX_VALUE;
		    for(int j=0; j<m_medoids.size(); j++){
		    	int medoid = m_medoids.get(j);
		    	float dist = getDM(medoid,i);
		    	if(mindist>dist){
		    		mindist = dist;
		    	}
		    }
		    cost = cost + (double)mindist;
		}
		
		return cost;
	}
	
	
	/**
	 * Findnearests2.
	 *
	 * @return the object[]
	 */
	private Object[] findnearests2(){
		int nSeq = numberOfCases();
		
		// Find the two nearest medoids for each case
		float[] nearest1 = new float[nSeq];
		float[] nearest2 = new float[nSeq];
		for(int i=0; i<nSeq; i++){
		    float mindisth1 = Float.MAX_VALUE;
		    float mindisth2 = Float.MAX_VALUE;
		    for(int j=0; j<m_medoids.size(); j++){
		    	float dij = getDM(i,m_medoids.get(j));
		    	if(mindisth1 > dij){
		    		mindisth2 = mindisth1;
		    		mindisth1 = dij;
		    	} else {
		    		if(mindisth2 > dij){
		    			mindisth2 = dij;
		    		}
		    	}
		    }
		    nearest1[i] = mindisth1;
		    nearest2[i] = mindisth2;
		}
		
		// return
		Object[] objA = new Object[2];
		objA[0] = nearest1;
		objA[1] = nearest2;
		return objA;
	}
	
	/**
	 * Gets the dm.
	 *
	 * @param i the i
	 * @param j the j
	 * @return the dm
	 */
	private float getDM(int i, int j){
		if(i<m_realInds.length && j<m_realInds.length){
			int i2 = m_realInds[i];
			int j2 = m_realInds[j];
			int nrow = m_dm.length;
			if(i2<nrow && j2<nrow){
				if(i2<=j2){
					return m_dm[i2][j2];
				} else {
					return m_dm[j2][i2];
				}
			} else {
				return m_maxDM;
			}
		} else{
			return m_maxDM;
		}
	}
	
	/**
	 * Prints the medoids.
	 */
	public void printMedoids(){
		System.out.print(" " + m_medoids.get(0));
		for(int i=1; i<m_medoids.size(); i++){
			System.out.print("," + m_medoids.get(i));
		}
		System.out.println();
	}
	
	/**
	 * Prints the medoid assignment.
	 */
	public void printMedoidAssignment(){
		int[] medAssign = this.findNearestsMedoid();
		System.out.print(" " + medAssign[0]);
		for(int i=1; i<medAssign.length; i++){
			System.out.print("," + medAssign[i]);
		}
		System.out.println();
	}
	
	/**
	 * Gets the medoid assignment.
	 *
	 * @return the medoid assignment
	 */
	public int[] getMedoidAssignment(){
		int[] medAssign = this.findNearestsMedoid();
		int[] clusters = new int[medAssign.length];
		Hashtable<Integer,Integer> medToCl = new Hashtable<Integer,Integer>();
		int kont = 0;
		for(int i=0; i<medAssign.length; i++){
			int med = medAssign[i];
			int cl;
			if(medToCl.containsKey(med)){
				cl = medToCl.get(med);
			} else {
				cl = kont;
				medToCl.put(med, cl);
				kont++;
			}
			clusters[i] = cl;
		}
		return clusters;
	}
	
	/**
	 * Find nearests medoid.
	 *
	 * @return the int[]
	 */
	private int[] findNearestsMedoid(){
		int nSeq = numberOfCases();
		
		// Find the nearest medoid for each case
		int[] nearest1 = new int[nSeq];
		for(int i=0; i<nSeq; i++){
		    float minDistH1 = Float.MAX_VALUE;
		    int minDistInd = -1;
		    for(int j=0; j<m_medoids.size(); j++){
		    	int medoidInd = m_medoids.get(j);
		        float dij = getDM(i,medoidInd);
		        if(minDistH1 > dij){
		        	minDistH1 = dij;
		        	minDistInd = medoidInd; 
		        }
		    }
		    nearest1[i] = minDistInd;
		}
		
		return nearest1;
	}
	
	/**
	 * Sets the initial medoids.
	 *
	 * @param medoids the new initial medoids
	 */
	private void setInitialMedoids(ArrayList<Integer> medoids){
		m_medoids = medoids;
	}
	
	/**
	 * Number of cases.
	 *
	 * @return the int
	 */
	private int numberOfCases(){
		return m_realInds.length;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args){
		// create the distance matrix
		float[][] dm0 = new float[][]{
				{10.171465f, 10.908582f, 10.927378f, 10.337211f, 10.054189f},
				{ 9.566395f, 10.189844f, 10.351626f,  9.183324f,  8.928350f},
				{10.238303f, 10.272046f,  9.328211f,  9.877080f, 10.435159f},
				{ 9.667533f,  8.847469f, 10.411028f, 10.039314f,  9.614549f},
				{10.522501f, 10.297253f, 10.359733f, 10.443354f,  9.551569f}
		};
		float[][] dm1 = new float[][]{
				{ 0.000000f, 9.830335f, 10.182363f,  9.834867f,  9.719266f},
				{ 9.830335f, 0.000000f,  9.493369f,  9.899817f,  9.513756f},
				{10.182363f, 9.493369f,  0.000000f, 10.028477f,  9.830633f},
				{ 9.834867f, 9.899817f, 10.028477f,  0.000000f, 10.237578f},
				{ 9.719266f, 9.513756f,  9.830633f, 10.237578f,  0.000000f}
			};
		float[][] dm2 = new float[][]{
				{0,1,2,3,4},
				{1,0,1,2,3},
				{1,2,0,1,2},
				{1,2,3,0,1},
				{1,2,3,4,0}
		};
		
		// medoids
		ArrayList<Integer> meds = new ArrayList<Integer>();
		meds.add(1);
		meds.add(4);
		
		// indexes
		int[] inds = new int[]{0,1,3,4};
		
		// create the object
		ClusteringPAM pam = new ClusteringPAM(2, dm0, inds);
		
		// the center of the database
		pam.runPAM();
		//pam.build();
		//pam.setInitialMedoids(meds);
		//pam.swap();
		int[] cls = pam.getMedoidAssignment();
		for(int i=0; i<cls.length; i++){System.out.print(cls[i] + ",");}
		System.out.println();
		
		/*
		# R comparison
		m = matrix(rnorm(25,10,0.5), nrow=5, ncol=5)
		dm = as.dist(m, diag = TRUE, upper = TRUE)
		pam(dm, 2, diss=TRUE, trace.lev=3)
		*/
	}
	
}
