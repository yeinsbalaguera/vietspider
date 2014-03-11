/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.crawler.services;

import java.io.File;

import javax.swing.ImageIcon;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Dec 6, 2006
 */
public class TestImageIcon {
  public static void main(String[] args)  throws Exception {
    ImageIcon icon = new ImageIcon(new File("E:\\temp\\1CA00JT76.gif").toURL());
    System.out.println(icon.getIconWidth());
    System.out.println(icon.getIconHeight());
  }
}
