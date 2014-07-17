package angelu.webrecommendation.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import angelu.webrecommendation.converter.URLconverterUsaCon;
import ehupatras.webrecommendation.recommender.RecommenderKnnToClustersTopURLs;


public class RecommenderKnnToClustersTopURLsAndContentsA1 
				extends RecommenderKnnToClustersTopURLs {

	// ATTRIBUTES
	
	private int m_leftZerosLen = 6;
	private int m_nURLs = 0;
	private float[][] m_UrlSimilarityMatrix = null;
	private String[][] m_UrlRelationMatrix = null;
	private HashMap<Integer,Integer> m_UrlClusteringDict = null;
	private URLconverterUsaCon m_conv = null;
	
	
	// CREATOR
	
	public RecommenderKnnToClustersTopURLsAndContentsA1(
			ArrayList<String[]> medoids,
			int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid,
			boolean isDistance,
			float[][] rolesW,
			int nURLs,
			float[][] urlSimilarityMatrix,
			String[][] urlRelationMatrix,
			HashMap<Integer,Integer> urlClusteringDict,
			URLconverterUsaCon conv){
		super(medoids, globalMedoids, recosForEachMedoid, isDistance, rolesW);
		m_nURLs = nURLs;
		m_UrlSimilarityMatrix = urlSimilarityMatrix;
		m_UrlRelationMatrix = urlRelationMatrix;
		m_UrlClusteringDict = urlClusteringDict;
		m_conv = conv;
	}
	
	
	// FUNCTIONS
	
	protected ArrayList<String> getNextpossibleSteps(int nRecos){
		
		// All information about the recommendations		
		Object[] objA = this.getNextpossibleSteps_Info(nRecos);
		ArrayList<String> recosL = (ArrayList<String>)objA[0];
		ArrayList<Integer> supportL = (ArrayList<Integer>)objA[1];
		ArrayList<Integer> clustersL = (ArrayList<Integer>)objA[2];
		ArrayList<Float> distsL = (ArrayList<Float>)objA[3];
		
		// Prepare the recommendations and the way done	
		// Recommendations
		int[] url = new int[recosL.size()];
		for(int j=0;j<recosL.size();j++){
			int recUsage = Integer.valueOf(recosL.get(j));
			int recContent = m_conv.getContentURL(recUsage);
			url[j] = recContent;
		}
		// way done
		int[] urlDone = new int[m_waydone.size()];
		for(int j=0;j<m_waydone.size();j++){
			int wayUsage = Integer.valueOf(m_waydone.get(j));
			int wayContent = m_conv.getContentURL(wayUsage);
			urlDone[j] = Integer.valueOf(wayContent);
		}
		
		
		// Enrichment
		ArrayList<Integer> urlAL = this.a1_Proposamena(url, urlDone);
		
		
		// Convert the final recommendations to usage URLs
		ArrayList<String> recosFinal = new ArrayList<String>();
		for(int i=0; i<urlAL.size(); i++){
			int urlContent = urlAL.get(i);
			int urlUsage = m_conv.getUsageURL(urlContent);
			String urlStr = String.format("%0"+ m_leftZerosLen + "d", urlUsage);
			recosFinal.add(urlStr); 
		}
		
		// return the new recommendations
		return recosFinal;
	}
	
	public ArrayList<Integer> a1_Proposamena (int[] url, int[] urlDone){ 
		ArrayList<Integer> recomendations= new ArrayList<Integer>();
		int[] nearestURL;
		int[] n_relation=number_of_Relation(url,true);
		
		if(urlDone.length<=1){
			for(int i=0; i<url.length; i++){
				recomendations.add(url[i]);
			}
			return recomendations;
		}
		
		else if(urlDone.length==2)
		{
			if(n_relation[0] >=n_relation[1])
			{//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[1], 4, true);
				for(int i=0; i<=nearestURL.length-1;i++)
				{recomendations.add(nearestURL[i]);}}
			else
			{	//nearest urls URL3,URL4
				nearestURL= gertueneko_urla(urlDone[1], 2, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}	
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[0], 2, false);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}}
			
			return recomendations;}
	
		else if (urlDone.length==3)
		{	if(n_relation[0] >=n_relation[1])
			{//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[2], 2, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}
				nearestURL= gertueneko_urla(urlDone[1], 2, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}}
			else
			{ 	//nearest urls SPURL4
				nearestURL= gertueneko_urla(urlDone[2], 1, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}	
				//nearest urls SPURL3
				nearestURL= gertueneko_urla(urlDone[1], 1, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[0], 2, false);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}}
		
			return recomendations;}
		
		else
		{	if(n_relation[0] >=n_relation[1])
			{	//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
				nearestURL= gertueneko_urla(urlDone[3], 2, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}
				nearestURL= gertueneko_urla(urlDone[2], 2, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}}
		 
			else
			{ 	//nearest urls SPURL4
				nearestURL= gertueneko_urla(urlDone[3], 1, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}	
				//nearest urls SPURL3
				nearestURL= gertueneko_urla(urlDone[2], 1, true);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}	
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[1], 1, false);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}	
				//nearest urls SPURL2
				nearestURL= gertueneko_urla(urlDone[0], 1, false);
				for(int i=0; i<=nearestURL.length-1;i++){
					recomendations.add(nearestURL[i]);}}
		
			return recomendations;}
		
	}
	
	
	
	
	
	
	/*
	public ArrayList<String> a2_Proposamena (ArrayList<String> SPADE, ArrayList<String> waydone)throws IOException
	{ ArrayList<String> recomendations= new ArrayList<String>();
	int[] nearestURL;
	int[] url=null;
	int[] urlDone=null;
	int [] n_relation=number_of_Relation(SPADE,true);
	for(int j=0;j<=SPADE.size()-1;j++)
	{url[j]=Integer.valueOf(SPADE.get(j));}
	for(int j=0;j<=waydone.size()-1;j++)
	{urlDone[j]=Integer.valueOf(waydone.get(j));}
	recomendations.add(Integer.toString(url[2]));
	recomendations.add(Integer.toString(url[3]));
	if(waydone.size()==1)
	{recomendations.add(Integer.toString(url[1]));
	recomendations.add(Integer.toString(url[0]));}
	else if( waydone.size()==2)
	
	{ if(n_relation[0] >=n_relation[1])
	 {//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
		 nearestURL= gertueneko_urla(urlDone[1], 2, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
			 recomendations.add(Integer.toString(nearestURL[i]));}}
		 
	else
	{//nearest urls SPURL2
		nearestURL= gertueneko_urla(urlDone[0], 2, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}
		
		
		
	}}
	
	else if(waydone.size()==3)
	{if(n_relation[0] >=n_relation[1])
	 {//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
		 nearestURL= gertueneko_urla(urlDone[2], 1, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
			 recomendations.add(Integer.toString(nearestURL[i]));}
		 
		 nearestURL= gertueneko_urla(urlDone[1], 1, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
			 recomendations.add(Integer.toString(nearestURL[i]));}}
		 
	else
	{//nearest urls SPURL2
		nearestURL= gertueneko_urla(urlDone[0], 2, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}
		
		
		
	}}
	else
	{if(n_relation[0] >=n_relation[1])
	 {//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
		 nearestURL= gertueneko_urla(urlDone[3], 1, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
			 recomendations.add(Integer.toString(nearestURL[i]));}
		 
		 nearestURL= gertueneko_urla(urlDone[2], 1, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}}
		 
	else
	{//nearest urls SPURL2
		nearestURL= gertueneko_urla(urlDone[1], 1, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL2
		nearestURL= gertueneko_urla(urlDone[0], 1, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}
		
		
		
	}}
	
	
	
		return recomendations;}
	*/
	
	
	/*
	public ArrayList<String> b1_Proposamena (ArrayList<String> SPADE)throws IOException
	{ ArrayList<String> recomendations= new ArrayList<String>();
	int[] nearestURL;
	int[] url=null;
	int [] n_relation=number_of_Relation(SPADE,true);
	for(int j=0;j<=SPADE.size()-1;j++)
	{url[j]=Integer.valueOf(SPADE.get(j));}
	 if(n_relation[0] >=n_relation[1])
	 {//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
		 nearestURL= gertueneko_urla(url[3], 2, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
			 recomendations.add(Integer.toString(nearestURL[i]));}		 
		 nearestURL= gertueneko_urla(url[2], 2, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}}
		 
	else
	{ //nearest urls SPURL4
		nearestURL= gertueneko_urla(url[3], 1, true);
		for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL3
		nearestURL= gertueneko_urla(url[2], 1, true);
		for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL2
		nearestURL= gertueneko_urla(url[1], 1, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL2
		nearestURL= gertueneko_urla(url[0], 1, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}
		
		
		
	}
		return recomendations;}
	*/
	
	/*
	public ArrayList<String> b2_Proposamena (ArrayList<String> SPADE)throws IOException
	{ ArrayList<String> recomendations= new ArrayList<String>();
	int[] nearestURL;
	int[] url=null;
	int [] n_relation=number_of_Relation(SPADE,true);
	for(int j=0;j<=SPADE.size()-1;j++)
	{url[j]=Integer.valueOf(SPADE.get(j));}
	recomendations.add(Integer.toString(url[2]));
	recomendations.add(Integer.toString(url[3]));
	 if(n_relation[0] >=n_relation[1])
	 {//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
		 nearestURL= gertueneko_urla(url[3], 1, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
			 recomendations.add(Integer.toString(nearestURL[i]));}
		 
		 nearestURL= gertueneko_urla(url[2], 1, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}}
		 
	else
	{//nearest urls SPURL2
		nearestURL= gertueneko_urla(url[1], 1, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL2
		nearestURL= gertueneko_urla(url[0], 1, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}
		
		
		
	}
		return recomendations;}
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// AUXILIAR FUNCTIONS
	
	private int[] gertueneko_urla(int url1,int zenbat, boolean clusterrakEzDuAxola)
	{	final float[] similarityak= new float[m_nURLs];
		final Integer[] indizeak= new Integer[m_nURLs];
		int[] gertuenekoURLak = new int[zenbat];
		
		for (int j=0;j<=m_UrlSimilarityMatrix.length-1;j++)
		{	similarityak[j]=m_UrlSimilarityMatrix[url1][j];
			indizeak[j]=j;}
		Integer[] min_max= ordenatumin_max(similarityak, indizeak);	
		int tamaina=min_max.length-1;
	
		if (clusterrakEzDuAxola)
		{	for(int k=0; k<=zenbat-1;k++)
			{	gertuenekoURLak[k]=min_max[tamaina];
				tamaina--;}}
	
		else
		{	int cl2 = m_UrlClusteringDict.get(url1);
			for(int k=0; k<=zenbat-1;k++)
			{	int cl1=m_UrlClusteringDict.get(min_max[tamaina]);
				while(cl1==cl2)
				{	tamaina--;
					cl1=m_UrlClusteringDict.get(min_max[tamaina]);}
				gertuenekoURLak[k]=min_max[tamaina];}}
	
		return  gertuenekoURLak;
	}
	
	private Integer[] ordenatumin_max(final float[] data,final Integer[] idx )	
	{	Arrays.sort(idx, new Comparator<Integer>() {
	    
		@Override
	    public int compare(final Integer o1, final Integer o2) {
	        return Float.compare(data[o1], data[o2]);}});
	
		return idx;
	}
	
	
	
	
	/*
	private float[] calcula_maximo(float valores[], int url) {
        float maximo = 0;
        int indice;
        int indi=0;
        for (indice = 0;indice < valores.length; indice++) {
          if(indice!=url) 
          {
        	if(valores[indice]>maximo) {
                maximo = valores[indice];
                indi=indice;
            }
        }}     
        float[] indizea= new float[2];
        indizea[0]=maximo;
        indizea[1]=indi;
        return indizea;
    }
	*/
	
	private int[] number_of_Relation (int[] urls, boolean SpDa)
	{ 	int Equal_kop=0;
	  	int different_kop=0;
	  	String erlazioa;
	  	int[] kontagailua= new int[2];
	  
	  	if (SpDa)
	  	{	for (int z=0; z<=urls.length-1;z++)
	  		{	for (int j=0; j<=urls.length-1;j++)
	  			{ 	if(z!=j){
	  					erlazioa=equal_different(urls[z], urls[j]);
	  					if (erlazioa=="Equal")
	  					{	Equal_kop++;}
	  					else
	  					{	different_kop++;}}}}
	  		kontagailua[0]=Equal_kop;
	  		kontagailua[1]=different_kop;
	  		return kontagailua;
	  	}
	  	else
	  	{	//hemen nabigazioaren asuntoakin hasi beharko nintzen number of relation honetan bakarrik 3 erlazio begiratzen dira.
	  		if(urls.length==1)
	  		{	kontagailua[0]=0;
	  			kontagailua[1]=0;
	  			return kontagailua;}		
		
	  		else
	  		{	for (int h=0; h<=urls.length-2;h++)
	  			{	erlazioa=equal_different(urls[h], urls[h+1]);
	  				if (erlazioa=="Equal")
	  				{	Equal_kop++;}
	  				else
	  				{	different_kop++;}}
	  		
	  		kontagailua[0]=Equal_kop;
	  		kontagailua[1]=different_kop;
	  		return kontagailua;}
		}
	}
	
	private String equal_different(int url1, int url2)
	{	String erlazioa=m_UrlRelationMatrix[url1][url2];
		if(erlazioa!="Equal" && erlazioa!="Disjoint")
		{	int cl1=m_UrlClusteringDict.get(url1);
			int cl2=m_UrlClusteringDict.get(url2);
			if (cl1==cl2)
			{	return "Equal";}
			else
			{	return "Disjoint";}}
		else
		{	return erlazioa;}
	}

	/*
	private float similarityak_atera(int url1, int url2) 
			throws IOException
	{ float[][] MatrizeErlazioak= new float[m_nContentURLs][m_nContentURLs];
		String line2;
		int kontagailua=0;
		FileInputStream fstream2 = new FileInputStream(m_resSimilarityFile);
	DataInputStream in2= new DataInputStream(fstream2);
	BufferedReader br2= new BufferedReader(new InputStreamReader(in2));
	while ((line2= br2.readLine())!= null)
	{
		String[] erlazioak=line2.split(";");
	for(int i=0; i<= erlazioak.length-1;i++)
		{MatrizeErlazioak[kontagailua][i]=Float.valueOf(erlazioak[i]);
		}
	kontagailua++;
	}
		
		br2.close();
		return MatrizeErlazioak[url1][url2];}
		*/
}
