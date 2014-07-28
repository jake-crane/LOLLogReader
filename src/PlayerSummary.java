import java.util.ArrayList;
import java.util.Comparator;

public class PlayerSummary {
	
	private String name;
	private ArrayList<ChampionSummary> championSummaries = new ArrayList<ChampionSummary>();
	private int blueTeamGames = 0;
	private int redTeamGames = 0;
	private int blueTeamWins = 0;
	private int redTeamWins = 0;

	public static Comparator<PlayerSummary> gamesPlayedComparator = new Comparator<PlayerSummary>() {

		public int compare(PlayerSummary p1, PlayerSummary p2) {

			if (p1.championSummaries.size() < p2.getChampionSummaries().size()) {
				return 1;
			} else if (p1.championSummaries.size() > p2.getChampionSummaries().size()) {
				return -1;
			}
			return 0;
		}

	};

	public PlayerSummary(String name) {
		this.name = name;
	}

	public PlayerSummary(Player player, ChampionSummary firstFoundChampionSummary) {
		this.name = player.getName();
		championSummaries.add(firstFoundChampionSummary);
		updateTeamInfo(player);
	}

	public String getName() {
		return name;
	}

	public ArrayList<ChampionSummary> getChampionSummaries() {
		return championSummaries;
	}
	
	/**
	 * Info is not updated if game outcome is not known.
	 * @param player
	 */
	public void updateTeamInfo(Player player) {
		if (player.getGameResult() == GameResult.UNKNOWN) {
			return;
		}
		if (player.getTeam() == Game.BLUE_TEAM) {
			incrementBlueGamsPlayed();
			if (player.getGameResult() == GameResult.WON) {
				incrementblueTeamWins();
			}
		} else if (player.getTeam() == Game.RED_TEAM) {
			incrementRedGamsPlayed();
			if (player.getGameResult() == GameResult.WON) {
				incrementredTeamWins();
			}
		}
	}
	
	public int getRedTeamGames() {
		return redTeamGames;
	}
	
	public void incrementRedGamsPlayed() {
		redTeamGames++;
	}
	
	public void incrementRedGamsPlayedBy(int amount) {
		redTeamGames += amount;
	}
	
	public int getBlueTeamGames() {
		return blueTeamGames;
	}
	
	public void incrementBlueGamsPlayed() {
		blueTeamGames++;
	}
	
	public void incrementBlueGamsPlayedBy(int amount) {
		blueTeamGames += amount;
	}
	
	public void incrementblueTeamWins() {
		blueTeamWins++;
	}
	
	public void incrementredTeamWins() {
		redTeamWins++;
	}
	
	public int getBlueTeamWins() {
		return blueTeamWins;
	}
	
	public int getRedTeamWins() {
		return redTeamWins;
	}

	@Override
	public String toString() {
		return name;
	}

}