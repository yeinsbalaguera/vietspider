/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.nukeviet;

import java.io.File;
import java.net.URL;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;
import org.vietspider.browser.HttpSessionUtils;
import org.vietspider.net.client.HttpMethodHandler;
import org.vietspider.net.client.WebClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * May 31, 2011  
 */
public class TestNukeVietLogin {
  public static void main(String[] args) throws Exception {
    File file  = new File("D:\\Program\\VietSpider Build 19\\data\\");
    
//    File file = new File("D:\\Releases\\Releases\\VietSpider3_18_News_Vi_Windows\\data\\");

    System.setProperty("save.link.download", "true");
    System.setProperty("vietspider.data.path", file.getCanonicalPath());
    System.setProperty("vietspider.test", "true");
    
    WebClient webClient = new WebClient();
    
//    String homepage = "http://mvidvn.com/";
//    String loginURL =  "http://localhost/wordpress/wp-admin/post-new.php";
//    String loginURL =  "http://mvidvn.com/wp-login.php";
//    String username = "admin";
//    String password = "nh@tl1nhMvidvn";
//    String charset = "ISO-8859-1";
    
    String homepage = "http://127.0.0.1/nukeviet/";
//    String loginURL =  "http://127.0.0.1/wordpress/wp-admin/post-new.php";
    String loginURL =  "http://127.0.0.1/nukeviet/admin/";
    String username = "admin";
    String password = "123456";
    String charset = "utf8";
    
    webClient.setURL(homepage, new URL(homepage));
    
    
    CookieSpecFactory csf = new CookieSpecFactory() {
      public CookieSpec newInstance(HttpParams params) {
        return new BrowserCompatSpec() {   
          @Override
          public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
            System.out.println(" === >" + cookie.getName() + " : "+ cookie.getValue());
            //                  return true;
          }
        };
      }
    };
    DefaultHttpClient httpclient = (DefaultHttpClient) webClient.getHttpClient();
    httpclient.getCookieSpecs().register("easy", csf);
    httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");
//    httpclient.getCookieStore().addCookie(new BasicClientCookie("nv3c_Ke5k8_sess","6fdf86f7d6098d466eccdb94b358b9eb2130706433"));
//    httpclient.getCookieStore().addCookie(new BasicClientCookie("nv3c_Ke5k8_cltz","420.420.420%257C%252Fnukeviet%252F%257C;"));
//    httpclient.getCookieStore().addCookie(new BasicClientCookie("nv3_cltz", "420.420.420%257C%252F%257C"));
    
//    System.out.println("webclient 1 " + webClient.hashCode());
    HttpMethodHandler handler = new HttpMethodHandler(webClient);
    HttpSessionUtils httpSession = new HttpSessionUtils(handler, "Error");
    StringBuilder builder = new StringBuilder(loginURL).append('\n');
    builder.append(username).append(':').append(password);
    webClient.setLog(true);
    System.out.println(" bat dau ");
    boolean login = httpSession.login(builder.toString(), charset, new URL(loginURL), null);
//    login = httpSession.login(builder.toString(), charset, new URL(loginURL), null);
    System.out.println(" chay vao day " + login);
    webClient.setLog(false);
    
    System.exit(0);
  }
}
