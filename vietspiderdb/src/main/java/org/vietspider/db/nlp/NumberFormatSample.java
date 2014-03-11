/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.db.nlp;

import java.text.DecimalFormat;
import java.util.Currency;
import java.util.Locale;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Sep 23, 2009  
 */
public class NumberFormatSample {
  
  public static void main(String[] args) {
    Currency currency = Currency.getInstance(Locale.FRANCE);
    DecimalFormat format = new DecimalFormat();
    format.setCurrency(currency);
    System.out.println(format.format(2000000000l));

  }
  
}
