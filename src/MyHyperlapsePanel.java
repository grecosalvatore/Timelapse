import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

@SuppressWarnings("serial")
public class MyHyperlapsePanel extends JPanel {

	// image to be painted on the panel
	private static MyImage img;
	// size of image painting
	// (effective size are in MyImage class)
	private int imgWidth, imgHeight;
	// image position (to center it in the panel)
	private static int imgX;
	private static int imgY;
	// flag: indicate if a portion of image is selected
	private static boolean sel;
	// mouse movements listener (to draw selection rectangle)
	private MyMouseListener lis;
	private JPopupMenu menuImage;
	private JMenuItem histo;
	// internal reference of itself
	private MyHyperlapsePanel thisclass;

	private static int rectX_start = 0;
	private static int rectY_start = 0;
	private static int rectHeight_start = 0;
	private static int rectWidth_start = 0;

	private static int rectX_end = 0;
	private static int rectY_end = 0;
	private static int rectHeight_end = 0;
	private static int rectWidth_end = 0;

	private static Point center_rect_start = new Point(0, 0);
	private static Point center_rect_end = new Point(0, 0);

	private static int realx_start, realy_start, realh_start, realw_start;
	private static int realx_end, realy_end, realh_end, realw_end;
	private static int flag_start_or_end; // 1 = click start rect, 2 = click end
											// rect, 0 out of rects
	private static int start_move_x = 0;
	private static int start_move_y = 0;

	public MyHyperlapsePanel() {
		this.imgWidth = this.imgHeight = this.imgX = this.imgY = 0;
		thisclass = this;
		MyImagePanel.setSel(false);
		if (Timelapse.getLookAndFeel() == 1) {
			this.setBackground(Color.DARK_GRAY);
		}
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
		g.drawRect(rectX_start, rectY_start, rectWidth_start, rectHeight_start);

		Color red_background = new Color(1f, 0f, 0f, .2f);
		g.setColor(red_background);
		g.fillRect(rectX_start, rectY_start, rectWidth_start, rectHeight_start);

		// draw colored rectangle (selection)
		g.setColor(Color.GREEN);
		Color green_background = new Color(0f, 1f, 0f, .2f);
		g.drawRect(rectX_end, rectY_end, rectWidth_end, rectHeight_end);

		g.setColor(green_background);
		g.fillRect(rectX_end, rectY_end, rectWidth_end, rectHeight_end);

		g.setColor(Color.cyan);
		g.drawLine(center_rect_start.x, center_rect_start.y, center_rect_end.x, center_rect_end.y);

		if (rectY_start == rectY_end) {
			Graphics2D g2 = (Graphics2D) g;

			BasicStroke bs = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10,
					new float[] { 5, 5, 5 }, 7);

			g2.setStroke(bs);
			g2.drawLine(rectX_start, rectY_start, rectX_end, rectY_end);
			g2.drawLine(rectX_start, rectY_start + rectHeight_start, rectX_end, rectY_end + rectHeight_start);
		}
		if (rectX_start == rectX_end) {
			Graphics2D g2 = (Graphics2D) g;

			BasicStroke bs = new BasicStroke(1, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10,
					new float[] { 5, 5, 5 }, 7);

			g2.setStroke(bs);
			g2.drawLine(rectX_start, rectY_start, rectX_end, rectY_end);
			g2.drawLine(rectX_start + rectWidth_start, rectY_start, rectX_end + rectWidth_start, rectY_end);
		}
	}

	public void setImage(MyImage img) {
		if (img == null)
			return;

		MyHyperlapsePanel.img = img;

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

	void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
		Graphics2D g = (Graphics2D) g1.create();

		double dx = x2 - x1, dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		int len = (int) Math.sqrt(dx * dx + dy * dy);
		AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
		at.concatenate(AffineTransform.getRotateInstance(angle));
		g.transform(at);

		// Draw horizontal arrow starting in (0, 0)
		g.setColor(Color.cyan);

		g.drawLine(0, 0, len, 0);

		g.fillPolygon(new int[] { len, len - 2, len - 2, len }, new int[] { 0, -2, 2, 0 }, 4);
	}

	public static int getRealX_start() {

		return realx_start;
	}

	public static int getRealY_start() {
		return realy_start;
	}

	public static int getRealH_start() {
		return realh_start;
	}

	public static int getRealW_start() {
		return realw_start;
	}

	public static int getRealX_end() {
		return realx_end;
	}

	public static int getRealY_end() {
		return realy_end;
	}

	public static int getRealH_end() {
		return realh_end;
	}

	public static int getRealW_end() {
		return realw_end;
	}

	public boolean isSel() {
		return sel;
	}

	public static void setSel(boolean sel) {

		MyHyperlapsePanel.sel = sel;

	}

	public static int getRectX_start() {
		return rectX_start;
	}

	public static int getRectY_start() {
		return rectY_start;
	}

	public static int getRectHeight_start() {
		return rectHeight_start;
	}

	public static int getRectWidth_start() {
		return rectWidth_start;
	}

	public static int getRectX_end() {
		return rectX_end;
	}

	public static int getRectY_end() {
		return rectY_end;
	}

	public static int getRectHeight_end() {
		return rectHeight_end;
	}

	public static int getRectWidth_end() {
		return rectWidth_end;
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
			if (evt.getClickCount() == 2) {
			}

			if (SwingUtilities.isLeftMouseButton(evt) && (evt.getClickCount() == 1)) {
				// check that selection doesn't start outside the painted image
				if ((evt.getX() >= imgX && evt.getX() <= imgWidth + imgX && (Timelapse.getFlagDrawRect() == true))
						&& (evt.getY() >= imgY && evt.getY() <= imgHeight + imgY)) {

					if ((evt.getX() >= rectX_end) && (evt.getX() <= (rectX_end + rectWidth_end))
							&& (evt.getY() >= rectY_end) && (evt.getY() <= (rectY_end + rectHeight_end))) {
						// end rect

						drawOk = true;
						flag_start_or_end = 2;
						start_move_x = evt.getX();
						start_move_y = evt.getY();
						setRealDimension();

					} else {
						if ((evt.getX() >= rectX_start) && (evt.getX() <= (rectX_start + rectWidth_start))
								&& (evt.getY() >= rectY_start) && (evt.getY() <= (rectY_start + rectHeight_start))) {
							// start rect

							drawOk = true;
							flag_start_or_end = 1;
							start_move_x = evt.getX();
							start_move_y = evt.getY();
							setRealDimension();

						}

					}

				} else {

					drawOk = false;
				}
			} else {
				drawOk = false;

			}

		}

		@Override
		public void mouseDragged(MouseEvent evt) {
			if (!drawOk)
				return;

			endX = evt.getX();
			endY = evt.getY();

			checkImageBorder();

			if (flag_start_or_end == 1) {

				// movement of start rect
				int app_move_x = 0;
				int app_move_y = 0;

				app_move_x = endX - start_move_x;
				app_move_y = endY - start_move_y;

				rectX_start += app_move_x;
				rectY_start += app_move_y;

				center_rect_start.x += app_move_x;
				center_rect_start.y += app_move_y;

				checkRectOutOfImageStart();
				start_move_x = endX;
				start_move_y = endY;
				setRealDimension();
				repaint();

			} else {
				if (flag_start_or_end == 2) {

					// movement of end rect
					int app_move_x = 0;
					int app_move_y = 0;

					checkRectOutOfImageStart();

					app_move_x = endX - start_move_x;
					app_move_y = endY - start_move_y;

					rectX_end += app_move_x;
					rectY_end += app_move_y;

					center_rect_end.x += app_move_x;
					center_rect_end.y += app_move_y;

					checkRectOutOfImageEnd();

					start_move_x = endX;
					start_move_y = endY;
					setRealDimension();
					repaint();
				}
			}

		}

		@Override
		public void mouseReleased(MouseEvent evt) {
			if (!drawOk)
				return;
			Timelapse.setFlagDir(false);
			endX = evt.getX();
			endY = evt.getY();

			checkImageBorder();
			setRealDimension();

			MyHyperlapsePanel.setSel(true);

		}

		private void checkRectOutOfImageStart() {
			int xSupLimit = imgWidth + imgX;
			int ySupLimit = imgHeight + imgY;
			int xInfLimit = imgX;
			int yInfLimit = imgY;

			// right overcome
			if ((endX + (rectX_start + rectWidth_start - start_move_x)) > xSupLimit) {
				rectX_start = xSupLimit - rectWidth_start - 1;
				center_rect_start.x = xSupLimit - (rectWidth_start / 2);

			}
			// lower overcome
			if (endY - (start_move_y - rectY_start) < yInfLimit) {
				rectY_start = yInfLimit;
				center_rect_start.y = yInfLimit + (rectHeight_start / 2);
			}
			// left overcome
			if (endX - (start_move_x - rectX_start) < xInfLimit) {
				rectX_start = xInfLimit;
				center_rect_start.x = xInfLimit + (rectWidth_start / 2);
			}
			// upper overcome
			if ((endY + (rectY_start + rectHeight_start - start_move_y)) > ySupLimit) {
				rectY_start = ySupLimit - rectHeight_start;
				center_rect_start.y = ySupLimit - (rectHeight_start / 2);
			}
		}

		private void checkRectOutOfImageEnd() {
			int xSupLimit = imgWidth + imgX;
			int ySupLimit = imgHeight + imgY;
			int xInfLimit = imgX;
			int yInfLimit = imgY;

			// right overcome
			if ((endX + (rectX_end + rectWidth_end - start_move_x)) > xSupLimit) {
				rectX_end = xSupLimit - rectWidth_end - 1;
				center_rect_end.x = xSupLimit - (rectWidth_end / 2);

			}
			// lower overcome
			if (endY - (start_move_y - rectY_end) < yInfLimit) {
				rectY_end = yInfLimit;
				center_rect_end.y = yInfLimit + (rectHeight_end / 2);
			}
			// left overcome
			if (endX - (start_move_x - rectX_end) < xInfLimit) {
				rectX_end = xInfLimit;
				center_rect_end.x = xInfLimit + (rectWidth_end / 2);
			}
			// upper overcome
			if ((endY + (rectY_end + rectHeight_end - start_move_y)) > ySupLimit) {
				rectY_end = ySupLimit - rectHeight_end;
				center_rect_end.y = ySupLimit - (rectHeight_end / 2);
			}
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
				if (flag_start_or_end == 2) {

				} else {
					if (flag_start_or_end == 1) {

					}
				}
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

	public void rectAspectRatio(int width, int height) {
		rectWidth_start = imgWidth * width / img.getWidth();
		rectWidth_end = imgWidth * width / img.getWidth();

		rectHeight_start = imgHeight * height / img.getHeight();
		rectHeight_end = imgHeight * height / img.getHeight();
	}

	public void setRectDimension(int width, int height) {
		realw_start = width;
		realw_end = width;

		realh_start = height;
		realh_end = height;

		rectAspectRatio(width, height);

		flag_start_or_end = 0;

		rectX_start = imgX;
		rectY_start = imgY;

		rectX_end = imgX + imgWidth - rectWidth_end - 1;
		rectY_end = imgY;

		center_rect_start.setLocation(imgX + (rectWidth_start / 2), imgY + (rectHeight_start / 2));
		center_rect_end.setLocation(imgX + (imgWidth) - (rectWidth_end / 2), imgY + (rectHeight_end / 2));

		repaint();

	}

	public void setRealDimension() {
		// realx_start = img.getWidth() * rectX_start / realw_start - imgX ;
		// realx_end = img.getWidth() * rectX_end / realw_end - imgX ;

		realx_start = (rectX_start - imgX) * (img.getWidth()) / imgWidth;
		realy_start = (rectY_start - imgY) * (img.getHeight()) / imgHeight;

		// realy_start = (img.getHeight() * rectY_start / imgHeight) ;
		// realy_end = (img.getHeight() * rectY_end / imgHeight) - imgY ;

		realx_end = (rectX_end - imgX) * (img.getWidth()) / imgWidth;
		realy_end = (rectY_end - imgY) * (img.getHeight()) / imgHeight;

		System.out.println("rettangolo partenza");
		System.out.println("x nello schermo:    " + rectX_start);
		System.out.println("y nello schermo:    " + rectY_start);
		System.out.println("x reale:    " + realx_start);
		System.out.println("y reale:    " + realy_start);
		System.out.println("");
		System.out.println("rettangolo arrivo");
		System.out.println("x nello schermo:    " + rectX_end);
		System.out.println("y nello schermo:    " + rectY_end);
		System.out.println("x reale:    " + realx_end);
		System.out.println("y reale:    " + realy_end);

	}

}
