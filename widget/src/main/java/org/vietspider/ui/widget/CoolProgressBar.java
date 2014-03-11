package org.vietspider.ui.widget;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
/**
 * <dl>
 * <dt><b>Styles:</b></dt>
 *    <dd>SWT.HORIZONTAL</dd>
 *    <dd>SWT.VERTICAL</dd>
 * </dl>
 * <p>
 * 
 * The <code>CoolProgressBar</code> is a progress bar class created to provide a dynamic way to have
 * a really (cool) looking progress bar. <br><br>
 * 
 * The <code>CoolProgressBar</code> makes use of a 4 <code>org.eclipse.swt.graphics.Image</code> classes.<br> 
 * 2 of the images are used for the left/top and right/bottom border. <br>
 * 1 image (tiled) will be used for filled region.<br>
 * 1 image (tiled) will be used for empty region.<br><br>
 * 
 * 
 * <br><br>
 * Sample on how to use the <code>CoolProgressBar</code> is provided in the samples package.
 * <br><br>
 * 
 * @author Code Crofter <br>
 * On behalf Polymorph Systems
 *
 * @since RCP Toolbox v0.1 <br>
 * 
 */
public class CoolProgressBar extends Composite{
	/**Right border region, or bottom border region (SWT.VERTICAL). Image used in this region is not tiled and the area that this 
	 * region occupies is not part of the fill or empty region.*/
	private final Canvas rightBorder;
	/**Right region, or bottom region (SWT.VERTICAL). Image used in this region is tiled*/
	private final Canvas fillRegion;
	/**Left border region, or top border region (SWT.VERTICAL). Image used in this region is not tiled and the area that this 
	 * region occupies is not part of the fill or empty region.*/
	private final Canvas leftBorder;
	/**Flag indicating that the style is horizontal. If not horizontal the vertical is assumed and visa versa*/
	private final boolean horizontal;
	/** the recommended width of the progress bar*/
	private final int recommendedWidth;
	/** the recommended height of the progress bar*/
	private final int recommendedHeight;
	/** The current progressed pixel position*/
	private int progressedPosition = 0;
	/** The current progress percentage 0 -> 1*/
	private double progressedPercentage = 0;

	/**
	 * The constructor for the <code>CoolProgressBar</code> to
	 * create progress either in vertical (fill from bottom to top) or horizontal (from left to right) alignment. <br>
	 * 
	 * An additional option is given here to specify the focus images for the thumb.
	 * 
	 * @param parent
	 * @param style   <code>SWT.VERTICAL</code> or <code>SWT.HORIZONTAL</code>
	 * @param leftmost 
	 * @param left
	 * @param right
	 * @param rightBorder
	 */	
	public CoolProgressBar(Composite parent, int style,
			Image leftBorderImage, 
			Image filledImage,
			Image emptyImage,
			Image rightBorderImage){
		super(parent,SWT.DOUBLE_BUFFERED);
		
		horizontal = (SWT.VERTICAL != (style&SWT.VERTICAL));
		setLayout(createLayout());

		recommendedWidth = leftBorderImage.getBounds().width;
		
		recommendedHeight = leftBorderImage.getBounds().height;
		
		leftBorder = createBorder(this,leftBorderImage);
		
		fillRegion = createFillRegion(this,horizontal?filledImage:emptyImage);
		
		createEmptyRegion(this,horizontal?emptyImage:filledImage);			
		
		rightBorder = createBorder(this,rightBorderImage);
		
		addControlListener(new ControlAdapter(){
			@Override
			public void controlResized(ControlEvent e) {
				progressedPosition = (int)(calculatePotentialFillExtent()*(horizontal?progressedPercentage:1-progressedPercentage));
				fillRegion.setLayoutData(createFilledLayoutData(new Rectangle(0,0,recommendedWidth,recommendedHeight)));
				layout();
			}
		});
	}
	
	private GridLayout createLayout(){
		final GridLayout gl = new GridLayout(horizontal?4:1,false);
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		gl.marginHeight = 0;
		gl.marginWidth = 0;
		return gl;
	}
	
	private Canvas createBorder(Composite parent, Image image){
		final Canvas canvas = new Canvas(parent,SWT.NONE);
		canvas.setBackgroundImage(image);		
		final GridData gd = new GridData(SWT.BEGINNING,SWT.BEGINNING,false,false);
		gd.minimumHeight = recommendedHeight;				
		gd.heightHint = recommendedHeight;
		gd.minimumWidth = recommendedWidth;
		gd.widthHint = recommendedWidth;		
		canvas.setLayoutData(gd);
		return canvas;
	}
	
	private Canvas createFillRegion(Composite parent, Image image){
		final Canvas canvas = new Canvas(parent,SWT.NONE);
		canvas.setBackgroundImage(image);				
		canvas.setLayoutData(createFilledLayoutData(image.getBounds()));
		return canvas;
	}
	
	private GridData createFilledLayoutData(Rectangle bounds){
		final GridData gd = new GridData(horizontal?SWT.BEGINNING:SWT.FILL,horizontal?SWT.FILL:SWT.BEGINNING,false,false);
		if(horizontal){
			gd.minimumHeight = recommendedHeight;				
			gd.heightHint = recommendedHeight;
			gd.widthHint = progressedPosition;
			gd.minimumWidth = progressedPosition;
		}else{
			gd.minimumWidth = recommendedWidth;
			gd.widthHint = recommendedWidth;
			gd.heightHint = progressedPosition;
			gd.minimumHeight = progressedPosition;
		}
		return gd;
	}
	
	private Canvas createEmptyRegion(Composite parent, Image image){
		final Canvas canvas = new Canvas(parent,SWT.NONE);
		canvas.setBackgroundImage(image);		
		final GridData gd = new GridData(horizontal?SWT.FILL:SWT.FILL,horizontal?SWT.BEGINNING:SWT.FILL,horizontal,!horizontal);
		if(horizontal){
			gd.minimumHeight = recommendedHeight;				
			gd.heightHint = recommendedHeight;
		}else{
			gd.minimumWidth = recommendedWidth;
			gd.widthHint = recommendedWidth;
		}
		canvas.setLayoutData(gd);
		return canvas;
	}
	
	/**
	 * Method to update the progress
	 * 
	 * @param percentage between 0 and 100%
	 */
	public void updateProgress(double percentage){
		checkWidget();
		if(percentage < 0){
			percentage = 0;
		}else if(percentage > 1){
			percentage = 1;
		}
		progressedPercentage = percentage;
		progressedPosition = (int)(calculatePotentialFillExtent()*(horizontal?percentage:1-percentage));
		fillRegion.setLayoutData(createFilledLayoutData(new Rectangle(0,0,recommendedWidth,recommendedHeight)));
		layout();	
	}
	
	/** calculate the potential fill extent of the progress bar*/
	private int calculatePotentialFillExtent(){
		if(horizontal){
			return getClientArea().width - leftBorder.getBounds().width-rightBorder.getBounds().width;
		}else{
			return getClientArea().height - leftBorder.getBounds().height-rightBorder.getBounds().height;
		}
	}
	/** The current progress range 0 -> 1*/
	public double getCurrentProgress(){
		checkWidget();
		return progressedPercentage; 
	}
	
	@Override
	public final void setLayout(Layout layout) {
		super.setLayout(layout);
	}	
	
	@Override
	public Point computeSize(int wHint, int hHint) {
		checkWidget();		    
		int width = recommendedWidth;
		int height = recommendedHeight;
	    if (wHint != SWT.DEFAULT) width = wHint;
	    if (hHint != SWT.DEFAULT) height = hHint; 
		return new Point(width, height);  
	}	
}
