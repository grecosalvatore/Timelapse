import javax.swing.SwingUtilities;

public class TimelapseRun {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new Timelapse(); //start Timelapse

			}
		});
	}
}
