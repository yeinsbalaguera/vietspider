/***************************************************************************
 * Copyright 2001-2008 The VietSpider         All rights reserved.  		 *
 **************************************************************************/
package org.vietspider.content;

import java.io.File;
import java.util.prefs.Preferences;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.FileEntity;
import org.apache.http.message.BasicHeader;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.client.common.ClientConnector2;
import org.vietspider.common.util.Worker;
import org.vietspider.net.client.RequestManager;
import org.vietspider.net.client.WebClient;
import org.vietspider.net.server.URLPath;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.WaitLoading;

/**
 * Author : Nhu Dinh Thuan nhudinhthuan@yahoo.com Jun 16, 2008
 */
public class ImportDataDialog {

  private Shell shell;
  private Button butImportFile;
  private Button butImportFolder;

  private Label lblStatus;
  private boolean aborted;

  public ImportDataDialog(Shell parent) {
    shell = new Shell(parent, SWT.TITLE | SWT.RESIZE | SWT.APPLICATION_MODAL | SWT.CLOSE);
    shell.setLayout(new GridLayout(1, false));


    ApplicationFactory factory = new ApplicationFactory(shell, "DataExporter", getClass().getName());
    shell.setText(factory.getLabel("title"));
    factory.setComposite(shell);

    Composite dataComposite = new Composite(shell, SWT.NONE);
    GridData gridData = new GridData(GridData.FILL_BOTH);
    dataComposite.setLayoutData(gridData);
    dataComposite.setLayout(new GridLayout(4, false));
    factory.setComposite(dataComposite);

    Composite bottomComposite = new Composite(shell, SWT.NONE);
    bottomComposite.setLayout(new GridLayout(2, false));
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    factory.setComposite(bottomComposite);

    Composite buttonComposite = new Composite(shell, SWT.NONE);
    RowLayout rowLayout = new RowLayout();
    rowLayout.justify = true;
    buttonComposite.setLayout(rowLayout);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    buttonComposite.setLayoutData(gridData);
    factory.setComposite(buttonComposite);

    butImportFile = factory.createButton("butImportFile", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        File file = getFile();
        if (file == null) return;
        importFile(0, file);
      }
    });

    butImportFolder = factory.createButton("butImportFolder", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        File file = getFolder();
        if (file == null) return;
        importFolder(file);
      }
    });

    gridData = new GridData(GridData.FILL_HORIZONTAL);
    lblStatus = new Label(shell, SWT.BORDER);
    lblStatus.setLayoutData(gridData);


    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 300) / 2;
    shell.addDisposeListener(new DisposeListener() {
      @SuppressWarnings("unused")
      public void widgetDisposed(DisposeEvent evt) {
        aborted = true;
      }

    });
    shell.setImage(parent.getImage());
    shell.setLocation(x, y);
    shell.setSize(450, 110);
    shell.open();
  }

  private void importFile(final int index, final File...files) {
    if(files == null || index >= files.length) return;
    Worker excutor = new Worker() {

      private String error = null;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        lblStatus.setText("Import data from " + files[index].getName()+"...");
      }

      public void execute() {
        if(!files[index].getName().endsWith("zip")) return;
        Header [] headers = new Header[] {
            new BasicHeader("action", "import.data"),
            new BasicHeader("import.file.name", files[index].getName()),
            new BasicHeader("plugin.name", "export.import.data.plugin"),
            new BasicHeader("data.type", "file")
        };
        error = sendData(files[index], headers);
      }

      public void after() {
        if(shell.isDisposed() || aborted) {
          startImport();
          shell.dispose();
          return;
        }

        if(error != null && !error.trim().isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
        }

        if(index + 1 < files.length) {
          importFile(index+1, files);
          return ;
        }

        startImport();
        shell.dispose();
      }
    };
    butImportFile.setEnabled(false);
    butImportFolder.setEnabled(false);
    WaitLoading waitLoading = new WaitLoading(butImportFile, excutor, SWT.TITLE);
    waitLoading.open();
  }

  private void importFolder(final File folder) {
    File [] files = folder.listFiles();
    if(files == null || files.length < 1) return;
    importFile(0, files);
  }

  private void startImport() {
    Worker excutor = new Worker() {

      private String error;

      public void abort() {
        ClientConnector2.currentInstance().abort();
      }

      public void before() {
        if(lblStatus.isDisposed()) return;
        lblStatus.setText("Start import...");
      }

      public void execute() {
        Header [] headers = new Header[] {
            new BasicHeader("action", "start.import.data"),
            new BasicHeader("plugin.name", "export.import.data.plugin"),
            new BasicHeader("data.type", "file")
        };

        ClientConnector2 connector = ClientConnector2.currentInstance();
        String address = connector.getRemoteURL()+ URLPath.DATA_PLUGIN_HANDLER;
        WebClient webClient = connector.getWebClient();
        HttpGet httpPost = RequestManager.getInstance().createGet(webClient, null, address);
        httpPost.addHeader(connector.getUserHeader());
        for(Header header : headers) httpPost.addHeader(header); 
        connector.manage(httpPost);

        HttpResponse httpResponse = null;
        try {
          HttpHost httpHost = webClient.createHttpHost(connector.getRemoteURL());
          httpResponse = webClient.execute(httpHost, httpPost);
        } catch (Exception e) {
          error = e.toString();
        } finally {
          connector.release(httpPost, httpResponse);
        }
      }

      public void after() {
        if(shell.isDisposed()) return;

        if(error != null && !error.trim().isEmpty()) {
          ClientLog.getInstance().setMessage(shell, new Exception(error));
        }

        shell.dispose();
      }
    };
    butImportFile.setEnabled(false);
    butImportFolder.setEnabled(false);
    WaitLoading waitLoading = new WaitLoading(butImportFile, excutor, SWT.TITLE);
    waitLoading.open();
  }


  private File getFile() {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    String path = prefs.get("openImportData", "");
    FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    dialog.setFilterExtensions(new String[] { "*.zip" });
    if (path != null) {
      File file = new File(path);
      if (file.isDirectory()) {
        dialog.setFilterPath(path);
      } else {
        try {
          dialog.setFilterPath(file.getParentFile().getAbsolutePath());
        } catch (Exception e) {
        }
      }
    }
    path = dialog.open();
    if (path == null || path.trim().isEmpty()) return null;
    prefs.put("openImportData", path);
    return new File(path);
  }

  private File getFolder() {
    Preferences prefs = Preferences.userNodeForPackage(getClass());
    String path = prefs.get("openImportData", "");
    DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
    if (path != null) {
      File file = new File(path);
      if (file.isDirectory()) {
        dialog.setFilterPath(path);
      } else {
        try {
          dialog.setFilterPath(file.getParentFile().getAbsolutePath());
        } catch (Exception e) {
        }
      }
    }
    path = dialog.open();
    if (path == null || path.trim().isEmpty()) return null;
    prefs.put("openImportData", path);
    return new File(path);
  }

  public String sendData(File file, Header...headers) {
    ClientConnector2 connector = ClientConnector2.currentInstance();
    String address = connector.getRemoteURL()+URLPath.DATA_PLUGIN_HANDLER;
    WebClient webClient = connector.getWebClient();
    HttpPost httpPost = RequestManager.getInstance().createPost(webClient, null, address);
    HttpResponse httpResponse = null;
    String error = null;
    try {
      connector.manage(httpPost);
      httpPost.addHeader(connector.getUserHeader());
      for(Header header : headers) httpPost.addHeader(header); 
      if(file != null) httpPost.setEntity(new FileEntity(file, "automatic/name"));



      HttpHost httpHost = webClient.createHttpHost(connector.getRemoteURL());
      webClient.execute(httpHost, httpPost);
    } catch (Exception e) {
      error = e.toString();
    } finally {
      connector.release(httpPost, httpResponse); 
    }
    return error;
  }
}
