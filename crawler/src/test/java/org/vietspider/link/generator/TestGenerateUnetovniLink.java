/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 18, 2008  
 */
public class TestGenerateUnetovniLink {
  
  public static void main(String[] args) throws Exception {
    for(int i = 1; i < 100; i++) {
      StringBuilder builder = new StringBuilder("http://www.uneto-vni.nl/applications/zoekeenlid/lijst.asp?frmAction=Next&frmWhere=FROM%09wx40_Organisatie+WHERE%09wx40_Organisatie.Organisatie_Organisatie+IN+%09%09(%09SELECT%09lidmaat.orga+%09%09%09FROM+lidmaat+%09%09%09WHERE%09lidmaat.lidtyp+IN+(360%2C361%2C363%2C30000002%2C30000016%2C30000017%2C+40000001%2C+40000005%2C+40000006%2C40000011%2C40000035)+AND+(lidmaat.beg+<%3D+getdate()+Or+lidmaat.beg+is+NULL)+AND+(lidmaat.ein+>%3D+getdate()+Or+Lidmaat.ein+is+NULL))+AND%09(+wx40_Organisatie.Organisatie_Postcode+>%3D+'1000+AA'+AND+wx40_Organisatie.Organisatie_Postcode+<%3D+'9999+ZZ')++AND+wx40_Organisatie.Organisatie_Organisatie+IN++(%09SELECT+Organisatie_Organisatie%09FROM+wx40_Organisatie_Classificatie+%09WHERE+%09Classificatie_Classificatie+%3D+40000560+%09%09OR+Classificatie_Parent+%3D+40000560)+AND+NOT+wx40_Organisatie.Organisatie_Organisatie+IN+%09%09(%09SELECT+geven.objectnr%09%09%09FROM+geven%2C+gegtyp+%09%09%09WHERE+geven.gegtyp+%3D+gegtyp.gegtyp+AND+gegtyp.alfa+%3D+'BGZ'+AND+geven.object+%3D+'O'+AND+convert(varchar%2Cgeven.waarde)%3D'1')&intpage=");
      builder.append(i);
      System.out.println(builder);
    }
    
  }
  
}
