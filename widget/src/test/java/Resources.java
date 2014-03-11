import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class Resources {
	private static class InnerPanel extends Canvas {
		public InnerPanel(final Composite parent) {
			super(parent, SWT.DOUBLE_BUFFERED);

			new Thread() {
				@Override
				public void run() {
					while (true) {
						try {
							Thread.sleep(100);
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									if (!InnerPanel.this.isDisposed())
										InnerPanel.this.redraw();
								}
							});
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				}
			}.start();

			this.addPaintListener(new PaintListener() {
				int i = 0;

				@Override
				public void paintControl(PaintEvent e) {
					GC gc = e.gc;
					int xl = e.x;
					int yt = e.y;
					int xc = e.x + e.width / 2;
					int yc = e.y + e.height / 2;
					gc.setBackground(e.display.getSystemColor(SWT.COLOR_BLACK));
					gc.fillRectangle(xl, yt, e.width / 2, e.height / 2);
					i++;
					gc.setForeground(e.display.getSystemColor(SWT.COLOR_WHITE));
					gc.drawString("" + i, e.x + 10, e.y + 10);

					Color c2 = new Color(e.display, 0, 0, 0);
					gc.setBackground(c2);
					gc.fillRectangle(xc, yc, e.width / 2, e.height / 2);
					c2.dispose();

					Color c3 = new Color(e.display, 100, 100, 100);
					gc.setBackground(c3);
					gc.fillRectangle(xl, yc, e.width / 2, e.height / 2);
					c3.dispose();

					Color c4 = new Color(e.display, 200, 200, 200);
					gc.setBackground(c4);
					gc.fillRectangle(xc, yt, e.width / 2, e.height / 2);
					c4.dispose();

				}
			});
		}
	}

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setSize(430, 240);
		shell.setText("SWT Snake");
		FillLayout layout = new FillLayout();
		shell.setLayout(layout);

		InnerPanel snake = new InnerPanel(shell);

		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();
	}
}
