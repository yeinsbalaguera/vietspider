package org.vietspider.link.regex;

import java.util.ArrayList;
import java.util.List;

import org.vietspider.link.regex.Element.ElementType;

public class URIElement  {
	
	protected List<Element> elements = new ArrayList<Element>();

	public List<Element> getElements() { return elements; }
	public void setElements(List<Element> elements) { 
		this.elements = elements;
	}
	public Element getElement(int index) { return elements.get(index); }
	public void addElement(String value, ElementType type) {
	  if(value.startsWith("s=")
	      || value.startsWith("sid=")) {
	    if(isSessionCode(value)) return;
	  }
		elements.add(new Element(value, type));
	}
	
	private boolean isSessionCode(String value) {
	  if(value.length() != 34) return false;
	  for(int i = 2; i < value.length(); i++) {
	    if(!Character.isLetterOrDigit(value.charAt(i))) return false;
	  }
	  return true;
	}
	
	public List<Element> getElements(ElementType type) {
	  List<Element> list = new ArrayList<Element>();
	  for(int i = 0; i < elements.size(); i++) {
	    if(elements.get(i).getType() != type) continue;
	    list.add(elements.get(i));
	  }
	  return list;
	}
	
	public List<Element> getElementsByNotType(ElementType type) {
    List<Element> list = new ArrayList<Element>();
    for(int i = 0; i < elements.size(); i++) {
      if(elements.get(i).getType() == type) continue;
      list.add(elements.get(i));
    }
    return list;
  }
	
	public int size() { return elements.size(); }
	
	public boolean match(String url) {
		return match(URIParser.parse(url));
	}
	
	public boolean match(URIElement url) {
//	  System.out.println("match "+ url.toString());
	  List<Element> hosts_1 = getElements(ElementType.HOST);
	  List<Element> hosts_2 = url.getElements(ElementType.HOST);
	  int i = hosts_1.size() - 1;
	  int j = hosts_2.size() - 1;
	  while(i > -1 && j > -1) {
	    if(!hosts_1.get(i).match(hosts_2.get(j))) return false;
	    i--;
	    j--;
	  }
	  if(j == -1 && i == 0 
	      && hosts_1.get(0).getSubType() != ElementType.WWW) return false;
	  if(i == -1 && j == 0 
	      && hosts_2.get(0).getSubType() != ElementType.WWW) return false;
	  
	  List<Element> elements_1 = getElementsByNotType(ElementType.HOST);
    List<Element> elements_2 = url.getElementsByNotType(ElementType.HOST);
    
	  
//	  System.out.println(elements_1.size() + " : "+ elements_2.size());
//    for(int k = 0; k < elements_1.size(); k++) {
//      System.out.println(elements_1.get(k).getValue());
//    }
//    System.out.println("============");
//    for(int k = 0; k < elements_2.size(); k++) {
//      System.out.println(elements_2.get(k).getValue());
//    }
    
//    System.out.println(" ===================== ");
    
		if(elements_1.size() != elements_2.size()) return false;
		for(i = 0;  i < elements_1.size(); i++) {
			if(!elements_1.get(i).match(elements_2.get(i))) {
//			  System.out.println("error index : "+ i);
//				System.out.println(elements_1.get(i).getValue());
//				System.out.println(elements_2.get(i).getValue());
				return false;
			}
		}
		return true;
	}

}
