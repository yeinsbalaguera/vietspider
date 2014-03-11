/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawler;

import java.io.InputStream;
import java.net.URL;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 7, 2009  
 */
public class SimpleClient {
  
  public static void main (String args[]) throws Exception {
    System.out.println ("Starting timer.");
    // Start timer
    Timer timer = new Timer(5000);
    timer.start();

    // Connect to remote host
    URL url = new URL("http://csgt.vn/csgt/");
    InputStream inputStream = url.openStream();
    int read  = -1;
    byte [] buff = new byte[1024];
    while( (read = inputStream.read(buff)) != -1) {
      System.out.println(new String(buff, 0, read, "utf-8"));
    }
//    Socket socket = new Socket ("vietnamnet.vn", 80);
//    System.out.println ("Connected to localhost:2000");

    // Reset timer - timeout can occur on connect
    timer.reset();

    // Create a print stream for writing
//    PrintStream pout = new PrintStream (socket.getOutputStream() );

    // Create a data input stream for reading
//    DataInputStream din = new DataInputStream(socket.getInputStream() );

    // Print hello msg
//    pout.println ("Hello world!");

    // Reset timer - timeout is likely to occur during the read
    timer.reset();

    // Print msg from server
//    System.out.println (din.readLine());

    // Shutdown timer
    timer.stop();

    // Close connection
//    socket.close();
  }
  public static class Timer extends Thread {
    
    /** Rate at which timer is checked */
    protected int m_rate = 100;

    /** Length of timeout */
    private int m_length;

    /** Time elapsed */
    private int m_elapsed;

    /**
     * Creates a timer of a specified length
     * @param  length  Length of time before timeout occurs
     */
    public Timer (int length) {
      // Assign to member variable
      m_length = length;

      // Set time elapsed
      m_elapsed = 0;
    }


    /** Resets the timer back to zero */
    public synchronized void reset() {
      m_elapsed = 0;
    }

    /** Performs timer specific code */
    public void run() {
      // Keep looping
      for (;;) {
        // Put the timer to sleep
        try { 
          Thread.sleep(m_rate);
        } catch (InterruptedException ioe) {
          continue;
        }

        // Use 'synchronized' to prevent conflicts
        synchronized (this) {
          // Increment time remaining
          m_elapsed += m_rate;

          // Check to see if the time has been exceeded
          if (m_elapsed > m_length) {
            // Trigger a timeout
            timeout();
          }
        }

      }
    }

    // Override this to provide custom functionality
    public void timeout() {
      System.err.println ("Network timeout occurred.... terminating");
      System.exit(1);
    }
  }
}
