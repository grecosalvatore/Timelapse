import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.List;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayer;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import de.javasoft.plaf.synthetica.SyntheticaBlackEyeLookAndFeel;
import de.javasoft.plaf.synthetica.SyntheticaBlueMoonLookAndFeel;
import javax.swing.plaf.LayerUI;

@SuppressWarnings("serial")
/**
 * this class extends JFrame and is the frame of the java application create all
 * the GUI and manage all Listener in the frame it's the principal class the
 * manage all the program.
 *
 */
public class Timelapse extends JFrame {

	private static JPopupMenu popup; // popup with message
	private static MyImagePanel imagePanel; // panel with selected image
	private static MyImage image; // image of the imagepanel
	private static MyImage clickedImage;
	private static MyMenu menu; // menu of the frame
	private static MyDir dir; // current directory
	private static MyTable table; // table with all images
	private static JButton cancel; // canel button
	private static JPanel buttonPanel; // panel with defalut , straightline and
										// ffmpeg button
	private static MyHisto h; // histogram
	private static JSplitPane splitHigh; // split high with the selected image
											// and the setting panel
	private static JSplitPane splitCenter; // split center with the deflickering
											// graph
	private static JSplitPane split; // split with split high , split center and
										// table
	private static JSplitPane splitHisto; // split with rgb and brightness
											// histogram
	private static JPanel RGBPanel; // panel with the rgb histogram
	private static JPanel BrightPanel; // panel with the brightness histogram
	private static JPanel panelGrapich; // panel with the chartpanel of the
										// deflickering graph
	private static SettingPanel panelSettings; // panel settings with buttons
												// and scrolltable
	private static JDialog startDialog; // dialog at the first start of
										// tl_studio for path of ffmpeg
	private static JDialog createVideoDialog; // dialog for the creation of the
												// video
	private static JDialog createHyperlapseDialog; // dialog for the
													// speedeffect
	private static HyperlapseDialog createHyperlapsePanel;
	private static File[] listFile; // list of file in the directory
	private CheckOs os; // operating system
	private static int app; // value in the table
	private static JMenuItem addKey; // "add keyframes" for the right click in
										// the table

	private static JProgressBar openBar; // progess bar of the open

	public static Thread threadImg; // thread to change image
	private static Thread threadOpenDir; // thread when open is clicked
	private static Thread threadWrite; // thread for brightness calculation
	public static Thread threadHisto; // thread for the creation of histogram
	private static boolean stopped; // flag if open is stopped
	private static boolean startEncoding = false; // flag if open is stopped

	private static boolean flagDir; // images are loaded correctly
	private static boolean flagCalc = false; // brightness is calculated and
												// graph is
	private static int laf; // painted
	private static boolean flagEnd; // exposure compensation tags are written
	private static boolean flagDrawRect; // if can draw rect and calc brightness
	private static boolean flagGraph = false;
	private static double[] avgBright; // vector with average brightness of each
										// image
	private static Date[] dateTime; // vector with the date and time of each
									// image
	private static double[] brightDiff; // vector with the difference between
										// the real curve and the edited curve
	private static ArrayList<Integer> keyFrames; // list of selected keyframes
	private static MyGrahpPanel graphicDeflickering; // graph of deflickering
	private static ChartMouseListener chartListener; // chart listener for add
														// keyframes in the
														// chart

	private static ArrayList<String> listerror; // list of problem and error at
												// the start of the program
	private static ArrayList<String> nameerror; // names of the errors
	private static String lafName; // name of the look and feel
	private static boolean firstOpen = true;
	private static boolean flagErrors = false;
	private static boolean flagKeyframes = false;
	private static File fileInformationMassage;
	private static int dontShowMeAgainFirstMessage = 0;
	private static int dontShowMeAgainSecondMessage = 0;
	private static Boolean flagFindImageWithoutError = false;
	private static Boolean flagImageError = false;

	// opens images and reads metadata (with a thread in background)
	/**
	 * this method start opendir with a thread
	 *
	 */
	public static class openDirWorker implements Runnable {
		public openDirWorker() {
		}

		@Override
		public void run() {
			openDir();
		}
	}

	// create the histogram of the selected image (with a thread in background)
	/**
	 * this method start the creation of histogram with a thread
	 */
	public static class createHistoWorker implements Runnable {
		public createHistoWorker() {
		}

		@Override
		public void run() {

			elaborateHisto();

		}
	}

	// processes images (with a thread in background)
	/**
	 * this method start the calculation of the brigh with a thread
	 *
	 */
	public static class imgWorker implements Runnable {
		public imgWorker() {
		}

		@Override
		public void run() {
			elaborateImg();
		}
	}

	// writes exif metadata on the images (with a thread in background)
	/**
	 * this method start the writing of the metadata with a thread
	 */
	public static class writeWorker implements Runnable {
		public writeWorker() {
		}

		@Override
		public void run() {
			writeExif();
		}
	}

	/**
	 * this method set the LookAndFeel of the frame
	 * 
	 * @param n
	 */
	public static void setLookAndFeel(int n) {
		// set the look and feel of the theme
		if (n == 2) {
			try {
				UIManager.setLookAndFeel(new SyntheticaBlueMoonLookAndFeel());
				laf = n;
				lafName = "white";

			} catch (UnsupportedLookAndFeelException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (n == 1) {
			try {
				laf = n;
				lafName = "black";
				UIManager.setLookAndFeel(new SyntheticaBlackEyeLookAndFeel());
			} catch (UnsupportedLookAndFeelException | ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	/**
	 * constructor of Timelapse
	 */
	public Timelapse() {

		os = new CheckOs();
		// search the file config
		File f2 = new File(System.getProperty("user.dir") + "\\config_theme.txt");

		fileInformationMassage = new File(System.getProperty("user.dir") + "\\infoMessage.txt");

		if (f2.exists()) {
			String slaf = this.getFFmpegPath(f2);
			this.laf = Integer.parseInt(slaf);
		} else {
			laf = 1;
			lafName = "black";
		}
		Timelapse.setLookAndFeel(laf);

		if (!fileInformationMassage.exists()) {
			try {
				fileInformationMassage.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			PrintWriter writer;
			try {
				writer = new PrintWriter(System.getProperty("user.dir") + "\\infoMessage.txt", "UTF-8");
				writer.println(0);
				writer.println(0);
				writer.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// create and show the gui
		createAndShowGUI();

		this.setFlagDir(false);

	}

	/**
	 * this method read in the file config the path of ffmpeg
	 * 
	 * @param f
	 * @return
	 */
	private String getFFmpegPath(File f) {
		// return the path of ffmpeg
		String s = null;
		try {
			FileReader fr = new FileReader(f);
			BufferedReader b;
			b = new BufferedReader(fr);
			s = b.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;

	}

	/**
	 * this method create the dialog with the two histogram of the selected
	 * image
	 */
	public static void elaborateHisto() {
		// create the rgb and the brightness histogram
		h = new MyHisto(clickedImage.getImage());
		RGBPanel = h.getRGBHisto();
		BrightPanel = h.getBrightHisto();
		RGBPanel.setVisible(true);
		BrightPanel.setVisible(true);

		splitHisto.setLeftComponent(RGBPanel);
		splitHisto.setRightComponent(BrightPanel);
		splitHisto.setVisible(true);

		JDialog dialogHisto = new JDialog();
		dialogHisto.setSize(1200, 800);
		dialogHisto.add(splitHisto);
		splitHisto.setDividerLocation(dialogHisto.getWidth() / 2);
		splitHisto.setDividerSize(3);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		dialogHisto.setLocation((screenSize.width / 2 - (dialogHisto.getWidth() / 2)),
				(screenSize.height / 2 - (dialogHisto.getHeight() / 2)));
		dialogHisto.setVisible(true);

	}

	/**
	 * this method create the Gui of the frame
	 */
	public void createAndShowGUI() {
		UIManager.put("InternalFrame.activeTitleBackground", new ColorUIResource(Color.black));
		// set the icon of the program
		Image icon = Toolkit.getDefaultToolkit().getImage("img//ic.jpg");
		this.setIconImage(icon);

		// create the table and add the mouse listener
		table = new MyTable();
		table.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mouseClicked(final java.awt.event.MouseEvent evt) { // with
																			// the

				if (SwingUtilities.isRightMouseButton(evt) && Timelapse.isFlagDir()) {
					popup = new JPopupMenu();
					addKey = new JMenuItem("exclude photo");
					popup.add(addKey);
					popup.show(table, evt.getX(), evt.getY());
					addKey.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							// TODO Auto-generated method stub
							Timelapse.nameerror
									.add((String) table.getModel().getValueAt(table.rowAtPoint(evt.getPoint()), 0));
							DefaultTableModel dm = (DefaultTableModel) table.getModel();
							dm.removeRow(table.rowAtPoint(evt.getPoint()));
						}
					});
				}
			}
		});



		// set the layout before the selection of the rect in the image
		Timelapse.graphicDeflickering = null;
		imagePanel = new MyImagePanel();

		image = new MyImage();
		clickedImage = new MyImage();
		menu = new MyMenu();
		dir = new MyDir();

		Timelapse.setFlagDir(false);
		Timelapse.setFlagCalc(false);
		Timelapse.setFlagEnd(false);

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		h = new MyHisto(null);
		RGBPanel = h.getRGBHisto();
		BrightPanel = h.getBrightHisto();
		panelSettings = new SettingPanel();

		splitHisto = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitHisto.setContinuousLayout(true);
		splitHisto.add(RGBPanel);
		splitHisto.add(BrightPanel);

		splitHigh = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitHigh.setOneTouchExpandable(true);
		splitHigh.setContinuousLayout(true);
		splitHigh.add(imagePanel);

		splitHigh.setBackground(Color.DARK_GRAY);
		splitHigh.add(new JScrollPane(table));
		splitHigh.repaint();

		panelGrapich = new JPanel();
		graphicDeflickering = new MyGrahpPanel();

		BorderLayout bl = new BorderLayout();
		panelGrapich.setLayout(bl);

		panelGrapich.add(graphicDeflickering.getPannello(), bl.CENTER);

		if (Timelapse.getLookAndFeel() == 1)
			panelGrapich.setBackground(Color.DARK_GRAY);
		splitCenter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitHigh, panelGrapich);
		splitCenter.setOneTouchExpandable(true);
		splitCenter.setContinuousLayout(true);
		split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, splitHigh, panelGrapich);
		System.out.println("altezza : " + panelGrapich.getHeight());

		split.setOneTouchExpandable(true);
		split.setContinuousLayout(true);
		if (Timelapse.getLookAndFeel() == 1)
			split.setBackground(Color.DARK_GRAY);

		// when the position of splitter divisor changes, selection on the image
		// are deleted!
		split.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				if (arg0.getPropertyName().equals("dividerLocation")) {
					imagePanel.clearRect();
				}
			}
		});
		// same as above
		splitHigh.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent arg0) {
				if (arg0.getPropertyName().equals("dividerLocation")) {
					imagePanel.clearRect();

				}
			}
		});

		// panel with progressbar and botton "cancel"
		buttonPanel = new JPanel();
		BorderLayout lay = new BorderLayout();
		buttonPanel.setLayout(lay);

		// create and show the progress bar for the open
		openBar = new JProgressBar();
		openBar.setVisible(false);
		openBar.setStringPainted(true);

		// create the cancel buttono
		cancel = new JButton("Cancel");
		cancel.setVisible(false);
		cancel.setEnabled(false);

		// add the action listener at the action button
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// the click on cancel stop the thread
				Timelapse.stopThread(0);
				openBar.setString("Canceling...");
			}
		});

		// create the panel with the bar and the cancel button
		buttonPanel.add(openBar, BorderLayout.CENTER);
		buttonPanel.add(cancel, BorderLayout.LINE_END);
		buttonPanel.setVisible(true);

		if (Timelapse.getLookAndFeel() == 1)
			setBackground(Color.DARK_GRAY);

		getContentPane().add(menu.getMenu(), BorderLayout.PAGE_START);
		getContentPane().add(split, BorderLayout.CENTER);
		getContentPane().add(buttonPanel, BorderLayout.PAGE_END);

		if (Timelapse.getLookAndFeel() == 1)
			this.getContentPane().setBackground(Color.DARK_GRAY);

		setSize(screenSize);
		setTitle("Timelapse Studio");
		setVisible(true);
		// extend the frame in all the screen
		setExtendedState(JFrame.MAXIMIZED_BOTH);

		// the splithigh is 1/3 of the screen
		splitHigh.setSize(screenSize);
		splitHisto.setDividerLocation(screenSize.width / 3);
		splitHisto.setDividerSize(0);

		// the split center is 1/3 on the screen
		splitHigh.setDividerLocation(screenSize.width / 2);
		splitCenter.setSize(screenSize);
		splitCenter.setDividerLocation(screenSize.height / 2);
		splitCenter.setDividerSize(0);
		splitHigh.setDividerSize(0);
		split.setDividerSize(0);
		split.setDividerLocation(screenSize.height / 2);

		this.addComponentListener(new ComponentListener() {
			public void componentResized(ComponentEvent arg0) {
				// table resizing
				// within a certain size, table is displayed with a scrollbar
				// when this size is exceeded, table size fits form size
				if (!table.getResizable() && split.getWidth() > table.getWidth()) {
					table.setResizable(true);
					// saves size "limit"
					table.setTableWidthLimit(table.getWidth());
				} else if (split.getWidth() < table.getTableWidthLimit())
					table.setResizable(false);

				imagePanel.clearRect();

			}

			public void componentMoved(ComponentEvent arg0) {
			}

			public void componentShown(ComponentEvent arg0) {
			}

			public void componentHidden(ComponentEvent arg0) {
			}
		});

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * this method start the thread opendir
	 */
	public static void startOpenDir() {

		/*
		 * // when open dir is clicked start the thread opendir
		 * openBar.setMinimum(0); // the bar is visible
		 * openBar.setVisible(true); openBar.setString(
		 * "Scanning errors in photos");
		 */

		threadOpenDir = new Thread(new openDirWorker());
		threadOpenDir.start();

	}

	@SuppressWarnings("deprecation")
	/**
	 * this method read all file and all metadata when is open a new folder
	 */
	public static void openDir() {

		if (flagDrawRect == true) {
			System.out.println("flagdrawrect : true");
			panelGrapich.remove(graphicDeflickering.getPannello());
			graphicDeflickering = new MyGrahpPanel();
			panelGrapich.add(graphicDeflickering.getPannello());
			panelGrapich.repaint();
			split.repaint();

		}

		if (flagKeyframes == true) {
			panelSettings.clearKeyframes();
			flagKeyframes = false;
			menu.setEncodingItem(false);
		}
		/*
		 * if (firstOpen == false){ table = new MyTable(); }
		 */

		Timelapse.listerror = new ArrayList<String>();
		nameerror = new ArrayList<String>();
		try {
			// before write metadata on the table the program check problem in
			// the photo and start error conscole
			if (!Timelapse.checkPhotoResolution()) {
				flagErrors = false;
				System.out.println("errore");

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (false == false) {

			// when a new folder is opened,
			// all the previously calculated data are no longer valid
			setFlagCalc(false);

			// clears the panel
			imagePanel.clearRect();

			// clears the table
			DefaultTableModel dm = (DefaultTableModel) table.getModel();
			while (dm.getRowCount() > 0)
				dm.removeRow(0);

			// loads image preview
			if (dir.getPath() == null)
				return;
			// opens selected directory
			File folder = new File(dir.getPath());
			// lists files in the directory
			listFile = folder.listFiles();

			// sets maximum progressbar value
			openBar.setMaximum(listFile.length);

			// saves the first file
			File firstFile = listFile[0];

			// load the first file without error
			MyImage image = new MyImage();
			// loads the first image without error...
			// if there are error search the first image without error ,
			// otherwise
			// take the first file

			if (Timelapse.getNameError().size() > 0) {
				for (int i = 0; (i < Timelapse.getListFile().length); i++) {
					flagImageError = false;
					for (int j = 0; j < Timelapse.getNameError().size() - 1; j++) {

						if (Timelapse.getNameError().contains(Timelapse.getListFile()[i].getName())) {
							flagImageError = true;
							break;
						}

					}
					if (flagImageError == false) {
						image.load(Timelapse.getListFile()[i].getAbsolutePath());
						Timelapse.setClickedImg(image);
						break;
					}

				}
			} else {
				// if are there there aren't image with errors then take the
				// first file
				System.out.println("no error then first file");
				image.load(firstFile.getAbsolutePath());
				Timelapse.setClickedImg(image);
			}

			imagePanel.setImage(image);
			clickedImage = image;
			Timelapse.image = image;
	

			// ...and paints it on the panel

			// reads metadata from all the images
			int cont = 0;
			dateTime = new Date[listFile.length];
			openBar.setString("Sorting Files...");

			// for each image
			for (File f : listFile) {
				image.readExif(f.getAbsolutePath());

				// writes metadata in the table
				// table.addRow(image.getMetadata());

				// increments progressbar value
				openBar.setValue(cont);

				// gets shot date and time
				dateTime[cont++] = image.getDateTime();

			}
			openBar.setValue(0);
			openBar.setMaximum(listFile.length);
			openBar.setString("EXIF reading in progress...");
			cont = 0;
			for (int i = 0; i < dateTime.length; i++) {
				openBar.setValue(cont);
				Date appDate;
				File appFile;
				// order the photos based on the time of shooting
				for (int j = i + 1; j < dateTime.length; j++) {
					if (dateTime[i].getTime() > dateTime[j].getTime()) {
						appDate = dateTime[i];
						dateTime[i] = dateTime[j];
						dateTime[j] = appDate;
						appFile = listFile[i];
						listFile[i] = listFile[j];
						listFile[j] = appFile;
					}

				}
				// cont++;

			}
			// for each image read the metadata and put in the table
			for (File f : listFile) {
				image.readExif(f.getAbsolutePath());
				table.addRow(image.getMetadata());
				openBar.setValue(cont);
				cont++;
			}
			table.ChangeRow();
			menu.setHistoItem();
			Timelapse.setFlagDrawRect(true);
			Timelapse.setFlagDir(true);
			openBar.setValue(0);
			openBar.setVisible(false);
			System.out.println("n file " + listFile.length);
			graphicDeflickering.setXNumberOfAxis(listFile.length);

			if (fileInformationMassage.exists()) {
				String fileName = "infoMessage.txt";

				// This will reference one line at a time
				String line = null;

				try {
					// FileReader reads text files in the default encoding.
					FileReader fileReader = new FileReader(fileName);

					// Always wrap FileReader in BufferedReader.
					BufferedReader bufferedReader = new BufferedReader(fileReader);
					int c = 0;
					while ((line = bufferedReader.readLine()) != null) {
						if (c == 0) {
							dontShowMeAgainFirstMessage = Integer.parseInt(line);

						} else {
							if (c == 1) {
								dontShowMeAgainSecondMessage = Integer.parseInt(line);
							}
						}
						System.out.println(line);
						c++;
					}

					System.out.println(" primo " + dontShowMeAgainFirstMessage);
					System.out.println(" secondo " + dontShowMeAgainSecondMessage);

					// Always close files.
					bufferedReader.close();
				} catch (FileNotFoundException ex) {
					System.out.println("Unable to open file '" + fileName + "'");
				} catch (IOException ex) {
					System.out.println("Error reading file '" + fileName + "'");
					// Or we could just do this:
					// ex.printStackTrace();
				}
			}
			if (Timelapse.flagErrors == false)
				table.ChangeRow();

		}

		if (dontShowMeAgainFirstMessage == 0) {
			JCheckBox checkbox = new JCheckBox("Do not show this message again.");
			String message = "Draw rect in the image for calc the average bright in the selected area\n\n";
			Object[] params = { message, checkbox };
			JOptionPane.showMessageDialog(null, params, "Information", JOptionPane.INFORMATION_MESSAGE);
			if (checkbox.isSelected()) {
				// write in the file
				PrintWriter writer = null;
				try {
					writer = new PrintWriter(System.getProperty("user.dir") + "\\infoMessage.txt", "UTF-8");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				writer.println(1);
				dontShowMeAgainFirstMessage = 1;
				writer.print(dontShowMeAgainSecondMessage);
				writer.close();

			} else {

			}

			threadOpenDir.stop();
		} else {
			openBar.setValue(0);
			openBar.setVisible(false);
			threadOpenDir.stop();
		}

	}

	/**
	 * this method start the thread for elaborate image
	 */
	public static void startElaborateImg() {

		/*
		 * // start the tread for calc the average brightness of the rect in
		 * each // image openBar.setVisible(true); cancel.setVisible(true);
		 * cancel.setEnabled(true); openBar.setValue(0);
		 */

		threadImg = new Thread(new imgWorker());
		threadImg.start();
	}

	/**
	 * this method elborate the image when the rect is draw and draw the
	 * deflickering grapg in the frame
	 */
	public static void elaborateImg() {
		stopped = false;
		/*
		 * if (Timelapse.graphicDeflickering != null) { firstOpen = false;
		 * panelSettings.clearKeyframes();
		 * 
		 * if (Timelapse.getLookAndFeel() == 1)
		 * panelGrapich.setBackground(Color.DARK_GRAY);
		 * 
		 * 
		 * 
		 * panelSettings.disableButton();
		 * 
		 * }
		 */

		flagCalc = false;

		if (flagGraph == true) {
			if (flagKeyframes == true) {
				panelSettings.clearKeyframes();
				flagKeyframes = false;
				menu.setEncodingItem(false);
			}
			graphicDeflickering.removeRealSeries();

		}

		flagGraph = true;

		Timelapse.setFlagDrawRect(false);
		// calculates brightness for each image in the directory
		File folder = new File(dir.getPath());

		int numImg = 0;
		for (@SuppressWarnings("unused")
		File f : /* folder. */listFile)
			numImg++;

		int cont = 0;
		avgBright = new double[numImg];

		for (int i = 0; i < numImg; i++) {
			avgBright[i] = 0;
		}

		MyImage img = new MyImage();
		// prova
		int area;

		// iterate all files in the selected directory
		for (File file : listFile) {
			// loads in memory only the selected part of the image
			// saves a lot of memory!!
			if (Timelapse.getNameError().contains(file.getName()))
				continue;
			ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG").next();
			ImageInputStream iis = null;
			try {
				iis = ImageIO.createImageInputStream(new FileInputStream(file.getAbsoluteFile()));
			} catch (IOException e) {
				e.printStackTrace();
			}
			reader.setInput(iis);
			ImageReadParam param = reader.getDefaultReadParam();
			area = imagePanel.getRealW() * imagePanel.getRealH();
			if (area != 0) {
				// if area is greater then zero , draw the rect in the image
				param.setSourceRegion(new Rectangle(imagePanel.getRealX(), imagePanel.getRealY(), imagePanel.getRealW(),
						imagePanel.getRealH()));
				BufferedImage detail = null;
				try {
					detail = reader.read(0, param);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				img.setImage(detail);

				// calculates average brightness of the selection
				try {
					img.pixelBright();
					avgBright[cont] = img.getBright();

					System.out.println("x : " + cont + " avg : " + avgBright[cont]);
					graphicDeflickering.addRealCurve(cont, avgBright[cont]);

					// inserts the just calculated value in the table (value
					// between
					// 0 and 1)

					// table.getModel()
					// .setValueAt((double) Math.round(
					// (avgBright[cont] /* / 255) * 100) / 100 */)), cont,
					// table.getColumnFromName("Brightness"));

				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}

				openBar.setString(file.getName());
				openBar.setValue(cont);

				cont++;
			} else {
				// the are must be greater then zero
				JOptionPane.showMessageDialog(null, "Error! You must select a region with area >0!", "Error!",
						JOptionPane.ERROR_MESSAGE);
				stopped = true;
				Timelapse.setFlagDir(true);
				Timelapse.setFlagCalc(true);
				Timelapse.setFlagDrawRect(true);
				openBar.setString("Processing completed!");
				openBar.setVisible(false);
				cancel.setVisible(false);

				stopThread(1);
				break;

			}

		}

		if (!stopped) {
			if (firstOpen == false) {

			}

			// put the graph in the split center

			// create the arrylist of keyframes
			keyFrames = new ArrayList<Integer>();
			panelSettings.setGraphButton();
			Timelapse.setFlagDir(true);
			Timelapse.setFlagCalc(true);
			Timelapse.setFlagDrawRect(true);
			openBar.setString("Processing completed!");
			openBar.setVisible(false);
			cancel.setVisible(false);

			// create and draw the graph in the center

			/*
			 * JOptionPane.showMessageDialog(null,
			 * "Select Keyframes with a double click in the chart", "Info",
			 * JOptionPane.INFORMATION_MESSAGE);
			 */

			if (dontShowMeAgainSecondMessage == 0) {
				JCheckBox checkbox = new JCheckBox("Do not show this message again.");
				String message = "Select Keyframes with a double click in the chart\n\n";
				Object[] params = { message, checkbox };
				JOptionPane.showMessageDialog(null, params, "Information", JOptionPane.INFORMATION_MESSAGE);
				if (checkbox.isSelected()) {
					// write in the file
					PrintWriter writer = null;
					try {
						writer = new PrintWriter(System.getProperty("user.dir") + "\\infoMessage.txt", "UTF-8");
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					writer.println(dontShowMeAgainFirstMessage);
					writer.print(1);
					dontShowMeAgainSecondMessage = 1;
					writer.close();
				} else {

				}
			}

			flagCalc = true;

			stopThread(1);
		}

	}

	// stop all the threads in background
	// when user hits "cancel" button to stop the execution of brightness
	// calculation
	@SuppressWarnings("deprecation")
	/**
	 * this method stop the thread of index type
	 * 
	 * @param type
	 *            index of thread to stop
	 */
	public static void stopThread(int type) {

		if (threadImg.isAlive()) {
			if (type == 0) {
				Timelapse.setFlagCalc(false);

				stopped = true;

			}
			threadImg.stop();
		} else if (threadWrite != null && threadWrite.isAlive()) {
			threadWrite.stop();
		}
		Timelapse.setFlagDrawRect(true);
		Timelapse.setFlagDir(true);
		openBar.setVisible(false);
		cancel.setVisible(false);
	}

	/**
	 * this method start the thread for read metadata
	 */
	public static void startWriteExif() {
		// start thread for write exif data
		openBar.setValue(0);
		openBar.setString("Preparing to write EXIF...");
		openBar.setVisible(true);
		cancel.setVisible(true);

		threadWrite = new Thread(new writeWorker());
		threadWrite.start();
	}

	/**
	 * this method write the metadata in the table
	 */
	public static void writeExif() {
		int i = 0;
		boolean first = true;

		File path = new File(dir.getPath());
		openBar.setMaximum(path.listFiles().length);

		MyImage img = new MyImage();
		for (File f : path.listFiles()) {
			img.setName(f.getAbsolutePath());
			if (!first) // Exif data of the first image don't have to be
						// modified
			{
				openBar.setString("Writing EXIF: " + f.getName());
				openBar.setValue(i);

				i++;
			}
			first = false;
		}

		openBar.setString("Operation completed successfully!");
		openBar.setVisible(false);
		cancel.setVisible(false);
	}

	/**
	 * this method read the metadata of images
	 */
	public static void calcAndWriteExif() {
		// already processed images and painted graph
		if (Timelapse.isFlagCalc()) {
			startWriteExif();
		} else {
			if (!isFlagDir()) {
				JOptionPane.showMessageDialog(null, "You have to select the directory\nwith the image to be processed",
						"Error!", JOptionPane.ERROR_MESSAGE);
			} else if (!imagePanel.isSel()) {
				JOptionPane.showMessageDialog(null,
						"You have to select the part of image\nof which calculate brightness", "Error!",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * this method delete all ekyframes from the graph and the keyframes list
	 */
	public static void deleteKeyFrames() {
		// delete all keyframes
		graphicDeflickering.clearKeyFrames();// delete keyframes from graph
		panelGrapich.repaint();
		// delete keyframes from the list
		for (int i = 0; i < keyFrames.size(); i++) {
			keyFrames.remove(i);
		}
		keyFrames = new ArrayList<Integer>();
	}

	/**
	 * this method delete the duplicate from keyframes arraylist
	 */
	public static void removeDuplicate() {
		// remove duplicate from the list of keyframes
		java.util.Set setPmpListArticle = new HashSet(keyFrames);
		ArrayList newKeyFrames = new ArrayList(setPmpListArticle);
		keyFrames = newKeyFrames;
	}

	/**
	 * this method check if the resolution of all the images in the folder is
	 * the same
	 * 
	 * @return boolean true if all images have the same resolution
	 * @throws IOException
	 */
	private static boolean checkPhotoResolution() throws IOException {
		// check the resolution of the photo
		int ref = Timelapse.getRefvalue();
		int app;
		boolean status = true;
		File folder = new File(dir.getPath());
		listFile = folder.listFiles();
		int cont = 0;
		openBar.setMaximum(listFile.length);
		openBar.setValue(0);
		String s;
		for (File file : listFile) {

			SimpleImageInfo imageInfo = new SimpleImageInfo(file);
			app = imageInfo.getHeight() * imageInfo.getHeight();
			if (app != ref) {
				status = false;

				s = file.getName();
				nameerror.add(s);
				listerror.add("Different risolution");
			}
			openBar.setValue(++cont);
		}

		return status;
	}

	/**
	 * this method check the value of the resolution more frequent in all images
	 * 
	 * @return int resolution more frequent
	 * @throws IOException
	 */
	private static int getRefvalue() throws IOException {
		// check the value of resolution more frequent
		File folder = new File(dir.getPath());
		listFile = folder.listFiles();
		ArrayList<Integer> size1 = new ArrayList();
		ArrayList<Integer> pos = new ArrayList();
		int rif = 0;
		int c = 0;
		for (File file : listFile) {
			SimpleImageInfo imageInfo = new SimpleImageInfo(file);
			rif = imageInfo.getHeight() * imageInfo.getHeight();
			if (c == 0) {
				size1.add(rif);
				pos.add(1);
			}
			if (size1.contains(rif)) {
				pos.set(size1.indexOf(rif), pos.get(size1.indexOf(rif)) + 1);
			} else {
				size1.add(rif);
				pos.add(1);
			}
			c++;
		}
		int max = 0;
		int p = 0;
		max = Collections.max(pos);

		return size1.get(pos.indexOf(max));

	}

	/**
	 * this method create the dialog for the creation of the video
	 */
	public static void setCreateVideoDialog() {
		// create the dialog for the creation of the video

		System.out.println("image selected : " + clickedImage.getWidth());
		MyDialog createVideoPanel = new MyDialog();
		createVideoDialog = new JDialog();
		createVideoDialog.setTitle("Video Encoding Settings");
		createVideoDialog.setModal(true);
		createVideoPanel.setVisible(true);
		createVideoDialog.setSize(1000, 850);
		// place the dialog centered on the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		createVideoDialog.setLocation((screenSize.width / 2 - (createVideoDialog.getWidth() / 2)),
				(screenSize.height / 2 - (createVideoDialog.getHeight() / 2)));
		createVideoDialog.add(createVideoPanel);
		createVideoDialog.setVisible(true);

		createVideoDialog.setResizable(false);
	}

	/**
	 * this method create the dialog for speedeffect
	 */
	public static void speedEffect() {
		// create the dialog for the speedeffect
		createHyperlapsePanel = new HyperlapseDialog();
		createHyperlapseDialog = new JDialog();
		createHyperlapseDialog.setTitle("HYPERLAPSE");
		createHyperlapseDialog.setModal(true);
		createHyperlapsePanel.setVisible(true);

		createHyperlapseDialog.setSize(1200, 800);
		// place the dialog centered on the screen
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		createHyperlapseDialog.setLocation((screenSize.width / 2 - (createHyperlapseDialog.getWidth() / 2)),
				(screenSize.height / 2 - (createHyperlapseDialog.getHeight() / 2)));
		createHyperlapseDialog.add(createHyperlapsePanel);
		createHyperlapseDialog.setVisible(true);
		createHyperlapseDialog.setResizable(false);

	}

	// setter
	/**
	 * this method set the flag if is end the calc of the bright
	 * 
	 * @param flagCalc
	 *            boolean
	 */
	public static void setFlagCalc(boolean flagCalc) {
		Timelapse.flagCalc = flagCalc;
	}

	/**
	 * this method set the flag if the directory of images is selected
	 * 
	 * @param flagDir
	 */
	public static void setFlagDir(boolean flagDir) {
		Timelapse.flagDir = flagDir;
	}

	/**
	 * this method set the flag if is end the open of all images
	 * 
	 * @param flagEnd
	 *            boolean
	 */
	public static void setFlagEnd(boolean flagEnd) {
		Timelapse.flagEnd = flagEnd;
	}

	/**
	 * this method set the clicked image
	 * 
	 * @param tmp
	 *            MyImage image clicked
	 */
	public static void setClickedImg(MyImage tmp) {
		clickedImage = tmp;
	}

	/**
	 * this method set the vector of bright compensation
	 * 
	 * @param tmp
	 *            double[] with the bright compensation
	 */
	public static void setBrightDiff(double[] tmp) {
		brightDiff = tmp;
	}

	/**
	 * this method set the flag if is or isn't draw the rect
	 * 
	 * @param flag
	 *            true if the rect is draw . false if the rect isn't draw
	 */
	public static void setFlagDrawRect(Boolean flag) {
		Timelapse.flagDrawRect = flag;
	}

	// getter
	/**
	 * this method return the graph panel of the frame
	 * 
	 * @return MyGraphPanel graphiccDeflicjering
	 */
	public static MyGrahpPanel getGraphicDeflickering() {
		return graphicDeflickering;
	}

	/**
	 * this method return true if the rect is draw
	 * 
	 * @return boolean flagDrawRect
	 */
	public static boolean getFlagDrawRect() {
		return flagDrawRect;
	}

	/**
	 * this method return the lookeandfeel number
	 * 
	 * @return int laf
	 */
	public static int getLookAndFeel() {
		return laf;
	}

	/**
	 * this method return the name of the look and feel
	 * 
	 * @return String lafname
	 */
	public static String getLafName() {
		return lafName;
	}

	/**
	 * this method return the settinpanel in the frame
	 * 
	 * @return SettingPanel panelSettings
	 */
	public static SettingPanel getSettingPanel() {
		return panelSettings;
	}

	/**
	 * this method return the list of file in the folder
	 * 
	 * @return File[] listFile
	 */
	public static File[] getListFile() {
		return listFile;
	}

	/**
	 * this method return the array list of jeyframes
	 * 
	 * @return ArrayList<Integer> keyFrames
	 */
	public static ArrayList<Integer> getKeyFrames() {
		return keyFrames;
	}

	/**
	 * this method create the dialog for the creation of the video
	 * 
	 * @return JDialgo createVideoDialog
	 */
	public static JDialog getCreateVideoDialog() {
		return createVideoDialog;
	}

	/**
	 * this method return the image clicked in the table
	 * 
	 * @return MyIMage clickedImage
	 */
	public static MyImage getClickedImg() {
		return clickedImage;
	}

	/**
	 * this method return the progressbar
	 * 
	 * @return JProgressBar openBar
	 */
	public static JProgressBar getBar() {
		return Timelapse.openBar;
	}

	/**
	 * this method return the double vector of brightness
	 * 
	 * @return double[] avgBright
	 */
	public static double[] getBrightList() {
		return avgBright;
	}

	/**
	 * this method return if the calc of the brightness is true or not
	 * 
	 * @return boolean flagCalc
	 */
	public static boolean getFlagCalc() {
		return flagCalc;
	}

	/**
	 * this method returb the vector of double with the bright compensation
	 * 
	 * @return double[] brighDiff
	 */
	public static double[] getBrightDiff() {
		return brightDiff;
	}

	/**
	 * this method return the rime of the images was taken
	 * 
	 * @return Date[] dateTime
	 */
	public static Date[] getTimeList() {
		return dateTime;
	}

	/**
	 * this method return the imagepanel of the frame
	 * 
	 * @return MyImagePanel imagepanel
	 */
	public static MyImagePanel getImagePanel() {
		return Timelapse.imagePanel;
	}

	/**
	 * this method return the selected image
	 * 
	 * @return MyImage image
	 */
	public static MyImage getImage() {
		return Timelapse.image;
	}

	/**
	 * this method return the menu of the frame
	 * 
	 * @return MyMenu menu
	 */
	public static MyMenu getMenu() {
		return Timelapse.menu;
	}

	/**
	 * this method return the directory with the images
	 * 
	 * @return MyDir dir
	 */
	public static MyDir getDir() {
		return Timelapse.dir;
	}

	/**
	 * this method return the table with all images
	 * 
	 * @return MyTable table
	 */
	public static MyTable getTable() {
		return Timelapse.table;
	}

	// flag getter
	/**
	 * this method return true if are read all image in the folder
	 * 
	 * @return boolean flagEnd
	 */
	public static boolean isFlagEnd() {
		return flagEnd;
	}

	/**
	 * this method return true if is selected a directory with the images
	 * 
	 * @return boolean flagddir
	 */
	public static boolean isFlagDir() {
		return flagDir;
	}

	public static void setStartEncoding(Boolean flag) {
		startEncoding = flag;
	}

	/**
	 * this method return true if is calculated the bright compensation
	 * 
	 * @return boolean flagcalc
	 */
	public static boolean isFlagCalc() {
		return flagCalc;
	}

	public static HyperlapseDialog getHyperlapseDialog() {
		return createHyperlapsePanel;
	}

	public static JDialog getCreateHyperlapseDialog() {
		return createHyperlapseDialog;
	}

	public static void setFlagErrors(boolean flag) {
		flagErrors = flag;
	}

	public static boolean getFlagErrors() {
		return Timelapse.flagErrors;
	}

	public static void setFlagKeyframes(boolean flag) {
		flagKeyframes = flag;
	}

	public static ArrayList<String> getNameError() {
		return Timelapse.nameerror;
	}

	public static Boolean getStartEncoding() {
		return startEncoding;
	}

}