import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

public class Main {

	public static final File WINDOWS_DEFAULT_LOG_DIR = new File("C:\\Riot Games\\League of Legends\\Logs\\Game - R3d Logs");
	public static final File ALTERNATE_WINDOWS_LOG_DIR = new File("C:\\Program Files (x86)\\Riot Games\\League of Legends\\Logs\\Game - R3d Logs");
	public static final File MAC_LOG_DIR = new File("/Applications/Riot Games/League of Legends/Logs/Game - R3d Logs");//untested

	public static final File[] LOG_DIRS = {WINDOWS_DEFAULT_LOG_DIR, MAC_LOG_DIR, ALTERNATE_WINDOWS_LOG_DIR};

	public static File getUsersLogDirectory() {
		for (File logDir : LOG_DIRS) {
			if (logDir.exists()) {
				return logDir;
			}
		}

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.showOpenDialog(null);

		return fc.getSelectedFile();
	}

	public static void main(String[] args) {

		try {

			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}

			File usersLogDir = getUsersLogDirectory();

			if (usersLogDir == null || (!usersLogDir.getName().equals(WINDOWS_DEFAULT_LOG_DIR.getName())
					&& !usersLogDir.getName().equals(MAC_LOG_DIR.getName()))) {
				System.out.println("You selected '" + usersLogDir.getName() + "'");
				System.out.println("You must select '" + WINDOWS_DEFAULT_LOG_DIR.getName() + "' or "
						+ "'" + MAC_LOG_DIR.getName() + "'");
				JOptionPane.showMessageDialog(null,
						"You did not Select a \"Game - R3d Logs\" Folder",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
			}

			Gui gui = new Gui();
			gui.setSize(300, 75);
			gui.setLocationRelativeTo(null);
			gui.setVisible(true);

			long startTime = System.currentTimeMillis();

			File[] logFiles = usersLogDir.listFiles();

			Game[] games = new Game[logFiles.length];

			gui.getReadingFilesLabel().setText("Reading " + logFiles.length +  " Log Files...");

			JProgressBar progressBar = gui.getProgressBar();
			progressBar.setMaximum(logFiles.length);
			for (int i = 0; i < logFiles.length; i++) {
				games[i] = new Game(logFiles[i]);
				progressBar.setValue(i + 1);
			}

			double readTimeInSeconds = (System.currentTimeMillis() - startTime) / 1000d;
			System.out.println("finished reading " + logFiles.length + " log files in "
					+ readTimeInSeconds + "s (" + (logFiles.length / readTimeInSeconds) + " files/s)");

			long guiStartTime = System.currentTimeMillis();

			PlayerStatsGui playerStatsGui = new PlayerStatsGui(games);
			playerStatsGui.pack();
			playerStatsGui.setLocationRelativeTo(null);

			gui.setVisible(false);

			playerStatsGui.setVisible(true);

			gui.dispose();

			double guiDsiplayTimeInSeconds = (System.currentTimeMillis() - guiStartTime) / 1000d;

			System.out.println("displayed gui in " + guiDsiplayTimeInSeconds + "s");

		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			JOptionPane.showMessageDialog(null, sw.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

}
