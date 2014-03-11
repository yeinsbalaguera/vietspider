import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.html.util.HyperLinkUtil;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.Attributes;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 19, 2006
 */
public class AttributeExplorer {
  
  private File file;

  public AttributeExplorer() throws Exception {
    URL url = getClass().getResource("/");
    String path  = url.toString()+File.separator+".."+File.separator+"example";
    path  = path + File.separator+"resources"+File.separator+"complexshapes.html";
    url = new URL(path);    
    file = new File(url.toURI());

    System.out.println(file.getPath());
    System.out.println("*********************************************************");    
  }
  
  public List<String> getAttribute(String nodeName, String attrName) throws Exception {  
    HTMLParser2 parser2 = new HTMLParser2();
    HTMLDocument document  = parser2.createDocument(file, null);
    
    List<String> attrValues = new ArrayList<String>();
    
    NodeIterator iterator = document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(node.isNode(nodeName)) {
        Attributes attributes = node.getAttributes();
        Attribute attribute = attributes.get(attrName);
        if(attribute == null) continue;
        attrValues.add(attribute.getValue());
      }
    }
    return attrValues;
  }
  
  public List<String> getLinks() throws Exception {
    HTMLParser2 parser2 = new HTMLParser2();
    HTMLDocument document  = parser2.createDocument(file, null);
    HyperLinkUtil linkUtil = new HyperLinkUtil() ;
    return linkUtil.scanSiteLink(document.getRoot());
  }
  
  public static void main(String[] args) throws Exception {
    AttributeExplorer explorer = new AttributeExplorer();
    List<String> list = explorer.getAttribute("table", "width");
    System.out.println(list.size());
    
    list = explorer.getAttribute("img", "src");
    System.out.println(list.size());
    
    System.out.println("*********************************************************");
    list = explorer.getLinks();
    for(String ele : list){
      System.out.println(ele);
    }
  }

}
