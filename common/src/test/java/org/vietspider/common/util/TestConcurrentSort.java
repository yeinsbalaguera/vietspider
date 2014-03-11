package org.vietspider.common.util;

import java.util.Arrays;

public class TestConcurrentSort {
  
	public static void main(String[] args) throws Exception {
		int testNo = 1000;
		for (int i = 0; i < testNo; i++) {
			int objNo = 1 + (int) (100000 * Math.random());
			String[] toSort = new String[objNo];
			for (int j = 0; j < objNo; j++) {
				int strLen = 1 + (int) (100 * Math.random());
				StringBuffer strBuf = new StringBuffer();
				for (int k = 0; k < strLen; k++) {
					char c = (char) (32 + (int) (100 * Math.random()));
					strBuf.append(c);
				}
				toSort[j] = strBuf.toString();
			}
			// make a copy for verification
			String[] toSortCore = new String[objNo];
			System.arraycopy(toSort, 0, toSortCore, 0, objNo);

			// start new concurrent sort
			System.out.println("Sorting " + objNo + "... ");
			long start = System.currentTimeMillis();
			
			Arrays.sort(toSortCore);
			long end = System.currentTimeMillis();
			long total1 = (end - start);
			// sort with the core implementation
			start = System.currentTimeMillis();
			ConcurrentSort.sort(toSort);
			end = System.currentTimeMillis();
			long total2 = (end - start);
			if(total2 < total1) {
			  System.err.println("  ===  > "+ total1 + " : " + total2);
			} else {
			  System.out.println("  ===  > "+ total1 + " : " + total2);
			}
			// verify
			for (int j = 0; j < objNo; j++) {
				if (toSort[j].compareTo(toSortCore[j]) > 0)
					throw new Exception("Not sorted at index " + j);
			}
			System.out.println(" Done.");
		}
	}
}
