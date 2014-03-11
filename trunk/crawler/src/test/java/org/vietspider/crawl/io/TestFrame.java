/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.crawl.io;

import java.awt.HeadlessException;

import javax.swing.JFrame;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jun 2, 2008  
 */
public class TestFrame extends JFrame {

  public TestFrame() throws HeadlessException {
//       this.setSize(Toolkit.getDefaultToolkit().getScreenSize());
//       this.setUndecorated(true);
      this.setVisible(true);
      this.setExtendedState(JFrame.MAXIMIZED_BOTH);
  }
  
  public static void main(String[] args) {
    new TestFrame();
  }
}