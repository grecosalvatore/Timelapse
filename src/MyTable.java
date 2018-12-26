import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
/**
 * this class extends JTable and have all method to the management of the table
 * of images. String[] columnNames are the name of the column ( the name and the
 * exif of each image ) DefaultTableModel model is the model of the table
 * threadChangeImg is the thread for change image in the panel MyImage img is a
 * image of type MyImage boolean resizable indicates wheter table is resizable
 * or not int tableWidthLimit indicates the table width limit
 */
public class MyTable extends JTable {

	private String[] columnNames = { "Image Name", "Time", "ISO Value", "Focal Length", "Exposure Time", "Aperture",
			"Selected" /*
						 * , "Flash", "Brightness", "Bright.Compensation"
						 */ }; // vector of string with the column names
	private DefaultTableModel model; // model of the table
	private MyImage img; // support variable as MyImage
	private Thread threadChangeImg; // thread to change the selected image

	// flag: indicates whether table is resizable or not
	private boolean resizable;
	private static int p=0;

	// width limit: used to decide if make table resizable or not
	private int tableWidthLimit;
	private static JCheckBox selectionCheckBox;

	public  int getP(){
		return this.p;}
	/**
	 * constructor of MyTable
	 */
	public MyTable() {
		// p=0;

		selectionCheckBox = new JCheckBox();
		model = new DefaultTableModel() {

			/*
			 * Class[] types = { String.class, String.class, String.class,
			 * String.class, String.class, String.class, Boolean.class };
			 * 
			 */

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
					clazz = String.class;
					break;
				case 3: // the column 3 is a imageicon type
					clazz = String.class;
					break;
				case 4: // the column 3 is a imageicon type
					clazz = String.class;
					break;
				case 5: // the column 3 is a imageicon type
					clazz = String.class;
					break;
				case 6: // the column 3 is a imageicon type
					clazz = Boolean.class;
					break;
				}
				return clazz;
			}
		  @Override
			public boolean isCellEditable(int row, int column) {
			  if(row>p-1)
				return column == 6;
			  else
				  return false;
			}
		};
		// color of dark theme
		/*if (Timelapse.getLookAndFeel() == 1) {
			this.setBackground(Color.DARK_GRAY);
			this.setGridColor(Color.WHITE);
			this.setForeground(Color.WHITE);
		}*/
		
		setModel(model);
		setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
					boolean hasFocus, int row, int col) {

				super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

				
				
					if (isSelected) {
						setBackground(table.getSelectionBackground());
						setForeground(table.getSelectionForeground());
					} else if (row < p) {
						//System.out.println("trovato");
						// setBackground(Color.RED);
							
						
						setBackground(new Color(1f, 0f, 0f, .4f));
						setForeground(Color.WHITE);
					} else {
						
						
						setBackground(table.getBackground());
						setForeground(table.getForeground());

					}
				return this;
			}
		});

		// creates columns
		for (String s : columnNames)

			model.addColumn(s);

		setResizable(false);

		this.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent arg0) {

				if (Timelapse.isFlagDir()) {
					if (Timelapse.getStartEncoding() == false) {
						threadChangeImg = new Thread(new changeImg());
						threadChangeImg.start();
					}
				}
			}
		});

	}

	/**
	 * 
	 * thread that change the image in the panel
	 *
	 */
	public class changeImg implements Runnable {
		public changeImg() {
		}

		@Override
		public void run() {
			getSelectedImg();
		}
	}

	/**
	 * method that get the selected image in the folder
	 */
	public void getSelectedImg() {
		int cont = 0;
		img = new MyImage();
		BufferedImage tmp = null;
		// get current directory
		File folder = new File(Timelapse.getDir().getPath());
		String name = (String) this.getModel().getValueAt(this.getSelectedRow(), 0);
		for (File fileName : Timelapse.getListFile()) {
			if (fileName.getName().equals(name)) {
				try {
					// read file and save data as image
					tmp = ImageIO.read(fileName);

				} catch (IOException ex) {
					System.out.println(ex.getMessage());
				}
				img.setImage(tmp);
				// allows image drawing on panel
				Timelapse.setClickedImg(img);
				Timelapse.getImagePanel().setImage(img);

				// if graphic is painted, highlights the corresponding value of
				// the selected photo

				if (Timelapse.isFlagCalc()) {
					MyGrahpPanel.setLine(cont);

				}

				return;
			}
			cont++;

		}

		threadChangeImg.stop();
	}

	// add a row to the table
	/**
	 * this method add a row in the table
	 * 
	 * @param data
	 *            vector of string with the value for each column of the row
	 */
	/*public void addRow(String[] data) {
		this.model.addRow(data);
		
	}*/
	
	
	public void addRow(String[] data) {
		// add a row in the table
		Object[] tmp = new Object[7];
		tmp[0] = data[0];
		tmp[1] = data[1];
		tmp[2] = data[2];;
		tmp[3] = data[3];
		tmp[4] = data[4];
		tmp[5] = data[5];
		if(!Timelapse.getNameError().contains(data[0]))
			tmp[6] = true;
		else
			tmp[6]=false;
		this.model.addRow(tmp);

	}

	// Setter
	/**
	 * this method set the table width limit
	 * 
	 * @param tableWidth
	 *            integer with the table width limit
	 */
	public void setTableWidthLimit(int tableWidth) {
		this.tableWidthLimit = tableWidth;
	}

	/**
	 * set the resizable or not resizable of the table
	 * 
	 * @param resiz
	 *            boolean if the table is resizable
	 */
	public void setResizable(boolean resiz) {
		if (resiz)
			setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		else
			setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.resizable = resiz;
	}

	// Getter
	/**
	 * this method return the index of the column with the name as the string in
	 * input , if the column is not found this method return -1
	 * 
	 * @param name
	 *            is the string name of the column
	 * @return integer i index of the column of -1 if the column is not found
	 */
	public int getColumnFromName(String name) {
		for (int i = 0; i < this.columnNames.length; i++) {
			if (this.columnNames[i].equals(name))
				return i;
		}
		return -1;
	}

	/**
	 * return true if the table is resizable
	 * 
	 * @return boolean resizable
	 */
	public boolean getResizable() {
		return resizable;
	}

	/**
	 * getter of the width limit of the table
	 * 
	 * @return int tableWidthLimit is the with limit of the table
	 */
	public int getTableWidthLimit() {
		return tableWidthLimit;
	}

	public void ChangeRow() {
		p = 0;
		for (int i = 0; i < this.getRowCount(); i++) {
			for (int j = 0; j < Timelapse.getNameError().size(); j++)
				if (this.getModel().getValueAt(i, 0).equals(Timelapse.getNameError().get(j))) {
					String val[] = new String[6];
					for (int x = 0; x < model.getColumnCount()-1; x++) {
						val[x] = (String) this.getModel().getValueAt(i, x);
						
				
					}
					
					model.removeRow(i);

					model.insertRow(p, val);

					p++;
				}

		}

	}

}
/*
class PopUpDemo extends JPopupMenu {
	JMenuItem anItem;

	public PopUpDemo() {
		anItem = new JMenuItem("exclude this photo");
		add(anItem);
	}
}
*/