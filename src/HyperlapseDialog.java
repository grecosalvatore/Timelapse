import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

public class HyperlapseDialog extends JPanel implements ActionListener {

	private Thread threadLoadImg;

	private static MyHyperlapsePanel panelUp;
	private static JPanel panelDown;
	private String[] typeresolution = { "640x480", "720x576", "800x600", "1024x768", "1280x720", "1280x1024",
			"1360x768", "1920x1080", "2560x1600", "3840x2160" };
	private JComboBox resolution;
	private JButton confirm;
	private JButton cancel;
	private JLabel labelResolution;
	private Point resolutionHyperlapse;
	private static Boolean flagImageError = false;

	public static class loadImgWorker implements Runnable {
		public loadImgWorker() {
		}

		@Override
		public void run() {
			loadImg();
		}
	}

	public static void loadImg() {

		System.out.println("sono dentro al thread ");

		// loads image preview
		if (Timelapse.getDir() == null)
			return;
		// opens selected directory
		File folder = new File(Timelapse.getDir().getPath());
		System.out.println(folder);
		// lists files in the directory
		File[] listFile = folder.listFiles();

		// saves the first file
		File firstFile = listFile[0];
		System.out.println(firstFile);

		MyImage image = new MyImage();
		// loads the first image without error...
		// if there are error search the first image without error , otherwise
		// take the first file
		if (Timelapse.getNameError().size() > 0) {
			for (int i = 0; i < Timelapse.getListFile().length; i++) {
				for (int j = 0; j < Timelapse.getNameError().size() - 1; j++) {
					System.out.println(Timelapse.getListFile()[i].getName() + "  ");
					System.out.println(Timelapse.getNameError().get(j) + "\n");

					if (Timelapse.getListFile()[i].getName() == Timelapse.getNameError().get(j)) {
						System.out.println("trovato");
						flagImageError = true;
						break;
					}
				}
				if (flagImageError == false) {
					image.load(Timelapse.getListFile()[i].getAbsolutePath());
					break;
				}
			}
		} else {
			//if are there there aren't image with errors then take the first file
			System.out.println("no errors then take the first file");
			image.load(firstFile.getAbsolutePath());
		}
		// ...and paints it on the panel

		panelUp.setImage(image);

	}

	public HyperlapseDialog() {
		this.setBackground(Color.WHITE);
		resolutionHyperlapse = new Point(0, 0);
		setVisible(true);
		// this.setLayout(new BorderLayout());

		BorderLayout borderLayout = new BorderLayout();
		this.setLayout(borderLayout);

		panelUp = new MyHyperlapsePanel();
		// panelLeft.setSize(800, 800);
		panelUp.setVisible(true);
		panelUp.setSize(1200, 600);

		resolution = new JComboBox(this.typeresolution);
		resolution.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch (resolution.getSelectedItem().toString()) {
				case "640x480": {
					if ((Timelapse.getImage().getWidth() > 640) && (Timelapse.getImage().getHeight() > 480)) {
						panelUp.setRectDimension(640, 480);
						resolutionHyperlapse.x = 640;
						resolutionHyperlapse.y = 480;
					} else {
						JOptionPane.showMessageDialog(null, "Select a resolution < at images resolution ", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				}
				case "720x576": {
					if ((Timelapse.getImage().getWidth() > 720) && (Timelapse.getImage().getHeight() > 576)) {
						panelUp.setRectDimension(720, 576);
						resolutionHyperlapse.x = 720;
						resolutionHyperlapse.y = 576;
					} else {
						JOptionPane.showMessageDialog(null, "Select a resolution < at images resolution ", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				}
				case "800x600": {
					if ((Timelapse.getImage().getWidth() > 800) && (Timelapse.getImage().getHeight() > 600)) {
						panelUp.setRectDimension(800, 600);
						resolutionHyperlapse.x = 800;
						resolutionHyperlapse.y = 600;
					} else {
						JOptionPane.showMessageDialog(null, "Select a resolution < at images resolution ", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				}
				case "1024x768": {
					if ((Timelapse.getImage().getWidth() > 1024) && (Timelapse.getImage().getHeight() > 768)) {
						panelUp.setRectDimension(1024, 768);
						resolutionHyperlapse.x = 1024;
						resolutionHyperlapse.y = 768;
					} else {
						JOptionPane.showMessageDialog(null, "Select a resolution < at images resolution ", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
					break;

				}
				case "1280x720": {
					if ((Timelapse.getImage().getWidth() > 1080) && (Timelapse.getImage().getHeight() > 720)) {
						panelUp.setRectDimension(1280, 720);
						resolutionHyperlapse.x = 1080;
						resolutionHyperlapse.y = 720;
					} else {
						JOptionPane.showMessageDialog(null, "Select a resolution < at images resolution ", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				}
				case "1280x1024": {
					if ((Timelapse.getImage().getWidth() > 1280) && (Timelapse.getImage().getHeight() > 1024)) {
						panelUp.setRectDimension(1280, 1024);
						resolutionHyperlapse.x = 1280;
						resolutionHyperlapse.y = 1024;
					} else {
						JOptionPane.showMessageDialog(null, "Select a resolution < at images resolution ", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				}
				case "1360x768": {
					if ((Timelapse.getImage().getWidth() > 1360) && (Timelapse.getImage().getHeight() > 768)) {
						panelUp.setRectDimension(1360, 768);
						resolutionHyperlapse.x = 1360;
						resolutionHyperlapse.y = 768;
					} else {
						JOptionPane.showMessageDialog(null, "Select a resolution < at images resolution ", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
					break;

				}
				case "1920x1080": {
					if ((Timelapse.getImage().getWidth() > 1920) && (Timelapse.getImage().getHeight() > 1080)) {
						panelUp.setRectDimension(1920, 1080);
						resolutionHyperlapse.x = 1920;
						resolutionHyperlapse.y = 1080;
					} else {
						JOptionPane.showMessageDialog(null, "Select a resolution < at images resolution ", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				}
				case "2560x1600": {
					if ((Timelapse.getImage().getWidth() > 2560) && (Timelapse.getImage().getHeight() > 1600)) {

						panelUp.setRectDimension(2560, 1600);
						resolutionHyperlapse.x = 2560;
						resolutionHyperlapse.y = 1600;
					} else {
						JOptionPane.showMessageDialog(null, "Select a resolution < at images resolution ", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				}
				case "3840x2160": {
					if ((Timelapse.getImage().getWidth() > 3840) && (Timelapse.getImage().getHeight() > 2160)) {
						resolutionHyperlapse.x = 3840;
						resolutionHyperlapse.y = 2160;
						panelUp.setRectDimension(3840, 2160);
					} else {
						JOptionPane.showMessageDialog(null, "Select a resolution < at images resolution ", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}
					break;
				}
				}
				MyDialog.enabledHyperlapseSelection(true);

			}
		});

		labelResolution = new JLabel("RESOLUTION");
		confirm = new JButton("CONFIRM");
		cancel = new JButton("CANCEL");

		confirm.addActionListener(this);
		cancel.addActionListener(this);

		createPanelDown();
		if (Timelapse.getLookAndFeel() == 1) {
			panelUp.setBackground(Color.DARK_GRAY);
		}
		this.add(panelUp, borderLayout.CENTER);
		this.add(panelDown, borderLayout.SOUTH);

		threadLoadImg = new Thread(new loadImgWorker());
		threadLoadImg.start();

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSource() == confirm) {
			MyDialog.setHyperSelected(true);
			MyDialog.setFlagHyperlapse(true);
			MyDialog.getResolution().setEnabled(false);
			Timelapse.getCreateHyperlapseDialog().dispose();

		}
		if (arg0.getSource() == cancel) {
			MyDialog.setHyperSelected(false);
			MyDialog.setFlagHyperlapse(false);
			Timelapse.getCreateHyperlapseDialog().dispose();
		}

	}

	public void createPanelDown() {
		// panel down with resolution , confirm , cancel
		JPanel panelDownApp = new JPanel();
		JPanel panelLabel = new JPanel();
		BorderLayout borderLayoutDown = new BorderLayout();
		panelDown = new JPanel(borderLayoutDown);
		panelDown.setSize(1200, 200);

		panelDown.setBackground(Color.DARK_GRAY);

		FlowLayout flowLayout = new FlowLayout();
		FlowLayout flowLayoutLabel = new FlowLayout();

		panelDownApp.setLayout(flowLayout);

		panelDownApp.add(labelResolution);
		panelDownApp.add(resolution);
		panelDownApp.add(confirm);
		panelDownApp.add(cancel);

		panelDownApp.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		panelLabel.setLayout(flowLayoutLabel);
		panelLabel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

		if (Timelapse.getLookAndFeel() == 1) {
			panelLabel.setBackground(Color.DARK_GRAY);
			panelDownApp.setBackground(Color.DARK_GRAY);
		}

		panelDown.add(panelDownApp, borderLayoutDown.SOUTH);

		ImageIcon redRect = new ImageIcon("img//redrect.png");
		Image imgConstRed = redRect.getImage();
		Image redRectResize = imgConstRed.getScaledInstance(18, 18, java.awt.Image.SCALE_SMOOTH);
		redRect = new ImageIcon(redRectResize);
		JLabel red = new JLabel("   Start Rectangle           ", redRect, JLabel.CENTER);
		red.setForeground(Color.RED);
		panelLabel.add(red);
		ImageIcon greenRect = new ImageIcon("img//greenrect.png");

		Image imgConstGreen = greenRect.getImage();
		Image greenRectResize = imgConstGreen.getScaledInstance(15, 15, java.awt.Image.SCALE_SMOOTH);
		greenRect = new ImageIcon(greenRectResize);
		JLabel green = new JLabel("   End Rectangle      ", greenRect, JLabel.CENTER);
		green.setForeground(Color.GREEN);
		panelLabel.add(green);

		panelDown.add(panelLabel, borderLayoutDown.CENTER);

	}

	public int getResolutionHyperlapseW() {
		return resolutionHyperlapse.x;

	}

	public int getResolutionHyperlapseH() {
		return resolutionHyperlapse.y;

	}

	// getter

}