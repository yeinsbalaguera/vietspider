import java.net.URL;

import org.vietspider.html.HTMLDocument;
import org.vietspider.html.HTMLNode;
import org.vietspider.html.Name;
import org.vietspider.html.NodeIterator;
import org.vietspider.html.parser.HTMLParser2;
import org.vietspider.token.attribute.Attributes;
/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 21, 2009  
 */
public class JavascriptExtractor {

  public static void main(String[] args) throws Exception {
    URL url = new URL("http://java.sun.com/");
    HTMLDocument document = new HTMLParser2().createDocument(url.openStream(), "utf-8");
    
    NodeIterator iterator =  document.getRoot().iterator();
    while(iterator.hasNext()) {
      HTMLNode node = iterator.next();
      if(node.isNode(Name.SCRIPT)) {
        if(node.hasChildren() && node.getChildren().size() > 0) {
          System.out.println("===================================================");
          System.out.println(node.getChild(0).getTextValue());
        }
        continue;
      } 
      Attributes attributes = node.getAttributes(); 
      for(int i = 0; i < attributes.size(); i++) {
        String value = attributes.get(i).getValue();
        if(attributes.get(i).getName().startsWith("on") 
            || value.toLowerCase().startsWith("javascript")) {
          System.out.println("===================================================");
          System.out.println(value);
        }
      }
    }
  }

}
