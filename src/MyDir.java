import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Locale;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * this class save the path of a chooser of exe , chooser of audio or chooser of
 * a directory
 * String path string with the path of the chooser
 */
public class MyDir {

	private String path; // selected path
	/**
	 * return 0 if ok -1 if a problem occurred
	 * @return
	 */
		public int save(){
			JFileChooser fileChoose = new JFileChooser();
			fileChoose.setLocale(Locale.ENGLISH);
			fileChoose.updateUI();
			fileChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			//fileChoose.showSaveDialog(null);

			// "cancel" or "close" button pressed
			if (fileChoose.showSaveDialog(fileChoose) != JFileChooser.APPROVE_OPTION) {
				return -1;
			}
			// get the path of the selected directory
			this.path = fileChoose.getSelectedFile().getPath();

			

			return 0; // all right
			
			
			
		}
		

	/**
	 * this method create a chooser for a directory
	 * 
	 * @return int 0 if It was successful and -1 if It wasn't successful
	 */
	public int choose() {
		JFileChooser fileChoose = new JFileChooser();
		fileChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChoose.setLocale(Locale.ENGLISH);
		fileChoose.updateUI();
		// "cancel" or "close" button pressed
		if (fileChoose.showOpenDialog(fileChoose) != JFileChooser.APPROVE_OPTION) {
			return -1;
		}
		// get the path of the selected directory
		this.path = fileChoose.getSelectedFile().getPath();

		if (!checkAllImages()) {
			return 1;
		}

		return 0; // all right
	}

	/**
	 * this method create a chooser for a file audio
	 * 
	 * @return int 0 if It was successful and -1 if It wasn't successful
	 */
	public int chooseSound() {
		JFileChooser fileChoose = new JFileChooser();
		fileChoose.setLocale(Locale.ENGLISH);
		fileChoose.updateUI();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Audio file", "mp3", "wav", "wma");
		fileChoose.setFileFilter(filter);
		// fileChoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		// "cancel" or "close" button pressed
		if (fileChoose.showOpenDialog(fileChoose) != JFileChooser.APPROVE_OPTION) {
			return -1;
		}
		// get the path of the selected directory
		this.path = fileChoose.getSelectedFile().getPath();

		return 0; // all right
	}

	/**
	 * this method create a chooser for a file .exe
	 * 
	 * @return int 0 if It was successful and -1 if It wasn't successful
	 */
	public int chooseExe() {
		JFileChooser fileChoose = new JFileChooser();
		fileChoose.setLocale(Locale.ENGLISH);
		fileChoose.updateUI();
		FileNameExtensionFilter filter = new FileNameExtensionFilter("Exe file", "exe");
		fileChoose.setFileFilter(filter);
		// fileChoose.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		// "cancel" or "close" button pressed
		if (fileChoose.showOpenDialog(fileChoose) != JFileChooser.APPROVE_OPTION) {
			return -1;
		}
		// get the path of the selected directory
		this.path = fileChoose.getSelectedFile().getPath();

		return 0; // all right
	}

	// checks if all files in the selected dir are images
	/**
	 * this method check if all file in the selected direcctory are image
	 * 
	 * @return Boolean true if all file are image and false if not file are
	 *         image
	 */
	private boolean checkAllImages() {
		int count = 0;
		File folder = new File(this.path);
		String fileType = null;
		// iterate on all files in the selected dir
		for (File fileName : folder.listFiles()) {
			try {
				// get file type
				fileType = Files.probeContentType(fileName.toPath());
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}

			// directory
			if (fileType == null) {
				return false;
			}
			// not image
			if (fileType.startsWith("image") == false) {
				return false;
			}

			count++; // count number of file
		}

		// there aren't files in the dir
		if (count == 0) {
			return false;
		}

		// not all files are images
		return true;
	}

	@SuppressWarnings("unused")
	// checks if all images have the same size
	/**
	 * this method check if all images in the selected directory have the same
	 * resolution
	 * 
	 * @return Boolean true if all image have same resolution and false if not
	 *         all images have the same resolution
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws SecurityException
	 * @throws IOException
	 * @throws JpegProcessingException
	 */
	private boolean checkSameSize() throws NumberFormatException, IllegalArgumentException, SecurityException,
			IOException, JpegProcessingException {
		File folder = new File(this.path);

		File first = folder.listFiles()[0];

		Metadata data = JpegMetadataReader.readMetadata(first);

		ExifSubIFDDirectory dir; // directory with exif data
		ExifSubIFDDescriptor exif; // descriptor of exif data

		dir = data.getDirectory(ExifSubIFDDirectory.class);
		exif = new ExifSubIFDDescriptor(dir);

		int firstHeight = Integer.valueOf((exif.getExifImageHeightDescription().split(" "))[0]);
		int firstWidth = Integer.valueOf((exif.getExifImageWidthDescription().split(" "))[0]);

		int cont = 0;

		// iterate on all files in the selected dir
		for (File fileName : folder.listFiles()) {
			data = JpegMetadataReader.readMetadata(fileName);

			dir = data.getDirectory(ExifSubIFDDirectory.class);
			exif = new ExifSubIFDDescriptor(dir);

			if (firstWidth != Integer.valueOf((exif.getExifImageWidthDescription().split(" "))[0])
					|| firstHeight != Integer.valueOf((exif.getExifImageHeightDescription().split(" "))[0]))
				return false;
		}
		return true;
	}

	// getter
	/**
	 * this method retur the selected path
	 * 
	 * @return String path with the selected path
	 */
	public String getPath() {
		return this.path;
	}

	// setter
	/**
	 * this method set the path of the directory selected
	 * 
	 * @param path
	 *            String with the selected path
	 */
	public void setpath(String path) {
		this.path = path;
	}
}
