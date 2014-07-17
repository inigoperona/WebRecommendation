package angelu.webrecommendation.evaluator;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import ehupatras.webrecommendation.recommender.RecommenderKnnToClustersTopURLs;

public class RecommenderKnnToClustersTopURLsAndContents001 
				extends RecommenderKnnToClustersTopURLs {

	// ATTRIBUTES
	
	private int m_leftZerosLen = 6;
	
	// CREATOR
	
	public RecommenderKnnToClustersTopURLsAndContents001(
			ArrayList<String[]> medoids,
			int[] globalMedoids,
			ArrayList<Object[]> recosForEachMedoid,
			boolean isDistance,
			float[][] rolesW){
		super(medoids, globalMedoids, recosForEachMedoid, isDistance, rolesW);
	}
	
	// FUNCTIONS
	
	protected ArrayList<String> getNextpossibleSteps(int nRecos){
		
		// All information about the recommendations		
		Object[] objA = this.getNextpossibleSteps_Info(nRecos);
		ArrayList<String> recosL = (ArrayList<String>)objA[0];
		ArrayList<Integer> supportL = (ArrayList<Integer>)objA[1];
		ArrayList<Integer> clustersL = (ArrayList<Integer>)objA[2];
		ArrayList<Float> distsL = (ArrayList<Float>)objA[3];
		
		// print the path done until this moment
		/*
		for(int i=0; i<m_waydone.size(); i++){
			int step = Integer.valueOf(m_waydone.get(i));
			System.out.print(step + ",");
		}
		System.out.println();
		*/
		
		// print all the information of each recommendation
		/*
		System.out.println("---");
		for(int i=0; i<recosL.size(); i++){
			int rec = Integer.valueOf(recosL.get(i));
			int sup = supportL.get(i);
			int cl = clustersL.get(i);
			float d = distsL.get(i);
			System.out.println(recosL.get(i) + " " + sup + " " + cl + " " + d);
		}
		System.out.println("---");
		*/
		
		// enrich/change the list of recommendations
		/*
		// for example, add 200 recommendation
		int recExample = 200;
		String recExampleStr = String.format(
				"%0"+ m_leftZerosLen + "d", recExample);
		recosL.add(recExampleStr);
		*/
		
		// return the new recommendations
		return recosL;
	}
	
	public ArrayList<String> a1_Proposamena (ArrayList<String> SPADE, ArrayList<String> waydone)throws IOException
	{ ArrayList<String> recomendations= new ArrayList<String>();
	int[] nearestURL;
	int[] url=null;
	int[] urlDone=null;
	int [] n_relation=number_of_Relation(SPADE,true);
	for(int j=0;j<=SPADE.size()-1;j++)
	{url[j]=Integer.valueOf(SPADE.get(j));}
	for(int j=0;j<=waydone.size()-1;j++)
	{urlDone[j]=Integer.valueOf(waydone.get(j));}
	if(waydone.size()==1)
	{return SPADE;}
	else if( waydone.size()==2)
	{ if(n_relation[0] >=n_relation[1])
	 {//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
		 nearestURL= gertueneko_urla(urlDone[1], 4, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
			 recomendations.add(Integer.toString(nearestURL[i]));}}
		 
	else
	{ //nearest urls URL3,URL4
		nearestURL= gertueneko_urla(urlDone[1], 2, true);
		for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL2
		nearestURL= gertueneko_urla(urlDone[0], 2, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}}return recomendations;}
	
	else if (waydone.size()==3)
	{if(n_relation[0] >=n_relation[1])
	 {//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
		nearestURL= gertueneko_urla(urlDone[2], 2, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
			 recomendations.add(Integer.toString(nearestURL[i]));}		 
		 nearestURL= gertueneko_urla(urlDone[1], 2, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}}
		 
	else
	{ //nearest urls SPURL4
		nearestURL= gertueneko_urla(urlDone[2], 1, true);
		for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL3
		nearestURL= gertueneko_urla(urlDone[1], 1, true);
		for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL2
		nearestURL= gertueneko_urla(urlDone[0], 2, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}}return recomendations;}
	else
	{if(n_relation[0] >=n_relation[1])
	 {//itzuli beharko nuke spurl4ren gertukoena, horretarako gertuko clusterra kalkulatuko duen programa egin behar dut
		 nearestURL= gertueneko_urla(urlDone[3], 2, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
			 recomendations.add(Integer.toString(nearestURL[i]));}		 
		 nearestURL= gertueneko_urla(urlDone[2], 2, true);
		 for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}}
		 
	else
	{ //nearest urls SPURL4
		nearestURL= gertueneko_urla(urlDone[3], 1, true);
		for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL3
		nearestURL= gertueneko_urla(urlDone[2], 1, true);
		for(int i=0; i<=nearestURL.length-1;i++){
		 recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL2
		nearestURL= gertueneko_urla(urlDone[1], 1, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}	
	//nearest urls SPURL2
		nearestURL= gertueneko_urla(urlDone[0], 1, false);
		for(int i=0; i<=nearestURL.length-1;i++){
		recomendations.add(Integer.toString(nearestURL[i]));}
		
		
		
	}return recomendations;}}
	
	
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	public int[] gertueneko_urla(int url1,int zenbat, boolean clusterrakEzDuAxola)throws IOException
	{ float[][] MatrizeErlazioak= new float[231][231];
	final float[] similarityak= new float[231];
	final Integer[] indizeak= new Integer[231];
	int[] gertuenekoURLak=null;
	String line2;
	int kontagailua=0;
	FileInputStream fstream2 = new FileInputStream("/home/aizea/ecplipse20140529/Datuak/ResSimilarity.txt");
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
	for (int j=0;j<=MatrizeErlazioak.length-1;j++)
	{similarityak[j]=MatrizeErlazioak[url1][j];
	indizeak[j]=j;
	}
	
	Integer[] min_max= ordenatumin_max(similarityak, indizeak);	
	int tamaina=min_max.length-1;
	
	if (clusterrakEzDuAxola)
	{
	for(int k=0; k<=zenbat-1;k++)
	{gertuenekoURLak[k]=min_max[tamaina];
	tamaina--;}
	}
	
	else
	{int cl2=zein_clusterretakoa(url1);
	for(int k=0; k<=zenbat-1;k++)
	{int cl1=zein_clusterretakoa(min_max[tamaina]);
	while(cl1==cl2)
		{tamaina--;
		cl1=zein_clusterretakoa(min_max[tamaina]);
		}
		gertuenekoURLak[k]=min_max[tamaina];
	}
	}
	
	return  gertuenekoURLak;}
	
	
	
	
	
	
	public final Integer[] ordenatumin_max(final float[] data,final Integer[] idx )throws IOException
	
	{Arrays.sort(idx, new Comparator<Integer>() {
	    @Override public int compare(final Integer o1, final Integer o2) {
	        return Float.compare(data[o1], data[o2]);}});
	
	return idx;
	}
	
	
	
	
	
	static float[] calcula_maximo(float valores[], int url) {
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
	
	
		
	public int[] number_of_Relation (ArrayList<String> SPADE, boolean SpDa) throws IOException
	{ int Equal_kop=0;
	  int different_kop=0;
	  String erlazioa;
	  int[] kontagailua= new int[2];
	if (SpDa)
	{for (int z=0; z<=SPADE.size()-1;z++)
		{for (int j=0; j<=SPADE.size()-1;j++)
			{ if(z!=j){
				erlazioa=equal_different(Integer.valueOf(SPADE.get(z)), Integer.valueOf(SPADE.get(j)));
				if (erlazioa=="Equal")
				{Equal_kop++;}
				else
				{different_kop++;}}

	}}
	kontagailua[0]=Equal_kop;
	kontagailua[1]=different_kop;
	return kontagailua;
	}
	else
	{//hemen nabigazioaren asuntoakin hasi beharko nintzen number of relation honetan bakarrik 3 erlazio begiratzen dira.
		if(SPADE.size()==1)
		{kontagailua[0]=0;
		kontagailua[1]=0;
			return kontagailua;}		
		
		else
		{for (int h=0; h<=SPADE.size()-2;h++)
		{
			erlazioa=equal_different(Integer.valueOf(SPADE.get(h)), Integer.valueOf(SPADE.get(h+1)));
			if (erlazioa=="Equal")
			{Equal_kop++;}
			else
			{different_kop++;}
		}
		kontagailua[0]=Equal_kop;
		kontagailua[1]=different_kop;
		return kontagailua;}
		
		}
	}
		
	
	public String equal_different(int url1, int url2) throws IOException
	{String erlazioa=erlazioak_atera(url1,url2);
	if(erlazioa!="Equal" && erlazioa!="Disjoint")
	{int cl1=zein_clusterretakoa(url1);
	int cl2=zein_clusterretakoa(url2);
	if (cl1==cl2)
		{return "Equal";}
	else
		{return "Disjoint";}}
	else
	{return erlazioa;}}
	
	public int zein_clusterretakoa(int url1)throws IOException
	{HashMap<Integer, Integer> cluster = new HashMap<Integer, Integer>();
	String line2;	
	FileInputStream fstream2 = new FileInputStream("/home/aizea/ecplipse20140529/Datuak/clusterrak.csv");
	DataInputStream in2= new DataInputStream(fstream2);
	BufferedReader br2= new BufferedReader(new InputStreamReader(in2));
	while ((line2= br2.readLine())!= null)
	{String[] clusterrak=line2.split(";");
	if(!cluster.containsKey(clusterrak[0]))
		{cluster.put(Integer.valueOf(clusterrak[0]), Integer.valueOf(clusterrak[1]));}
	}
	br2.close();
	return cluster.get(url1);	
	}
	
	public String erlazioak_atera (int url1, int url2) throws IOException
	{ String[][] MatrizeErlazioak= new String[231][231];
		String line2;
		int kontagailua=0;
		FileInputStream fstream2 = new FileInputStream("/home/aizea/ecplipse20140529/Datuak/ResRelations.txt");
	DataInputStream in2= new DataInputStream(fstream2);
	BufferedReader br2= new BufferedReader(new InputStreamReader(in2));
	while ((line2= br2.readLine())!= null)
	{
		String[] erlazioak=line2.split(";");
	for(int i=0; i<= erlazioak.length-1;i++)
		{MatrizeErlazioak[kontagailua][i]=erlazioak[i];
		}
	kontagailua++;
	}
		
		br2.close();
		return MatrizeErlazioak[url1][url2];}

	
	public float similarityak_atera (int url1, int url2) throws IOException
	{ float[][] MatrizeErlazioak= new float[231][231];
		String line2;
		int kontagailua=0;
		FileInputStream fstream2 = new FileInputStream("/home/aizea/ecplipse20140529/Datuak/ResSimilarity.txt");
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
}
