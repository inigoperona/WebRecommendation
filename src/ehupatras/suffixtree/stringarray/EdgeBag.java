package ehupatras.suffixtree.stringarray;

import ehupatras.suffixtree.stringarray.Edge;

import java.util.ArrayList;
import java.util.Collections;

class EdgeBag {
    private ArrayList<String> words = null;
    private ArrayList<Edge> values;
    private static final int BSEARCH_THRESHOLD = 6;

    void put(String c, Edge e) {        
        if (words == null) {
            words = new ArrayList<String>();
            values = new ArrayList<Edge>();
        }
        int idx = search(c);

        if (idx < 0) {
            words.add(c);
            values.add(e);
            int currsize = words.size();
            if (currsize > BSEARCH_THRESHOLD) {
                sortArrays();
            }
        } else {
            values.set(idx, e);
        }
    }

    Edge get(String c) {
        int idx = search(c);
        if (idx < 0) {
            return null;
        }
        return values.get(idx);
    }

    private int search(String c) {
        if (words == null)
            return -1;
        
        if (words.size() > BSEARCH_THRESHOLD) {
            return Collections.binarySearch(words, c);
        }

        for (int i = 0; i < words.size(); i++) {
            if (c.equals(words.get(i))) {
                return i;
            }
        }
        return -1;
    }

    ArrayList<Edge> values() {
        return values == null ? new ArrayList<Edge>() : values;
    }
    
    /**
     * A trivial implementation of sort, used to sort chars[] and values[] according to the data in chars.
     * 
     * It was preferred to faster sorts (like qsort) because of the small sizes (<=36) of the collections involved.
     */
    private void sortArrays() {
        for (int i = 0; i < words.size(); i++) {
         for (int j = i; j > 0; j--) {
            if (words.get(j-1).compareTo(words.get(j)) > 0) {
                String swap = words.get(j);
                words.set(j, words.get(j-1));
                words.set(j-1, swap);

               Edge swapEdge = values.get(j);
               values.set(j, values.get(j-1));
               values.set(j-1, swapEdge);
            }
         }
      }
    }
}
