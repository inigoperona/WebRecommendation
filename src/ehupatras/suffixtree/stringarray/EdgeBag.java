  
package ehupatras.suffixtree.stringarray;

class EdgeBag {
    private String[] chars;
    private Edge[] values;
    private static final int BSEARCH_THRESHOLD = 6;

    void put(String c, Edge e) {        
        if (chars == null) {
            chars = new String[0];
            values = new Edge[0];
        }
        int idx = search(c);

        if (idx < 0) {
            int currsize = chars.length;
            String[] copy = new String[currsize + 1];
            System.arraycopy(chars, 0, copy, 0, currsize);
            chars = copy;
            Edge[] copy1 = new Edge[currsize + 1];
            System.arraycopy(values, 0, copy1, 0, currsize);
            values = copy1;
            chars[currsize] = c;
            values[currsize] = e;
            currsize++;
            if (currsize > BSEARCH_THRESHOLD) {
                sortArrays();
            }
        } else {
            values[idx] = e;
        }
    }

    Edge get(String c) {        
        int idx = search(c);
        if (idx < 0) {
            return null;
        }
        return values[idx];
    }

    private int search(String c) {
        if (chars == null)
            return -1;
        
        if (chars.length > BSEARCH_THRESHOLD) {
            return java.util.Arrays.binarySearch(chars, c);
        }

        for (int i = 0; i < chars.length; i++) {
            if (c.equals(chars[i])) {
                return i;
            }
        }
        return -1;
    }

    Edge[] values() {
        return values == null ? new Edge[0] : values;
    }
    
    /**
     * A trivial implementation of sort, used to sort chars[] and values[] according to the data in chars.
     * 
     * It was preferred to faster sorts (like qsort) because of the small sizes (<=36) of the collections involved.
     */
    private void sortArrays() {
        for (int i = 0; i < chars.length; i++) {
         for (int j = i; j > 0; j--) {
            if (chars[j-1].compareTo(chars[j])>0) {
               String swap = chars[j];
               chars[j] = chars[j-1];
               chars[j-1] = swap;

               Edge swapEdge = values[j];
               values[j] = values[j-1];
               values[j-1] = swapEdge;
            }
         }
      }
    }
}
