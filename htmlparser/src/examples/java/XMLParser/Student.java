import org.vietspider.serialize.NodeMap;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 26, 2008  
 */
@NodeMap("student")
public class Student {

  @NodeMap("id")
  private String id;
  @NodeMap("ho-ten")
  private String name;
  
  public Student() {    
  }
  
  public Student(String id, String name) {
    this.id = id;
    this.name = name;
  }
  
  public String getId() { return id; }
  public void setId(String id) { this.id = id; }
  
  public String getName() { return name; }
  public void setName(String name) { this.name = name;}
}
