import org.vietspider.parser.xml.XMLDocument;
import org.vietspider.serialize.Object2XML;
import org.vietspider.serialize.XML2Object;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2008  
 */
public class Object2XMLEx {
  
  public static void main(String[] args) throws Exception {
    Student student = new Student("123", "Nguyen Van A");
    
    Object2XML bean2XML = Object2XML.getInstance();
    XMLDocument document = bean2XML.toXMLDocument(student);
    
    String text = document.getTextValue();
    System.out.println(text);
    
    
    XML2Object xml2Bean = XML2Object.getInstance();
    Student student2 = xml2Bean.toObject(Student.class, text);
    
    System.out.println("\nstudent 2 name: " +student2.getName());
    
  }
  
  
}
