import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYImageAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.block.BlockFrame;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.XYAnnotationEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.Series;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * this class create the panel with the chart of the deflickering graph and
 * manage the add of the keyframes JFreeChart jfreechart jfreechart of
 * deflickering double[] avgBright vector with bright value of each images int
 * line number of selected image where create a green line
 * 
 * 
 * 
 * 
 */
public class MyGrahpPanel {

	private static JFreeChart jfreechart; // jfreechart of deflickering
	private JPanel jpanel; // panel with the graph
	private static double[] avgBright; // vector with bright value of each
										// images
	private static int line; // number of selected image where create a green
								// line
	private static XYSeries selectedImage; // series with the constant line of
											// the select image onn the table
	private static XYPlot xyplot; // xy plot of the chart
	private static XYDataset dataset; // dataset of the series
	private static XYSeriesCollection datasetSeries; // seriescollection of the
														// dataset
	private static XYSeries keyFramesSeries; // series with the keyframes
	private static ImageIcon imageIcon; // variable with yellow point of
										// keyframes
	private static JFreeChart chart; // chart with the deflickering graph
	private static XYSeries selectedKeyframes; // series with only the first and
												// the last point
	private static MyMouseListener lis; // mouse listener of the graph for add
										// keyframes
	private static ChartPanel cp; // chart panel with the graph
	private static XYPlot plot; // plot of the chart
	private static XYSeries realSeries;
	private static XYSeries RedEncodingSeries;

	/**
	 * contructor of MyGraphpanel
	 */
	public MyGrahpPanel() {
		// load the vector with all brithness of the images
		avgBright = Timelapse.getBrightList();
		// load the icon of yellow point for the keyframes
		imageIcon = new ImageIcon("img//yellow_point.png");

		line = 0;
		// create the dataset of the chart
		dataset = createDataset();
		// create the jfreechart with the dataset
		jfreechart = createChart(dataset);
		// change the color for the two theme
		if (Timelapse.getLookAndFeel() == 1) {
			jfreechart.getTitle().setPaint(Color.LIGHT_GRAY);
			jfreechart.setBackgroundPaint(Color.DARK_GRAY);
			LegendTitle legend = jfreechart.getLegend();
			legend.setWidth(100);
			if (Timelapse.getLookAndFeel() == 1) {
				legend.setBackgroundPaint(Color.DARK_GRAY);
				legend.setItemPaint(Color.LIGHT_GRAY);
				legend.setFrame(new BlockBorder(Color.DARK_GRAY));
			}
		}
		// create the legend
		LegendItemCollection chartLegend = new LegendItemCollection();
		jfreechart.getXYPlot().getRenderer().setSeriesPaint(1, Color.GREEN);
		jfreechart.getXYPlot().getRenderer().setSeriesPaint(2, Color.YELLOW);
		// create the mouse listener in the chart for add keyframes
		lis = new MyMouseListener();
		// create the chart panel with the jfreechart
		cp = new ChartPanel(jfreechart);
		plot = jfreechart.getXYPlot();
		// add the mouse listener to the chart panel
		cp.addChartMouseListener(lis);
		xyplot = (XYPlot) jfreechart.getPlot();
		if (Timelapse.getLookAndFeel() == 1)
			xyplot.setBackgroundPaint(Color.DARK_GRAY);
		xyplot.setForegroundAlpha(0.85F);
		xyplot.getLegendItems().add(new LegendItem(""));
		// create the jpanel and add the chart panel
		jpanel = new JPanel(new GridLayout());
		jpanel.add(cp);

	}

	/**
	 * this methdo create the XYDataset of the deflickering graph
	 * 
	 * @return
	 */
	private XYDataset createDataset() {

		
		realSeries = new XYSeries("Real Curve");// series with
												// the real
												// brightness of
												// each image

		selectedImage = new XYSeries("image selected"); // series that show the
														// selected image in the
														// table on the graph
		keyFramesSeries = new XYSeries("KeyFrame"); // series with all keyframes
													// point
		selectedKeyframes = new XYSeries(""); // series with
																// the
		
		RedEncodingSeries = new XYSeries(" ");
		// first and the
		// last point

		// add all average brightness value in the graph
		// for (int i = 0; i < avgBright.length; i++) {
		// realSeries.add(i, avgBright[i]);
		// }
		// create the xyseriescollection and add all series
		datasetSeries = new XYSeriesCollection();
		//datasetSeries.setIntervalWidth(1000);
		datasetSeries.addSeries(realSeries);
		datasetSeries.addSeries(selectedImage);
		datasetSeries.addSeries(keyFramesSeries);
		datasetSeries.addSeries(selectedKeyframes);
		datasetSeries.addSeries(RedEncodingSeries);

		return datasetSeries;

	}

	/**
	 * this class implements ChartMouseListener and MouseListener of all click
	 * in the chart
	 *
	 */
	public class MyMouseListener implements ChartMouseListener/* , MouseListener */ {
		// mouse listener on the chart to add keyframes with a double click
		@Override
		public void chartMouseClicked(ChartMouseEvent arg0) {
			int clickedX = 0; // x on the graph of the clicked point
			int clickedY = 0; // y on the graph of the clicked point

			plot.setDomainCrosshairVisible(false);
			plot.setDomainCrosshairLockedOnData(false);
			plot.setRangeCrosshairVisible(false);
			plot.setRangeCrosshairLockedOnData(false);
			

			ChartEntity entity = arg0.getEntity();
			clickedX = calcApproximatingX(plot.getDomainCrosshairValue());

			System.out.println("clicked x " + clickedX);
			avgBright = Timelapse.getBrightList();

			// can add keyframes only if the button straightlinebutton was
			// pressed
			if (Timelapse.getFlagCalc()  && (Timelapse.getStartEncoding() == false)) {

				// add keyframes only with a double click
				if (arg0.getTrigger().getClickCount() == 2) {
					// control if the click is in the interval and in the chart
					if ((((plot.getDomainCrosshairValue() != 0) || (plot.getRangeCrosshairValue()) != 0))
							&& ((clickedX >= 0) && (clickedX < avgBright.length))) {
						clickedY = (int) avgBright[clickedX];
						System.out.println(plot.getRangeCrosshairValue());
						// range in height of the click near the real line
						if ((plot.getRangeCrosshairValue() >= (clickedY - 20))
								&& (plot.getRangeCrosshairValue() <= (clickedY + 20))) {
							// if there isn't keyframes add the first and the
							// last image as keyframes
							if (keyFramesSeries.getItemCount() == 0) {
								Timelapse.setFlagKeyframes(true);
								
								setKeyFramePoint(0); // add keyframes in the
														// series
								Timelapse.getKeyFrames().add(0); // add
																	// keyframes
																	// in the
																	// list
								String s = (String) Timelapse.getTable().getModel().getValueAt(0,
										Timelapse.getTable().getColumnFromName("Image Name"));
								String[] app;
								app = new String[3];
								app[0] = "1";

								app[1] = s;
								app[2] = " ";

								Timelapse.getSettingPanel().addTableKeyFrames(app); // add
																					// keyframes
																					// in
																					// the
																					// scrolltable
								setKeyFramePoint(avgBright.length - Timelapse.getNameError().size() - 1);
								Timelapse.getKeyFrames().add(avgBright.length - Timelapse.getNameError().size()- 1);

								s = (String) Timelapse.getTable().getModel().getValueAt(avgBright.length - 1,
										Timelapse.getTable().getColumnFromName("Image Name"));
								app[0] = "2";
								app[1] = s;
								app[2] = " ";
								Timelapse.getSettingPanel().addTableKeyFrames(app);
								Timelapse.getMenu().setEncodingItem(true);
							}
							setKeyFramePoint(clickedX); // add the clicked
														// keyframes in the
														// series
							Timelapse.getKeyFrames().add(clickedX); // add the
																	// clicked
																	// keyframes
																	// in the
																	// list
							String s = (String) Timelapse.getTable().getModel().getValueAt(clickedX,
									Timelapse.getTable().getColumnFromName("Image Name"));
							String[] app;
							app = new String[3];
							app[0] = Integer.toString(keyFramesSeries.getItemCount());
							app[1] = s;
							app[2] = "";
							Timelapse.getSettingPanel().addTableKeyFrames(app); // add
																				// the
																				// clicked
																				// keyframes
																				// in
																				// the
																				// scrolltable
						}
					}
				}

			}
		}

		@Override
		public void chartMouseMoved(ChartMouseEvent arg0) {

		}

		/*
		 * @Override public void mouseEntered(MouseEvent e) {
		 * 
		 * }
		 * 
		 * @Override public void mouseExited(MouseEvent e) {
		 * 
		 * }
		 * 
		 * @Override public void mousePressed(MouseEvent e) {
		 * 
		 * }
		 * 
		 * @Override public void mouseReleased(MouseEvent e) {
		 * 
		 * }
		 * 
		 * @Override public void mouseClicked(MouseEvent arg0) { // TODO
		 * Auto-generated method stub System.out.println("aaaabbbb"); }
		 */

	}

	/**
	 * this method create the JFreeChart with the deflickering graph
	 * 
	 * @param dataset
	 *            dataset of the jfreechart
	 * @return JFreeChart deflickering graph
	 */
	private JFreeChart createChart(final XYDataset dataset) {
		// create the chart...
		chart = ChartFactory.createXYLineChart(" ", // chart
				// title
				"N� Photo", // x axis label
				"Brightness", // y axis label
				dataset, // data
				PlotOrientation.VERTICAL, true, // include legend
				false, // tooltips
				false // urls
		);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...
		if (Timelapse.getLookAndFeel() == 1){
			chart.setBackgroundPaint(Color.DARK_GRAY);
		    
		}
		else {
			Color c = null;
			c.decode("#eeece4");
			chart.setBackgroundPaint(c);

		}
		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);
		
		chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(3, Boolean.FALSE);
		chart.getXYPlot().getRenderer().setSeriesVisibleInLegend(4, Boolean.FALSE);
		return chart;
	}

	/**
	 * this method approximates the clicked x in the chart to the nearest
	 * integer
	 * 
	 * @param realX
	 *            coordinate x of the click
	 * @return int approximatedX
	 */
	public static int calcApproximatingX(double realX) {
		// calc the x more near at the click
		int approximatedX = 0;
		approximatedX = (int) Math.round(realX);
		return approximatedX;
	}

	/**
	 * this method delete the DefaultSeries from the graph
	 */
	public static void removeDeafultSeries() {
		// delete the default series
		selectedKeyframes.clear();
	}
	
	public static void removeRealSeries() {
		// delete the real curve
		realSeries.clear();
	}

	/**
	 * this method delete all keyframes of the keyFramesSeries and delete all
	 * yelllow points in the graph
	 */
	public static void clearKeyFrames() {
		// delete all keyframes
		jfreechart.getXYPlot().clearAnnotations();
		keyFramesSeries.clear();
	}
	
	public static void clearSelectedImage() {
		selectedImage.clear();
	}

	// setter
	/**
	 * this method create the green line for the selected image in the table
	 * 
	 * @param int
	 *            i number of selected image
	 */
	public static void setLine(int i) {
		// draw a green line of the selected image in the table
		selectedImage.clear();
		selectedImage.add(i, 0);
		selectedImage.add(i, Timelapse.getBrightList()[i]);
		line = i;

	}

	/**
	 * this method add a keyframe in the keyFramesSeries
	 * 
	 * @param i
	 *            number of the image and x in the graph
	 */
	public static void setKeyFramePoint(int i) {
		// add keyframes point in the series keyframes
		System.out.println(" set " + i);
		keyFramesSeries.add(i, Timelapse.getBrightList()[i]);
		XYAnnotation xyannotation = null;
		xyannotation = new XYImageAnnotation(i, Timelapse.getBrightList()[i], imageIcon.getImage());

		jfreechart.getXYPlot().addAnnotation(xyannotation);

	}

	public static void deleteKyframesPoint(int i) {
		// delete single keyframe
		System.out.println(" remove " + i);

		int x = keyFramesSeries.getDataItem(i).getX().intValue();
		System.out.print(" x : " + x);
		XYAnnotation xyannotation = new XYImageAnnotation(x, Timelapse.getBrightList()[x], imageIcon.getImage());
		jfreechart.getXYPlot().removeAnnotation(xyannotation);
		keyFramesSeries.remove(keyFramesSeries.getDataItem(i).getX());
		Timelapse.getKeyFrames().remove(i);

	}

	/**
	 * this method create the default series of the graph with as keyframes only
	 * the first and the last image
	 */
	public static void setLineKeyframes(int i) {
		// draw blue line when keyframes was selected in table keyframes
		int x = keyFramesSeries.getDataItem(i).getX().intValue();
		selectedKeyframes.clear();
		selectedKeyframes.add(x, 0);
		selectedKeyframes.add(x, Timelapse.getBrightList()[x]);
		jfreechart.getXYPlot().getRenderer().setSeriesPaint(3, Color.BLUE);
	}

	public static void addRealCurve(int n, double avg) {
		realSeries.add(n, avg);
	}

	// getter
	/**
	 * this method return the series of the keyframes
	 * 
	 * @return XYSeries keyFramesSeries
	 */
	public static XYSeries getKeyFramesSeries() {
		return keyFramesSeries;
	}

	/**
	 * this method return the JFreechart of the graph
	 * 
	 * @return JFreeChart
	 */
	public static JFreeChart getChart() {
		return chart;
	}

	/**
	 * this method return the chart panel with the graph
	 * 
	 * @return ChartPanel
	 */
	public static ChartPanel getChartPanel() {
		return cp;
	}

	/**
	 * this method return the panel with the graph
	 * 
	 * @return JPanel
	 */
	public JPanel getPannello() {
		return this.jpanel;
	}

	public static void setLineVideo(int i) {
		// draw a green line of the selected image in the table
		RedEncodingSeries.remove(i);	
		plot.getRenderer().setSeriesPaint(1,new Color(6, 176,145,30));
		selectedImage.add(i, 0);
		selectedImage.add(i, Timelapse.getBrightList()[i]);
		plot.getRenderer().setSeriesPaint(3,new Color(167, 200,80,30));
		selectedKeyframes.add(i, Timelapse.getBrightList()[i]);
		selectedKeyframes.add(i, 260);
		line = i;

	}

	public static void setXNumberOfAxis(int numberOfPhoto) {
		// Create an NumberAxis
		NumberAxis xAxis = new NumberAxis();
		xAxis.setAutoRange(false);
		xAxis.setRange(- 10 , numberOfPhoto + 10);
		xyplot.setDomainAxis(xAxis);
		
		// Create an NumberAxis
		NumberAxis yAxis = new NumberAxis();
		yAxis.setAutoRange(false);
		yAxis.setRange(0 , 260);
		xyplot.setRangeAxis(yAxis);
		
		chart.getXYPlot().getDomainAxis().setTickLabelPaint(Color.lightGray);
		chart.getXYPlot().getRangeAxis().setTickLabelPaint(Color.lightGray);
		
	}

	public static void startEncodingVideo(){
		plot.getRenderer().setSeriesPaint(4,new Color(255,0,0,20));
		for (int i=0 ; i < Timelapse.getBrightList().length ; i++){
			RedEncodingSeries.add(i,0);
			RedEncodingSeries.add(i,260);
		}
	}
}
