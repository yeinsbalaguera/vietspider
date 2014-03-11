import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import org.vietspider.deploy.DeployApp;
import org.vietspider.deploy.IOUtils;
import org.vietspider.deploy.ZipExtractor;

/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/

/** 
 * Author : Nhu Dinh Thuan
 *          nhudinhthuan@yahoo.com
 * Nov 20, 2008  
 */
public class TestDeployer {

  private File repository ;//new File("D:\\java\\dependencies\\vietspider\\");
  private static String resourcesFolder;
  private String deployFolder;

  private List<File> vsfiles = new ArrayList<File>();

  private String version = "1.0";

  private final static int CLIENT = 0;
  private final static int SERVER = 1;
  private final static int ALL = 2;
  private int type = ALL;

  public TestDeployer(String folder, String deployFolder_, int type) {
    this.type = type;
    this.repository = new File(folder);
    this.deployFolder = deployFolder_;

    File file  = new File(deployFolder);
    file.mkdirs();

    IOUtils.deleteFolder(new File(deployFolder), false);
  }

  private void buildDeployLibrary() {
    vsfiles.clear();
    List<String> fileNames = new ArrayList<String>();
    if(type != CLIENT) {
//      fileNames.add("headvances.core.commons");
//      fileNames.add("headvances.core.storage");
//      fileNames.add("headvances.component.nlp");
//      fileNames.add("headvances.webdb.storage");
//      fileNames.add("headvances.webdb.vietspider");
//      fileNames.add("headvances.component.storage");
//      fileNames.add("headvances.component.common");
//      fileNames.add("headvances.component.service");
    }

//    fileNames.add("headvances.core.application");

    fileNames.add("org.vietspider.common");
    fileNames.add("org.vietspider.htmlparser");
    fileNames.add("org.vietspider.model");

    if(type != CLIENT) {
      fileNames.add("org.vietspider.vietspiderdb");
//      fileNames.add("org.vietspider.solr3");
      fileNames.add("org.vietspider.solr3-searcher");
      fileNames.add("org.vietspider.embededb");
      fileNames.add("org.vietspider.offices");
      fileNames.add("org.vietspider.io");
      fileNames.add("org.vietspider.crawler");
      fileNames.add("org.vietspider.httpserver2");
      fileNames.add("org.vietspider.server-plugin");
    }

    fileNames.add("org.vietspider.client");

    if(type != SERVER) {
      fileNames.add("org.vietspider.widget");
      fileNames.add("org.vietspider.htmlexplorer");
      fileNames.add("org.vietspider.gui");
      fileNames.add("org.vietspider.client-plugin");
    } 

    File jarFolder  = new File(repository, 
        File.separator + "org" + File.separator + "vietspider" + File.separator);

    for(int i = 0; i < fileNames.size(); i++) {
      String fileName = fileNames.get(i) + File.separator + version + File.separator;
      fileName += fileNames.get(i) + "-" + version + ".jar";
      File file  = new File(jarFolder, fileName);
      if(!file.exists()) {
        System.err.println(file.getAbsolutePath()+ " not found!");
        System.exit(0);
      }
      vsfiles.add(file);
    }

//    if(type == SERVER)  return;
    fileNames = new ArrayList<String>();

    if(type != SERVER) {
      //client lib
      fileNames.add("swt/ibm-icu/4.2.2/ibm-icu-4.2.2.jar");
      fileNames.add("swt/eclipse-core-commands/3.6.0/eclipse-core-commands-3.6.0.jar");
      fileNames.add("swt/eclipse-equinox-common/3.6.0/eclipse-equinox-common-3.6.0.jar");
      fileNames.add("swt/eclipse-osgi/3.7.1/eclipse-osgi-3.7.1.jar");
//      fileNames.add("swt/mozilla-interfaces/1.0/mozilla-interfaces-1.0.jar");
    }


    fileNames.add("javax/activation/activation/1.1/activation-1.1.jar");
    fileNames.add("org/apache/james/apache-mime4j/0.6/apache-mime4j-0.6.jar");
    fileNames.add("commons-codec/commons-codec/1.3/commons-codec-1.3.jar");
    fileNames.add("commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar");
    fileNames.add("org/apache/lucene/lucene-core/3.1.0/lucene-core-3.1.0.jar");
    fileNames.add("net/arnx/jsonic/1.2.7/jsonic-1.2.7.jar");


//    fileNames.add("org/headvances/headvances.hadoop.core/0.19.1/headvances.hadoop.core-0.19.1.jar");

//    fileNames.add("org/vietspider/vietspider.httpclient/4.0/vietspider.httpclient-4.0.jar");
//    fileNames.add("org/vietspider/vietspider.httpcore/4.0.1/vietspider.httpcore-4.0.1.jar");
//    fileNames.add("org/headvances/headvances.3rdparty.lucene/1.0/headvances.3rdparty.lucene-1.0.jar");
//    fileNames.add("org/vietspider/vietspider.httpmime/4.0/vietspider.httpmime-4.0.jar");

    fileNames.add("org/vietspider/vietspider.httpclient/4.1.2/vietspider.httpclient-4.1.2.jar");
    fileNames.add("org/vietspider/vietspider.httpcore/4.1.4/vietspider.httpcore-4.1.4.jar");
    fileNames.add("org/vietspider/vietspider.httpmime/4.1.2/vietspider.httpmime-4.1.2.jar");

    if(type != CLIENT) {

      fileNames.add("org/vietspider/org.vietspider.solr3/3.0/org.vietspider.solr3-3.0.jar");

//      fileNames.add("org/headvances/headvances.component.storage/1.0/headvances.component.storage-1.0.jar");
//      fileNames.add("org/headvances/headvances.component.nlp/1.0/headvances.component.nlp-1.0.jar");
//      fileNames.add("org/vietspider/vietspider.httpcore-nio/4.0.1/vietspider.httpcore-nio-4.0.1.jar");
      fileNames.add("org/vietspider/vietspider.httpcore-nio/4.1.4/vietspider.httpcore-nio-4.1.4.jar");
//      fileNames.add("org/headvances/headvances.moom.storage.hdfs/1.0/headvances.moom.storage.hdfs-1.0.jar");
//      fileNames.add("org/headvances/headvances.moom.server/1.0/headvances.moom.server-1.0.jar");
//      fileNames.add("org/headvances/headvances.moom.common/1.0/headvances.moom.common-1.0.jar");
//      fileNames.add("org/headvances/headvances.moom.nlp.core/1.0/headvances.moom.nlp.core-1.0.jar");

      fileNames.add("com/sleepycat/je/5.0.34/je-5.0.34.jar");
      fileNames.add("com/h2database/h2/1.1.107/h2-1.1.107.jar");
      fileNames.add("jtds/jtds/1.2.2/jtds-1.2.2.jar");
      fileNames.add("log4j/log4j/1.2.14/log4j-1.2.14.jar"); 
      fileNames.add("javax/mail/mail/1.4/mail-1.4.jar");
      fileNames.add("/mysql/mysql-connector-java/5.1.6/mysql-connector-java-5.1.6.jar");
      fileNames.add("oracle/ojdbc/1.4/ojdbc-1.4.jar");
      
      fileNames.add("babu/BabuDB/0.5.6/BabuDB-0.5.6.jar");
      fileNames.add("babu/Foundation/1.0/Foundation-1.0.jar");

      fileNames.add("commons-cli/commons-cli/1.1/commons-cli-1.1.jar");
      fileNames.add("commons-io/commons-io/1.4/commons-io-1.4.jar");
//      fileNames.add("org/json/json/20080701/json-20080701.jar");
//      fileNames.add("org/openoffice/juh/3.1.0/juh-3.1.0.jar");
//      fileNames.add("org/openoffice/jurt/3.1.0/jurt-3.1.0.jar");
//      fileNames.add("org/openoffice/ridl/3.1.0/ridl-3.1.0.jar");
//      fileNames.add("org/openoffice/unoil/3.1.0/unoil-3.1.0.jar");

      //solr
      fileNames.add("org/apache/lucene/lucene-analyzers/3.1.0/lucene-analyzers-3.1.0.jar");
      fileNames.add("org/apache/lucene/lucene-highlighter/3.1.0/lucene-highlighter-3.1.0.jar");
      fileNames.add("org/apache/lucene/lucene-memory/3.1.0/lucene-memory-3.1.0.jar");
      fileNames.add("org/apache/lucene/lucene-misc/3.1.0/lucene-misc-3.1.0.jar");
      fileNames.add("org/apache/lucene/lucene-queries/3.1.0/lucene-queries-3.1.0.jar");
      fileNames.add("org/apache/lucene/lucene-spatial/3.1.0/lucene-spatial-3.1.0.jar");
      fileNames.add("org/apache/lucene/lucene-spellchecker/3.1.0/lucene-spellchecker-3.1.0.jar");

      fileNames.add("javax/servlet/servlet-api/2.5/servlet-api-2.5.jar");

      fileNames.add("org/slf4j/slf4j-simple/1.5.10/slf4j-simple-1.5.10.jar");
      fileNames.add("org/slf4j/slf4j-api/1.5.10/slf4j-api-1.5.10.jar");
      fileNames.add("org/slf4j/jcl-over-slf4j/1.5.10/jcl-over-slf4j-1.5.10.jar");

      fileNames.add("org/apache/velocity/velocity/1.6.1/velocity-1.6.1.jar");
      fileNames.add("org/apache/velocity/velocity-tools/2.0-beta3/velocity-tools-2.0-beta3.jar");
      fileNames.add("org/apache/solr3/apache-solr-noggit/r944541/apache-solr-noggit-r944541.jar");
      fileNames.add("org/apache/solr/solr-commons-csv/1.3.0/solr-commons-csv-1.3.0.jar");
//      fileNames.add("org/apache/hadoop/hadoop-core/0.20.2/hadoop-core-0.20.2.jar");


//      fileNames.add("org/apache/hadoop/zookeeper/zookeeper/3.2.1/zookeeper-3.2.1.jar");

//      fileNames.add("wstx/wstx-asl/3.2.7/wstx-asl-3.2.7.jar");
      fileNames.add("commons-httpclient/commons-httpclient/3.1/commons-httpclient-3.1.jar");
      fileNames.add("commons-fileupload/commons-fileupload/1.2.1/commons-fileupload-1.2.1.jar");
//      fileNames.add("stax/stax-api/1.0.1/stax-api-1.0.1.jar");


      fileNames.add("vn/opennlp-tools/1.4.3/opennlp-tools-1.4.3.jar");
      fileNames.add("vn/maxent/2.5.2/maxent-2.5.2.jar");
//      fileNames.add("vn/vn.hus.nlp.sd/2.0.0/vn.hus.nlp.sd-2.0.0.jar");
//      fileNames.add("vn/vn.hus.nlp.utils/1.0.0/vn.hus.nlp.utils-1.0.0.jar");
//      fileNames.add("");
//      fileNames.add("");


    }

    for(int i = 0; i < fileNames.size(); i++) {
      File file  = new File(repository, fileNames.get(i));
      if(!file.exists()) {
        System.err.println(file.getAbsolutePath()+ " not found!");
        System.exit(0);
      }
      vsfiles.add(file);
    }
  }

  private void deployCommonResources() throws Exception {
    if(type != SERVER) {
      File file = new File(resourcesFolder, "executor.zip");
      if(file.exists()) ZipExtractor.extract(file, deployFolder);

      file = new File(resourcesFolder, "data.zip");
      if(file.exists()) ZipExtractor.extract(file, deployFolder);
    }

    URL url  = TestDeployer.class.getResource("/");
    File file = new File(url.toURI());
    file = new File(file.getParent(), "org.vietspider.startup-"+ version+".jar");

    File startFile  = new File(deployFolder, "startup.jar");
    IOUtils.copy(file, startFile);

    file  = new File(deployFolder+"lib");
    file.mkdir();

    if(type != SERVER) {
      file = new File(resourcesFolder, "gui_lib.zip");
      if(file.exists()) ZipExtractor.extract(file, deployFolder + "lib/");
    }

  }

  private void deploy() throws Exception  {
    buildDeployLibrary();
    deployCommonResources();

    DeployApp deployer = new DeployApp(); 
    deployer.deploy(vsfiles, new File(deployFolder + "lib/"), false);
  }

//  private void deployServer(boolean pack) {
//    buildDeployAll();
//    deployCommonResources();
//
//    DeployApp deployer = new DeployApp(); 
//    deployer.deploy(vsfiles, new File(deployFolder + "lib/"), false);
//  }

  //java TestDeployer D:\\java\\dependencies\\repository\\ D:\\java\\VietSpider\\
  public static void main(String[] args) throws Exception  {
    String repo = null;
    String deployFolder = null;
    Preferences prefs = Preferences.userNodeForPackage(TestDeployer.class);
    int type = ALL;

    if(args.length > 2) {
      repo = args[0]; 
      deployFolder = args[1];

      try {
        type = Integer.parseInt(args[2]);
      } catch (Exception e) {
        System.out.println("unknown deploy type");
      }

      prefs.put("deployer.repo", repo);
      prefs.put("deployer.folder", deployFolder);
      prefs.sync();
    } else if(args.length == 1){
      try {
        type = Integer.parseInt(args[0]);
      } catch (Exception e) {
        System.out.println("unknown deploy type");
      }
    } else {
      repo  = prefs.get("deployer.repo", "");
      deployFolder  = prefs.get("deployer.folder", "");  
    }
//    String deployFolder = "D:\\java\\VietSpider\\";

    repo = "F:\\Dependencies\\Repo\\";
//    repo = "D:\\java\\dependencies\\repository";
    resourcesFolder = "F:\\Bakup\\codes\\vietspider3\\deploy\\resources\\";
    String parentDeployFolder = "F:\\Bakup\\codes\\vietspider3\\deploy\\";
//    String parentDeployFolder = "D:\\java\\vietspider3\\deploy\\";
    
    deployFolder = parentDeployFolder + "full\\";
    TestDeployer deployer = new TestDeployer(repo, deployFolder, type);
    deployer.deploy();

    deployFolder = parentDeployFolder + "server\\";
    deployer = new TestDeployer(repo, deployFolder, SERVER);
    deployer.deploy();

    deployFolder = parentDeployFolder + "client\\";
    deployer = new TestDeployer(repo, deployFolder, CLIENT);
    deployer.deploy();
  }

}
