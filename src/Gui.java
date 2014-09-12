import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;

@SuppressWarnings("serial")
public class Gui extends JFrame {
	
	private JLabel readingFilesLabel = new JLabel("Reading Log Files...");
	private JLabel percentLabel = new JLabel("0%");
	
	public Gui() {
		setLayout(new GridBagLayout());

		setTitle("LOL Log Reader");
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		add(readingFilesLabel);
		add(getPercentLabel());
	}

	public JLabel getPercentLabel() {
		return percentLabel;
	}
	
}
