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
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.vietspider.chars.URLEncoder;
import org.vietspider.common.io.DataWriter;

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Dec 21, 2007  
 */
public class TestPostMethod {
  
  private static WebClient webClient = new WebClient();
  
  public static byte[] loadContent(String address, List<NameValuePair> nvpList) throws Exception {
    HttpPost httpPost = null;
    try {
      URLEncoder urlEncoder = new URLEncoder();
      address = urlEncoder.encode(address);
      httpPost = webClient.createPostMethod(address, "", nvpList, "utf-8");      

      if(httpPost == null) return null;
      HttpHost httpHost = webClient.createHttpHost(address);
      HttpResponse httpResponse = webClient.execute(httpHost, httpPost);

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
    String home = "http://vietnamese.rti.org.tw/Content/NewsArchives.aspx";
    
    URLEncoder urlEncoder = new URLEncoder();
    home = urlEncoder.encode(home);
    
    System.out.println(home);
    
    
   org.vietspider.common.io.RWData writer = org.vietspider.common.io.RWData.getInstance();
    
    webClient.setURL(null, new URL(home));
    
    List<NameValuePair> nvpList = new ArrayList<NameValuePair>();
    nvpList.add(new BasicNameValuePair("__EVENTTARGET", "DataGrid1$_ctl19$_ctl1"));
    nvpList.add(new BasicNameValuePair("__EVENTARGUMENT", ""));
    nvpList.add(new BasicNameValuePair("__VIEWSTATE", "dDwtMjQzMTI3MDYzO3Q8O2w8aTwxPjs+O2w8dDw7bDxpPDExPjs+O2w8dDxAMDxwPHA8bDxQYWdlQ291bnQ7XyFJdGVtQ291bnQ7XyFEYXRhU291cmNlSXRlbUNvdW50O0RhdGFLZXlzOz47bDxpPDEzPjtpPDE1PjtpPDE5MT47bDw+Oz4+Oz47Ozs7Ozs7Ozs7PjtsPGk8MD47PjtsPHQ8O2w8aTwyPjtpPDM+O2k8ND47aTw1PjtpPDY+O2k8Nz47aTw4PjtpPDk+O2k8MTA+O2k8MTE+O2k8MTI+O2k8MTM+O2k8MTQ+O2k8MTU+O2k8MTY+Oz47bDx0PDtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDxAPDIyLzA3LzIwMDg7NjE4NDU7SHkgdm/Mo25nIGR1zIBuZyBrecyDIHRodcOizKN0IMSRw6rMiSBwaG/MgG5nIGNow7TMgW5nIGx1zIMgbHXMo3QgLCBUw7TMiW5nIHRow7TMgW5nIE1hzIMgQW5oIEPGsMyJdSA6IE5oYcyAIGxhzINuaCDEkWHMo28gcGhhzIlpIGJpw6rMgXQgbsO0zINpIGtow7TMiSBjdcyJYSBuZ8awxqHMgGkgZMOibiAuOz47Oz47Pj47Pj47dDw7bDxpPDA+Oz47bDx0PDtsPGk8MD47PjtsPHQ8QDwyMi8wNy8yMDA4OzYxODQ0O1R1w6LMgG4gc2FuIHF1w7TMgWMgcGhvzIBuZyBNecyDIMSRxrBhIHRpbiA6IFZpw6rMo2MgYmHMgW4gdnXMgyBraGnMgSBjaG8gxJBhzIBpIExvYW4gZSByxIPMgG5nIHNlzIMgZG8gdMO0zIluZyB0aMO0zIFuZyBuaGnDqsyjbSBrecyAIHNhdSBjdcyJYSBNecyDIHF1ecOqzIF0IMSRacyjbmggLjs+Ozs+Oz4+Oz4+O3Q8O2w8aTwwPjs+O2w8dDw7bDxpPDA+Oz47bDx0PEA8MjIvMDcvMjAwODs2MTg0MztUcsOizIBuIFRodcyJeSBCacOqzIluIHF1ecOqzIF0IMSRacyjbmgga2nDqsyjbiBuZ8awxqHMgGkgxJFhzIBuIMO0bmcgaG/MoyBUw7QgdmnMgCDDtG5nIG5hzIB5IMSRYcyDIMSRYcyjcCB2YcyAbyBuZ8awxqHMgGkgbWnMgG5oIC47Pjs7Pjs+Pjs+Pjt0PDtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDxAPDIyLzA3LzIwMDg7NjE4NDI7TsOqzIBuIGtpbmggdMOqzIEgVmnDqsyjdCBOYW0gxJFpIHZhzIBvIGdpYWkgxJFvYcyjbiBjYcyJbmggYmHMgW8ga2h1zIluZyBob2HMiW5nICwgdGhhzIFuZyA5IHTGocyBaSBIacOqzKNwIGjDtMyjaSBjw7RuZyB0aMawxqFuZyDEkGHMgGkgTG9hbiBjb8yBIHRow6rMiSBzZcyDIG3GocyAaSBxdWFuIGNoxrDMgWMgVmnDqsyjdCBOYW0gbm/MgWkgcm/MgyB0acyAbmggaGnMgG5oIMSRw6LMgHUgdMawIMahzIkgVmnDqsyjdCBOYW0gLjs+Ozs+Oz4+Oz4+O3Q8O2w8aTwwPjs+O2w8dDw7bDxpPDA+Oz47bDx0PEA8MjIvMDcvMjAwODs2MTg0MTtBbmggcXXDtMyBYyBwaGHMgXQgbWluaCBtxqHMgWkgdHJvbmcgdmnDqsyjYyBiYcyJbyB2w6rMoyB2w6rMoyBzaW5oIHLEg25nIG1pw6rMo25nICwgdMawxqFuZyBsYWkgYmHMgG4gY2hhzIlpIMSRYcyBbmggcsSDbmcgLCBtYcyBeSBraG9hbmcgcsSDbmcgc2XMgyDEkWkgdmHMgG8gbGnMo2NoIHPGsMyJIC47Pjs7Pjs+Pjs+Pjt0PDtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDxAPDIyLzA3LzIwMDg7NjE4NDA7QsO0zKMgeSB0w6rMgSBTaW5nYXBvcmUgxJFhbmcgc3V5IG5naGnMgyBjaG8gdsOizIFuIMSRw6rMgCBoxqHMo3AgcGhhzIFwIGhvzIFhIHZpw6rMo2MgbXVhIGJhzIFuIGPGoSBxdWFuIGPGoSB0aMOqzIkgY29uIG5nxrDGocyAaSAuOz47Oz47Pj47Pj47dDw7bDxpPDA+Oz47bDx0PDtsPGk8MD47PjtsPHQ8QDwyMS8wNy8yMDA4OzYxNzY5O1RyYW5oIGNow6LMgXAga2hhaSB0aGHMgWMgZMOizIB1IMahzIkgQmnDqsyJbiDEkMO0bmcgLiBUw7TMiW5nIHRow7TMgW5nIE1hzIMgQW5oIEPGsMyJdSA6IGN1zIBuZyBjaHVuZyBraGFpIHRoYcyBYyAsIGN1zIBuZyBoxrDGocyJbmcgdGHMgGkgbmd1ecOqbiBsYcyAIGNhzIFjaCBnaWHMiWkgcXV5w6rMgXQgdMO0zIF0IG5ow6LMgXQgLjs+Ozs+Oz4+Oz4+O3Q8O2w8aTwwPjs+O2w8dDw7bDxpPDA+Oz47bDx0PEA8MjEvMDcvMjAwODs2MTc2ODtCdcyAIMSRxIPMgXAgdmnDqsyjYyBiw6LMgHUgY8awzIkga2hpw6rMgW4geGHMgyBow7TMo2kgYmnMoyBjaGlhIHJlzIMgLCB0w7TMiW5nIHRow7TMgW5nIE1hzIMgQW5oIEPGsMyJdSBydcyBdCBib8yJIG1vzKNpIHTDtMyBIHR1zKNuZyBiw6LMgHUgY8awzIkgLjs+Ozs+Oz4+Oz4+O3Q8O2w8aTwwPjs+O2w8dDw7bDxpPDA+Oz47bDx0PEA8MjEvMDcvMjAwODs2MTc2NztDdcO0zKNjIHRyacOqzIluIGxhzINtIGPDtG5nIG5naMOqzKMgc2luaCBob8yjYyBjdcyJYSDEkGHMgGkgTG9hbiBzZcyDIMSRxrDGocyjYyBkacOqzINuIHJhIHZhzIBvIHRoxrDMgSA1IHR1w6LMgG4gbmHMgHkgLiDigJwgVMOqzIEgYmHMgG8gbcOizIBt4oCdIHRyxqHMiSB0aGHMgG5oIHRpw6p1IMSRacOqzIltIGN1zIlhIGN1w7TMo2MgdHJpw6rMiW4gbGHMg20gLjs+Ozs+Oz4+Oz4+O3Q8O2w8aTwwPjs+O2w8dDw7bDxpPDA+Oz47bDx0PEA8MjEvMDcvMjAwODs2MTc2NjtUaOG7nWkgYsOhbyBMb3MgQW5nZWxlcyDEkcSDbmcgYmHMgGkgdmnDqsyBdCDigJwgxJHGsMyAbmcgaGHMoyB0aMOizIFwIMSRacyjYSB2acyjIMSQYcyAaSBMb2FuIGx1zIFjIMSQYcyAaSBMb2FuIHRoYW0gZMawzKMgVGjDqsyBIHbDosyjbiBow7TMo2kgT2x5bXBpY+KAnSAuOz47Oz47Pj47Pj47dDw7bDxpPDA+Oz47bDx0PDtsPGk8MD47PjtsPHQ8QDwyMS8wNy8yMDA4OzYxNzY1O1NhbmcgbsSDbSBzZcyDIGNvzIEgOCwwMDAgY8ahIHF1YW4gLCB0csawxqHMgG5nIGhvzKNjIGR1ecOqzKN0IGPDtG5nIHbEg24gdHLDqm4gbWHMo25nIEludGVybmV0IC47Pjs7Pjs+Pjs+Pjt0PDtsPGk8MD47PjtsPHQ8O2w8aTwwPjs+O2w8dDxAPDIxLzA3LzIwMDg7NjE3NjQ7xJBpw6rMgHUgdHJhIHRoxINtIGRvzIAgOiB0b2HMgG4gZMOibiB0acOqzIl1IHRhbSB0aMO0bmcgLCBjb8yBIHRyw6puIDUw77yFIG5ob8yBbSBuZ8awxqHMgGkgxJFpIGxhzIBtIGNvzIEgecyBIG5ndXnDqsyjbiBzYW5nIFRydW5nIFF1w7TMgWMgbGHMgG0gdmnDqsyjYyAuOz47Oz47Pj47Pj47dDw7bDxpPDA+Oz47bDx0PDtsPGk8MD47PjtsPHQ8QDwxOS8wNy8yMDA4OzYxNzMwO0PGoW4gYsOjbyBLYWxtYWVnaSDEkeG6v24gxJDDoGkgTG9hbiwgbsO0bmcgbmdoaeG7h3AgxJDDoGkgTG9hbiB04buVbiB0aOG6pXQga2hv4bqjbmcgNDAwIHRyaeG7h3UgxJDDoGkgdOG7hzs+Ozs+Oz4+Oz4+O3Q8O2w8aTwwPjs+O2w8dDw7bDxpPDA+Oz47bDx0PEA8MTkvMDcvMjAwODs2MTcyOTtUaOG7pyB0xrDhu5tuZyBTdC4gVmluY2VudCBuZ8OgeSAyMC83IMSR4bq/biB0aMSDbSDEkMOgaSBMb2FuOz47Oz47Pj47Pj47dDw7bDxpPDA+Oz47bDx0PDtsPGk8MD47PjtsPHQ8QDwxOS8wNy8yMDA4OzYxNzI4O0x14bqtdCB0aeG7gW4gaGnhur9uIHThurduZyBjaMOtbmggdHLhu4sgxJHGsOG7o2MgdGjDtG5nIHF1YSwgeMOieSBk4buxbmcgY2jhur8gxJHhu5kgdHLhuqMgdGnhu4FuIGNow61uaCB0cuG7izs+Ozs+Oz4+Oz4+Oz4+Oz4+Oz4+Oz4+Oz5HSgDzu/B5H8vrEj1YDuEilAz0Og=="));
    nvpList.add(new BasicNameValuePair("Comments1:txtEmail", ""));
    nvpList.add(new BasicNameValuePair("Comments1:btnSubmit", "g??i ?i"));
    nvpList.add(new BasicNameValuePair("txtKeyword", ""));
    nvpList.add(new BasicNameValuePair("btnSearch", "Search"));
    nvpList.add(new BasicNameValuePair("txtBeginDate", "23/06/2008"));
    nvpList.add(new BasicNameValuePair("txtEndDate", "23/07/2008"));
    
    
    byte [] bytes = loadContent(home, nvpList);
    writer.save(new File("F:\\Temp2\\webclient\\b.html"), bytes);
    
  }
  
}
