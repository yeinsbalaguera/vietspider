

import java.io.File;
import java.net.URL;

import org.vietspider.common.io.DataReader;
import org.vietspider.html.parser.CharsToken;
import org.vietspider.html.parser.NodeImpl;
import org.vietspider.token.TokenParser;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 19, 2006
 */
public class TokenExample {

  private File file;

  public TokenExample() throws Exception {
    URL url = getClass().getResource("/");
    String path  = url.toString()+File.separator;
    path  = path + File.separator+"resources"+File.separator+"complexshapes.html";
    url = new URL(path);
    file = new File(url.toURI());

    System.out.println(file.getPath());
    System.out.println("*********************************************************");
    DataReader buffer = RWData.getInstance();
    String text = new String(buffer.load(file), "utf-8");
    CharsToken tokens = new CharsToken();
    TokenParser tokenParser = new TokenParser();
    tokenParser.createBeans(tokens, text.toCharArray());
    while(tokens.hasNext()){
      NodeImpl node = tokens.pop();
      String value = new String(node.getValue());
      System.out.println(value +" : "+ node.getType());
    }
  }

  public static void main(String[] args) throws Exception {
    new TokenExample();
  }
}
