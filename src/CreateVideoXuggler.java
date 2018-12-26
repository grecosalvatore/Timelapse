
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageProducer;
import java.awt.image.RescaleOp;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;
import com.xuggle.xuggler.IRational;

public class CreateVideoXuggler {
	private String path; // destination path
	protected CheckOs osType; // operating system
	protected String outputFilename = "";; // name output
	protected static String format; // format of the video
	protected static int framerate; // framerate of the video
	protected static String resolution; // resolution of the video
	protected static String qualityCodec;
	protected static int width;
	protected static int height;
	protected static int counter = 0;
	private static double brightDiff[];
	private static int counterImageSelected = 0;

	private File[] listFile;

	/**
	 * constructor of CreateVideo
	 * 
	 * @param path
	 *            String with the destination path of the video output
	 */
	public CreateVideoXuggler(String path) {
		// set all settings of video
		MyDialog.setVisibleButton(false);
		String sourcePath;

		sourcePath = path;

		listFile = Timelapse.getListFile();
		new Date();
		Date dat = new Date();
		String data;
		String time;
		data = dat.getDate() + "_" + (dat.getMonth() + 1) + "_" + dat.getYear();
		time = dat.getHours() + "_" + dat.getMinutes() + "_" + dat.getSeconds();
		this.format = ".mp4";
		outputFilename = "Output_" + data + "_" + time + format;
		framerate = 30;
		setCodec();
		width = Timelapse.getClickedImg().getWidth();
		height = Timelapse.getClickedImg().getHeight();

	}

	void getVideo(String pathdest) throws IOException {
		// start encoding video
		String DestinationPath;

		DestinationPath = pathdest;
		CheckOs os = new CheckOs();
		long startTime = System.nanoTime();
		if (os.isWindows())
			outputFilename = outputFilename + format;
		final IMediaWriter writer = ToolFactory.makeWriter(DestinationPath + File.separator + outputFilename);
		System.out.println(DestinationPath);

		brightDiff = new double[Timelapse.getBrightList().length];
		ArrayList<Integer> sortedKeyFrames = new ArrayList<Integer>();
		sortedKeyFrames = Timelapse.getKeyFrames();
		Collections.sort(sortedKeyFrames); // sort the array of
											// keyframes for can revenue
											// the interval
		for (int i = 1; i < Timelapse.getBrightList().length - 1; i++) {
			Segment s;
			Point a = null;
			Point b = null;
			// revenue the interval
			for (int y = 0; y < Timelapse.getKeyFrames().size() - 1; y++) {
				if (i == sortedKeyFrames.get(y)) {
					brightDiff[i] = 0;
					// Timelapse.getTable().getModel().setValueAt(0, i,
					// Timelapse.getTable().getColumnFromName("Bright.Compensation"));
				}
				if ((i > sortedKeyFrames.get(y)) && (i < sortedKeyFrames.get(y + 1))) {
					a = new Point(sortedKeyFrames.get(y), (int) Timelapse.getBrightList()[sortedKeyFrames.get(y)]);
					b = new Point(sortedKeyFrames.get(y + 1),
							(int) Timelapse.getBrightList()[sortedKeyFrames.get(y + 1)]);
					s = new Segment(a, b);
					// calc the distance between real curve and
					// keyframes curve in this point
					brightDiff[i] = s.distance(new Point(i, (int) Timelapse.getBrightList()[i]));
					// Timelapse.getTable().getModel().setValueAt(
					// s.distance(new Point(i, (int)
					// Timelapse.getBrightList()[i])), i,
					// Timelapse.getTable().getColumnFromName("Bright.Compensation"));//
					// put
					// the
					// value
					// in
					// the
					// table
				}
			}

		}

		// set width and height of rect if is a hyprelapse
		if (MyDialog.getFlagHyperlapse()) {
			width = Timelapse.getHyperlapseDialog().getResolutionHyperlapseW();
			height = Timelapse.getHyperlapseDialog().getResolutionHyperlapseH();

		}
		System.out.println(width + "  " + height);
		if (qualityCodec == "standard") {
			// standard quality use codec mpeg4
			writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, width, height);
		} else {
			// high quality use codec ...
			if (qualityCodec == "high") {
				// System.out.println(ICodec.getInstalledCodecs());
				writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_MPEG4, width, height);
			} else {
				// low quality use codec h264
				writer.addVideoStream(0, 0, ICodec.ID.CODEC_ID_H264, width, height);

			}
		}

		File F;
		int count = 1;

		RescaleOp brighterOp;

		IRational fps = IRational.make(framerate, 1);
		Timelapse.getGraphicDeflickering().startEncodingVideo();

		// loop for each image
		for (int i = Timelapse.getNameError().size(); i < listFile.length; i++) {

			// draw current line in the graph
			Timelapse.getGraphicDeflickering().setLineVideo(i);

			BufferedImage subImage = null;
			long timeStamp = (long) (1e6 / fps.getNumerator() * counterImageSelected/* i */);
			F = listFile[i];
			if (Timelapse.getNameError().contains(F.getName()))
				continue;
			// check if the image is selected
			System.out
					.println(Timelapse.getTable().getValueAt(i, Timelapse.getTable().getColumnCount() - 1).toString());
			if (Timelapse.getTable().getValueAt(i, Timelapse.getTable().getColumnCount() - 1).toString() == "true") {

				counterImageSelected++;

				BufferedImage image = ImageIO.read(F);

				// change bright of single image
				brighterOp = new RescaleOp(1f, (float) brightDiff[i], null);
				brighterOp.filter(image, image);

				if (MyDialog.getFlagHyperlapse()) {
					double inc_x = 0;
					double inc_y = 0;
					double rectX_start = MyHyperlapsePanel.getRealX_start();// x
																			// of
																			// start
																			// rectangle
					double rectY_start = MyHyperlapsePanel.getRealY_start();// y
																			// of
																			// start
																			// rectangle
					double rectX_end = MyHyperlapsePanel.getRealX_end();// x of
																		// end
																		// rectangle
					double rectY_end = MyHyperlapsePanel.getRealY_end();// y of
																		// end
																		// rectangle
					double app_x = 0;
					double app_y = 0;
					int resolution_width = 0;
					int resolution_height = 0;

					resolution_width = Timelapse.getHyperlapseDialog().getResolutionHyperlapseW();
					resolution_height = Timelapse.getHyperlapseDialog().getResolutionHyperlapseH();

					inc_x = (rectX_end - rectX_start) / listFile.length;// increase
																		// x of
																		// each
																		// image
					inc_y = (rectY_end - rectY_start) / listFile.length;// increase
																		// y of
																		// each
																		// image

					app_x = rectX_start + (inc_x * i);
					app_y = rectY_start + (inc_y * i);

					System.out.println(" app x :  " + app_x);
					System.out.println(" app y :  " + app_y);
					System.out.println(" inc_x :  " + inc_x);
					System.out.println(" inc_y :  " + inc_y);

					// subImage = image.getSubimage((int) Math.round(app_x),
					// (int)
					// Math.round(app_y), resolution_width,
					// resolution_height);
					subImage = image.getSubimage((int) Math.floor(app_x), (int) Math.floor(app_y), resolution_width,
							resolution_height);

					File tmpFile = new File(DestinationPath + File.pathSeparator + "TmpSubImage.jpg");
					ImageIO.write(subImage, "jpg", tmpFile);
					image = ImageIO.read(tmpFile);

				}

				// Obtain an image to encode
				MyDialog.setTextFramelabel("Frame " + i + " of " + listFile.length);
				MyDialog.getbar().setValue((int) Math.round(((double) i / (double) listFile.length) * 100.0));
				writer.encodeVideo(0, image, timeStamp, TimeUnit.MICROSECONDS);
				// create line string of output of creation video
				String line, type;
				line = new String();
				type = new String();
				if (MyDialog.getFlagHyperlapse()) {
					type = "Hyperlapse";
				} else {
					type = "Timelapse";
				}
				line = "Frame " + i + "  " + "Img " + Timelapse.getListFile()[i].getName() + "  " + writer.toString()
						+ "  " + type;
				MyDialog.getTextArea().setText(MyDialog.getTextArea().getText() + "\n" + line);
			} else {
				continue;

			}

		}
		writer.close();

		MyDialog.getTextArea()
				.setText(MyDialog.getTextArea().getText() + "\n" + "Video Creation performed Successfully");

		if (MyDialog.getFlagHyperlapse()) {
			File tmpF = new File(DestinationPath + File.pathSeparator + "TmpSubImage.jpg");
			Files.deleteIfExists(tmpF.toPath());
		}
		Timelapse.setStartEncoding(false);
		System.out.println("finish");
		MyDialog.setVisibleButton(true);

	}

	public void setCodec() {
		// set codec of xuggler
		if (MyDialog.getQualitySlider().getValue() == 0) {
			qualityCodec = "low";
		} else {
			if (MyDialog.getQualitySlider().getValue() == 50) {
				qualityCodec = "standard";
			} else {
				qualityCodec = "high";
			}
		}
	}

	public void setnameout(String s) {
		this.outputFilename = s;
	}

	public void setformat(String s) {
		this.format = s;
	}

	public void setfraamerate(int f) {
		this.framerate = f;
	}

	public void setResolution(String s) {
		this.resolution = s;

		switch (s) {
		case "640x480": {
			width = 640;
			height = 480;
			break;
		}
		case "720x576": {
			width = 720;
			height = 576;
			break;
		}
		case "800x600": {
			width = 800;
			height = 600;
			break;
		}
		case "1024x768": {
			width = 1024;
			height = 768;
			break;
		}
		case "1280x720": {
			width = 1280;
			height = 720;
			break;
		}
		case "1280x1024": {
			width = 1280;
			height = 1024;
			break;
		}
		case "1360x768": {
			width = 1360;
			height = 768;
			break;
		}
		case "1920x1080": {
			width = 1920;
			height = 1080;
			break;
		}
		case "2560x1600": {
			width = 2560;
			height = 1600;
			break;
		}
		case "3840x2160": {
			width = 3840;
			height = 2160;
			break;
		}
		case "auto": {
			width = Timelapse.getClickedImg().getWidth();
			height = Timelapse.getClickedImg().getHeight();
			break;
		}

		default: {
			width = Timelapse.getClickedImg().getWidth();
			height = Timelapse.getClickedImg().getHeight();
			break;
		}

		}
	}

}
