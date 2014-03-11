package org.vietspider.common.util;

import java.util.Arrays;

public class CoreSort {
	public static void main(String[] args) {
		int size = 1000000;
		String[] input = new String[size];
		for (int i = 0; i < size; i++) {
			input[i] = "" + (int) (10000.0 * Math.sin(i));
		}
		long start = System.currentTimeMillis();
		Arrays.sort(input);
		long end = System.currentTimeMillis();
		System.out.println("Sorted in " + (end - start) + " ms");
	}
}
