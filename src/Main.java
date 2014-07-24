import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;


public class Main {

	public static final File WINDOWS_DEFAULT_LOG_DIR = new File("C:\\Riot Games\\League of Legends\\Logs\\Game - R3d Logs");
	public static final File ALTERNATE_WINDOWS_LOG_DIR = new File("C:\\Program Files (x86)\\Riot Games\\League of Legends\\Logs\\Game - R3d Logs");
	public static final File MAC_LOG_DIR = new File("/Applications/Riot Games/League of Legends/Logs/Game - R3d Logs");//untested

	public static final File[] logDirs = {WINDOWS_DEFAULT_LOG_DIR, MAC_LOG_DIR, ALTERNATE_WINDOWS_LOG_DIR};

	public static File getUsersLogDirectory() {
		for (File logDir : logDirs) {
			if (logDir.exists()) {
				return logDir;
			}
		}

		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
		fc.showOpenDialog(null);

		return fc.getSelectedFile();
	}

	static int getBlueWins(ArrayList<Game> games) {
		int count = 0;
		for (Game game : games) {
			if (game.getTeamThatWon() == Game.BLUE_TEAM) {
				count++;
			}
		}
		return count;
	}

	static int getRedWins(ArrayList<Game> games) {
		int count = 0;
		for (Game game : games) {
			if (game.getTeamThatWon() == Game.RED_TEAM) {
				count++;
			}
		}
		return count;
	}

	public static void main(String[] args) throws IOException {

		File usersLogDir = getUsersLogDirectory();

		if (usersLogDir == null || !usersLogDir.getName().equals(WINDOWS_DEFAULT_LOG_DIR.getName())) {
			System.err.println("unable to find Game - R3d Logs Directory");
			JOptionPane.showMessageDialog(null,
					"You did not Select a \"Game - R3d Logs\" Folder",
					"Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		Gui gui = new Gui();
		gui.getreadingFilesLabel().setText("Reading Log Files...");
		gui.pack();

		long startTime = System.currentTimeMillis();

		ArrayList<Game> games = new ArrayList<Game>();

		for (File file : usersLogDir.listFiles()) {
			games.add(new Game(file));
			//break;
		}

		ArrayList<PlayerSummary> playerSummaries = new ArrayList<PlayerSummary>();

		for (Game game : games) {
			for (Player player : game.getBlueTeam()) {
				summarizePlayers(game, player, playerSummaries);
			}
			for (Player player : game.getRedTeam()) {
				summarizePlayers(game, player, playerSummaries);
			}
		}

		Collections.sort(playerSummaries, PlayerSummary.gamesPlayedComparator);

		gui.setPlayerSummaries(playerSummaries.toArray(new PlayerSummary[0]));

		System.out.println("finished in " + (System.currentTimeMillis() - startTime));

		System.out.println("blue game wins: " + getBlueWins(games));
		System.out.println("red game wins: " + getRedWins(games));


	}

	public static void summarizePlayers(Game game, Player player, ArrayList<PlayerSummary> playerSummaries) {
		int playerIndex = playerSummaries.indexOf(new PlayerSummary(player.getName()));
		if (playerIndex == -1) { //user does not exist create new and add to list with champ
			ChampionSummary championSummary = new ChampionSummary(player.getChampionName(), game.getGameLength(), player.getTeam(), player.getGameResult());
			playerSummaries.add(new PlayerSummary(player.getName(), championSummary));
		} else { //user found get list of champions and add new champion info
			ArrayList<ChampionSummary> championsummaries = playerSummaries.get(playerIndex).getChampionSummaries();
			int championIndex = championsummaries.indexOf(new ChampionSummary(player.getChampionName()));
			if (championIndex == -1) { //champion does not exist for this user
				ChampionSummary championSummary = new ChampionSummary(player.getChampionName(), game.getGameLength(), player.getTeam(), player.getGameResult());
				championsummaries.add(championSummary);
			} else { //user has used this champion before
				ChampionSummary championSummary = championsummaries.get(championIndex);
				championSummary.incrementGamesPlayed();
				championSummary.incrementMinutesPlayedBy(game.getGameLength());
				if (player.getGameResult() == GameResult.WON) {
					championSummary.incrementWins();
				} else if (player.getGameResult() == GameResult.LOST) {
					championSummary.incrementLosses();
				}
			}
		}
	}

}
