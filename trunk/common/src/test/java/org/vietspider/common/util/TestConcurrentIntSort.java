package org.vietspider.common.util;

import java.util.Arrays;

public class TestConcurrentIntSort {
  
	public static void main(String[] args) throws Exception {
		int testNo = 100;
		long total1 = 0;
		long total2 = 0;
		for (int i = 0; i < testNo; i++) {
			int objNo = 1 + (int) (1000000 * Math.random());
			int[] toSort = new int[objNo];
			for (int j = 0; j < objNo; j++) {
				toSort[j] = (int) (objNo * Math.random());
			}
			// make a copy for verification
			int[] toSortCore = new int[objNo];
			System.arraycopy(toSort, 0, toSortCore, 0, objNo);

			// start new concurrent sort
			System.out.print("Sorting " + objNo + "... ");
			long start = System.currentTimeMillis();
			ConcurrentIntSort.sort(toSort);
			long end = System.currentTimeMillis();
			total1 += (end - start);
			// sort with the core implementation
			start = System.currentTimeMillis();
			Arrays.sort(toSortCore);
			end = System.currentTimeMillis();
			total2 += (end - start);
			// verify
			for (int j = 0; j < objNo; j++) {
				if (toSort[j] != toSortCore[j])
					throw new Exception("Not sorted at index " + j);
			}
			System.out.println(" Done.");
		}
		if(total2 < total1) {
      System.err.println("  ===  > "+ total1 + " : " + total2);
    } else {
      System.out.println("  ===  > "+ total1 + " : " + total2);
    }
	}
}
