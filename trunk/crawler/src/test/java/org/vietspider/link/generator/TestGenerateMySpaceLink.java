/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.link.generator;


/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Apr 18, 2008  
 */
public class TestGenerateMySpaceLink {
  
  public static void main(String[] args) throws Exception {
    
    String link1 = "http://browseusers.myspace.com/Browse/browse.aspx?MyToken=633483860162715478&__EVENTTARGET=ctl00%24cpMain%24Browse%24PagingTop&__EVENTARGUMENT=";
    String link2 = "&__VIEWSTATE=%2FwEPDwUKMTI4ODk4NTUwN2QYAQUeX19Db250cm9sc1JlcXVpcmVQb3N0QmFja0tleV9fFgsFLWN0bDAwJGNwTWFpbiRCcm93c2UkQ3JpdGVyaWFWaWV3JHN0YXR1c1NpbmdsZQU1Y3RsMDAkY3BNYWluJEJyb3dzZSRDcml0ZXJpYVZpZXckc3RhdHVzSW5SZWxhdGlvbnNoaXAFLmN0bDAwJGNwTWFpbiRCcm93c2UkQ3JpdGVyaWFWaWV3JHN0YXR1c01hcnJpZWQFL2N0bDAwJGNwTWFpbiRCcm93c2UkQ3JpdGVyaWFWaWV3JHN0YXR1c0Rpdm9yY2VkBS5jdGwwMCRjcE1haW4kQnJvd3NlJENyaXRlcmlhVmlldyRzdGF0dXNFbmdhZ2VkBS1jdGwwMCRjcE1haW4kQnJvd3NlJENyaXRlcmlhVmlldyRtb3RpdmVEYXRpbmcFMWN0bDAwJGNwTWFpbiRCcm93c2UkQ3JpdGVyaWFWaWV3JG1vdGl2ZU5ldHdvcmtpbmcFNGN0bDAwJGNwTWFpbiRCcm93c2UkQ3JpdGVyaWFWaWV3JG1vdGl2ZVJlbGF0aW9uc2hpcHMFLmN0bDAwJGNwTWFpbiRCcm93c2UkQ3JpdGVyaWFWaWV3JG1vdGl2ZUZyaWVuZHMFMWN0bDAwJGNwTWFpbiRCcm93c2UkQ3JpdGVyaWFWaWV3JHNob3dIYXNQaG90b09ubHkFMmN0bDAwJGNwTWFpbiRCcm93c2UkQ3JpdGVyaWFWaWV3JHNob3dOYW1lUGhvdG9Pbmx5&ctl00%24cpMain%24Browse%24CriteriaView%24gender_International=genderWomen&ctl00%24cpMain%24Browse%24CriteriaView%24minAge=18&ctl00%24cpMain%24Browse%24CriteriaView%24maxAge=35&ctl00%24cpMain%24Browse%24CriteriaView%24GeoLocation1%24ddlCountry=230__VN&ctl00%24cpMain%24Browse%24CriteriaView%24GeoLocation1%24ddlRegion=0&ctl00%24cpMain%24Browse%24CriteriaView%24GeoLocation1%24txtRegion=&ctl00%24cpMain%24Browse%24CriteriaView%24GeoLocation1%24ddlSearchRadius=0&ctl00%24cpMain%24Browse%24CriteriaView%24GeoLocation1%24txtPostalCode=&hdnSearchMode=none&ctl00%24cpMain%24Browse%24CriteriaView%24showHasPhotoOnly=on&ctl00%24cpMain%24Browse%24CriteriaView%24showNamePhotoOnly=on&ctl00%24cpMain%24Browse%24CriteriaView%24SortResultsBy=LastLogin&ctl00%24cpMain%24Browse%24CriteriaView%24AdvancedView=False&ctl00%24cpMain%24Browse%24CriteriaView%24PersistenceCookie=VW%3DB%26GV%3D1%26PO%3D1%26SC%3DFN%26GN%3DW%26AG%3D18-35%26CN%3DVN%26SB%3DL&___msPagerState=3000%2C40%2C14%2C-1%2C-1&ctl00%24cpMain%24Browse%24QueryToken=0d50f136-5ade-4b32-bd28-1555bdff618d|AAAAARIjAQAAAAAAAAAAAAAAAAAAAA%3D%3D|VN";
    
    for(int i = 1; i < 101; i++) {
      System.out.println(link1+ String.valueOf(i)+ link2); 
    }
    
  }
  
}
