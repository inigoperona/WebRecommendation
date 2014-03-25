package ehupatras.clustering;

import java.util.ArrayList;

public class ClusteringPAM {

	private int m_k = 10;
	private float[][] m_dm;
	private ArrayList<Integer> m_medoids;
	private double m_cost;
	
	public ClusteringPAM(int k, float[][] dm){
		m_k = k;
		m_dm = dm;
		
		// create the medoids array
		m_medoids = new ArrayList<Integer>();
	}
	
	
	private int databaseMedoid(){
		int nSeq = m_dm.length;
		
		// Find the most centered case
		float minsumdist = Float.MAX_VALUE;
		int medoidIndex = -1;
		for(int i=0; i<nSeq; i++){
			float rowsum = 0f;
		    for(int j=0; j<nSeq; j++){
		    	rowsum = rowsum + m_dm[i][j];
		    }
		    if(minsumdist>=rowsum){
		      minsumdist = rowsum;
		      medoidIndex = i;
		    }
		}
		
		return medoidIndex;
	}
	
	
	private void build(){
		int nSeq = m_dm.length;
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
		    		float rest = distmedh - m_dm[j][h];
		    		float chj = rest>0f ? rest : 0f;
		    		contribution = contribution + chj;
		    	}
		    	if(contributionMAX<=contribution){
		    		contributionMAX = contribution;
		    		representant = j;
		    	}
		    }
		    m_medoids.add(representant);
		}
		
		// print the medoids
		System.out.print(" after build: medoids are " + m_medoids.get(0));
		for(int i=1; i<m_medoids.size(); i++){
			System.out.print("," + m_medoids.get(i));
		}
		System.out.println();
		
		// print the cost (objective function)
		m_cost = this.objectivefunc();
		System.out.println("   objective function: " + m_cost);
	}
	
	
	private float[] findNearests1(){
		int nSeq = m_dm.length;
		
		// Find the nearest medoid for each case
		float[] nearest1 = new float[nSeq];
		for(int i=0; i<nSeq; i++){
		    float minDistH1 = Float.MAX_VALUE;
		    for(int j=0; j<m_medoids.size(); j++){
		    	int medoidInd = m_medoids.get(j);
		        float dij = m_dm[i][medoidInd];
		        if(minDistH1 > dij){
		        	minDistH1 = dij;
		        }
		    }
		    nearest1[i] = minDistH1;
		}
		
		return nearest1;
	}
	
	
	private void swap(){
		int nSeq = m_dm.length;
		System.out.println("SWAP phase");

		//while(true){
		for(int ii=0; ii<10; ii++){
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

						if(m_dm[medoid][j]==nearest1[j]){
							// is j's medoid
							float small = nearest2[j]>m_dm[h][j] ? m_dm[h][j] : nearest2[j];
							contj = small - nearest1[j];
						} else {
							// is not j's medoid
							if(m_dm[h][j] < nearest1[j]) {
								contj = m_dm[h][j] - nearest1[j];
							}
						}

						// total sum of contributions
						contribution = contribution + (double)contj;
					}

					// which j has the minimum contribution value?
					if(contributionMIN>contribution){
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
		    		 "] no swp. (" + cost + ")\n");
		     break;
		   }
		}
	}
	
	
	private double objectivefunc(){
		int nSeq = m_dm.length;
		
		// the objective function
		double cost = 0d;
		for(int i=0; i<nSeq;i++){
		    float mindist = Float.MAX_VALUE;
		    for(int j=0; j<m_medoids.size(); j++){
		    	int medoid = m_medoids.get(j);
		    	float dist = m_dm[medoid][i];
		    	if(mindist>dist){
		    		mindist = dist;
		    	}
		    }
		    cost = cost + (float)mindist;
		}
		
		return cost;
	}
	
	
	private Object[] findnearests2(){
		int nSeq = m_dm.length;
		
		// Find the two nearest medoids for each case
		float[] nearest1 = new float[nSeq];
		float[] nearest2 = new float[nSeq];
		for(int i=0; i<nSeq; i++){
		    float mindisth1 = Float.MAX_VALUE;
		    float mindisth2 = Float.MAX_VALUE;
		    for(int j=0; j<m_medoids.size(); j++){
		    	float dij = m_dm[i][m_medoids.get(j)];
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

	
	public static void main(String[] args){
		// create the distance matrix
		float[][] dm = new float[][]{
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
		
		// create the object
		ClusteringPAM pam = new ClusteringPAM(2, dm);
		
		// the center of the database
		pam.build();
		pam.swap();
		
		/*
		# R comparison
		m = matrix(rnorm(25,10,0.5), nrow=5, ncol=5)
		dm = as.dist(m, diag = TRUE, upper = TRUE)
		pam(dm, 2, diss=TRUE, trace.lev=3)
		*/
	}
	
}
