import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;

import mediautil.image.jpeg.Entry;
import mediautil.image.jpeg.Exif;
import mediautil.image.jpeg.LLJTran;
import mediautil.image.jpeg.LLJTranException;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDescriptor;
import com.drew.metadata.exif.ExifSubIFDDirectory;

/**
 * this class create a new time of image with more information
 *
 */
public class MyImage {

	// image name
	private static String name;
	// image
	private BufferedImage image;
	// shot date and time
	private Date dateTime;
	// image exif data
	private String[] exifData = { "", "", "", "", "", "", "", "" };
	// average brightness of the selection
	double avgBright;

	public MyImage() {
		int r, g, b = 0;
		this.image = null;
		avgBright = 0;

	}

	// load image in the memory
	/**
	 * this method load the image with the path passed as parameter 
	 * @param imgPath is the string with the path of the image
	 */
	public void load(String imgPath) {
		File img = new File(imgPath);
		try {
			// read file and save data as image
			this.image = ImageIO.read(img);
		} catch (IOException ex) {
			ex.getMessage();
		}
	}



	// setter
	/**
	 * this method set the name of the image
	 * @param name string with the nam of the image
	 */
	public void setName(String name) {
		MyImage.name = name;
	}

	public void setImage(BufferedImage img) {
		if (img != null)
			this.image = img;
	}

	/* Exif Metadata Read */
	public void readExif(String filePath) {
		File fImg = new File(filePath);

		Metadata data = null;

		MyTable table = Timelapse.getTable();

		try {
			// read metadata from image
			data = JpegMetadataReader.readMetadata(fImg);
		} catch (JpegProcessingException | IOException e) {
			System.out.println(e.getMessage());
			return;
		}

		ExifSubIFDDirectory dir;
		ExifSubIFDDescriptor exif;

		dir = data.getDirectory(ExifSubIFDDirectory.class);
		exif = new ExifSubIFDDescriptor(dir);

		// if image doesn't have exif metadata available
		try {
			dateTime = dir.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
		} catch (NullPointerException e) {
			return;
		}

		// automatically get index (from column name)
		// if column names are being exchanged, it is not necessary to modify
		// indices in the code below
		safeRead(table, "Image Name", fImg.getName());
		safeRead(table, "Time", dateTime.toString());
		safeRead(table, "Exposure Time", exif.getExposureTimeDescription());
		safeRead(table, "Aperture", exif.getFNumberDescription());
		safeRead(table, "ISO Value", exif.getIsoEquivalentDescription());
		safeRead(table, "Focal Length", exif.getFocalLengthDescription());
		//safeRead(table, "Flash", exif.getFlashDescription());
	}

	private void safeRead(MyTable table, String name, String value) {
		if (value != null)
			this.exifData[table.getColumnFromName(name)] = value;
		else
			this.exifData[table.getColumnFromName(name)] = "/";
	}



	/* brightness calculus */
	public static int[] convertToPixels(Image img) throws InterruptedException {
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		int[] pixel = new int[width * height];

		PixelGrabber pg = new PixelGrabber(img, 0, 0, width, height, pixel, 0, width);

		pg.grabPixels();

		return pixel;
	}

	public void pixelBright() throws InterruptedException {
		int r, g, b;
		double lum = 0;

		this.avgBright = 0;

		int[] argb = convertToPixels(this.image);

		for (int i = 0; i < argb.length; i++) {
			// get RGB from Pixel data
			r = (argb[i]) & 0xFF;
			g = (argb[i] >> 8) & 0xFF;
			b = (argb[i] >> 16) & 0xFF;
			// different formulas to get brightness from RGB
			// an example site: http://www.w3.org/TR/AERT#color-contrast
			// lum = 0.33*r + 0.5*g + 0.16*b;
			// lum = 0.2126*r + 0.7152*g + 0.0722*b;
			lum = 0.299 * r + 0.587 * g + 0.114 * b;
			this.avgBright += lum;
		}

		this.avgBright = this.avgBright / argb.length;
	}



	/* Exif Metadata Write (it uses external tool: "EXIFTOOL") */
	public void writeExif(double value) throws IOException, InterruptedException {
		// remove escape / from file name
		// String fileName = name.replace("\\", "\\\\");

		if (value == Double.valueOf(0))
			return;

		// this.writeLLJ(name, value);

		this.writeExifTool(name, value);

		// if it is not possible to write EXIF data directly on the images,
		// it writes them on a file
		this.writeOnFile(name, value);
	}

	// it uses MediaUtil library
	// problem: it doesn't re-write correctly the images
	@SuppressWarnings("unused")
	private void writeLLJ(String fileName, double value) throws IOException {
		InputStream fin = new BufferedInputStream(new FileInputStream(fileName));

		LLJTran llj = new LLJTran(fin);
		try {
			llj.read(LLJTran.READ_ALL, true);
		} catch (LLJTranException e) {
			e.printStackTrace();
		}

		// Change the value of the EXPOSUREBIASVALUE Entry

		/*
		 * Exif exif = (Exif) llj.getImageInfo(); Entry entry =
		 * exif.getTagValue(Exif.EXPOSUREBIASVALUE, true); if(entry != null)
		 * entry.setValue(0, value);
		 */

		System.out.println(String.valueOf(value));
		llj.setComment(String.valueOf(value));
		llj.refreshAppx();

		fin.close();

		FileOutputStream fout = new FileOutputStream(fileName);
		llj.save(new BufferedOutputStream(fout));

		fout.close();

		llj.freeMemory();
	}

	// it uses external tool "ExifTool"
	// @SuppressWarnings("unused")
	/**
	 * 
	 * @param fileName
	 * @param value
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private void writeExifTool(String fileName, double value) throws IOException, InterruptedException {
		List<String> listArgs = new ArrayList<String>();
		listArgs.add("exiftool");
		listArgs.add("-overwrite_original");
		listArgs.add("-exposurecompensation=" + value);
		listArgs.add(fileName);

		ProcessBuilder pb = new ProcessBuilder(listArgs);
		Process p = pb.start();

		p.waitFor();

		BufferedReader errinput = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		String line;
		while ((line = errinput.readLine()) != null) {
			System.out.println(line);
		}

		p.destroy();
		System.gc();
	}

	private void writeOnFile(String fileName, double value) {
		String dateTime = new SimpleDateFormat("yyyyMMdd").format(Calendar.getInstance().getTime());
		try {
			String filename = "EXIF_" + dateTime + ".txt";
			FileWriter fw = new FileWriter(filename, true); // the true will
															// append the new
															// data
			fw.write(fileName + " = " + String.valueOf(value) + "\n"); // appends
																		// the
																		// string
																		// to
																		// the
																		// file
			fw.close();
		} catch (IOException ioe) {
			System.err.println("IOException: " + ioe.getMessage());
		}
	}
	
	
	// getter
	/**
	 * this method return the buffered image of the image
	 * @return BufferedImage image
	 */
	public BufferedImage getImage() {
		return this.image;
	}

	/**
	 * this method return the width of the image
	 * @return int width of the image
	 */
	public int getWidth() {
		return this.image.getWidth();
	}

	/**
	 * this method return the height of the image
	 * @return int height of the image
	 */
	public int getHeight() {
		return this.image.getHeight();
	}
	
	/**
	 * this method return the metadata of the image
	 * @return string[] with the metadata of the image
	 */
	public String[] getMetadata() {
		return this.exifData;
	}
	
	/**
	 * this method return the average brightness of the image
	 * @return double with the average brightness of the image
	 */
	public double getBright() {
		return this.avgBright;
	}

	/**
	 * this method return the dataTime when the image was taken
	 * @return Date when the image was taken
	 */
	public Date getDateTime() {
		return dateTime;
	}

}
