import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * This class creates charts with histogram (rgb and bright) for an image. The
 * chart is created with JFreeChart.
 */
public class MyHisto extends JPanel {

	private static double[] red; // vector of red pixel
	private static double[] green; // vector of green pixel
	private static double[] blue; // vector of blue pixel
	private static double[] brightness; // vector of bright
	private static int[] pix; // vector with pixel of image
	private JFreeChart jfreechartRGB; // jfreechart with the rgb histogram
	private JFreeChart jfreechartBright; // jfreechart with the brightness
											// histogram
	private Image image; // support variable of a image

	public MyHisto(Image image) {

		int r, g, b = 0;
		// the inrerval value of r , g , b is 0-255
		red = new double[256];
		green = new double[256];
		blue = new double[256];
		brightness = new double[256];
		this.image = image;
		if (this.image != null) {

			try {
				pix = MyImage.convertToPixels(image);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			double bright = 0;
			for (int i = 0; i < pix.length; i++) {	
				r = (pix[i] >> 16) & 0xFF;
				g = (pix[i] >> 8) & 0xFF;
				b = (pix[i]) & 0xFF;
				red[r]++;
				green[g]++;
				blue[b]++;
				// function for calc the brightness with the rgb
				bright = 0.3 * r + 0.6 * g + 0.1 * b;
				brightness[(int) bright]++;
			}

			// create rgb histogram
			jfreechartRGB = ChartFactory.createHistogram(" RGB Histogram", null, null, createDataset(),
					PlotOrientation.VERTICAL, true, true, false);
			if (Timelapse.getLookAndFeel() == 1) {
				jfreechartRGB.setBackgroundPaint(Color.DARK_GRAY);
				jfreechartRGB.getTitle().setPaint(Color.LIGHT_GRAY);
				jfreechartRGB.getLegend().setBackgroundPaint(Color.DARK_GRAY);
				jfreechartRGB.getLegend().setItemPaint(Color.LIGHT_GRAY);
				jfreechartRGB.getLegend().setFrame(new BlockBorder(Color.DARK_GRAY));
			}
			;
			XYPlot xyplot = (XYPlot) jfreechartRGB.getPlot();
			if (Timelapse.getLookAndFeel() == 1)
				xyplot.setBackgroundPaint(Color.DARK_GRAY);
			xyplot.setForegroundAlpha(0.85F);

			XYBarRenderer xybarrenderer = (XYBarRenderer) xyplot.getRenderer();
			xybarrenderer.setDrawBarOutline(false);

			// create brightness histogram
			jfreechartBright = ChartFactory.createHistogram(" Brightness Histogram", null, null,
					createDatasetBrightness(), PlotOrientation.VERTICAL, true, true, false);
			if (Timelapse.getLookAndFeel() == 1) {
				jfreechartBright.setBackgroundPaint(Color.DARK_GRAY);
				jfreechartBright.getTitle().setPaint(Color.LIGHT_GRAY);
				jfreechartBright.getLegend().setBackgroundPaint(Color.DARK_GRAY);
				jfreechartBright.getLegend().setItemPaint(Color.LIGHT_GRAY);
				jfreechartBright.getLegend().setFrame(new BlockBorder(Color.DARK_GRAY));
			}
			;
			XYPlot xyplot2 = (XYPlot) jfreechartBright.getPlot();
			if (Timelapse.getLookAndFeel() == 1)
				xyplot2.setBackgroundPaint(Color.DARK_GRAY);
			xyplot2.setForegroundAlpha(0.85F);

			XYBarRenderer xybarrenderer2 = (XYBarRenderer) xyplot2.getRenderer();
			if (Timelapse.getLookAndFeel() == 1)
				xybarrenderer2.setPaint(Color.WHITE);
			xybarrenderer2.setDrawBarOutline(false);

		}

	}

	/**
	 * This method return the RGB histogram of the image.
	 */
	public JPanel getRGBHisto() {
		// return the jpanel with the rgb histogram
		JPanel jpanel;
		if (image == null) {
			jpanel = new JPanel();
			jpanel.setBackground(Color.DARK_GRAY);
			return jpanel;
		}
		jpanel = new ChartPanel(this.jfreechartRGB);
		jpanel.setVisible(true);

		return jpanel;
	}

	/**
	 * This method return the Brightness histogram of the image.
	 */
	public JPanel getBrightHisto() {
		// return the jpanel with the brightness histogram
		JPanel jpanel2;
		if (image == null) {
			jpanel2 = new JPanel();
			jpanel2.setBackground(Color.DARK_GRAY);
			return jpanel2;
		}
		jpanel2 = new ChartPanel(this.jfreechartBright);
		jpanel2.setVisible(true);

		return jpanel2;
	}

	/**
	 * This method create the dataset of the RGBchart and contains three
	 * XYSeries : redSeries , blueSeries , greenSeries.
	 */
	private static IntervalXYDataset createDataset() {

		final XYSeries redSeries = new XYSeries("red"); // series with all red
														// value
		final XYSeries blueSeries = new XYSeries("blue");// series with all blue
															// value
		final XYSeries greenSeries = new XYSeries("green");// series with all
															// green value

		for (int i = 0; i <= 255; i++) {
			// add the value of red , green ,blue
			redSeries.add(i, red[i]);
			blueSeries.add(i, blue[i]);
			greenSeries.add(i, green[i]);

		}
		// create the dataset of the rgb histogram
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(redSeries); // add red series on dataset
		dataset.addSeries(blueSeries); // add blue series on dataset
		dataset.addSeries(greenSeries); // add green series on dataset
		return dataset;
	}

	/**
	 * This method create the dataset of the Brightnesschart and contains one
	 * XYSeries : brightnessSeries.
	 */
	private static IntervalXYDataset createDatasetBrightness() {

		final XYSeries brightnessSeries = new XYSeries("brightness");

		for (int i = 0; i <= 255; i++) {
			// add the value oof bright
			brightnessSeries.add(i, brightness[i]);
		}
		// create the dataset and add brigthness series at dataset
		final XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(brightnessSeries);
		return dataset;

	}

}
