import java.awt.*;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.swing.*;
import javax.swing.SpringLayout.Constraints;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

/**
 * this class extends JPanel and implements ActionListener and create the panel
 * of the dialog for the creation of the video
 * 
 * JLabel dest label with the destination of the video JButton addDest button
 * for add a destination path JLabel sound label with the soundtrack JLabel
 * title label with the title JLabel statusFrame label with the status of the
 * frame JButton addsound button for add a soundtrack JButton getvideo button to
 * start the creation of the video JButton finish button to exit of the dialog
 * JRadioButton check enabled the soundtrack JPanel selectpanel panel with the
 * settings of the video JProgressBar bar with the progress of the editing of
 * the image and the creation of the video JLabel status label with the status
 * of the frame Boolean flagdest flag if is set the destination path Boolean
 * flagsound flag if is set the path of the soundtrack Thread t thread for edit
 * the image Boolean flagspeedeffect flag if speedeffect is clicked before MyDir
 * d chooser path of the destination MyDir s chooser path of the soundtrack int
 * iframe index of current frame Boolean flagfinish if is finish the creation of
 * the video JTextField nameout; JComboBox format with the format of the video
 * JComboBox framerate with the framerate of the video JComboBox resolution with
 * the resolution of the video String[] typeformat string of supported format
 * String[] framerateformat string of supported framerate String[]
 * typeresolution string of supported resolution JLabel LabelexstimateDurate
 * with the exstimate durate of the video JLabel LabelsoundDurate with the
 * durate of the soundtrack CheckOs osType operating system of the machine
 * JTextArea stderr standard error of ffmpeg JScrollPane jsp with the stderr of
 * ffmpeg
 */
public class MyDialog extends JPanel implements ActionListener {
	private JLabel dest; // label with the path destination
	private static JButton addDest; // button tu choose the path destination
	private JLabel sound; // label with the soundtrack path
	private JLabel title; // label with the title
	private static JLabel statusframe; // label with the progress frame create
	private JButton addsound; // button for add a soundtrack
	private static JButton getvideo; // button to create video
	private static JButton finish; // button for exit on the dialog
	private static JButton hyperlapseButton;
	private static JRadioButton check;// if is include the soundtrack
	static String s1 = "Please select a destination path...";
	static String s2 = "No soundtrack selected...";
	private JPanel selectpanel; // penel with the settings of create video
	public static JProgressBar bar; // progress bar of the creation
	private static JLabel status; //
	private boolean flagdest = false; // if is selected the destination
	private boolean flagsound; // if is selected the soundtrack
	private static Thread t; // thread that create the video
	private static boolean flagHyperlapse = false; // if before was selected
													// the speedeffect
	static MyDir d = new MyDir(); // chooser path of the destination
	static MyDir s = new MyDir(); // chooser path of the soundtrack
	static boolean flagstatus; //
	static int iframe; // number of created frame
	static boolean flagfinish; // flag if is finish the creation of the video
	private static JTextField nameOut;
	private static JTextField destinationPath;
	private static JComboBox format; // combobox of the format
	private static JComboBox framerate; // combobox of the framerate
	private static JComboBox resolution; // combobox of the resolution
	private String[] typeformat = { ".mp4", ".avi", ".mov" }; // supported
																// formats
	private String[] framerateformat = { "24", "25", "30", "60", "120" }; // supported
	// framerates
	private static JSlider quality;
	private JLabel LabelexstimateDurate; // label with the exstimate duration of
											// the video
	private JLabel LabelsoundDurate; // label with the durate of the sound
	private static CheckOs osType; // operating system
	private String[] typeresolution = { "auto", "640x480", "720x576", "800x600", "1024x768", "1280x720", "1280x1024",
			"1360x768", "1920x1080", "2560x1600", "3840x2160" }; // suppported
																	// resolution
	private static JTextArea stderr;
	private JScrollPane jsp;
	private JPanel panel;
	private JPanel panel2;
	private DefaultCaret caret;

	private static JRadioButton hyperlapseSelection;
	private JLabel hyper;
	private boolean hyperlapseFlag = false;
	private JPanel p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12, p13, p14, p15;

	/**
	 * this class implements Runnable start the thread for the creation of the
	 * video
	 */
	public static class videoWorker implements Runnable {
		/**
		 * this method start the thread for the creation of the video in the
		 * path destination as parameter
		 * 
		 * @param d
		 *            String with the destination path of the video
		 */
		public videoWorker(MyDir d) {
		}

		@Override
		public void run() {
			flagstatus = true;
			MyDialog.statusframe.setVisible(true);
			MyDialog.bar.setVisible(true);
			MyDialog.bar.setValue(0);
			status.setVisible(true);

			// CreateVideo v = new CreateVideo(Timelapse.getDir().getPath());
			CreateVideoXuggler v = new CreateVideoXuggler(Timelapse.getDir().getPath());

			// status.setText("Creating frames...");
			// v.printFotogramWin(d.getPath());
			// status.setText("FFmpeg video encoding...");

			status.setText("Video Encoding");
			if (!MyDialog.nameOut.getText().equals("")) {
				v.setnameout(MyDialog.nameOut.getText());
			}
			if (!(String.valueOf(resolution.getSelectedItem()) == "auto"))
				v.setResolution(String.valueOf(resolution.getSelectedItem()));
			v.setformat(String.valueOf(format.getSelectedItem()));
			// v.setfraamerate(String.valueOf(framerate.getSelectedItem()));
			v.setfraamerate(Integer.parseInt(String.valueOf(framerate.getSelectedItem())));

			MyDialog.bar.setValue(0);
			try {
				System.out.println(d.getPath());
				if (d.getPath() == null) {
					d.setpath(Timelapse.getDir().getPath());
					v.getVideo(Timelapse.getDir().getPath());
				} else {
					v.getVideo(d.getPath());
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			MyDialog.bar.setValue(0);
			// status.setText("Deleting frames....");
			// v.deleteFotogram(d.getPath());
			MyDialog.bar.setVisible(false);

			if (flagstatus) {

				status.setText("Process complete!");
				if (osType.isWindows()) {
					ProcessBuilder p = new ProcessBuilder("explorer.exe", d.getPath());
					try {
						p.start();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				if (osType.isUnix()) {
					try {
						Desktop.getDesktop().open(new File(d.getPath()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
				MyDialog.finish.setEnabled(true);
				statusframe.setVisible(false);
			} else
				status.setText("Critical error , ffmpeg not found!");
			MyDialog.finish.setEnabled(true);
			statusframe.setVisible(false);
		}

	}

	/**
	 * this class implements Runnable start the thread for the creation of the
	 * video with the soundtrack
	 */
	public static class videoWorkerSound implements Runnable {
		/**
		 * this method start the thread for the creation of the video with the
		 * soundtrack in the path destination as parameter
		 * 
		 * @param d
		 *            String with the destination path of the video
		 */
		public videoWorkerSound(MyDir d) {
		}

		@Override
		public void run() {
		}
	}

	/**
	 * contructor of MyDialog that create the GUI of the dialog
	 */
	public MyDialog() {
		// GUI of the creation video dialog
		super();
		setLocation(400, 100);
		setSize(500, 500);
		setVisible(true);
		stderr = new JTextArea();
		stderr.setEditable(false);

		jsp = new JScrollPane(stderr);
		jsp.setPreferredSize(new Dimension(50, 50));
		bar = new JProgressBar();
		bar.setVisible(false);
		flagdest = false;
		flagsound = false;
		nameOut = new JTextField("", 10);
		nameOut.setEditable(true);

		destinationPath = new JTextField(Timelapse.getDir().getPath(), 10);
		destinationPath.setEditable(true);

		caret = (DefaultCaret) stderr.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		this.finish = new JButton("Close");
		this.finish.setEnabled(false);
		this.finish.addActionListener(this);
		this.statusframe = new JLabel("");
		this.statusframe.setVisible(false);

		dest = new JLabel(s1);
		addDest = new JButton("...");
		addDest.addActionListener(this);

		sound = new JLabel(s2);
		addsound = new JButton("Add track");
		getvideo = new JButton("Create Video");
		check = new JRadioButton("Include soundtrack");
		check.addActionListener(this);
		title = new JLabel("Soundtrack Path");
		getvideo.addActionListener(this);
		addsound.addActionListener(this);
		status = new JLabel();
		format = new JComboBox(typeformat);
		this.resolution = new JComboBox(this.typeresolution);
		format.setEditable(false);
		this.resolution.setEditable(false);

		framerate = new JComboBox(this.framerateformat);
		framerate.addActionListener(this);
		framerate.setEditable(false);

		hyperlapseButton = new JButton("...");

		LabelsoundDurate = new JLabel();
		LabelsoundDurate.setVisible(false);
		LabelexstimateDurate = new JLabel("Estimated duration of the video: "
				+ (Timelapse.getBrightList().length / Integer.parseInt((String) framerate.getSelectedItem())
						+ " seconds"));

		Hashtable<Integer, JLabel> qualityTable = new Hashtable<Integer, JLabel>();
		qualityTable.put(0, new JLabel("Low"));
		qualityTable.put(50, new JLabel("Standard"));
		qualityTable.put(100, new JLabel("High"));
		quality = new JSlider(JSlider.HORIZONTAL);
		quality.setLabelTable(qualityTable);
		quality.setPaintLabels(true);
		quality.addChangeListener(new SliderListener());

		setLayout(new BorderLayout());

		// create the gridbaglayout and the gui
		GridBagConstraints gbc = new GridBagConstraints();

		setLocation(100, 100);
		p0 = new JPanel(new BorderLayout());
		p1 = new JPanel(new BorderLayout());
		p2 = new JPanel(new BorderLayout());
		p3 = new JPanel(new BorderLayout());
		p4 = new JPanel(new BorderLayout());
		p5 = new JPanel(new BorderLayout());
		p6 = new JPanel(new BorderLayout());
		p7 = new JPanel(new BorderLayout());
		p8 = new JPanel(new BorderLayout());
		p9 = new JPanel(new BorderLayout());
		p10 = new JPanel(new BorderLayout());
		p11 = new JPanel(new BorderLayout());
		p13 = new JPanel(new BorderLayout());
		p14 = new JPanel(new BorderLayout());
		p12 = new JPanel(new BorderLayout());
		p15 = new JPanel(new BorderLayout());
		p0.setBorder(new EmptyBorder(10, 10, 10, 10));
		p1.setBorder(new EmptyBorder(10, 10, 10, 10));
		p2.setBorder(new EmptyBorder(10, 10, 10, 10));
		p3.setBorder(new EmptyBorder(10, 10, 10, 10));
		p4.setBorder(new EmptyBorder(10, 10, 10, 10));
		p5.setBorder(new EmptyBorder(10, 10, 10, 10));
		p6.setBorder(new EmptyBorder(10, 10, 10, 10));
		p7.setBorder(new EmptyBorder(10, 10, 10, 10));
		p8.setBorder(new EmptyBorder(10, 10, 10, 10));
		p9.setBorder(new EmptyBorder(10, 10, 10, 10));
		p10.setBorder(new EmptyBorder(10, 10, 10, 10));
		p11.setBorder(new EmptyBorder(10, 10, 10, 10));
		p12.setBorder(new EmptyBorder(10, 10, 10, 10));
		p13.setBorder(new EmptyBorder(10, 10, 10, 10));
		p14.setBorder(new EmptyBorder(10, 10, 10, 10));
		p15.setBorder(new EmptyBorder(10, 10, 10, 10));

		panel = new JPanel(new GridBagLayout());
		panel2 = new JPanel(new BorderLayout());
		gbc.anchor = gbc.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;

		hyperlapseSelection = new JRadioButton();
		hyperlapseSelection.setEnabled(false);
		hyperlapseSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {

				if (hyperlapseSelection.isSelected()) {
					hyperlapseFlag = true;
					MyDialog.setFlagHyperlapse(true);

				} else {
					hyperlapseFlag = false;
					MyDialog.setFlagHyperlapse(false);
				}
			}
		});

		hyper = new JLabel("HYPERLAPSE");
		hyperlapseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {

				Timelapse.speedEffect();
			}
		});

		/*
		 * p0.add(new JLabel("Destinaton Path"),BorderLayout.NORTH);
		 * p0.add(dest,BorderLayout.CENTER); p0.add(
		 * this.addDest,BorderLayout.SOUTH); gbc.gridx=0; gbc.gridy=0;
		 * panel.add(p0,gbc);
		 * 
		 * p1.add(new JLabel("insert output name"),BorderLayout.NORTH);
		 * p1.add(this.nameout,BorderLayout.SOUTH); gbc.gridx=1; gbc.gridy=0;
		 * panel.add(p1,gbc);
		 * 
		 * p2.add(new JLabel("format file output..."),BorderLayout.NORTH);
		 * p2.add(format,BorderLayout.SOUTH); gbc.gridx=0; gbc.gridy=1;
		 * panel.add(p2,gbc);
		 * 
		 * 
		 * 
		 * 
		 * p3.add(new JLabel("Video's Framerate"),BorderLayout.NORTH);
		 * p3.add(this.framerate,BorderLayout.SOUTH);
		 * add(panel,BorderLayout.NORTH); add(panel2, BorderLayout.SOUTH);
		 * gbc.gridx=1; gbc.gridy=1; panel.add(p3,gbc); p4.add(new JLabel(
		 * "Video's resolutions"),BorderLayout.NORTH); p4.add(this.resolution);
		 * gbc.gridx=0; gbc.gridy=2; panel.add(p4,gbc); p5.add(new JLabel(
		 * "Qaulity codec:"),BorderLayout.NORTH);
		 * p5.add(this.quality,BorderLayout.SOUTH); gbc.gridx=1; gbc.gridy=2;
		 * panel.add(p5); p6.add(this.hyper,BorderLayout.NORTH);
		 * p6.add(this.hyperlapseSelection,BorderLayout.SOUTH); gbc.gridx=0;
		 * gbc.gridy=3; panel.add(p6,gbc); gbc.gridx=0; gbc.gridy=4;
		 * panel.add(getvideo,gbc);
		 * 
		 */

		p0.add(new JLabel("Destinaton Path"), BorderLayout.NORTH);
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(p0, gbc);

		p1.add(destinationPath, BorderLayout.NORTH);
		gbc.gridx = 1;
		gbc.gridy = 0;
		panel.add(p1, gbc);

		p2.add(this.addDest, BorderLayout.NORTH);
		gbc.gridx = 2;
		gbc.gridy = 0;
		panel.add(p2, gbc);

		add(panel, BorderLayout.CENTER);
		gbc.gridx = 1;
		gbc.gridy = 1;

		p3.add(new JLabel("Output Name"), BorderLayout.NORTH);
		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(p3, gbc);

		p4.add(this.nameOut, BorderLayout.NORTH);
		gbc.gridx = 1;
		gbc.gridy = 1;
		panel.add(p4, gbc);

		add(panel, BorderLayout.CENTER);
		gbc.gridx = 1;
		gbc.gridy = 2;

		p5.add(new JLabel("Video Resolution"), BorderLayout.NORTH);
		gbc.gridx = 0;
		gbc.gridy = 2;
		panel.add(p5, gbc);

		p6.add(this.resolution, BorderLayout.NORTH);
		gbc.gridx = 1;
		gbc.gridy = 2;
		panel.add(p6, gbc);

		add(panel, BorderLayout.CENTER);
		gbc.gridx = 1;
		gbc.gridy = 3;

		p7.add(new JLabel("Format File"), BorderLayout.NORTH);
		gbc.gridx = 0;
		gbc.gridy = 3;
		panel.add(p7, gbc);

		p8.add(this.format, BorderLayout.NORTH);
		gbc.gridx = 1;
		gbc.gridy = 3;
		panel.add(p8, gbc);

		add(panel, BorderLayout.CENTER);
		gbc.gridx = 1;
		gbc.gridy = 4;

		p9.add(new JLabel("Frame Rate"), BorderLayout.NORTH);
		gbc.gridx = 0;
		gbc.gridy = 4;
		panel.add(p9, gbc);

		p10.add(this.framerate, BorderLayout.NORTH);
		gbc.gridx = 1;
		gbc.gridy = 4;
		panel.add(p10, gbc);

		add(panel, BorderLayout.CENTER);
		gbc.gridx = 1;
		gbc.gridy = 5;

		p11.add(new JLabel("Quality of Codec"), BorderLayout.NORTH);
		gbc.gridx = 0;
		gbc.gridy = 5;
		panel.add(p11, gbc);

		p12.add(this.quality, BorderLayout.NORTH);
		gbc.gridx = 1;
		gbc.gridy = 5;
		panel.add(p12, gbc);

		add(panel, BorderLayout.CENTER);
		gbc.gridx = 1;
		gbc.gridy = 6;

		p13.add(new JLabel("Hyperlapse"), BorderLayout.NORTH);
		gbc.gridx = 0;
		gbc.gridy = 6;
		panel.add(p13, gbc);

		p14.add(this.hyperlapseButton, BorderLayout.NORTH);
		gbc.gridx = 1;
		gbc.gridy = 6;
		panel.add(p14, gbc);

		p15.add(this.hyperlapseSelection, BorderLayout.NORTH);
		gbc.gridx = 2;
		gbc.gridy = 6;
		panel.add(p15, gbc);

		add(panel, BorderLayout.CENTER);
		gbc.gridx = 1;
		gbc.gridy = 7;
		panel.add(getvideo, gbc);

		status.setVisible(false);
		addsound.setEnabled(false);
		sound.setEnabled(false);
		title.setEnabled(false);

		// getvideo.setEnabled(false);
		if (flagHyperlapse == true) {
			resolution.setEnabled(false);
		}

		setSize(400, 400);

	}

	class SliderListener implements ChangeListener {
		public void stateChanged(ChangeEvent e) {
			JSlider source = (JSlider) e.getSource();
			if (!source.getValueIsAdjusting()) {
				System.out.println(source.getValue());
			}
			if (source.getValue() < 26) {
				source.setValue(0);
			} else {
				if (source.getValue() < 76) {
					source.setValue(50);
				} else
					source.setValue(100);
			}
		}
	}

	@Override
	/**
	 * method for the action performed
	 */
	public void actionPerformed(ActionEvent e) {

		// TODO Auto-generated method stub
		if (e.getSource() == addDest) {

			if (d.save() != -1) {
				s1 = Timelapse.getDir().getPath();
				System.out.println(d.getPath());
				destinationPath.setText(d.getPath());
				if (destinationPath.getText().equals(""))
					destinationPath.setText(s1);
				// this.flagdest = true;
				// getvideo.setEnabled(true);
			}

		}
		if (e.getSource() == check) {
			if (check.isSelected() == true) {
				title.setEnabled(true);
				sound.setEnabled(true);
				addsound.setEnabled(true);
			}
			if (check.isSelected() == false) {
				title.setEnabled(false);
				sound.setEnabled(false);
				addsound.setEnabled(false);
			}
		}

		if (e.getSource() == addsound) {

			if (s.chooseSound() == 0) {

				sound.setText(s.getPath());
				this.flagsound = true;
				if (sound.getText().equals("")) {
					sound.setText(s2);
					this.flagsound = false;
				}
				if (this.flagsound) {
					this.LabelsoundDurate.setVisible(true);
					// LabelsoundDurate.setText("Length of the selected track: "
					// + (new CreateVideoSound(null,
					// null).Getdurate(s.getPath())) + " seconds");
				}
				;

			}
		}
		if (e.getSource() == getvideo) {
			Timelapse.getGraphicDeflickering().clearSelectedImage();
			setVisibleButton(false);
			Timelapse.setStartEncoding(true);

			/*
			 * if (osType.isUnix()) {
			 * System.out.println(System.getProperty("user.name"));
			 * this.flagdest = true; File folder = new File("/home/" +
			 * System.getProperty("user.name") + "/outputvideoffmpeg"); if
			 * (!folder.exists()) folder.mkdir();
			 * 
			 * this.d.setpath(folder.getPath()); }
			 */

			t = new Thread(new videoWorker(d));
			t.start();

			Timelapse.getCreateVideoDialog().dispose();
		}

		if (e.getSource() == finish) {
			Timelapse.getCreateVideoDialog().dispose();
		}
		if (e.getSource() == this.framerate) {

			this.LabelexstimateDurate.setText("Estimated duration of the video: "
					+ (Timelapse.getBrightList().length) / Integer.parseInt((String) framerate.getSelectedItem())
					+ " seconds");
		}
	}

	/**
	 * this method enable or disable format , resolution , framerate , check ,
	 * addDest , name out , get video in the dialog . enable if isVIsible is
	 * true and disable if isVisible is false
	 * 
	 * @param isVisible
	 *            Boolean to enable or disable the components
	 */
	public static void setVisibleButton(Boolean isVisible) {
		// enabled or disabled buttons ,checkbox and edittext for settings video
		format.setEnabled(isVisible);
		resolution.setEnabled(isVisible);
		framerate.setEnabled(isVisible);
		check.setEnabled(isVisible);
		if (osType.isWindows())
			addDest.setEnabled(isVisible);
		nameOut.setEditable(isVisible);
		getvideo.setEnabled(isVisible);
		hyperlapseSelection.setEnabled(isVisible);
		quality.setEnabled(isVisible);

	}

	// setter
	/**
	 * this method set the visibility of the bar with the boolean as parameter
	 * if b is true the bar is visible and if b is false the bar is not visible
	 * 
	 * @param b
	 *            Boolean for the visibility of the bar
	 */
	public static void setVisiblebar(boolean b) {
		MyDialog.bar.setVisible(b);
	}

	/**
	 * set the number of the current frame
	 * 
	 * @param i
	 *            int with the nnumber of the current frame
	 */
	public static void setiframe(int i) {
		MyDialog.iframe = i;
	}

	/**
	 * this method set the text with the String as parameter
	 * 
	 * @param s
	 *            String with the text to put in the status frame
	 */
	public static void setTextFramelabel(String s) {
		MyDialog.statusframe.setText(s);
	}

	/**
	 * this method set the flag if speedeffect is clicked
	 * 
	 * @param Flag
	 *            flagSpeedEffect is clicked
	 */
	public static void setFlagHyperlapse(Boolean Flag) {
		flagHyperlapse = Flag;
	}

	/**
	 * this method set the flag of the status of the frame
	 * 
	 * @param b
	 *            Boolean with the status of the frame
	 */
	public static void setFlagStatus(boolean b) {
		MyDialog.flagstatus = b;
	}

	// getter
	/**
	 * this method return true if is clicked speedeffect and false if si not
	 * clicked
	 * 
	 * @return Boolean flagSpeedEffect if speedeffect is clicked
	 */
	public static boolean getFlagHyperlapse() {
		return flagHyperlapse;
	}

	/**
	 * this method return the progressbar in the dialog
	 * 
	 * @return JProgressBar
	 */
	public static JProgressBar getbar() {
		return MyDialog.bar;
	}

	/**
	 * this method return the text area in the dialog
	 * 
	 * @return JTextArea
	 */
	public static JTextArea getTextArea() {
		return MyDialog.stderr;
	}

	public static JSlider getQualitySlider() {
		return quality;
	}

	public static void setHyperSelected(Boolean flag) {
		hyperlapseSelection.setSelected(flag);
	}

	public static JComboBox getResolution() {
		return resolution;
	}

	public static void enabledHyperlapseSelection(Boolean flag) {
		hyperlapseSelection.setEnabled(flag);
	}
}