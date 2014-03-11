package org.vietspider.ui.widget.waiter;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.common.util.Worker;
import org.vietspider.notifier.cache.ColorCache;
import org.vietspider.ui.services.ImageLoader;

public class WaitLoading {
  
  private volatile static Image image = null;

  private Shell shell;

  private Worker worker;

  private Control control;
  private Color       _borderColor  = ColorCache.getColor(240, 240, 240);
  
  private ProgressBar progressBar;
  private Button button;


  public WaitLoading(Control control, Worker excutor_) {
    this(control, excutor_, SWT.APPLICATION_MODAL);
  }

  public WaitLoading(Control control, Worker excutor_, int style) {
    this.worker = excutor_;
    this.control = control;
    shell = new Shell(control.getShell(), style | SWT.NO_TRIM);
    shell.setAlpha(200);
    //    shell.setLayout(new GridLayout(2, true));

    //    shell.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_INFO_BACKGROUND));

    shell.setLayout(new Layout() {
      @SuppressWarnings("unused")
      protected void layout(Composite composite, boolean flushCache) {
        int width = composite.getBounds().width;
        int height = composite.getBounds().height;
        shell.setBounds(0, 0, width, height);
        int closeButtonDim = 30;
        button.setBounds(width - closeButtonDim - 3, 3, closeButtonDim, closeButtonDim);
        progressBar.setBounds(5, 37, width - 10, height - 45);
      }

      @SuppressWarnings("unused")
      protected Point computeSize(Composite composite, int wHint, int hHint, boolean flushCache) {
        return new Point(wHint, hHint);
      }
    });

    progressBar = new ProgressBar(shell, SWT.SMOOTH | SWT.INDETERMINATE);
//    progressBar.setBounds(10, 71, 274, 25);
    progressBar.setEnabled(true);
    progressBar.setVisible(true);

    /*ImageSequencer seqB = new ImageSequencer(shell, SWT.NONE,
        new Image[]{ 
        getImage(ToolBoxImageRegistry.IMG_INDICATOR_B_1),
        getImage(ToolBoxImageRegistry.IMG_INDICATOR_B_2),
        getImage(ToolBoxImageRegistry.IMG_INDICATOR_B_3),
        getImage(ToolBoxImageRegistry.IMG_INDICATOR_B_4),
        getImage(ToolBoxImageRegistry.IMG_INDICATOR_B_5),
        getImage(ToolBoxImageRegistry.IMG_INDICATOR_B_6),
        getImage(ToolBoxImageRegistry.IMG_INDICATOR_B_7),
        getImage(ToolBoxImageRegistry.IMG_INDICATOR_B_8),				
    },
    250,true);*/

    //    seqB.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING,false,false));
    //    seqB.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));

    button = new Button(shell, SWT.PUSH);
    if(image == null) {
        ImageLoader loader = new ImageLoader();
        image = loader.load(shell.getDisplay(), "butStop.png");
    }
    button.setImage(image); 
    button.moveAbove(shell);
    button.addMouseListener(new MouseAdapter() {
      @SuppressWarnings("unused")
      public void mouseUp(MouseEvent e) {
        worker.abortTask();
        shell.dispose();
      }
    });

    //    Button button = new Button(shell, SWT.PUSH);
    //    button.setText("Abort");
    //    button.addSelectionListener(new SelectionAdapter(){
    //      @SuppressWarnings("unused")
    //      public void widgetSelected(SelectionEvent e) {
    //        excutor.abortTask();
    //        shell.dispose();
    //      }
    //    });

    //    Region region = new Region();
    //    region.add(new int[] {0, 100, 50, 100});
    //    //define the shape of the shell using setRegion
    //    shell.setRegion(region);
    //    Rectangle size = region.getBounds();

    shell.addListener(SWT.Resize, new Listener() {
      @SuppressWarnings("unused")
      public void handleEvent(Event e) {
        Rectangle rect = shell.getClientArea();
        // create a new image with that size
        Image newImage = new Image(Display.getDefault(), Math.max(1, rect.width), rect.height);
        // create a GC object we can use to draw with
        GC gc = new GC(newImage);

        // fill background
//        gc.setForeground(_bgFgGradient);
//        gc.setBackground(_bgBgGradient);
        int round = 5;
        gc.fillRoundRectangle(rect.x, rect.y, rect.width, rect.height, round, round);

        // draw shell edge
        gc.setLineWidth(2);
        gc.setForeground(_borderColor);
        gc.drawRoundRectangle(rect.x + 1, rect.y + 1, rect.width - 2, rect.height - 2, round, round);
        // remember to dipose the GC object!
        gc.dispose();

        // now set the background image on the shell
        shell.setBackgroundImage(newImage);
      }
    });
    
    shell.setSize(250, 70);
    
    //    System.out.println(parent.getLocation().x + " , "+parent.getLocation().y);
//    int x = parent.getLocation().x + parent.getSize().x ;
//    int y = parent.getLocation().y + parent.getSize().y;
//    shell.setLocation(x/2 - shell.getSize().x /2, y/2 - (shell.getSize().y/2));
    
  }



  public void open() {
    Shell parent  = control.getShell();
    Point point = parent.getLocation();
    Point size = parent.getSize();
    
//    System.out.println("===  > " + point.x + " : "+ size.x + " : " + shell.getSize().x);
    
    int x =  point.x + size.x/2 - shell.getSize().x/2;
//    System.out.println(x);
    int y = point.y + size.y/2 - shell.getSize().y/2;// + shell.getSize().y/2;
    shell.setLocation(x, y);
//    XPWidgetTheme.setWin32Theme(shell);
    shell.open();
    Thread thread = new Thread () {
      public void run () {
        shell.getDisplay().syncExec(new Runnable () {
          public void run () {
            worker.executeBefore();
            new Thread(new Runnable() {
              public void run () {
                worker.executeTask();
              }
            }).start();

            while(worker.isRunning()) {
              if(control.isDisposed() || shell.isDisposed()) return;
              if (!shell.getDisplay().readAndDispatch()) shell.getDisplay().sleep();
            }

            worker.after();
            Worker [] plugins = worker.getPlugins();
            if(plugins != null) {
              for(int i = 0; i < plugins.length; i++) {
                if(plugins[i] == null) continue;
                new WaitLoading(control, plugins[i]).open();
              }
            }
            shell.dispose();
          }
        });
      }
    };
    thread.start ();
  }

  public Shell getWindow() { return shell; }

  public static void main(String[] args) {
    final Display display = new Display();
    final Shell shell = new Shell(display);
    shell.setText("Progress indicators");
    shell.setLayout(new GridLayout(1,false));	
    shell.setBounds(10, 50, 250, 200);		

    Worker excutor = new Worker() {

      public void abort() {
      }

      public void before() {
      }

      public void execute() {
        try {
          Thread.sleep(10*1000);
        } catch (Exception e) {
        }
      }

      public void after() {
        System.exit(0);
      }
    };

    WaitLoading loading =	new WaitLoading(shell, excutor);
//    Rectangle displayRect = UIDATA.DISPLAY.getBounds();
//    int x = (displayRect.width - 125) / 2;
//    int y = (displayRect.height - 100)/ 2;
//    loading.getShell().setLocation(x, y);
    
    Monitor primary = display.getPrimaryMonitor();
    Rectangle bounds = primary.getBounds();
    Rectangle rect = shell.getBounds();
    int x = bounds.x + (bounds.width - rect.width) / 2;
    int y = bounds.y + (bounds.height - rect.height) / 2;
    shell.setLocation(x, y);
    
    shell.open();
    
    loading.open();

    
    while (!shell.isDisposed()) {
      if (!display.readAndDispatch()){
        display.sleep();
      }
    }

    display.dispose();
  }
}