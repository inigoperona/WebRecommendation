package angelu.webrecommendation.evaluator;

public class RecommenderKnnToClustersTopURLsAndContentsA2 {

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
	
}
