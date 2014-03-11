/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.common.text;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Comparator for Vietnamese lexical ordering
 * 
 * @author Gero Herrmann
 * @created April 9, 2003
 * @version 1.6, October 25, 2004
 */
public class VietComparator2 implements Comparator<String> {
    
    /*
        Vietnamese collation rule:
     
            // tone accent: grave, hook above, tilde, acute, dot below
            "&\u0300;\u0309;\u0303;\u0301;\u0323"
            // D with stroke
            + "&D<\u0111,\u0110"
            // breve, circumflex, horn
            + "&Z<\u0306<\u0302<\u031B";
    */
    
    public static final String DELIMITERS = "\\p{Cntrl}\\s\\p{Punct}\u0080-\u00BF\u2000-\uFFFF";

    private static Collator primary, secondary;

    /**
     * Constructor for the VietComparator object
     */
    public VietComparator2() {
        if (primary == null) {
            primary = Collator.getInstance(new Locale("vi"));
            secondary = (Collator) primary.clone();
            secondary.setStrength(Collator.SECONDARY);
        }
    }

    /**
     * Compares its two arguments for Vietnamese lexical order.
     * 
     * @param o1
     *            the first argument to be compared.
     * @param o2
     *            the second argument to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare(String o1, String o2) {

        int result;
        String[] s1 = (" " + o1).split("[" + DELIMITERS + "]+");
        String[] s2 = (" " + o2).split("[" + DELIMITERS + "]+");

        // compare by syllable until a difference is found
        for (int i = 1; i < s1.length && i < s2.length; i++) {
            result = secondary.compare(s1[i], s2[i]);
            if (result != 0) {
                return result;
            }
        }

        // if one argument starts with the other, the shorter is less
        if (s1.length > s2.length) {
            return 1;
        } else if (s1.length < s2.length) {
            return -1;
        }

        // consider case
        for (int i = 1; i < s1.length; i++) {
            result = primary.compare(s1[i], s2[i]);
            if (result != 0) {
                return result;
            }
        }

        s1 = (o1 + " ").split("[^" + DELIMITERS + "]+");
        s2 = (o2 + " ").split("[^" + DELIMITERS + "]+");

        // compare groups of characters between syllables
        for (int i = 1; i < s1.length - 1 && i < s2.length - 1; i++) {
            result = primary.compare(s1[i], s2[i]);
            if (result != 0) {
                return result;
            }
        }

        // compare group of characters before first syllable
        result = primary.compare(s1[0], s2[0]);
        if (result != 0) {
            return result;
        }

        // compare group of characters after last syllable
        return primary.compare(o1, o2);

    }

}
