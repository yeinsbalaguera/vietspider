

import org.vietspider.chars.refs.RefsDecoder;
import org.vietspider.chars.refs.RefsEncoder;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 19, 2006
 */
public class DecodeExample {
  public static void main(String arg[]){
    RefsDecoder ref = new RefsDecoder();
    String text = "&nbsp;&nbsp;&nbsp;" ;
    String value = new String(ref.decode(text.toCharArray()));
    System.out.println("|"+value+"|");
    RefsEncoder encoder = new RefsEncoder(false);
    value = value + "&";
    text = new String(encoder.encode(value.toCharArray()));
    System.out.println(text);
  }
}
