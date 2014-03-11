/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.nlp.impl;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Feb 1, 2011  
 */
class PlaceVariantCreator {
  
  void createVariant(Place place, String _name) {
    if(!_name.toLowerCase().startsWith("phường")) return;
    int idx = _name.indexOf(' ');
    if(idx < 0) return;
    _name = _name.substring(idx+1).trim();
    if(_name.length() < 1) return;
    place.addVariant("p." + _name);
    if(Character.isDigit(_name.charAt(0))) {
      place.addVariant("p" + _name);
      place.addVariant("p " + _name);
      place.addVariant("f" + _name);
      place.addVariant("f " + _name);
      place.addVariant("f." + _name);
    }
  }

  void createVariant2(Place place, String _name) {
    //    System.out.println(_name+  ":" +_name.startsWith("xã"));
    if(!_name.toLowerCase().startsWith("xã")) return;
    int idx = _name.indexOf(' ');
    if(idx < 0) return;
    _name = _name.substring(idx+1).trim();
//        System.out.println("+++++ > "+ _name);
    if(_name.length() < 1) return;
    place.addVariant(_name);
    place.addVariant("x." + _name);
    place.addVariant("x " + _name);
  }

  void createVariant3(Place place, String _name) {
    int index = _name.toLowerCase().indexOf("thị trấn");
    if(index != 0) return;
    int idx = _name.indexOf(' ', index+8);
    if(idx < 0) return;
    _name = _name.substring(idx+1).trim();
//      System.out.println("+++++ > "+ _name);
    if(_name.length() < 1) return;
    place.addVariant(_name);
    place.addVariant("tt." + _name);
    place.addVariant("tt " + _name);
  }
  
}
