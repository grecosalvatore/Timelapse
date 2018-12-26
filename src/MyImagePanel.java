import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

@SuppressWarnings("serial")
/**
 * this class extends JPanel and create the panel with the selected image and manage all listener 
 * of the image
 * 
 * MyImage img image to be painted on the panel
 * int imgWidth width of image painting
 * int imgHeight height of image painting
 * int imgX x position in the panel
 * int imgY y position in the panel
 * boolean sel flag if the rectange is selected 
 * MyMouseListener lis listener to draw the rectangle
 * 
 *  
 */
public class MyImagePanel extends JPanel {

	// image to be painted on the panel
	private static MyImage img;
	// size of image painting
	// (effective size are in MyImage class)
	private int imgWidth, imgHeight;
	// image position (to center it in the panel)
	private int imgX, imgY;
	// flag: indicate if a portion of image is selected
	private static boolean sel;
	// mouse movements listener (to draw selection rectangle)
	private MyMouseListener lis;
	private JPopupMenu menuImage;
	private JMenuItem histo;
	// internal reference of itself
	private MyImagePanel thisclass;
	private MyImage logo;
	private File app;

	/**
	 * constructor of MyIMagePanel
	 */
	public MyImagePanel() {
		this.imgWidth = this.imgHeight = this.imgX = this.imgY = 0;
		thisclass = this;
		MyImagePanel.setSel(false);
		if(Timelapse.getLookAndFeel()==1){
			this.setBackground(Color.DARK_GRAY);}
		
		
		logo = new MyImage();
		
		try {
			logo.setImage(ImageIO.read(new File("img//logotemporaneo.png")));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MyImagePanel.img = logo;
		
		
		
		lis = new MyMouseListener();
		this.addMouseListener(lis);
		this.addMouseMotionListener(lis);
		histo = new JMenuItem("show image's histogram");
		histo.addMouseListener(lis);
		menuImage = new JPopupMenu();
		menuImage.add(histo);
	}

	// overrides javax.swing.JComponent.paintComponent
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (img == null)
			return;

		this.calcAspectRatio();

		// draw image
		g.drawImage(img.getImage(), imgX, imgY, imgWidth, imgHeight, null);

		// draw colored rectangle (selection)
		g.setColor(Color.RED);
		g.drawRect(Math.min(lis.getStartX(), lis.getEndX()), Math.min(lis.getStartY(), lis.getEndY()),
				Math.abs(lis.getEndX() - lis.getStartX()), Math.abs(lis.getEndY() - lis.getStartY()));
	}

	public void setImage(MyImage img) {
		if (img == null)
			return;

		MyImagePanel.img = img;

		this.repaint();
	}

	// used to preserve the aspect ratio of the image
	public void calcAspectRatio() {
		double aspectRatio = (double) img.getWidth() / (double) img.getHeight();
		double aspectRatioPanel = (double) this.getWidth() / (double) this.getHeight();

		// panel is "tighter than high" compared to the image
		if (aspectRatioPanel < aspectRatio) {
			imgWidth = this.getWidth();
			imgHeight = (int) (this.getWidth() / aspectRatio);
		}
		// panel is "higher than wide" compared to the image
		else {
			imgWidth = (int) (this.getHeight() * aspectRatio);
			imgHeight = this.getHeight();
		}
		// to center image on panel
		imgX = (this.getWidth() - imgWidth) / 2;
		imgY = (this.getHeight() - imgHeight) / 2;
	}

	public void clearRect() {
		lis.setStartX(-1);
		lis.setStartY(-1);
		lis.setEndX(-1);
		lis.setEndY(-1);

		MyImagePanel.setSel(false);
	}

	public int getRealX() {
		return lis.realX;
	}

	public int getRealY() {
		return lis.realY;
	}

	public int getRealH() {
		return lis.realH;
	}

	public int getRealW() {
		return lis.realW;
	}

	public boolean isSel() {
		return sel;
	}

	public static void setSel(boolean sel) {

		MyImagePanel.sel = sel;

	}

	public class MyMouseListener extends MouseInputAdapter {
		private int startX, startY, endX, endY;
		private boolean drawOk;
		// real dimension of the selection on the image
		// (in the window, image is a preview and its size may not match real
		// size
		public int realX, realY, realH, realW;

		// getter
		public int getStartX() {
			return startX;
		}

		public int getStartY() {
			return startY;
		}

		public int getEndX() {
			return endX;
		}

		public int getEndY() {
			return endY;
		}

		// setter
		public void setStartX(int startX) {
			this.startX = startX;
		}

		public void setStartY(int startY) {
			this.startY = startY;
		}

		public void setEndX(int endX) {
			this.endX = endX;
		}

		public void setEndY(int endY) {
			this.startX = endY;
		}

		@Override
		public void mousePressed(MouseEvent evt) {
			if ((evt.getSource() == histo)&&(Timelapse.getStartEncoding() == false)) {
				Timelapse.elaborateHisto();
			}
			if (SwingUtilities.isLeftMouseButton(evt)&&(Timelapse.getStartEncoding() == false)) {
				// check that selection doesn't start outside the painted image
				if ((evt.getX() >= imgX && evt.getX() <= imgWidth + imgX && (Timelapse.getFlagDrawRect() == true))
						&& (evt.getY() >= imgY && evt.getY() <= imgHeight + imgY)) {
					startX = evt.getX();
					startY = evt.getY();
					drawOk = true;
				} else {
					drawOk = false;
				}
			} else {
				drawOk = false;
				menuImage.show(thisclass, evt.getX(), evt.getY());
			}

		}

		@Override
		public void mouseDragged(MouseEvent evt) {
			if (!drawOk)
				return;

			endX = evt.getX();
			endY = evt.getY();

			checkImageBorder();

			repaint(); // call paintComponent()
		}

		@Override
		public void mouseReleased(MouseEvent evt) {
			if (!drawOk)
				return;
			Timelapse.setFlagDir(false);
			endX = evt.getX();
			endY = evt.getY();

			checkImageBorder();

			// calculate real size and position of selection
			// -> real image X (not painted: may have different size)
			// -> (Math.min(startX, endX) - imgX) = X position on painted image
			// -> image.getWidth() = real width (size) of the image
			// -> imgWidth = painted image width (size)
			realX = (Math.min(startX, endX) - imgX) * (img.getWidth()) / imgWidth;
			realY = (Math.min(startY, endY) - imgY) * (img.getHeight()) / imgHeight;
			// (endX-startX) = painted rectangle size
			realW = Math.abs(endX - startX) * (img.getWidth()) / imgWidth;
			realH = Math.abs(endY - startY) * (img.getHeight()) / imgHeight;
			MyImagePanel.setSel(true);

			// as soon as selection is completed, image processing starts
			Timelapse.startElaborateImg();

		}

		// check that selection not overcome image border
		private void checkImageBorder() {
			// image is centered in (X,Y) -> (imgX,imgY)
			// it has size (width,height) -> (imgWidth,imgHeight)
			// maximux x is (X + width) -> (imgWidth + imgX)
			// minimux x is (X) -> (imgX)
			// Y is the same
			int xSupLimit = imgWidth + imgX;
			int ySupLimit = imgHeight + imgY;
			int xInfLimit = imgX;
			int yInfLimit = imgY;
			// right overcome
			if (endX > xSupLimit) {
				endX = xSupLimit;
			}
			// lower overcome
			if (endY > ySupLimit) {
				endY = ySupLimit;
			}
			// left overcome
			if (endX < xInfLimit) {
				endX = xInfLimit;
			}
			// upper overcome
			if (endY < yInfLimit) {
				endY = yInfLimit;
			}
		}
	}

}
