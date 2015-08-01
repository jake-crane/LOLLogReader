import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class Gui extends JFrame {

	private JLabel readingFilesLabel = new JLabel("Reading Log Files...", JLabel.CENTER);
	private JProgressBar progressBar = new JProgressBar();

	public Gui() {
		setLayout(new GridLayout(2, 1));

		setTitle("LOL Log Reader");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		add(readingFilesLabel);

		progressBar.setStringPainted(true);
		add(progressBar);
	}

	public JLabel getReadingFilesLabel() {
		return readingFilesLabel;
	}

	public JProgressBar getProgressBar() {
		return progressBar;
	}

}
