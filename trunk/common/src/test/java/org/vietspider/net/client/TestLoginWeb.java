/***************************************************************************
 * Copyright 2001-2007 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.net.client;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 21, 2007  
 */
public class TestLoginWeb {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loginWeb(String login) throws Exception {
    try{
      String [] elements = login.split("\n");
      List<String[]> name_values = new ArrayList<String[]>();
      for(int i = 1; i < elements.length; i++) {
        int idx = elements[i].indexOf('=');
        if(idx < 0) continue;
        String [] values = new String[2];
        values[0] = elements[i].substring(0, idx);
        values[1] = elements[i].substring(idx+1);
        name_values.add(values);
      }
//      HttpResponse httpResponse = webClient.login(elements[0], name_values);
//
//      HttpResponseReader httpResponseReader = new HttpResponseReader();
//      return httpResponseReader.readBody(httpResponse);
      
      return null;
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static byte[] loadContent(String address) throws Exception {
    HttpGet httpGet = null;
    try {
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpGet = webClient.createGetMethod(address, "");      

      if(httpGet == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpGet);

//      HttpEntity httpEntity = httpResponse.getEntity();
//      return webClient.decodeResponse(webClient.readData(httpEntity.getContent()), httpEntity.getContentEncoding());
      HttpResponseReader httpResponseReader = new HttpResponseReader();
      return httpResponseReader.readBody(httpResponse);
      
    } catch(Exception exp){
      exp.printStackTrace();
      return null;
    }
  }
  
  public static void main(String[] args) throws Exception {
    String home = "http://www.cyvee.com/index.aspx";
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
    
    String login = "http://www.cyvee.com/Index.aspx\n"+
                   "__EVENTTARGET= \n"+
                   "__EVENTARGUMENT= \n"+
                   "__VIEWSTATE=/wEPDwUJNzU0Njg2OTExD2QWAmYPZBYCZg9kFgQCAg9kFgJmDxYCHgRocmVmBT1odHRwOi8vd3d3LmN5dmVlLmNvbS9BcHBfVGhlbWVzL0RlZmF1bHQvdmktVk4vaW1hZ2VzL2xvZ28uaWNvZAIED2QWBAIBD2QWCAIBD2QWCAIDDxYCHgdWaXNpYmxlaGQCBQ8WAh8BaGQCBw8WAh8BaGQCCw8PFgIfAWhkZAIDDxYCHwFoZAIFDw8WAh4LUmVmZXJyZXJVcmwFHC9Nb2R1bGVzL015Q3l2ZWUvTXlIb21lLmFzcHhkFgJmD2QWBAIFDw8WAh4LTmF2aWdhdGVVcmwFKGh0dHA6Ly93d3cuY3l2ZWUuY29tL0ZvcmdldFBhc3N3b3JkLmFzcHhkZAIGDw8WAh8DBSJodHRwOi8vd3d3LmN5dmVlLmNvbS9SZWdpc3Rlci5hc3B4ZGQCBw9kFgoCAQ8PFgIfAwUraHR0cDovL3d3dy5jeXZlZS5jb20vTW9kdWxlcy9OZXdzL0hvbWUuYXNweGRkAgIPDxYCHwMFNmh0dHA6Ly93d3cuY3l2ZWUuY29tL01vZHVsZXMvTWVtYmVyL1NlYXJjaEJ5UGhvdG8uYXNweGRkAgMPDxYCHwMFTmh0dHA6Ly93d3cuY3l2ZWUuY29tL01vZHVsZXMvR3JvdXAvSG9tZUdyb3Vwcy5hc3B4P3RwPTMmY3R5PS0xJmtleT0mbW9kZT1GYWxzZWRkAgQPDxYCHwMFRGh0dHA6Ly93d3cuY3l2ZWUuY29tL01vZHVsZXMvT3Bwb3J0dW5pdHkvU2VhcmNoT3Bwb3J0dW5pdHkuYXNweD90cD0xZGQCBg8PFgIfAwUraHR0cDovL3d3dy5jeXZlZS5jb20vTW9kdWxlcy9RQS9Ccm93c2UuYXNweGRkAgIPZBYCAgEPZBYKAgIPZBYCAgMPFgIeC18hSXRlbUNvdW50AgUWCgIBD2QWAgIBDw8WBh4HVG9vbFRpcAU0VFAgSENNIHPhur0gxJHDs24gR2nDoW5nIHNpbmggduG7m2kgdHJp4buBdSBjxrDhu51uZx4EVGV4dAU8VFAgSENNIHPhur0gxJEmIzI0MztuIEdpJiMyMjU7bmcgc2luaCB24bubaSB0cmnhu4F1IGPGsOG7nW5nHwMFO2h0dHA6Ly93d3cuY3l2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cDovL3d3dy5jeXZlZS5jb20vTW9kdWxlcy9FdmVudC9FdmVudC5hc3B4P2VpZD00OTNkZAIBD2QWAgIBDw8WBh8GBR5VSyBBbHVtbmkgVmlldG5hbSBHYWxhIEV2ZW5pbmcfBQUeVUsgQWx1bW5pIFZpZXRuYW0gR2FsYSBFdmVuaW5nHwMFNWh0dHA6Ly93d3cuY3l2ZWUuY29tL01vZHVsZXMvRXZlbnQvRXZlbnQuYXNweD9laWQ9NDQzZGQCAg9kFgICAQ8PFgYfBgUkS29hbGEgU3BpZGF0ZSA3LjA6IGjhurluIDcgcGgmIzI1MDt0HwUFIEtvYWxhIFNwaWRhdGUgNy4wOiBo4bq5biA3IHBow7p0HwMFNWh0dHA6Ly93d3cuY3l2ZWUuY29tL01vZHVsZXMvRXZlbnQvRXZlbnQuYXNweD9laWQ9NDY1ZGQCAw9kFgICAQ8PFgYfBgUcxJDhu6ljIEh1eSAncyBmaXJzdCBsaXZlc2hvdx8FBRzEkOG7qWMgSHV5ICdzIGZpcnN0IGxpdmVzaG93HwMFNWh0dHA6Ly93d3cuY3l2ZWUuY29tL01vZHVsZXMvRXZlbnQvRXZlbnQuYXNweD9laWQ9NTA4ZGQCBA9kFgICAQ8PFgYfBgU0UGFydHkgR2kmIzIyNTtuZyBTaW5oIGcmIzIyNjt5IHF14bu5IHThu6sgdGhp4buHbi4uLh8FBT1QYXJ0eSBHacOhbmcgU2luaCBnw6J5IHF14bu5IHThu6sgdGhp4buHbiAiQ0hSSVNUTUFTIEJBTlFVRVQiHwMFNWh0dHA6Ly93d3cuY3l2ZWUuY29tL01vZHVsZXMvRXZlbnQvRXZlbnQuYXNweD9laWQ9NDEyZGQCBQ8PFgIeB0lTSU5ERVhnZBYGAgMPDxYCHghJbWFnZVVybAVRaHR0cDovL3d3dy5jeXZlZS5jb20vR2V0UGhvdG8uYXNweD9uYW1lPTI1LmpwZyZzPTc1eDc1JnRwPTEmaWQ9NjMzMzM4NDQ3MDE2MzM0MTY0ZGQCBQ8PFgYfBgUWT05MSU5FIE1BUktFVElORyBHUk9VUB8FBRZPTkxJTkUgTUFSS0VUSU5HIEdST1VQHwMFOGh0dHA6Ly93d3cuY3l2ZWUuY29tL01vZHVsZXMvR3JvdXAvVmlld0dyb3VwLmFzcHg/Z2lkPTI1ZGQCBw8WAh4FY2xhc3MFEENvbnRlbnRGaXhIZWlnaHQWAgIBDxYCHwYFYU9ubGluZSBtYXJrZXRpbmcgb3IgRW1hcmtldGluZyBvciBJbnRlcm5ldCBNYXJrZXRpbmcsIGlzIG1hcmtldGluZyB0aGF0IHVzZXMgdGhlIEludGVybmV0LiBUaGUuLi5kAgYPZBYCAgMPFgIfBAIHFg5mD2QWAmYPFQVHaHR0cDovL3d3dy5jeXZlZS5jb20vTW9kdWxlcy9NeUN5dmVlL1Byb2ZpbGUvTWVtYmVyUHJvZmlsZS5hc3B4P2lkPTE1MzFTaHR0cDovL3d3dy5jeXZlZS5jb20vR2V0UGhvdG8uYXNweD9uYW1lPTE1MzEuanBnJnM9NTl4NzUmdHA9MCZpZD02MzMzMzg0NDcwMTY0OTA0MDlHaHR0cDovL3d3dy5jeXZlZS5jb20vTW9kdWxlcy9NeUN5dmVlL1Byb2ZpbGUvTWVtYmVyUHJvZmlsZS5hc3B4P2lkPTE1MzENUGhhbiBOZ29jIExhbgNMYW5kAgEPZBYCZg8VBUdodHRwOi8vd3d3LmN5dmVlLmNvbS9Nb2R1bGVzL015Q3l2ZWUvUHJvZmlsZS9NZW1iZXJQcm9maWxlLmFzcHg/aWQ9ODU1N1NodHRwOi8vd3d3LmN5dmVlLmNvbS9HZXRQaG90by5hc3B4P25hbWU9ODU1Ny5qcGcmcz01OXg3NSZ0cD0wJmlkPTYzMzMzODQ0NzAxNjQ5MDQwOUdodHRwOi8vd3d3LmN5dmVlLmNvbS9Nb2R1bGVzL015Q3l2ZWUvUHJvZmlsZS9NZW1iZXJQcm9maWxlLmFzcHg/aWQ9ODU1NxtQaOG6oW0gVGjhu4sgVGh14buzIETGsMahbmcOVGh14buzIETGsMahbmdkAgIPZBYCZg8VBUdodHRwOi8vd3d3LmN5dmVlLmNvbS9Nb2R1bGVzL015Q3l2ZWUvUHJvZmlsZS9NZW1iZXJQcm9maWxlLmFzcHg/aWQ9MzA1N1NodHRwOi8vd3d3LmN5dmVlLmNvbS9HZXRQaG90by5hc3B4P25hbWU9MzA1Ny5qcGcmcz01OXg3NSZ0cD0wJmlkPTYzMzMzODQ0NzAxNjQ5MDQwOUdodHRwOi8vd3d3LmN5dmVlLmNvbS9Nb2R1bGVzL015Q3l2ZWUvUHJvZmlsZS9NZW1iZXJQcm9maWxlLmFzcHg/aWQ9MzA1NxJQaGFuIFR14bqlbiBRdeG7kWMGUXXhu5FjZAIDD2QWAmYPFQVIaHR0cDovL3d3dy5jeXZlZS5jb20vTW9kdWxlcy9NeUN5dmVlL1Byb2ZpbGUvTWVtYmVyUHJvZmlsZS5hc3B4P2lkPTEwNjE0VGh0dHA6Ly93d3cuY3l2ZWUuY29tL0dldFBob3RvLmFzcHg/bmFtZT0xMDYxNC5qcGcmcz01OXg3NSZ0cD0wJmlkPTYzMzMzODQ0NzAxNjQ5MDQwOUhodHRwOi8vd3d3LmN5dmVlLmNvbS9Nb2R1bGVzL015Q3l2ZWUvUHJvZmlsZS9NZW1iZXJQcm9maWxlLmFzcHg/aWQ9MTA2MTQTS2gmIzIyNTtuaCBOaCYjMjI3OwhOaCYjMjI3O2QCBA9kFgJmDxUFSGh0dHA6Ly93d3cuY3l2ZWUuY29tL01vZHVsZXMvTXlDeXZlZS9Qcm9maWxlL01lbWJlclByb2ZpbGUuYXNweD9pZD0xMzczOFRodHRwOi8vd3d3LmN5dmVlLmNvbS9HZXRQaG90by5hc3B4P25hbWU9MTM3MzguanBnJnM9NTl4NzUmdHA9MCZpZD02MzMzMzg0NDcwMTY0OTA0MDlIaHR0cDovL3d3dy5jeXZlZS5jb20vTW9kdWxlcy9NeUN5dmVlL1Byb2ZpbGUvTWVtYmVyUHJvZmlsZS5hc3B4P2lkPTEzNzM4EFRydW9uZyBIdXUgVGh1YW4FVGh1YW5kAgUPZBYCZg8VBUhodHRwOi8vd3d3LmN5dmVlLmNvbS9Nb2R1bGVzL015Q3l2ZWUvUHJvZmlsZS9NZW1iZXJQcm9maWxlLmFzcHg/aWQ9MTI1NTdbaHR0cDovL3d3dy5jeXZlZS5jb20vR2V0UGhvdG8uYXNweD9uYW1lPW1lbWJlcl8xMjU1Ny5qcGcmcz01OXg3NSZ0cD0wJmlkPTYzMzMzODQ0NzAxNjQ5MDQwOUhodHRwOi8vd3d3LmN5dmVlLmNvbS9Nb2R1bGVzL015Q3l2ZWUvUHJvZmlsZS9NZW1iZXJQcm9maWxlLmFzcHg/aWQ9MTI1NTcHd2VuIHN1bgNzdW5kAgYPZBYCZg8VBUdodHRwOi8vd3d3LmN5dmVlLmNvbS9Nb2R1bGVzL015Q3l2ZWUvUHJvZmlsZS9NZW1iZXJQcm9maWxlLmFzcHg/aWQ9MTE2N1podHRwOi8vd3d3LmN5dmVlLmNvbS9HZXRQaG90by5hc3B4P25hbWU9bWVtYmVyXzExNjcuanBnJnM9NTl4NzUmdHA9MCZpZD02MzMzMzg0NDcwMTY0OTA0MDlHaHR0cDovL3d3dy5jeXZlZS5jb20vTW9kdWxlcy9NeUN5dmVlL1Byb2ZpbGUvTWVtYmVyUHJvZmlsZS5hc3B4P2lkPTExNjcdVGgmIzIyNTtpIEhvJiMyMjQ7bmcgQW5oIFPGoW4EU8ahbmQYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgEFNWN0bDAwJGN0bDAwJGhlYWRlclBsYWNlSG9sZGVyJHVjUXVpY2tMb2dpbiRjYlJlbWVtYmVyzMW0G301nQx4DqnEowVgCAVcxxo=\n"+
                   "ctl00$ctl00$headerPlaceHolder$ucQuickLogin$txtEmail=nhudinhthuan@yahoo.com\n"+
                   "ctl00$ctl00$headerPlaceHolder$ucQuickLogin$txtPassword=head123\n"+
                   "ctl00$ctl00$headerPlaceHolder$ucQuickLogin$btnLogin=Đăng nhập\n"+
                   "__EVENTVALIDATION=/wEWCALAi6q5BQL8vfm/BALFheD3BwLF29auDgL0mJb1BQLM7r6IDQK719brCgL8lb33DwL86pKBdNv9wAl447lMkRvoNXXD";
    
    byte [] bytes = loginWeb(login);
    writer.save(new File("D:\\Temp\\webclient\\a.html"), bytes);
    
    bytes = loginWeb(login);
    writer.save(new File("D:\\Temp\\webclient\\a1.html"), bytes);
    
    bytes = loadContent(home);
    writer.save(new File("D:\\Temp\\webclient\\b.html"), bytes);
    
  }
  
}
