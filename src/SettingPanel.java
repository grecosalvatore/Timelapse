import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;



/**
 * this class extends JPanel and implements ActionListener , create the GUI of
 * the settingPanel and manage all listener with the possibility of add
 * keyframes at the graph for draw the keyframes curve or draw the default curve
 * . with finish start che calculation of the bringht compensation and with
 * ffmpeg button start the creation of the video
 */
public class SettingPanel extends JPanel implements ActionListener {
	private boolean flagfinish; // flag if finish is pressed
	private JButton compensation; // button for calc the bright compensation
	private static JButton clear; // button for clear the graph and delete all
									// keyframes
	private JSplitPane split; // split with the split left and split right
	private JSplitPane splitRight; // panel right with the scroll table
	private JScrollPane scrollTableKeyFrames; // scrolltable with the selected
												// keyframes
	private JTable tableKeyFrames; // table of selected keyframes
	private DefaultTableModel model; // model of the tablekeyframes
	// private JPanel panelLeft; // panel left with the three buttons
	private JSplitPane splitButton; // split with finish button and clear button
	private static JButton createVideoButton; // button for start the create
												// video

	private static JPopupMenu popUp;
	private static boolean flagIsClickedConstantButton = false; // flag if the
																// constantButton
																// is pressed
	private static boolean flagIsClickedStraightLineButton = false; // flag if
																	// the
																	// straightLineButton
																	// is
																	// pressed
	private String[] columnNames = { "N°", "KeyFrames", "Remove" };

	private static JRadioButton hyperlapseSelection;
	private JLabel hyper;
	private boolean hyperlapseFlag = false;
	private JLabel imagelabel;
	private ImageIcon iconCloseResize;

	/**
	 * consntructor of settingPanel create the GUI f setting panel and add the
	 * listeners
	 */
	public SettingPanel() {
		// create the setting panel with the buttons and scrolltable
		this.flagfinish = false;
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// create the clear and the finish buttons
		compensation = new JButton("Calc Compensation");

		compensation.addActionListener(this);
		clear = new JButton("Clear");
		clear.addActionListener(this);
		compensation.setVisible(true);
		clear.setVisible(true);

		compensation.setToolTipText("calc bright compensation with selected keyframes");
		clear.setToolTipText("clear deflickering graph and delete all keyframes");

		clear.setEnabled(false);

		ImageIcon iconFfmpeg = new ImageIcon("img//pellicola.png");
		Image img = iconFfmpeg.getImage();
		Image newimg = img.getScaledInstance(150, 40, java.awt.Image.SCALE_SMOOTH);

		ImageIcon iconFfmpegResize = new ImageIcon(newimg);
		// create the ffmpeg button
		createVideoButton = new JButton(iconFfmpegResize);
		createVideoButton.addActionListener(this);
		createVideoButton.setToolTipText("Click the button for start encoding video.");
		createVideoButton.setEnabled(false);

		hyperlapseSelection = new JRadioButton();
		hyper = new JLabel("HYPERLAPSE");

		ImageIcon iconConstant = new ImageIcon("img//constant.png");
		Image imgConst = iconConstant.getImage();
		Image newimgConst = imgConst.getScaledInstance(175, 50, java.awt.Image.SCALE_SMOOTH);

		// create the table of keyframes
		tableKeyFrames = new JTable();
		createTable();
		
		/*tableKeyFrames.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {
				int row = arg0.getLastIndex()-1;
				Timelapse.getGraphicDeflickering().setLineKeyframes(row);
			}
		});
		*/

		tableKeyFrames.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {

				JTable target = (JTable) e.getSource();
				int row = target.getSelectedRow();
				int column = target.getSelectedColumn();
				// do some action if appropriate column
				// System.out.println(row+" "+column);
				Timelapse.getGraphicDeflickering().setLineKeyframes(row);

				if (column == 2) {
					if ( (row == 0) || (row == tableKeyFrames.getRowCount()-1) ){
						JOptionPane.showMessageDialog(null, "Error! Impossible delete first and last keyframes", "Error!",
								JOptionPane.ERROR_MESSAGE);
					}else {
					model.removeRow(row);
					System.out.println(Timelapse.getKeyFrames().get(row));
					//Timelapse.getGraphicDeflickering().deleteKyframesPoint(Timelapse.getKeyFrames().get(row));
					Timelapse.getGraphicDeflickering().deleteKyframesPoint(row);
					}
				}
			}

		});

		scrollTableKeyFrames = new JScrollPane(tableKeyFrames);
		if (Timelapse.getLookAndFeel() == 1)
			scrollTableKeyFrames.setBackground(Color.DARK_GRAY);

		// create the split with in the left the three buttons and in the right
		// a split with in the top the scrolltable and in the bottom the clear
		// and the finish button
		split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		split.setSize(screenSize.width / 2, screenSize.height / 2);
		clear.setPreferredSize(new Dimension(split.getHeight() / 2, (split.getWidth() / 3)));

		compensation.setPreferredSize(new Dimension(split.getHeight() / 2, (split.getWidth() / 3)));
		createVideoButton.setPreferredSize(new Dimension(split.getHeight() / 2, (split.getWidth() / 3)));
		if (Timelapse.getLookAndFeel() == 1)
			split.setBackground(Color.DARK_GRAY);

		JPanel buttonpanel = new JPanel(new BorderLayout());

		buttonpanel.add(clear, BorderLayout.WEST);
		buttonpanel.add(compensation, BorderLayout.CENTER);
		buttonpanel.add(createVideoButton, BorderLayout.EAST);
		buttonpanel.setPreferredSize(new Dimension(45, 45));
		if (Timelapse.getLookAndFeel() == 1)
			buttonpanel.setBackground(Color.DARK_GRAY);
		// splitRight.setTopComponent(scrollTableKeyFrames);
		// splitRight.setBottomComponent(buttonpanel);
		JPanel PanelRight = new JPanel(new BorderLayout());
		PanelRight.add(this.scrollTableKeyFrames, BorderLayout.CENTER);
		PanelRight.add(buttonpanel, BorderLayout.SOUTH);
		split.setContinuousLayout(true);
		// splitRight.setContinuousLayout(true);
		split.setDividerLocation(split.getWidth() / 2);
		split.setDividerSize(0);

		split.setRightComponent(PanelRight);
		// split.setLeftComponent(panelLeft);
		split.setVisible(true);
		// splitButton.setDividerSize(0);
		split.setDividerSize(0);
		// splitRight.setDividerSize(0);
		split.repaint();

	}

	@Override
	/**
	 * this method manage the action
	 */
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getSource() == compensation) {
			// when finish is clicked the program calc the bright compensation
			// between real curve of brightness and
			// the keyframes curve
			if (Timelapse.getFlagCalc() && (Timelapse.getKeyFrames().size() >= 1)) {
				// the first and and the last image must be always be in the
				// graph
				Timelapse.getKeyFrames().add(0); // add the first image as
													// keyframes
				Timelapse.getKeyFrames().add(Timelapse.getBrightList().length - 1); // add
																					// the
																					// last
																					// image
																					// as
																					// keyframes
				this.flagfinish = true; // finish is clicked then the flag is
										// true
				ArrayList<Integer> sortedKeyFrames = new ArrayList<Integer>();
				sortedKeyFrames = Timelapse.getKeyFrames();
				Collections.sort(sortedKeyFrames); // sort the array of
													// keyframes for can revenue
													// the interval
				double tmp[] = new double[Timelapse.getBrightList().length];// instance
																			// a
																			// temporary
																			// array
																			// for
																			// bright
																			// compensation
				// the bright compensation of the selected keyframes is always 0
				tmp[0] = 0;
				tmp[Timelapse.getBrightList().length - 1] = 0;
				//Timelapse.getTable().getModel().setValueAt(0, 0,
				//		Timelapse.getTable().getColumnFromName("Bright.Compensation")); // insert
																						// the
																						// value
																						// in
																						// the
																						// table
				//Timelapse.getTable().getModel().setValueAt(0, sortedKeyFrames.get(sortedKeyFrames.size() - 1),
				//		Timelapse.getTable().getColumnFromName("Bright.Compensation")); // insert
																						// the
																						// value
																						// in
																						// the
																						// table
				// loop for calc the bright compensation for each point
				for (int i = 1; i < Timelapse.getBrightList().length - 1; i++) {
					Segment s;
					Point a = null;
					Point b = null;
					// revenue the interval
					for (int y = 0; y < Timelapse.getKeyFrames().size() - 1; y++) {
						if (i == sortedKeyFrames.get(y)) {
							tmp[i] = 0;
							//Timelapse.getTable().getModel().setValueAt(0, i,
							//		Timelapse.getTable().getColumnFromName("Bright.Compensation"));
						}
						if ((i > sortedKeyFrames.get(y)) && (i < sortedKeyFrames.get(y + 1))) {
							a = new Point(sortedKeyFrames.get(y),
									(int) Timelapse.getBrightList()[sortedKeyFrames.get(y)]);
							b = new Point(sortedKeyFrames.get(y + 1),
									(int) Timelapse.getBrightList()[sortedKeyFrames.get(y + 1)]);
							s = new Segment(a, b);
							// calc the distance between real curve and
							// keyframes curve in this point
							tmp[i] = s.distance(new Point(i, (int) Timelapse.getBrightList()[i]));
							//Timelapse.getTable().getModel().setValueAt(
							//		s.distance(new Point(i, (int) Timelapse.getBrightList()[i])), i,
							//		Timelapse.getTable().getColumnFromName("Bright.Compensation"));// put
																									// the
																									// value
																									// in
																									// the
																									// table
						}
					}
					Timelapse.setBrightDiff(tmp); // copy the vector of
													// brightness in timelapse
					createVideoButton.setEnabled(true);// when all bright
														// compensation were
														// calculated FFmpeg
														// button
					// i enabled

					// Timelapse.getMenu().setMenuVideo(true);// enabled create
					// video from menu
					// Timelapse.getMenu().setHyperlapse(true);

				}
			} else {
				JOptionPane.showMessageDialog(null,
						"error! you must select a keyframes for calc the brhight compensation!!", "Error!",
						JOptionPane.ERROR_MESSAGE);

			}
		}
		if (arg0.getSource() == clear) {
			clearKeyframes();
		}

		if (arg0.getSource() == createVideoButton) {
			if (Timelapse.getSettingPanel().getflag()) {
				Timelapse.setCreateVideoDialog(); // if ffmpeg button was
													// clicked the program
													// create the dialog with
													// the settings of the
													// encoder video
			} else {
				JOptionPane.showMessageDialog(null, "error! you must calc bright compensation before get video!",
						"Error!", JOptionPane.ERROR_MESSAGE);

			}
		}

	}

	/**
	 * this method delete all keyframes in the scrolltable , in the list of
	 * keyframes and in the graph
	 * 
	 */
	public void clearKeyframes() {
		createVideoButton.setEnabled(false);
		// when clear was clicked the create
		// video is not enabled beacuse
		// isn't calculated teh bright
		// compensation

		for (int i = model.getRowCount() - 1; i >= 0; i--) {
			model.removeRow(i); // remove all keyframes from table and
								// jscrolltable
		}
		Timelapse.removeDuplicate();
		Timelapse.deleteKeyFrames(); // delete the keyframes from array
		Timelapse.getGraphicDeflickering().removeDeafultSeries(); // remove the
																	// keyframes
																	// curve
																	// from
																	// graph
		scrollTableKeyFrames.repaint();
	}

	/**
	 * this method create the table with the keyframes
	 */
	private void createTable() {
		// create the table for the list of keyframes in the scrolltable
		model = new DefaultTableModel() {
			@Override
			public Class<?> getColumnClass(int columnIndex) {
				Class clazz = String.class;
				switch (columnIndex) {
				case 0: // the column 0 is a string type
					clazz = String.class;
					break;
				case 1: // the column 1 is a string type
					clazz = String.class;
					break;
				case 2: // the column 2 is a boolean type
					clazz = ImageIcon.class;
					break;

				}
				return clazz;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;

			}

		};

		if (Timelapse.getLookAndFeel() == 1) {
			tableKeyFrames.setBackground(Color.DARK_GRAY);
			tableKeyFrames.setGridColor(Color.WHITE);
			tableKeyFrames.setForeground(Color.WHITE);
		}

		tableKeyFrames.setModel(model);
		for (int i = 0; i < columnNames.length; i++) {
			model.addColumn(columnNames[i]);
		}
	}

	/**
	 * this method add a keyframe in the table of the scrolltable
	 * 
	 * @param s
	 *            String with the name of the image to add
	 */
	public void addTableKeyFrames(String[] s) {
		// add a keyframe in the table
		Boolean flagAddKey = true;
		for (int j = 0; j < tableKeyFrames.getRowCount(); j++) {
			String appString = null;
			appString = (String) tableKeyFrames.getModel().getValueAt(j, 0);
			if (s[1] == appString) {
				flagAddKey = false;
			} // search if the keyframe was selected before
		}
		if (flagAddKey) {
			String[] ss = new String[3];
			ss[0] = s[0];
			ss[1] = s[1];
			ImageIcon iconClose = new ImageIcon("img//close.png");

			Image imgClose = iconClose.getImage();
			Image newimgClose = imgClose.getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
			iconCloseResize = new ImageIcon(newimgClose);
			JButton buttonDelete = new JButton("tasto");
			buttonDelete.setSize(3, 3);

			ss[2] = null;

			model.addRow(ss);
			model.setValueAt(iconCloseResize, model.getRowCount() - 1, 2);

		}

	}

	/**
	 * this method disable all buttons
	 */
	public static void disableButton() {
		// function for disabled each button
		// constantButton.setEnabled(false);
		// straightLineButton.setEnabled(false);
		hyperlapseSelection.setEnabled(false);
		createVideoButton.setEnabled(false);
		clear.setEnabled(false);
		Border emptyBorder = BorderFactory.createEmptyBorder();
		// constantButton.setBorder(emptyBorder);
		// straightLineButton.setBorder(emptyBorder);
	}

	// setter
	/**
	 * this method enable all buttons
	 */
	public static void setGraphButton() {
		// function for enabled each button
		// constantButton.setEnabled(true);
		// straightLineButton.setEnabled(true);
		hyperlapseSelection.setEnabled(true);
		clear.setEnabled(true);
		flagIsClickedConstantButton = false;
		flagIsClickedStraightLineButton = false;
	}

	/**
	 * this metho return the flag straightlinebutton
	 * 
	 * @return booelan flagIsClickedStraightLineButton that is true if the
	 *         button straighLineButton is clicked
	 */
	public static boolean getFlagIsClickedStraightLineButton() {
		// getter flag of straightlinebutton
		return flagIsClickedStraightLineButton;
	}

	// getter
	/**
	 * this method return the flag finish
	 * 
	 * @return Boolean flagfinish that is true if finish button is clicked
	 */
	public boolean getflag() {
		/// getter flag of finish button
		return this.flagfinish;
	}

	/**
	 * this method retunr the JSplitPane of the setting panel
	 * 
	 * @return JSplitPane with the setting panel
	 */
	public JSplitPane getSettingPanel() {
		return split;
	}

	/*
	 * public JRadioButton getHyperlapseSelection() { return
	 * hyperlapseSelection; }
	 */
}