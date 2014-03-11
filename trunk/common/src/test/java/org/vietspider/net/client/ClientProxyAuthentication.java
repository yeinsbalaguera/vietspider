/***************************************************************************
 * Copyright 2001-2009 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Jul 28, 2009  
 */
public class ClientProxyAuthentication {
  public static void main(String[] args) throws Exception {
    
    DefaultHttpClient httpclient = new DefaultHttpClient();

    httpclient.getCredentialsProvider().setCredentials(
            new AuthScope("proxy", 8080), 
            new UsernamePasswordCredentials("vpbtc\\khach", "khach"));

    HttpHost targetHost = new HttpHost("www.verisign.com", 443, "https"); 
    HttpHost proxy = new HttpHost("proxy", 8080); 

    httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

    HttpGet httpget = new HttpGet("/");
    
    System.out.println("executing request: " + httpget.getRequestLine());
    System.out.println("via proxy: " + proxy);
    System.out.println("to target: " + targetHost);
    
    HttpResponse response = httpclient.execute(targetHost, httpget);
    HttpEntity entity = response.getEntity();

    System.out.println("----------------------------------------");
    System.out.println(response.getStatusLine());
    if (entity != null) {
        System.out.println("Response content length: " + entity.getContentLength());
    }
    if (entity != null) {
        entity.consumeContent();
    }
    
    // When HttpClient instance is no longer needed, 
    // shut down the connection manager to ensure
    // immediate deallocation of all system resources
    httpclient.getConnectionManager().shutdown();        
}

}
