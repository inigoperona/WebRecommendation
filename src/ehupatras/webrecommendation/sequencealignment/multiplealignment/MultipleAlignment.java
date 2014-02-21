package ehupatras.webrecommendation.sequencealignment.multiplealignment;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

// BioJava project
// http://biojava.org/wiki/Main_Page
// http://biojava.org/wiki/BioJava:CookBook3:MSA
// http://www.biocodershub.net/community/multiple-sequence-alignment-using-biojava-3-0/
import org.biojava3.alignment.template.Profile;
import org.biojava3.alignment.Alignments;
import org.biojava3.core.sequence.BasicSequence;
import org.biojava3.core.sequence.template.CompoundSet;
import org.biojava3.core.sequence.io.FastaReaderHelper;


import org.biojava3.core.sequence.loader.StringProxySequenceReader;
//import org.biojava3.core.sequence.;

public class MultipleAlignment {

    public static void main(String[] args) {
        String[] ids = new String[] {"Q21691", "Q21495", "O48771"};
        try {
            multipleSequenceAlignment(ids);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
 
    private static void multipleSequenceAlignment(String[] ids) throws Exception {
   /*
    	StringProxySequenceReader reader = new StringProxySequenceReader();
    	Compount c1 = new Compound();
    	ProxySequenceReader psr = new ProxySequenceReader(); 
    	BasicSequence bs = new BasicSequence();
    	List<BasicSequence> lst = new ArrayList<BasicSequence>();
        lst.add(new BasicSequence<>("0123456789", null));
        lst.add(new BasicSequence("2345678901", null));
        lst.add(new BasicSequence("5678901234", null));
        
        Profile profile = Alignments.getMultipleSequenceAlignment(lst);
        System.out.printf("Clustalw:%n%s%n", profile);
        ConcurrencyTools.shutdown();
        */
    }
}

