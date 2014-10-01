import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

			ArrayList<Game> games = new ArrayList<Game>();

			File[] logFiles = usersLogDir.listFiles();

			JLabel guiLabel = gui.getPercentLabel();
			for (int i = 0; i < logFiles.length; i++) {
				//file = new File("C:\\Program Files (x86)\\Riot Games\\League of Legends\\Logs\\Game - R3d Logs\\2013-12-13T13-28-09_r3dlog.txt");

				Game game = new Game(logFiles[i]);
				games.add(game);
				float precent = (i + 1f) / logFiles.length * 100f;
				guiLabel.setText((i + 1) + "/ " + logFiles.length + " (" + (int)precent + "%)");
				//System.out.println(game);
				//System.out.println(file);
				//break;
			}

			HashMap<String, PlayerSummary> playerSummaries = new HashMap<String, PlayerSummary>();
			for (Game game : games) {
				if (!game.isBotGame()) {
					for (Player player : game.getBlueTeam()) {
						summarizePlayer(game, player, playerSummaries);
					}
					for (Player player : game.getRedTeam()) {
						summarizePlayer(game, player, playerSummaries);
					}
				}
			}

			PlayerSummary[] playerSummaryArray = playerSummaries.values().toArray(new PlayerSummary[0]);
			
			Arrays.sort(playerSummaryArray, PlayerSummary.GAMES_PLAYED_COMPARATOR);
			

			PlayerStatsGui playerStatsGui = new PlayerStatsGui();
			playerStatsGui.setPlayerSummaries(playerSummaryArray);
			playerStatsGui.pack();
			playerStatsGui.setLocationRelativeTo(null);

			gui.setVisible(false);

			playerStatsGui.setVisible(true);

			gui.dispose();

			System.out.println("finished reading log files in " + (System.currentTimeMillis() - startTime));

		} catch (Exception e) {
			e.printStackTrace();
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			JOptionPane.showMessageDialog(null, sw.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	public static void summarizePlayer(Game game, Player player, HashMap<String, PlayerSummary> playerSummaries) {
		PlayerSummary playerSummary = playerSummaries.get(player.getName());
		if (playerSummary == null) { //user does not exist create new and add to list with champ
			ChampionSummary championSummary = new ChampionSummary(player.getChampionName(), game, player.getTeam(), player.getGameResult());
			playerSummary = new PlayerSummary(player, championSummary, game.getEndTime());
			playerSummaries.put(playerSummary.getName(), playerSummary);
		} else { //user found get list of champions and add new champion info
			playerSummary.updateTeamInfo(player);
			HashMap<String, ChampionSummary> championsummaries = playerSummary.getChampionSummaries();
			ChampionSummary championSummary = championsummaries.get(player.getChampionName());
			if (championSummary == null) { //champion does not exist for this user
				championSummary = new ChampionSummary(player.getChampionName(), game, player.getTeam(), player.getGameResult());
				championsummaries.put(championSummary.getChampionName(), championSummary);
			} else { //user has used this champion before
				championSummary.updateTeamInfo(player);
				championSummary.incrementMinutesPlayedBy(game.getGameLength());
				championSummary.updateFirstLastSeen(game.getEndTime());
				championSummary.addGame(game);
			}
		}
	}

}
