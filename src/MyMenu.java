import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

@SuppressWarnings("serial")
/**
 * this class create and manages all method of the menuBar
 * 
 */
public class MyMenu {
	private static JMenuBar menuBar; // menubar with the menu : file , about ,
										
	private static JMenu menuFile; // menu with the menuitem open,histogram ,
									// close and the menu properties
	private static JMenu menuHelp; // menu with about and instruction
	private static JMenuItem encodingItem; // menu to start the creation of the video
	private static JMenu menuHyperlapse; // menu to start the panel for the
											// speedeffect
	private static JMenuItem histoItem; // menuitem to start the creation of the
										// histogram
	private static JMenu propertiesMenu; // menu with the menuitem settings and
											// theme
	private static JMenu themeMenu; // menuitem with two radiobutton dark and
									// light that change the theme
	private static JDialog createDialog; // Dialog for changing the path of
											// ffmpeg

	/**
	 * constructor of the class MyMenu create the GUI of the menu with all
	 * menuItem and put the listeners
	 */
	MyMenu() {
		// constructor of mymenu
		JMenuItem menuItem;

		// create the menubar
		menuBar = new JMenuBar();
		// create the menufile
		menuFile = new JMenu("File");
		menuBar.add(menuFile);
		// create the menulistener for the menu
		ActionListener menuListener = new MenuListener();
		// load the icon and resize the icon for the menu
		ImageIcon iconOpen = new ImageIcon("img//open.png");
		Image imgOpen = iconOpen.getImage();
		Image newimgOpen = imgOpen.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		ImageIcon iconOpenResize = new ImageIcon(newimgOpen);
		menuItem = new JMenuItem("Open", iconOpenResize);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
		menuItem.addActionListener(menuListener);
		
		iconOpen = new ImageIcon("img//video.png");
		imgOpen = iconOpen.getImage();
		newimgOpen = imgOpen.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		iconOpenResize = new ImageIcon(newimgOpen);
		
		encodingItem =  new JMenuItem("Encoding", iconOpenResize);
		encodingItem.setEnabled(false);
		encodingItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		encodingItem.addActionListener(menuListener);
		
		// add the menu item open on the filemenu
		menuFile.add(menuItem);
		menuFile.add(encodingItem);
		// load the icon and resize the icon for the menu
		ImageIcon iconSetting = new ImageIcon("img//setting.png");
		Image imgSetting = iconSetting.getImage();
		Image newimgSetting = imgSetting.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		ImageIcon iconSettingResize = new ImageIcon(newimgSetting);
		// create the menu proprietes and add the menuitem settings and the menu
		// with the two radiobutton
		propertiesMenu = new JMenu("Properties");
		themeMenu = new JMenu("Theme");
		JRadioButtonMenuItem light = new JRadioButtonMenuItem("Light");
		JRadioButtonMenuItem dark = new JRadioButtonMenuItem("Dark");
		if (Timelapse.getLafName().equals("white"))
			light.setSelected(true);
		else
			dark.setSelected(true);
		ButtonGroup myGroup = new ButtonGroup();
		myGroup.add(dark);
		myGroup.add(light);
		themeMenu.add(dark);
		themeMenu.add(light);
		dark.addActionListener(new DarkActionListener());
		light.addActionListener(new LightActionListener());
		menuItem = new JMenuItem("Settings", iconSettingResize);
		menuItem.addActionListener(menuListener);

		// propertiesMenu.add(menuItem);
		propertiesMenu.add(themeMenu);
		menuFile.add(propertiesMenu);
		// load the icon and resize the icon for the menu
		ImageIcon iconHisto = new ImageIcon("img//rgbh.png");
		Image imgHisto = iconHisto.getImage();
		Image newimgHisto = imgHisto.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		ImageIcon iconHistoResize = new ImageIcon(newimgHisto);
		// create and add the menuitem histogram
		histoItem = new JMenuItem("Histogram", iconHistoResize);
		histoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
		histoItem.addActionListener(menuListener);
		histoItem.setEnabled(false);
		menuFile.add(histoItem);
		menuFile.addSeparator();
		// load the icon and resize the icon for the menu
		ImageIcon iconClose = new ImageIcon("img//close.png");
		Image imgClose = iconClose.getImage();
		Image newimgClose = imgClose.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		ImageIcon iconCloseResize = new ImageIcon(newimgClose);
		// create and add the menuitem close
		menuItem = new JMenuItem("Exit", iconCloseResize);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		menuItem.addActionListener(menuListener);
		menuFile.add(menuItem);

		// create the menu help and add on the menubar
		menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);
		// load the icon and resize the icon for the menu
		ImageIcon iconAbout = new ImageIcon("img//about.png");
		Image imgAbout = iconAbout.getImage();
		Image newimgAbout = imgAbout.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		ImageIcon iconAboutResize = new ImageIcon(newimgAbout);
		// create the menuitem about
		menuItem = new JMenuItem("About", iconAboutResize);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.ALT_MASK));
		menuItem.addActionListener(menuListener);
		menuHelp.add(menuItem);
		// load the icon and resize the icon for the menu
		ImageIcon iconInstruction = new ImageIcon("img//punto_interrogativo.png");
		Image imgInstruction = iconInstruction.getImage();
		Image newimgInstruction = imgInstruction.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
		ImageIcon iconInstructionResize = new ImageIcon(newimgInstruction);
		// create the menuitem instruction
		menuItem = new JMenuItem("Instruction", iconInstructionResize);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, ActionEvent.ALT_MASK));
		menuItem.addActionListener(menuListener);
		menuHelp.add(menuItem);

	
	}

	/**
	 * method for the action of the dark theme this method change the theme in
	 * dark
	 */
	private class DarkActionListener implements ActionListener {
		// if dark is clicked change the lookandfeel and all colors
		public void actionPerformed(ActionEvent e) {
			PrintWriter writer;
			try {
				File f = new File(System.getProperty("user.dir") + "\\config_theme.txt", "UTF-8");
				if (f.exists())
					f.delete();
				writer = new PrintWriter(System.getProperty("user.dir") + "\\config_theme.txt", "UTF-8");
				writer.println(1);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			JOptionPane.showMessageDialog(null, "Theme is changed! please restart the program", "Theme chaged",
					JOptionPane.INFORMATION_MESSAGE);

		}
	}

	/**
	 * method for the action of the light theme this method change the theme in
	 * light
	 */
	private class LightActionListener implements ActionListener {
		// if light is clicked change the lookandfeel and all colors
		public void actionPerformed(ActionEvent e) {
			PrintWriter writer;
			File f = new File(System.getProperty("user.dir") + "\\config_theme.txt", "UTF-8");
			if (f.exists())
				f.delete();
			try {
				writer = new PrintWriter(System.getProperty("user.dir") + "\\config_theme.txt", "UTF-8");
				writer.println(2);
				writer.close();
			} catch (FileNotFoundException | UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			JOptionPane.showMessageDialog(null, "Theme is changed! please restart the program", "Theme chaged",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * method with the action if the menuItem open is clicked this method start
	 * a chooser with the folder that contains the images
	 */
	private void openClicked() {
		MyDir dir = Timelapse.getDir();

		switch (dir.choose()) {
		case 0: // ok
		{
			Timelapse.startOpenDir();
			break;
		}
		case 1: // there are files that are not images
		{
			JOptionPane.showMessageDialog(null,
					"In the selected directory: \"" + dir.getPath() + "\"\nthere MUST be only images!", "Error!",
					JOptionPane.ERROR_MESSAGE);
			break;
		}
		case 2: // images have different resolutions
		{
			JOptionPane.showMessageDialog(null, "Images MUST have the same resolution!", "Error!",
					JOptionPane.ERROR_MESSAGE);
			break;
		}
		default: {
			// do nothing
		}
		}
	}

	/**
	 * method with the action if the menuItem about is clicked this method start
	 * a information massage about the info of timelapse studio
	 */
	private static void aboutClicked() {
		// start the message of about
		ImageIcon iconAbout = new ImageIcon("img//about.png");
		Image imgAbout = iconAbout.getImage();
		Image newimgAbout = imgAbout.getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
		ImageIcon iconAboutResize = new ImageIcon(newimgAbout);
		JOptionPane.showMessageDialog(null, "Timelapse Studio v1.1", "About", JOptionPane.INFORMATION_MESSAGE,
				iconAboutResize);
	}

	// waits for menu items to be clicked
	/**
	 * method with the action for close , histogram , open , about and
	 * instruction of the menu
	 */
	private class MenuListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {

			if (e.getActionCommand().equals("Exit")) {
				System.exit(-1);
			} else {

				if (e.getActionCommand().equals("Histogram")) {
					Timelapse.elaborateHisto();
				} else {
					if (e.getActionCommand().equals("Open")) {
						openClicked();
					} else {
						if (e.getActionCommand().equals("About")) {

							aboutClicked();

						} else {
							if ((e.getActionCommand().equals("Instruction"))) {
								instructionClicked();
							}else{
								if ((e.getActionCommand().equals("Encoding"))){
									Timelapse.setCreateVideoDialog();
								}
							}
						}
					}
				}
			}
		}
	}

	/**
	 * method with the action if the menuItem instruction is clicked this method
	 * oopen the folder with the instruction of the program Timelapse Studio
	 */
	public static void instructionClicked() {
		// open the dir with the instruction of Timelapse_Studio
		CheckOs osType = new CheckOs();
		if (osType.isWindows()) {
			// command in windows
			File f = new File("instruction//istruzioni.rtf");
			ProcessBuilder p = new ProcessBuilder("explorer.exe", f.getParentFile().getAbsolutePath());
			try {
				p.start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			if (osType.isUnix()) {
				// command in unix
				File f = new File("instruction//istruzioni.rtf");
				try {
					Desktop.getDesktop().open(f.getParentFile());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	// setter
	/**
	 * this method enabled if flag is true and disable if flag is false the
	 * menuItem MenuVideo in the menuBar
	 * 
	 * @param flag
	 *            enabled or disabled the menuItem menuVideo
	 */

	/*
	 * public static void setMenuVideo(Boolean flag) {
	 * menuVideo.setEnabled(flag); }
	 */
	/**
	 * this method enabled if flag is true and disable if flag is false the
	 * menuItem SpeedEffect in the menuBar
	 * 
	 * @param flag
	 *            enabled or disabled the menuItem menuenabled or disabled the
	 *            menuItem menuVideoVideo
	 */
	public static void setHyperlapse(Boolean flag) {
		menuHyperlapse.setEnabled(flag);
	}

	/**
	 * this method enabled the menuItem for the creation of the histogram
	 */
	public static void setHistoItem() {
		histoItem.setEnabled(true);
	}
	
	
	public static void setEncodingItem(Boolean flag) {
		encodingItem.setEnabled(flag);
	}

	// getter
	/**
	 * return the menuBar created in the constructor
	 * 
	 * @return JMenuBar menuBar is the menuBar of the frame
	 */
	public static JMenuBar getMenu() {
		return menuBar;
	}

	/**
	 * return the dialog with the settings
	 * 
	 * @return JDialog createDialog
	 */
	public static JDialog getJDialogSetting() {
		return MyMenu.createDialog;
	}

}
