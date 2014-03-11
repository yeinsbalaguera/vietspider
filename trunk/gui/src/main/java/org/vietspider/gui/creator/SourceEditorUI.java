/***************************************************************************
 * Copyright 2003-2006 by VietSpider - All rights reserved.  *
 *    *
 **************************************************************************/
package org.vietspider.gui.creator;

import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.prefs.Preferences;

import net.sf.swtaddons.autocomplete.combo.AutocompleteComboInput;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.vietspider.chars.TextSpliter;
import org.vietspider.common.Application;
import org.vietspider.common.Install;
import org.vietspider.gui.browser.BrowserWidget;
import org.vietspider.gui.creator.action.AutoCompleteFromHomePage;
import org.vietspider.gui.creator.action.LoadVerionSource;
import org.vietspider.gui.creator.action.SaveSource;
import org.vietspider.gui.creator.action.SelectDataBlock;
import org.vietspider.gui.creator.action.SelectExtractBlock;
import org.vietspider.gui.creator.action.SelectRemoveBlock;
import org.vietspider.gui.creator.action.SelectVisitBlock;
import org.vietspider.gui.creator.action.TestConfig;
import org.vietspider.gui.creator.action.pattern.TPAutoCompleteHomepage;
import org.vietspider.gui.creator.action.pattern.TPphpBB;
import org.vietspider.gui.creator.action.pattern.TPvBulletin;
import org.vietspider.gui.creator.action.pattern.TextPatternModify;
import org.vietspider.gui.web.FastWebClient2;
import org.vietspider.ui.BareBonesBrowserLaunch;
import org.vietspider.ui.services.ClientLog;
import org.vietspider.ui.widget.ApplicationFactory;
import org.vietspider.ui.widget.ImageHyperlink;
import org.vietspider.ui.widget.UIDATA;
import org.vietspider.ui.widget.action.HyperlinkAdapter;
import org.vietspider.ui.widget.action.HyperlinkEvent;

/**
 *  Author : Nhu Dinh Thuan
 *          Email:nhudinhthuan@yahoo.com
 * Nov 2, 2006
 */
abstract class SourceEditorUI extends SourceEditorExtractUI {
  
  protected Creator creator; 
  
  protected Text txtName ;
  protected Button butAdd, butSave, butReset, butTest, butServerConfig, butAllowRemoveFrom;  
  protected Spinner spinDepth, spinPriority;
  protected Text txtPattern/*, txtCharset*/;  
  
  protected URLWidget txtHome;
  
  /*protected Combo *//*cboUpdateRegion,*/ /*cboExtractRegion,*/ /*cboRemoveRegion*/
  protected Combo cboCharset, cboExtractType, cboCrawlHours;
  
  protected Composite bottom;
  
  protected Object menuVersion;
  protected Hyperlink lblVersion;
  
//  private Combo cboNameRegion;
//  private Combo cboDataRegion;
  
  protected Label lblLastModified;
  protected Label lblLastCrawledTime;
  
  protected String txtVersion;
  protected String txtLastModified;
  protected String txtLastCrawledTime;
  
  protected URLTemplate templateDataLink;
  protected URLTemplate templateVisitLink;
  protected MenuItem miEditTemplate, miDeleteTemplate;
  
  protected Section sectionProperties;
  protected PropertiesComposite propertiesComposite;
  
  protected Properties properties = new Properties();
  
  protected boolean isNewSource = true;
//  protected LoginWebsite loginWebsite;
  protected String charset = null;
  
  protected FastWebClient2 webClient;
  
  protected List<TextPatternModify> textPatternModified = new ArrayList<TextPatternModify>();
  
  public SourceEditorUI(Composite parent, ApplicationFactory factory) {
    super(parent);
    
    SourceEditorUtil uiUtils = new SourceEditorUtil(this);
    
    imgNewBlock = factory.loadImage("new_block.png");
    imgEditBlock = factory.loadImage("edit_block.png");
    
    tipHasData = factory.getLabel("has.data.tip");
    tipVisitRegion = factory.getLabel("visit.region.tip");
    tipExtractRegion = factory.getLabel("extract.region.tip");
    tipRemoveRegion = factory.getLabel("remove.region.tip");
    tipDataRegion = factory.getLabel("data.region.tip");
    
    parent = factory.getComposite();
    String parentName = factory.getClassName();
    factory.setClassName(getClass().getName());
    
    GridLayout gridLayout = new GridLayout(3, false);
    gridLayout.marginHeight = 5;
    gridLayout.horizontalSpacing = 5;
    gridLayout.verticalSpacing = 5;
    gridLayout.marginWidth = 2;
    setLayout(gridLayout);
    
    GridData gridData;
    
    //########################################## name and categories ########################
    
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
//    gridData.horizontalSpan = 2;
    uiUtils.createComposite(factory, 2, gridData);
    factory.createLabel("lblName");  
    txtName = factory.createText();
    Object nameMenu = new TextPopupMenu(factory, txtName, true).getMenu();
    if(Application.LICENSE == Install.SEARCH_SYSTEM) {
      factory.createStyleMenuItem(nameMenu, SWT.SEPARATOR);
      factory.createStyleMenuItem(nameMenu, "menuSendSource", new SelectionAdapter() {
        @SuppressWarnings("unused")
        public void widgetSelected(SelectionEvent e) {
          sendSource();
        }
      });
    }
    
    txtName.setFont(UIDATA.FONT_10);
    gridData = new GridData(GridData.FILL_HORIZONTAL);   
    txtName.setLayoutData(gridData); 
    
    Composite compVersion  = new Composite(this, SWT.NONE);
    gridData = new GridData();
    gridData.widthHint = 470;
    compVersion.setLayoutData(gridData);
    factory.setComposite(compVersion);
    
    gridLayout = new GridLayout(4, false);
    compVersion.setLayout(gridLayout);
    
    butServerConfig = factory.createButton(SWT.CHECK, "Server?");
    gridData = new GridData();
    gridData.widthHint = 70;
    butServerConfig.setLayoutData(gridData);
    butServerConfig.setToolTipText(factory.getLabel("load.web.server"));
    butServerConfig.setEnabled(Application.LICENSE != Install.PERSONAL);
    
    lblLastModified = factory.createLabel(SWT.NONE);
    gridData = new GridData();
    gridData.widthHint = 150;
    lblLastModified.setLayoutData(gridData);
    lblLastModified.setFont(UIDATA.FONT_9);
    txtLastModified = factory.getLabel("lblLastModified");
    
    lblLastCrawledTime = factory.createLabel(SWT.NONE);
    gridData = new GridData();
    gridData.widthHint = 150;
    lblLastCrawledTime.setLayoutData(gridData);
    lblLastCrawledTime.setFont(UIDATA.FONT_9);
    txtLastCrawledTime = factory.getLabel("lblLastCrawledTime");
    
    lblVersion = factory.createLink("lblHistory", new IHyperlinkListener() {
      @SuppressWarnings("unused")
      public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent arg0) {
        lblVersion.setFont(UIDATA.FONT_9);
      }
      @SuppressWarnings("unused")
      public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent arg0) {
        lblVersion.setFont(UIDATA.FONT_9I);
      }
      @SuppressWarnings("unused")
      public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent arg0) {
        lblVersion.setFont(UIDATA.FONT_9);
      }
    });
    gridData = new GridData();   
    lblVersion.setLayoutData(gridData);
    txtVersion = factory.getLabel("lblVersion");
      menuVersion = new Menu(getShell(), SWT.POP_UP);
      lblVersion.setMenu((Menu)menuVersion);
      for(int i = 0; i < 11; i++) {
        String text = txtVersion + " "+ String.valueOf(i);
        factory.createStyleMenuItem(menuVersion, text, new SelectionAdapter() {
          public void widgetSelected(SelectionEvent e) {
            MenuItem item = (MenuItem)e.getSource();
            String value = item.getText();
            try {
              int idx = value.indexOf(' ');
              int version = Integer.parseInt(value.substring(idx).trim());
              LoadVerionSource loader = new LoadVerionSource(SourceEditorUI.this, version);
              String group = creator.getSelectedGroupName(); 
              String cate = creator.getSelectedCategory();
              loader.execute(group, cate, txtName.getText());
            } catch (Exception exp) {
            }
          }
          
        });
      }
    
    if(Application.LICENSE == Install.PERSONAL) {
      lblVersion.setEnabled(false); 
    } else {
      lblVersion.addMouseListener(new MouseAdapter() {
        @SuppressWarnings("unused")
        public void mouseDown(MouseEvent e) {
          if(Application.LICENSE == Install.PERSONAL) return;
          if(txtName.getText().trim().isEmpty()) return;
//          if( menuVersion instanceof  PopupMenu) {  
//            ((PopupMenu)menuVersion).open();
//          } else {
            ((Menu)menuVersion).setVisible(true);
//          }
        }
      });
    }
    
    factory.setComposite(this);
    
  //########################################## home address ################################
    
    Group uiGroupHome = uiUtils.createGroup(factory, "", 1);
    factory.setComposite(uiGroupHome);
    
    uiUtils.createComposite(factory, uiGroupHome, 4);
    factory.createLabel("lblHome");
    txtHome = new URLWidget(factory, this) ;
    txtHome.addModifyListener(new ModifyListener() {
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent e){
        AutoCompleteFromHomePage instance = 
          new AutoCompleteFromHomePage(txtName, txtHome, cboCharset, webClient);
        String value = instance.complete();
        if(value != null && value.trim().length() > 0) charset = value;
      }
    });
    
    iconVisitRegion = factory.createIcon(imgNewBlock, tipVisitRegion, new HyperlinkAdapter(){  
      public void linkActivated(HyperlinkEvent evt) {
        txtName.setFocus();
        Control control = (Control)evt.widget;
        new SelectVisitBlock(SourceEditorUI.this, control).execute();
      }
    });
    gridData = new GridData();
    gridData.heightHint = 26;
    iconVisitRegion.setLayoutData(gridData);
    
    createClearMenu(factory, iconVisitRegion, "menuClearVisitRegion", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        setVisitRegions(null);
      }
    });
    
//    gridData = new GridData(GridData.FILL_HORIZONTAL);      
//    gridData.horizontalSpan = 3;
    uiUtils.createComposite(factory, uiGroupHome, 3);
//    createComposite(factory, 3, gridData);
    factory.createLabel("lblLinkTemplate");
    templateVisitLink = new URLTemplate(factory);
    
  //########################################## input pattern url ###############################
    
    factory.setComposite(this);
    
    Group uiGroupPattern = uiUtils.createGroup(factory, "", 1);
    factory.setComposite(uiGroupPattern);
    
    uiUtils.createComposite(factory, uiGroupPattern, 3);
    factory.createLabel("lblPattern"); 
    txtPattern = factory.createText();
    txtPattern.setFont(UIDATA.FONT_10);
    txtPattern.setLayoutData(new GridData(GridData.FILL_HORIZONTAL)); 
    txtPattern.addModifyListener(new ModifyListener(){
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent e){
        String text = txtPattern.getText();
        if((text = text.trim()).isEmpty() || !isNewSource) return;
        for(int i = 0; i < textPatternModified.size(); i++) {
          textPatternModified.get(i).execute(SourceEditorUI.this, text);
        }
      }
    });
    textPatternModified.add(new TPAutoCompleteHomepage());
    textPatternModified.add(new TPvBulletin());
    textPatternModified.add(new TPphpBB());
    SourceEditorUtil.setDropTarget(txtPattern);
    txtPattern.setDoubleClickEnabled(false);
    txtPattern.addMouseListener(new MouseAdapter() {
      public void mouseDown(MouseEvent e) {
        if(e.count == 3) {
          txtPattern.setText("");
          txtPattern.paste();
          return;
        }       
      }
    });
    
    iconExtractRegion = factory.createIcon(imgNewBlock, tipExtractRegion, new HyperlinkAdapter(){  
      public void linkActivated(HyperlinkEvent evt) {
        selectExtractDataBlock((Control)evt.widget);
      }
    });
    gridData = new GridData();
    gridData.heightHint = 26;
    iconExtractRegion.setLayoutData(gridData);

    createClearMenu(factory, iconExtractRegion, "menuClearExtractRegion", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        setExtractRegions(null);
      }
    });
    
    uiUtils.createComposite(factory, uiGroupPattern, 3);
    factory.createLabel("lblPatternTemplate");
    templateDataLink = new URLTemplate(factory);
    
    factory.createStyleMenuItem(templateDataLink.getMenu(), SWT.SEPARATOR);
    
    factory.createStyleMenuItem(templateDataLink.getMenu(), "menuUseDataLink", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String text = txtPattern.getText();
        if((text = text.trim()).isEmpty()) return;
        templateDataLink.putText(text);
      }
    });
    
    Object patternMenu = new TextPopupMenu(factory, txtPattern, true).getMenu();

    factory.createStyleMenuItem(patternMenu, SWT.SEPARATOR);
    
    factory.createStyleMenuItem(patternMenu, "menuGo", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String address = txtPattern.getText();
        if(ApplicationFactory.isMozillaBrowser()) {
          BrowserWidget browserWidget = creator.getWorkspace().getTab().createItem();
          browserWidget.setUrl(address);
          return;
        }
        BareBonesBrowserLaunch.openURL(getShell(), address);
      }
    });
    
    factory.createStyleMenuItem(patternMenu, "Reauto Config", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        String url = txtPattern.getText();
        setExtractRegions(null);
        txtPattern.setText(url);
      }
    });
    
  //########################################## extract type ######################################
    
    Composite compExtractOption = uiUtils.createComposite(factory, uiGroupPattern, 3);
    
    uiUtils.createComposite(factory, compExtractOption, 2);
//    composite.setLayoutData(new GridData());
    factory.createLabel("lblExtractType");    
    cboExtractType = factory.createCombo(SWT.DROP_DOWN | SWT.READ_ONLY);
    cboExtractType.setVisibleItemCount(5);
    cboExtractType.setItems(new String[]{"NORMAL", "ROW"});
    cboExtractType.select(0);
    
    uiUtils.createComposite(factory, compExtractOption, 3);
    factory.createLabel("lblRemove"); 
    
    gridData = new GridData();
    gridData.heightHint = 26;
    iconRemoveRegion = factory.createIcon(imgNewBlock, tipRemoveRegion, new HyperlinkAdapter(){  
      public void linkActivated(HyperlinkEvent evt) {
        Control control = (Control)evt.widget;
        new SelectRemoveBlock(SourceEditorUI.this, control).execute();
      }
    });
    gridData = new GridData();
    gridData.heightHint = 26;
    iconRemoveRegion.setLayoutData(gridData);
    
    createClearMenu(factory, iconRemoveRegion, "menuClearRemoveRegion", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        setRemoveRegions(null);
      }
    });
    
    butAllowRemoveFrom = factory.createButton("butAllowRemoveFrom", SWT.CHECK, null);
    
    uiUtils.createComposite(factory, compExtractOption, 2);
//    composite.setLayoutData(new GridData());
    factory.createLabel("lblDataRegion"); 
    
    gridData = new GridData();
    gridData.heightHint = 26;
    iconDataRegion = factory.createIcon(imgNewBlock, tipDataRegion, new HyperlinkAdapter(){  
      public void linkActivated(HyperlinkEvent evt) {
        Control control = (Control)evt.widget;
        new SelectDataBlock(SourceEditorUI.this, control).execute();
      }
    });
    gridData = new GridData();
    gridData.heightHint = 26;
    iconDataRegion.setLayoutData(gridData);
    
    createClearMenu(factory, iconDataRegion, "menuClearDataRegion", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        setDataRegions(createDefaultDataRegions());
      }
    });
    
 // ############################################ Properties Component #############################
    
    sectionProperties = new Section(this, Section.TWISTIE  | Section.EXPANDED);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    gridData.heightHint = 70;
    sectionProperties.setLayoutData(gridData);
    sectionProperties.addExpansionListener(new ExpansionAdapter() {
      @SuppressWarnings("unused")
      public void expansionStateChanged(ExpansionEvent e) {
        SourceEditorUI uiSourceInfo = SourceEditorUI.this;
        Point point = uiSourceInfo.getSize();
        boolean expanded = sectionProperties.isExpanded();
        if(sectionProperties.isExpanded()) {
          uiSourceInfo.setSize(point.x, point.y + 180);
        } else {
          uiSourceInfo.setSize(point.x, point.y - 180);
        }
        Preferences prefs_ = Preferences.userNodeForPackage(Creator.class);
        prefs_.put("source.properties.expanded", String.valueOf(expanded));
      }
    });
    sectionProperties.setText(factory.getResources().getLabel("source.properties"));
    sectionProperties.setFont(UIDATA.FONT_10);
    
//    loginWebsite = new LoginWebsite(this);
    propertiesComposite = new PropertiesComposite(factory, sectionProperties, false);
    sectionProperties.setClient(propertiesComposite);
    propertiesComposite.setProperties(properties);
    sectionProperties.setExpanded(false);
    
  //########################################## common data ###################################
    
    factory.setComposite(this);
    
//    Group settingComposite = createGroup(factory, "", 4); 
    Composite settingComposite = uiUtils.createComposite(factory, this, 3);
    gridLayout = new GridLayout(4, false);
    gridLayout.marginHeight = 0;
    gridLayout.horizontalSpacing = 0;
    gridLayout.verticalSpacing = 0;
    gridLayout.marginWidth = 0;
    settingComposite.setLayout(gridLayout);
    gridData = new GridData(GridData.FILL_HORIZONTAL);
    gridData.horizontalSpan = 3;
    settingComposite.setLayoutData(gridData);
//    uiGroup = createGroup(factory, "", 4);
    uiUtils.createComposite(factory, settingComposite, 3);
    factory.createLabel("lblEncoding"); 
    
    cboCharset = factory.createCombo(SWT.DROP_DOWN);
    cboCharset.setVisibleItemCount(10);
    String [] charsets = {"UTF-8", "windows-1252", "windows-1250", "ISO-8859-1", "US-ASCII", "UTF-16", "UTF-16BE", "UTF-16LE"};
    cboCharset.setItems(charsets);
    new AutocompleteComboInput(cboCharset);
    cboCharset.setText(Application.CHARSET);
    cboCharset.addSelectionListener(new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent arg0) {
        int idx = cboCharset.getSelectionIndex();
        if(idx < 0) return;
        charset = cboCharset.getItem(idx);
      }
    });
    cboCharset.addKeyListener(new KeyAdapter(){
      public void keyReleased(KeyEvent evt) {
        if(evt.keyCode == SWT.CR) charset = cboCharset.getText();
      }
    });

    gridData = new GridData();
    gridData.heightHint = 26;
    factory.createIcon("butMoreCharset", new HyperlinkAdapter(){  
      @SuppressWarnings("unused")
      public void linkActivated(HyperlinkEvent e) {
        SortedMap<String, Charset> sortedMap = Charset.availableCharsets();
        Iterator<String> iter = sortedMap.keySet().iterator();
        cboCharset.removeAll();
        while(iter.hasNext()) {
          cboCharset.add(iter.next());
        }
      }
    }).setLayoutData(gridData);
    cboCharset.setMenu(new Menu(getShell(), SWT.NONE));
    
    uiUtils.createComposite(factory, settingComposite, 2);
    factory.createLabel("lblDepth");    
    spinDepth = factory.createSpinner(SWT.BORDER);
    spinDepth.setMinimum(0);
    spinDepth.setMaximum(1000);
    spinDepth.setSelection(1);
    spinDepth.setIncrement(1);
    spinDepth.setMenu(new Menu(getShell(), SWT.NONE));
    
    uiUtils.createComposite(factory, settingComposite, 2);
    factory.createLabel("lblCrawlHours");  
    cboCrawlHours = factory.createCombo(SWT.DROP_DOWN);;
    gridData = new GridData();
    gridData.widthHint = 50;
    cboCrawlHours.setLayoutData(gridData);
    cboCrawlHours.addKeyListener(new KeyAdapter(){
      public void keyReleased(KeyEvent evt) {
        if(evt.keyCode != SWT.CR) return;
        addCrawlHour(normalizeHour(cboCrawlHours.getText()));
      }
    });
    Menu menuCrawlHours = new Menu(cboCrawlHours.getShell(), SWT.POP_UP);
    cboCrawlHours.setMenu(menuCrawlHours);
    factory.createStyleMenuItem(menuCrawlHours, "menuRemoveItem", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        cboCrawlHours.remove(normalizeHour(cboCrawlHours.getText()));
      }
    });
    
    factory.createStyleMenuItem(menuCrawlHours, "menuRemoveAll", new SelectionAdapter() {
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent e) {
        cboCrawlHours.removeAll();
      }
    });
    
    uiUtils.createComposite(factory, settingComposite, 2);
    factory.createLabel("lblPriority");  
    spinPriority = factory.createSpinner(SWT.BORDER);
    spinPriority.setMinimum(-1);
    spinPriority.setMaximum(15*48);
    spinPriority.setSelection(8);
    spinPriority.setIncrement(1);
    spinPriority.setPageIncrement(8);
    spinPriority.setMenu(new Menu(getShell(), SWT.NONE));
    final String dateLabel = factory.getLabel("date");
    final String hourLabel = factory.getLabel("hour");
    final String minuteLabel = factory.getLabel("minute");
    final String updateLabel = factory.getLabel("update");
    spinPriority.addModifyListener(new ModifyListener() {
      @SuppressWarnings("unused")
      public void modifyText(ModifyEvent event) {
        int value = spinPriority.getSelection();
        int hour = value/2;
        int date = 0;
        while(hour >= 24) {
          date++;
          hour -= 24;
        }
        StringBuilder builder = new StringBuilder(updateLabel);
        if(date > 0) builder.append(' ').append(date).append(' ').append(dateLabel);
        if(hour > 0) builder.append(' ').append(hour).append(' ').append(hourLabel);
        if(value%2 == 1) builder.append(' ').append(" 30 ").append(minuteLabel);
        spinPriority.setToolTipText(builder.toString());
      }
    });
    
//#################################################################################################
    
    bottom = new Composite(this, SWT.NONE);
    gridData = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
    gridData.horizontalSpan = 3;
    bottom.setLayoutData(gridData);
    RowLayout rowLayout = new RowLayout();
    bottom.setLayout(rowLayout);
    rowLayout.justify = true;
    factory.setComposite(bottom);  
    
    butSave = factory.createButton("butSave", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        saveSource();
      }      
    }, factory.loadImage("save.png"));    

    butTest = factory.createButton("butTest", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        test();
      }      
    }, factory.loadImage("test.png"));

    butReset = factory.createButton("butReset", new SelectionAdapter(){
      @SuppressWarnings("unused")
      public void widgetSelected(SelectionEvent evt) {
        reset();
      }      
    }, factory.loadImage("butReset.png"));     
    
    factory.setComposite(parent);
    factory.setClassName(parentName);
    setVisible(true);
  } 
  
  public void test() {
    new TestConfig(SourceEditorUI.this, this).execute();
  }

  public void saveSource() {
  /*  int type = cboSourceType.getSelectionIndex();
    String template = null;
    if(type > 0) {
      template = cboSourceType.getItem(type);
      int idx = template.indexOf(':');
      if(idx > 0) template = template.substring(idx+1).trim();
    }*/
    new SaveSource(SourceEditorUI.this/*, type, template*/).execute();
  }
  
  abstract void reset();
  
  abstract void sendSource();
  
//  public Button getButAllowRemoveFrom() { return butAllowRemoveFrom; }
  
  @SuppressWarnings("unchecked")
  public <T> T getField(String fieldName) {
    try {
      Field field = SourceEditorUI.class.getDeclaredField(fieldName);
      return (T)field.get(this);
    } catch (Exception e) {
       ClientLog.getInstance().setException(getShell(), e); 
    }
    return null;
  }
  
  public void setCharset(String charset) { this.charset = charset; }

  public void setIsNewSource(boolean isNewSource) { this.isNewSource = isNewSource; }

  /*private void setTemplate() {
    int selected  = cboSourceType.getSelectionIndex();
    if(selected < 2) {
      creator.setTemplate(null);
      return;
    }
    String template = cboSourceType.getItem(selected);
    creator.setTemplate(template.substring(template.indexOf(':')+1).trim());
  }*/
  
  void loadExpandedProperties() {
    boolean expanded = false;
    try {
      Preferences prefs_ = Preferences.userNodeForPackage(Creator.class);
      String value = prefs_.get("source.properties.expanded", "");
      if(value == null) return;
      expanded = Boolean.valueOf(value).booleanValue();
    } catch (Exception e) {
      return;
    }
    sectionProperties.setExpanded(expanded);
    Point point = getSize();
    if(!sectionProperties.isExpanded()) return;
    setSize(point.x, point.y + 180);
  }
  
  public void setCreator(Creator creator) { 
    this.creator = creator; 
  }
  
/*  public String getGroup() { 
    try {
      return creator.getSelectedGroupName(); 
    } catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
      return new org.vietspider.model.Group().getType();
    }
  }*/
  
  public void selectExtractDataBlock(Control control) {
    txtPattern.setFocus();
    new SelectExtractBlock(SourceEditorUI.this, control).execute();
  }
  
  public org.vietspider.model.Group getGroupInstance() { 
    try {
      return creator.getSelectedGroup(); 
    } catch (Exception e) {
      ClientLog.getInstance().setException(getShell(), e);
      return new org.vietspider.model.Group();
    }
  }
  
  
  void setEnable(boolean value) {
    butSave.setEnabled(value);
    butReset.setEnabled(value);
    butTest.setEnabled(value);
    
    iconVisitRegion.setEnabled(value);
    iconExtractRegion.setEnabled(value);
    iconRemoveRegion.setEnabled(value);
    iconDataRegion.setEnabled(value);
//    cboDataRegion.setEnabled(value);
   /* cboNameRegion.setEnabled(value);
    
    cboNameRegion.setItems(RegionUtils.getNames(getProcessRegions()));
    cboNameRegion.select(0);
    cboDataRegion.removeAll();*/
  }

  public Text getUIPattern() { return txtPattern; }
  
  private void createClearMenu(ApplicationFactory factory, 
      ImageHyperlink icon, String name, SelectionAdapter adapter) {
    Object iconMenu;
//    if(XPWidgetTheme.isPlatform()) {  
//      PopupMenu popupMenu = new PopupMenu(icon, XPWidgetTheme.THEME);
//      iconMenu = new CMenu();
//      popupMenu.setMenu((CMenu)iconMenu);
//    } else {
      iconMenu  = new Menu(getShell(), SWT.POP_UP);
      icon.setMenu((Menu)iconMenu);
//    }
    
    factory.createStyleMenuItem(iconMenu, name, adapter);
  }
  
  protected String normalizeHour(String value) {
    int index =  0;
    StringBuilder builder = new StringBuilder();
    while(index < value.length()) {
      char c = value.charAt(index);
      if(Character.isDigit(c) || c == '-') builder.append(c);
      index++;
    }
    
    value = builder.toString();
    TextSpliter spliter = new TextSpliter();
    String [] elements = spliter.toArray(value, '-');
    builder.setLength(0);
    if(elements.length < 2) return null;
    try {
      int min = Integer.parseInt(elements[0]);
      int max = Integer.parseInt(elements[1]);
      if(min > max) {
        int temp = min;
        min = max;
        max = temp;
      }
      if(min < 0) return null;
      if(max > 23) return null;
      builder.append(min).append('-').append(max);
    } catch (Exception e) {
      return null;
    }
    return builder.toString();
  }
  
  protected void addCrawlHour(String value) {
    if(value == null || value.length() < 1) return;
    String [] items = cboCrawlHours.getItems();
    for(String item : items) {
      if(item.equals(value)) return;
    }
    cboCrawlHours.add(value);
  }
  
  public void setEditMode(boolean value) {
//    butSave.setVisible(value);
//    butReset.setVisible(value);
    
    if(!value && !bottom.isDisposed()) {
      Control[] controls = bottom.getChildren();
      for(int i = 0; i < controls.length; i++) {
        controls[i].dispose();
      }
      bottom.dispose();
//      layout();
//      pack();
      redraw();
    }
    sectionProperties.setExpanded(value);
  }
}