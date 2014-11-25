package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.webrecommendation.recommender.RecommenderKnnToClustersTopURLs;

public abstract class RecommenderKnnToClustersTopURLsAndContents 
				extends RecommenderKnnToClustersTopURLs {

	// ATTRIBUTES
	
	protected int m_leftZerosLen = 6;
	protected int m_nURLs = 0;
	protected float[][] m_UrlSimilarityMatrix = null;
	protected String[][] m_UrlRelationMatrix = null;
	protected HashMap<Integer,Integer> m_UrlClusteringDict = null;
	protected HashMap<Integer,Integer> m_url2topic = null;
	protected URLconverterUsaCon m_conv = null;
	private ArrayList<Integer> m_noProposeURLs_contID = new ArrayList<Integer>();
	
	
	// CREATOR
	
	public RecommenderKnnToClustersTopURLsAndContents(
			ArrayList<String[]> medoids,
			int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid,
			boolean isDistance,
			float[][] rolesW,
			int nURLs,
			float[][] urlSimilarityMatrix,
			String[][] urlRelationMatrix,
			HashMap<Integer,Integer> urlClusteringDict,
			URLconverterUsaCon conv,
			ArrayList<Integer> noProposeURLs){
		super(medoids, globalMedoids, recosForEachMedoid, isDistance, rolesW);
		m_nURLs = nURLs;
		m_UrlSimilarityMatrix = urlSimilarityMatrix;
		m_UrlRelationMatrix = urlRelationMatrix;
		m_UrlClusteringDict = urlClusteringDict;
		m_url2topic = (HashMap<Integer,Integer>)urlClusteringDict.clone();
		m_conv = conv;
		
		// convert usage URL-IDs to Content-ID
		m_noProposeURLs_contID = new ArrayList<Integer>();
		for(int i=0; i<noProposeURLs.size(); i++){
			int usageID = Integer.valueOf(noProposeURLs.get(i));
			int conteID = m_conv.getContentURL(usageID);
			m_noProposeURLs_contID.add(conteID);
		}
	}
	
	
	
	// FUNCTIONS
	
	protected ArrayList<String> getNextpossibleSteps(int nRecos){
		
		// All information about the recommendations		
		Object[] objA = this.getNextpossibleSteps_Info(nRecos);
		ArrayList<String> recosL = (ArrayList<String>)objA[0];
		ArrayList<Integer> supportL = (ArrayList<Integer>)objA[1];
		ArrayList<Integer> clustersL = (ArrayList<Integer>)objA[2];
		ArrayList<Float> distsL = (ArrayList<Float>)objA[3];
		
		// order the recommendations
		final float[] sups = new float[recosL.size()];
		for(int i=0; i<supportL.size(); i++){
			sups[i] = (float)supportL.get(i);
		}
		ArrayList<Integer> recosL2 = this.orderRecommendations(sups, recosL);
		
		// Prepare the recommendations and the way done	
		// Recommendations
		int[] url = new int[recosL.size()];
		int ind = 0;
		for(int j=recosL2.size()-1; j>=0; j--){
			// Put all recommendations in reverse order
			// because afterwards the strategies takes 
			// the SPADE's URLs 
			// from the bottom to the top
			int recUsage = recosL2.get(j);
			int recContent = m_conv.getContentURL(recUsage);
			url[ind] = recContent;
			ind++;
		}
		// way done
		int memory = 4;
		int nWayDoneLastPart = m_waydone.size()>=memory ? 4 : m_waydone.size();
		int[] urlDone = new int[nWayDoneLastPart];
		int startpoint = m_waydone.size()-nWayDoneLastPart; 
		for(int j=startpoint; j<m_waydone.size(); j++){
			int wayUsage = Integer.valueOf(m_waydone.get(j));
			int wayContent = m_conv.getContentURL(wayUsage);
			urlDone[j-startpoint] = Integer.valueOf(wayContent);
		}
		
		
		// Enrichment
		ArrayList<Integer> urlAL = this.applyEnrichment(url, urlDone);
		
		
		// Convert the final recommendations to usage URLs
		ArrayList<String> recosFinal = new ArrayList<String>();
		for(int i=0; i<urlAL.size(); i++){
			int urlContent = urlAL.get(i);
			int urlUsage = m_conv.getUsageURL(urlContent);
			if(urlUsage>0)
			{String urlStr = String.format("%0"+ m_leftZerosLen + "d", urlUsage);
			recosFinal.add(urlStr); 
			}
		}
		
		// return the new recommendations
		return recosFinal;
	}
	
	public abstract ArrayList<Integer> applyEnrichment(int[] url, int[] urlDone);
	
	public ArrayList<Integer> orderRecommendations(final float[] supports, ArrayList<String> recos){
		ArrayList<Integer> recosAL = new ArrayList<Integer>();
		for(int i=0; i<recos.size(); i++){
			int val = Integer.valueOf(recos.get(i));
			recosAL.add(val);
		}
		return recosAL;
	}
	
	public ArrayList<Integer> orderRecommendations_SpOrder(final float[] supports, ArrayList<String> recos){
		final Integer[] idx = new Integer[recos.size()];
		for(int i=0; i<recos.size(); i++){
			int val = Integer.valueOf(recos.get(i));
			idx[i] = val;
		}

		Integer[] recosI = this.ordenatumin_max2(supports, idx);
		
		ArrayList<Integer> recosAL = new ArrayList<Integer>();
		for(int i=recosI.length-1; i>=0; i--){
			recosAL.add(recosI[i]);
		}
		
		return recosAL;
	}
	
	
	// AUXILIAR FUNCTIONS
	
	protected ArrayList<Integer> gehituUrlEzErrepikatua(
			int url1,
			int[] nearestURL,
			ArrayList<Integer> recomendations,
			boolean clusterrakEzDuAxola){
		
		// Add the URLs we do not want to appear as an enrichment
		for(int i=0; i<m_noProposeURLs_contID.size(); i++){
			int contID = m_noProposeURLs_contID.get(i);
			recomendations.add(contID);
		}
		
		
		// Add the enrichments
		int kontagailua=0;
		
		for(int i=0; i<=nearestURL.length-1;i++)
		{	int GehitzekoUrla= nearestURL[i];
			if(!recomendations.contains(GehitzekoUrla))
			{	recomendations.add(GehitzekoUrla);
				kontagailua++;}
		}
		if(kontagailua<nearestURL.length)
		{	int[] nearestURL2=gertueneko_urla(url1, 100, clusterrakEzDuAxola);
			for(int j=0; j<=nearestURL2.length-1;j++)
			{	int GehitzekoUrla= nearestURL2[j];
				if(!recomendations.contains(GehitzekoUrla))
				{	recomendations.add(GehitzekoUrla);
					kontagailua++;
					if(kontagailua==nearestURL.length){break;}}
			}
		}
		
		
		// Remove the URLs we do not want in the final set of recommendations
		for(int i=0; i<m_noProposeURLs_contID.size(); i++){
			int contID = m_noProposeURLs_contID.get(i);
			recomendations.remove(new Integer(contID));
		}
		
		
		// return the recommendations list
		return recomendations;
	}
	
	protected int[] gertueneko_urla(int url1, int zenbat, boolean clusterrakEzDuAxola)
	{	final float[] similarityak= new float[m_nURLs];
		final Integer[] indizeak= new Integer[m_nURLs];
		ArrayList<Integer> gertuenekoURLak = new ArrayList<Integer>();
		
		for (int j=0;j<=m_UrlSimilarityMatrix.length-1;j++)
		{	similarityak[j]=m_UrlSimilarityMatrix[url1][j];
			indizeak[j]=j;}
		Integer[] min_max= ordenatumin_max(similarityak, indizeak);	
		int tamaina=min_max.length-2;
	
		if (clusterrakEzDuAxola)
		{	for(int k=0; k<=zenbat-1;k++)
			{	gertuenekoURLak.add(k,min_max[tamaina]);
				tamaina--;}}
	
		else
		{	int cl2 = m_UrlClusteringDict.get(url1);
			for(int k=0; k<=zenbat-1;k++)
			{	int cl1=m_UrlClusteringDict.get(min_max[tamaina]);
				while(cl1==cl2)
				{	tamaina--;
					cl1=m_UrlClusteringDict.get(min_max[tamaina]);}
				gertuenekoURLak.add(k,min_max[tamaina]);
				tamaina--;
				if(tamaina<0){break;}}}
	
		// convert to int[]
		int[] gertuenekoURLak2 = new int[gertuenekoURLak.size()];
		for(int i=0; i<gertuenekoURLak.size(); i++){
			gertuenekoURLak2[i] = gertuenekoURLak.get(i);
		}

		return  gertuenekoURLak2;
	}
	
	private Integer[] ordenatumin_max(final float[] data,final Integer[] idx )	
	{	Arrays.sort(idx, new Comparator<Integer>() {
	    
		@Override
	    public int compare(final Integer o1, final Integer o2) {
	        return Float.compare(data[o1], data[o2]);}});
	
		return idx;
	}
	
	private Integer[] ordenatumin_max2(float[] sups, Integer[] recos)	{
		Integer[] idx = new Integer[recos.length];
		for(int i=0; i<idx.length; i++){ idx[i] = i; }
		Integer[] orderIdx = this.ordenatumin_max(sups, idx);
		
		Integer[] recosOrd = new Integer[orderIdx.length];
		for(int i=0; i<orderIdx.length; i++){
			recosOrd[i] = recos[orderIdx[i]]; 
		}
		
		return recosOrd;
	}
	
	protected int[] number_of_Relation(int[] urls, boolean SpDa)
	{ 	int Equal_kop=0;
	  	int different_kop=0;
	  	String erlazioa;
	  	int[] kontagailua= new int[2];
	  
	  	if (SpDa)
	  	{	for (int z=0; z<=urls.length-1;z++)
	  		{	for (int j=0; j<=urls.length-1;j++)
	  			{ 	if(z!=j){
	  					erlazioa=equal_different(urls[z], urls[j]);
	  					if (erlazioa.equals("Equal"))
	  					{	Equal_kop++;}
	  					else
	  					{	different_kop++;}}}}
	  		kontagailua[0]=Equal_kop;
	  		kontagailua[1]=different_kop;
	  		return kontagailua;
	  	}
	  	else
	  	{	//hemen nabigazioaren asuntoakin hasi beharko nintzen number of relation honetan bakarrik 3 erlazio begiratzen dira.
	  		if(urls.length<=1)
	  		{	kontagailua[0]=0;
	  			kontagailua[1]=0;
	  			return kontagailua;}		
		
	  		else
	  		{	for (int h=0; h<=urls.length-2;h++)
	  			{	erlazioa=equal_different(urls[h], urls[h+1]);
	  				if (erlazioa.equals("Equal"))
	  				{	Equal_kop++;}
	  				else
	  				{	different_kop++;}}
	  		
	  		kontagailua[0]=Equal_kop;
	  		kontagailua[1]=different_kop;
	  		return kontagailua;}
		}
	}
	
	private String equal_different(int url1, int url2){
		String erlazioa=m_UrlRelationMatrix[url1][url2];
		if(!erlazioa.equals("Equal") && !erlazioa.equals("Disjoint")){
			int cl1=m_UrlClusteringDict.get(url1);
			int cl2=m_UrlClusteringDict.get(url2);
			if (cl1==cl2){
				erlazioa = "Equal";
			}
			else{
				erlazioa = "Disjoint";
			}
		}
		return erlazioa;
	}
	
	protected int[] number_of_Topics(int[] urls, boolean SpDa)
	{ 	int Equal_kop=0;
	  	int different_kop=0;
	  	int[] kontagailua= new int[2];
	  
	  	if (SpDa)
	  	{	for (int z=0; z<=urls.length-1;z++)
	  		{	for (int j=0; j<=urls.length-1;j++)
	  			{ 	if(z!=j){
	  					int top1 = m_url2topic.get(urls[z]);
	  					int top2 = m_url2topic.get(urls[j]);
	  					if (top1==top2)
	  					{	Equal_kop++;}
	  					else
	  					{	different_kop++;}}}}
	  		kontagailua[0]=Equal_kop;
	  		kontagailua[1]=different_kop;
	  		return kontagailua;
	  	}
	  	else
	  	{	//hemen nabigazioaren asuntoakin hasi beharko nintzen number of relation honetan bakarrik 3 erlazio begiratzen dira.
	  		if(urls.length<=1){
	  			kontagailua[0]=0;
	  			kontagailua[1]=0;
	  			return kontagailua;
	  		} else {
	  			for (int h=0; h<=urls.length-2;h++){	
	  				int top1 = m_url2topic.get(urls[h]);
					int top2 = m_url2topic.get(urls[h+1]);
	  				if (top1==top2){
	  					Equal_kop++;
	  				} else {
	  					different_kop++;
	  				}
	  			}	  		
	  			kontagailua[0]=Equal_kop;
	  			kontagailua[1]=different_kop;
	  			return kontagailua;
	  		}
		}
	}
	
}
