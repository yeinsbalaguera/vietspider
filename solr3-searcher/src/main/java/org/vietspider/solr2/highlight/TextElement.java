/***************************************************************************
 * Copyright 2001-2010 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.solr2.highlight;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 9, 2010  
 */
public class TextElement {
  
  TextElement previous;
  TextElement next;
  
  String value;
  
  public TextElement(String value) {
    this.value = value;
  }
  
  public TextElement getPrevious() { return previous; }
  public void setPrevious(TextElement previous) { this.previous = previous;}

  public TextElement getNext() { return next; }
  public void setNext(TextElement next) { this.next = next; }
  public boolean hasNext() { return next != null; }
  
  public String getValue() { return value; }
  public void setValue(String value) { this.value = value; }

  static public String toTextNotMarked(String text) {
    StringBuilder builder = new StringBuilder();
    for(int i = 0; i < text.length(); i++) {
      char c = text.charAt(i);
      switch (c) {
      case 'À':
      case 'Á':
      case 'Ả':
      case 'Ã':
      case 'Ạ': 
        
      case 'Ă': 
      case 'Ằ': 
      case 'Ắ': 
      case 'Ẳ': 
      case 'Ẵ': 
      case 'Ặ': 
        
      case 'Â': 
      case 'Ầ': 
      case 'Ấ': 
      case 'Ẩ': 
      case 'Ẫ': 
      case 'Ậ':
        
      case 'à':
      case 'á':     
      case 'ả':
      case 'ã':
      case 'ạ':
        
      case 'ă': 
      case 'ằ': 
      case 'ắ': 
      case 'ẳ': 
      case 'ẵ': 
      case 'ặ':
        
      case 'â': 
      case 'ầ': 
      case 'ấ': 
      case 'ẩ': 
      case 'ẫ': 
      case 'ậ':
        builder.append('a');
        break;
        
      case 'Đ': 
      case 'đ': 
        builder.append('d');
        break;
        
      case 'È': 
      case 'É': 
      case 'Ẻ': 
      case 'Ẽ': 
      case 'Ẹ':
        
      case 'Ê':
      case 'Ề': 
      case 'Ế': 
      case 'Ể': 
      case 'Ễ': 
      case 'Ệ':
        
      case 'è': 
      case 'é': 
      case 'ẻ': 
      case 'ẽ': 
      case 'ẹ': 
        
      case 'ê':
      case 'ề': 
      case 'ế': 
      case 'ể': 
      case 'ễ': 
      case 'ệ':
        builder.append('e');
        break;
        
      case 'Ì': 
      case 'Í': 
      case 'Ỉ': 
      case 'Ĩ': 
      case 'Ị':
        
      case 'ì': 
      case 'í': 
      case 'ỉ': 
      case 'ĩ': 
      case 'ị':
        builder.append('i');
        break;     
      case 'Ò': 
      case 'Ó': 
      case 'Ỏ': 
      case 'Õ': 
      case 'Ọ':
        
      case 'Ô':
      case 'Ồ': 
      case 'Ố': 
      case 'Ổ': 
      case 'Ỗ': 
      case 'Ộ':
        
      case 'Ơ': 
      case 'Ờ': 
      case 'Ớ': 
      case 'Ở': 
      case 'Ỡ': 
      case 'Ợ': 
        
      case 'ò': 
      case 'ó': 
      case 'ỏ': 
      case 'õ': 
      case 'ọ': 
        
      case 'ô':
      case 'ồ': 
      case 'ố': 
      case 'ổ': 
      case 'ỗ': 
      case 'ộ': 
        
      case 'ơ': 
      case 'ờ':
      case 'ớ': 
      case 'ở': 
      case 'ỡ': 
      case 'ợ': 
        builder.append('o');
        break;
        
      case 'Ù': 
      case 'Ú': 
      case 'Ủ': 
      case 'Ũ': 
      case 'Ụ':
        
      case 'Ư': 
      case 'Ừ': 
      case 'Ứ': 
      case 'Ử': 
      case 'Ữ': 
      case 'Ự':
        
      case 'ù': 
      case 'ú': 
      case 'ủ':
      case 'ũ': 
      case 'ụ': 
        
      case 'ư': 
      case 'ừ':
      case 'ứ': 
      case 'ử': 
      case 'ữ': 
      case 'ự':
        builder.append('u');
        break;
        
      case 'Ỳ': 
      case 'Ý': 
      case 'Ỷ':
      case 'Ỹ': 
      case 'Ỵ': 
      case 'ỳ': 
      case 'ý': 
      case 'ỷ': 
      case 'ỹ': 
      case 'ỵ': 
        builder.append('y');
        break;
      default: 
        if(Character.isUpperCase(c)) {
          c = Character.toLowerCase(c);
        }
        builder.append(c);
      break;
      }
    }
    return builder.toString();
  }
}
