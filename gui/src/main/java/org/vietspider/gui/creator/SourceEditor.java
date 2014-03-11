package org.vietspider.gui.creator;

import static org.vietspider.link.pattern.LinkPatternFactory.createPatterns;

import java.net.URL;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.ClientProperties;
import org.vietspider.browser.FastWebClient;
import org.vietspider.chars.URLEncoder;
import org.vietspider.client.common.DataClientHandler;
import org.vietspider.common.Application;
import org.vietspider.common.text.SWProtocol;
import org.vietspider.common.util.Worker;
import org.vietspider.gui.creator.action.LoadSourceVerions;
import org.vietspider.gui.web.FastWebClient2;
import org.vietspider.link.pattern.JSOnclickPatternUtils;
import org.vietspider.link.pattern.LinkPatterns;
import org.vietspider.model.ExtractType;
import org.vietspider.model.Group;
import org.vietspider.model.Region;
import org.vietspider.model.RegionUtils;
import org.vietspider.model.Source;
import org.vietspider.model.SourceProperties;
import org.vietspider.net.channel.DefaultSourceConfig;
import org.vietspider.net.channel.ISourceConfig;
import org.vietspider.net.client.HttpClientFactory;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.services.ClientRM;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.waiter.ThreadExecutor;

public class SourceEditor extends SourceEditorUI {
  
  public final static short ENGLISH = 0;
  public final static short VIETNAM = 1;

  private String lastUrl;
  private short localeType = 0;
  
  public SourceEditor(Composite parent, ApplicationFactory factory) {
    super(parent, factory);
    webClient = new FastWebClient2();
    ClientProperties clientProperties = ClientProperties.getInstance();
    if("en".equals(clientProperties.getValue("locale"))) {
      localeType = ENGLISH;
    } else if("vn".equals(clientProperties.getValue("locale"))) {
      localeType = VIETNAM;
    } else {
      localeType = ENGLISH;
    }
  }
  
  public void reset() {      
    propertiesComposite.reset();
    webClient = new FastWebClient2();
//    loginWebsite.reset();
    
    txtHome.clearItems();
    txtPattern.setText("");
    txtName.setText("");
    cboCharset.setText(Application.CHARSET);
    cboCrawlHours.removeAll();
    
    setVisitRegions(null);
    setExtractRegions(null);
    setRemoveRegions(null);
    
    cboExtractType.select(0);
    butAllowRemoveFrom.setSelection(false);
    spinPriority.setSelection(8);
    
    Color normalColor = new Color(getDisplay(), 0, 0, 0);
    txtPattern.setForeground(normalColor);
    txtHome.getTextComponent().setForeground(normalColor);
    cboCharset.setForeground(normalColor);
    
    if("FORUM".equals(creator.getSelectedGroupName())) {
      spinDepth.setSelection(3);
    } else {
      spinDepth.setSelection(1);
    }
    
    setDataRegions(null);
    
    templateVisitLink.setItems(new String[0]);
    templateDataLink.setItems(new String[0]);
    
    charset = null;
    isNewSource = true;
    properties.clear();
    propertiesComposite.setProperties(properties);
    
  }

  public Source createSource(){
  	if(!check()) return null;
  	
  	if(templateVisitLink.getValue().trim().length() > 0 
  	    && spinDepth.getSelection() < 2) {
      spinDepth.setSelection(2);
    }
  	
    final Source source = new Source();      
    source.setName(txtName.getText());
    source.setCategory(creator.getSelectedCategory());
    source.setHome(txtHome.getItems());
    
    source.setDepth(spinDepth.getSelection());
    source.setExtractType(ExtractType.valueOf(cboExtractType.getText()));
    source.setPriority(spinPriority.getSelection());
    
    addCrawlHour(normalizeHour(cboCrawlHours.getText()));
    if(cboCrawlHours.getItems() != null) {
      source.setCrawlTimes(cboCrawlHours.getItems());
    }
    
    if(charset == null || (charset = charset.trim()).isEmpty()) {
      charset = cboCharset.getText();      
    } 
    source.setEncoding(charset);
    
    if(!txtPattern.getText().trim().isEmpty()) {
      source.setPattern(txtPattern.getText().trim());
    }
    
    source.setUpdateRegion(new Region(Region.UPDATE, getVisitRegions()));
    
    Region[] detachPaths = new Region[2];
    detachPaths[0] = new Region(Region.EXTRACT, getExtractRegions());
    if(getRemoveRegions().length > 0) {
      detachPaths[1] = new Region(Region.CLEAN, getRemoveRegions());
      String cleanFrom = String.valueOf(butAllowRemoveFrom.getSelection());
      detachPaths[1].getProperties().put(Region.CLEAN_FROM, cleanFrom);
    }
    source.setExtractRegion(detachPaths);
    
    source.setGroup(creator.getSelectedGroupName());
    
    source.setProcessRegion(RegionUtils.toArray(getDataRegions()));
    
    List<Region> dataRegions = getDataRegions();
    source.setProcessRegion(dataRegions.toArray(new Region[dataRegions.size()]));
    
    this.properties.setProperty("LinkPattern", templateVisitLink.getValue());
    this.properties.setProperty("URLPattern", templateDataLink.getValue());
    
    String jsOnclickPatterns = this.properties.getProperty(SourceProperties.JS_ONCLICK_PATTERN);
    if(jsOnclickPatterns != null && !jsOnclickPatterns.trim().isEmpty()) {
      jsOnclickPatterns = JSOnclickPatternUtils.toTemplates(jsOnclickPatterns);
      properties.setProperty(SourceProperties.JS_ONCLICK_PATTERN, jsOnclickPatterns);
    }
    
    propertiesComposite.clearWorkingProperties();
    propertiesComposite.saveProperties();
    
    Properties props = new Properties();
    props.putAll(this.properties);
    source.setProperties(props);
    
    return source;
  }
  
  public void setSource(Source source, int version) { 
    Color normalColor = new Color(getDisplay(), 0, 0, 0);
    txtPattern.setForeground(normalColor);
    txtHome.getTextComponent().setForeground(normalColor);
    cboCharset.setForeground(normalColor);
    
    if(source == null) return;
    
    long lastModified = source.getLastModified();
    DateFormat dateFormat =  null;
    if(localeType == 1) {
      dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, new Locale("vi"));
    } else {
      dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    }
    Calendar calendar = Calendar.getInstance();
    if(lastModified > 1) {
      calendar.setTimeInMillis(lastModified);
      lblLastModified.setText(txtLastModified + " " + dateFormat.format(calendar.getTime()));
    } else {
      lblLastModified.setText("");
    }
    
    long lastCrawledTime = source.getLastCrawledTime();
    if(lastCrawledTime > 1) {
      calendar.setTimeInMillis(lastCrawledTime);
      lblLastCrawledTime.setText(txtLastCrawledTime + " " + dateFormat.format(calendar.getTime()));
    } else {
      lblLastCrawledTime.setText("");
    }

    this.properties.clear();
    this.properties.putAll(source.getProperties());
    txtHome.clearItems();
    
    webClient = new FastWebClient2();
//   loginWebsite.reset();
    
    String [] home = source.getHome();
    if(home != null) txtHome.setItems(home);
    
    templateDataLink.setValue(source.getProperties(), "URLPattern");
    templateVisitLink.setValue(source.getProperties(), "LinkPattern");
    
    txtName.setText(source.getName());
    spinDepth.setSelection(source.getDepth());
    spinPriority.setSelection(source.getPriority());
    for(int i = 0; i < cboExtractType.getItemCount(); i++) {
      if(source.getExtractType().toString().equals(cboExtractType.getItem(i))) {
        cboExtractType.select(i);
        break;
      }
    }
    cboCharset.setText(source.getEncoding());   
    charset = source.getEncoding();
    
    
    setVisitRegions(null);
    setExtractRegions(null);
    setRemoveRegions(null);
//    cboUpdateRegion.removeAll();
    
    setDataRegions(RegionUtils.toList(source));
    
    if(source.getUpdateRegion() != null && source.getUpdateRegion().getPaths() != null) {
      setVisitRegions(source.getUpdateRegion().getPaths());
    }
    
    Region [] regions = source.getExtractRegion();
    if(regions != null && regions.length > 0 &&
        regions[0] != null && regions[0].getPaths() != null) {
      setExtractRegions(regions[0].getPaths());
    }
    
    boolean isCleanFrom = false;
    if(regions != null && regions.length > 1 &&
        regions[1] != null && regions[1].getPaths() != null) {
      setRemoveRegions(regions[1].getPaths());
      
      try {
        String property = regions[1].getProperties().getProperty(Region.CLEAN_FROM);
        if(property != null) isCleanFrom = Boolean.valueOf(property).booleanValue();
      } catch (Exception e) {
        ClientLog.getInstance().setThrowable(null, e);
      }
    }
    butAllowRemoveFrom.setSelection(isCleanFrom);
    
    cboCrawlHours.removeAll();
    if(source.getCrawlTimes() != null) {
      cboCrawlHours.setItems(source.getCrawlTimes());
      if(cboCrawlHours.getItemCount() > 0) cboCrawlHours.select(0);
    }
    
    
    isNewSource = false;
//    cboSourceType.select(0);
    
    if(version == 0) new LoadSourceVerions(this).execute(source);
    propertiesComposite.setProperties(properties);
    
    if(source.getPattern() != null && !source.getPattern().trim().isEmpty()) {
      String urlPattern = source.getPattern();
      if(!SWProtocol.isHttp(urlPattern)) {
        try {
          URL url =  new URL(home[0]);
          urlPattern = url.getProtocol()+"://"+url.getHost();
          if( url.getPort() > 0) urlPattern += ":"+String.valueOf(url.getPort());
          urlPattern += source.getPattern();
        } catch( Exception exp){
          txtPattern.setText("");
        }   
      } 
      txtPattern.setText(urlPattern);
    } else {
      txtPattern.setText("");
    }
    
  }
  

  public void setGroup(Group value) {
    setDataRegions(null);
    setEnable(!value.getType().equals(Group.DUSTBIN));
  }
  
  public boolean check(){
    String text = txtName.getText().trim();
    ClientRM resources = new ClientRM("Creator");
//    StatusBar statusBar = creator.getStatusBar();
    Color errorColor = new Color(getDisplay(), 255, 0, 0);
    Color normalColor = new Color(getDisplay(), 0, 0, 0);
    if(text.length() == 0){
      String message = resources.getLabel(getClass().getName()+".msgErrorEmptyName");
      creator.showMessage(message, creator.getErrorIcon(), Creator.ERROR_FIELD);
//      ClientLog.getInstance().setMessage(getShell(), new Exception(message));
      return false;
    }
    
    int i = 0;
    StringBuilder builder = new StringBuilder();
    while(i < text.length()) {
      char c = text.charAt(i); 
      if(Character.isLetterOrDigit(c) || c == ' ') builder.append(c);
      i++;
    }
    txtName.setText(builder.toString());
    
    
    String category = creator.getSelectedCategory();
    if(category == null || category.trim().isEmpty()) {
      category = creator.getSelectedCategory();
    }
    
    if(category == null || category.trim().isEmpty()) {
      String message = resources.getLabel(getClass().getName()+".msgErrorEmptyCategory");
//      statusBar.setMessage(message, errorColor);
      ClientLog.getInstance().setMessage(getShell(), new Exception(message));
      return false;
    }
    
    if(txtHome.getText().trim().isEmpty() && txtHome.getItems().length > 0) {
      txtHome.select(0);
    }
    
    txtHome.getTextComponent().setForeground(normalColor);
    if(txtHome.getText().trim().isEmpty()) {
      String message = resources.getLabel(getClass().getName()+".msgErrorEmptyHome");
      creator.showMessage(message, creator.getErrorIcon(), Creator.ERROR_FIELD);
      txtHome.getTextComponent().setForeground(errorColor);
//      ClientLog.getInstance().setMessage(getShell(), new Exception(message));
      return false;
    }
    
    cboCharset.setForeground(normalColor);
    if(!(cboCharset.getText().trim().isEmpty())){
      try {
        Charset.forName(cboCharset.getText().trim());        
      }catch( Exception exp){
        String message = resources.getLabel(getClass().getName()+".msgErrorEncoding");
        creator.showMessage(message, creator.getErrorIcon(), Creator.ERROR_FIELD);
        cboCharset.setForeground(errorColor);
//        ClientLog.getInstance().setMessage(getShell(), new Exception(message));
        return false;
      }
    }
    
    propertiesComposite.saveProperties();
    LinkPatterns dataPatterns = createPatterns(LinkPatterns.class, templateDataLink.getItems());
    txtPattern.setForeground(normalColor);
    if(dataPatterns != null) {
      URLEncoder encoder = new URLEncoder();
      String p = txtPattern.getText();
      if(!dataPatterns.match(p) && !dataPatterns.match(encoder.encode(p))) {
        String message = resources.getLabel(getClass().getName()+".msgErrorPattern");
        creator.showMessage(message, creator.getErrorIcon(), Creator.ERROR_FIELD);
        txtPattern.setForeground(errorColor);
//        ClientLog.getInstance().setMessage(getShell(), new Exception(message));
      } 
    } 
    return true;
  }   
  
  public FastWebClient getWebClient() {
    String userAgent = properties.getProperty(SourceProperties.USER_AGENT);
    if(userAgent != null && userAgent.trim().isEmpty()) userAgent = null;
    webClient.setUserAgent(userAgent);
    return webClient; 
  }

  public String getLastURL() { return lastUrl; }

  public void setLastURL(String lastUrl) { this.lastUrl = lastUrl; }
  
  public String getReferer() {
    if(properties == null) return null;
    if(properties.containsKey(HttpClientFactory.REFERER_NAME)) {
      return properties.getProperty(HttpClientFactory.REFERER_NAME).trim();
    }
    return null;
  }
  
  protected void sendSource() {
    Source source = createSource();
    if(source == null) return;
    Shell window = new Shell(getShell(), SWT.TITLE | SWT.APPLICATION_MODAL);
    window.setLayout(new FillLayout());
    new SendSource(window, new SendSourceWorker(source));
    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
    int x = (displayRect.width - 350) / 2;
    int y = (displayRect.height - 200)/ 2;
    window.setLocation(x, y);
    window.pack();
//    XPWindowTheme.setWin32Theme(window);
    window.open();
  }
  
  public ISourceConfig getSourceConfig() {
    DefaultSourceConfig config = new DefaultSourceConfig();
    config.setName(String.valueOf(hashCode()));
    
    config.setServer(butServerConfig.getSelection());
    
    config.setReferer(getReferer());
    config.setCharset(charset);
    config.setHomepage(txtHome.getText());
    
    config.setProperties(this.properties);
    config.setWebClient(webClient);
    
    config.setDecode("#decode#".equals(properties.getProperty(SourceProperties.CONTENT_FILTER)));
    
    loadSystemProperties(config);
    
    return config;
  }
  
  private void loadSystemProperties(final DefaultSourceConfig config) {
    Worker excutor = new Worker() {

      public void abort() { }
      public void before() { }

      public void execute() {
        try {
          DataClientHandler handler = DataClientHandler.getInstance();
          config.setSystem(handler.getSystemProperties());
        } catch (Exception e) {
          ClientLog.getInstance().setException(null, e);
        }
      }

      public void after() {}
    };
    new ThreadExecutor(excutor, this).start();
  }
 
}
