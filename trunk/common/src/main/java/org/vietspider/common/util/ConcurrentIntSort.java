package org.vietspider.common.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentIntSort {

  private static void mergeSort(int[] src, int[] dest, int low, int high, int off) {
    int length = high - low;

    // Insertion sort on smallest arrays
    if (length < 7) {
      for (int i = low; i < high; i++) {
        for (int j = i; j > low && dest[j - 1]  > dest[j]; j--) {
          swap(dest, j, j - 1);
        }
      }
      return;
    }

    // Recursively sort halves of dest into src
    int destLow = low;
    int destHigh = high;
    low += off;
    high += off;
    int mid = (low + high) >> 1;
    mergeSort(dest, src, low, mid, -off);
    mergeSort(dest, src, mid, high, -off);

    // If list is already sorted, just copy from src to dest. This is an
    // optimization that results in faster sorts for nearly ordered lists.
    if (src[mid - 1] <= src[mid]) {
      System.arraycopy(src, low, dest, destLow, length);
      return;
    }

    // Merge sorted halves (now in src) into dest
    for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
      if (q >= high || p < mid && (src[p] <= src[q]))
        dest[i] = src[p++];
      else
        dest[i] = src[q++];
    }
  }

  private static void swap(int[] x, int a, int b) {
    int t = x[a];
    x[a] = x[b];
    x[b] = t;
  }

  public static void sort(final int[] a) {
    final int[] aux = a.clone();

    // special case - small array or one processor. Revert to regular
    // implementation.
    if ((a.length < 7) || (Runtime.getRuntime().availableProcessors() == 1)) {
      mergeSort(aux, a, 0, a.length, 0);
      return;
    }

    final CountDownLatch doneSignal = new CountDownLatch(2);
    ExecutorService e = Executors.newFixedThreadPool(2);

    class WorkerRunnable implements Runnable {
      int start;
      int end;

      WorkerRunnable(int start, int end) {
        this.start = start;
        this.end = end;
      }

      public void run() {
        mergeSort(aux, a, start, end, 0);
        doneSignal.countDown();
      }
    }

    int mid = a.length >> 1;
    e.execute(new WorkerRunnable(0, mid));
    e.execute(new WorkerRunnable(mid, a.length));
    try {
      doneSignal.await(); // wait for all to finish
    } catch (InterruptedException ie) {
    }
    e.shutdown();

    System.arraycopy(a, 0, aux, 0, a.length);
    // merge two halves
    for (int i = 0, p = 0, q = mid; i < a.length; i++) {
      if (q >= a.length || p < mid && (aux[p] <= aux[q])) {
        a[i] = aux[p++];
      } else {
        a[i] = aux[q++];
      }
    }

  }

  /*	public static void main(String[] args) throws Exception {
		int size = 1000000;
		String[] input = new String[size];
		for (int i = 0; i < size; i++) {
			input[i] = "" + (int) (10000.0 * Math.sin(i));
		}

		// make a copy for verification
		String[] inputCore = new String[size];
		System.arraycopy(input, 0, inputCore, 0, size);

		long start = System.currentTimeMillis();
		ConcurrentSort.sort(input);
		long end = System.currentTimeMillis();
		System.out.println("Sorted in " + (end - start) + " ms");

		// sort with the core implementation
		java.util.Arrays.sort(inputCore);
		// verify
		System.out.print("Verifying... ");
		for (int i = 0; i < size; i++) {
			if (input[i].compareTo(inputCore[i]) > 0)
				throw new Exception("Not sorted at index " + i);
		}
		System.out.println(" Done.");
	}*/

}
