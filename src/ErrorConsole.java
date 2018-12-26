import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Vector;

/**
 * this method create the JPanel for the dialog of Error Console that contain
 * the table with all error or problem found in the source folder of image
 *
 */
public class ErrorConsole extends JPanel implements ActionListener {
	private JTable table; // table that contains photos issues
	private JButton BtnDelete; // delete button that delete one photo
	private JButton BtndeleteAll; // delete all button that delete all photos
	private DefaultTableModel model; // model of the table
	private JScrollPane p; // scroll panel with all problem
	private JPanel buttonpanel; // panel with the buttons
	private String[] columnNames = { "photo name", "error type", "delete photo", "priority" }; // names
																								// column
																								// of
																								// the
																								// table
	private CheckOs os; // variable with the os
	

	/**
	 * constructor of ErrorConsole
	 */
	public ErrorConsole() {
		// constructor
		this.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		os = new CheckOs();
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
					clazz = Boolean.class;
					break;
				case 3: // the column 3 is a imageicon type
					clazz = ImageIcon.class;
					break;
				}
				return clazz;
			}

			@Override
			public boolean isCellEditable(int row, int column) {
				// the column 2 is editable
				return column == 2;
			}

			@Override
			public void setValueAt(Object aValue, int row, int column) {
				// override of setvalueat to convert the column 2 in a checkbox
				if (aValue instanceof Boolean && column == 2) {
					System.out.println(aValue);
					Vector rowData = (Vector) getDataVector().get(row);
					rowData.set(2, (boolean) aValue);
					fireTableCellUpdated(row, column);
				}
			}

		};

		for (String s : columnNames)
			model.addColumn(s);
		// create the button delete
		this.BtnDelete = new JButton("Delete selectd photo");
		this.BtnDelete.addActionListener(this);
		// create the button delete all
		this.BtndeleteAll = new JButton("Delete all photo");
		this.BtnDelete.addActionListener(this);
		this.BtndeleteAll.addActionListener(this);
		// create the jpanel that contains the buttons
		buttonpanel = new JPanel();
		buttonpanel.setLayout(new BorderLayout());
		buttonpanel.add(this.BtnDelete, BorderLayout.WEST);
		buttonpanel.add(this.BtndeleteAll, BorderLayout.EAST);
		// create the table with all problems
		table = new JTable();
		if (Timelapse.getLookAndFeel() == 1) {
			table.setBackground(Color.DARK_GRAY);
			table.setGridColor(Color.WHITE);
			table.setForeground(Color.WHITE);
		}
		table.setModel(model);
		table.setModel(model);
		p = new JScrollPane(table);
		p.setSize(100, 100);
		// set the gridbacklayout of the panel
		gbc.gridx = 0;
		gbc.gridy = 0;
		add(new JLabel("Attention the following photos have found problems!"), gbc);
		gbc.gridx = 0;
		gbc.gridy = 1;
		add(p, gbc);
		gbc.gridx = 0;
		gbc.gridy = 3;
		add(this.buttonpanel, gbc);

	}

	/**
	 * this method add a row in the table of error console
	 * 
	 * @param data
	 *            Strig[] vector with the string for each column in the row to
	 *            add at the table
	 */
	public void addRow(String[] data) {
		// add a row in the table
		Object[] data1 = new Object[4];
		data1[0] = data[0];
		data1[1] = data[1];
		data1[2] = false;
		if (data[1].equals("Different risolution")) {
			// load the image and do the resize
			ImageIcon imgerror = new ImageIcon("img//Error0.png");
			Image imageerror = imgerror.getImage();
			Image newimg = imageerror.getScaledInstance(table.getRowHeight(), table.getRowHeight(),
					java.awt.Image.SCALE_SMOOTH);

			ImageIcon redwarning = new ImageIcon(newimg);

			data1[3] = redwarning;

		}
		this.model.addRow(data1);

	}

	@Override
	/**
	 * method for the action performed
	 */
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == this.BtnDelete) {
			File file = null;
			for (int i = 0; i < (table.getRowCount()); i++) {
				if (this.getValuedelete(i)) {
					if (os.isWindows())
						file = new File(Timelapse.getDir().getPath() + "\\" + this.GetNamedelete(i));
					if (os.isUnix())
						file = new File(Timelapse.getDir().getPath() + "//" + this.GetNamedelete(i));

					try {

						Files.deleteIfExists(file.toPath());
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					model.removeRow(i); // remove the deleted row
					table.repaint();
					if (table.getRowCount()== 0){
						Timelapse.setFlagErrors(false);
					}
				}

			}
		}
		if (e.getSource() == this.BtndeleteAll) {
			// delete all row
			for (int i = 0; i < (table.getRowCount()); i++) {
				File file = null;
				if (os.isWindows())
					file = new File(Timelapse.getDir().getPath() + "\\" + this.GetNamedelete(i));
				if (os.isUnix())
					file = new File(Timelapse.getDir().getPath() + "//" + this.GetNamedelete(i));

				try {

					Files.deleteIfExists(file.toPath());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				model.removeRow(i);
				table.repaint();
				Timelapse.setFlagErrors(false);
			}
		}
	}

	// getter
	/**
	 * this method return the number of the column with the name as parameter
	 * 
	 * @param name
	 *            String with the name of the column
	 * @return int with the number of the column with the name as parameter
	 */
	public int getColumnFromName(String name) {
		// get the column with the same name as the parameter
		for (int i = 0; i < this.columnNames.length; i++) {
			if (this.columnNames[i].equals(name))
				return i;
		}
		return -1;
	}

	/**
	 * this method return a boolean true if the image is deleted and false if is
	 * not deleted
	 * 
	 * @param index
	 *            int with the number of the image
	 * @return Boolean with the result of the cancellation
	 */
	private boolean getValuedelete(int index) {
		return (boolean) (this.table.getModel().getValueAt(index, this.getColumnFromName("delete photo")));
	}

	/**
	 * this method return the name of the image deleted
	 * 
	 * @param index
	 *            int with the number of the image
	 * @return String with the name of the image deleted
	 */
	private String GetNamedelete(int index) {
		return (String) this.table.getModel().getValueAt(index, this.getColumnFromName("photo name"));
	}
}
