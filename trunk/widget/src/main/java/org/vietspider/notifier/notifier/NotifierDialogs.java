package org.vietspider.notifier.notifier;

//@author Emil
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.vietspider.ui.widget.UIDATA;

public class NotifierDialogs {

  static List<NotifierDialog> activeShells = new CopyOnWriteArrayList<NotifierDialog>();

  //  public static NotifierDialog notify(Shell parent, int displayTime, 
  //      String title, String message, String id, Image icon, NotificationListener listener) {
  public static NotifierDialog notify(Notifier notifier) {
    NotifierDialog dialog_ = new NotifierDialog(notifier);
    //        parent, displayTime, title, message, id, icon, listener);
    // move other shells up
    if (!activeShells.isEmpty()) {
      List<NotifierDialog> modifiable = new ArrayList<NotifierDialog>(activeShells);
      Collections.reverse(modifiable);
      for (NotifierDialog dialog : modifiable) {
        Shell shell = dialog.getShell();
        if(shell.isDisposed() ||shell.getAlpha() < 1) continue;
        Point curLoc = shell.getLocation();
        shell.setLocation(curLoc.x, curLoc.y - 100);
        if (curLoc.y - 100 < 0) {
          activeShells.remove(shell);
          shell.dispose();
        }
      }
    }

    //    Rectangle clientArea = parent.getMonitor().getClientArea();
    Rectangle clientArea = notifier.getParent().getBounds();//parent.getClientArea();

    int startX = clientArea.x + clientArea.width - 352;//352;
    int startY = clientArea.y + clientArea.height - 105;

    Shell shell = dialog_.getShell();
    shell.setLocation(startX, startY);
    shell.setAlpha(0);
    shell.setVisible(true);

    activeShells.add(dialog_);
    dialog_.fadeIn();

    return dialog_;
  }

  public static NotifierDialog notify2(Notifier notifier) {
    NotifierDialog dialog_ = new NotifierDialog();
    Color textColor = new Color(Display.getDefault(), 0, 0, 0);
    dialog_.setTitleFgColor(textColor);
    dialog_.setTitleFont(UIDATA.FONT_9B);
    dialog_.setTextFont(UIDATA.FONT_8T);
    dialog_.setTextFgColor(textColor);
    dialog_.init(notifier);
    dialog_.setWidth(400);
    dialog_.createGUI(notifier);
    //        parent, displayTime, title, message, id, icon, listener);
    // move other shells up
    if (!activeShells.isEmpty()) {
      List<NotifierDialog> modifiable = new ArrayList<NotifierDialog>(activeShells);
      Collections.reverse(modifiable);
      for (NotifierDialog dialog : modifiable) {
        Shell shell = dialog.getShell();
        if(shell.isDisposed() ||shell.getAlpha() < 1) continue;
        //        Point curLoc = shell.getLocation();
        //        shell.setLocation(curLoc.x, curLoc.y - 50);
        //        if (curLoc.y - 50 < 0) {
        activeShells.remove(shell);
        shell.dispose();
        //        }
      }
    }

    Rectangle clientArea = notifier.getParent().getMonitor().getBounds();

    int startX = clientArea.x + clientArea.width - (dialog_.getWidth() + 2);//352;
    int startY = clientArea.y + clientArea.height - 150;

    final Shell shell = dialog_.getShell();
    shell.setLocation(startX, startY);
    shell.setAlpha(0);
    shell.setVisible(true);
    Timer t = new Timer ();
    TimerTask task = new TimerTask () {
      @Override
      public void run () {
        if(shell.isDisposed()) return;
        shell.getDisplay().asyncExec (new Runnable () {
          public void run () {
            if(shell.isDisposed()) return;
            shell.forceActive();
          }
        });
      }
    };
    int time = 5000;
    t.scheduleAtFixedRate (task, 0, time);

    activeShells.add(dialog_);
    dialog_.fadeIn();

    return dialog_;
  }

  public static void remove(NotifierDialog dialog) {
    if(!dialog.getShell().isDisposed()) {
      dialog.getShell().dispose();
    }
    activeShells.remove(dialog);
  }

}
