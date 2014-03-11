import org.vietspider.token.Node;
import org.vietspider.token.attribute.Attribute;
import org.vietspider.token.attribute.AttributeParser;
import org.vietspider.token.attribute.Attributes;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Oct 9, 2006
 */
public class AttributeExample {
  
  static private class MockNode implements  Node<Object> {
    
    public boolean isTag() {
      return true;
    }

    public String name() {
      return "MockNode";
    }

    public Object getName() {
      return "MockNode";
    }
    
    public char[] getValue() {
      return value.toCharArray();
    }
    
    public void setValue(char[] chars) {
      value = new String(chars);
    }
    
    private String value = "";
  }
  
  public static void main(String[] args) {
    MockNode node = new MockNode();
    
    StringBuilder builder = new StringBuilder();
    builder.append("name=\"nodeName1\" size=\"'15'\" maxlength=30\" ");
    builder.append("onmouseover=\"this.style.visibility='visible';\" ");
    builder.append("style=\"width: 80px;\" type='text' ");
    builder.append(" src='resources/2D-47.gif'");
    node.setValue(builder.toString().toCharArray());
    
    
    Attributes attrs = AttributeParser.getInstance().get(node); 
    for(Attribute attr : attrs){
      System.out.println(attr.getName()+" : "+attr.getValue());
    }
  }
  
}
