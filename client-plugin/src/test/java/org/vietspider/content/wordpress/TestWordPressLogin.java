/***************************************************************************
 * Copyright 2001-2011 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content.wordpress;

import java.io.File;
import java.net.URL;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.DefaultHttpClient;
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
public class TestWordPressLogin {
  public static void main(String[] args) throws Exception {
    //    File file  = new File("D:\\java\\test\\vsnews\\data\\");

    File file = new File("D:\\Releases\\Releases\\VietSpider3_18_News_Vi_Windows\\data\\");

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

    String homepage = "https://doibuon.wordpress.com/wp-admin/";
    //    String loginURL =  "http://127.0.0.1/wordpress/wp-admin/post-new.php";
    String loginURL =  "https://en.wordpress.com/wp-login.php";
    String username = "doibuon";
    String password = "thuan123";
    String charset = "utf8";

    webClient.setURL(homepage, new URL(homepage));

    CookieSpecFactory csf = new CookieSpecFactory() {
      public CookieSpec newInstance(HttpParams params) {
        return new BrowserCompatSpec() {   
          @Override
          public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
            System.out.println(" da caht vao daday asdasd");
            //                  return true;
          }
        };
      }
    };

    DefaultHttpClient httpclient = (DefaultHttpClient) webClient.getHttpClient();
    httpclient.getCookieSpecs().register("easy", csf);
    httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");

    //    System.out.println("webclient 1 " + webClient.hashCode());
    HttpMethodHandler handler = new HttpMethodHandler(webClient);
    HttpSessionUtils httpSession = new HttpSessionUtils(handler, "Error");
    StringBuilder builder = new StringBuilder(loginURL).append('\n');
    builder.append(username).append(':').append(password);
    webClient.setLog(true);
    System.out.println(" truoc khi login");
    boolean login = httpSession.login(builder.toString(), charset, new URL(loginURL), null);
    //    login = httpSession.login(builder.toString(), charset, new URL(loginURL), null);
    System.out.println(" chay vao day " + login);
    webClient.setLog(false);

    System.exit(0);
  }
}
