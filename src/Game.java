import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Game {

	public static final int SUMMONERS_RIFT_ID = 1;

	public static final int BLUE_TEAM = 100, RED_TEAM = 200;

	private final ArrayList<Player> blueTeam = new ArrayList<Player>();
	private final ArrayList<Player> redTeam = new ArrayList<Player>();
	private Player localPlayer;
	private int teamThatWon = -1;
	private boolean botGame;
	private int netUID;
	private long startTime;
	private long endTime;
	//private int mapId = -1;

	private static final Pattern SPAWNING_PATTERN = Pattern.compile("Spawning champion \\((.+)\\) with skinID \\d+ on team (\\d+) for clientID (-*\\d+) and summonername \\((.+)\\) \\(is (.+)\\)");
	private static final Pattern NETUID_PATTERN = Pattern.compile("netUID: (\\d) defaultname");
	//private static final Pattern MAP_PATTERN = Pattern.compile("zip file: Map(\\d+).zip");

	public Game(File file) throws IOException {

		BasicFileAttributes attributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class); //could also use line1 to get start time
		this.startTime = attributes.creationTime().toMillis();
		this.endTime = file.lastModified();

		try (BufferedReader input = new BufferedReader(new FileReader(file))) {
			String line = input.readLine();// read time line
			while ((line = input.readLine()) != null) {
				Matcher netUIDMatcher = NETUID_PATTERN.matcher(line);
				Matcher spawningtestMatcher = SPAWNING_PATTERN.matcher(line);
				//Matcher mapMatcher = MAP_PATTERN.matcher(line);
				if (netUIDMatcher.find()) {
					netUID = Integer.parseInt(netUIDMatcher.group(1));
				} else if (spawningtestMatcher.find()) {
					String PlayerName = spawningtestMatcher.group(4);
					String championName = spawningtestMatcher.group(1);
					int clientId = Integer.parseInt(spawningtestMatcher.group(3));
					if (clientId == -1) {
						botGame = true;
					}
					int team = Integer.parseInt(spawningtestMatcher.group(2));
					Player player = new Player(PlayerName, championName, clientId, team);
					if (player.getClientId() == netUID) {
						localPlayer = player;
					}
					if (player.getTeam() == BLUE_TEAM) {
						if (!listContainsPlayerWithName(blueTeam, player.getName())) {
							blueTeam.add(player);
						}
					} else if (player.getTeam() == RED_TEAM) {
						if (!listContainsPlayerWithName(redTeam, player.getName())) {
							redTeam.add(player);
						}
					}
				//} else if (mapMatcher.find()) {
					//mapId = Integer.parseInt(mapMatcher.group(1));
				} else if (localPlayer != null && line.contains("exit_code")) {
					updateTeamsWinLoss(line);
					break;
				}
			}
		}
		if (getBlueTeam().size() + getRedTeam().size() == 1) {
			botGame = true;
		}
	}

	public boolean listContainsPlayerWithName(ArrayList<Player> playerList, String name) {
		for (Player player : playerList) {
			if (player.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	private void updateTeamsWinLoss(String line) {
		if (line.contains("EXITCODE_WIN")) {
			if (localPlayer.getTeam() == BLUE_TEAM) {
				teamThatWon = BLUE_TEAM;
				for (Player player : blueTeam) {
					player.setGameResult(GameResult.WON);
				}
				for (Player player : redTeam) {
					player.setGameResult(GameResult.LOST);
				}
			} else if (localPlayer.getTeam() == RED_TEAM) {
				teamThatWon = RED_TEAM;
				for (Player player : redTeam) {
					player.setGameResult(GameResult.WON);
				}
				for (Player player : blueTeam) {
					player.setGameResult(GameResult.LOST);
				}
			}
		} else if (line.contains("EXITCODE_LOSE")) {
			if (localPlayer.getTeam() == BLUE_TEAM) {
				teamThatWon = RED_TEAM;
				for (Player player : redTeam) {
					player.setGameResult(GameResult.WON);
				}
				for (Player player : blueTeam) {
					player.setGameResult(GameResult.LOST);
				}
			} else if (localPlayer.getTeam() == RED_TEAM) {
				teamThatWon = BLUE_TEAM;
				for (Player player : redTeam) {
					player.setGameResult(GameResult.LOST);
				}
				for (Player player : blueTeam) {
					player.setGameResult(GameResult.WON);
				}
			}
		}
	}

	public int getTeamThatWon() {
		return teamThatWon;
	}

	public void setTeamThatWon(int teamThatWon) {
		this.teamThatWon = teamThatWon;
	}

	public boolean isBotGame() {
		return botGame;
	}

	public void setBotGame(boolean botGame) {
		this.botGame = botGame;
	}

	public int getNetUID() {
		return netUID;
	}

	public void setNetUID(int netUID) {
		this.netUID = netUID;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	/*public int getMapId() {
		return mapId;
	}*/

	public ArrayList<Player> getBlueTeam() {
		return blueTeam;
	}

	public ArrayList<Player> getRedTeam() {
		return redTeam;
	}

	public Player getLocalPlayer() {
		return localPlayer;
	}

	public long getGameLength() {
		return TimeUnit.MILLISECONDS.toMinutes(endTime - startTime);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Blue Team:" + System.lineSeparator());
		for (Player player : blueTeam) {
			sb.append("    " + player.toString() + System.lineSeparator());
		}
		sb.append("Red Team:" + System.lineSeparator());
		for (Player player : redTeam) {
			sb.append("    " + player.toString() + System.lineSeparator());
		}
		sb.append("Local Player:" + System.lineSeparator());
		sb.append("    " + localPlayer + System.lineSeparator());
		sb.append("Winner: " + teamThatWon + System.lineSeparator());
		sb.append("botGame: " + botGame + System.lineSeparator());
		//sb.append("mapId: " + mapId + System.lineSeparator());
		sb.append("Game Length: " + getGameLength() + " Minutes" + System.lineSeparator());
		return sb.toString();
	}

}